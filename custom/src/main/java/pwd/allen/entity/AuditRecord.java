package pwd.allen.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 审批记录
 * </p>
 *
 * @author pwdan
 * @since 2020-10-10
 */
@Data
public class AuditRecord implements Serializable {

    public static final Integer TYPE_FIRST = 1;

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    private String id;

    /**
     * 父ID
     */
    private String parentId;

    /**
     * 流程定义key
     */
    private String flowDefineKey;

    /**
     * 审批单号
     */
    private String auditCode;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 结束时间
     */
    private Date endDate;

    /**
     * 流程节点
     */
    private String flowNode;

    /**
     * 审核人ID
     */
    private String auditorId;

    /**
     * 审核人
     */
    private String auditor;

    /**
     * 创建人ID
     */
    private String creatorId;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建人机构ID
     */
    private String creatorOrgId;

    /**
     * 审批信息
     */
    private String auditMessage;

    /**
     * 拓展变量
     */
    private String jsonMessage;

    /**
     * 候选组
     */
    private String candidateGroup;

    /**
     * 候选人
     */
    private String candidate;

    /**
     * 类型 1：第一个节点
     */
    private Integer type;

}
