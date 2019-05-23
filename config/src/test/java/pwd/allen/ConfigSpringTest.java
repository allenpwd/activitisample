package pwd.allen;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pwd.allen.delegate.HelloBean;

/**
 * @author pwd
 * @create 2019-04-07 18:23
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:activiti-context.xml"})
public class ConfigSpringTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigSpringTest.class);

    @Rule
    @Autowired
    public ActivitiRule activitiRule;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HelloBean helloBean;

    @Test
    @Deployment(resources = {"pwd/allen/my-process-spring.bpmn20.xml"})
    public void test() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");
        Task task = taskService.createTaskQuery().singleResult();
        taskService.complete(task.getId());

        helloBean.sayHello();
    }
}
