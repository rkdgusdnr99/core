package hello.core.singleton;

import hello.core.AppConfig;
import hello.core.member.MemberRepository;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import hello.core.order.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;

class SingletonTest {

    @Test
    @DisplayName("스프링 없는 순수 DI 컨테이너: 호출할 때마다 새 객체")
    void pureContainer() {
        AppConfig appConfig = new AppConfig();
        MemberService memberService1 = appConfig.memberService();
        MemberService memberService2 = appConfig.memberService();

        System.out.println("memberService1 = " + memberService1);
        System.out.println("memberService2 = " + memberService2);
        assertThat(memberService1).isNotSameAs(memberService2);
    }

    @Test
    @DisplayName("스프링 컨테이너: 몇 번을 꺼내도 같은 객체")
    void springContainer() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService1 = ac.getBean("memberService", MemberService.class);
        MemberService memberService2 = ac.getBean("memberService", MemberService.class);

        System.out.println("memberService1 = " + memberService1);
        System.out.println("memberService2 = " + memberService2);
        assertThat(memberService1).isSameAs(memberService2);
    }

    @Test
    @DisplayName("@Configuration: memberRepository()를 여러 번 불러도 객체는 하나")
    void configurationTest() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberServiceImpl memberService = ac.getBean("memberService", MemberServiceImpl.class);
        OrderServiceImpl orderService = ac.getBean("orderService", OrderServiceImpl.class);
        MemberRepository memberRepository = ac.getBean("memberRepository", MemberRepository.class);

        assertThat(memberService.getMemberRepository()).isSameAs(memberRepository);
        assertThat(orderService.getMemberRepository()).isSameAs(memberRepository);

        AppConfig bean = ac.getBean(AppConfig.class);
        System.out.println("AppConfig bean class = " + bean.getClass());
        assertThat(bean.getClass().getName()).contains("$$SpringCGLIB$$");
    }

    // @Configuration이 없는 설정
    static class NoConfigurationAppConfig {
        @Bean
        public MemberService memberService() {
            return new MemberServiceImpl(memberRepository());
        }

        @Bean
        public MemberRepository memberRepository() {
            return new MemoryMemberRepository();
        }
    }

    @Test
    @DisplayName("@Configuration이 없으면 싱글톤이 깨진다")
    void noConfigurationTest() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(NoConfigurationAppConfig.class);
        MemberServiceImpl memberService = ac.getBean(MemberServiceImpl.class);
        MemberRepository memberRepository = ac.getBean(MemberRepository.class);

        assertThat(memberService.getMemberRepository()).isNotSameAs(memberRepository);
    }
}
