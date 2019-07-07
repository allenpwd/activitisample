package pwd.allen.coreapi;

import com.google.common.collect.Maps;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author 门那粒沙
 * @create 2019-04-21 16:37
 **/
public class RuntimeServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(RuntimeServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

    /**
     * 启动流程
     */
    @Test
    @Deployment(resources = "bpmn/my-process.bpmn20.xml")
    public void testStartProcess() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        ProcessDefinition processDefinition = activitiRule.getRepositoryService().createProcessDefinitionQuery().singleResult();

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");

        //根据流程定义key启动流程，默认使用最新版本，常用
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process", variables);

        logger.info("processInstance {}", processInstance);
    }

    /**
     * 使用processInstanceBuilder启动流程
     */
    @Test
    @Deployment(resources = "bpmn/my-process.bpmn20.xml")
    public void testProcessInstanceBuilder() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        ProcessDefinition processDefinition = activitiRule.getRepositoryService().createProcessDefinitionQuery().singleResult();

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");

        ProcessInstanceBuilder processInstanceBuilder = runtimeService.createProcessInstanceBuilder();

        ProcessInstance processInstance = processInstanceBuilder.businessKey("businessKey001")
                .processDefinitionKey("my-process")
                .variables(variables)
                .start();

        logger.info("processInstance {}", processInstance);
    }

    /**
     * 操作流程变量
     */
    @Test
    @Deployment(resources = "bpmn/my-process.bpmn20.xml")
    public void testVariables() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        ProcessDefinition processDefinition = activitiRule.getRepositoryService().createProcessDefinitionQuery().singleResult();

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");
        variables.put("key2", "value2");

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), variables);
        logger.info("processInstance {}", processInstance);

        //增加（之前不存在）、修改（之前已存在）、删除流程实例全局变量
        runtimeService.setVariable(processInstance.getId(), "key3", "value3");
        runtimeService.setVariable(processInstance.getId(), "key2", "value2-1");
        runtimeService.removeVariable(processInstance.getId(), "key1");

        //获取流程实例的变量
        Map<String, Object> variables1 = runtimeService.getVariables(processInstance.getId());
        logger.info("variables1 {}", variables1);

    }

    /**
     * 查询流程实例
     */
    @Test
    @Deployment(resources = "bpmn/my-process.bpmn20.xml")
    public void testProcessInstanceQuery() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        ProcessDefinition processDefinition = activitiRule.getRepositoryService().createProcessDefinitionQuery().singleResult();

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        logger.info("processInstance {}", processInstance);

        //流程启动后因为有个userTask，所以流程会存在着等待执行
        ProcessInstance processInstance1 = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
    }

    /**
     * 查询流程对象
     */
    @Test
    @Deployment(resources = "bpmn/my-process.bpmn20.xml")
    public void testExecutionQuery() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        ProcessDefinition processDefinition = activitiRule.getRepositoryService().createProcessDefinitionQuery().singleResult();

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), "myBusinessKey");
        logger.info("processInstance {}", processInstance);

        //流程启动后因为有个userTask，所以流程会存在着等待执行
        List<Execution> executions = runtimeService.createExecutionQuery()
                .listPage(0, 100);

        for (Execution execution : executions) {
            logger.info("execution {}", execution);
        }
    }

    /**
     * 使用trigger触发receiveTask节点
     */
    @Test
    @Deployment(resources = "bpmn/my-process-trigger.bpmn20.xml")
    public void testTrigger() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");

        //获取receiveTask的那个任务流
        Execution execution = runtimeService.createExecutionQuery()
                .activityId("someTask")
                .singleResult();
        logger.info("execution {}", execution);

        //触发该执行流
        runtimeService.trigger(execution.getId());

        //再次获取的时候任务流为null
        execution = runtimeService.createExecutionQuery()
                .activityId("someTask")
                .singleResult();
        logger.info("execution {}", execution);
    }

    /**
     * 触发信号捕获事件signalEventReceived
     */
    @Test
    @Deployment(resources = "bpmn/my-process-signal.bpmn20.xml")
    public void testSignalEventReceived() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");

        //获取订阅了my-signal信号的任务流
        Execution execution = runtimeService.createExecutionQuery()
                .signalEventSubscriptionName("my-signal")
                .singleResult();
        logger.info("exection {}", execution);

        //通知工作流引擎接受到my-signal信号事件，会递送信号到所有等待该信号事件的执行流
        runtimeService.signalEventReceived("my-signal");

        execution = runtimeService.createExecutionQuery()
                .signalEventSubscriptionName("my-signal")
                .singleResult();
        logger.info("exection {}", execution);
    }

    /**
     * 触发消息捕获事件messageEventReceived，与信号不同的是需要指定某个execution
     */
    @Test
    @Deployment(resources = "bpmn/my-process-message.bpmn20.xml")
    public void testMessage() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");

        //获取订阅了my-message信息的任务流
        Execution execution = runtimeService.createExecutionQuery()
                .messageEventSubscriptionName("my-message")
                .singleResult();
        logger.info("exection {}", execution);

        //通知工作流引擎接受到my-message信息事件
        runtimeService.messageEventReceived("my-message", execution.getId());

        execution = runtimeService.createExecutionQuery()
                .messageEventSubscriptionName("my-message")
                .singleResult();
        logger.info("exection {}", execution);
    }
}
