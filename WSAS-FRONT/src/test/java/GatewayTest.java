import static org.springframework.http.MediaType.APPLICATION_JSON;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.webank.wsas.front.utils.JacksonUtils;

public class GatewayTest {

    @Test
    public void commTest() {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject("http://0.0.0.0:43671/wsas-front/data/general/", String.class);
        System.out.println("response -->" + response);
    }
    
    @Test
    public void TestMock() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        
        HttpEntity<?> entity = buildHttpEntity(null, "test");
//        ResponseEntity<String> response = restTemplate.exchange("http://0.0.0.0:43671/wsas-front/config/initMethod/", HttpMethod.POST, entity, String.class);
        ResponseEntity<String> response = restTemplate.exchange("http://0.0.0.0:43671/wsas-front/data/userFlowList/1/2?dateEnd=2024-04-01&userAddress=0xe6b84dd6894ff9bc532c0b75bc0b132285c6ae38", HttpMethod.GET, entity, String.class);
//        ResponseEntity<String> response = restTemplate.exchange("http://0.0.0.0:43671/wsas-front/data/general/", HttpMethod.GET, entity, String.class);
//        ResponseEntity<String> response = restTemplate.exchange("http://0.0.0.0:43671/wsas-front/data/userBalance?type=4&userAddress=0xe6b84dd6894ff9bc532c0b75bc0b132285c6ae38", HttpMethod.GET, entity, String.class);
//        ResponseEntity<String> response = restTemplate.exchange("http://0.0.0.0:43781/wsbcs-chdis/data/general/", HttpMethod.GET, entity, String.class);
        System.out.println("response --> " + response.getBody());
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public HttpEntity buildHttpEntity(Object param, String bizSeqNo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (StringUtils.isNotBlank(bizSeqNo)) {
            headers.add("bizSeqNo", bizSeqNo);
        }
        String paramStr = null;
        if (Objects.nonNull(param)) {
            paramStr = JacksonUtils.objToString(param);
        }
        HttpEntity requestEntity = new HttpEntity(paramStr, headers);
        return requestEntity;
    }
}
