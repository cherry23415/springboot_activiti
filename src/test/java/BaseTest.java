import com.ying.SpringbootActivitiApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
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

    @Test
    public void getName() {
        String name = restTemplate.getForObject("/design/model/view/1", String.class);
        System.out.println(name);
    }
}
