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
            //Expression的getValue方法传入执行对象作为参数的目的是注入的如果是UEL表达式需要操作到流程的参数，如果只是注入字符串则传入null即可。
            Object value = name.getValue(null);
            logger.info("【MyJavaDelegate】name = {}", value);
        }
        if (desc != null) {
            //这里不传execution的话会报NullPointException
            Object value = desc.getValue(execution);
            logger.info("【MyJavaDelegate】desc = {}", value);
        }
        logger.info("【MyJavaDelegate】run my java delegate {}", this);
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
