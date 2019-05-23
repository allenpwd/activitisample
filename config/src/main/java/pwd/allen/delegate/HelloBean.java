package pwd.allen.delegate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author 门那粒沙
 * @create 2019-04-20 21:36
 **/
@Service
public class HelloBean {
    private static final Logger logger = LoggerFactory.getLogger(HelloBean.class);
    public void sayHello() {
        logger.info("{}：say hello", this.getClass().getSimpleName());
    }
}
