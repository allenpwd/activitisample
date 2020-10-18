package pwd.allen.coreapi.script;

import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author lenovo
 * @create 2020-10-03 10:11
 **/
public class JavascriptTest {

    /**
     * 获取的是NashornScriptEngine脚本引擎，jdk内置的，它支持javascript
     * @see jdk.nashorn.api.scripting.NashornScriptEngineFactory
     * @throws ScriptException
     */
    @Test
    public void test() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");

        engine.eval("for (var i = 0; i < 5; i++) { print(i);}");
    }
}
