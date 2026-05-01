# Keycloak + Spring Boot Integration Guide
### Keycloak 26.6.1 · Spring Boot 4 · Spring Security 7 · Java 21
### Your setup: Realm = `Yerfdog` · User = `Cyril`

---

## Table of Contents
1. [Keycloak on Docker](#1-keycloak-on-docker)
2. [Keycloak Admin Setup (Your Realm & User)](#2-keycloak-admin-setup-your-realm--user)
3. [Spring Boot Project — pom.xml](#3-spring-boot-project--pomxml)
4. [Application Configuration — application.yml](#4-application-configuration--applicationyml)
5. [JwtAuthConverterProperties.java](#5-jwtauthconverterpropertiesjava)
6. [JwtAuthConverter.java](#6-jwtauthconverterjava)
7. [SecurityConfig.java](#7-securityconfigjava)
8. [DemoController.java](#8-democontrollerjava)
9. [KeycloakDemoApplication.java](#9-keycloakdemoapplicationjava)
10. [Testing with Postman / curl](#10-testing-with-postman--curl)
11. [How It All Connects](#11-how-it-all-connects)
12. [Common Errors & Fixes](#12-common-errors--fixes)

---

## 1. Keycloak on Docker

> **Official source:** https://www.keycloak.org/getting-started/getting-started-docker

Run this single command to start Keycloak 26.6.1 in development mode:

```bash
docker run -p 127.0.0.1:8080:8080 \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:26.6.1 \
  start-dev
```

| What | Value |
|---|---|
| Admin Console URL | http://localhost:8080/admin |
| Admin username | `admin` |
| Admin password | `admin` |
| Image registry | `quay.io/keycloak/keycloak:26.6.1` |

> **Keycloak 26 note:** The environment variables changed. Use `KC_BOOTSTRAP_ADMIN_USERNAME` and `KC_BOOTSTRAP_ADMIN_PASSWORD` — NOT the old `KEYCLOAK_ADMIN` / `KEYCLOAK_ADMIN_PASSWORD` used in versions before 21.

---

## 2. Keycloak Admin Setup (Your Realm & User)

You already have:
- ✅ Realm: **`Yefdog`**
- ✅ User: **`Cyril`**

Complete the remaining steps below.

### 2a. Create a Client

1. Log in to http://localhost:8080/admin
2. Switch to the **Yefdog** realm (top-left dropdown).
3. Click **Clients** → **Create client**
4. Fill in:
    - **Client type:** `OpenID Connect`
    - **Client ID:** `yerfdog-rest-api`
5. Click **Next**
6. On *Capability config*:
    - Enable **Direct access grants** (needed for password-based token generation in dev/testing)
    - Leave **Client authentication** OFF (public client)
7. Click **Next**
8. On *Login settings*:
    - **Valid redirect URIs:** `http://localhost:8081/*`
    - **Web origins:** `*`
9. Click **Save**

### 2b. Create Realm Roles

1. Click **Realm roles** in the left menu → **Create role**
2. Create role: `ADMIN` → **Save**
3. Create role: `USER` → **Save**

### 2c. Set a Password for Cyril

1. Click **Users** → click on `Cyril`
2. Click the **Credentials** tab → **Set password**
3. Enter a password (e.g., `cyril123`)
4. Set **Temporary** → **Off**
5. Click **Save password**

### 2d. Assign a Role to Cyril

1. Still on Cyril's page → click **Role mapping** tab
2. Click **Assign role**
3. Select `ADMIN` → **Assign**

### 2e. Verify Cyril's Token Endpoint

The token endpoint for your realm is:
```
http://localhost:8080/realms/Yerfdog/protocol/openid-connect/token
```

The OpenID discovery document (useful reference):
```
http://localhost:8080/realms/Yerfdog/.well-known/openid-configuration
```

---

## 3. Spring Boot Project — pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.0</version>  <!-- Spring Boot 4 requires Java 21+ -->
    <relativePath/>
  </parent>

  <groupId>com.yefdog</groupId>
  <artifactId>keycloak-demo</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <name>keycloak-demo</name>
  <description>Yefdog API secured with Keycloak 26 + Spring Boot 4</description>

  <properties>
    <java.version>21</java.version>
  </properties>

  <dependencies>

    <!-- REST API support -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Security core -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!--
      OAuth2 Resource Server — validates the JWT that Keycloak issues.
      This replaces the old keycloak-spring-boot-adapter which is
      REMOVED in Keycloak 26 / Spring Boot 3+/4+.
    -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    </dependency>

    <!-- Lombok — reduces boilerplate -->
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
    </dependency>

    <!-- Tests -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework.security</groupId>
      <artifactId>spring-security-test</artifactId>
      <scope>test</scope>
    </dependency>

  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <configuration>
          <excludes>
            <exclude>
              <groupId>org.projectlombok</groupId>
              <artifactId>lombok</artifactId>
            </exclude>
          </excludes>
        </configuration>
      </plugin>
    </plugins>
  </build>

</project>
```

---

## 4. Application Configuration — application.yml

`src/main/resources/application.yml`

```yaml
server:
  port: 8081   # Keycloak already uses 8080

spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          # Tells Spring who issued the token — must match the "iss" field inside the JWT.
          # Format: http://localhost:8080/realms/<realm-name>
          issuer-uri: http://localhost:8080/realms/Yerfdog

          # Where Spring fetches Keycloak's public keys to verify JWT signatures.
          # Providing this explicitly avoids startup failures if Keycloak is slow to start.
          jwk-set-uri: ${spring.security.oauth2.resourceserver.jwt.issuer-uri}/protocol/openid-connect/certs

# Custom properties read by JwtAuthConverterProperties
jwt:
  auth:
    converter:
      # Must match the Client ID you created in Keycloak: "yefdog-rest-api"
      # Spring will look inside: resource_access.yefdog-rest-api.roles
      resource-id: yefdog-rest-api

      # Which JWT claim to use as the principal (login) name in Spring Security.
      # "preferred_username" gives you "Cyril" instead of Keycloak's internal UUID.
      principal-attribute: preferred_username
```

---

## 5. JwtAuthConverterProperties.java

This class binds the `jwt.auth.converter.*` properties from your YAML into a typed object.

`src/main/java/com/yefdog/keycloakdemo/config/JwtAuthConverterProperties.java`

```java
package com.ouath2.keycloakdemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
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
}
```

---

## 6. JwtAuthConverter.java

This is the core of the Keycloak-Spring integration. It extracts roles from the JWT and converts them into Spring Security `GrantedAuthority` objects.

`src/main/java/com/yefdog/keycloakdemo/config/JwtAuthConverter.java`

```java
package com.oauth2.keycloakdemo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
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
@RequiredArgsConstructor
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    /*
     * Spring's built-in converter — extracts "scope" and "scp" claims as SCOPE_xxx authorities.
     * We concat these with our custom Keycloak role authorities below.
     */
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    private final JwtAuthConverterProperties properties;

    /**
     * Called by Spring Security on every incoming request.
     * Converts the validated JWT into a full Spring Authentication object.
     */
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                // Standard scope-based authorities (e.g., SCOPE_profile, SCOPE_email)
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                // Our custom Keycloak role authorities (e.g., ROLE_ADMIN, ROLE_USER)
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
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
        Map<String, Object> resource =
                (Map<String, Object>) resourceAccess.get(properties.getResourceId());
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
```

---

## 7. SecurityConfig.java

`src/main/java/com/oauth2/keycloakdemo/config/SecurityConfig.java`

```java
package com.oauth2.keycloakdemo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // Activates @PreAuthorize on controller methods
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

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
```

> **Spring Security 7 changes vs. 6:**
> - Lambda DSL is now the **only** style. The deprecated `.and()` chaining is removed.
> - `authorizeRequests()` → `authorizeHttpRequests()`
> - `csrf().disable()` → `csrf(AbstractHttpConfigurer::disable)`

---

## 8. DemoController.java

`src/main/java/com/yefdog/keycloakdemo/controller/DemoController.java`

```java
package com.oauth.keycloakdemo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/demo")
public class DemoController {

    /**
     * Open to any authenticated user.
     * Cyril can access this with any valid token.
     *
     * GET http://localhost:8081/api/v1/demo/hello
     * Authorization: Bearer <cyril's token>
     *
     * Response: "Hello Cyril! You are authenticated in the Yefdog realm."
     */
    @GetMapping("/hello")
    public ResponseEntity<String> hello(Principal principal) {
        return ResponseEntity.ok(
            "Hello %s! You are authenticated in the Yerfdog realm.".formatted(principal.getName())
        );
    }

    /**
     * Restricted to users with the ADMIN role.
     * If Cyril has the ADMIN role in Keycloak → 200 OK
     * If Cyril only has USER role → 403 Forbidden
     *
     * GET http://localhost:8081/api/v1/demo/hello-admin
     * Authorization: Bearer <cyril's token>
     *
     * @PreAuthorize("hasRole('ADMIN')") checks for the authority "ROLE_ADMIN"
     * which JwtAuthConverter adds when it sees "ADMIN" in Cyril's JWT roles.
     */
    @GetMapping("/hello-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> helloAdmin(Principal principal) {
        return ResponseEntity.ok(
            "Hello Admin %s! You have full access to the Yefdog admin area.".formatted(principal.getName())
        );
    }

    /**
     * Restricted to users with the USER role.
     *
     * GET http://localhost:8081/api/v1/demo/hello-user
     * Authorization: Bearer <cyril's token>
     */
    @GetMapping("/hello-user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> helloUser(Principal principal) {
        return ResponseEntity.ok(
            "Hello %s! You have standard user access.".formatted(principal.getName())
        );
    }

    /**
     * Accessible by ADMIN or USER — either role grants entry.
     *
     * GET http://localhost:8081/api/v1/demo/hello-any
     */
    @GetMapping("/hello-any")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<String> helloAny(Principal principal) {
        return ResponseEntity.ok(
            "Hello %s! You have at least basic access.".formatted(principal.getName())
        );
    }

    /**
     * Shows all the JWT claims — useful for debugging Cyril's token.
     * Remove this in production!
     *
     * GET http://localhost:8081/api/v1/demo/token-info
     */
    @GetMapping("/token-info")
    public ResponseEntity<?> tokenInfo(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(jwt.getClaims());
    }
}
```

---

## 9. KeycloakDemoApplication.java

`src/main/java/com/yefdog/keycloakdemo/KeycloakDemoApplication.java`

```java
package com.yefdog.keycloakdemo;

import com.yefdog.keycloakdemo.config.JwtAuthConverterProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtAuthConverterProperties.class)
public class KeycloakDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(KeycloakDemoApplication.class, args);
    }
}
```

---

## 10. Testing with Postman / curl

### Step 1 — Get Cyril's Access Token

**Postman:**
```
POST http://localhost:8080/realms/Yefdog/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type    = password
client_id     = yefdog-rest-api
username      = Cyril
password      = cyril123
```

**curl equivalent:**
```bash
curl -X POST http://localhost:8080/realms/Yefdog/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=yefdog-rest-api" \
  -d "username=Cyril" \
  -d "password=cyril123"
```

**Expected response:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "scope": "email profile"
}
```

Copy the `access_token` value.

---

### Step 2 — Call Protected Endpoints

**Any authenticated user (Cyril):**
```bash
curl http://localhost:8081/api/v1/demo/hello \
  -H "Authorization: Bearer <paste_access_token_here>"

# → Hello Cyril! You are authenticated in the Yefdog realm.
```

**Admin-only endpoint (works if Cyril has ADMIN role):**
```bash
curl http://localhost:8081/api/v1/demo/hello-admin \
  -H "Authorization: Bearer <paste_access_token_here>"

# → 200 OK: Hello Admin Cyril! You have full access to the Yefdog admin area.
# → 403 Forbidden if Cyril does NOT have the ADMIN role
```

**No token → always rejected:**
```bash
curl http://localhost:8081/api/v1/demo/hello

# → 401 Unauthorized
```

**Inspect Cyril's token claims:**
```bash
curl http://localhost:8081/api/v1/demo/token-info \
  -H "Authorization: Bearer <paste_access_token_here>"

# → JSON map of all JWT claims including resource_access, preferred_username, etc.
```

---

### Step 3 — Decode the JWT (optional)

Paste the `access_token` at https://jwt.io to inspect the payload. For your setup it should contain:

```json
{
  "iss": "http://localhost:8080/realms/Yefdog",
  "preferred_username": "Cyril",
  "resource_access": {
    "yefdog-rest-api": {
      "roles": ["ADMIN"]
    }
  },
  "realm_access": {
    "roles": ["ADMIN", "offline_access", "uma_authorization"]
  }
}
```

---

## 11. How It All Connects

```
Cyril's browser / Postman
        │
        │  POST /realms/Yefdog/protocol/openid-connect/token
        ▼
┌──────────────────────┐
│   Keycloak :8080     │  Validates credentials, looks up Cyril's roles,
│   Realm: Yefdog      │  generates a signed JWT containing resource_access.
│   User: Cyril        │  Returns JWT to Cyril.
└──────────────────────┘
        │
        │  GET /api/v1/demo/hello-admin
        │  Authorization: Bearer <JWT>
        ▼
┌──────────────────────────────────────────────────┐
│   Spring Boot App :8081                          │
│                                                  │
│  SecurityFilterChain                             │
│    └─ oauth2ResourceServer                       │
│         └─ fetches public keys from              │
│            /realms/Yefdog/protocol/openid-       │
│            connect/certs  (jwk-set-uri)          │
│         └─ validates JWT signature & expiry      │
│         └─ calls JwtAuthConverter.convert()      │
│              └─ reads resource_access            │
│                   .yefdog-rest-api               │
│                   .roles → ["ADMIN"]             │
│              └─ creates ROLE_ADMIN authority     │
│                                                  │
│  @PreAuthorize("hasRole('ADMIN')")               │
│    └─ checks ROLE_ADMIN → ✅ access granted      │
└──────────────────────────────────────────────────┘
```

---

## 12. Common Errors & Fixes

| Error | Cause | Fix |
|---|---|---|
| `401 Unauthorized` | No token, expired token, or Keycloak down | Generate a fresh token; check Docker is running |
| `403 Forbidden` | Token valid but Cyril lacks the required role | Go to Keycloak Admin → Users → Cyril → Role mapping → Assign role |
| `invalid_grant` on token request | Wrong username/password or Direct access grants disabled | Check credentials; enable Direct access grants on the client |
| `Connection refused to :8080` | Keycloak container not running | Run `docker ps`; restart with the `docker run` command above |
| `Could not resolve placeholder 'jwt.auth.converter...'` | Missing `jwt.auth.converter` block in yml | Add the block from Section 4 exactly as shown |
| `invalid_token` — wrong issuer | `issuer-uri` realm name doesn't match | Realm name is case-sensitive; it must be `Yefdog` not `yefdog` |
| `JwkException` at startup | Spring can't reach Keycloak on startup | Add `jwk-set-uri` explicitly (as shown) so Spring doesn't block startup waiting for issuer discovery |
| Roles not appearing in Spring Security | `resource-id` in yml doesn't match Client ID | Ensure `resource-id: yefdog-rest-api` matches exactly what's in Keycloak Clients |
| `NullPointerException` in `JwtAuthConverter` | `resource_access` claim missing from token | Ensure Cyril has at least one client role assigned in Keycloak |

---

## Project File Layout

```
keycloak-demo/
├── pom.xml
└── src/
    └── main/
        ├── java/com/yefdog/keycloakdemo/
        │   ├── KeycloakDemoApplication.java
        │   ├── config/
        │   │   ├── JwtAuthConverter.java
        │   │   ├── JwtAuthConverterProperties.java
        │   │   └── SecurityConfig.java
        │   └── controller/
        │       └── DemoController.java
        └── resources/
            └── application.yml
```

---

