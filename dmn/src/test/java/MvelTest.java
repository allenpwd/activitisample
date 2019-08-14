import org.junit.Test;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import pwd.allen.entity.Person;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.function.Function;

/**
 * @author lenovo
 * @create 2019-08-13 9:06
 **/
public class MvelTest {

    @Test
    public void test1() {

        //编译表达式
        Serializable compileExpression = MVEL.compileExpression("person.age >= 18");

        //执行表达式返回结果
        HashMap<String, Object> params = new HashMap<>();
        Person person = new Person();
        person.setAge(17);
        person.setName("allen");
        params.put("person", person);
        System.out.println(MVEL.executeExpression(compileExpression, params, Boolean.class));

        System.out.println(MVEL.executeExpression(MVEL.compileExpression("person.name"), params, String.class));

    }

    @Test
    public void testMethod() throws NoSuchMethodException {
        Method method = Person.class.getDeclaredMethod("getMd5", String.class);

        //创建解析上下文对象
        ParserContext parserContext = new ParserContext();

        //添加方法导入
        parserContext.addImport("myMethod", method);

        Serializable compileExpression = MVEL.compileExpression("myMethod('a')", parserContext);

        System.out.println(MVEL.executeExpression(compileExpression, null, String.class));

    }
}
