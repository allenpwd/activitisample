package pwd.allen.coreapi.script;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.junit.Test;
import org.springframework.util.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Date;

/**
 * groovy脚本测试，需要引入groovy相关Jar
 * @author 门那粒沙
 * @create 2019-12-14 16:45
 **/
public class GroovyTest {

    /**
     * 测试GroovyShell
     * 这是性能较低的方式，因为每次都需要创建shell和script，这也意味着每次都需要对expression进行“编译”（JAVA Class）
     */
    @Test
    public void testGroovyShell() {
        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);

        binding.setVariable("name", "zhangsan");
        shell.evaluate("println 'Hello World! I am ' + name;");

        //在script中,声明变量,不能使用def,否则scrope不一致.
        shell.evaluate("date = new Date();ifEmpty = org.springframework.util.StringUtils.isEmpty(name);");
        Date date = (Date)binding.getVariable("date");
        System.out.println("Date:" + date);
        Boolean ifEmpty = (Boolean) binding.getVariable("ifEmpty");
        System.out.println("ifEmpty:" + ifEmpty);

        //以返回值的方式,获取script内部变量值,或者执行结果
        //一个shell实例中,所有变量值,将会在此"session"中传递下去."date"可以在此后的script中获取
        Long time = (Long)shell.evaluate("def time = date.getTime(); return time;");
        System.out.println("Time:" + time);

        //invoke method
//        binding.setVariable("list", new String[]{"A","B","C"});
        shell.setVariable("list", new String[]{"A","B","C"});
        String joinString = (String)shell.evaluate("def call(){return list.join(' - ')};call();");
        System.out.println("Array join:" + joinString);
        shell = null;
        binding = null;
    }

    @Test
    public void manager() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine groovy = manager.getEngineByName("groovy");

        Object eval = groovy.eval("name='pwd is awesome!'; return name;");
        System.out.println(eval);

        groovy.eval("for (i = 0; i < 5; i++) { println i;}");
    }
}
