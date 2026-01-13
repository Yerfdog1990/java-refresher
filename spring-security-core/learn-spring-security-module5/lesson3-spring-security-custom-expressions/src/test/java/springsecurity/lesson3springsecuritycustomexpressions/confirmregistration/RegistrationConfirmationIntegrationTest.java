package springsecurity.lesson3springsecuritycustomexpressions.confirmregistration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.MyUser;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.VerificationToken;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.MyUserRepository;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.VerificationTokenRepository;

import java.util.Calendar;
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
    private MyUserRepository studentRepository;

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
        MyUser myUser = new MyUser();
        myUser.setUsername("Joe Doe");
        myUser.setEmail("joedoe@gmail.com");
        myUser.setPassword("JoeDoe123!");
        myUser.setPasswordConfirmation("JoeDoe123!");
        studentRepository.save(myUser);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, myUser);
        
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
        MyUser myUser = new MyUser();
        myUser.setUsername("JaneDoe2");
        myUser.setEmail("jane2@example.com");
        myUser.setPassword("JaneDoe123!");
        myUser.setPasswordConfirmation("JaneDoe123!");
        myUser.setEnabled(false);
        studentRepository.save(myUser);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, myUser);
        tokenRepository.save(verificationToken);

        mockMvc.perform(get("/registrationConfirm").param("token", token))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/authenticated"));

        MyUser verifiedMyUser = studentRepository.findByEmail("jane2@example.com");
        assertTrue(verifiedMyUser.isEnabled());
    }
}
