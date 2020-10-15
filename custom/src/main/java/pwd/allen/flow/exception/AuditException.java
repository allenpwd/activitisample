package pwd.allen.flow.exception;

/**
 * @author lenovo
 * @create 2020-10-09 11:03
 **/
public class AuditException extends RuntimeException {

    public static final String ILLEGAL_PARAMETER = "非法参数";

    public AuditException(String message) {
        super(message);
    }

    public AuditException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
