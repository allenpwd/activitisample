package pwd.allen.interceptor;

import org.activiti.engine.impl.interceptor.AbstractCommandInterceptor;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义命令拦截器
 * @author 门那粒沙
 * @create 2019-05-23 10:56
 **/
public class MyInterceptor extends AbstractCommandInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(MyInterceptor.class);

    @Override
    public <T> T execute(CommandConfig config, Command<T> command) {
        //输出字符串和命令
        logger.info("【myInterceptor】 {}", command.getClass().getName());
        //让责任链中的下一个请求处理命令
        return next.execute(config, command);
    }

}
