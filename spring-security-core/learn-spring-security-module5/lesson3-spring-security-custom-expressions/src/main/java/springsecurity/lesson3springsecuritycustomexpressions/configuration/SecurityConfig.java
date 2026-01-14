package springsecurity.lesson3springsecuritycustomexpressions.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import springsecurity.lesson3springsecuritycustomexpressions.customesecurity.CustomPermissionEvaluator;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.service.StudentDetailsService;

@EnableMethodSecurity(jsr250Enabled = true, prePostEnabled = true)
@Configuration
public class SecurityConfig {

    private final JdbcTemplate jdbcTemplate;
    private final StudentDetailsService studentDetailsService;
    private final CustomPermissionEvaluator customPermissionEvaluator;

    @Autowired
    public SecurityConfig(JdbcTemplate jdbcTemplate, StudentDetailsService studentDetailsService,  CustomPermissionEvaluator customPermissionEvaluator) {
        this.jdbcTemplate = jdbcTemplate;
        this.studentDetailsService = studentDetailsService;
        this.customPermissionEvaluator = customPermissionEvaluator;
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
                        .requestMatchers("/login", "/signup", "/student/register", "/registrationConfirm",
                                "/activation", "/authenticated", "/forgotPassword", "/student/resetPassword",
                                "/student/changePassword", "/user/savePassword", "/css/**", "/js/**")
                        .permitAll()
                        .requestMatchers("/users/{id}/edit", "/css/**", "/js/**").access((authentication, object) -> {
                            Authentication authenticated = authentication.get();
                            return new AuthorizationDecision(customPermissionEvaluator.hasPermission(authenticated, "Student", "READ"));
                        })
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
                        .userDetailsService(studentDetailsService)
                        .tokenRepository(persistentTokenRepository())
                );
        return http.build();
    }

    // Registering the PermissionEvaluator
    @Bean
    MethodSecurityExpressionHandler expressionHandler(CustomPermissionEvaluator evaluator) {

        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(evaluator);
        return handler;
    }
}