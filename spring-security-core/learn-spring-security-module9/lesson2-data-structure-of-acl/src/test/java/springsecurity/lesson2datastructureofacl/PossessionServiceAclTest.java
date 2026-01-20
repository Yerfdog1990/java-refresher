package springsecurity.lesson2datastructureofacl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import springsecurity.lesson2datastructureofacl.persistence.entity.Possession;
import springsecurity.lesson2datastructureofacl.service.PossessionService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class PossessionServiceAclTest {

    @Autowired
    private PossessionService possessionService;

    @Test
    @WithMockUser(username = "1")
    public void givenUser1_whenReadOwnPossession_thenSuccess() {
        Possession p = new Possession();
        p.setId(1L);
        assertNotNull(possessionService.read(p));
    }

    @Test
    @WithMockUser(username = "2")
    public void givenUser2_whenReadUser1Possession_thenFail() {
        Possession p = new Possession();
        p.setId(1L);
        assertThrows(AccessDeniedException.class, () -> possessionService.read(p));
    }

    @Test
    @WithMockUser(username = "2")
    public void givenUser2_whenReadSharedPossession_thenSuccess() {
        Possession p = new Possession();
        p.setId(2L);
        assertNotNull(possessionService.read(p));
    }

    @Test
    @WithMockUser(username = "1")
    public void givenUser1_whenUpdateOwnPossession_thenSuccess() {
        Possession p = new Possession();
        p.setId(1L);
        p = possessionService.read(p);
        p.setName("Updated Laptop");
        assertNotNull(possessionService.update(p));
    }

    @Test
    @WithMockUser(username = "2")
    public void givenUser2_whenUpdateUser1Possession_thenFail() {
        Possession p = new Possession();
        p.setId(1L);
        p.setName("Malicious Update");
        assertThrows(AccessDeniedException.class, () -> possessionService.update(p));
    }

    @Test
    @WithMockUser(username = "1")
    public void givenUser1_whenDeleteOwnPossession_thenSuccess() {
        possessionService.delete(1L);
    }

    @Test
    @WithMockUser(username = "2")
    public void givenUser2_whenDeleteUser1Possession_thenFail() {
        assertThrows(AccessDeniedException.class, () -> possessionService.delete(1L));
    }
}
