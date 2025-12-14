package springsecurity.lesson3explicitusernameandpassword.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springsecurity.lesson3explicitusernameandpassword.exceptions.DuplicateStudentException;
import springsecurity.lesson3explicitusernameandpassword.persistence.Mapper.StudentMapper;
import springsecurity.lesson3explicitusernameandpassword.persistence.dto.StudentRequestDto;
import springsecurity.lesson3explicitusernameandpassword.persistence.dto.StudentResponseDto;
import springsecurity.lesson3explicitusernameandpassword.persistence.entity.Student;
import springsecurity.lesson3explicitusernameandpassword.persistence.service.StudentService;
import springsecurity.lesson3explicitusernameandpassword.validation.OnCreate;

@Controller
@RequestMapping("/")
public class StudentController {

    private final StudentService studentService;

    //

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("users")
    public String list(Model model) {
        java.util.List<Student> students = studentService.findAll();
        model.addAttribute("students", students);
        return "users";
    }

    @GetMapping({"", "home"})
    public String home(Model model) {
        // Landing page (no list), with a button to proceed to registration
        return "home";
    }

    @GetMapping("users/form")
    public String createForm(Model model) {
        model.addAttribute("student", new StudentRequestDto());
        return "registration";
    }

    @PostMapping("users")
    public String create(@Validated(OnCreate.class) @ModelAttribute("student") StudentRequestDto studentDto, BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "registration";
        }
        try {
            Student toCreate = StudentMapper.toEntity(studentDto);
            Student saved = studentService.create(toCreate);
            ra.addFlashAttribute("message", "Student created successfuly");
            return "redirect:/users/" + saved.getId();
        } catch (DuplicateStudentException e) {
            model.addAttribute("warning", e.getMessage());
            return "registration";
        }
    }

    @GetMapping("users/{id}")
    public String view(@PathVariable Long id, Model model) {
        Student student = studentService.findById(id).orElse(null);
        if (student == null) {
            return "redirect:/home";
        }
        StudentResponseDto dto = StudentMapper.toResponseDto(student);
        model.addAttribute("student", dto);
        return "view";
    }

    @GetMapping("users/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Student student = studentService.findById(id).orElse(null);
        if (student == null) {
            return "redirect:/home";
        }
        StudentRequestDto dto = StudentMapper.toRequestDto(student);
        model.addAttribute("student", dto);
        return "registration";
    }

    @PostMapping("users/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("student") StudentRequestDto studentDto, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "registration";
        }
        studentDto.setId(id);
        Student toUpdate = StudentMapper.toEntity(studentDto);
        // Preserve existing password on update when not provided
        if (toUpdate.getPassword() == null || toUpdate.getPassword().isBlank()) {
            Student existing = studentService.findById(id).orElse(null);
            if (existing != null) {
                toUpdate.setPassword(existing.getPassword());
            }
        }
        studentService.modify(toUpdate);
        ra.addFlashAttribute("message", "User updated successfully");
        return "redirect:/users/" + id;
    }

    @GetMapping("users/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        studentService.deleteById(id);
        ra.addFlashAttribute("message", "User deleted successfully");
        return "redirect:/home";
    }

}
