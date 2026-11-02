package hello.core.singleton;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;

class StatefulServiceTest {

    static class TestConfig {
        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }

    @Test
    @DisplayName("A가 주문한 뒤 B가 끼어들면, A의 금액이 B 금액으로 바뀐다")
    void statefulServiceSingleton() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);
        StatefulService service1 = ac.getBean(StatefulService.class); // 사용자 A의 요청
        StatefulService service2 = ac.getBean(StatefulService.class); // 사용자 B의 요청

        service1.order("userA", 10000); // A: 1만원 주문
        service2.order("userB", 20000); // B: 2만원 주문 (끼어듦)

        int price = service1.getPrice(); // A가 자기 주문 금액 조회
        System.out.println("userA price = " + price);

        assertThat(price).isEqualTo(20000); // ❌ 1만원이어야 하는데 2만원
    }

    @Test
    @DisplayName("값을 필드 대신 반환값으로 처리하면 안전하다")
    void statelessService() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);
        StatefulService service1 = ac.getBean(StatefulService.class);
        StatefulService service2 = ac.getBean(StatefulService.class);

        int userAPrice = service1.orderStateless("userA", 10000);
        int userBPrice = service2.orderStateless("userB", 20000);

        assertThat(userAPrice).isEqualTo(10000); // ✅
        assertThat(userBPrice).isEqualTo(20000);
    }
}
