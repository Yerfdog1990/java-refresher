package springsecurity.lesson1topologyofrolesandprivileges.userspage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.model.Student;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.model.VerificationToken;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.repository.IStudentRepository;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.repository.VerificationTokenRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class UsersPageReproductionTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private IStudentRepository studentRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void givenNewRegistration_whenConfirmingEmail_thenStudentShouldBeEnabledInDB() throws Exception {
        String email = "repro@test.com";
        String password = "Password123!";

        // 1. Register
        mockMvc.perform(post("/student/register")
                .param("username", "repro")
                .param("email", email)
                .param("password", password)
                .param("passwordConfirmation", password)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/activation"));

        // 2. Verify it's disabled in DB
        Student student = studentRepository.findByEmail(email);
        assertFalse(student.isEnabled(), "Student should be disabled after registration");

        // 3. Find the token
        List<VerificationToken> tokens = tokenRepository.findAll();
        VerificationToken tokenObj = tokens.stream()
                .filter(t -> t.getStudent().getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Token not found for email: " + email));
        
        String token = tokenObj.getToken();

        // 4. Confirm registration
        mockMvc.perform(get("/registrationConfirm").param("token", token))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/authenticated"));

        // 5. Verify it's enabled in DB
        Student verifiedStudent = studentRepository.findByEmail(email);
        assertTrue(verifiedStudent.isEnabled(), "Student should be enabled after confirmation");
    }
}
