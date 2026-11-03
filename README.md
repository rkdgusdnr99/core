2026-11-02
----------------------------------------------------------------------------------------
1강 – 회원 도메인 만들기

요약

회원 저장소가 아직 미정이라 MemberRepository(역할)와 MemoryMemberRepository(구현)로 나눴다.
서비스도 MemberService / MemberServiceImpl로 분리했다.
given / when / then 흐름으로 첫 테스트를 작성했다.

질문

Q. 필드를 인터페이스 타입으로 선언했으니 다형성을 잘 쓴 것 아닌가?

java
private final MemberRepository memberRepository = new MemoryMemberRepository();
왼쪽이 MemberRepository이니 다형성을 쓴 건 맞다.
그런데 오른쪽의 new MemoryMemberRepository() 때문에 구현체까지 직접 알고 있다.
저장소를 DB로 바꾸려면 결국 이 서비스를 고쳐야 한다. → 다형성은 썼지만 DIP는 위반한 상태.

느낀 점

2~3년 전에 배운 내용인데, 실무를 하고 다시 보니 확실히 새롭다.

----------------------------------------------------------------------------------------
2강 – 주문과 할인, 그리고 문제 발견

요약

DiscountPolicy를 인터페이스로 두고 Fix / Rate 구현을 만들었다.
주문 서비스는 회원 조회 → 할인 금액 계산 → 주문 생성 순서로 동작한다.
테스트는 성공 케이스뿐 아니라 실패 케이스(VIP가 아니면 할인 없음)도 넣는다.

질문

Q. 여기서 의존관계 역전(DIP)이 생긴건가?

아니다. DIP가 위반된 것을 확인한 것이다.
정책을 Fix → Rate로 바꾸려고 할인과 상관없는 OrderServiceImpl을 수정해야 했다.
DIP 위반: 추상화(DiscountPolicy)뿐 아니라 구체 클래스(RateDiscountPolicy)에도 의존
OCP 위반: 기능을 확장하려고 기존 코드를 변경

----------------------------------------------------------------------------------------
3강 – AppConfig로 관심사 분리

요약

서비스에서 new를 지우고 생성자로 받도록 바꿨다. import에서 구현체가 사라지고 인터페이스만 남았다. (DIP 준수)
구현체 선택과 연결은 AppConfig가 맡는다. 할인 정책을 바꿀 땐 AppConfig 한 줄만 고친다. (OCP 준수)
@RequiredArgsConstructor: final 필드를 받는 생성자를 만들어준다.

용어

IoC(제어의 역전): 객체 생성·연결의 제어권이 내 코드가 아니라 외부에 있다.
DI(의존성 주입): 외부가 필요한 구현체를 생성자 등으로 넣어준다.

질문

Q1. SI에서는 DI/IoC를 신경 쓰고 개발한 적이 없다. 돌아가기만 하면 됐고, 어차피 수정은 해야 한다. 정말 의미가 있나?

돌아보니 이미 쓰고 있었다. 컨트롤러에서 서비스를 new 하지 않고 @Autowired나 생성자로 받아 썼다. 그게 DI고, 스프링이 객체를 관리한 게 IoC다.
그 덕분에 모르고 누리던 것들
@Transactional: 스프링이 관리하는 빈이라 프록시로 트랜잭션을 걸 수 있었다. new로 만든 객체였다면 안 걸린다.
싱글톤: 요청마다 서비스 객체를 새로 만들지 않았다.

Q2. AppConfig가 꼭 있어야 하나? appConfig.memberService()처럼 쓰면 오히려 코드가 어지럽다. 필요할 때 선언해서 쓰는 게 낫지 않나?

3강의 AppConfig는 스프링 컨테이너가 하는 일을 손으로 해보는 교육용 단계다. 실무에서 저렇게 쓰지 않는다.
단계	객체를 만들고 연결하는 주체	사용하는 쪽
1~2강	서비스가 직접 new	new MemoryMemberRepository()
3강	AppConfig	appConfig.memberService()
4강	스프링 컨테이너 + AppConfig	ac.getBean(...)
6강~	스프링 컨테이너 + @Component 스캔	생성자로 받기만 함
현업에서 하던 "@Service 붙이고 받아 쓰기"가 6강의 최종 모습이다.
필요할 때 직접 new 하면
쓰는 쪽이 의존의 의존까지 알고 조립해야 하고
그 코드가 여러 곳에 퍼지면 정책 변경 때 전부 고쳐야 하고
스프링 빈이 아니라서 싱글톤도 @Transactional도 안 된다.

Q3. 그럼 Config는 언제 만드나? 스프링은 뭘 대신 해주나?

스프링이 대신 해주는 것
컴포넌트 스캔: 내 업무 클래스는 @Service, @Component만 붙이면 된다.
부트 자동 설정: starter를 추가하고 application.properties에 값만 적으면 DataSource, TransactionManager, ObjectMapper 등이 등록된다.
내가 같은 타입 빈을 직접 등록하면 부트는 내 것을 우선한다.
직접 만드는 경우
부트 기본값을 바꿀 때 (ObjectMapper 날짜 포맷, RestTemplate 타임아웃)
웹 동작 확장: WebMvcConfigurer로 인터셉터·CORS (현업의 WebConfig가 이거였다)
보안 설정, 다중 DataSource, 부트가 모르는 외부 라이브러리 객체
기준: 내 업무 클래스는 @Component 계열, 남의 클래스를 설정해서 쓰거나 기본값이 안 맞으면 @Configuration + @Bean

느낀 점

DI를 안 지킨게 아니라 스프링이 대신 지켜주고 있던 것이였다.

----------------------------------------------------------------------------------------
4강 – 스프링으로 전환하기

