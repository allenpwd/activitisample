package pwd.allen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import groovy.util.logging.Slf4j;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pwd.allen.entity.PersonInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    private ObjectMapper objectMapper;

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

    /**
     * 模拟activiti explorer的新建模型
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("createModel")
    public String createModel(HttpServletRequest request, HttpServletResponse response
            , @RequestParam(name = "name", defaultValue = "流程demo") String name
            , @RequestParam(name = "description", defaultValue = "这是一个流程demo") String description) {

        String id = null;
        try {
            Model model = repositoryService.newModel();
            model.setName(name);
            model.setKey(name);
            //模型分类 结合自己的业务逻辑
            //model.setCategory(category);

            ObjectNode modelNode = objectMapper.createObjectNode();
            modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
            modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            // 版本号
            modelNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
            model.setMetaInfo(modelNode.toString());

            repositoryService.saveModel(model);
            id = model.getId();

            //完善ModelEditorSource
            ObjectNode editorNode = objectMapper.createObjectNode()
                    .put("id", "canvas")
                    .put("resourceId", "canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode()
                    .put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.set("stencilset", stencilSetNode);
            repositoryService.addModelEditorSource(id, editorNode.toString().getBytes("utf-8"));

            // 转发到模型设计器页面
            response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=" + id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "index";
    }

}
