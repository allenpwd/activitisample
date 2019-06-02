package pwd.allen;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试自定义的命令拦截器
 * @author pwd
 * @create 2019-04-07 18:23
 **/
public class ConfigMyInterceptorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigMyInterceptorTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti_interceptor.cfg.xml");

    @Test
    @Deployment(resources = {"bpmn/my-process.bpmn20.xml"})
    public void test() {

        Map<String, ProcessEngine> processEngines = ProcessEngines.getProcessEngines();
        System.out.println(processEngines.size());

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("day", 10);
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", vars);
//        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
//        activitiRule.getTaskService().complete(task.getId());
    }
}
