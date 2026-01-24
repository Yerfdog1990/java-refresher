package com.example.demo.controllers;

import com.example.demo.entities.MyUser;
import com.example.demo.entities.OneTimePassword;
import com.example.demo.requests.SignOTPRequest;
import com.example.demo.requests.UserRequest;
import com.example.demo.repos.responses.StandardResponse;
import com.example.demo.services.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Burak Fircasiguzel < www.github.com/burakfircasiguzel >
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OneTimePasswordService oneTimePasswordService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private HttpSession session;

    @PostMapping("/login")
    public ResponseEntity<StandardResponse> login(@RequestBody @Valid UserRequest userRequest) {
        if (userService.login(userRequest)) {
            // Send OTP after successful login
            smsService.sendSmsByService(userRequest);
            return ResponseEntity.ok(new StandardResponse("OTP has been sent to your registered phone number"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new StandardResponse("Invalid username or password"));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        if (userService.isAuthenticated()) {
            return ResponseEntity.ok(userService.getAll());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new StandardResponse("Please login first"));
    }

    @PostMapping("/register")
    public ResponseEntity<StandardResponse> register(@RequestBody @Valid UserRequest userRequest) {
        if (userService.register(userRequest)) {
            return ResponseEntity.ok(new StandardResponse("Registration successful"));
        }
        return ResponseEntity.badRequest()
                .body(new StandardResponse("Username already exists"));
    }

    @PostMapping("/logout")
    public ResponseEntity<StandardResponse> logout() {
        userService.logout();
        return ResponseEntity.ok(new StandardResponse("Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        if (userService.isAuthenticated()) {
            return ResponseEntity.ok(userService.getCurrentUser());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new StandardResponse("Please login first"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<StandardResponse> verifyOtp(@RequestBody SignOTPRequest signOTPRequest) {
        if (!userService.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new StandardResponse("Please login first"));
        }

        MyUser currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new StandardResponse("User not found"));
        }

        OneTimePassword oneTimePassword = oneTimePasswordService.findTopByUserId(currentUser.getId());

        if (oneTimePassword != null && oneTimePassword.getCode().equalsIgnoreCase(signOTPRequest.getCode())) {
            // Mark OTP as used
            oneTimePassword.setStatus(true);
            oneTimePasswordService.save(oneTimePassword);

            // Update session to mark OTP as verified
            session.setAttribute("otpVerified", true);

            return ResponseEntity.ok(new StandardResponse("OTP verified successfully"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new StandardResponse("Invalid OTP"));
    }

    @GetMapping("/secret")
    public ResponseEntity<?> secretPlace() {
        if (!userService.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new StandardResponse("Please login first"));
        }

        // Check if OTP is verified in the current session
        if (session.getAttribute("otpVerified") == null ||
                !(boolean) session.getAttribute("otpVerified")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new StandardResponse("Please verify OTP first"));
        }

        return ResponseEntity.ok(new StandardResponse("Welcome to the secret page"));
    }
}