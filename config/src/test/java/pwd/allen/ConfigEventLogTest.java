package pwd.allen;

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
 * @author pwd
 * 测试MDC日志打印，activiti_mdc.cfg.xml配置了MDCCommandInvoker拦截器做MDC启动和变量赋值
 * @create 2019-04-07 18:23
 **/
public class ConfigEventLogTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigEventLogTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti_eventlog.cfg.xml");

    @Test
    @Deployment(resources = {"pwd/allen/my-process.bpmn20.xml"})
    public void test() {

        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
        Task task = activitiRule.getTaskService().createTaskQuery().list().get(0);
        activitiRule.getTaskService().complete(task.getId());

        List<EventLogEntry> eventLogEntries = activitiRule.getManagementService()
                .getEventLogEntriesByProcessInstanceId(processInstance.getProcessInstanceId());
        for (EventLogEntry eventLogEntry : eventLogEntries) {
            LOGGER.info("eventLog.type = {}, eventLog.data = {}", eventLogEntry.getType(), new String(eventLogEntry.getData()));
        }
        LOGGER.info("eventLogEntries.size = {}", eventLogEntries.size());
    }
}
