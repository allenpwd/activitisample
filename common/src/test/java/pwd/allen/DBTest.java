package pwd.allen;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
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

import java.util.List;
import java.util.Map;

/**
 * 数据表操作
 * @author pwd
 * @create 2019-04-07 18:23
 **/
@Slf4j
public class DBTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

    @Rule
    public ActivitiRule activitiRule2 = new ActivitiRule("activiti-h2.cfg.xml");

    /**
     * 自动更新数据库
     * 原理：设置了databaseSchemaUpdate=true，会自动检测并初始化
     * 测试完成后会删除流程相关记录
     */
    @Test
    @Deployment(resources = {"bpmn/my-process.bpmn20.xml"})
    public void testDeploy() {
        List<ProcessDefinition> processDefinitions = activitiRule.getRepositoryService().createProcessDefinitionQuery().list();
        log.info("流程部署 {}", processDefinitions);
    }

    /**
     * 可以创建多个流程引擎，如果processEngineName没有指定，默认为default，这里通过@Rule创建了两个：mysqlEngine和h2Engine
     */
    @Test
    public void getProcessEngine() {
        Map<String, ProcessEngine> processEngines = ProcessEngines.getProcessEngines();
        log.info(processEngines.toString());
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
