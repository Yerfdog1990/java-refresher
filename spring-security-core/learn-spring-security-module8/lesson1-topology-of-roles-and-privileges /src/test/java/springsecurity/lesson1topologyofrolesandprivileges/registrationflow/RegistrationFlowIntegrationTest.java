package springsecurity.lesson1topologyofrolesandprivileges.registrationflow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class RegistrationFlowIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void givenLoginPage_whenGetLogin_thenSuccess() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginPage"));
    }

    @Test
    public void givenRegistrationPage_whenGetSignup_thenSuccess() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("registrationPage"));
    }

    @Test
    public void givenNewStudent_whenRegisterWithEmptyConfirmation_thenShowError() throws Exception {
        mockMvc.perform(post("/student/register")
                .param("username", "emptyconf")
                .param("email", "emptyconf@test.com")
                .param("password", "password123")
                .param("passwordConfirmation", "")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("registrationPage"))
                .andExpect(model().hasErrors());
    }

    @Test
    public void givenNewStudent_whenRegisterWithMismatchingPasswords_thenShowError() throws Exception {
        mockMvc.perform(post("/student/register")
                .param("username", "mismatch")
                .param("email", "mismatch@test.com")
                .param("password", "password123")
                .param("passwordConfirmation", "wrong")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("registrationPage"))
                .andExpect(model().hasErrors());
    }

    @Test
    public void givenNewStudent_whenRegister_thenRedirectToActivation() throws Exception {
        mockMvc.perform(post("/student/register")
                .param("username", "testuser")
                .param("email", "test@test.com")
                .param("password", "Password123!")
                .param("passwordConfirmation", "Password123!")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/activation"));
    }

    @Test
    public void givenUnverifiedStudent_whenLogin_thenRedirectToLoginWithError() throws Exception {
        // Register a new student
        mockMvc.perform(post("/student/register")
                .param("username", "unverified")
                .param("email", "unverified@test.com")
                .param("password", "Password123!")
                .param("passwordConfirmation", "Password123!")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Try to login
        mockMvc.perform(formLogin("/doLogin")
                .user("unverified@test.com")
                .password("Password123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    public void givenRegisteredStudent_whenLogin_thenRedirectToUsers() throws Exception {
        var mvcResult = mockMvc.perform(formLogin("/doLogin")
                .user("alice@example.com")
                .password("alice123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andReturn();

        var session = mvcResult.getRequest().getSession();

        mockMvc.perform(get("/users").session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(view().name("users"));
    }
}
