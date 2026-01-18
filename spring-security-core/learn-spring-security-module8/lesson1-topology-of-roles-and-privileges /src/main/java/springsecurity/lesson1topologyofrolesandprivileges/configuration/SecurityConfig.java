package springsecurity.lesson1topologyofrolesandprivileges.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import springsecurity.lesson1topologyofrolesandprivileges.customsecurity.CustomAuthenticationProvider;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.service.StudentDetailsService;

import java.util.List;

@Configuration
@EnableMethodSecurity(jsr250Enabled = true, prePostEnabled=true, securedEnabled = true)
public class SecurityConfig{

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
    public PasswordEncoder passwordEncoder() { // @formatter=off
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    } // @formatter=on

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(studentDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("ADMIN").implies("USER").build();
    }

    // and, if using pre-post method security also add
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }

    @Bean
    public AuthenticationManager authenticationManager(CustomAuthenticationProvider customProvider, DaoAuthenticationProvider daoProvider) {
        List<AuthenticationProvider> authenticationProviders = List.of(customProvider, daoProvider);
        ProviderManager providerManager = new ProviderManager(authenticationProviders);

        // Prevent the clearing of credentials after a successful authentication request
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager, MethodSecurityExpressionHandler methodSecurityExpressionHandler) throws Exception {
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
                .authenticationManager(authenticationManager)
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