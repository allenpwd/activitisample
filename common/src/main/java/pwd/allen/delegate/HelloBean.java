package pwd.allen.delegate;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author 门那粒沙
 * @create 2019-04-20 21:36
 **/
@Service
@Slf4j
public class HelloBean {
    public void sayHello(String type) {
        log.info("------------------{}：say hello：{}", this.getClass().getSimpleName(), type);

        // 如果在执行service时抛出异常，会：org.activiti.engine.ActivitiException: Could not execute service task expression
        if ("error".equals(type)) {
            throw new RuntimeException("我是个错误");
        }
    }
}
