package springsecurity.l3springsecurityannotations.controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    // Admin can create student and teacher
    @Secured("ROLE_ADMIN")
    @PostMapping("/registerStudent")
    public String registerStudent() {
        return "Student registered successfully!";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/registerTeacher")
    public String registerTeacher() {
        return "Teacher registered successfully!";
    }

    // Teacher and Admin can enter/update grades
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping("/enterGrade")
    public String enterGrade() {
        return "Grade entered successfully!";
    }

    // Read operations
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/getStudents")
    public String getStudents() {
        return "Students fetched successfully!";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/getTeachers")
    public String getTeachers() {
        return "Teachers fetched successfully!";
    }

    // All authenticated users can get subjects and classes
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getSubjects")
    public String getSubjects() {
        return "Subjects fetched successfully!";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getSubjects/{id}")
    public String getSubjectsById(@PathVariable Long id) {
        return "Subjects fetched successfully for class id: " + id;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getClasses")
    public String getClasses() {
        return "Classes fetched successfully!";
    }

    // Update operations - Admin only
    @RolesAllowed("ADMIN")
    @PutMapping("/updateStudent")
    public String updateStudent() {
        return "Student updated successfully!";
    }

    @RolesAllowed("ADMIN")
    @PutMapping("/updateTeacher")
    public String updateTeacher() {
        return "Teacher updated successfully!";
    }

    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/updateGrade")
    public String updateGrade() {
        return "Grade updated successfully!";
    }

    // Delete operations - Admin only
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/deleteStudent")
    public String deleteStudent() {
        return "Student deleted successfully!";
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/deleteTeacher")
    public String deleteTeacher() {
        return "Teacher deleted successfully!";
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/deleteClass")
    public String deleteClass() {
        return "Class deleted successfully!";
    }
}