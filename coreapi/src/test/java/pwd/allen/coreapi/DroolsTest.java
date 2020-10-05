package pwd.allen.coreapi;

import groovy.util.logging.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Rule;
import org.junit.Test;
import pwd.allen.delegate.MyJavaBean;
import pwd.allen.util.DroolsUtil;

import java.util.HashMap;

/**
 * @author 门那粒沙
 * @create 2019-08-11 21:48
 **/
@Slf4j
public class DroolsTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-rule.cfg.xml");

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
     * 需引用drools-compiler和knowledge-api（用来兼容activiti对drools的调用，缺失的话acitivit执行drools任务时会报classNotFound错误）
     * 需配置规则文件的部署实现类，否则执行规则任务会报错deployment XXX doesn't contain any rules;配置格式
     *     <property name="customPostDeployers">
     *       <list>
     *         <bean class="org.activiti.engine.impl.rules.RulesDeployer"/>
     *       </list>
     *     </property>
     *
     * 结果：userTask执行后自动继续执行businessRuleTask然后再继续自动执行serviceTask
     */
    @Test
    @Deployment(resources = {"drools/test.drl", "bpmn/droolsTask.bpmn20.xml"})
    public void test() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();


        //根据流程定义key启动流程，默认使用最新版本，常用
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");

        Task task = activitiRule.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        //设置流程参数bean，完成任务后会走到businessRuleTask，以bean为规则事实进行drools规则匹配，结果以result作为参数名放到流程变量中
        HashMap<String, Object> varMap = new HashMap<>();
        varMap.put("bean", new MyJavaBean());
        activitiRule.getTaskService().complete(task.getId(), varMap);
        System.out.println("到这里已经流程执行完毕了");
    }
}
