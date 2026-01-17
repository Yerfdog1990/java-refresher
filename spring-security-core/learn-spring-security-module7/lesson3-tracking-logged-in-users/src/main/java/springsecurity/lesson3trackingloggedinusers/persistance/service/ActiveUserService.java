package springsecurity.lesson3trackingloggedinusers.persistance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActiveUserService {

    private final SessionRegistry sessionRegistry;

    @Autowired
    public ActiveUserService(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public List<String> getAllActiveUsers() {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> principal instanceof User)
                .map(principal -> (User) principal)
                .filter(user -> !sessionRegistry.getAllSessions(user, false).isEmpty())
                .map(User::getUsername)
                .collect(Collectors.toList());
    }
}
