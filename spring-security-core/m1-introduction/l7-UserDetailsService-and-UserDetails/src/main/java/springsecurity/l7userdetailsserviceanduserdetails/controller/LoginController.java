package springsecurity.l7userdetailsserviceanduserdetails.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/custom-login")
    public String loginPage() {
        return "login";
    }
}
