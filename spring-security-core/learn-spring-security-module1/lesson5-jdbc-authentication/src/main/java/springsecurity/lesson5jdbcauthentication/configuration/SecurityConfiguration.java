package springsecurity.lesson5jdbcauthentication.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder(){
        // Using plaintext passwords per current seed/data model
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DataSource dataSource) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()
                        .anyRequest().authenticated()  // Secure all other endpoints
                )
                // Use Spring Security's default login page
                .formLogin(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .userDetailsService(userDetailsService(dataSource))
                .csrf(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public JdbcUserDetailsManager userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery(
                "select username, password, enabled from student where username = ?");
        manager.setAuthoritiesByUsernameQuery(
                "select s.username, a.authority from student s " +
                        "join authority a on s.id = a.student_id " +
                        "where s.username = ?");
        return manager;
    }
}
