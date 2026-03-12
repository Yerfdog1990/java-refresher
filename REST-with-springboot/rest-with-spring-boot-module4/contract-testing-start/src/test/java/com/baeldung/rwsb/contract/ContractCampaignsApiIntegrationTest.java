package com.baeldung.rwsb.contract;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.Matchers.hasItems;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContractCampaignsApiIntegrationTest {
    @Autowired
    WebTestClient webClient;

    @Test
    void givenPreloadedData_whenGetCampaigns_thenResponseFieldsMatch() {
        webClient.get()
                .uri("/campaigns")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$[?(@.code == 'C1')].tasks.length()")
                .isEqualTo(3)
                .jsonPath("$[?(@.code == 'C1')].tasks[?(@.name == 'Task 2')].description")
                .isEqualTo("Task 2 Description")
                .jsonPath("$..tasks..name")
                .value(hasItems("Task 1", "Task 2", "Task 3", "Task 4"));
    }
}
