package springsecurity.lesson1urlauthorizationwithexpressions.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User
                .withUsername("user")
                .password("{noop}pass")
                .roles("USER")
                .build();

        UserDetails admin = User
                .withUsername("admin")
                .password("{noop}pass")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // ❌ Incorrect - these are separate rules, the second one will override the first
                        //.requestMatchers("/login").hasRole("ADMIN").hasAuthority("READ_PRIVILEGE")

                        // ✅ Correct - use either one
                        .requestMatchers("/user").access(new WebExpressionAuthorizationManager("hasRole('USER')")) // checks for ROLE_USER
                        // OR
                        .requestMatchers("/admin").access(new WebExpressionAuthorizationManager("hasAuthority('ROLE_USER')")) // equivalent to hasAnyRole("ADMIN")

                        // Match based on IP address
                        .requestMatchers("/ip").access(new WebExpressionAuthorizationManager(
                                "hasIpAddress('127.0.0.1') or " +  // IPv4 localhost
                                        "hasIpAddress('::1') or " +        // IPv6 localhost
                                        "hasIpAddress('43.208.178.20')"))  // Your external IP
                        // Match anonymous user
                        .requestMatchers("/anonymous").access(new WebExpressionAuthorizationManager("isAnonymous()"))

                        // Match all GET methods
                        .requestMatchers("/user", "/admin").access(new WebExpressionAuthorizationManager("request.method == 'GET'"))
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .successHandler((request, response, authentication) -> {
                            if (authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                                response.sendRedirect("/admin");
                            } else {
                                response.sendRedirect("/user");
                            }
                        })
                )
                .logout(logout -> logout
                        .permitAll()
                        .logoutUrl("/logout")
                );
        return http.build();
    }
}
