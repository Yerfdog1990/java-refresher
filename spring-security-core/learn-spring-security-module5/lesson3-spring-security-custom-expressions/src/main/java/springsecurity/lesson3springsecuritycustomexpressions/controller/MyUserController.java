package springsecurity.lesson3springsecuritycustomexpressions.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.*;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.SecurityQuestionDefinitionRepository;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.SecurityQuestionRepository;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.service.IMyUserService;
import springsecurity.lesson3springsecuritycustomexpressions.registration.OnRegistrationCompleteEvent;
import springsecurity.lesson3springsecuritycustomexpressions.validation.EmailExistsException;

import java.util.*;

@Controller
public class MyUserController {

    private final IMyUserService myuserService;
    private final ApplicationEventPublisher eventPublisher;
    private final JavaMailSender mailSender;
    private final Environment env;
    private final SecurityQuestionDefinitionRepository securityQuestionDefinitionRepository;
    private final SecurityQuestionRepository securityQuestionRepository;

    @Autowired
    public MyUserController(IMyUserService myuserService, ApplicationEventPublisher eventPublisher, JavaMailSender mailSender, Environment env, SecurityQuestionDefinitionRepository securityQuestionDefinitionRepository, SecurityQuestionRepository securityQuestionRepository) {
        this.myuserService = myuserService;
        this.eventPublisher = eventPublisher;
        this.mailSender = mailSender;
        this.env = env;
        this.securityQuestionDefinitionRepository = securityQuestionDefinitionRepository;
        this.securityQuestionRepository = securityQuestionRepository;
    }

    @GetMapping("/signup")
    public ModelAndView registrationForm() {
        Map<String, Object> model = new HashMap<>();
        model.put("myuser", new MyUser());
        model.put("questions", securityQuestionDefinitionRepository.findAll());
        return new ModelAndView("registrationPage", model);
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

    // The username field on the MyUser Entity returned matches the currently authenticated user's username
    @PostAuthorize("returnObject.username==authentication.principal.username")
    @GetMapping("/users")
    public ModelAndView showUsers() {
        return new ModelAndView("users", "myusers", myuserService.findAll());
    }

    // The username field on the MyUser Entity returned matches the currently authenticated user's username
    @PostAuthorize("returnObject.username==authentication.principal.username")
    @GetMapping("/users/{id}")
    public ModelAndView showUser(@PathVariable("id") Long id) {
        MyUser myUser = myuserService.findById(id);
        return new ModelAndView("view", "myuser", myUser);
    }

    @GetMapping("/users/{id}/edit")
    public ModelAndView editUserForm(@PathVariable("id") Long id) {
        MyUser myUser = myuserService.findById(id);
        Map<String, Object> model = new HashMap<>();
        model.put("myuser", myUser);
        model.put("questions", securityQuestionDefinitionRepository.findAll());
        return new ModelAndView("registrationPage", model);
    }

    @PostMapping("/users/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView deleteUser(@PathVariable("id") Long id) {
        myuserService.deleteById(id);
        return new ModelAndView("redirect:/users");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "You have no previlage to delete any user! Kindly contact the admin");
        return new ModelAndView("redirect:/users");
    }

    @PostMapping("/users/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView editUser(@PathVariable("id") Long id, @Valid final MyUser myUser, final BindingResult result, @RequestParam(value = "roleNames", required = false) List<String> roleNames) {
        if (result.hasErrors()) {
            Map<String, Object> model = new HashMap<>();
            model.put("myuser", myUser);
            model.put("questions", securityQuestionDefinitionRepository.findAll());
            return new ModelAndView("registrationPage", model);
        }
        try {
            myUser.setId(id);
            if (roleNames != null) {
                myUser.getAuthorities().clear();
                for (String auth : roleNames) {
                    myUser.getAuthorities().add(new Authority(myUser, auth));
                }
            }
            myuserService.updateExistingMyUser(myUser);
        } catch (EmailExistsException e) {
            result.addError(new FieldError("myUser", "email", e.getMessage()));
            Map<String, Object> model = new HashMap<>();
            model.put("myuser", myUser);
            model.put("questions", securityQuestionDefinitionRepository.findAll());
            return new ModelAndView("registrationPage", model);
        }
        return new ModelAndView("redirect:/users");
    }

