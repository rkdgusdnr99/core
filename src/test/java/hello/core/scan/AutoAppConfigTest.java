package hello.core.scan;

import hello.core.AutoAppConfig;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.MemberService;
import hello.core.order.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AutoAppConfigTest {

    @Test
    @DisplayName("@Bean 없이 @Component만으로 빈 등록 + 의존관계 주입")
    void basicScan() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class);

        // 빈 이름 = 클래스명 첫 글자 소문자
        MemberService memberService = ac.getBean("memberServiceImpl", MemberService.class);
        assertThat(memberService).isNotNull();

        // 스프링이 생성자에 RateDiscountPolicy를 넣어줬는지 확인
        OrderServiceImpl orderService = ac.getBean(OrderServiceImpl.class);
        assertThat(orderService.getDiscountPolicy()).isInstanceOf(RateDiscountPolicy.class);
    }
}
