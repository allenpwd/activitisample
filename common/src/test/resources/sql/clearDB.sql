DELETE  FROM  `act_hi_attachment`;
DELETE  FROM  `act_hi_comment`;
DELETE  FROM  `act_hi_detail`;
DELETE  FROM  `act_hi_actinst`;
DELETE  FROM  `act_hi_identitylink`;

DELETE  FROM  `act_hi_procinst`;
DELETE  FROM  `act_hi_taskinst`;
DELETE  FROM  `act_hi_varinst`;
DELETE  FROM  `act_ru_identitylink`;

DELETE  FROM  `act_ru_task`;

DELETE  FROM  `act_ru_variable`;

-- 删这个比较麻烦
DELETE  FROM  `act_ru_execution`;

DELETE  FROM act_ge_bytearray;

DELETE  FROM act_re_model;
DELETE  FROM act_re_procdef;
DELETE  FROM act_re_deployment