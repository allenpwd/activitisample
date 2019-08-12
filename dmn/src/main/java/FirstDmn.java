import org.activiti.dmn.api.*;
import org.activiti.dmn.engine.DmnEngine;
import org.activiti.dmn.engine.DmnEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @author 门那粒沙
 * @create 2019-08-12 22:21
 *
 * 遇到的问题
 * mysql用了高版本 在已有表的基础上更新表schame会报错：liquibase.exception.DatabaseException: liquibase.exception.DatabaseException: java.sql.SQLException: Column name pattern can not be NULL or empty.
 * 处理方式：换成低版本
 **/
public class FirstDmn {

    private static final Logger logger = LoggerFactory.getLogger(FirstDmn.class);

    public static void main(String[] args) {
        //根据默认配置创建引擎的配置实例
        DmnEngineConfiguration configuration = DmnEngineConfiguration.createDmnEngineConfigurationFromResourceDefault();

        //创建规则引擎
        DmnEngine engine = configuration.buildDmnEngine();

        //获取规则存储服务组件
        DmnRepositoryService repositoryService = engine.getDmnRepositoryService();

        //获取规则服务组件
        DmnRuleService ruleService = engine.getDmnRuleService();

        //进行规则部署
        DmnDeployment deploy = repositoryService.createDeployment().addClasspathResource("dmn/rule.dmn").deploy();

        //查询决策表数据
        DmnDecisionTable dmnDecisionTable = repositoryService.createDecisionTableQuery().deploymentId(deploy.getId()).singleResult();

        //初始化
        HashMap<String, Object> params = new HashMap<>();
        params.put("personAge", 19);
        params.put("personName", "Angus");

        //传入参数执行决策，并返回结果
        RuleEngineExecutionResult executionResult = ruleService.executeDecisionByKey(dmnDecisionTable.getKey(), params);

        logger.info("myResult = {}", executionResult.getResultVariables().get("myResult"));

        //再次决策
        params.put("personAge", 18);
        params.put("personName", "Angus");
        executionResult = ruleService.executeDecisionByKey(dmnDecisionTable.getKey(), params);
        logger.info("myResult = {}", executionResult.getResultVariables().get("myResult"));
    }
}
