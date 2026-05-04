package com.codewithbisky.keycloak.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.cache.concurrent.ConcurrentMapCache;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

    public WebSecurityConfig(@Lazy JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        String jwkSetUri = "http://localhost:9082/realms/Yerfdog/protocol/openid-connect/certs";
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .cache(new ConcurrentMapCache("jwk-set-cache"))
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        return httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/v3/api-docs/**", "/configuration/**", "/swagger-ui/**",
                                "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/api-docs/**",
                                "/users/{userId}/roles", "/users/{id}/groups")
                        .permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/users")
                        .permitAll()
                        .requestMatchers(HttpMethod.PUT, "/users/{id}/send-verification-email",
                                "/users/forgot-password")
                        .permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/users/{id}")
                        .permitAll()
                        .requestMatchers(HttpMethod.PUT, "/roles/assign/users/{userId}",
                                "/{groupId}/assign/users/{userId}")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/roles/remove/users/{userId}",
                                "/{groupId}/remove/users/{userId}")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/rbac/merge-role", "/users/{id}/groups",
                                "/users/{id}/roles")
                        .hasAnyRole("MERGE", "ADMIN")
                        .requestMatchers("/public/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthConverter)
                        )
                        .authenticationEntryPoint((request, response, authException) -> {
                            System.out.println("=== OAUTH2 ENTRY POINT DEBUG ===");
                            System.out.println("Request URI: " + request.getRequestURI());
                            System.out.println("Auth Exception: " + authException.getMessage());
                            System.out.println("=== END OAUTH2 ENTRY POINT DEBUG ===");
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
