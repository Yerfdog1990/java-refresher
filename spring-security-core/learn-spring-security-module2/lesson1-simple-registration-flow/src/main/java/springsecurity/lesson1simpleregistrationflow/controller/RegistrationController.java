package springsecurity.lesson1simpleregistrationflow.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import springsecurity.lesson1simpleregistrationflow.persistance.model.Student;
import springsecurity.lesson1simpleregistrationflow.persistance.service.IStudentService;
import springsecurity.lesson1simpleregistrationflow.validation.EmailExistsException;

@Controller
public class RegistrationController {

    @Autowired
    private IStudentService studentService;

    @GetMapping("/signup")
    public ModelAndView registrationForm() {
        return new ModelAndView("registrationPage", "student", new Student());
    }

    @GetMapping("/login")
    public String login() {
        return "loginPage";
    }

    @GetMapping("/users")
    public ModelAndView showUsers() {
        return new ModelAndView("users", "students", studentService.findAll());
    }

    @PostMapping("/student/register")
    public ModelAndView registerNewStudent(@Valid final Student student, final BindingResult result) {
        if (result.hasErrors()) {
            return new ModelAndView("registrationPage", "student", student);
        }
        try {
            studentService.registerNewStudent(student);
        } catch (EmailExistsException e) {
            result.addError(new FieldError("student", "email", e.getMessage()));
            return new ModelAndView("registrationPage", "student", student);
        }
        return new ModelAndView("redirect:/login");
    }

}