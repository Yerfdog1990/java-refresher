package l2springsecurityarchitecture.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping("/john")
    public String hello1() {
        return "Welcome John to Spring security! You have been successfully authenticated.";
    }
    @GetMapping("/jane")
    public String hello2() {
        return "Welcome Jane to Spring security! You have been successfully authenticated.";
    }
}
