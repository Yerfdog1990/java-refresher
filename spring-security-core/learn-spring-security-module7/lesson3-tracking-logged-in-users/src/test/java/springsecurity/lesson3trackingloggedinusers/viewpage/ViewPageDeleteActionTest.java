package springsecurity.lesson3trackingloggedinusers.viewpage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import springsecurity.lesson3trackingloggedinusers.persistance.model.Student;
import springsecurity.lesson3trackingloggedinusers.persistance.repository.IStudentRepository;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
public class ViewPageDeleteActionTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private IStudentRepository studentRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    public void givenViewPage_whenCheckingDeleteButton_thenShouldHaveCorrectAction() throws Exception {
        Student student = new Student();
        student.setUsername("testviewdelete");
        student.setEmail("testviewdelete@test.com");
        student.setPassword("Password123!");
        student.setPasswordConfirmation("Password123!");
        student = studentRepository.save(student);

        mockMvc.perform(get("/users/" + student.getId()))
                .andExpect(content().string(containsString("action=\"/users/" + student.getId() + "/delete\"")));
    }
}
