package l2springsecurityarchitecture.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping("/john")
    public String hello1() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            log.info("Logged in as: " + authentication.getName() +
                    " | Roles: " + authentication.getAuthorities());
        }
        return "Welcome John to Spring security! You have been successfully authenticated.";
    }
    @GetMapping("/jane")
    public String hello2() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            log.info("Logged in as: " + authentication.getName() +
                    " | Roles: " + authentication.getAuthorities());
        }
        return "Welcome Jane to Spring security! You have been successfully authenticated.";
    }
}
