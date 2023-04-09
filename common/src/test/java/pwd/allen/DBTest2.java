package pwd.allen;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 数据表操作
 * @author pwd
 * @create 2019-04-07 18:23
 **/
@RunWith(SpringRunner.class)
@ContextConfiguration(value = "classpath:activiti-mysql.cfg.xml")
public class DBTest2 {

    /**
     * 清空表数据
     * @Sql是spring test的功能，应该需要在spring容器中有个dataSource
     */
    @Test
    @Sql(scripts = {"/sql/clearDB.sql"}, config = @SqlConfig(commentPrefix = "--", separator = ";"))
    public void clearDB() {}

}
