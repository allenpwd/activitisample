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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserTaskTest {

	private static final Logger logger = LoggerFactory.getLogger(UserTaskTest.class);

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

	/**
	 * 以流程配置文件方式指定任务的候选人或候选组，设置代理人
	 */
	@Test
	@Deployment(resources = {"bpmn/my-userTask.bpmn20.xml"})
	public void test() {
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");

		//候选人
		TaskService taskService = activitiRule.getTaskService();
		Task task = taskService.createTaskQuery()
				.taskCandidateUser("user1").singleResult();
		logger.info("find by user1 task={}", task);

		task = activitiRule.getTaskService().createTaskQuery()
				.taskCandidateUser("user2").singleResult();
		logger.info("find by user2 task={}", task);

		//候选组
		task = activitiRule.getTaskService().createTaskQuery()
				.taskCandidateGroup("group1").singleResult();
		logger.info("find by group1 task={}", task);

		//指定代理人 指定代理人之后候选人就失效了
		//方式一 claim 推荐该方式 会校验是否已指定了代理人，如果是且指定的代理人与参数userId不同则抛错
		taskService.claim(task.getId(), "user1");

		//方式二 setAssignee 不推荐 直接设置，之前设置的会被覆盖
//		taskService.setAssignee(task.getId(), "user1");
		logger.info("claim task id={}", task.getId());

		//因为已指定候选人，所以用user2查不到task
		task = taskService.createTaskQuery().taskCandidateOrAssigned("user2").singleResult();
		logger.info("find by user2 task={}", task);
	}

	/**
	 * 测试TaskListener方式设置候选人或候选组
	 */
	@Test
	@Deployment(resources = {"bpmn/my-userTask2.bpmn20.xml"})
	public void testTaskListener() {
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");

		TaskService taskService = activitiRule.getTaskService();
		Task task = taskService.createTaskQuery()
				.taskCandidateUser("user1").singleResult();
		logger.info("find by user1 task={}", task);

		task = activitiRule.getTaskService().createTaskQuery()
				.taskCandidateUser("user2").singleResult();
		logger.info("find by user2 task={}", task);

		task = activitiRule.getTaskService().createTaskQuery()
				.taskCandidateGroup("group1").singleResult();
		logger.info("find by group1 task={}", task);
	}

    /**
     * 清空DATABASE
     */
	@Test
	public void clearDB() {
		activitiRule.getManagementService().executeCommand(new Command<Object>() {
			@Override
			public Object execute(CommandContext commandContext) {
				commandContext.getDbSqlSession().dbSchemaDrop();
				return null;
			}
		});
	}

}
