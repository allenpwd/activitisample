package pwd.allen.coreapi;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author 门那粒沙
 * @create 2019-04-21 16:37
 **/
public class EventTest {

    private static final Logger logger = LoggerFactory.getLogger(EventTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

    /**
     *  * 定时事件
     *  以下三个子元素用来定义时间，需要遵守ISO 8601轨迹标准。
     *  * 1. 指定时间（timeDate）
     *      例子：2019-07-20T10:00:00
     *  * 2. 指定定时器激活后多久的时间内会被运行（timeDuration）
     *      例子：PT5M(5分钟后)
     *  * 3. 周期执行（timeCycle）：支持cron表达式
     *  * 定义方式
     *  * <timerEventDefinition>
     *  *     <timeDate>2018-01-01T10:10:00</timeDate>
     *  * </timerEventDefinition>
     * 有定时事件，需要打开异步执行器<property name="asyncExecutorActivate" value="true" />
     * 测试定时边界事件
     * 功能描述：5秒之内commonTask没有执行完就会执行timeoutTask
     */
    @Test
    @Deployment(resources = "bpmn/event/boundaryEvent.bpmn20.xml")
    public void testBoundaryEvent() throws InterruptedException {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        //根据流程定义key启动流程，默认使用最新版本，常用
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");

        List<Task> tasks = activitiRule.getTaskService().createTaskQuery().listPage(0, 100);

        for (Task task : tasks) {
            logger.info("task={}", task);
            //activitiRule.getTaskService().complete(task.getId());
        }
        logger.info("tasks.size={}", tasks.size());

        //5秒之内commonTask没有执行完就会执行timeoutTask,如果边界事件的cancelActivity=true,则还会结束commonTask
        Thread.sleep(1000 * 10);

        tasks = activitiRule.getTaskService().createTaskQuery().listPage(0, 100);
        for (Task task : tasks) {
            logger.info("task={}", task);
        }
        logger.info("tasks.size={}", tasks.size());
    }

    /**
     * 定时开始事件
     * 开始事件如果没有指定任何触发条件，就需要使用RuntimeService的startProcessByXXX方法去启动流程。
     * （子流程的开始事件指定的触发条件是无效的，因为流程到达子流程时就意味着子流程需要启动，并不需要其他的启动条件）
     * 示例功能描述：每隔5秒启动一次流程
     */
    @Test
    @Deployment(resources = "bpmn/event/startEvent.bpmn20.xml")
    public void testErrorEvent() throws InterruptedException {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        List<Task> tasks = activitiRule.getTaskService().createTaskQuery().listPage(0, 100);
        logger.info("tasks.size={}", tasks.size());

        Thread.sleep(1000 * 10);
        tasks = activitiRule.getTaskService().createTaskQuery().listPage(0, 100);
        logger.info("tasks.size={}", tasks.size());

        Thread.sleep(1000 * 10);
        tasks = activitiRule.getTaskService().createTaskQuery().listPage(0, 100);
        logger.info("tasks.size={}", tasks.size());

        Thread.sleep(1000 * 10);
        tasks = activitiRule.getTaskService().createTaskQuery().listPage(0, 100);
        logger.info("tasks.size={}", tasks.size());
    }

    /**
     * 各种中间捕获时间
     */
    @Test
    @Deployment(resources = "bpmn/event/IntermediateCatchEvent.bpmn20.xml")
    public void testIntermediateCatchEvent() throws InterruptedException {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        //根据流程定义key启动流程，默认使用最新版本，常用
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");


        /** signalCatchEvent begin **/
        //流程到了信号捕获事件，会在事件表(ACT_RU_EVENT_SUBSCR)中产生事件描述数据，EVENT_TYPE_字段值为signal
        //获取catchEvent执行节点
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).onlyChildExecutions().singleResult();
        logger.info("当前执行节点为 {}", execution);

        //发送信号给事件，结束节点，结束后事件订阅表（ACT_RU_EVENT_SUBSCR）中相关数据会删除
        //这里传的参数是signalName，即signal标签的name属性，传错的话不会触发
        runtimeService.signalEventReceived("mySignalName");
        /** signalCatchEvent end **/

        /** messageCatchEvent begin **/
        //流程到了信息捕获事件，会在事件表(ACT_RU_EVENT_SUBSCR)中产生事件描述数据，EVENT_TYPE_字段值为message
        //获取catchEvent执行节点
        execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).onlyChildExecutions().singleResult();
        logger.info("当前执行节点为 {}", execution);

        //发送信息给执行节点，结束节点，结束后事件订阅表（ACT_RU_EVENT_SUBSCR）中相关数据会删除
        //这里传的参数是messageName，即message标签的name属性（在整个流程引擎里唯一，否则会部署失败），传错的话不会触发；和signal不同的是message针对某个执行节点的
        runtimeService.messageEventReceived("myMessageName", execution.getId());
        /** messageCatchEvent end **/


        /** timerCatchEvent begin **/
        //流程到了定时捕获事件，10s后结束节点，会在定时任务表（ACT_RU_TIMER_JOB）中产生数据，HANDLER_TYPE_=trigger_timer
        //获取catchEvent执行节点
        execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).onlyChildExecutions().singleResult();
        logger.info("当前执行节点为 {}", execution);

        //获取定时任务
        Job job = activitiRule.getManagementService().createTimerJobQuery().processInstanceId(processInstance.getId()).executionId(execution.getId()).singleResult();

        //如果删除了定时任务 流程仍然卡在中间事件
//        activitiRule.getManagementService().deleteTimerJob(job.getId());

        //如果流程被中断，则任务会被放到act_ru_suspended_job表，恢复任务则任务也会恢复；如果任务执行出错次数达到重试次数则会被放到act_ru_deadletter_job表
//        runtimeService.suspendProcessInstanceById(processInstance.getId());

        //等10秒让任务执行
        Thread.sleep(10000);
        /** timerCatchEvent end **/

        //流程到了commonTask
        List<Execution> list = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).list();
        logger.info("执行流数量：{}", list.size());
    }

}
