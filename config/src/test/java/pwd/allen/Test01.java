package pwd.allen;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 代码方式创建和部署流程
 * @author pwd
 * @create 2019-04-07 18:23
 **/
public class Test01 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Test01.class);

    @Test
    public void testConfig1() {
        /* 创建流程引擎配置对象 begin */
        //方式一：以下代码相当于调用
        //BeansConfigurationHelper.parseProcessEngineConfigurationFromResource("activiti.cfg.xml", "processEngineConfiguration");
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResourceDefault();

        //方式二：直接指定流程引擎配置对象的xml配置文件
        /*ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti_druid.cfg.xml");*/

        //方式三：采用默认的流程引擎配置
        //默认属性有：databaseSchemaUpdate=create-drop jdbcUrl=jdbc:h2:mem:activiti
        /*ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createStandaloneInMemProcessEngineConfiguration();*/

        //配置对象的属性可以在xml里指定，也可以直接调用对象的set方法进行设置
        LOGGER.info("configuration {}", configuration);
        /* 创建流程引擎配置对象 end */

        //配置对象根据配置构建流程引擎
        ProcessEngine processEngine = configuration.buildProcessEngine();
        LOGGER.info("获取流程引擎 {}", processEngine);

        //部署流程文件，部署后才能使用流程
        Deployment deploy = processEngine.getRepositoryService()
                .createDeployment()
                .name("测试部署")
                .addClasspathResource("bpmn/my-process.bpmn20.xml")
                .deploy();

        processEngine.close();
    }

}
