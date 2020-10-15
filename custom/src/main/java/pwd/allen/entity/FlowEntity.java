package pwd.allen.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author pwdan
 * @create 2020-10-09 9:38
 **/
@Data
public class FlowEntity implements Serializable {

    public static final Integer FLOW_STATUS_AUDITING = 1;
    public static final Integer FLOW_STATUS_FINISH = 2;
    public static final Integer FLOW_STATUS_BACK = 4;
    public static final Integer AUDIT_STATUS_AUDITING = 1;
    public static final Integer AUDIT_STATUS_PASS = 2;

    /**
     * 审批单号
     */
    protected String auditCode;

    /**
     * 当前流程记录
     */
    protected String curAuditRecordId;

    /**
     * 流程状态 工作流状态，0--启动，1--审批中，2--审批完成，4--退回（审批不通过），9-作废
     */
    protected Integer flowStatus;

    /**
     * 审批状态 1：审批中，2：已通过
     */
    protected Integer auditStatus;

}
