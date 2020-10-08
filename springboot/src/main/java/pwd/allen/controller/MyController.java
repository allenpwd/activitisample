package pwd.allen.controller;

import groovy.util.logging.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pwd.allen.entity.PersonInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author 门那粒沙
 * @create 2019-08-17 18:56
 **/
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RequestMapping("my")
@Controller
@Slf4j
public class MyController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @RequestMapping("process")
    @ResponseBody
    public Object process(@RequestParam("action")String action
            , @RequestParam(value = "instId", required = false)String instId
            , @RequestParam(value = "taskId", required = false)String taskId
            , @RequestParam(value = "cand", required = false)String cand) {
        try {
            if ("start".equals(action)) {
                HashMap<String, Object> varMap = new HashMap<>();
                varMap.put("personInfo", new PersonInfo());
                ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("springboot-01", varMap);
                return processInstance.getProcessInstanceId();
            } else if ("task".equals(action) && instId != null) {
                TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(instId);
                if (cand != null) {
                    taskQuery.taskCandidateUser(cand);
                }
                List<Task> list = taskQuery.list();
                return list;
            } else if ("complate".equals(action) && taskId != null) {
                taskService.complete(taskId);
                return "执行成功";
            } else if ("history".equals(action) && instId != null) {
                List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(instId).list();
                return list;
            } else if ("repo".equals(action)) {
                List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
        return null;
    }

    @RequestMapping("/complete/{taskId}")
    @ResponseBody
    public Object complete(@PathVariable String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (task != null) {
            taskService.complete(task.getId());

            task = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();

            return "执行成功，当前流程节点：" + task;
        }

        return task;
    }

}
