package pwd.allen.coreapi;

import org.activiti.engine.TaskService;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pwd.allen.bean.MyJavaBean;

import java.util.HashMap;

public class TaskListenerTest {

	private static final Logger logger = LoggerFactory.getLogger(TaskListenerTest.class);

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");


	/**
	 * class属性指定任务监听器（需实现TaskListener接口）
	 * 测试任务监听器TaskListener方式设置候选人或候选组
	 */
	@Test
	@Deployment(resources = {"bpmn/userTask/my-userTask2.bpmn20.xml"})
	public void taskListenerClass() {
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");

		TaskService taskService = activitiRule.getTaskService();
		Task task = taskService.createTaskQuery()
				.taskCandidateUser("user1").singleResult();
		logger.info("find by user1 task={}", task);

	}

    /**
     * expression属性指定，使用juel表达式调用bean方法，可以使用内置变量task
     */
	@Test
	@Deployment(resources = {"bpmn/taskListener/taskListener.expression.bpmn20.xml"})
	public void taskListenerExpression() {

		HashMap<String, Object> varMap = new HashMap<>();
		varMap.put("myJavaBean", new MyJavaBean());
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", varMap);

	}

}
