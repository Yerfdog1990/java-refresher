package springsecurity.lesson2datastructureofacl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import springsecurity.lesson2datastructureofacl.persistence.entity.Student;
import springsecurity.lesson2datastructureofacl.persistence.repository.IStudentRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StudentDateCreatedPreservationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IStudentRepository studentRepository;

    @Test
    public void givenExistingStudent_whenUpdated_thenDateCreatedIsPreserved() throws Exception {
        // 1. Fetch initial student and their dateCreated
        Student alice = studentRepository.findByEmail("alice@example.com").orElseThrow();
        LocalDateTime originalDateCreated = alice.getDateCreated();
        assertNotNull(originalDateCreated, "Original dateCreated should not be null");

        // 2. Perform update via form
        mockMvc.perform(post("/users/" + alice.getId())
                        .with(user("bob@example.com").roles("ADMIN"))
                        .param("username", "AliceUpdated")
                        .param("email", "alice@example.com")
                        .param("dateCreated", originalDateCreated.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // 3. Verify dateCreated is still the same
        Student updatedAlice = studentRepository.findById(alice.getId()).orElseThrow();
        assertEquals(originalDateCreated, updatedAlice.getDateCreated(), "dateCreated should be preserved after update");
    }
}
