package hello.core.web;

import hello.core.scope.MyLogger;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LogDemoController {

    private final LogDemoService logDemoService;
    private final MyLogger myLogger;

    @GetMapping("/log-demo")
    public String logDemo(HttpServletRequest request) {
        System.out.println("myLogger = " + myLogger.getClass());
        myLogger.setRequestURL(request.getRequestURL().toString());
        myLogger.log("controller test");
        logDemoService.logic("testId");
        return "OK " + myLogger.getUuid();
    }
}
