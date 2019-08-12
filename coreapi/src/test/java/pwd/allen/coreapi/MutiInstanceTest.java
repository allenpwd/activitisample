package pwd.allen.coreapi;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pwd.allen.bean.MyJavaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author 门那粒沙
 * @create 2019-08-11 14:33
 **/
public class MutiInstanceTest {

    private static final Logger logger = LoggerFactory.getLogger(MutiInstanceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

    /**
     * 多实例
     * 循环的内置参数
     * 			1）nrOfInstances：实例总数，为loopCardinality指定的值或者指定的集合的大小
     * 			2）nrActiveInstances：当前正在执行的实例数，若isSequential=true则该值总是为1，否则执行完一个实例就减1
     * 			3）nrOfCompletedInstances：已经完成的实例数
     * 			4）loopCounter：当前循环的索引（作用域只限于对应循环的实例执行流中）
     */
    @Test
    @Deployment(resources = "bpmn/mutiInstance.bpmn20.xml")
    public void testIntermediateCatchEvent() throws InterruptedException {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        HashMap<String, Object> varMap = new HashMap<>();
        varMap.put("myJavaBean", new MyJavaBean());

        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        varMap.put("loopDataInput", list);

        //根据流程定义key启动流程，默认使用最新版本，常用
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process", varMap);

        logger.info("test");
    }
}
