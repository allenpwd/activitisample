package pwd.allen;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.cfg.BeansConfigurationHelper;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 代码方式创建和部署流程
 * @author pwd
 * @create 2019-04-07 18:23
 **/
public class Test01 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Test01.class);

    private ProcessEngine processEngine;

    @Before
    public void init() {
        //<editor-fold desc="创建流程引擎配置对象">
        //方式一：约定的方式
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResourceDefault();
        //以上代码相当于调用
//        BeansConfigurationHelper.parseProcessEngineConfigurationFromResource("activiti.cfg.xml", "processEngineConfiguration");

        //方式二：直接指定流程引擎配置对象的xml配置文件
        /*ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti_druid.cfg.xml");*/

        //方式三：采用默认的流程引擎配置
        //默认属性有：databaseSchemaUpdate=create-drop jdbcUrl=jdbc:h2:mem:activiti
        /*ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createStandaloneInMemProcessEngineConfiguration();*/

        //配置对象的属性可以在xml里指定，也可以直接调用对象的set方法进行设置
        LOGGER.info("configuration {}", configuration);
        //</editor-fold>

        //配置对象根据配置构建流程引擎
        processEngine = configuration.buildProcessEngine();
        LOGGER.info("获取流程引擎 {}", processEngine);
    }

    @After
    public void close() {
        processEngine.close();
    }

    /**
     * 发布流程
     */
    @Test
    public void deploy() {
        //部署流程文件，部署后才能使用流程
        Deployment deploy = processEngine.getRepositoryService()
                .createDeployment()
                .name("测试部署")
                .addClasspathResource("bpmn/my-process.bpmn20.xml")
                .deploy();
    }

    /**
     * 根据流程key获取流程定义
     */
    @Test
    public void getProcess() throws IOException {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //获取流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("my-process").latestVersion().singleResult();
        LOGGER.info("process={}", processDefinition.getId());

        //获取流程定义内容
        InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());
        LOGGER.info("resource.size={}", resourceAsStream.available());
    }

    /**
     * 启动一个my-process流程
     */
    @Test
    public void instance() throws IOException {

        //启动流程
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey("my-process");
        System.out.println(processInstance);

        Task task = processEngine.getTaskService().createTaskQuery().singleResult();
        processEngine.getTaskService().complete(task.getId());
    }

}
