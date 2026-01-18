package springsecurity.lesson1topologyofrolesandprivileges.rolehierarchy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.dto.StudentDTO;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.model.Privilege;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.model.Role;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.model.Student;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.repository.IStudentRepository;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.service.StudentDetailsService;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.service.StudentService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class RoleHierarchyTest {

    @MockitoBean
    private IStudentRepository studentRepository;

    @MockitoBean
    private StudentService studentService;

    @Autowired
    private StudentDetailsService studentDetailsService;

    @Test
    @WithMockUser(roles = "USER")
    void givenExistingEmail_whenLoadingUserByUsername_thenUserDetailsAreReturned() {
        // Given
        Student student = new Student();
        student.setEmail("test@example.com");
        student.setPassword("encodedPassword");
        student.setEnabled(true);

        Role userRole = new Role("ROLE_USER");
        Privilege readPrivilege = new Privilege("READ_PRIVILEGE");
        userRole.setPrivileges(Set.of(readPrivilege));
        student.setRoles(Set.of(userRole));

        when(studentRepository.findByEmail("test@example.com")).thenReturn(student);

        // When
        UserDetails userDetails = studentDetailsService.loadUserByUsername("test@example.com");

        // Then
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("READ_PRIVILEGE")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenAdminRole_whenLoadingUserByUsername_thenHasAllPrivileges() {
        // Given
        Student admin = new Student();
        admin.setEmail("admin@example.com");
        admin.setPassword("adminPass");
        admin.setEnabled(true);

        Role adminRole = new Role("ROLE_ADMIN");
        adminRole.setPrivileges(Set.of(new Privilege("WRITE_PRIVILEGE"), new Privilege("READ_PRIVILEGE")));
        admin.setRoles(Set.of(adminRole));

        when(studentRepository.findByEmail("admin@example.com")).thenReturn(admin);

        // When
        UserDetails userDetails = studentDetailsService.loadUserByUsername("admin@example.com");

        // Then
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertTrue(authorities.stream()
                .anyMatch(auth -> Objects.equals(auth.getAuthority(), "ROLE_ADMIN")));
        assertTrue(authorities.stream()
                .anyMatch(auth -> Objects.equals(auth.getAuthority(), "WRITE_PRIVILEGE")));
        assertTrue(authorities.stream()
                .anyMatch(auth -> Objects.equals(auth.getAuthority(), "READ_PRIVILEGE")));
    }

    @Test
    void givenNonExistentEmail_whenLoadingUserByUsername_thenThrowsUsernameNotFoundException() {
        // Given
        when(studentRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        // When & Then
        assertThrows(UsernameNotFoundException.class, () ->
                studentDetailsService.loadUserByUsername("nonexistent@example.com")
        );
    }

}