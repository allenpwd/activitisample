package pwd.allen;

import lombok.extern.slf4j.Slf4j;
import pwd.allen.delegate.HelloBean;
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

import java.util.Collections;
import java.util.List;

/**
 * spring整合 activiti
 * 使用：pom需引入activiti-spring
 * 功能：juel表达式能使用spring容器里的bean
 *
 * @author pwd
 * @create 2019-04-07 18:23
 **/
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:activiti-context.xml"})
public class ConfigSpringTest {

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
    @Deployment(resources = {"bpmn/my-process-spring.bpmn20.xml"})
    public void test() {
        // 启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");
        Task task = taskService.createTaskQuery().singleResult();

        //在调用complete方法时，会将完成的Task数据从任务表中删除，如果发现这个任务为流程中的最后一个任务，则会连同流程实例的数据一并删除
        //并按照历史配置来记录流程的历史数据。
        try {
            taskService.complete(task.getId(), Collections.singletonMap("type", "success"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试service执行抛出异常
     * 结果：
     *  报错：org.activiti.engine.ActivitiException: Could not execute service task expression
     *  流程仍然在上个节点，可再次执行complete
     */
    @Test
    @Deployment(resources = {"bpmn/my-process-spring.bpmn20.xml"})
    public void serviceError() {
        // 启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");
        Task task = taskService.createTaskQuery().singleResult();

        try {
            taskService.complete(task.getId(), Collections.singletonMap("type", "error"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Task newTask = task = taskService.createTaskQuery().singleResult();
        log.info("task是否同一个:{}", task.getId().equals(newTask.getId()));

        try {
            taskService.complete(task.getId(), Collections.singletonMap("type", "success"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
