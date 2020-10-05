package pwd.allen.listener;

import com.google.common.collect.Lists;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 门那粒沙
 * @create 2019-05-24 23:55
 **/
public class TaskListener1 implements TaskListener {
    private static final Logger logger = LoggerFactory.getLogger(TaskListener1.class);
    @Override
    public void notify(DelegateTask delegateTask) {
        logger.info("config by listener");

        delegateTask.addCandidateUsers(Lists.newArrayList("user1", "user2"));
        delegateTask.addCandidateGroup("group1");
        delegateTask.setVariable("key1", "value1");
        delegateTask.setDueDate(DateTime.now().plusDays(3).toDate());
    }
}
