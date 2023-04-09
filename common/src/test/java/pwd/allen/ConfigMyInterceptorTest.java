package pwd.allen;

import lombok.extern.slf4j.Slf4j;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试自定义的命令拦截器
 * @author pwd
 * @create 2019-04-07 18:23
 **/
@Slf4j
public class ConfigMyInterceptorTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti_interceptor.cfg.xml");

    @Test
    @Deployment(resources = {"bpmn/my-process.bpmn20.xml"})
    public void test() {

        Map<String, ProcessEngine> processEngines = ProcessEngines.getProcessEngines();
        log.info(processEngines.toString());

        // 这里会产生StartProcessInstanceCmd命令
        ProcessInstance processInstance = activitiRule.getRuntimeService()
                .startProcessInstanceByKey("my-process", Collections.singletonMap("day", 10));
        // 这里会产生TaskQueryImpl命令
        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        // 这里会产生CompleteTaskCmd命令
        activitiRule.getTaskService().complete(task.getId());
        log.info("");
    }
}
