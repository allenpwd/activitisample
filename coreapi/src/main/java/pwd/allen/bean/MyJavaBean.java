package pwd.allen.bean;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 门那粒沙
 * @create 2019-06-02 20:39
 **/
public class MyJavaBean implements Serializable {
    
    private static final Logger logger = LoggerFactory.getLogger(MyJavaBean.class);

    private String value;

    public String getValue() {
        logger.info("run method getValue {}", value);
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void doSomething() {
        logger.info("call method doSomething of MyJavaBean {}", this);
    }

    public List<String> getCandidateUsers() {
        ArrayList<String> list = new ArrayList<>();
        list.add("user1");
        list.add("user2");
        return list;
    }

    public String getCandidateGroup() {
        return "group1";
    }

    public void print(Execution exec, String str) {
        logger.info("【MyJavaBean】execution=" + exec + "，str=" + str);
    }

    public void setTask(DelegateTask task) {
        logger.info("【MyJavaBean】task=" + task);
    }
}