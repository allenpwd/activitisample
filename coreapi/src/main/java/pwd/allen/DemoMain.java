package pwd.allen;

import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author pwd
 * @create 2019-04-05 23:01
 **/
public class DemoMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoMain.class);

    public static void main(String[] args) throws ParseException {
        LOGGER.info("启动程序");
        //创建流程引擎
        ProcessEngine processEngine = getProcessEngine();

        //部署流程定义文件
        ProcessDefinition processDefinition = getProcessDefinition(processEngine);

        //启动运行流程
        ProcessInstance processInstance = getProcessInstance(processEngine, processDefinition);

        //处理流程任务
        Scanner scanner = new Scanner(System.in);
        while (processInstance != null && !processInstance.isEnded()) {

            TaskService taskService = processEngine.getTaskService();
            List<Task> list = taskService.createTaskQuery().list();
            LOGGER.info("待处理任务数量 [{}]", list.size());
            for (Task task : list) {
                LOGGER.info("待处理任务 {}", task.toString());
                FormService formService = processEngine.getFormService();
                TaskFormData taskFormData = formService.getTaskFormData(task.getId());
                List<FormProperty> formProperties = taskFormData.getFormProperties();
                Map<String, Object> variables = new HashMap<>();
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
                        LOGGER.info("类型暂不支持 {}", formProperty.getName());
                    }
                    variables.put(formProperty.getId(), val);
                    LOGGER.info("您输入的内容是 【{}】", val);

                }
                taskService.complete(task.getId(), variables);
                processInstance = processEngine.getRuntimeService()
                        .createProcessInstanceQuery()
                        .processDefinitionId(processDefinition.getId())
                        .singleResult();
            }
        }

        LOGGER.info("结束程序");
    }

    private static ProcessInstance getProcessInstance(ProcessEngine processEngine, ProcessDefinition processDefinition) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        LOGGER.info("启动流程 {}", processInstance.getProcessDefinitionKey());
        return processInstance;
    }

    private static ProcessDefinition getProcessDefinition(ProcessEngine processEngine) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder deployment = repositoryService.createDeployment();
        deployment.addClasspathResource("second_approve.bpmn20.xml");
        Deployment deploy = deployment.deploy();
        String deployId = deploy.getId();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployId).singleResult();
        LOGGER.info("流程定义文件{}，流程ID{}", processDefinition.getName(), processDefinition.getId());
        return processDefinition;
    }

    private static ProcessEngine getProcessEngine() {
        ProcessEngineConfiguration cfg = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
        ProcessEngine processEngine = cfg.buildProcessEngine();
        String name = processEngine.getName();
        String version = processEngine.VERSION;
        LOGGER.info("流程引擎名称{}，版本{}", name, version);
        return processEngine;
    }
}
