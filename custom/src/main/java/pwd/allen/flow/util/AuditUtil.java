package pwd.allen.flow.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import pwd.allen.entity.AuditParam;
import pwd.allen.entity.AuditRecord;
import pwd.allen.flow.exception.AuditException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pwdan
 * @create 2020-10-09 11:10
 **/
@Service
public class AuditUtil {

    @Value("classpath*:flow/*.json")
    private Resource[] resources;

    private Map<String, FlowDefine> flowDefineMap = null;

    public Map<String, FlowDefine> getFlowDefineMap() {
        if (flowDefineMap != null) {
            return flowDefineMap;
        }
        // 加载流程定义
        flowDefineMap = new HashMap<>();
        try {
            if (resources != null && resources.length > 0) {
                for (Resource resource : resources) {
                    FlowDefine flowDefine = JSONUtil.toBean(IoUtil.read(resource.getInputStream(), Charset.forName("utf-8")), FlowDefine.class);
                    flowDefineMap.put(flowDefine.getKey(), flowDefine);
                }
            } else {
                // 加载本地目录下的
                List<String> fileNames = FileUtil.listFileNames("flow");
                for (String fileName : fileNames) {
                    FlowDefine flowDefine = JSONUtil.toBean(ResourceUtil.readUtf8Str("flow/" + fileName), FlowDefine.class);
                    flowDefineMap.put(flowDefine.getKey(), flowDefine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flowDefineMap;

    }

    /**
     * 审批，不更新数据库，只封装并返回下一步的流程记录
     * @param param
     * @return
     */
    public AuditRecord findNext(AuditParam param) {
        AuditRecord curAuditRecord = param.getCurAuditRecord();
        AuditRecord nextAuditRecord = new AuditRecord();

        FlowDefine flowDefine = getFlowDefineMap().get(param.getFlowDefineKey());
        if (flowDefine == null) {
            throw new AuditException(String.format("流程定义%s 不存在！", param.getFlowDefineKey()));
        }

        // 如果没有当前节点，则默认开始节点
        if (curAuditRecord == null) {
            curAuditRecord = new AuditRecord();
            curAuditRecord.setFlowNode(FlowDefine.NODE_START);
        }

        FlowDefine.Sequence curSequence = null;
        for (FlowDefine.Sequence sequence : flowDefine.getSequences()) {
            if (curAuditRecord.getFlowNode().equals(sequence.getSourceNode())) {
                curSequence = sequence;
                break;
            }
        }

        if (curSequence == null) {
            throw new AuditException(String.format("找不到下个流程节点，当前节点：%s", curAuditRecord.getFlowNode()));
        }

        nextAuditRecord.setAuditCode(param.getAuditCode());
        nextAuditRecord.setFlowDefineKey(flowDefine.getKey());
        nextAuditRecord.setFlowNode(curSequence.getTargetNode());
        nextAuditRecord.setCreateDate(new Date());
        nextAuditRecord.setCreator(param.getUser().getUserName());
        nextAuditRecord.setCreatorId(param.getUser().getId());
        nextAuditRecord.setCreatorOrgId(param.getUser().getOrgId());
        if (StringUtils.isNotEmpty(curAuditRecord.getId())) {
            nextAuditRecord.setParentId(curAuditRecord.getId());
        }
        param.setNewAuditRecord(nextAuditRecord);

        // 如果有执行脚本，则执行之
        if (StringUtils.isNotEmpty(curSequence.getExpression())) {
            Binding binding = new Binding();
            GroovyShell shell = new GroovyShell(binding);
            binding.setVariable("param", param);
            shell.evaluate(curSequence.getExpression());
        }
        curAuditRecord.setJsonMessage(JSONUtil.toJsonStr(param.getResultMap()));

        return nextAuditRecord;
    }

    /**
     * 校验流程节点
     * @param curRecord
     * @param param
     */
    public static void validate(AuditRecord curRecord, AuditParam param) throws AuditException {
        if (curRecord == null) {
            throw new AuditException("找不到流程节点！");
        }
        if (curRecord.getEndDate() != null) {
            throw new AuditException("流程已结束！");
        }
        if (StringUtils.isNotEmpty(param.getCurAuditRecordId()) && !curRecord.getId().equals(param.getCurAuditRecordId())) {
            throw new AuditException(String.format("当前流程节点不一致！当前流程节点：%s", curRecord.getFlowNode()));
        }
        if (StringUtils.isNotEmpty(curRecord.getCandidate()) && !curRecord.getCandidate().equals(param.getUser().getId())) {
            throw new AuditException(String.format("无权限执行，当前节点候选人：%s", curRecord.getCandidate()));
        }
        if (StringUtils.isNotEmpty(curRecord.getCandidateGroup()) && !curRecord.getCandidateGroup().equals(param.getUser().getRoleCode())) {
            throw new AuditException(String.format("无权限执行，当前节点候选组：%s", curRecord.getCandidateGroup()));
        }
    }

}
