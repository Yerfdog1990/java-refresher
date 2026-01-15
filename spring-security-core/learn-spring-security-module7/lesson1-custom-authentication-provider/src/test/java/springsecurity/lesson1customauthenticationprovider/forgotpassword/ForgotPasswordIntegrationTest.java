package springsecurity.lesson1customauthenticationprovider.forgotpassword;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import springsecurity.lesson1customauthenticationprovider.persistance.model.PasswordResetToken;
import springsecurity.lesson1customauthenticationprovider.persistance.model.Student;
import springsecurity.lesson1customauthenticationprovider.persistance.repository.PasswordResetTokenRepository;
import springsecurity.lesson1customauthenticationprovider.persistance.service.IStudentService;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import springsecurity.lesson1customauthenticationprovider.persistance.model.SecurityQuestion;
import springsecurity.lesson1customauthenticationprovider.persistance.model.SecurityQuestionDefinition;
import springsecurity.lesson1customauthenticationprovider.persistance.repository.SecurityQuestionDefinitionRepository;
import springsecurity.lesson1customauthenticationprovider.persistance.repository.SecurityQuestionRepository;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class ForgotPasswordIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private IStudentService studentService;

    @MockitoBean
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @MockitoBean
    private SecurityQuestionRepository securityQuestionRepository;

    @MockitoBean
    private SecurityQuestionDefinitionRepository securityQuestionDefinitionRepository;

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
        Student student = new Student();
        student.setId(1L);
        student.setEmail("alice@example.com");

        when(studentService.findUserByEmail("alice@example.com")).thenReturn(student);
        String token = UUID.randomUUID().toString();
        PasswordResetToken passToken = new PasswordResetToken(token, student);
        when(studentService.getPasswordResetToken(token)).thenReturn(passToken);

        mockMvc.perform(get("/student/changePassword")
                .param("id", String.valueOf(student.getId()))
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(view().name("resetPassword"))
                .andExpect(model().attribute("token", token))
                .andExpect(model().attributeExists("questions"));
    }

    @Test
    public void givenValidPasswordResetRequest_whenSavePassword_thenSuccess() throws Exception {
        // Given
        String newPassword = "newPassword123";
        Long questionId = 1L;
        String answer = "test answer";
        String token = UUID.randomUUID().toString();

        // Mock the student
        Student student = new Student();
        student.setId(1L);
        student.setEmail("user@example.com");

        PasswordResetToken passToken = new PasswordResetToken(token, student);

        // Mock the security question
        SecurityQuestion securityQuestion = new SecurityQuestion();
        securityQuestion.setStudent(student);
        securityQuestion.setAnswer(answer);
        SecurityQuestionDefinition definition = new SecurityQuestionDefinition();
        definition.setId(questionId);
        definition.setText("What is your favorite color?");
        securityQuestion.setQuestionDefinition(definition);

        // Mock the service/repository calls
        when(studentService.getPasswordResetToken(token)).thenReturn(passToken);
        when(securityQuestionRepository.findByQuestionDefinitionIdAndStudentIdAndAnswer(
                eq(questionId), eq(student.getId()), eq(answer)))
                .thenReturn(securityQuestion);

        // When & Then
        mockMvc.perform(post("/user/savePassword")
                        .with(csrf())
                        .param("password", newPassword)
                        .param("passwordConfirmation", newPassword)
                        .param("questionId", questionId.toString())
                        .param("answer", answer)
                        .param("token", token))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?resetSuccess"));
    }

    @Test
    public void givenInvalidSecurityAnswer_whenSavePassword_thenError() throws Exception {
        // Given
        String newPassword = "newPassword123";
        Long questionId = 1L;
        String wrongAnswer = "wrong answer";
        String token = UUID.randomUUID().toString();

        // Mock the student
        Student student = new Student();
        student.setId(1L);
        student.setEmail("user@example.com");

        PasswordResetToken passToken = new PasswordResetToken(token, student);

        // Mock the service/repository calls
        when(studentService.getPasswordResetToken(token)).thenReturn(passToken);
        when(securityQuestionRepository.findByQuestionDefinitionIdAndStudentIdAndAnswer(
                anyLong(), anyLong(), anyString()))
                .thenReturn(null);

        // When & Then
        mockMvc.perform(post("/user/savePassword")
                        .with(csrf())
                        .param("password", newPassword)
                        .param("passwordConfirmation", newPassword)
                        .param("questionId", questionId.toString())
                        .param("answer", wrongAnswer)
                        .param("token", token))
                .andExpect(status().isOk())  // Should return to the same page
                .andExpect(view().name("resetPassword"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Incorrect answer to security question"));
    }

    @Test
    public void givenInvalidToken_whenSavePassword_thenRedirectToLogin() throws Exception {
        // Given
        String token = "invalid-token";
        when(studentService.getPasswordResetToken(token)).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/user/savePassword")
                        .with(csrf())
                        .param("password", "newPassword123")
                        .param("passwordConfirmation", "newPassword123")
                        .param("questionId", "1")
                        .param("answer", "answer")
                        .param("token", token))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("errorMessage", "Invalid password reset token"));
    }
}
