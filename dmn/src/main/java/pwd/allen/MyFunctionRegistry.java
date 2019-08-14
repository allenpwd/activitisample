package pwd.allen;

import org.activiti.dmn.engine.CustomExpressionFunctionRegistry;
import pwd.allen.entity.Person;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lenovo
 * @create 2019-08-14 19:34
 **/
public class MyFunctionRegistry implements CustomExpressionFunctionRegistry {

    protected static Map<String, Method> methodMap = new HashMap<String, Method>();

    static {
        try {
            methodMap.put("getMd5", Person.class.getDeclaredMethod("getMd5", String.class));
            methodMap.put("isChild", Person.class.getDeclaredMethod("isChild", Person.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Method> getCustomExpressionMethods() {
        return methodMap;
    }
}
