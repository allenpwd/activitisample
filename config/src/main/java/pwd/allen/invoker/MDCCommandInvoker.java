package pwd.allen.invoker;

import org.activiti.engine.impl.agenda.AbstractOperation;
import org.activiti.engine.impl.interceptor.DebugCommandInvoker;
import org.activiti.engine.logging.LogMDC;

/**
 * @author pwd
 * @create 2019-04-09 11:08
 **/
public class MDCCommandInvoker extends DebugCommandInvoker {
    @Override
    public void executeOperation(Runnable runnable) {
        boolean mdcEnabled = LogMDC.isMDCEnabled();
        LogMDC.setMDCEnabled(true);
        if (runnable instanceof AbstractOperation) {
            AbstractOperation operation = (AbstractOperation) runnable;
            if (operation.getExecution() != null) {
                LogMDC.putMDCExecution(operation.getExecution());
            }
        }
        super.executeOperation(runnable);
        //保证环境的清洁，还原MDC环境
        LogMDC.clear();
        if (!mdcEnabled) {
            LogMDC.setMDCEnabled(false);
        }
    }
}
