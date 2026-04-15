# OAuth2 Client Support

## 1. Overview & Goal

The goal of this lesson is to implement the **OAuth2 Client** as part of a simple OAuth application. It uses the new OAuth stack, which now provides full client support.

Three services form the application:

| Service | Port | Context Path | Role |
|---|---|---|---|
| `lsso-client` | 8082 | `/lsso-client` | OAuth2 client application |
| `lsso-resource-server` | 8081 | `/lsso-resource-server` | Protected API |
| Keycloak (Auth Server) | 8083 | `/auth` | Authorization server, realm: `baeldung` |

---

## 2. Keycloak Realm & Base URL

A **Realm** in Keycloak is a namespace and security boundary. All authentication, token issuance, and user operations are scoped under the realm path:

```
http://localhost:8083/auth/realms/baeldung
```

Every important endpoint is derived from this base path.

> **Version note:** In newer versions of Keycloak the `/auth` prefix is removed. The base URL becomes `http://localhost:8083/realms/baeldung`. Always check your Keycloak version.

### Key Endpoints Under the Realm

| Endpoint | Path | Purpose |
|---|---|---|
| Discovery | `/.well-known/openid-configuration` | Spring Security auto-discovers all other endpoints from here |
| Authorization | `/protocol/openid-connect/auth` | Where the browser is redirected for login |
| Token | `/protocol/openid-connect/token` | Exchange authorization code for access/ID tokens |
| Logout | `/protocol/openid-connect/logout` | Terminate the session in Keycloak |
| User Info | `/protocol/openid-connect/userinfo` | Fetch authenticated user profile claims |
| JWKS (Public Keys) | `/protocol/openid-connect/certs` | Used by Spring Security to verify JWT signatures |

> **Tip:** The discovery endpoint `/.well-known/openid-configuration` is the most important URL. Providing it as the `issuer-uri` in Spring lets Spring automatically configure all other endpoints — you rarely need to list them individually.

---

## 3. Authorization Code Flow

1. User hits a protected route on the client app (`localhost:8082/lsso-client`)
2. Spring Security redirects the browser to Keycloak's authorization endpoint for login
3. User logs in; Keycloak redirects back to the client's registered redirect URI with an authorization code
4. Client app exchanges the code at the token endpoint for access and ID tokens
5. Client app uses the access token as a Bearer token when calling the resource server (`localhost:8081`)
6. Resource server validates the JWT using the JWKS endpoint and serves the response if valid

---

## 4. Client Application Configuration

The client runs on port **8082** with context path `/lsso-client`. Registration is named `custom` and uses the `authorization_code` grant type.

```yaml
spring:
  thymeleaf:
    cache: false

  security:
    oauth2:
      client:
        registration:
          custom:
            client-id: lssoClient
            client-secret: lssoSecret
            scope: read, write
            authorization-grant-type: authorization_code
            redirect-uri: http://localhost:8082/lsso-client/login/oauth2/code/custom

        provider:
          custom:
            authorization-uri: http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/auth
            token-uri:         http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/token
            user-info-uri:     http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/userinfo
            user-name-attribute: preferred_username

  devtools:
    livereload:
      port: 35729

server:
  port: 8082
  servlet:
    context-path: /lsso-client

resourceserver:
  api:
    project:
      url: http://localhost:8081/lsso-resource-server/api/projects/
```

> **Note on `user-name-attribute`:** Setting this to `preferred_username` tells Spring which claim from the userinfo response to use as the principal name. Keycloak populates this with the user's login name.

---

## 5. Resource Server Configuration

The resource server runs on port **8081**. It validates incoming JWTs using either the issuer URI (for auto-discovery) or the JWKS URI directly.

```yaml
server:
  port: 8081
  servlet:
    context-path: /lsso-resource-server

spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8083/auth/realms/baeldung
          jwk-set-uri: http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/certs
  jpa:
    open-in-view: false
  devtools:
    livereload:
      port: 35730
```

> **How it works:** When `issuer-uri` is set, Spring calls the discovery endpoint at startup and auto-configures the JWKS URI, token validation, and issuer claim checking. Providing `jwk-set-uri` explicitly is also supported if discovery is not desired.

---

## 6. Keycloak (Authorization Server) Configuration

The embedded Keycloak server runs on port **8083**. It uses an H2 in-memory database and imports its realm definition from a JSON file.

```yaml
server:
  port: 8083

spring:
  datasource:
    username: sa
    url: jdbc:h2:mem:testdb
  jpa:
    open-in-view: false

keycloak:
  server:
    contextPath: /auth
    adminUser:
      username: bael-admin
      password: pass
    realmImportFile: baeldung-realm.json

logging:
  level:
    liquibase: error
```

> **Realm import:** The `baeldung-realm.json` file pre-configures the realm (clients, scopes, users, roles) so you do not have to set them up manually via the admin console each time.

---

## 7. Spring Security — How the Pieces Connect

### OAuth2 Login (Client Side)

When using `issuer-uri` on the provider, Spring automatically calls the discovery endpoint and wires up the authorization and token endpoints. Alternatively, each endpoint can be listed explicitly as shown in the client config above.

**Shorthand (using `issuer-uri` only):**

```yaml
spring:
  security:
    oauth2:
      client:
        provider:
          keycloak:
            issuer-uri: http://localhost:8083/auth/realms/baeldung
```

### Resource Server (JWT Validation)

Spring Security reads the `issuer-uri`, fetches the JWKS from Keycloak's certs endpoint, and uses the public keys to verify every incoming JWT's signature and validate the `iss` claim.

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8083/auth/realms/baeldung
```

---

## 8. Key Concepts Summary

| Concept | Description |
|---|---|
| **Realm** | A namespace in Keycloak. Every endpoint, user, client, and token is scoped under `/realms/{realm-name}`. |
| **Discovery endpoint** | `/.well-known/openid-configuration` — the single URL from which Spring can discover all other OAuth2/OIDC endpoints automatically. |
| **Client registration** | Identifies the app to Keycloak with a `client-id`, `client-secret`, grant type, scopes, and redirect URI. |
| **Provider** | Tells Spring where to find Keycloak's authorization, token, and userinfo endpoints (or just `issuer-uri` for auto-discovery). |
| **JWKS / `jwk-set-uri`** | Public keys endpoint used by the resource server to verify JWT signatures without calling Keycloak on every request. |
| **`preferred_username`** | The Keycloak claim used as the Spring Security principal name, set via `user-name-attribute`. |

---