package springsecurity.lesson3springsecuritycustomexpressions.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.service.MyUserDetailsService;

@Configuration
public class SecurityConfig {

    private final JdbcTemplate jdbcTemplate;
    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    public SecurityConfig(JdbcTemplate jdbcTemplate, MyUserDetailsService myUserDetailsService) {
        this.jdbcTemplate = jdbcTemplate;
        this.myUserDetailsService = myUserDetailsService;
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
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/signup", "/myuser/register", "/registrationConfirm",
                                "/activation", "/authenticated", "/forgotPassword", "/myuser/resetPassword",
                                "/myuser/changePassword", "/user/savePassword", "/css/**", "/js/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/doLogin")
                        .defaultSuccessUrl("/users", true)  // Redirect to users page after successful login
                        .failureUrl("/login?error=true")  // Redirect back to login page with error
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .cors(Customizer.withDefaults()
                )
                .csrf(Customizer.withDefaults()
                )
                .rememberMe(rem -> rem
                        .userDetailsService(myUserDetailsService)
                        .tokenRepository(persistentTokenRepository())
                );
        return http.build();
    }

}