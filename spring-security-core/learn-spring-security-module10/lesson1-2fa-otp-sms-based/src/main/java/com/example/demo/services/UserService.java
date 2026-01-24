package com.example.demo.services;

import com.example.demo.entities.MyUser;
import com.example.demo.repos.UserRepository;
import com.example.demo.requests.UserRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpSession session;

    public boolean login(UserRequest userRequest) {
        MyUser user = userRepository.findByUsername(userRequest.getUsername());
        if (user != null && passwordEncoder.matches(userRequest.getPassword(), user.getPassword())) {
            // Create session
            session.setAttribute("user", user);
            session.setAttribute("authenticated", true);
            return true;
        }
        return false;
    }

    public void logout() {
        session.invalidate();
    }

    public MyUser getCurrentUser() {
        return (MyUser) session.getAttribute("user");
    }

    public boolean isAuthenticated() {
        return session.getAttribute("authenticated") != null &&
                (boolean) session.getAttribute("authenticated");
    }

    public boolean register(UserRequest userRequest) {
        if (userRepository.findByUsername(userRequest.getUsername()) != null) {
            return false;
        }

        MyUser user = new MyUser();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setTelephoneNumber(userRequest.getTelephoneNumber());

        userRepository.save(user);
        // Create session after registration for testing purposes
        session.setAttribute("user", user);
        session.setAttribute("authenticated", true);

        return true;
    }

    public List<MyUser> getAll() {
        return userRepository.findAll();
    }
}