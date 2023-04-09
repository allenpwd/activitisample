package pwd.allen;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.impl.persistence.entity.HistoricFormPropertyEntity;
import org.activiti.engine.impl.persistence.entity.HistoricFormPropertyEntityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DemoMain的测试
 *
 * @author lenovo
 * @create 2020-10-04 11:28
 **/
@Slf4j
public class DemoTest {

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

    /**
     * 发布流程
     */
    @Test
    public void deploy() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 一个deployment可以发布多个流程定义
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                .name("demo")
                .addInputStream("my-process.bpmn", this.getClass().getResourceAsStream("/bpmn/my-process.bpmn")) // 发布my-process.bpmn，路径前不带/则从当前class所在路径下找
                .addClasspathResource("bpmn/second_approve.bpmn")   // 发布second_approve.bpmn
                ;
        Deployment deploy = deploymentBuilder.deploy();
        log.info("流程发布id={}，name={}", deploy.getId(), deploy.getName());

        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploy.getId()).list();
        for (ProcessDefinition processDefinition : list) {
            log.info("流程定义文件【{}】，流程ID【{}】", processDefinition.getName(), processDefinition.getId());
        }
    }

    /**
     * 启动并根据输入走流程
     * @throws ParseException
     */
    @Test
    public void start() throws ParseException {
        log.info("启动程序");

        //部署流程定义文件
        ProcessDefinition processDefinition = getProcessDefinition();

        //启动运行流程
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceById(processDefinition.getId());
        log.info("启动流程【{}】", processInstance.getProcessDefinitionKey());

        //处理流程任务
        Scanner scanner = new Scanner(System.in);
        while (processInstance != null && !processInstance.isEnded()) {

            TaskService taskService = processEngine.getTaskService();
            List<Task> list = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
            log.info("待处理任务数量 [{}]", list.size());
            for (Task task : list) {
                log.info("待处理任务 {}", task.toString());
                FormService formService = processEngine.getFormService();
                TaskFormData taskFormData = formService.getTaskFormData(task.getId());
                List<FormProperty> formProperties = taskFormData.getFormProperties();
                Map<String, Object> variables = Maps.newHashMap();
                for (FormProperty formProperty : formProperties) {
                    Object val = null;
                    if (StringFormType.class.isInstance(formProperty.getType())) {
                        log.info("请输入 {} ?", formProperty.getName());
                        val = scanner.nextLine();
                    } else if (DateFormType.class.isInstance(formProperty.getType())) {
                        log.info("请输入 {} ? 格式yyyy-MM-dd，默认当前时间", formProperty.getName());
                        String line = scanner.nextLine();
                        val = line;
//                        if (StringUtils.isEmpty(line)) {
//                            val = new Date();
//                        } else {
//                            val = new SimpleDateFormat("yyyy-MM-dd").parse(line);
//                        }
                    } else {
                        log.info("类型暂不支持{}", formProperty.getName());
                    }
                    variables.put(formProperty.getId(), val);
                    log.info("您输入的内容是【{}】", val);

                }
                task.setDescription("添加一下内容");
                taskService.saveTask(task);
                taskService.addComment(task.getId(), task.getProcessInstanceId(), "备注:" + task.getName());
                // 提交方式一，variables默认作为流程全局变量
//                taskService.complete(task.getId(), variables);
                // 提交方式二，作为表单属性
                Map<String, String> variables2 = new HashMap<>();
                variables.forEach((k,v) -> variables2.put(k, v != null ? v.toString() : null));
                processEngine.getFormService().submitTaskFormData(task.getId(), variables2);
                processInstance = processEngine.getRuntimeService()
                        .createProcessInstanceQuery()
                        .processInstanceId(processInstance.getId())
                        .singleResult();
            }
        }

        log.info("结束程序");
    }

    /**
     * 查询 执行历史的审批记录
     */
    @Test
    public void query() {
        // 查询最新的流程实例
        HistoricProcessInstance processInstance = processEngine.getHistoryService().createHistoricProcessInstanceQuery()
                .processDefinitionKey("second_approve")
                .orderByProcessInstanceStartTime().desc()
                .listPage(0, 1).get(0);

        List<HistoricTaskInstance> taskInstances = processEngine.getHistoryService().createHistoricTaskInstanceQuery()
                .processInstanceId(processInstance.getId()).list();

        Map<String, String> taskNameList = new HashMap<>();
        for (HistoricTaskInstance taskInstance : taskInstances) {
            List<Comment> taskComments = processEngine.getTaskService().getTaskComments(taskInstance.getId());
            List<String> listComments = taskComments.stream().map(o -> o.getFullMessage()).collect(Collectors.toList());
            System.out.println(String.format("流程名：%s；备注：%s" , taskInstance.getName() , listComments));
            taskNameList.put(taskInstance.getId(), taskInstance.getName());
        }

        // 打印流程全局变量
        List<HistoricVariableInstance> variableInstances = processEngine.getHistoryService().createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId()).list();
        for (HistoricVariableInstance variableInstance : variableInstances) {
            System.out.println(String.format("流程变量：[%s]%s=%s", variableInstance.getVariableTypeName()
                    , variableInstance.getVariableName(), variableInstance.getValue()));
        }

        // 获取formProperty类型
        List<HistoricDetail> historicDetailList = processEngine.getHistoryService().createHistoricDetailQuery()
                .processInstanceId(processInstance.getId()).formProperties().list();
        for (HistoricDetail historicDetail : historicDetailList) {
            log.info("task【{}】{}}", JSONUtil.toJsonStr(historicDetail));
        }

    }

    /**
     * 查询并导出 发布时自动生成的流程图
     * 若流程图乱码，则可以在配置文件中指定字体为宋体
     * @throws IOException
     */
    @Test
    public void img() throws IOException {
        Deployment deployment = processEngine.getRepositoryService().createDeploymentQuery()
                .orderByDeploymenTime().desc()
                .listPage(0, 1).get(0);

        List<String> deploymentResourceNames = processEngine.getRepositoryService().getDeploymentResourceNames(deployment.getId());
        for (String deploymentResourceName : deploymentResourceNames) {
            if (deploymentResourceName.endsWith("png")) {
                InputStream inputStream = processEngine.getRepositoryService().getResourceAsStream(deployment.getId(), deploymentResourceName);
                String[] split = deploymentResourceName.split("/");
                FileCopyUtils.copy(inputStream, new FileOutputStream(System.getProperty("user.home") + "\\Desktop\\" + split[split.length - 1]));
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
//        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault();
        //以上代码相当于调用
//        BeansConfigurationHelper.parseProcessEngineConfigurationFromResource("activiti.cfg.xml", "processEngineConfiguration");

        //方式二：直接指定流程引擎配置对象的xml配置文件
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti-mysql.cfg.xml");

        //方式三：采用默认的流程引擎配置
        //默认属性有：databaseSchemaUpdate=create-drop jdbcUrl=jdbc:h2:mem:activiti
//        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();

        //配置对象的属性可以在xml里指定，也可以直接调用对象的set方法进行设置
        log.info("configuration {}", configuration);
        //</editor-fold>

        //配置对象根据配置构建流程引擎
        processEngine = configuration.buildProcessEngine();
        log.info("流程引擎名称【{}】，版本【{}】", processEngine.getName(), ProcessEngine.VERSION);
        return processEngine;
    }

    /**
     * 查询最新版本的流程定义
     * @return
     */
    private ProcessDefinition getProcessDefinition() {
        String processDefinitonKey = "second_approve";
        // 如果流程没有发布，返回的是null
        ProcessDefinition processDefinition = processEngine.getRepositoryService().createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitonKey).latestVersion().singleResult();
        log.info("流程定义文件【{}】，流程ID【{}】", processDefinition.getName(), processDefinition.getId());
        return processDefinition;
    }
}
