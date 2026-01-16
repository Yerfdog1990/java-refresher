package springsecurity.lesson2multipleprovidersandauthenticationmanager.authenticationproviders;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProviderOrderTest {

    @Autowired
    AuthenticationManager authManager;

    @Test
    void givenDatabase_whenAuthenticatePersistedUser_thenSuccess() {

        Authentication auth = new UsernamePasswordAuthenticationToken("bob@example.com", "bob123");

        Authentication result = authManager.authenticate(auth);

        boolean roleUser = result.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));
        assertTrue(roleUser);
    }


    @Test
    void givenDatabase_whenAuthenticateNonPersistedUser_thenFailure() {
        Authentication auth = new UsernamePasswordAuthenticationToken("xyz@example.com", "xyz123");

        // Expect an AuthenticationException to be thrown
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> authManager.authenticate(auth));

        // Check the specific type of exception
        assertInstanceOf(BadCredentialsException.class, exception);

        // And verify the error message if needed
        assertTrue(exception.getMessage().contains("Bad credentials"));
    }
}
