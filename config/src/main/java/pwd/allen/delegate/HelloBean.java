package pwd.allen.delegate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 门那粒沙
 * @create 2019-04-20 21:36
 **/
public class HelloBean {
    private static final Logger logger = LoggerFactory.getLogger(HelloBean.class);
    public void sayHello() {
        logger.info("{}：say hello", this.getClass().getSimpleName());
    }
}
