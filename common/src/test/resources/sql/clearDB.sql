-- 关闭外键约束检查
SET FOREIGN_KEY_CHECKS=0;
TRUNCATE TABLE `act_hi_attachment`;
TRUNCATE TABLE `act_hi_comment`;
TRUNCATE TABLE `act_hi_detail`;
TRUNCATE TABLE `act_hi_actinst`;
TRUNCATE TABLE `act_hi_identitylink`;
TRUNCATE TABLE `act_hi_procinst`;
TRUNCATE TABLE `act_hi_taskinst`;
TRUNCATE TABLE `act_hi_varinst`;
TRUNCATE TABLE `act_ru_identitylink`;
TRUNCATE TABLE `act_ru_task`;
TRUNCATE TABLE `act_ru_variable`;
TRUNCATE TABLE `act_ru_execution`;
TRUNCATE TABLE act_ge_bytearray;
TRUNCATE TABLE act_re_model;
TRUNCATE TABLE act_re_procdef;
TRUNCATE TABLE act_re_deployment