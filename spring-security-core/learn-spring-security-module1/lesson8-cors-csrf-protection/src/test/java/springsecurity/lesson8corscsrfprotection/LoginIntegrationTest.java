package springsecurity.lesson8corscsrfprotection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.FormLoginRequestBuilder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenCorrectCredentials_whenLogin_thenSuccess() throws Exception {
        FormLoginRequestBuilder loginRequest = formLogin("/doLogin")
                .user("username", "Alice")
                .password("password", "alice123");

        mockMvc.perform(loginRequest)
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/home"))
                .andExpect(authenticated().withUsername("Alice"));
    }

    @Test
    public void givenWrongCredentials_whenLogin_thenFailure() throws Exception {
        FormLoginRequestBuilder loginRequest = formLogin("/doLogin")
                .user("username", "Alice")
                .password("password", "wrongpassword");

        mockMvc.perform(loginRequest)
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/custom-login?error"))
                .andExpect(unauthenticated());
    }
}
