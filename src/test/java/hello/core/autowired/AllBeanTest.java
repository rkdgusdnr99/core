package hello.core.autowired;

import hello.core.AutoAppConfig;
import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AllBeanTest {

    // ---- 해결 3: @Qualifier ----
    static class QualifierClient {
        private final DiscountPolicy discountPolicy;

        QualifierClient(@Qualifier("fixDiscountPolicy") DiscountPolicy discountPolicy) {
            this.discountPolicy = discountPolicy;
        }
    }

    /*
    * 스프링의 규칙은 더 구체적으로 지정한 쪽이 우선
    * @Primary는 "기본값은 자신"
    * @Qualifier는 "여기서는 정확히 이것"
    * 즉 @Qualifier가 우선
    * @RequiredArgsConstructor 대신 생성자 사용 이유: Lombok이 만드는 생성자에는 파라미터 애노테이션(@Qualifier)를 붙힐 수 없음
    * */
    @Test
    @DisplayName("@Qualifier가 @Primary보다 우선한다")
    void qualifierWins() {
        AnnotationConfigApplicationContext ac =
                new AnnotationConfigApplicationContext(AutoAppConfig.class, QualifierClient.class);

        QualifierClient client = ac.getBean(QualifierClient.class);
        assertThat(client.discountPolicy).isInstanceOf(FixDiscountPolicy.class);
    }

    // ---- 전부 받아서 골라 쓰기 ----
    @RequiredArgsConstructor
    static class DiscountService {
        private final Map<String, DiscountPolicy> policyMap;
        private final List<DiscountPolicy> policies;

        public int discount(Member member, int price, String discountCode) {
            DiscountPolicy discountPolicy = policyMap.get(discountCode);
            System.out.println("discountCode = " + discountCode + ", discountPolicy = " + discountPolicy);
            return discountPolicy.discount(member, price);
        }
    }

    @Test
    @DisplayName("Map으로 모든 DiscountPolicy를 받아 골라 쓴다")
    void findAllBean() {
        AnnotationConfigApplicationContext ac =
                new AnnotationConfigApplicationContext(AutoAppConfig.class, DiscountService.class);
        DiscountService discountService = ac.getBean(DiscountService.class);
        Member member = new Member(1L, "userA", Grade.VIP);

        System.out.println("policyMap = " + discountService.policyMap);
        System.out.println("policies = " + discountService.policies);

        assertThat(discountService.policyMap).containsKeys("fixDiscountPolicy", "rateDiscountPolicy");
        assertThat(discountService.discount(member, 20000, "fixDiscountPolicy")).isEqualTo(1000);
        assertThat(discountService.discount(member, 20000, "rateDiscountPolicy")).isEqualTo(2000);
        assertThat(discountService.policyMap.get("rateDiscountPolicy")).isInstanceOf(RateDiscountPolicy.class);
    }
}
