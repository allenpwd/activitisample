package pwd.allen.bean;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author 门那粒沙
 * @create 2019-06-02 20:39
 **/
public class MyJavaDelegate implements JavaDelegate, Serializable {
    
    private static final Logger logger = LoggerFactory.getLogger(MyJavaDelegate.class);

    private Expression name;
    private Expression desc;

    private String value;

    @Override
    public void execute(DelegateExecution execution) {
        if (name != null) {
            Object value = name.getValue(execution);
            logger.info("name = {}", value);
        }
        if (desc != null) {
            Object value = desc.getValue(execution);
            logger.info("desc = {}", value);
        }
        logger.info("run my java delegate {}", this);

    }

    public String getValue() {
        logger.info("run method getValue {}", value);
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void doSomething() {
        logger.info("call method doSomething of MyJavaDelegate {}", this);
    }
}
