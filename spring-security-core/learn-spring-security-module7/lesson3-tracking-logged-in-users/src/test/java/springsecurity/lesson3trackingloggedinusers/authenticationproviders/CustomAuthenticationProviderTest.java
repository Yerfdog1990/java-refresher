package springsecurity.lesson3trackingloggedinusers.authenticationproviders;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import springsecurity.lesson3trackingloggedinusers.customsecurity.CustomAuthenticationProvider;
import springsecurity.lesson3trackingloggedinusers.persistance.service.StudentDetailsService;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomAuthenticationProviderTest {
    @Mock
    private StudentDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DaoAuthenticationProvider daoAuthenticationProvider;

    @InjectMocks
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Test
    void givenValidPassword_whenUseCustomAuthenticationProvider_thenSuccess() {

        UserDetails user = User.withUsername("john")
                .password("encodedPass")
                .authorities("ROLE_USER")
                .build();

        when(userDetailsService.loadUserByUsername("john"))
                .thenReturn(user);

        when(passwordEncoder.matches("rawPass", "encodedPass"))
                .thenReturn(true);

        Authentication auth = new UsernamePasswordAuthenticationToken("john", "rawPass");

        Authentication result = customAuthenticationProvider.authenticate(auth);

        assert result != null;
        assertTrue(result.isAuthenticated());
        assertEquals("john", ((UserDetails) Objects.requireNonNull(result.getPrincipal())).getUsername());
    }

    @Test
    void givenInvalidPassword_whenUseCustomAuthenticationProvider_thenFail() {

        UserDetails user = User.withUsername("john")
                .password("encodedPass")
                .authorities("ROLE_USER")
                .build();

        when(userDetailsService.loadUserByUsername("john"))
                .thenReturn(user);

        when(passwordEncoder.matches("wrong", "encodedPass"))
                .thenReturn(false);

        Authentication auth = new UsernamePasswordAuthenticationToken("john", "wrong");

        assertThrows(BadCredentialsException.class, () -> customAuthenticationProvider.authenticate(auth));
    }

    @Test
    void givenNonExitingUser_whenUseCustomAuthenticationProvider_thenUserNotFound() {

        when(userDetailsService.loadUserByUsername("unknown"))
                .thenThrow(new UsernameNotFoundException("Not found"));

        Authentication auth = new UsernamePasswordAuthenticationToken("unknown", "pass");

        assertThrows(UsernameNotFoundException.class, () -> customAuthenticationProvider.authenticate(auth));
    }

    @Test
    void givenCustomAuthenticationProvider_WhenTestSupportMethod_ThenSuccess() {

        // Test supporting UsernamePasswordAuthenticationToken class
        assertTrue(customAuthenticationProvider.supports(UsernamePasswordAuthenticationToken.class));

        // Test supporting RememberMeAuthenticationToken class
        assertFalse(customAuthenticationProvider.supports(RememberMeAuthenticationToken.class));
    }

    @Test
    void givenDaoAuthenticationProvider_WhenTestSupportMethod_ThenFai() {

        // Test supporting UsernamePasswordAuthenticationToken class
        assertFalse(daoAuthenticationProvider.supports(RememberMeAuthenticationToken.class));

        // Test supporting RememberMeAuthenticationToken class
        assertFalse(daoAuthenticationProvider.supports(UsernamePasswordAuthenticationToken.class));
    }

}
