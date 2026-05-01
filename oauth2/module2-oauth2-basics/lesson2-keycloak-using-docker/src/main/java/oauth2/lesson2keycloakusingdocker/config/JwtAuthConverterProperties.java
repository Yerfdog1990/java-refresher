package oauth2.lesson2keycloakusingdocker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt.auth.converter")
public class JwtAuthConverterProperties {
    /**
     * The Keycloak Client ID — must match exactly what you created in the admin console.
     * Example: "yefdog-rest-api"
     *
     * Keycloak stores client-level roles in the JWT under:
     *   resource_access.<resourceId>.roles
     */
    private String resourceId;

    /**
     * The JWT claim to use as the authenticated principal's name.
     * "preferred_username" → returns "Cyril"
     * "sub"               → returns Keycloak's internal UUID (less readable)
     * "email"             → returns Cyril's email address
     */
    private String principalAttribute;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getPrincipalAttribute() {
        return principalAttribute;
    }

    public void setPrincipalAttribute(String principalAttribute) {
        this.principalAttribute = principalAttribute;
    }
}
