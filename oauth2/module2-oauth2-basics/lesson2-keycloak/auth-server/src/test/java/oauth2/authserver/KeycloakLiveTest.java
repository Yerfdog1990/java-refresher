package oauth2.authserver;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class KeycloakLiveTest {

    @LocalServerPort
    private int port;

    @Test
    public void whenLoadKeycloakHealth_thenStatusIsUp() {
        RestAssured.given()
                .port(port)
                .when()
                .get("/auth/health")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    public void whenLoadKeycloakMasterRealm_thenNameIsMaster() {
        RestAssured.given()
                .port(port)
                .when()
                .get("/auth/realms/master")
                .then()
                .statusCode(200)
                .body("realm", equalTo("master"));
    }
}
