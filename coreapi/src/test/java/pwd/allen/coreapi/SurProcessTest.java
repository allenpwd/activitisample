package pwd.allen.coreapi;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pwd.allen.delegate.MyJavaBean;

import java.util.HashMap;
import java.util.List;

/**
 * @author 门那粒沙
 * @create 2019-04-21 16:37
 **/
public class SurProcessTest {

    private static final Logger logger = LoggerFactory.getLogger(SurProcessTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

    /**
     *  测试调用子流程
     */
    @Test
    @Deployment(resources = {"bpmn/surProcess/callActivity.bpmn20.xml", "bpmn/my-process.bpmn20.xml"})
    public void testCallActivity() throws InterruptedException {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        //根据流程定义key启动流程，默认使用最新版本，常用
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-callActivity-process");

        Task task = activitiRule.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        HashMap<String, Object> parMap = new HashMap<>();
        MyJavaBean myJavaBean = new MyJavaBean();
        myJavaBean.setValue("测试通过sourceExpression属性指定juel表达式来给子流程传参");
        parMap.put("myJavaBean", myJavaBean);

        //任务执行后 会调用my-process子流程，启动一个my-process流程实例，子流程实例通过SUPER_PROCESS_INSTANCE_ID_(ACT_HI_PROCINST)关联父流程实例
        // 子流程的执行流通过SUPER_EXEC_(ACT_RU_EXECUTION)关联父流程的执行流ID
        //可以通过拓展属性 activiti:in activiti:out与子流程做参数交互
        activitiRule.getTaskService().complete(task.getId(), parMap);

        //查询子流程实例
        ProcessInstance surProcessInstance = runtimeService.createProcessInstanceQuery().superProcessInstanceId(processInstance.getId()).singleResult();
        logger.info("surProcessInstance={}", surProcessInstance);

        //根据子流程实例ID查询流程实例
        ProcessInstance processInstance1 = runtimeService.createProcessInstanceQuery().subProcessInstanceId(surProcessInstance.getId()).singleResult();

        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).list();
        logger.info("executions.size={}", executions.size());

    }

    /**
     *  测试调用特别子流程
     */
    @Test
    @Deployment(resources = {"bpmn/surProcess/adHocSubProcess.bpmn20.xml"})
    public void adHocSubProcess() throws InterruptedException {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        //根据流程定义key启动流程，默认使用最新版本，常用
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");

        logger.info("执行流数量 {}", runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).count());

        //获取特别子流程的执行节点
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).onlyChildExecutions().singleResult();

        //先让执行流到达第二个任务
        runtimeService.executeActivityInAdhocSubProcess(execution.getId(), "task2");
        logger.info("执行流数量 {}", runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).count());

        //让执行流到达第一个任务，如果adHocSubProcess的ordering属性为Sequential则一次只能激活一个activityId，否则会报错：Sequential ad-hoc sub process already has an active execution
        runtimeService.executeActivityInAdhocSubProcess(execution.getId(), "task1");
        logger.info("执行流数量 {}", runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).count());

        //完成第二个任务
        Task task = activitiRule.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        activitiRule.getTaskService().complete(task.getId());

        //完成特别子流程
        //需保证特别子流程的子节点都执行完，否则会报错：Ad-hoc sub process has running child executions that need to be completed first
        runtimeService.completeAdhocSubProcess(execution.getId());
        logger.info("执行流数量 {}", runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).count());
    }
}
