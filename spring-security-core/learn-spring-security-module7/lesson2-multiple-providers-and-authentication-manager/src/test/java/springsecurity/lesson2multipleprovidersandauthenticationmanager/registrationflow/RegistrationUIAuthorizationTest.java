package springsecurity.lesson2multipleprovidersandauthenticationmanager.registrationflow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;

import springsecurity.lesson2multipleprovidersandauthenticationmanager.persistance.model.Student;
import springsecurity.lesson2multipleprovidersandauthenticationmanager.persistance.repository.IStudentRepository;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class RegistrationUIAuthorizationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private IStudentRepository studentRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithUserDetails("bob@example.com")
    public void givenAdminUser_whenGetSignup_thenShowRolesRadioButtons() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"roleNames\"")))
                .andExpect(content().string(containsString("type=\"radio\"")));
    }

    @Test
    public void givenAnonymousUser_whenGetSignup_thenDoNotShowRolesRadioButtons() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("name=\"roleNames\""))));
    }

    @Test
    @WithUserDetails("alice@example.com")
    public void givenRegularUser_whenGetSignup_thenDoNotShowRolesRadioButtons() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("name=\"roleNames\""))));
    }

    @Test
    public void givenNewRegistration_whenRegister_thenDefaultToRoleUser() throws Exception {
        String email = "newuser@test.com";
        mockMvc.perform(post("/student/register")
                .param("username", "newuser")
                .param("email", email)
                .param("password", "Password123!")
                .param("passwordConfirmation", "Password123!")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/activation"));

        Student student = studentRepository.findByEmail(email);
        assertNotNull(student);
        assertEquals(1, student.getAuthorities().size());
        assertTrue(student.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @WithUserDetails("bob@example.com")
    public void givenAdminUser_whenRegisterWithAdminRole_thenSaveAdminRole() throws Exception {
        String email = "newadmin@test.com";
        mockMvc.perform(post("/student/register")
                .param("username", "newadmin")
                .param("email", email)
                .param("password", "Password123!")
                .param("passwordConfirmation", "Password123!")
                .param("questionId", "1")
                .param("answer", "test answer")
                .param("roleNames", "ROLE_ADMIN", "ROLE_USER")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/activation"));

        Student student = studentRepository.findByEmail(email);
        assertNotNull(student);
        assertEquals(2, student.getAuthorities().size());
        assertTrue(student.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(student.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @WithUserDetails("bob@example.com")
    public void givenAdminUser_whenEditUserRole_thenUpdateRole() throws Exception {
        // Alice is user 1 (based on data.sql)
        Long aliceId = 1L;
        mockMvc.perform(post("/users/" + aliceId + "/edit")
                .param("username", "AliceUpdated")
                .param("email", "alice@example.com")
                .param("password", "Alice123!")
                .param("passwordConfirmation", "Alice123!")
                .param("roleNames", "ROLE_ADMIN")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        Student student = studentRepository.findById(aliceId).orElseThrow();
        assertEquals(1, student.getAuthorities().size());
        assertTrue(student.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}
