package pwd.allen.coreapi;

import com.google.common.collect.Maps;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
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
public class EventTest {

    private static final Logger logger = LoggerFactory.getLogger(EventTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    /**
     * 触发信号事件
     */
    @Test
    @Deployment(resources = "bpmn/event/signalEvent.bpmn20.xml")
    public void testSignalEvent() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        //根据流程定义key启动流程，默认使用最新版本，常用
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");

        //获取receiveTask1执行节点
        Execution execution = runtimeService.createExecutionQuery().activityId("receiveTask1").singleResult();
        logger.info("当前执行节点为 {}", execution);

        //触发 receiveTask
        runtimeService.trigger(execution.getId());

        //触发之后流程到了catchEvent，会在事件表(ACT_RU_EVENT_SUBSCR)中产生事件描述数据，EVENT_TYPE_字段值为signal

        //获取catchEvent执行节点
        execution = runtimeService.createExecutionQuery().activityId("catchEvent").singleResult();
        logger.info("当前执行节点为 {}", execution);

        //发送信号给事件，结束节点
        //这里传的参数是signalName，即signal标签的name属性，传错的话不会触发
        runtimeService.signalEventReceived("mySignalName");

        List<Execution> list = runtimeService.createExecutionQuery()
                .processInstanceId(processInstance.getId()).list();
        logger.info("触发 catching 事件后，执行流数量：{}", list.size());
    }

    /**
     * 触发信号事件
     */
    @Test
    @Deployment(resources = "bpmn/event/signalEvent.bpmn20.xml")
    public void testMessageEvent() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        //根据流程定义key启动流程，默认使用最新版本，常用
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");

        //获取receiveTask1执行节点
        Execution execution = runtimeService.createExecutionQuery().activityId("receiveTask1").singleResult();
        logger.info("当前执行节点为 {}", execution);

        //触发 receiveTask
        runtimeService.trigger(execution.getId());

        //触发之后流程到了catchEvent，会在事件表(ACT_RU_EVENT_SUBSCR)中产生事件描述数据，EVENT_TYPE_字段值为signal

        //获取catchEvent执行节点
        execution = runtimeService.createExecutionQuery().activityId("catchEvent").singleResult();
        logger.info("当前执行节点为 {}", execution);

        //发送信号给事件，结束节点
        //这里传的参数是signalName，即signal标签的name属性，传错的话不会触发
        runtimeService.signalEventReceived("mySignalName");

        List<Execution> list = runtimeService.createExecutionQuery()
                .processInstanceId(processInstance.getId()).list();
        logger.info("触发 catching 事件后，执行流数量：{}", list.size());
    }

}
