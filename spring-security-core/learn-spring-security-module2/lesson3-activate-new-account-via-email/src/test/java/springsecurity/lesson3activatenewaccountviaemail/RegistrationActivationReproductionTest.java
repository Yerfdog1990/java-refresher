package springsecurity.lesson3activatenewaccountviaemail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import springsecurity.lesson3activatenewaccountviaemail.persistance.model.Student;
import springsecurity.lesson3activatenewaccountviaemail.persistance.model.VerificationToken;
import springsecurity.lesson3activatenewaccountviaemail.persistance.repository.IStudentRepository;
import springsecurity.lesson3activatenewaccountviaemail.persistance.repository.VerificationTokenRepository;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RegistrationActivationReproductionTest {

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
        tokenRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @Test
    public void givenValidToken_whenConfirmRegistration_thenShouldSucceed() throws Exception {
        Student student = new Student();
        student.setUsername("reproUser");
        student.setEmail("repro@example.com");
        student.setPassword("password");
        student.setPasswordConfirmation("password");
        student.setEnabled(false);
        studentRepository.save(student);

        // Simulate fetching student from DB where passwordConfirmation is NOT persisted
        Student savedStudent = studentRepository.findByEmail("repro@example.com");
        savedStudent.setPasswordConfirmation(null); // This is what happens when it's loaded from DB

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, savedStudent);
        tokenRepository.save(verificationToken);

        mockMvc.perform(get("/registrationConfirm").param("token", token))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/authenticated"));

        Student verifiedStudent = studentRepository.findByEmail("repro@example.com");
        assertTrue(verifiedStudent.isEnabled());
    }
}
