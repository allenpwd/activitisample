import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pwd.allen.entity.AuditParam;
import pwd.allen.entity.AuditRecord;
import pwd.allen.entity.SYSUsers;
import pwd.allen.flow.util.AuditUtil;

/**
 * @author lenovo
 * @create 2020-10-10 14:34
 **/
@Ignore
public class AuditTest {

    private AuditUtil auditUtil;

    @Before
    public void init() {
        auditUtil = new AuditUtil();
    }

    @Ignore
    @Test
    public void test() {
        AuditParam param = new AuditParam();
        param.setUser(new SYSUsers());

        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);
        binding.setVariable("param", param);
        shell.evaluate("param.resultMap.applyDate=new Date();param.resultMap.code='abc';");
        System.out.println(param.getResultMap());
    }

    @Test
    public void audit() {
        AuditParam auditParam = new AuditParam();
        auditParam.setUser(new SYSUsers());
        auditParam.setFlowDefineKey("wc_site_advert");
        AuditRecord next = auditUtil.findNext(auditParam);
        System.out.println(next);
        auditParam.setCurAuditRecord(next);

        AuditRecord next2 = auditUtil.findNext(auditParam);
        System.out.println(next2);
    }

}
