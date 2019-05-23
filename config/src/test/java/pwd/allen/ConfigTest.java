package pwd.allen;

import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pwd
 * @create 2019-04-07 18:23
 **/
public class ConfigTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigTest.class);

    @Test
    public void testConfig1() {
        //默认配置文件为activiti.cfg.xml， beanId为processEngineConfiguration
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResourceDefault();

        LOGGER.info("configuration {}", configuration);
    }

    @Test
    public void testConfig2() {
        // 不需要读取任何配置文件，采用默认的流程引擎配置
        // databaseSchemaUpdate=create-drop jdbcUrl=jdbc:h2:mem:activiti
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createStandaloneInMemProcessEngineConfiguration();

        LOGGER.info("configuration {}", configuration);
    }
}
