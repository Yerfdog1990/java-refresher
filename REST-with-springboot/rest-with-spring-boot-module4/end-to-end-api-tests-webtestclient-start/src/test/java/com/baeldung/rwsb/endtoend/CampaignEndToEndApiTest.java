package com.baeldung.rwsb.endtoend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CampaignEndToEndApiTest {
    @Autowired
    WebTestClient webClient;

    @Test
    void givenRunningService_whenGetSingleCampaign_thenExpectStatus() {
        webClient.get()
                .uri("/campaigns/3")
                .exchange()
                .expectStatus()
                .isOk();
    }
}


