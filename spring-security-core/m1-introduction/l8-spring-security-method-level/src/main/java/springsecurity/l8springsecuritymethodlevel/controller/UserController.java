package springsecurity.l8springsecuritymethodlevel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springsecurity.l8springsecuritymethodlevel.service.UserService;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String userAccess() {
        return userService.getUserData();
    }

    @GetMapping("/admin")
    public String adminAccess() {
        return userService.getAdminData();
    }
}