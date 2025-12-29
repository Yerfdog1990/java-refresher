package springsecurity.lesson2remembermewithcookies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import springsecurity.lesson2remembermewithcookies.persistance.model.Student;
import springsecurity.lesson2remembermewithcookies.persistance.repository.IStudentRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class UsersDeleteReproductionTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private IStudentRepository studentRepository;

    @Autowired
    private springsecurity.lesson2remembermewithcookies.persistance.service.IStudentService studentService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    public void givenExistingUserWithTokenAndAuthority_whenDeleteRequest_thenShouldRedirectToUsersList() throws Exception {
        Student student = new Student();
        student.setUsername("testuser_full");
        student.setEmail("test_full@test.com");
        student.setPassword("Password123!");
        student.setPasswordConfirmation("Password123!");
        
        springsecurity.lesson2remembermewithcookies.persistance.model.Authority authority = new springsecurity.lesson2remembermewithcookies.persistance.model.Authority(student, "ROLE_USER");
        student.getAuthorities().add(authority);
        
        student = studentRepository.save(student);

        studentService.createVerificationTokenForUser(student, "full-token");

        mockMvc.perform(post("/users/" + student.getId() + "/delete")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }
}
