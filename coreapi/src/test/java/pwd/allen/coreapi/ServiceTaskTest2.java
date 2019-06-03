package pwd.allen.coreapi;

import com.google.common.collect.Maps;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.ManagementService;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pwd.allen.bean.MyJavaDelegate;

import java.util.HashMap;
import java.util.List;

/**
 * 服务任务
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:activiti-context.xml")
public class ServiceTaskTest2 {

	private static final Logger logger = LoggerFactory.getLogger(ServiceTaskTest2.class);

	@Rule
	@Autowired
	public ActivitiRule activitiRule;

	@Autowired
	private MyJavaDelegate bean;


	/**
	 * 执行一个JavaDelegate对象表达式，表达式为spring容器中注册的beanId
	 * 这样有个好处就是如果bean是单例就不会每次到达该节点都去创建一个实例
	 */
	@Test
	@Deployment(resources = {"bpmn/my-service03.bpmn20.xml"})
	public void testJavaDelegateExpression() {
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");

		//启动后会走到serviceTask activiti:delegateExpression="${myJavaDelegate}"
		//会先从流程变量中找，没有则会使用spring容器中id为myJavaDelegate的bean
		//如果还找到会报错：Unknown property used in expression: ${myJavaDelegate}

	}

	/**
	 * 执行一个JavaDelegate对象表达式，会优先从流程变量中找
	 */
	@Test
	@Deployment(resources = {"bpmn/my-service03.bpmn20.xml"})
	public void testJavaDelegateExpression2() {
		MyJavaDelegate myJavaDelegate = new MyJavaDelegate();
		logger.info("JavaDelegate = {}", myJavaDelegate);
		logger.info("JavaDelegate from springContext = {}", bean);


		HashMap<String, Object> variables = Maps.newHashMap();
		variables.put("myJavaDelegate", myJavaDelegate);
		//作为流程变量的参数一定要可序列化，否则会报错 couldn't find a variable type that is able to serialize pwd.allen.bean.MyJavaDelegate@3bcd426c
		ProcessInstance processInstance = activitiRule.getRuntimeService()
				.startProcessInstanceByKey("my-process", variables);

		//启动后会走到serviceTask activiti:delegateExpression="${myJavaDelegate}"
		//会使用spring容器中id为myJavaDelegate的bean
	}

	/**
	 * //执行调用方法表达式和值表达式
	 */
	@Test
	@Deployment(resources = {"bpmn/my-service04.bpmn20.xml"})
	public void testJavaDelegateExpression3() {
		MyJavaDelegate myJavaDelegate = new MyJavaDelegate();
		myJavaDelegate.setValue("pwd");
		logger.info("JavaDelegate = {}", myJavaDelegate);

		HashMap<String, Object> variables = Maps.newHashMap();
		variables.put("myJavaDelegate", myJavaDelegate);
		ProcessInstance processInstance = activitiRule.getRuntimeService()
				.startProcessInstanceByKey("my-process", variables);

		//启动后会走到serviceTask activiti:delegateExpression="${myJavaDelegate}"
		//会使用spring容器中id为myJavaDelegate的bean
	}
}
