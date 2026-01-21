package springsecurity.lesson2datastructureofacl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenAdmin_whenListUsers_thenSuccess() throws Exception {
        mockMvc.perform(get("/users").with(user("bob@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("students"));
    }

    @Test
    public void givenUser_whenListUsers_thenSuccess() throws Exception {
        mockMvc.perform(get("/users").with(user("alice@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("students"));
    }

    @Test
    public void givenUser_whenHome_thenSuccess() throws Exception {
        mockMvc.perform(get("/").with(user("alice@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));

        mockMvc.perform(get("/home").with(user("alice@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    public void givenAdmin_whenCreateForm_thenSuccess() throws Exception {
        mockMvc.perform(get("/users/form").with(user("bob@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeExists("student"));
    }

    @Test
    public void givenAdmin_whenCreateStudent_thenSuccess() throws Exception {
        mockMvc.perform(post("/users")
                        .with(user("bob@example.com").roles("ADMIN"))
                        .param("username", "Dave")
                        .param("email", "dave@example.com")
                        .param("password", "dave123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/users/*"))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    public void givenUser_whenCreateStudent_thenForbidden() throws Exception {
        mockMvc.perform(post("/users")
                        .with(user("alice@example.com").roles("USER"))
                        .param("username", "Dave")
                        .param("email", "dave@example.com")
                        .param("password", "dave123")
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    public void givenUser_whenViewUser_thenSuccess() throws Exception {
        mockMvc.perform(get("/users/1").with(user("alice@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("view"))
                .andExpect(model().attributeExists("student"));
    }

    @Test
    public void givenAdmin_whenEditForm_thenSuccess() throws Exception {
        mockMvc.perform(get("/users/1/edit").with(user("bob@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeExists("student"));
    }

    @Test
    public void givenUser_whenEditForm_thenForbidden() throws Exception {
        mockMvc.perform(get("/users/2/edit").with(user("alice@example.com").roles("USER")))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    public void givenAdmin_whenUpdateStudent_thenSuccess() throws Exception {
        mockMvc.perform(post("/users/1")
                        .with(user("bob@example.com").roles("ADMIN"))
                        .param("username", "AliceUpdated")
                        .param("email", "alice@example.com")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/1"))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    public void givenAdmin_whenDeleteStudent_thenSuccess() throws Exception {
        mockMvc.perform(get("/users/1/delete").with(user("bob@example.com").roles("ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    public void givenUser_whenDeleteStudent_thenForbidden() throws Exception {
        mockMvc.perform(get("/users/1/delete").with(user("alice@example.com").roles("USER")))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }

    @Test
    public void givenUser_whenEditFormOwn_thenForbidden() throws Exception {
        mockMvc.perform(get("/users/1/edit").with(user("alice@example.com").roles("USER")))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    public void givenUser_whenUpdateStudent_thenForbidden() throws Exception {
        mockMvc.perform(post("/users/1")
                        .with(user("alice@example.com").roles("USER"))
                        .param("username", "AliceUpdated")
                        .param("email", "alice@example.com")
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(view().name("access-denied"));
    }
}
