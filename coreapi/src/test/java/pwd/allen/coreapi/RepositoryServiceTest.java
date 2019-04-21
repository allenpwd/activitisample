package pwd.allen.coreapi;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author 门那粒沙
 * @create 2019-04-21 10:41
 **/
public class RepositoryServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(RepositoryServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    public void testRepository() {
        RepositoryService repositoryService = activitiRule.getRepositoryService();

        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.name("测试部署资源")
                .addClasspathResource("pwd/allen/my-process.bpmn20.xml")
                .addClasspathResource("second_approve.bpmn20.xml");

        //部署两个流程文件，生成的记录有：一个部署记录、两个流程定义记录、两个流程数据流记录、一个流程图记录（my-process没有位置信息，所以没有生成流程图记录）
        Deployment deploy = deploymentBuilder.deploy();
        logger.info("deploy = {}", deploy);

        deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.name("测试部署资源")
                .addClasspathResource("pwd/allen/my-process.bpmn20.xml")
                .addClasspathResource("second_approve.bpmn20.xml");
        deploymentBuilder.deploy();

        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
        List<Deployment> deployment = deploymentQuery.orderByDeploymenTime().asc().listPage(0, 100);

        logger.info("deployment size is {}", deployment.size());

        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery()
                .listPage(0, 100);

        logger.info("processDefinitions size is {}", processDefinitions.size());

        for (ProcessDefinition processDefinition : processDefinitions) {
            logger.info("processDefinition = {}, version = {}, key = {}, id = {}",
                    processDefinition, processDefinition.getVersion(),
                    processDefinition.getKey(), processDefinition.getId());
        }
    }

    /**
     * 测试流程定义挂起，挂起后不能启动该流程
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = "pwd/allen/my-process.bpmn20.xml")
    public void testSuspend() {
        RepositoryService repositoryService = activitiRule.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();

        logger.info("processDefinition id {}", processDefinition.getId());

        repositoryService.suspendProcessDefinitionById(processDefinition.getId());

        try {
            logger.info("开始启动");
            activitiRule.getRuntimeService().startProcessInstanceById(processDefinition.getId());
            logger.info("启动成功");
        } catch (Exception e) {
            logger.info("启动失败");
            logger.info(e.getMessage(), e);
        }

        repositoryService.activateProcessDefinitionById(processDefinition.getId());

        logger.info("开始启动");
        activitiRule.getRuntimeService().startProcessInstanceById(processDefinition.getId());
        logger.info("启动成功");

    }

    /**
     * 配置启动流程权限
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = "pwd/allen/my-process.bpmn20.xml")
    public void testCandidateStarter() {
        RepositoryService repositoryService = activitiRule.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();

        logger.info("processDefinition.id {}", processDefinition.getId());

        repositoryService.addCandidateStarterUser(processDefinition.getId(), "user");
        repositoryService.addCandidateStarterGroup(processDefinition.getId(), "groupM");

        //获取流程绑定的用户或用户组
        List<IdentityLink> identityLinksForProcessDefinition = repositoryService.getIdentityLinksForProcessDefinition(processDefinition.getId());

        for (IdentityLink identityLink : identityLinksForProcessDefinition) {
            logger.info("identityLink {}", identityLink);
        }

        repositoryService.deleteCandidateStarterGroup(processDefinition.getId(), "groupM");
        repositoryService.deleteCandidateStarterUser(processDefinition.getId(), "user");
    }
}
