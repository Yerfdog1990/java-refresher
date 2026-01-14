package springsecurity.lesson3springsecuritycustomexpressions.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.dto.StudentDTO;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.*;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.SecurityQuestionDefinitionRepository;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.SecurityQuestionRepository;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.service.IStudentService;
import springsecurity.lesson3springsecuritycustomexpressions.registration.OnRegistrationCompleteEvent;
import springsecurity.lesson3springsecuritycustomexpressions.validation.EmailExistsException;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class RegistrationController {

    private final IStudentService studentService;
    private final ApplicationEventPublisher eventPublisher;
    private final JavaMailSender mailSender;
    private final Environment env;
    private final SecurityQuestionDefinitionRepository securityQuestionDefinitionRepository;
    private final SecurityQuestionRepository securityQuestionRepository;

    @Autowired
    public RegistrationController(IStudentService studentService, ApplicationEventPublisher eventPublisher, JavaMailSender mailSender, Environment env, SecurityQuestionDefinitionRepository securityQuestionDefinitionRepository, SecurityQuestionRepository securityQuestionRepository) {
        this.studentService = studentService;
        this.eventPublisher = eventPublisher;
        this.mailSender = mailSender;
        this.env = env;
        this.securityQuestionDefinitionRepository = securityQuestionDefinitionRepository;
        this.securityQuestionRepository = securityQuestionRepository;
    }

    @GetMapping("/signup")
    public ModelAndView registrationForm() {
        Map<String, Object> model = new HashMap<>();
        model.put("student", new StudentDTO());  // Changed from "student" to "studentDTO"
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

    // Check authorities the user has when authenticated
    @GetMapping("/debug/me")
    @ResponseBody
    public Map<String, Object> debugMe(Authentication auth) {
        Map<String, Object> info = new HashMap<>();
        info.put("name", auth.getName());
        info.put("authorities", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        info.put("details", auth.getDetails());
        info.put("principal", auth.getPrincipal());
        return info;
    }

    @GetMapping("/debug/user")
    @ResponseBody
    public String debugUser(Authentication authentication) {
        return "User: " + authentication.getName() +
                ", Authorities: " + authentication.getAuthorities();
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
        return new ModelAndView("users", "students", studentService.findAllStudents());
    }


    @GetMapping("/users/{id}")
    public ModelAndView showUser(@PathVariable("id") Long id) {
        StudentDTO studentDTO = studentService.findById(id);
        return new ModelAndView("view", "student", studentDTO);
    }

    @GetMapping("/users/{id}/edit")
    public ModelAndView editUserForm(@PathVariable("id") Long id) {
        StudentDTO student = studentService.findById(id);
        Map<String, Object> model = new HashMap<>();
        model.put("student", student);
        model.put("questions", securityQuestionDefinitionRepository.findAll());
        return new ModelAndView("registrationPage", model);
    }

    @PostMapping("/users/{id}/delete")
    public ModelAndView deleteUser(@PathVariable("id") Long id) {
        studentService.deleteById(id);
        return new ModelAndView("redirect:/users");
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex, RedirectAttributes redirectAttributes) {
        String message = ex.getMessage() != null ?
                ex.getMessage() : "You don't have permission to perform this action. Please contact your administrator.";
        redirectAttributes.addFlashAttribute("errorMessage", message);
        return new ModelAndView("redirect:/users");
    }

    @PostMapping("/users/{id}/edit")
    public ModelAndView editUser(
            @PathVariable("id") Long id,
            @Valid final Student student,
            final BindingResult result,
            @RequestParam(value = "roleNames", required = false) List<String> roleNames) {
        if (result.hasErrors()) {
            Map<String, Object> model = new HashMap<>();
            model.put("student", student);
            model.put("questions", securityQuestionDefinitionRepository.findAll());
            return new ModelAndView("registrationPage", model);
        }
        try {
            student.setId(id);
            if (roleNames != null) {
                student.getAuthorities().clear();
                for (String auth : roleNames) {
                    student.getAuthorities().add(new Authority(student, auth));
                }
            }
            studentService.updateExistingStudent(student);
        } catch (EmailExistsException e) {
            result.addError(new FieldError("student", "email", e.getMessage()));
            Map<String, Object> model = new HashMap<>();
            model.put("student", student);
            model.put("questions", securityQuestionDefinitionRepository.findAll());
            return new ModelAndView("registrationPage", model);
        }
        return new ModelAndView("redirect:/users");
    }

    @PostMapping("/student/register")
    public ModelAndView registerNewStudent(
            @Valid @ModelAttribute("student") StudentDTO student,
            BindingResult result,
            @RequestParam(value = "questionId", required = false) Long questionId,
            @RequestParam(value = "answer", required = false) String answer,
            @RequestParam(value = "roleNames", required = false) List<String> roleNames,
            HttpServletRequest request) {

        if (result.hasErrors()) {
            Map<String, Object> model = new HashMap<>();
            model.put("student", student);
            model.put("questions", securityQuestionDefinitionRepository.findAll());
            return new ModelAndView("registrationPage", model);
        }
        try {
            final Student registered = studentService.registerNewStudent(student);
            if (questionId != null && answer != null) {
                final SecurityQuestionDefinition questionDefinition = securityQuestionDefinitionRepository.findById(questionId).orElseThrow(() -> new RuntimeException("Security Question Definition not found"));
                securityQuestionRepository.save(new SecurityQuestion(registered, questionDefinition, answer));
            }
            final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, appUrl));
        } catch (EmailExistsException e) {
            result.addError(new FieldError("student", "email", e.getMessage()));
            Map<String, Object> model = new HashMap<>();
            model.put("student", student);
            model.put("questions", securityQuestionDefinitionRepository.findAll());
            return new ModelAndView("registrationPage", model);
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
            final PasswordResetToken passToken = studentService.getPasswordResetToken(token);
            Map<String, Object> model = new HashMap<>();
            model.put("errorMessage", "Passwords do not match");
            model.put("questions", securityQuestionDefinitionRepository.findAll());
            model.put("token", token);
            if (passToken != null) {
                model.put("email", passToken.getStudent().getEmail());
            }
            return new ModelAndView("resetPassword", model);
        }

        try {
            final PasswordResetToken passToken = studentService.getPasswordResetToken(token);
            if (passToken == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid password reset token");
                return new ModelAndView("redirect:/login");
            }

            final Student student = passToken.getStudent();

            // Check if password contains email
            if (password != null && student.getEmail() != null) {
                String lowerPassword = password.toLowerCase();
                String lowerEmail = student.getEmail().toLowerCase();
                String localPart = lowerEmail.split("@")[0];
                if (lowerPassword.contains(lowerEmail) || lowerPassword.contains(localPart)) {
                    Map<String, Object> model = new HashMap<>();
                    model.put("errorMessage", "Your password cannot contain your email");
                    model.put("questions", securityQuestionDefinitionRepository.findAll());
                    model.put("token", token);
                    model.put("email", student.getEmail());
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
                    .findByQuestionDefinitionIdAndStudentIdAndAnswer(questionId, student.getId(), answer);

            if (securityQuestion == null) {
                Map<String, Object> model = new HashMap<>();
                model.put("errorMessage", "Incorrect answer to security question");
                model.put("questions", securityQuestionDefinitionRepository.findAll());
                model.put("token", token);
                model.put("email", student.getEmail());
                return new ModelAndView("resetPassword", model);
            }

            // Update the password
            studentService.changeUserPassword(student, password);
            redirectAttributes.addFlashAttribute("message", "Password reset successfully");
            return new ModelAndView("redirect:/login?resetSuccess");
        } catch (Exception e) {
            final PasswordResetToken passToken = studentService.getPasswordResetToken(token);
            Map<String, Object> model = new HashMap<>();
            model.put("errorMessage", "An error occurred: " + e.getMessage());
            model.put("questions", securityQuestionDefinitionRepository.findAll());
            model.put("token", token);
            if (passToken != null) {
                model.put("email", passToken.getStudent().getEmail());
            }
            return new ModelAndView("resetPassword", model);
        }
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