package springsecurity.lesson1urlauthorizationwithexpressions.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityController {

    @GetMapping("/user")
    public String user() {
        return "user";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/ip")
    public String ipAddress(HttpServletRequest request) {
        String clientIP = request.getRemoteAddr();
        System.out.println("Client IP: " + clientIP);
        return "redirect:/admin";
    }

    @GetMapping("/anonymous")
    public String anonymous() {
        return "anonymous";
    }
}
