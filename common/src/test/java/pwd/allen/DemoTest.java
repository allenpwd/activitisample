package pwd.allen;

import com.google.common.collect.Maps;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * DemoMain的测试
 *
 * @author lenovo
 * @create 2020-10-04 11:28
 **/
public class DemoTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoTest.class);

    private ProcessEngine processEngine;

    @Before
    public void init() {
        //创建流程引擎
        processEngine = getProcessEngine();
    }

    @After
    public void close() {
        processEngine.close();
    }

    @Test
    public void start() throws ParseException {
        LOGGER.info("启动程序");

        //部署流程定义文件
        ProcessDefinition processDefinition = getProcessDefinition(processEngine);

        //启动运行流程
        ProcessInstance processInstance = getProcessInstance(processEngine, processDefinition);

        //处理流程任务
        Scanner scanner = new Scanner(System.in);
        while (processInstance != null && !processInstance.isEnded()) {

            TaskService taskService = processEngine.getTaskService();
            List<Task> list = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
            LOGGER.info("待处理任务数量 [{}]", list.size());
            for (Task task : list) {
                LOGGER.info("待处理任务 {}", task.toString());
                FormService formService = processEngine.getFormService();
                TaskFormData taskFormData = formService.getTaskFormData(task.getId());
                List<FormProperty> formProperties = taskFormData.getFormProperties();
                Map<String, Object> variables = Maps.newHashMap();
                for (FormProperty formProperty : formProperties) {
                    Object val = null;
                    if (StringFormType.class.isInstance(formProperty.getType())) {
                        LOGGER.info("请输入 {} ?", formProperty.getName());
                        val = scanner.nextLine();
                    } else if (DateFormType.class.isInstance(formProperty.getType())) {
                        LOGGER.info("请输入 {} ? 格式yyyy-MM-dd", formProperty.getName());
                        String line = scanner.nextLine();
                        val = new SimpleDateFormat("yyyy-MM-dd").parse(line);
                    } else {
                        LOGGER.info("类型暂不支持{}", formProperty.getName());
                    }
                    variables.put(formProperty.getId(), val);
                    LOGGER.info("您输入的内容是【{}】", val);

                }
                task.setDescription("添加一下内容");
                taskService.saveTask(task);
                taskService.addComment(task.getId(), task.getProcessInstanceId(), task.getName());
                taskService.complete(task.getId(), variables);
                processInstance = processEngine.getRuntimeService()
                        .createProcessInstanceQuery()
                        .processDefinitionId(processDefinition.getId())
                        .singleResult();
            }
        }

        LOGGER.info("结束程序");
    }

    /**
     * 查询 执行历史的审批记录
     */
    @Test
    public void query() {
        HistoricProcessInstance processInstance = processEngine.getHistoryService().createHistoricProcessInstanceQuery()
                .processDefinitionKey("second_approve")
                .orderByProcessInstanceStartTime().desc()
                .listPage(0, 1).get(0);

        List<HistoricTaskInstance> taskInstances = processEngine.getHistoryService().createHistoricTaskInstanceQuery()
                .processInstanceId(processInstance.getId()).list();

        for (HistoricTaskInstance taskInstance : taskInstances) {
            List<HistoricVariableInstance> variableInstances = processEngine.getHistoryService().createHistoricVariableInstanceQuery()
                    .executionId(taskInstance.getExecutionId()).list();
            System.out.println(String.format("流程名：%s；流程变量：%s；备注：%s"
                    , taskInstance.getName()
                    , variableInstances.toString(), processEngine.getTaskService().getTaskComments(taskInstance.getId())));

        }
    }

    /**
     * 查询并导出 发布时自动生成的流程图
     * 若流程图乱码，则可以在配置文件中指定字体为宋体
     * @throws IOException
     */
    @Test
    public void img() throws IOException {
        HistoricProcessInstance processInstance = processEngine.getHistoryService().createHistoricProcessInstanceQuery()
                .processDefinitionKey("second_approve")
                .orderByProcessInstanceStartTime().desc()
                .listPage(0, 1).get(0);

        List<String> deploymentResourceNames = processEngine.getRepositoryService().getDeploymentResourceNames(processInstance.getDeploymentId());
        for (String deploymentResourceName : deploymentResourceNames) {
            if (deploymentResourceName.endsWith("png")) {
                InputStream inputStream = processEngine.getRepositoryService().getResourceAsStream(processInstance.getDeploymentId(), deploymentResourceName);
                FileCopyUtils.copy(inputStream, new FileOutputStream(System.getProperty("user.home") + "\\Desktop\\test.png"));
            }
        }
    }

    /**
     * 创建流程引擎
     * @return
     */
    private ProcessEngine getProcessEngine() {
        //<editor-fold desc="创建流程引擎配置对象">
        //方式一：约定的方式
//        ProcessEngineConfiguration configuration = ProcessEngineConfiguration
//                .createProcessEngineConfigurationFromResourceDefault();
        //以上代码相当于调用
//        BeansConfigurationHelper.parseProcessEngineConfigurationFromResource("activiti.cfg.xml", "processEngineConfiguration");

        //方式二：直接指定流程引擎配置对象的xml配置文件
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti-mysql.cfg.xml");

        //方式三：采用默认的流程引擎配置
        //默认属性有：databaseSchemaUpdate=create-drop jdbcUrl=jdbc:h2:mem:activiti
        /*ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createStandaloneInMemProcessEngineConfiguration();*/

        //配置对象的属性可以在xml里指定，也可以直接调用对象的set方法进行设置
        LOGGER.info("configuration {}", configuration);
        //</editor-fold>

        //配置对象根据配置构建流程引擎
        processEngine = configuration.buildProcessEngine();
        LOGGER.info("流程引擎名称【{}】，版本【{}】", processEngine.getName(), ProcessEngine.VERSION);
        return processEngine;
    }

    /**
     * 发布流程，并返回流程定义
     * @param processEngine
     * @return
     */
    private ProcessDefinition getProcessDefinition(ProcessEngine processEngine) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                .addClasspathResource("bpmn/second_approve.bpmn").name("demo");
        Deployment deploy = deploymentBuilder.deploy();
        String deployId = deploy.getId();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployId).singleResult();
        LOGGER.info("流程定义文件【{}】，流程ID【{}】", processDefinition.getName(), processDefinition.getId());
        return processDefinition;
    }

    /**
     * 启动流程，返回流程实例
     * @param processEngine
     * @param processDefinition
     * @return
     */
    private ProcessInstance getProcessInstance(ProcessEngine processEngine, ProcessDefinition processDefinition) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        LOGGER.info("启动流程【{}】", processInstance.getProcessDefinitionKey());
        return processInstance;
    }
}
