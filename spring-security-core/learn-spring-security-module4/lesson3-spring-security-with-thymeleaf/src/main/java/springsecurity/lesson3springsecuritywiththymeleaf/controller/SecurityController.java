package springsecurity.lesson3springsecuritywiththymeleaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityController {

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
}
