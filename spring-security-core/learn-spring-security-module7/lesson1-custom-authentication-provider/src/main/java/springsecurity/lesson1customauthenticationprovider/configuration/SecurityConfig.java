package springsecurity.lesson1customauthenticationprovider.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import springsecurity.lesson1customauthenticationprovider.customsecurity.CustomAuthenticationProvider;
import springsecurity.lesson1customauthenticationprovider.persistance.service.StudentDetailsService;

@Configuration
@EnableMethodSecurity(jsr250Enabled = true, prePostEnabled=true, securedEnabled = true)
public class SecurityConfig{

    private final JdbcTemplate jdbcTemplate;
    private final StudentDetailsService studentDetailsService;
    private final CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    public SecurityConfig(JdbcTemplate jdbcTemplate, StudentDetailsService studentDetailsService, @Lazy CustomAuthenticationProvider customAuthenticationProvider) {
        this.jdbcTemplate = jdbcTemplate;
        this.studentDetailsService = studentDetailsService;
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setJdbcTemplate(jdbcTemplate);
        return tokenRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/signup", "/student/register", "/registrationConfirm",
                                "/activation", "/authenticated", "/forgotPassword", "/student/resetPassword",
                                "/student/changePassword", "/user/savePassword", "/css/**", "/js/**")
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
                //.authenticationProvider(customAuthenticationProvider)
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
                        .userDetailsService(studentDetailsService)
                        .tokenRepository(persistentTokenRepository())
                );
        return http.build();
    }
}