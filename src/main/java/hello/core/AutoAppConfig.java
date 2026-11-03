package hello.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackages = "hello.core",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class) // @Configuration이 붙은 클래스는 제외
) // hello.core 패키지와 그 하위를 뒤져서 @Component가 붙은 클래스를 전부 빈으로 등록
public class AutoAppConfig {
}