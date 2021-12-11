package pwd.allen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pwd.allen.delegate.MyJavaBean;

/**
 * @author lenovo
 * @create 2020-10-05 17:03
 **/
@Configuration
@ComponentScan(basePackages = "org.activiti.rest.editor.main")
public class MyConfig {

    @Bean
    public MyJavaBean myJavaBean() {
        return new MyJavaBean();
    }
}
