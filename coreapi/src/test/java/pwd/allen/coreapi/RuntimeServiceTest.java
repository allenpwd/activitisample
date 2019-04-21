package pwd.allen.coreapi;

import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author 门那粒沙
 * @create 2019-04-21 16:37
 **/
public class RuntimeServiceTest {
    @Rule
    ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = "pwd/allen/my-process.bpmn20.xml")
    public void test() {

    }
}
