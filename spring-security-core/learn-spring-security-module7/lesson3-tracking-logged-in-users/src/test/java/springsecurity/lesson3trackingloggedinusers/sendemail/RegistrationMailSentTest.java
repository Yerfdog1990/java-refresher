package springsecurity.lesson3trackingloggedinusers.sendemail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import springsecurity.lesson3trackingloggedinusers.registration.listener.RegistrationListener;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class RegistrationMailSentTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RegistrationListener registrationListener;

    @MockitoBean
    private JavaMailSender mailSender;

    private MockMvc mockMvc;

    @Test
    public void verifyListenerIsReal() {
        assertNotNull(registrationListener);
        assertTrue(registrationListener instanceof RegistrationListener);
    }

    @Test
    public void givenNewStudent_whenRegister_thenMailSent() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        mockMvc.perform(post("/student/register")
                .param("username", "mailtest")
                .param("email", "mailtest@test.com")
                .param("password", "Password123!")
                .param("passwordConfirmation", "Password123!")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        verify(mailSender, timeout(5000)).send(any(SimpleMailMessage.class));
    }
}
