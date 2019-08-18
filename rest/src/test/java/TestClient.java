import org.apache.cxf.common.util.Base64Utility;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 * 需要部署activiti-webapp-rest2项目
 *
 * @author 门那粒沙
 * @create 2019-08-18 18:19
 **/
public class TestClient {

    @Test
    public void showProperties() throws IOException {

        //设置URL、认证用户名、密码，（ACT_ID_USER里有配置帐号密码）
        WebClient webClient = WebClient.create("http://localhost:8080/activiti-rest/service/management/properties", "pwd", "123", null);


        //设置认证格式为基础认证格式
        String authorizationHeader = "Basic " + Base64Utility.encode("user:password".getBytes());
        webClient.header("Authorization", authorizationHeader);

        Response response = webClient.get();

        InputStream ips = (InputStream) response.getEntity();

        String content = IOUtils.readStringFromStream(ips);

        System.out.println(content);
    }
}
