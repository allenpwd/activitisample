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

/**
 * 单元测试注解 创建部署流程
 * @author pwd
 * @create 2019-04-07 18:23
 **/
public class Test02 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Test02.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti_druid.cfg.xml");

    @Test
    @Deployment(resources = {"bpmn/my-process.bpmn20.xml"})
    public void testDeploy() {
        /*activitiRule.getRepositoryService().createDeployment()
                .addClasspathResource("second_approve.bpmn20.xml")
                .deploy();*/
        ProcessDefinition processDefinition = activitiRule.getRepositoryService().createProcessDefinitionQuery().singleResult();
        LOGGER.info("流程部署 {}", processDefinition);
    }

    /**
     * 清空数据库
     */
    @Test
    public void testDrop() {
        activitiRule.getManagementService().executeCommand(new Command<Object>() {
            @Override
            public Object execute(CommandContext commandContext) {
                commandContext.getDbSqlSession().dbSchemaDrop();
                return null;
            }
        });
    }

}
