package pwd.allen.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.impl.interceptor.AbstractCommandInterceptor;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义命令拦截器
 *
 *
 * @author 门那粒沙
 * @create 2019-05-23 10:56
 **/
@Slf4j
public class MyInterceptor extends AbstractCommandInterceptor {

    @Override
    public <T> T execute(CommandConfig config, Command<T> command) {
        //输出字符串和命令
        log.info("【myInterceptor】 {}", command.getClass().getName());
        //让责任链中的下一个请求处理命令
        return next.execute(config, command);
    }

}
