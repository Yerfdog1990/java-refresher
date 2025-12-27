package springsecurity.lesson3activatenewaccountviaemail.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springsecurity.lesson3activatenewaccountviaemail.persistance.model.Student;
import springsecurity.lesson3activatenewaccountviaemail.persistance.model.VerificationToken;
import springsecurity.lesson3activatenewaccountviaemail.persistance.service.IStudentService;
import springsecurity.lesson3activatenewaccountviaemail.registration.OnRegistrationCompleteEvent;
import springsecurity.lesson3activatenewaccountviaemail.validation.EmailExistsException;

import java.util.Calendar;

@Controller
public class RegistrationController {

    private final IStudentService studentService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public RegistrationController(IStudentService studentService, ApplicationEventPublisher eventPublisher) {
        this.studentService = studentService;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/signup")
    public ModelAndView registrationForm() {
        return new ModelAndView("registrationPage", "student", new Student());
    }

    @GetMapping("/login")
    public String login() {
        return "loginPage";
    }

    @GetMapping("/activation")
    public String activation() {
        return "activation";
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

    @GetMapping(value = "/registrationConfirm")
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
}