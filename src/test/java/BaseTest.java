import com.ying.SpringbootActivitiApplication;
import com.ying.dto.resp.BaseRespDto;
import com.ying.model.ActModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author lyz
 */
// 获取启动类，加载配置，确定装载Spring程序的装载方法，它回去寻找主配置启动类（被 @SpringBootApplication 注解的）
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringbootActivitiApplication.class)
@RunWith(SpringRunner.class)
public class BaseTest {

    @Autowired
    private TestRestTemplate restTemplate;

//    @Test
    public void testGet() {
//        String name = restTemplate.getForObject("/design/model/view/1", String.class);
//        System.out.println(name);
        ResponseEntity<BaseRespDto> b = restTemplate.getForEntity("/design/model/activity/list/1", BaseRespDto.class);
        System.out.println(b.getBody().getData());
    }

//    @Test
    public void testPost() {
        ActModel a = new ActModel();
        a.setName("cherryTest");
        a.setCategory("1");
        ResponseEntity<BaseRespDto> b = restTemplate.postForEntity("/design/model/add", a, BaseRespDto.class);
        System.out.println(b.getBody().getData());
    }
}
