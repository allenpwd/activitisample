package pwd.allen.coreapi;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Rule;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pwd.allen.DroolsUtil;
import pwd.allen.bean.MyJavaBean;

import java.util.HashMap;

/**
 * @author 门那粒沙
 * @create 2019-08-11 21:48
 **/
public class DroolsTest {

    private static final Logger logger = LoggerFactory.getLogger(EventTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

    /**
     * 测试直接调用drools
     * @throws Exception
     */
    @Test
    public void testDrools() throws Exception {
        StatefulKnowledgeSession kSession = null;

        /** 新版 7.24.0.Final 的 操作代码 **/
//        KieServices kieServices = KieServices.Factory.get();
//        KieFileSystem kfs = kieServices.newKieFileSystem();
//        kfs.write( "src/main/resources/simple.drl", kieServices.getResources().newClassPathResource("drools/test.drl"));
//        KieBuilder kieBuilder = kieServices.newKieBuilder( kfs ).buildAll();
//        Results results = kieBuilder.getResults();
//        if( results.hasMessages( Message.Level.ERROR ) ){
//            System.out.println( results.getMessages() );
//            throw new IllegalStateException( "### errors ###" );
//        }
//        KieContainer kieContainer = kieServices.newKieContainer( kieServices.getRepository().getDefaultReleaseId() );
//        KieBase kieBase = kieContainer.getKieBase();
//        kSession = kieContainer.newKieSession();

        DroolsUtil droolsUtil = new DroolsUtil();
        droolsUtil.init();
        droolsUtil.addRules("drools/test.drl");
        kSession = droolsUtil.getKSession();

        MyJavaBean myJavaBean = new MyJavaBean();

        kSession.insert(myJavaBean);
        kSession.fireAllRules();

        droolsUtil.closeSession();
    }

    /**
     * 测试调用drools规则task
     */
    @Test
    @Deployment(resources = {"drools/test.drl", "bpmn/droolsTask.bpmn20.xml"})
    public void test() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();


        //根据流程定义key启动流程，默认使用最新版本，常用
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");

        Task task = activitiRule.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        HashMap<String, Object> varMap = new HashMap<>();
        varMap.put("bean", new MyJavaBean());
        activitiRule.getTaskService().complete(task.getId(), varMap);
    }
}
