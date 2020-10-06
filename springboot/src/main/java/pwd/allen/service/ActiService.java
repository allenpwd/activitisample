package pwd.allen.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import groovy.util.logging.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pwd.allen.entity.UserTaskNew;

import java.util.List;
import java.util.Map;

/**
 * @author lenovo
 * @create 2020-10-05 20:50
 **/
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
@Service
public class ActiService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    public List<Task> start(String processDefinitionKey, Map<String, Object> paramMap) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, paramMap);
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        return list;
    }

    public List<Task> getTask(String processInstanceId, String candidateGroup) {
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId)
                .taskCandidateGroup(candidateGroup).list();
        return list;
    }
}
