package pwd.allen.coreapi;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 门那粒沙
 * @create 2019-08-09 22:55
 **/
public class SendTaskTest {

    private static final Logger logger = LoggerFactory.getLogger(UserTaskTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg.xml");

    /**
     * 测试sendTask发送邮件
     *
     * 注：1.fromEmail（发件人）和tran.connect（邮件发送对象）要保持一致
     * 2.mailServerPassword配置的不是邮箱密码，而是授权码，第三方客户端登录用的，需要在邮箱设置开启
     */
    @Test
    @Deployment(resources = {"bpmn/sendTask.email.bpmn20.xml"})
    public void sendTaskEmail() {
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");

    }
}