요약

AppConfig에 @Configuration, @Bean 두 개만 붙여서 객체 관리를 스프링에 넘겼다.
@Bean이 붙은 메서드의 반환 객체가 스프링 빈이 되고, 빈 이름은 메서드 이름이다.
스프링 컨테이너 = ApplicationContext
BeanFactory(빈 관리·조회) + 부가기능(환경변수, 국제화, 이벤트, 리소스)
빈 조회
getBean(이름, 타입), getBean(타입)
같은 타입이 2개 이상이면 타입만으로 조회 시 NoUniqueBeanDefinitionException → 이름 지정 또는 getBeansOfType

질문

Q. 4강은 그냥 빈 설명인가?

빈 설명이 중심이지만 핵심은 3강에서 내가 하던 AppConfig 역할을 스프링에게 넘긴 것이다.
SI 레거시의 context-*.xml에 있던 <bean>도 결국 같은 BeanDefinition이었다.

----------------------------------------------------------------------------------------
5강 – 싱글톤

요약

스프링 컨테이너는 빈을 하나만 만들어서 계속 같은 객체를 돌려준다.
순수 AppConfig는 호출할 때마다 새 객체, 스프링 컨테이너는 몇 번을 꺼내도 같은 객체
직접 싱글톤 패턴(private 생성자 + getInstance())을 짜면 클라이언트가 구체 클래스를 알아야 해서 DIP가 깨진다. 스프링은 평범한 클래스를 싱글톤으로 관리해준다.
싱글톤 빈은 stateless로 설계해야 한다.

질문

Q1. stateless가 무슨 말인가?

객체가 요청과 관련된 값을 필드에 기억하지 않는다는 뜻이다.
싱글톤 빈은 모든 요청 스레드가 같은 객체를 쓴다.
필드는 객체당 하나 → 모든 스레드가 공유 → 덮어쓰기 사고
파라미터·지역변수는 호출마다 각 스레드 스택에 따로 생김 → 안전
java
// ❌ A가 1만원 주문 후 B가 2만원 주문하면, A 조회 시 2만원
private int price;
public void order(String name, int price) { this.price = price; }

// ✅ 필드에 저장하지 않고 바로 반환
public int order(String name, int price) { return price; }
필드를 아예 쓰면 안 되는 건 아니다. 기준은 요청마다 바뀌는 값이냐다.
private final MemberRepository memberRepository; → 주입 후 읽기만 하니 OK
private String currentUserId; → 요청마다 다르니 NO

Q2. 실무 ServiceImpl에 List<Order> tempList = new ArrayList<>();가 많았는데 잘못된 건가?

선언 위치에 따라 다르다.
메서드 안 → 지역변수 → 호출마다 새로 만들어짐 → 안전 (내가 본 대부분이 이 경우)
클래스 바로 아래 → 필드 → 앱 전체에 하나 → 요청끼리 섞이거나 계속 쌓임
이런 버그는 로컬에선 멀쩡하고 운영 동시 요청에서만 터졌을 것.

----------------------------------------------------------------------------------------
2026-11-03
----------------------------------------------------------------------------------------

6강 – 컴포넌트 스캔과 의존관계 주입 방법

요약

@ComponentScan이 @Component가 붙은 클래스를 찾아 자동으로 빈 등록한다. 빈 이름은 클래스명 첫 글자 소문자.
의존관계는 생성자에 @Autowired를 붙여 자동 주입한다. 생성자가 하나면 @Autowired도 생략 가능.
AutoAppConfig는 기존 AppConfig가 같이 스캔되지 않도록 excludeFilters로 @Configuration을 제외했다.
@SpringBootApplication 안에 @ComponentScan이 들어 있다. 그래서 스프링 부트에서는 따로 스캔 설정을 안 했던 것.
주입 방법: 생성자 / 수정자(setter) / 필드 / 일반 메서드 → 생성자 주입 + final + @RequiredArgsConstructor가 실무 표준

질문

Q1. CoreApplicationTests는 왜 통과하나? 무엇이 겹쳤나?

@Configuration도 내부에 @Component를 포함한다 → @SpringBootApplication의 스캔에 AppConfig까지 잡힌다.
그래서 같은 MemoryMemberRepository가 두 경로로 등록됐다.
등록한 곳	                            빈 이름
AppConfig의 @Bean memberRepository()	memberRepository
@Component MemoryMemberRepository	    memoryMemberRepository
빈이 2개 있다는 것만으로는 오류가 안 난다. 주입할 때 하나를 못 고르면 오류가 난다.
MemberServiceImpl(MemberRepository memberRepository) → 타입으로 2개 → 파라미터 이름 memberRepository와 같은 빈을 선택 → 통과
파라미터 이름 매칭은 컴파일 옵션 -parameters가 있어야 동작한다. (자바는 기본적으로 파라미터 이름을 지움)
contextLoads()는 모든 빈이 만들어지고 주입된 것까지만 확인한다. 2개인지 보려면 getBeansOfType으로 직접 확인해야 한다.

Q2. 그럼 @Component가 @Bean보다 우선인가?

아니다. 이번엔 이름이 맞아서 오히려 @Bean 쪽이 선택됐다. 주입할 때는 등록 방식이 아니라 타입과 이름만 본다.
수동/자동 우선순위는 빈 이름까지 같을 때 이야기다.
순수 스프링: 수동(@Bean)이 덮어씀
스프링 부트: 기본적으로 오류 (의도치 않은 덮어쓰기 방지)

느낀 점

저장소는 앱 전체에서 하나를 같이 써야 하고(싱글톤), 
서비스는 그 하나를 생성자로 받아 끝까지 바꾸지 않는다(생성자 주입 + final).