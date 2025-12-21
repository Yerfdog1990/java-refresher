package springsecurity.lesson8corscsrfprotection.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

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
                // Custom login and logout form
                .formLogin(login -> login
                        .loginPage("/custom-login")
                        .permitAll()
                        .usernameParameter("username")      // Match this with <input name="username">
                        .passwordParameter("password")  // Match this with <input name="password">
                        .loginProcessingUrl("/doLogin") // Match this with <form action="/doLogin" method="POST">
                        .defaultSuccessUrl("/home", true) // Redirect to home.html upon successful login
                        .failureUrl("/custom-login?error") // Redirects back to your custom page on failure
                )
                .logout(logout -> logout
                        .permitAll()
                        .logoutUrl("/doLogout")         // Triggered by a POST request to /doLogout
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/custom-login?logout=true") // Redirects back to your custom page after logout
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(Customizer.withDefaults()) // Use Defaults for Session-based CSRF
                .userDetailsService(userDetailsService(dataSource));

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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setMaxAge(3600L); // : It can set the maximum age of the CORS preflight request cache of the application.
        configuration.setAllowedOrigins(List.of("http://localhost:8080")); // It can allow the requests only from http://localhost:8080 of the application.
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // It can allow these http methods only of the Spring application.
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-csrf-token")); // It can allow these headers of the application.
        configuration.setAllowCredentials(true); //  It can allow the cookies and other credentials to be included.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

//    Alternative CORS configuration
//    @Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("http://localhost:8080");
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }
}
