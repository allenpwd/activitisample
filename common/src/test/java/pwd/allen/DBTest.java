package pwd.allen;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

/**
 * 数据表操作
 * @author pwd
 * @create 2019-04-07 18:23
 **/
public class DBTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

    /**
     * 自动更新数据库
     * 原理：设置了databaseSchemaUpdate=true，会自动检测并初始化
     * 测试完成后会删除流程相关记录
     */
    @Test
    @Deployment(resources = {"bpmn/my-process.bpmn20.xml"})
    public void testDeploy() {
        ProcessDefinition processDefinition = activitiRule.getRepositoryService().createProcessDefinitionQuery().singleResult();
        LOGGER.info("流程部署 {}", processDefinition);
    }

    /**
     * 清空数据库
     * 缺点：数据和结构都删了，速度太慢
     */
    @Test
    public void testDrop() {
        activitiRule.getManagementService().executeCommand(new Command<Object>() {
            @Override
            public Object execute(CommandContext commandContext) {
                commandContext.getDbSqlSession().dbSchemaDrop();
//                commandContext.getDbSqlSession().dbSchemaPrune();//这个比较轻，
                return null;
            }
        });
    }

}
