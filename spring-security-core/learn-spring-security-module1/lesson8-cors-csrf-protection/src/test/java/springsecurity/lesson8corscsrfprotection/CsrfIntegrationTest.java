package springsecurity.lesson8corscsrfprotection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CsrfIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenNoCsrf_whenPost_thenForbidden() throws Exception {
        mockMvc.perform(post("/users")
                        .param("username", "testuser")
                        .param("email", "test@test.com")
                        .param("password", "password")
                        .param("role", "STUDENT"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenCsrf_whenPost_thenRedirect() throws Exception {
        mockMvc.perform(post("/users")
                        .with(csrf())
                        .param("username", "testuser2")
                        .param("email", "test2@test.com")
                        .param("password", "password")
                        .param("role", "STUDENT"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void loginWhenValidCsrfTokenThenSuccess() throws Exception {
        this.mockMvc.perform(post("/doLogin").with(csrf())
                        .accept(MediaType.TEXT_HTML)
                        .param("username", "user")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(HttpHeaders.LOCATION, "/custom-login?error"));
    }

    @Test
    public void loginWhenInvalidCsrfTokenThenForbidden() throws Exception {
        this.mockMvc.perform(post("/doLogin")
                        .with(csrf().useInvalidToken())
                        .accept(MediaType.TEXT_HTML)
                        .param("username", "user")
                        .param("password", "password"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void loginWhenMissingCsrfTokenThenForbidden() throws Exception {
        this.mockMvc.perform(post("/doLogin")
                        .accept(MediaType.TEXT_HTML)
                        .param("username", "user")
                        .param("password", "password"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void logoutWhenValidCsrfTokenThenSuccess() throws Exception {
        this.mockMvc.perform(post("/doLogout")
                        .with(csrf())
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(HttpHeaders.LOCATION, "/custom-login?logout=true"));
    }
}
