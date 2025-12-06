package l2springsecurityarchitecture.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain");
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/hello/john").hasRole("USER")
                        .requestMatchers("/hello/jane").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .formLogin(withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        PasswordEncoder encoder = passwordEncoder();

        log.info("Creating in-memory users");

        UserDetails john = User.builder()
                .username("john")
                .password(encoder.encode("password"))
                .roles("USER")
                .build();

        UserDetails jane = User.builder()
                .username("jane")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        log.info("Created user: {} with roles: {}", john.getUsername(), john.getAuthorities());
        log.info("Created user: {} with roles: {}", jane.getUsername(), jane.getAuthorities());

        return new InMemoryUserDetailsManager(john, jane);
    }
}