package hello.core.member;

import org.springframework.stereotype.Component;

/*
 * 회원 서비스
 *
 * 1강
 * 저장소를 MemberRepository(역할)로 선언해 다형성을 사용
 * 하지만 아직 구현체를 직접 생성하므로 DIP 위반
 *
 * */
@Component
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }

    // 테스트용
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