    @PostMapping("/myuser/register")
    public ModelAndView registerNewmyuser(@Valid final MyUser myUser, final BindingResult result, final @RequestParam(required = false) Long questionId, @RequestParam(required = false) final String answer, @RequestParam(value = "roleNames", required = false) List<String> roleNames, final HttpServletRequest request, final RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Map<String, Object> model = new HashMap<>();
            model.put("myuser", myUser);
            model.put("questions", securityQuestionDefinitionRepository.findAll());
            return new ModelAndView("registrationPage", model);
        }
        try {
            if (roleNames != null) {
                for (String auth : roleNames) {
                    myUser.getAuthorities().add(new Authority(myUser, auth));
                }
            }
            final MyUser registered = myuserService.registerNewMyUser(myUser);
            if (questionId != null && answer != null) {
                final SecurityQuestionDefinition questionDefinition = securityQuestionDefinitionRepository.findById(questionId).orElseThrow(() -> new RuntimeException("Security Question Definition not found"));
                securityQuestionRepository.save(new SecurityQuestion(registered, questionDefinition, answer));
            }
            final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, appUrl));
        } catch (EmailExistsException e) {
            result.addError(new FieldError("myUser", "email", e.getMessage()));
            Map<String, Object> model = new HashMap<>();
            model.put("myuser", myUser);
            model.put("questions", securityQuestionDefinitionRepository.findAll());
            return new ModelAndView("registrationPage", model);
        }
        return new ModelAndView("redirect:/activation");
    }

    @GetMapping("/registrationConfirm")
    public ModelAndView confirmRegistration(final Model model, @RequestParam("token") final String token, final RedirectAttributes redirectAttributes) {
        final VerificationToken verificationToken = myuserService.getVerificationToken(token);
        if (verificationToken == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid account confirmation token.");
            return new ModelAndView("redirect:/login");
        }

        final MyUser myUser = verificationToken.getMyUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your registration token has expired. Please register again.");
            return new ModelAndView("redirect:/login");
        }

        myuserService.enableRegisteredMyUser(myUser);
        return new ModelAndView("redirect:/authenticated");
    }

    // password reset
    @PostMapping("/myuser/resetPassword")
    public ModelAndView resetPassword(final HttpServletRequest request, @RequestParam("email") final String userEmail, final RedirectAttributes redirectAttributes) {
        final MyUser myUser = myuserService.findUserByEmail(userEmail);
        if (myUser != null) {
            final String token = UUID.randomUUID().toString();
            myuserService.createPasswordResetTokenForUser(myUser, token);
            final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            final SimpleMailMessage email = constructResetTokenEmail(appUrl, token, myUser);
            mailSender.send(email);
        }

        redirectAttributes.addFlashAttribute("message", "You should receive an Password Reset Email shortly");
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/myuser/changePassword")
    public ModelAndView showChangePasswordPage(@RequestParam("id") final long id, @RequestParam("token") final String token, final RedirectAttributes redirectAttributes) {
        final PasswordResetToken passToken = myuserService.getPasswordResetToken(token);
        if (passToken == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid password reset token");
            return new ModelAndView("redirect:/login");
        }
        final MyUser user = passToken.getMyUser();
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
        view.addObject("questions", securityQuestionDefinitionRepository.findAll());
        view.addObject("email", user.getEmail());
        return view;
    }
    
    @PostMapping("/user/savePassword")
    public ModelAndView savePassword(
            @RequestParam("password") final String password,
            @RequestParam("passwordConfirmation") final String passwordConfirmation,
            @RequestParam final Long questionId,
            @RequestParam final String answer,
            @RequestParam("token") final String token,
            final RedirectAttributes redirectAttributes) {
        // Check if passwords match
        if (!password.equals(passwordConfirmation)) {
            final PasswordResetToken passToken = myuserService.getPasswordResetToken(token);
            Map<String, Object> model = new HashMap<>();
            model.put("errorMessage", "Passwords do not match");
            model.put("questions", securityQuestionDefinitionRepository.findAll());
            model.put("token", token);
            if (passToken != null) {
                model.put("email", passToken.getMyUser().getEmail());
            }
            return new ModelAndView("resetPassword", model);
        }

        try {
            final PasswordResetToken passToken = myuserService.getPasswordResetToken(token);
            if (passToken == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid password reset token");
                return new ModelAndView("redirect:/login");
            }

            final MyUser myUser = passToken.getMyUser();

            // Check if password contains email
            if (password != null && myUser.getEmail() != null) {
                String lowerPassword = password.toLowerCase();
                String lowerEmail = myUser.getEmail().toLowerCase();
                String localPart = lowerEmail.split("@")[0];
                if (lowerPassword.contains(lowerEmail) || lowerPassword.contains(localPart)) {
                    Map<String, Object> model = new HashMap<>();
                    model.put("errorMessage", "Your password cannot contain your email");
                    model.put("questions", securityQuestionDefinitionRepository.findAll());
                    model.put("token", token);
                    model.put("email", myUser.getEmail());
                    return new ModelAndView("resetPassword", model);
                }
            }

            final Calendar cal = Calendar.getInstance();
            if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Your password reset token has expired");
                return new ModelAndView("redirect:/login");
            }

            // Verify the security question and answer
            SecurityQuestion securityQuestion = securityQuestionRepository
                    .findByQuestionDefinitionIdAndMyUserIdAndAnswer(questionId, myUser.getId(), answer);

            if (securityQuestion == null) {
                Map<String, Object> model = new HashMap<>();
                model.put("errorMessage", "Incorrect answer to security question");
                model.put("questions", securityQuestionDefinitionRepository.findAll());
                model.put("token", token);
                model.put("email", myUser.getEmail());
                return new ModelAndView("resetPassword", model);
            }

            // Update the password
            myuserService.changeUserPassword(myUser, password);
            redirectAttributes.addFlashAttribute("message", "Password reset successfully");
            return new ModelAndView("redirect:/login?resetSuccess");
        } catch (Exception e) {
            final PasswordResetToken passToken = myuserService.getPasswordResetToken(token);
            Map<String, Object> model = new HashMap<>();
            model.put("errorMessage", "An error occurred: " + e.getMessage());
            model.put("questions", securityQuestionDefinitionRepository.findAll());
            model.put("token", token);
            if (passToken != null) {
                model.put("email", passToken.getMyUser().getEmail());
            }
            return new ModelAndView("resetPassword", model);
        }
    }
    // NON-API

    private SimpleMailMessage constructResetTokenEmail(final String contextPath, final String token, final MyUser myUser) {
        final String url = contextPath + "/myUser/changePassword?id=" + myUser.getId() + "&token=" + token;
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(myUser.getEmail());
        email.setSubject("Reset Password");
        email.setText("Please open the following URL to reset your password: \r\n" + url);
        email.setFrom(env.getProperty("spring.mail.username"));
        return email;
    }
}