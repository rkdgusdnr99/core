package hello.core.scope;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import static org.assertj.core.api.Assertions.assertThat;

class PrototypeTest {

    @Scope("prototype")
    static class PrototypeBean {
        private int count = 0;

        public void addCount() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init " + this);
        }

        @PreDestroy
        public void destroy() {
            System.out.println("PrototypeBean.destroy");
        }
    }

    // ---- 실습 1 ----
    @Test
    @DisplayName("프로토타입: 조회할 때마다 새 객체, 종료 콜백은 호출 안 됨")
    void prototypeBeanFind() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class);
        PrototypeBean bean1 = ac.getBean(PrototypeBean.class);
        PrototypeBean bean2 = ac.getBean(PrototypeBean.class);

        assertThat(bean1).isNotSameAs(bean2);
        ac.close();
    }

    // ---- 실습 2: 문제 ----
    @RequiredArgsConstructor
    static class ClientBeanBad {                     // 싱글톤 (기본값)
        private final PrototypeBean prototypeBean;   // 생성될 때 딱 한 번 주입됨

        public int logic() {
            prototypeBean.addCount();
            return prototypeBean.getCount();
        }
    }

    @Test
    @DisplayName("싱글톤에 주입된 프로토타입은 계속 같은 객체다")
    void singletonClientUsePrototype() {
        AnnotationConfigApplicationContext ac =
                new AnnotationConfigApplicationContext(ClientBeanBad.class, PrototypeBean.class);

        assertThat(ac.getBean(ClientBeanBad.class).logic()).isEqualTo(1);
        assertThat(ac.getBean(ClientBeanBad.class).logic()).isEqualTo(2); // 1을 기대했는데 2
    }

    // ---- 실습 3: 해결 ----
    @RequiredArgsConstructor
    static class ClientBeanGood {
        private final ObjectProvider<PrototypeBean> prototypeBeanProvider;

        public int logic() {
            PrototypeBean prototypeBean = prototypeBeanProvider.getObject(); // 호출할 때마다 조회
            prototypeBean.addCount();
            return prototypeBean.getCount();
        }
    }

    @Test
    @DisplayName("ObjectProvider로 필요할 때마다 조회하면 항상 새 객체")
    void singletonClientUseProvider() {
        AnnotationConfigApplicationContext ac =
                new AnnotationConfigApplicationContext(ClientBeanGood.class, PrototypeBean.class);

        assertThat(ac.getBean(ClientBeanGood.class).logic()).isEqualTo(1);
        assertThat(ac.getBean(ClientBeanGood.class).logic()).isEqualTo(1);
    }
}
