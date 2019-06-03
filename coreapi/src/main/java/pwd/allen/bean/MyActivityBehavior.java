package pwd.allen.bean;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.delegate.ActivityBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 门那粒沙
 * @create 2019-06-02 20:40
 **/
public class MyActivityBehavior implements ActivityBehavior {
    private static final Logger logger = LoggerFactory.getLogger(MyActivityBehavior.class);
    @Override
    public void execute(DelegateExecution execution) {
        logger.info("run my activity behavior {}", this);

    }
}
