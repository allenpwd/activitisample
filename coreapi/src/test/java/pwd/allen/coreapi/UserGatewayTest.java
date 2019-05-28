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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserGatewayTest {

	private static final Logger logger = LoggerFactory.getLogger(UserGatewayTest.class);

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

	/**
	 * 测试排他网关
	 */
	@Test
	@Deployment(resources = {"bpmn/gateway/my-process-Exclusive.bpmn20.xml"})
	public void testExclusive() {
		Map<String, Object> vars = new HashMap<String, Object>();
		//如果流程线配置了判断条件，条件里的变量不存在会报Unknown property used in expression
		vars.put("score", 56);
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", vars);

		TaskService taskService = activitiRule.getTaskService();
		List<Task> tasks = taskService.createTaskQuery().listPage(0, 100);
		for (Task task : tasks) {
			logger.info("task = {}", task);
			taskService.complete(task.getId());
		}

		tasks = taskService.createTaskQuery().listPage(0, 100);
		logger.info("task size = {}", tasks.size());
	}

	/**
	 * 测试并行网关
	 */
	@Test
	@Deployment(resources = {"bpmn/gateway/my-process-parallel.bpmn20.xml"})
	public void testParallel() {
		Map<String, Object> vars = new HashMap<String, Object>();

		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", vars);

		TaskService taskService = activitiRule.getTaskService();
		List<Task> tasks = taskService.createTaskQuery().listPage(0, 100);
		for (Task task : tasks) {
			logger.info("task = {}", task);
			taskService.complete(task.getId());
		}

		tasks = taskService.createTaskQuery().listPage(0, 100);
		logger.info("task size = {}", tasks.size());
		for (Task task : tasks) {
			logger.info("task = {}", task);
			taskService.complete(task.getId());
		}
	}

	/**
	 * 测试包容网关
	 */
	@Test
	@Deployment(resources = {"bpmn/gateway/my-process-inclusive.bpmn20.xml"})
	public void testInclusive() {
		Map<String, Object> vars = new HashMap<String, Object>();
		//day为请假天数，小于5天才走部门经理和主管会签，否则直接结束
		vars.put("day", 5);
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", vars);

		TaskService taskService = activitiRule.getTaskService();
		List<Task> tasks = taskService.createTaskQuery().listPage(0, 100);
		for (Task task : tasks) {
			logger.info("task = {}", task);
			taskService.complete(task.getId());
		}

		tasks = taskService.createTaskQuery().listPage(0, 100);
		logger.info("task size = {}", tasks.size());
		for (Task task : tasks) {
			logger.info("task = {}", task);
			taskService.complete(task.getId());
		}
	}

}
