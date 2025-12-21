package springsecurity.lesson6customloginform;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.FormLoginRequestBuilder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenValidUser_whenLogin_thenRedirectToHome() throws Exception {
        FormLoginRequestBuilder loginRequest = formLogin("/doLogin")
                .user("user", "Alice")
                .password("password", "alice123");

        mockMvc.perform(loginRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    public void givenInvalidUser_whenLogin_thenRedirectToError() throws Exception {
        FormLoginRequestBuilder loginRequest = formLogin("/doLogin")
                .user("user", "invalid")
                .password("password", "wrong");

        mockMvc.perform(loginRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/custom-login?error"));
    }
}
