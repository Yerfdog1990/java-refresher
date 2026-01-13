package springsecurity.lesson3springsecuritycustomexpressions.userspage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.MyUser;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.MyUserRepository;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
public class UsersPageDeleteActionTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MyUserRepository studentRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    public void givenUsersPage_whenCheckingDeleteButton_thenShouldHaveCorrectAction() throws Exception {
        MyUser myUser = new MyUser();
        myUser.setUsername("testdelete");
        myUser.setEmail("testdelete@test.com");
        myUser.setPassword("Password123!");
        myUser.setPasswordConfirmation("Password123!");
        myUser = studentRepository.save(myUser);

        mockMvc.perform(get("/users"))
                .andExpect(content().string(containsString("action=\"/users/" + myUser.getId() + "/delete\"")));
    }
}
