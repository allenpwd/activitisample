package pwd.allen.flow.service;


import cn.hutool.core.util.IdUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pwd.allen.entity.AuditParam;
import pwd.allen.entity.AuditRecord;
import pwd.allen.entity.FlowEntity;
import pwd.allen.entity.SYSUsers;
import pwd.allen.flow.exception.AuditException;
import pwd.allen.flow.util.AuditUtil;
import pwd.allen.flow.util.FlowDefine;

import java.util.Date;

/**
 * <p>
 * 审批记录 服务实现类
 * </p>
 *
 * @author pwdan
 * @since 2020-10-09
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class AuditRecordServiceImpl {

    @Autowired
    private AuditUtil auditUtil;

    /**
     * 流程审批
     * @param param
     * @return
     */
    @Transactional
    public AuditParam audit(AuditParam param) {

        SYSUsers user = param.getUser();
        if (user == null) {
            throw new AuditException("缺少用户信息：%s");
        }

        String redisKey = "AUDITRECORD:" + user.getId();

        // TODO 通过redis实现防频繁操作

        try {
            // 当前审批记录
            AuditRecord curRecord = getCurAuditRecord(param, true);

            if (AuditRecord.TYPE_FIRST.equals(curRecord.getType())) {
                // 流程刚开始时需要走完第一个步骤
                String auditCode = IdUtil.simpleUUID();
                curRecord = new AuditRecord();
                param.setAuditCode(auditCode);
                curRecord.setAuditCode(param.getAuditCode());
                param.setAuditCode(auditCode);
                curRecord.setFlowNode(FlowDefine.NODE_START);
                param.setCurAuditRecord(curRecord);
                AuditRecord firstRecord = auditUtil.findNext(param);
                firstRecord.setId(IdUtil.simpleUUID());
                // TODO 插入数据到数据库
                curRecord = firstRecord;
            }
            param.setCurAuditRecord(curRecord);

            AuditRecord newRecord = null;
            if (param.getAuditType() == AuditParam.AUDIT_TYPE_PASS) {
                // 通过
                newRecord = auditUtil.findNext(param);
            } else if (param.getAuditType() == AuditParam.AUDIT_TYPE_NOPASS) {
                // 不通过，退回上个节点
                if (curRecord.getParentId() == null) {
                    throw new AuditException(String.format("上个节点不存在！当前节点：%s",curRecord.getFlowNode()));
                }
                newRecord = null;
                // TODO 根据parentId从数据库中获取上个节点
                if (newRecord == null) {
                    throw new AuditException(String.format("上个节点不存在！当前节点：%s",curRecord.getFlowNode()));
                }
                newRecord.setId(null);
                newRecord.setAuditMessage(null);
                newRecord.setAuditor(null);
                newRecord.setAuditorId(null);
                newRecord.setEndDate(null);
                newRecord.setCandidate(newRecord.getCreatorId());
                newRecord.setCreateDate(new Date());
                param.setNewAuditRecord(newRecord);
            } else {
                throw new AuditException("审批无效！");
            }

            newRecord.setId(IdUtil.simpleUUID());
            param.setCurAuditRecordId(newRecord.getId());

            //<editor-fold desc="更新流程状态">
            if (newRecord.getFlowNode().equals(FlowDefine.NODE_END)) {
                param.setFlowStatus(FlowEntity.FLOW_STATUS_FINISH);
                param.setAuditStatus(FlowEntity.AUDIT_STATUS_PASS);
            } else {
                param.setAuditStatus(FlowEntity.AUDIT_STATUS_AUDITING);
                if (param.getAuditType() == AuditParam.AUDIT_TYPE_NOPASS) {
                    param.setFlowStatus(FlowEntity.FLOW_STATUS_BACK);
                } else {
                    param.setFlowStatus(FlowEntity.FLOW_STATUS_AUDITING);
                }
            }
            // 更新示例的流程状态
//            dao.updateFlowStatus(param);
            //</editor-fold>

            //<editor-fold desc="更新之前的审批记录">
            if (curRecord != null) {
                curRecord.setEndDate(newRecord.getCreateDate());
                curRecord.setAuditor(newRecord.getCreator());
                curRecord.setAuditorId(newRecord.getCreatorId());
                curRecord.setAuditMessage(param.getAuditMessage());
                // TODO 更新数据到数据库
                }
            //</editor-fold>

            // 插入审批记录
            if (!FlowDefine.NODE_END.equals(newRecord.getFlowNode())) {
                // TODO 更新数据到数据库
            }
        } finally {
            // TODO 清除防刷的redis缓存
        }

        return param;
    }

    /**
     * 获取当前流程节点
     * @param param
     * @param ifValidate 传true，则校验流程节点
     * @return
     */
    public AuditRecord getCurAuditRecord(AuditParam param, boolean ifValidate) throws AuditException {
        // 当前审批记录
        AuditRecord curRecord = null;

        if (param == null || param.getAuditType() == null || param.getUser() == null || StringUtils.isEmpty(param.getFlowDefineKey())) {
            throw new AuditException("缺少参数！");
        }

        if (StringUtils.isNotEmpty(param.getAuditCode())) {
            // 获取同一批审批记录的最后一个
            curRecord = null;
            // TODO 根据auditCode查询最后一个审批记录
        } else {
            AuditRecord startRecord = new AuditRecord();
            startRecord.setFlowNode(FlowDefine.NODE_START);
            param.setCurAuditRecord(startRecord);
            curRecord = auditUtil.findNext(param);
            curRecord.setType(AuditRecord.TYPE_FIRST);
        }
        if (!ifValidate) {
            return curRecord;
        }

        AuditUtil.validate(curRecord, param);
        return curRecord;
    }

}
