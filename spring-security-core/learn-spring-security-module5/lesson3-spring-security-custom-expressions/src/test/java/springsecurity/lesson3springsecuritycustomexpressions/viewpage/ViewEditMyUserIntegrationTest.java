package springsecurity.lesson3springsecuritycustomexpressions.viewpage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.MyUser;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.MyUserRepository;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.VerificationTokenRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
public class ViewEditMyUserIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MyUserRepository studentRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        jdbcTemplate.execute("DELETE FROM verification_token");
        jdbcTemplate.execute("DELETE FROM authority");
        jdbcTemplate.execute("DELETE FROM student");

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    public void givenExistingStudent_whenViewStudent_thenSuccess() throws Exception {
        MyUser myUser = new MyUser();
        myUser.setUsername("Alice");
        myUser.setEmail("alice@example.com");
        myUser.setPassword("Alice123!");
        myUser.setPasswordConfirmation("Alice123!");
        myUser = studentRepository.save(myUser);

        mockMvc.perform(get("/users/" + myUser.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("view"))
                .andExpect(model().attributeExists("myUser"))
                .andExpect(model().attribute("myUser", org.hamcrest.Matchers.hasProperty("username", org.hamcrest.Matchers.is("Alice"))))
                .andExpect(model().attribute("myUser", org.hamcrest.Matchers.hasProperty("email", org.hamcrest.Matchers.is("alice@example.com"))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void givenExistingStudent_whenEditStudent_thenSuccess() throws Exception {
        MyUser myUser = new MyUser();
        myUser.setUsername("AliceEdit");
        myUser.setEmail("aliceedit@example.com");
        myUser.setPassword("Alice123!");
        myUser.setPasswordConfirmation("Alice123!");
        myUser = studentRepository.save(myUser);

        String newUsername = "AlinaEdit";
        mockMvc.perform(post("/users/" + myUser.getId() + "/edit")
                .param("username", newUsername)
                .param("email", "alinaedit@example.com")
                .param("password", "Alina123!")
                .param("passwordConfirmation", "Alina123!")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        MyUser updatedMyUser = studentRepository.findById(myUser.getId()).orElseThrow();
        assertEquals(newUsername, updatedMyUser.getUsername());
    }
}
