package hello.core.beanfind;

import hello.core.AppConfig;
import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationContextFindTest {
    // 생성되는 순간 AppConfig의 @Bean 모두 호출
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("내가 등록한 빈 전부 출력")
    void findApplicationBean() {
        for (String name : ac.getBeanDefinitionNames()) {
            BeanDefinition bd = ac.getBeanDefinition(name);
            if (bd.getRole() == BeanDefinition.ROLE_APPLICATION) {
                System.out.println("name = " + name + " / object = " + ac.getBean(name));
            }
        }
    }

    @Test
    @DisplayName("빈 이름 + 타입으로 조회")
    void findBeanByName() {
        MemberService memberService = ac.getBean("memberService", MemberService.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }

    @Test
    @DisplayName("없는 이름이면 예외")
    void findBeanByNameX() {
        assertThrows(NoSuchBeanDefinitionException.class,
                () -> ac.getBean("xxxxx", MemberService.class));
    }

    // 같은 타입(DiscountPolicy) 빈을 2개 등록하는 설정
    static class SameTypeConfig {
        @Bean
        public DiscountPolicy fixDiscountPolicy() {
            return new FixDiscountPolicy();
        }

        @Bean
        public DiscountPolicy rateDiscountPolicy() {
            return new RateDiscountPolicy();
        }
    }

    @Test
    @DisplayName("같은 타입 빈이 둘 이상이면 타입만으로 조회할 수 없다")
    void findByTypeDuplicate() {
        AnnotationConfigApplicationContext sameAc = new AnnotationConfigApplicationContext(SameTypeConfig.class);

        assertThrows(NoUniqueBeanDefinitionException.class,
                () -> sameAc.getBean(DiscountPolicy.class));

        // 해결 1: 이름을 지정
        assertThat(sameAc.getBean("rateDiscountPolicy", DiscountPolicy.class))
                .isInstanceOf(RateDiscountPolicy.class);

        // 해결 2: 해당 타입을 전부 꺼내기
        Map<String, DiscountPolicy> beans = sameAc.getBeansOfType(DiscountPolicy.class);
        assertThat(beans).hasSize(2);
    }
}
