package springsecurity.lesson3springsecuritycustomexpressions.activateuser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.MyUser;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.VerificationToken;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.MyUserRepository;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.VerificationTokenRepository;

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
    private MyUserRepository studentRepository;

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
        MyUser myUser = new MyUser();
        myUser.setUsername("reproUser");
        myUser.setEmail("repro@example.com");
        myUser.setPassword("Password123!");
        myUser.setPasswordConfirmation("Password123!");
        myUser.setEnabled(false);
        studentRepository.save(myUser);

        // Simulate fetching myUser from DB where passwordConfirmation is NOT persisted
        MyUser savedMyUser = studentRepository.findByEmail("repro@example.com");
        savedMyUser.setPasswordConfirmation(null); // This is what happens when it's loaded from DB

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, savedMyUser);
        tokenRepository.save(verificationToken);

        mockMvc.perform(get("/registrationConfirm").param("token", token))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/authenticated"));

        MyUser verifiedMyUser = studentRepository.findByEmail("repro@example.com");
        assertTrue(verifiedMyUser.isEnabled());
    }
}
