package pwd.allen;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

/**
 * DemoMain的测试
 *
 * @author lenovo
 * @create 2020-10-04 11:28
 **/
public class DemoTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg.xml");

    /**
     * 查询 执行历史的审批记录
     */
    @Test
    public void test() {
        HistoricProcessInstance processInstance = activitiRule.getHistoryService().createHistoricProcessInstanceQuery()
                .processDefinitionKey("second_approve")
                .orderByProcessInstanceStartTime().desc()
                .listPage(0, 1).get(0);

        List<HistoricTaskInstance> taskInstances = activitiRule.getHistoryService().createHistoricTaskInstanceQuery()
                .processInstanceId(processInstance.getId()).list();

        for (HistoricTaskInstance taskInstance : taskInstances) {
            List<HistoricVariableInstance> variableInstances = activitiRule.getHistoryService().createHistoricVariableInstanceQuery()
                    .executionId(taskInstance.getExecutionId()).list();
            System.out.println(String.format("流程名：%s；流程变量：%s；备注：%s"
                    , taskInstance.getName()
                    , variableInstances.toString(), activitiRule.getTaskService().getTaskComments(taskInstance.getId())));

        }
    }
}
