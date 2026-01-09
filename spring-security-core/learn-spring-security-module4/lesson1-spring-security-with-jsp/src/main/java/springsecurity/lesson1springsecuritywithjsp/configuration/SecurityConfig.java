package springsecurity.lesson1springsecuritywithjsp.configuration;

import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import springsecurity.lesson1springsecuritywithjsp.persistance.service.StudentDetailsService;

@Configuration
public class SecurityConfig {

    private final JdbcTemplate jdbcTemplate;
    private final StudentDetailsService studentDetailsService;

    @Autowired
    public SecurityConfig(JdbcTemplate jdbcTemplate, StudentDetailsService studentDetailsService) {
        this.jdbcTemplate = jdbcTemplate;
        this.studentDetailsService = studentDetailsService;
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setJdbcTemplate(jdbcTemplate);
        return tokenRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Using plaintext passwords per current seed/data model
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Temporarily disable CSRF for testing
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        .requestMatchers(
                                "/", "/home", "/login", "/signup", "/login/**", "/signup/**",
                                "/student/register", "/student/register/**", "/registrationConfirm", "/registrationConfirm/**",
                                "/activation", "/activation/**", "/authenticated", "/authenticated/**",
                                "/forgotPassword", "/forgotPassword/**", "/student/resetPassword", "/student/resetPassword/**",
                                "/student/changePassword", "/student/changePassword/**", "/user/savePassword", "/user/savePassword/**",
                                "/error", "/error/**", "/css/**", "/js/**", "/webjars/**", "/static/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/doLogin")
                        .defaultSuccessUrl("/users", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .rememberMe(rem -> rem
                        .userDetailsService(studentDetailsService)
                        .tokenRepository(persistentTokenRepository())
                )
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

        return http.build();
    }
}