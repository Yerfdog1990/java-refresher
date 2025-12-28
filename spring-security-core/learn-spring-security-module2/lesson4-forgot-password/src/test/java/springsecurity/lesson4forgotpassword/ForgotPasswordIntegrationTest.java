package springsecurity.lesson4forgotpassword;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import springsecurity.lesson4forgotpassword.persistance.model.PasswordResetToken;
import springsecurity.lesson4forgotpassword.persistance.model.Student;
import springsecurity.lesson4forgotpassword.persistance.repository.PasswordResetTokenRepository;
import springsecurity.lesson4forgotpassword.persistance.service.IStudentService;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class ForgotPasswordIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private IStudentService studentService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void givenForgotPasswordPage_whenGetForgotPassword_thenSuccess() throws Exception {
        mockMvc.perform(get("/forgotPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("forgotPassword"));
    }

    @Test
    public void givenValidEmail_whenResetPassword_thenRedirectToLoginWithMessage() throws Exception {
        mockMvc.perform(post("/student/resetPassword")
                .param("email", "alice@example.com")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("message", "You should receive an Password Reset Email shortly"));
    }

    @Test
    public void givenValidToken_whenShowChangePasswordPage_thenSuccess() throws Exception {
        Student student = studentService.findUserByEmail("alice@example.com");
        String token = UUID.randomUUID().toString();
        studentService.createPasswordResetTokenForUser(student, token);

        mockMvc.perform(get("/student/changePassword")
                .param("id", String.valueOf(student.getId()))
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(view().name("resetPassword"))
                .andExpect(model().attribute("token", token));
    }

    @Test
    public void givenValidPasswordResetRequest_whenSavePassword_thenSuccess() throws Exception {
        Student student = studentService.findUserByEmail("alice@example.com");
        String token = UUID.randomUUID().toString();
        studentService.createPasswordResetTokenForUser(student, token);

        mockMvc.perform(post("/user/savePassword")
                .param("password", "newpassword")
                .param("passwordConfirmation", "newpassword")
                .param("token", token)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("message", "Password reset successfully"));
    }
}
