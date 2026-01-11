package springsecurity.lesson2methodlevelauthorizationwithexpressions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity
@SpringBootApplication
public class Lesson2MethodLevelAuthorizationWithExpressions {

    public static void main(String[] args) {
        SpringApplication.run(Lesson2MethodLevelAuthorizationWithExpressions.class, args);
    }

}
