package springsecurity.lesson2datastructureofacl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import springsecurity.lesson2datastructureofacl.persistence.entity.Student;
import springsecurity.lesson2datastructureofacl.service.StudentService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class StudentServiceSecurityTest {

    @Autowired
    private StudentService studentService;

    @Test
    @WithMockUser(username = "bob@example.com", roles = {"ADMIN"})
    public void givenAdmin_whenFindAll_thenAllStudentsReturned() {
        List<Student> students = studentService.findAll();
        // Based on data.sql, there are 4 students
        assertFalse(students.isEmpty());
        assertTrue(students.stream().anyMatch(s -> s.getEmail().equals("alice@example.com")));
        assertTrue(students.stream().anyMatch(s -> s.getEmail().equals("bob@example.com")));
    }

    @Test
    @WithMockUser(username = "carol@example.com", roles = {"USER"})
    public void givenUser_whenFindAll_thenOnlyAllowedStudentsReturned() {
        List<Student> students = studentService.findAll();
        // Carol (ROLE_USER) should see all students because we gave ROLE_USER READ on all students in data.sql
        assertFalse(students.isEmpty());
        assertTrue(students.stream().anyMatch(s -> s.getEmail().equals("alice@example.com")));
    }

    @Test
    @WithMockUser(username = "carol@example.com", roles = {"USER"})
    public void givenUser_whenFindByIdAlice_thenSuccess() {
        Optional<Student> student = studentService.findById(1L);
        assertTrue(student.isPresent());
        assertEquals("alice@example.com", student.get().getEmail());
    }

    @Test
    @WithMockUser(username = "bob@example.com", roles = {"ADMIN"})
    public void givenAdmin_whenDeleteStudent_thenSuccess() {
        studentService.deleteById(1L);
        // If no exception, it's a success
    }

    @Test
    @WithMockUser(username = "carol@example.com", roles = {"USER"})
    public void givenUser_whenDeleteStudent_thenFail() {
        assertThrows(AccessDeniedException.class, () -> studentService.deleteById(1L));
    }

    @Test
    @WithMockUser(username = "carol@example.com", roles = {"USER"})
    public void givenUser_whenModifyAlex_thenFail() {
        Student alex = new Student();
        alex.setId(3L);
        alex.setEmail("alex@example.com");
        
        assertThrows(AccessDeniedException.class, () -> studentService.modify(alex));
    }

    @Test
    @WithMockUser(username = "bob@example.com", roles = {"ADMIN"})
    public void givenAdmin_whenCreateStudent_thenSuccess() {
        Student newStudent = new Student();
        newStudent.setUsername("Dave");
        newStudent.setEmail("dave@example.com");
        newStudent.setPassword("dave123");
        
        Student saved = studentService.create(newStudent);
        assertNotNull(saved.getId());
    }

    @Test
    @WithMockUser(username = "carol@example.com", roles = {"USER"})
    public void givenUser_whenCreateStudent_thenFail() {
        Student newStudent = new Student();
        newStudent.setUsername("Dave");
        newStudent.setEmail("dave@example.com");
        newStudent.setPassword("dave123");
        
        assertThrows(AccessDeniedException.class, () -> studentService.create(newStudent));
    }
}
