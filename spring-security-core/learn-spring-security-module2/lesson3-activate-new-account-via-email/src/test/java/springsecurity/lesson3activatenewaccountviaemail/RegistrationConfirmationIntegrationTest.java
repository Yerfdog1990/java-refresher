package springsecurity.lesson3activatenewaccountviaemail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import springsecurity.lesson3activatenewaccountviaemail.persistance.model.Student;
import springsecurity.lesson3activatenewaccountviaemail.persistance.model.VerificationToken;
import springsecurity.lesson3activatenewaccountviaemail.persistance.repository.IStudentRepository;
import springsecurity.lesson3activatenewaccountviaemail.persistance.repository.VerificationTokenRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class RegistrationConfirmationIntegrationTest {

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
    public void givenInvalidToken_whenConfirmRegistration_thenRedirectToLoginWithErrorMessage() throws Exception {
        mockMvc.perform(get("/registrationConfirm").param("token", "invalid-token"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("errorMessage", "Invalid account confirmation token."));
    }

    @Test
    public void givenExpiredToken_whenConfirmRegistration_thenRedirectToLoginWithErrorMessage() throws Exception {
        Student student = new Student();
        student.setUsername("Joe Doe");
        student.setEmail("joedoe@gmail.com");
        student.setPassword("joe123");
        student.setPasswordConfirmation("joe123");
        studentRepository.save(student);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, student);
        
        // Set an expiry date to the past
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -1);
        verificationToken.setExpiryDate(cal.getTime());
        
        tokenRepository.save(verificationToken);

        mockMvc.perform(get("/registrationConfirm").param("token", token))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("errorMessage", "Your registration token has expired. Please register again."));
    }

    @Test
    public void givenValidToken_whenConfirmRegistration_thenRedirectToAuthenticated() throws Exception {
        Student student = new Student();
        student.setUsername("JaneDoe2");
        student.setEmail("jane2@example.com");
        student.setPassword("password");
        student.setPasswordConfirmation("password");
        student.setEnabled(false);
        studentRepository.save(student);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, student);
        tokenRepository.save(verificationToken);

        mockMvc.perform(get("/registrationConfirm").param("token", token))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/authenticated"));

        Student verifiedStudent = studentRepository.findByEmail("jane2@example.com");
        assertTrue(verifiedStudent.isEnabled());
    }
}
