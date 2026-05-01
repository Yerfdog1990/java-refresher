package oauth2.lesson2keycloakusingdocker.config;

import org.springframework.core.convert.converter.Converter;
import jakarta.annotation.Nonnull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    /*
     * Spring's built-in converter — extracts "scope" and "scp" claims as SCOPE_xxx authorities.
     * We concat these with our custom Keycloak role authorities below.
     */
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    private final JwtAuthConverterProperties properties;

    public JwtAuthConverter(JwtAuthConverterProperties properties) {
        this.properties = properties;
        this.jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    }

    /**
     * Called by Spring Security on every incoming request.
     * Converts the validated JWT into a full Spring Authentication object.
     */
    @Override
    public AbstractAuthenticationToken convert(@Nonnull Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                Stream.concat(
                        // Standard scope-based authorities (e.g., SCOPE_profile, SCOPE_email)
                        jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                        // Custom Realm roles (e.g., ROLE_ADMIN, ROLE_USER)
                        extractRealmRoles(jwt).stream()
                ),
                // Custom Client roles (e.g., ROLE_ADMIN, ROLE_USER)
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
    }

    /**
     * Extracts realm-level roles assigned to the user in Keycloak.
     *
     * Keycloak JWT payload (relevant section):
     * {
     *   "realm_access": {
     *     "roles": ["ADMIN", "USER"]
     *   }
     * }
     */
    @SuppressWarnings("unchecked")
    private Collection<? extends GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) {
            return Collections.emptySet();
        }

        Collection<String> realmRoles = (Collection<String>) realmAccess.get("roles");
        if (realmRoles == null) {
            return Collections.emptySet();
        }

        return realmRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    /**
     * Resolves which JWT claim to use as the principal name.
     *
     * With principalAttribute = "preferred_username":
     *   → principal.getName() returns "Cyril"
     *
     * Without it (falls back to "sub"):
     *   → principal.getName() returns "a1b2c3-uuid-from-keycloak"
     */
    private String getPrincipalClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB; // fallback: Keycloak's internal UUID
        if (properties.getPrincipalAttribute() != null) {
            claimName = properties.getPrincipalAttribute();
        }
        return jwt.getClaim(claimName);
    }

    /**
     * Extracts client-level roles assigned to Cyril in Keycloak.
     *
     * Keycloak JWT payload (relevant section):
     * {
     *   "resource_access": {
     *     "yefdog-rest-api": {          ← your Client ID
     *       "roles": ["ADMIN", "USER"]  ← roles assigned to Cyril
     *     }
     *   }
     * }
     *
     * Spring Security's hasRole("ADMIN") checks for the authority "ROLE_ADMIN",
     * so we prepend "ROLE_" to every role string extracted from the JWT.
     *
     * Result:  "ADMIN" → SimpleGrantedAuthority("ROLE_ADMIN")
     */
    @SuppressWarnings("unchecked")
    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        // Step 1: Get the "resource_access" map from the JWT
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess == null) {
            return Collections.emptySet();
        }

        // Step 2: Get the section for your client ("yefdog-rest-api")
        Map<String, Object> resource = (Map<String, Object>) resourceAccess.get(properties.getResourceId());
        if (resource == null) {
            return Collections.emptySet();
        }

        // Step 3: Get the list of roles for this client
        Collection<String> resourceRoles = (Collection<String>) resource.get("roles");
        if (resourceRoles == null) {
            return Collections.emptySet();
        }

        // Step 4: Map each role to a Spring GrantedAuthority with "ROLE_" prefix
        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}
