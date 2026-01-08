package springsecurity.lesson0learnspringbootwithjsp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/initial")
    public String loadInitialPage(){
        return "index";
    }

    @GetMapping("/home")
    public String loadHomePage(){
        return "home";
    }

    @PostMapping("/home")
    public String handleHomePagePost(){
        return "home";
    }

    @GetMapping("/script")
    public String loadScriptingPage(){
        return "scripting";
    }

    @PostMapping("/scriptlet")
    public String handleScriptPost(){
        return "scriptlet";
    }

    @PostMapping("/welcome")
    public String welcomeUser(@RequestParam("user") String user, Model model) {
        model.addAttribute("user", user);
        return "welcome";
    }

    @GetMapping("/declaration")
    public String loadDeclarationPage(){
        return "declaration";
    }

    @GetMapping("/directive")
    public String loadDirectivePage(){
        return "directive";
    }

    @GetMapping("/expression")
    public String loadExpressionPage(){
        return "expression";
    }
}
