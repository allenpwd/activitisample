package pwd.allen;

import javafx.scene.DepthTest;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 注解方式创建和部署流程
 * @author pwd
 * @create 2019-04-07 18:23
 **/
public class Test02 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Test02.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti_druid.cfg.xml");

    /**
     * 测试完后会删除记录
     */
    @Test
    @Deployment(resources = {"bpmn/my-process.bpmn20.xml"})
    public void testDeploy() {
        /*activitiRule.getRepositoryService().createDeployment()
                .addClasspathResource("second_approve.bpmn")
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
//                commandContext.getDbSqlSession().dbSchemaPrune();
                return null;
            }
        });
    }

    /**
     * 查询并导出发布时自动生成的流程图
     * 若流程图乱码，则可以在配置文件中指定字体为宋体
     * @throws IOException
     */
    @Test
    public void img() throws IOException {
        org.activiti.engine.repository.Deployment deployment = activitiRule.getRepositoryService().createDeploymentQuery().orderByDeploymenTime().desc().listPage(0, 1).get(0);

        List<String> deploymentResourceNames = activitiRule.getRepositoryService().getDeploymentResourceNames(deployment.getId());
        for (String deploymentResourceName : deploymentResourceNames) {
            if (deploymentResourceName.endsWith("png")) {
                InputStream inputStream = activitiRule.getRepositoryService().getResourceAsStream(deployment.getId(), deploymentResourceName);
                FileCopyUtils.copy(inputStream, new FileOutputStream(System.getProperty("user.home") + "\\Desktop\\test.png"));
            }
        }
    }

}
