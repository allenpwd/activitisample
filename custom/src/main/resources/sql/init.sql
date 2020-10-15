drop index idx_audit_record_piid on BASE_AUDIT_RECORD;

drop table if exists BASE_AUDIT_RECORD;

/*==============================================================*/
/* Table: BASE_AUDIT_RECORD                                     */
/*==============================================================*/
create table BASE_AUDIT_RECORD
(
   ID                   varchar(64) not null comment 'ID',
   PARENT_ID            varchar(64) comment '父ID',
   FLOW_DEFINE_KEY      varchar(50) comment '流程定义key',
   AUDIT_CODE           varchar(64) comment '审批单号',
   CREATE_DATE          datetime comment '创建时间',
   END_DATE             datetime comment '结束时间',
   FLOW_NODE            varchar(50) comment '流程节点',
   AUDITOR_ID           varchar(64) comment '审核人ID',
   AUDITOR              varchar(20) comment '审核人',
   CREATOR_ID           varchar(64) comment '创建人ID',
   CREATOR              varchar(20) comment '创建人',
   CREATOR_ORG_ID       varchar(64) comment '创建人机构ID',
   AUDIT_MESSAGE        varchar(100) comment '审批信息',
   JSON_MESSAGE         varchar(1000) comment '拓展变量',
   CANDIDATE_GROUP      varchar(50) comment '候选组',
   CANDIDATE            varchar(150) comment '候选人',
   primary key (ID)
);

alter table BASE_AUDIT_RECORD comment '审批记录';

/*==============================================================*/
/* Index: idx_audit_record_piid                                 */
/*==============================================================*/
create index idx_audit_record_piid on BASE_AUDIT_RECORD
(
   AUDIT_CODE
);
