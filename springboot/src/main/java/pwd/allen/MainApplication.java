package pwd.allen;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author 门那粒沙
 * @create 2019-08-17 18:59
 **/
//org.activiti.spring.boot.SecurityAutoConfiguration有注解@AutoConfigureBefore({org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class})
//因为没有引入security相关jar，所以要排除该自动配置，否则会报错
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
