package oauth2.lesson2keycloakusingdocker.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // Activates @PreAuthorize on controller methods
public class SecurityConfig {
    private final JwtAuthConverter jwtAuthConverter;

    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("http://localhost:8080/realms/Yerfdog/protocol/openid-connect/certs")
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                /*
                 * CSRF disabled — not needed for stateless REST APIs.
                 * CSRF protects browser-based session cookies; we use JWT Bearer tokens instead.
                 *
                 * Spring Security 7 syntax: AbstractHttpConfigurer::disable
                 * (old .csrf().disable() was removed)
                 */
                .csrf(AbstractHttpConfigurer::disable)

                /*
                 * Every endpoint requires a valid JWT.
                 * You can open specific paths here if needed, e.g.:
                 *   .requestMatchers("/api/v1/public/**").permitAll()
                 *   .anyRequest().authenticated()
                 */
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )

                /*
                 * Register this app as an OAuth2 Resource Server.
                 * Spring will:
                 *   1. Intercept the "Authorization: Bearer <token>" header
                 *   2. Fetch Keycloak's public keys from the jwk-set-uri
                 *   3. Validate the JWT signature, expiry, and issuer
                 *   4. Pass the validated JWT to our JwtAuthConverter
                 */
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthConverter)
                        )
                )

                /*
                 * Stateless sessions — Spring will NOT create an HttpSession.
                 * Each request must carry its own JWT; no server-side session storage.
                 */
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}
