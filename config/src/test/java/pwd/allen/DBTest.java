package pwd.allen;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pwd
 * @create 2019-04-07 18:23
 **/
public class DBTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBTest.class);

    @Test
    public void testConfig1() {
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResourceDefault();

        LOGGER.info("configuration {}", configuration);
        ProcessEngine processEngine = configuration.buildProcessEngine();
        LOGGER.info("获取流程引擎 {}", processEngine);
        processEngine.close();
    }

    @Test
    public void testConfig2() {
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti_druid.cfg.xml");

        LOGGER.info("configuration {}", configuration);
        ProcessEngine processEngine = configuration.buildProcessEngine();
        LOGGER.info("获取流程引擎 {}", processEngine);
        processEngine.close();
    }

}
