package springsecurity.lesson2multipleprovidersandauthenticationmanager.registrationflow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import springsecurity.lesson2multipleprovidersandauthenticationmanager.persistance.model.PasswordResetToken;
import springsecurity.lesson2multipleprovidersandauthenticationmanager.persistance.model.Student;
import springsecurity.lesson2multipleprovidersandauthenticationmanager.persistance.service.IStudentService;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class PasswordEmailValidationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private IStudentService studentService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void givenNewStudent_whenRegisterWithPasswordContainingEmail_thenShowError() throws Exception {
        mockMvc.perform(post("/student/register")
                .param("username", "testuser_email")
                .param("email", "test@test.com")
                .param("password", "Password123!test@test.com")
                .param("passwordConfirmation", "Password123!test@test.com")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("registrationPage"))
                .andExpect(model().attributeHasFieldErrors("student", "password"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Your password cannot contain your email")));
    }

    @Test
    public void givenNewStudent_whenRegisterWithPasswordContainingEmailCaseInsensitive_thenShowError() throws Exception {
        mockMvc.perform(post("/student/register")
                .param("username", "testuser_email")
                .param("email", "Goddyouma996@gmail.com")
                .param("password", "goddyouma996@1990")
                .param("passwordConfirmation", "goddyouma996@1990")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("registrationPage"))
                .andExpect(model().attributeHasFieldErrors("student", "password"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Your password cannot contain your email")));
    }

    @Test
    public void givenNewStudent_whenRegisterWithPasswordContainingEmailLocalPart_thenShowError() throws Exception {
        mockMvc.perform(post("/student/register")
                .param("username", "testuser_email")
                .param("email", "goddyouma996@gmail.com")
                .param("password", "Goddyouma996@1990")
                .param("passwordConfirmation", "Goddyouma996@1990")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("registrationPage"))
                .andExpect(model().attributeHasFieldErrors("student", "password"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Your password cannot contain your email")));
    }

    @Test
    public void givenValidToken_whenSavePasswordContainingEmail_thenShowError() throws Exception {
        // Given
        String email = "alice@example.com";
        String password = "Password123!" + email;
        String token = UUID.randomUUID().toString();

        Student student = new Student();
        student.setId(1L);
        student.setEmail(email);

        PasswordResetToken passToken = new PasswordResetToken(token, student);

        // Mock the service
        when(studentService.getPasswordResetToken(token)).thenReturn(passToken);

        // When & Then
        mockMvc.perform(post("/user/savePassword")
                        .with(csrf())
                        .param("password", password)
                        .param("passwordConfirmation", password)
                        .param("questionId", "1")
                        .param("answer", "answer")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(view().name("resetPassword"))
                .andExpect(model().attribute("errorMessage", "Your password cannot contain your email"));
    }

    @Test
    public void givenValidToken_whenSavePasswordContainingEmailLocalPartCaseInsensitive_thenShowError() throws Exception {
        // Given
        String email = "Goddyouma996@gmail.com";
        String password = "goddyouma996@1990";
        String token = UUID.randomUUID().toString();

        Student student = new Student();
        student.setId(1L);
        student.setEmail(email);

        PasswordResetToken passToken = new PasswordResetToken(token, student);

        // Mock the service
        when(studentService.getPasswordResetToken(token)).thenReturn(passToken);

        // When & Then
        mockMvc.perform(post("/user/savePassword")
                        .with(csrf())
                        .param("password", password)
                        .param("passwordConfirmation", password)
                        .param("questionId", "1")
                        .param("answer", "answer")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(view().name("resetPassword"))
                .andExpect(model().attribute("errorMessage", "Your password cannot contain your email"));
    }
}
