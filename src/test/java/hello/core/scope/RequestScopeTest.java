package hello.core.scope;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class RequestScopeTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DisplayName("요청마다 MyLogger가 새로 만들어져 UUID가 다르다")
    void requestScope() {
        String first = restTemplate.getForObject("/log-demo", String.class);
        String second = restTemplate.getForObject("/log-demo", String.class);

        System.out.println("first = " + first + ", second = " + second);
        assertThat(first).startsWith("OK ");
        assertThat(first).isNotEqualTo(second);
    }
}
