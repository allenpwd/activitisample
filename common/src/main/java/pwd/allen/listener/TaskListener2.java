package pwd.allen.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 在task创建时做一些自定义操作
 * @author 门那粒沙
 * @create 2019-05-24 23:55
 **/
public class TaskListener2 implements TaskListener {
    private static final Logger logger = LoggerFactory.getLogger(TaskListener2.class);

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

    @Override
    public void notify(DelegateTask delegateTask) {
        String assignee = delegateTask.getVariable("assignee", String.class);
        if (StringUtils.isEmpty(assignee)) {
            //没有指定assignee，则随机配一个处理人
            assignee = "assignee:" + delegateTask.getTaskDefinitionKey() + ":" + ATOMIC_INTEGER.getAndAdd(1);
        }
        delegateTask.setAssignee(assignee);
    }
}
