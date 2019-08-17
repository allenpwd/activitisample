package pwd.allen.controller;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pwd.allen.entity.PersonInfo;

/**
 * @author 门那粒沙
 * @create 2019-08-17 18:56
 **/
@RequestMapping("my")
@Controller
public class MyController {

    private static final Logger logger = LoggerFactory.getLogger(MyController.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @GetMapping("person/{id}")
    @ResponseBody
    public Object pathVariable(@PathVariable String id) {
        PersonInfo personInfo = new PersonInfo();
        personInfo.setName("哦买噶");
        return personInfo;
    }

    @RequestMapping("startProcess")
    @ResponseBody
    public Object startProcess() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("springbootProcess");
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        logger.info("当前流程节点：{}", task);

        return task.getId();
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
