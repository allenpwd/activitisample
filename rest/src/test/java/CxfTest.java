import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 门那粒沙
 * @create 2019-08-18 17:16
 **/
public class CxfTest {

    @Test
    public void test() throws IOException {
        WebClient webClient = WebClient.create("http://localhost:8080/activiti-rest/service/management/properties");

        Response response = webClient.get();

        InputStream ips = (InputStream) response.getEntity();

        String content = IOUtils.readStringFromStream(ips);

        System.out.println(content);
    }
}
