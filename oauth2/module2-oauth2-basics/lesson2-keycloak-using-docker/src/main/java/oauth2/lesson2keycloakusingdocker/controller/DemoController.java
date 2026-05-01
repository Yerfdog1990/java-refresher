package oauth2.lesson2keycloakusingdocker.controller;

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
     * Response: "Hello Cyril! You are authenticated in the Yerfdog realm."
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
                "Hello Admin %s! You have full access to the Yerfdog admin area.".formatted(principal.getName())
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
