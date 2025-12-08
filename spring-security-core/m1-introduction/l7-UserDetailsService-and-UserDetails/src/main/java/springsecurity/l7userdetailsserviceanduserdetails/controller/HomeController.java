package springsecurity.l7userdetailsserviceanduserdetails.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Collection;

@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping("/home")
    public String homePage(Principal principal, Authentication auth, Model model) {
        model.addAttribute("username", principal.getName());
        Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
        model.addAttribute("roles", roles);
        return "home";
    }
}
