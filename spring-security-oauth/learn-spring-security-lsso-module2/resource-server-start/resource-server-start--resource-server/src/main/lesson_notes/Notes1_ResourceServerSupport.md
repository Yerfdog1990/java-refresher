# The New Resource Server Support

---

## 1. Goal

Build a simple **Resource Server** using the new OAuth 2.0 stack in Spring Security 5. The lesson covers both property-based configuration and modern Java-based security configuration using a `SecurityFilterChain` bean.

---

## 2. Bearer Token Types

Spring Security 5 supports two forms of OAuth 2.0 Bearer Tokens:

| Type | How it Works | Verification |
|------|-------------|--------------|
| **JWT** | User information is *embedded* inside the token itself. | Resource Server verifies the token **locally** using the Authorization Server's public key. |
| **Opaque** | The token has no inherent meaning on its own. | Resource Server must call the Authorization Server **remotely** (token introspection) to validate it. |

> **Note:** This lesson uses JWTs because they are the Keycloak Authorization Server default. Deep-dives into both token types happen in later lessons.

---

## 3. Maven Dependency

Add the Spring Boot OAuth2 Resource Server starter. It transitively pulls in `spring-security-oauth2-resource-server` and the core Spring Security libraries, so the generic `spring-boot-starter-security` can be removed.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

---

## 4. Spring Boot Property Configuration

The Resource Server must establish a **trust relationship** with the Authorization Server so it can verify token signatures. Two YAML properties control this; using both together is best practice.

### Option A — Issuer URI (discovery-based)

Spring Security uses OIDC discovery (`/.well-known/openid-configuration`) to automatically locate the public key endpoint.

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8083/auth/realms/baeldung
```

> ⚠️ **Disadvantage (pre Spring Boot 2.6):** The Authorization Server *must be running* when the Resource Server starts, because the public key is fetched eagerly. From Spring Boot 2.6 onward, a `SupplierJwtDecoder` is auto-configured and the key is loaded *lazily*, removing this constraint.

### Option B — Explicit JWK Set URI

Directly specify the full URL of the public key endpoint. Removes the discovery requirement entirely.

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/certs
```

### Recommended — Both Together

Providing both properties is best practice: `jwk-set-uri` ensures the key endpoint is always known, while `issuer-uri` is used by the framework as an extra validation check (the `iss` claim is verified against this value).

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8083/auth/realms/baeldung
          jwk-set-uri: http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/certs
```

> ✅ With just these two lines the entire application is a functional Resource Server. All endpoints are secured by default and require a valid Bearer token.

---

## 5. Java Configuration — `ResourceSecurityConfig`

The legacy approach extended `WebSecurityConfigurerAdapter`, which is **deprecated** since Spring Security 5.7 and removed in 6.x. The modern approach exposes a `SecurityFilterChain` bean directly.

### Modern Configuration (SecurityFilterChain Bean)

```java
@Configuration
@EnableWebSecurity
public class ResourceSecurityConfig {

    /**
     * Expose a SecurityFilterChain bean instead of extending
     * WebSecurityConfigurerAdapter (deprecated / removed in Spring Security 6).
     *
     * Configures the application as an OAuth 2.0 Resource Server
     * that validates JWT Bearer Tokens.
     *
     * The actual public-key / issuer details are resolved from
     * application.yml (issuer-uri + jwk-set-uri), so no extra wiring
     * is needed here unless you want to override those defaults.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            // Require authentication for every request
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            // Declare this application as a Resource Server using JWTs
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            );

        return http.build();
    }
}
```

### Key API Points

1. **`authorizeHttpRequests()`** — replaces the old `authorizeRequests()`. Uses the newer `AuthorizationManager`-based infrastructure.
2. **`oauth2ResourceServer()`** — activates Resource Server support; without this the app behaves as a plain web application.
3. **`.jwt(Customizer.withDefaults())`** — signals that Bearer Tokens will be JWT-formatted. Spring Boot auto-wires the `JwtDecoder` from your YAML properties. Pass a lambda here to further customise the decoder.
4. **`http.build()`** — returns the assembled `SecurityFilterChain` bean registered with Spring's application context.

### Optional — Programmatic `JwtDecoder` Override

If you need to override the YAML-configured decoder (e.g. for testing or custom claim validation), register your own `JwtDecoder` bean:

```java
@Bean
public JwtDecoder jwtDecoder() {
    // Spring Security will use this instead of the auto-configured one.
    return NimbusJwtDecoder
        .withJwkSetUri("http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/certs")
        .build();
}
```

---

## 6. Verifying the Setup (Authorization Code Flow)

1. Start the **Keycloak Authorization Server** first.
2. Start the **Resource Server**. No errors on startup = trust relationship established.
3. Execute the Authorization Code flow to obtain an **Access Token**: redirect → Authorization Code → exchange for token.
4. `GET /projects` *without* a token → **401 Unauthorized** ✓
5. `GET /projects` with `Authorization: Bearer <token>` → **200 OK** + resource data ✓

---

## 7. Summary

| Approach | What's Needed | Use When |
|----------|--------------|----------|
| **Property-only** | `issuer-uri` and/or `jwk-set-uri` in YAML | Standard setups — covers the vast majority of use cases with zero Java code. |
| **Java config (basic)** | `SecurityFilterChain` bean with `oauth2ResourceServer().jwt()` | When you need to customise security rules (URL patterns, roles, CSRF, CORS…). |
| **Java config (advanced)** | Custom `JwtDecoder`, `JwtAuthenticationConverter`, etc. | Custom claim mapping, multi-tenant token validation, adding extra validators. |

> **Key takeaway:** A fully functional Resource Server can be bootstrapped with **a single YAML property**. The Java configuration exists for anything beyond the defaults and will be explored in depth in later lessons.

---