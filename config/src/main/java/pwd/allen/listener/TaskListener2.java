package pwd.allen.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 门那粒沙
 * @create 2019-05-24 23:55
 **/
public class TaskListener2 implements TaskListener {
    private static final Logger logger = LoggerFactory.getLogger(TaskListener2.class);

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

    @Override
    public void notify(DelegateTask delegateTask) {
        //在task创建时，随机配一个处理人
        delegateTask.setAssignee("assignee:" + delegateTask.getTaskDefinitionKey() + ":" + ATOMIC_INTEGER.getAndAdd(1));
    }
}
