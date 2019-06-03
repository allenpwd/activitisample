package pwd.allen.coreapi;

import com.google.common.collect.Maps;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.ManagementService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.Execution;
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

/**
 * 服务任务
 */
public class ServiceTaskTest {

	private static final Logger logger = LoggerFactory.getLogger(ServiceTaskTest.class);

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");


	/**
	 * 执行实现JavaDelegate的类，当流程到达该节点会执行（可以用activiti:field为对象赋值），然后继续下一步流程
	 * 每次执行该节点都会重新创建一个JavaDelegate类
	 */
	@Test
	@Deployment(resources = {"bpmn/my-service01.bpmn20.xml"})
	public void test() {
		HashMap<String, Object> variables = Maps.newHashMap();
		variables.put("desc", "test java delegate");
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", variables);

		List<HistoricActivityInstance> activityInstances = activitiRule.getHistoryService().createHistoricActivityInstanceQuery()
				.orderByHistoricActivityInstanceEndTime().asc()
				.list();

		for (HistoricActivityInstance activityInstance : activityInstances) {
			logger.info("activity = {}", activityInstance);
		}
	}

	/**
	 * 执行实现ActivityBehavior的类，流程到达该节点会执行，然后会等待
	 * 每次执行该节点都会重新创建一个ActivityBehavior类
	 */
	@Test
	@Deployment(resources = {"bpmn/my-service02.bpmn20.xml"})
	public void test02() {

		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");

		List<HistoricActivityInstance> activityInstances = activitiRule.getHistoryService().createHistoricActivityInstanceQuery()
				.orderByHistoricActivityInstanceEndTime().asc()
				.list();

		for (HistoricActivityInstance activityInstance : activityInstances) {
			logger.info("activity = {}", activityInstance);
		}
		Execution execution = activitiRule.getRuntimeService().createExecutionQuery()
				.activityId("serviceOne").singleResult();
		logger.info("execution = {}", execution);


		//让流程节点继续走下去
		ManagementService managementService = activitiRule.getManagementService();
		managementService.executeCommand(new Command<Object>() {
			@Override
			public Object execute(CommandContext commandContext) {
				ActivitiEngineAgenda agenda = commandContext.getAgenda();
				agenda.planTakeOutgoingSequenceFlowsOperation((ExecutionEntity) execution, false);
				return null;
			}
		});

		activityInstances = activitiRule.getHistoryService().createHistoricActivityInstanceQuery()
				.orderByHistoricActivityInstanceEndTime().asc()
				.list();

		for (HistoricActivityInstance activityInstance : activityInstances) {
			logger.info("activity = {}", activityInstance);
		}
	}

}
