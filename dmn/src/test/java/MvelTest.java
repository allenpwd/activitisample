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

    /**
     * 测试 调用对象属性、对象方法
     */
    @Test
    public void test1() {

        //编译表达式
        //在MVEL中所有的判断是否相等 都是对值的判断，而没有对引用的判断，因此表达式foo == 'bar' 等价于java中的foo.equals("bar")
        //null和nil都可以用来表示一个空值
        //当两个不同类型且没有可比性的值进行比较时，需要将左边的值强制转换成右边的值的类型时，MVEL会应用类型强制转换系统
        Serializable compileExpression = MVEL.compileExpression(" person!=nil && person.getAge() < 18 && person.name == 'allen' && '123'==123");

        //执行表达式返回结果
        HashMap<String, Object> params = new HashMap<>();
        Person person = new Person();
        person.setAge(17);
        person.setName("allen");
        params.put("person", person);
        System.out.println(MVEL.executeExpression(compileExpression, params, Boolean.class));

        System.out.println(MVEL.executeExpression(MVEL.compileExpression("person.name"), params, String.class));

    }

    /**
     * 测试 调用 自定义方法（必须是public static修饰）
     * @throws NoSuchMethodException
     */
    @Test
    public void testMethod() throws NoSuchMethodException {
        Method method1 = Person.class.getDeclaredMethod("getMd5", String.class);
        Method method2 = Person.class.getDeclaredMethod("print", Object.class);

        //创建解析上下文对象
        ParserContext parserContext = new ParserContext();

        //添加方法导入
        parserContext.addImport("getMd5", method1);
        parserContext.addImport("print", method2);

        //在一段脚本里，你可以写任意多个语句，但注意要用分号来作为每个语句的结束符，只有一个语句时或最后一个语句时除外
        Serializable compileExpression = MVEL.compileExpression("print(['a',true,123,12.3f,12.3d]);getMd5('a')", parserContext);
        System.out.println(MVEL.executeExpression(compileExpression, null, String.class));

        compileExpression = MVEL.compileExpression("print({'name':'pwd','age':'21'})", parserContext);
        System.out.println(MVEL.executeExpression(compileExpression, null, String.class));

    }
}
