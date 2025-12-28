package springsecurity.lesson4forgotpassword.controller;


import com.google.common.collect.ImmutableMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springsecurity.lesson4forgotpassword.persistance.model.PasswordResetToken;
import springsecurity.lesson4forgotpassword.persistance.model.Student;
import springsecurity.lesson4forgotpassword.persistance.model.VerificationToken;
import springsecurity.lesson4forgotpassword.persistance.service.IStudentService;
import springsecurity.lesson4forgotpassword.registration.OnRegistrationCompleteEvent;
import springsecurity.lesson4forgotpassword.validation.EmailExistsException;

import java.util.Calendar;
import java.util.UUID;

@Controller
public class RegistrationController {

    private final IStudentService studentService;
    private final ApplicationEventPublisher eventPublisher;
    private final JavaMailSender mailSender;
    private final Environment env;

    @Autowired
    public RegistrationController(IStudentService studentService, ApplicationEventPublisher eventPublisher, JavaMailSender mailSender, Environment env) {
        this.studentService = studentService;
        this.eventPublisher = eventPublisher;
        this.mailSender = mailSender;
        this.env = env;
    }

    @GetMapping("/signup")
    public ModelAndView registrationForm() {
        return new ModelAndView("registrationPage", "student", new Student());
    }

    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "loginPage";
    }

    @GetMapping("/activation")
    public String activation() {
        return "activation";
    }

    @GetMapping("/forgotPassword")
    public String forgotPassword() {
        return "forgotPassword";
    }

    @GetMapping("/authenticated")
    public String authenticated() {
        return "authenticated";
    }

    @GetMapping("/users")
    public ModelAndView showUsers() {
        return new ModelAndView("users", "students", studentService.findAll());
    }

    @GetMapping("/users/{id}")
    public ModelAndView showUser(@PathVariable("id") Long id) {
        Student student = studentService.findById(id);
        return new ModelAndView("view", "student", student);
    }

    @GetMapping("/users/{id}/edit")
    public ModelAndView editUserForm(@PathVariable("id") Long id) {
        Student student = studentService.findById(id);
        return new ModelAndView("registrationPage", "student", student);
    }

    @PostMapping("/users/{id}/delete")
    public ModelAndView deleteUser(@PathVariable("id") Long id) {
        studentService.deleteById(id);
        return new ModelAndView("redirect:/users");
    }

    @PostMapping("/users/{id}/edit")
    public ModelAndView editUser(@PathVariable("id") Long id, @Valid final Student student, final BindingResult result) {
        if (result.hasErrors()) {
            return new ModelAndView("registrationPage", "student", student);
        }
        try {
            student.setId(id);
            studentService.updateExistingStudent(student);
        } catch (EmailExistsException e) {
            result.addError(new FieldError("student", "email", e.getMessage()));
            return new ModelAndView("registrationPage", "student", student);
        }
        return new ModelAndView("redirect:/users");
    }

    @PostMapping("/student/register")
    public ModelAndView registerNewStudent(@Valid final Student student, final BindingResult result, final HttpServletRequest request, final RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return new ModelAndView("registrationPage", "student", student);
        }
        try {
            final Student registered = studentService.registerNewStudent(student);
            final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, appUrl));
        } catch (EmailExistsException e) {
            result.addError(new FieldError("student", "email", e.getMessage()));
            return new ModelAndView("registrationPage", "student", student);
        }
        return new ModelAndView("redirect:/activation");
    }

    @GetMapping("/registrationConfirm")
    public ModelAndView confirmRegistration(final Model model, @RequestParam("token") final String token, final RedirectAttributes redirectAttributes) {
        final VerificationToken verificationToken = studentService.getVerificationToken(token);
        if (verificationToken == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid account confirmation token.");
            return new ModelAndView("redirect:/login");
        }

        final Student student = verificationToken.getStudent();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your registration token has expired. Please register again.");
            return new ModelAndView("redirect:/login");
        }

        studentService.enableRegisteredStudent(student);
        return new ModelAndView("redirect:/authenticated");
    }

    // password reset
    @PostMapping("/student/resetPassword")
    public ModelAndView resetPassword(final HttpServletRequest request, @RequestParam("email") final String userEmail, final RedirectAttributes redirectAttributes) {
        final Student student = studentService.findUserByEmail(userEmail);
        if (student != null) {
            final String token = UUID.randomUUID().toString();
            studentService.createPasswordResetTokenForUser(student, token);
            final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            final SimpleMailMessage email = constructResetTokenEmail(appUrl, token, student);
            mailSender.send(email);
        }

        redirectAttributes.addFlashAttribute("message", "You should receive an Password Reset Email shortly");
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/student/changePassword")
    public ModelAndView showChangePasswordPage(@RequestParam("id") final long id, @RequestParam("token") final String token, final RedirectAttributes redirectAttributes) {
        final PasswordResetToken passToken = studentService.getPasswordResetToken(token);
        if (passToken == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid password reset token");
            return new ModelAndView("redirect:/login");
        }
        final Student user = passToken.getStudent();
        if (user.getId() != id) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid password reset token");
            return new ModelAndView("redirect:/login");
        }

        final Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your password reset token has expired");
            return new ModelAndView("redirect:/login");
        }

        final ModelAndView view = new ModelAndView("resetPassword");
        view.addObject("token", token);
        return view;
    }

    @PostMapping("/user/savePassword")
    public ModelAndView savePassword(@RequestParam("password") final String password, @RequestParam("passwordConfirmation") final String passwordConfirmation, @RequestParam("token") final String token, final RedirectAttributes redirectAttributes) {
        if (!password.equals(passwordConfirmation)) {
            return new ModelAndView("resetPassword", ImmutableMap.of("errorMessage", "Passwords do not match"));
        }
        final PasswordResetToken p = studentService.getPasswordResetToken(token);
        if (p == null) {
            redirectAttributes.addFlashAttribute("message", "Invalid token");
        } else {
            final Student student = p.getStudent();
            if (student == null) {
                redirectAttributes.addFlashAttribute("message", "Unknown user");
            } else {
                studentService.changeUserPassword(student, password);
                redirectAttributes.addFlashAttribute("message", "Password reset successfully");
            }
        }
        return new ModelAndView("redirect:/login");
    }
    // NON-API

    private SimpleMailMessage constructResetTokenEmail(final String contextPath, final String token, final Student student) {
        final String url = contextPath + "/student/changePassword?id=" + student.getId() + "&token=" + token;
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(student.getEmail());
        email.setSubject("Reset Password");
        email.setText("Please open the following URL to reset your password: \r\n" + url);
        email.setFrom(env.getProperty("spring.mail.username"));
        return email;
    }
}