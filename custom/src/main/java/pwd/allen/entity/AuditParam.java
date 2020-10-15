package pwd.allen.entity;

import cn.hutool.core.lang.Dict;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author pwdan
 * @create 2020-10-09 10:56
 **/
public class AuditParam extends FlowEntity {

    public static final int AUDIT_TYPE_PASS = 1; // 通过
    public static final int AUDIT_TYPE_NOPASS = -1;   // 不通过

    /**
     * 审批类型，1：通过  -1：不通过
     */
    private Integer auditType;

    /**
     * 审批意见
     */
    private String auditMessage;

    private String flowDefineKey;

    @JsonIgnore
    private SYSUsers user;

    /**
     * 当前流程节点
     */
    private AuditRecord curAuditRecord;

    /**
     * 新流程节点
     */
    private AuditRecord newAuditRecord;

    private Dict resultMap = Dict.create();

    public Integer getAuditType() {
        return auditType;
    }

    public void setAuditType(Integer auditType) {
        this.auditType = auditType;
    }

    public String getAuditMessage() {
        return auditMessage;
    }

    public void setAuditMessage(String auditMessage) {
        this.auditMessage = auditMessage;
    }

    public String getFlowDefineKey() {
        return flowDefineKey;
    }

    public void setFlowDefineKey(String flowDefineKey) {
        this.flowDefineKey = flowDefineKey;
    }

    public SYSUsers getUser() {
        return user;
    }

    public void setUser(SYSUsers user) {
        this.user = user;
    }

    public AuditRecord getCurAuditRecord() {
        return curAuditRecord;
    }

    public void setCurAuditRecord(AuditRecord curAuditRecord) {
        this.curAuditRecord = curAuditRecord;
    }

    public AuditRecord getNewAuditRecord() {
        return newAuditRecord;
    }

    public void setNewAuditRecord(AuditRecord newAuditRecord) {
        this.newAuditRecord = newAuditRecord;
    }

    public Dict getResultMap() {
        return resultMap;
    }
}
