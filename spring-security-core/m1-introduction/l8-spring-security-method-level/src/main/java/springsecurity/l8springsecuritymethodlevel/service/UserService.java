package springsecurity.l8springsecuritymethodlevel.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String getUserData() {
        return "This is user data accessible by USER or ADMIN.";
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String getAdminData() {
        return "This is admin data accessible only by ADMIN.";
    }
}