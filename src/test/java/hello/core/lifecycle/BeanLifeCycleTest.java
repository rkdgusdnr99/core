package hello.core.lifecycle;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;

class BeanLifeCycleTest {

    static class LifeCycleConfig {
        @Bean
        public NetworkClient networkClient() {
            NetworkClient networkClient = new NetworkClient();   // ① 객체 생성
            networkClient.setUrl("http://hello-spring.dev");    // ② 값 넣기
            return networkClient;
        }
    }

    @Test
    void lifeCycleTest() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);
        NetworkClient client = ac.getBean(NetworkClient.class);
        assertThat(client.getUrl()).isEqualTo("http://hello-spring.dev");

        ac.close(); // 컨테이너 종료
    }
}
