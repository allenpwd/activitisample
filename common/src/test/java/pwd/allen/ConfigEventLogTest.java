package pwd.allen;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.event.EventLogEntry;
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
 * 测试事件日志，数据表为：act_evt_log
 * @author pwd
 * @create 2019-04-07 18:23
 **/
@Slf4j
public class ConfigEventLogTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti_eventlog.cfg.xml");

    /**
     * 完成一条流程，并打印事件日志
     */
    @Test
    @Deployment(resources = {"bpmn/my-process.bpmn20.xml"})
    public void test() {

        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
        Task task = activitiRule.getTaskService().createTaskQuery().list().get(0);
        activitiRule.getTaskService().complete(task.getId());

        List<EventLogEntry> eventLogEntries = activitiRule.getManagementService()
                .getEventLogEntriesByProcessInstanceId(processInstance.getProcessInstanceId());
        for (EventLogEntry eventLogEntry : eventLogEntries) {
            log.info("eventLog.type = {}, eventLog.data = {}", eventLogEntry.getType(), new String(eventLogEntry.getData()));
        }
        log.info("eventLogEntries.size = {}", eventLogEntries.size());
    }
}
