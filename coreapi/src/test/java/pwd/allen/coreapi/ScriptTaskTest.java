package pwd.allen.coreapi;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricVariableInstance;
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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptTaskTest {

	private static final Logger logger = LoggerFactory.getLogger(ScriptTaskTest.class);

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

	/**
	 * 测试groovy脚本任务
	 */
	@Test
	@Deployment(resources = {"bpmn/my-script-groovy.bpmn20.xml"})
	public void test() {
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");

		HistoryService historyService = activitiRule.getHistoryService();

		List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId())
				.orderByVariableName().asc().listPage(0, 100);

		for (HistoricVariableInstance historicVariableInstance : historicVariableInstances) {
			logger.info("variable = {}", historicVariableInstance);
		}
		logger.info("variable.size {}", historicVariableInstances.size());
	}

	/**
	 * 测试juel脚本任务返回数据
	 */
	@Test
	@Deployment(resources = {"bpmn/my-script2.bpmn20.xml"})
	public void testJuelForReturn() {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("key1", 3);
		vars.put("key2", 5);
		ProcessInstance processInstance = activitiRule.getRuntimeService()
				.startProcessInstanceByKey("my-process", vars);

		HistoryService historyService = activitiRule.getHistoryService();

		List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId())
				.orderByVariableName().asc().listPage(0, 100);

		for (HistoricVariableInstance historicVariableInstance : historicVariableInstances) {
			logger.info("variable = {}", historicVariableInstance);
		}
		logger.info("variable.size {}", historicVariableInstances.size());
	}

	/**
	 * 脚本引擎跑脚本
	 */
	@Test
	public void testScriptEngine() throws ScriptException {
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("juel");

		Object eval = scriptEngine.eval("${1+2}");
		logger.info("value = {}", eval);
	}

}
