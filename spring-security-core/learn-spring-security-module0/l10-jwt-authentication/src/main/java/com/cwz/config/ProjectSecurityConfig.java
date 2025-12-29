package com.cwz.config;

import com.cwz.filter.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.sql.DataSource;

import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class ProjectSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler=new CsrfTokenRequestAttributeHandler();
//        http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll());
//        http.authorizeHttpRequests((requests) -> requests.anyRequest().denyAll());
        http
                .sessionManagement(sessionConfig-> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors((corsConfig)->corsConfig.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config=new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        config.setAllowedMethods(Collections.singletonList("*")); //GET<POST<PUT<DELETE
                        config.setAllowCredentials(true);
                        config.setExposedHeaders(Collections.singletonList("Authorization"));
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setMaxAge(3600L);
                        return  config;
                    }
                }))
                .csrf((csrfConfig)->
                        csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
                                .ignoringRequestMatchers("/contact","/register","/user")
                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        )
//                .csrf(csrfConfig->csrfConfig.disable())
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthoritiesLoggingAfterFilter(),BasicAuthenticationFilter.class)
                .addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new JwtTokenGeneratorFilter(),BasicAuthenticationFilter.class)
                .addFilterBefore(new JwtTokenValidatorFilter(),BasicAuthenticationFilter.class)
                .authorizeHttpRequests((requests) ->
                requests
//                        .requestMatchers("/accounts","/loans","/cards","/balance").authenticated()
                        .requestMatchers("/accounts").hasAuthority("READ")
                        .requestMatchers("/balance").hasAnyAuthority("READ","WRITE")
                        .requestMatchers("/loans").hasAuthority("READ")
                        .requestMatchers("/cards").hasAuthority("READ")
                        .requestMatchers("/notices","/contact","/error","/register","/user").permitAll()

                );
        http.formLogin(withDefaults());
        http.httpBasic(withDefaults());
        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user= User.withUsername("user").password("{noop}password")
//                .authorities("READ").build();
//
//        UserDetails admin= User.withUsername("admin").password("{bcrypt}$2a$12$PNcmiGuQfg38Puw79fDiXeSadDMzghhtxgDwZXRQkEoxz4L7VMuFC")
//                .authorities("WRITE","READ").build();
//
//        return new InMemoryUserDetailsManager(user, admin);
//    }
//    @Bean
//    public UserDetailsService userDetailsService(DataSource dataSource) {
//        return new JdbcUserDetailsManager(dataSource);
//    }
    @Bean
    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return  new HaveIBeenPwnedRestApiPasswordChecker();
    }
}
