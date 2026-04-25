package com.baeldung.lsso.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class KeycloakController {

    @GetMapping("/realms/baeldung/.well-known/openid_configuration")
    public ResponseEntity<String> openidConfiguration() {
        String config = """
            {
              "issuer": "http://localhost:8083/auth/realms/baeldung",
              "authorization_endpoint": "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/auth",
              "token_endpoint": "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/token",
              "userinfo_endpoint": "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/userinfo",
              "end_session_endpoint": "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/logout",
              "jwks_uri": "http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/certs",
              "response_types_supported": ["code", "code id_token", "id_token", "token id_token"],
              "grant_types_supported": ["authorization_code", "implicit", "refresh_token", "password", "client_credentials"],
              "subject_types_supported": ["public", "pairwise"],
              "id_token_signing_alg_values_supported": ["RS256"],
              "token_endpoint_auth_methods_supported": ["client_secret_basic", "client_secret_post"]
            }
            """;
        return ResponseEntity.ok(config);
    }

    @GetMapping("/realms/baeldung/protocol/openid-connect/certs")
    public ResponseEntity<String> certs() {
        // Return a placeholder JWKS response
        String jwks = """
            {
              "keys": [
                {
                  "kty": "RSA",
                  "kid": "test-key-id",
                  "use": "sig",
                  "n": "test-n-value",
                  "e": "AQAB",
                  "alg": "RS256"
                }
              ]
            }
            """;
        return ResponseEntity.ok(jwks);
    }

    @GetMapping("/realms/baeldung/protocol/openid-connect/auth")
    public ResponseEntity<String> auth() {
        // Return a simple login form HTML
        String loginForm = """
            <!DOCTYPE html>
            <html>
            <head><title>Login</title></head>
            <body>
            <h2>Login</h2>
            <form method="post" action="http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/auth-login">
                <input type="hidden" name="session_code" value="test-session-code"/>
                <input type="hidden" name="execution" value="test-execution"/>
                <input type="hidden" name="client_id" value="lssoClient"/>
                <input type="hidden" name="redirect_uri" value="http://localhost:8082/lsso-client/login/oauth2/code/custom"/>
                <input type="hidden" name="response_type" value="code"/>
                <input type="hidden" name="scope" value="read"/>
                <label>Username: <input type="text" name="username" value="john@test.com"/></label><br/>
                <label>Password: <input type="password" name="password" value="123"/></label><br/>
                <input type="submit" value="Login"/>
            </form>
            </body>
            </html>
            """;
        return ResponseEntity.ok()
            .header("Content-Type", "text/html")
            .body(loginForm);
    }

    @PostMapping("/realms/baeldung/protocol/openid-connect/auth-login")
    public ResponseEntity<String> authLogin() {
        // Simulate successful authentication and redirect with authorization code
        return ResponseEntity.status(302)
            .header("Location", "http://localhost:8082/lsso-client/login/oauth2/code/custom?code=test-auth-code&state=test-state")
            .body("Redirecting...");
    }

    @PostMapping("/realms/baeldung/protocol/openid-connect/token")
    public ResponseEntity<String> token() {
        // Return a mock token response
        String tokenResponse = """
            {
              "access_token": "test-access-token",
              "token_type": "Bearer",
              "expires_in": 3600,
              "refresh_token": "test-refresh-token",
              "scope": "read"
            }
            """;
        return ResponseEntity.ok()
            .header("Content-Type", "application/json")
            .body(tokenResponse);
    }
}
