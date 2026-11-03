package hello.core;

import hello.core.member.MemberRepository;
import hello.core.member.MemberServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CoreApplicationTests {

	@Autowired
    ApplicationContext ac;

	@Test
	void contextLoads() {
	}

	@Test
	void 같은_타입_빈이_2개인지_확인() {
		// MemberRepository 타입 빈을 이름과 함께 전부 꺼내기
		Map<String, MemberRepository> beans = ac.getBeansOfType(MemberRepository.class);
		beans.forEach((name, bean) -> System.out.println(name + " = " + bean));

		assertThat(beans).containsOnlyKeys("memberRepository", "memoryMemberRepository");

		// @Component로 등록된 MemberServiceImpl이 둘 중 어느 걸 받았는지 확인
		MemberServiceImpl memberService = ac.getBean("memberServiceImpl", MemberServiceImpl.class);
		assertThat(memberService.getMemberRepository()).isSameAs(beans.get("memberRepository"));
	}
	/*
	* 이름 다름 + 타입 같음 : 둘 다 등록, 주입 시 파라미터 이름으로 선택
	* 이름까지 같음 : 순수 스프링은 @Bean이 덮어씀, 스프링 부트는 오류
	* */
}
