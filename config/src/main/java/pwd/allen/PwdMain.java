package pwd.allen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author 门那粒沙
 * @create 2019-04-22 14:21
 **/
public class PwdMain {

    private static final Logger logger = LoggerFactory.getLogger(PwdMain.class);

    public static void main(String[] args) {
        logger.info("test error {}", new Date());
        try {
            int i = 123 / 0;
            System.out.println(i);
        } catch (Exception e) {
            logger.error("出错", e);
        }
    }
}
