package pwd.allen.coreapi;

import org.activiti.engine.TaskService;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网关
 * 如果没一个满足会抛异常（org.activiti.engine.ActivitiException: No outgoing sequence flow of the exclusive gateway 'gatewayName' could be selected for continuing the process）
 */
public class UserGatewayTest {

	private static final Logger logger = LoggerFactory.getLogger(UserGatewayTest.class);

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

	/**
	 * 测试排他网关 只会走一条线，如果多个条件满足则按顺序走最先满足的，
	 * 如果没有一条满足则报错：No outgoing sequence flow of the exclusive gateway 'myGatewayId' could be selected for continuing the process
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
		vars.put("day", 10);
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
	 * 测试事件网关（eventBasedGateway）
	 * 事件网关只会选择最先触发的事件所在的分支向前执行
	 * eventBasedGateway只能指向中间捕获事件，否则会报错：Event based gateway can only be connected to elements of type intermediateCatchEvent
	 * 场景：事件网关流向两个中间事件，一个定时中间事件（5秒），一个信号中间捕获事件；
	 * 5秒内发出mySignalName信号则走task1，否则走task2
	 */
	@Test
	@Deployment(resources = {"bpmn/gateway/eventBaseGateway.bpmn20.xml"})
	public void eventBaseGateway() throws InterruptedException {
		//流程走到事件网关会产生两个中间事件，等待事件触发执行
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");

		TaskService taskService = activitiRule.getTaskService();

		logger.info("tasks.size:{}", taskService.createTaskQuery().processInstanceId(processInstance.getId()).count());

		Thread.sleep(6000);

		activitiRule.getRuntimeService().signalEventReceived("mySignalName");

		logger.info("tasks.size:{}", taskService.createTaskQuery().processInstanceId(processInstance.getId()).count());
	}

}
