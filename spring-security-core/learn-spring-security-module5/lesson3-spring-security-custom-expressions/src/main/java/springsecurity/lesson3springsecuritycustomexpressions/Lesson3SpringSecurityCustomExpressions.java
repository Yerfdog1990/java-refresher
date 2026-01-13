package springsecurity.lesson3springsecuritycustomexpressions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity
@SpringBootApplication
public class Lesson3SpringSecurityCustomExpressions {

    public static void main(String[] args) {
        SpringApplication.run(Lesson3SpringSecurityCustomExpressions.class, args);
    }

}
