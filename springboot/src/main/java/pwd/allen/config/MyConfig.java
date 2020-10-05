package pwd.allen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pwd.allen.delegate.MyJavaBean;

/**
 * @author lenovo
 * @create 2020-10-05 17:03
 **/
@Configuration
public class MyConfig {

    @Bean
    public MyJavaBean myJavaBean() {
        return new MyJavaBean();
    }
}
