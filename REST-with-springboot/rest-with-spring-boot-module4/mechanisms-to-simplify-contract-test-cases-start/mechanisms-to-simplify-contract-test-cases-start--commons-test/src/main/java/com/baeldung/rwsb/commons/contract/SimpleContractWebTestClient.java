package com.baeldung.rwsb.commons.contract;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;

@Lazy
@Component
public class SimpleContractWebTestClient {

    private final WebTestClient webClient;

    public SimpleContractWebTestClient(WebTestClient webClient) {
        this.webClient = webClient;
    }

    // FIRST BASE APPROACH
    /* public void create(String url, String jsonBody) {
         webClient.post()
             .uri(url)
             .contentType(MediaType.APPLICATION_JSON)
             .bodyValue(jsonBody)
             .exchange()
             .expectStatus()
             .isCreated();
    }*/


    public SimpleContractBodyContentSpec create(String url, String jsonBody) {
        return new SimpleContractBodyContentSpec(webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonBody)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody());
    }

    public SimpleContractBodyContentSpec requestWithResponseStatus(
            String url, HttpMethod method, String jsonBody, HttpStatus responseStatus) {
        return new SimpleContractBodyContentSpec(webClient.method(method)
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonBody)
                .exchange()
                .expectStatus()
                .isEqualTo(responseStatus)
                .expectBody());
    }
}
