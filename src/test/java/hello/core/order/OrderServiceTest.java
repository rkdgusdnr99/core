package hello.core.order;

import hello.core.AppConfig;
import hello.core.discount.FixDiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemoryMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderServiceTest {

    MemberService memberService;
    OrderService orderService;

    @BeforeEach
    void beforeEach() {
        AppConfig appConfig = new AppConfig();
        memberService = appConfig.memberService();
        orderService = appConfig.orderService();
    }

    @Test
    void createOrder() {
        Long memberId = 1L;
        memberService.join(new Member(memberId, "memberA", Grade.VIP));

        Order order = orderService.createOrder(memberId, "itemA", 10000);

        assertThat(order.getDiscountPrice()).isEqualTo(1000);
    }

    @Test
    void 스프링_없이도_원하는_구현체로_테스트할_수_있다() {
        MemoryMemberRepository repository = new MemoryMemberRepository();
        repository.save(new Member(2L, "memberB", Grade.VIP));

        OrderServiceImpl orderService = new OrderServiceImpl(repository, new FixDiscountPolicy());
        Order order = orderService.createOrder(2L, "itemB", 20000);

        assertThat(order.getDiscountPrice()).isEqualTo(1000); // 정액 1000원
    }
}
