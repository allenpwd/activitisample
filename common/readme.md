#### databaseSchemaUpdate
为true会自动创建流程相关表结构，建表sql位于：activiti-engine-6.0.0.jar!\org\activiti\db\create

#### historyLevel
history属性有以下取值
- none：不保存任何的历史数据，因此，在流程执行过程中，这是最高效的。 
- activity：级别高于none，保存流程实例与流程行为，其他数据不保存。 
- audit：除 activity级别会保存的数据外，还会保存全部的流程任务及其属性数据。 为默认值。 
- full：保存历史数据的最高级别，除了会保存 audit级别的数据外，还会保存其他全部流程相关的细节数据，包括一些流程参数等。