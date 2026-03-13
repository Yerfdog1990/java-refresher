package com.baeldung.rwsb.commons.contract;

import org.hamcrest.Matcher;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;
import java.util.stream.Stream;

public class SimpleContractBodyContentSpec {
    private WebTestClient.BodyContentSpec contentBodySpec;

    public SimpleContractBodyContentSpec(WebTestClient.BodyContentSpec contentBodySpec) {
        super();
        this.contentBodySpec = contentBodySpec;
    }

    public SimpleContractBodyContentSpec containsFields(String... fields) {
        Stream.of(fields)
                .forEach(field -> contentBodySpec.jsonPath("$.%s".formatted(field))
                        .isNotEmpty());
        return this;
    }

    @SafeVarargs
    public final SimpleContractBodyContentSpec fieldsMatch(Map.Entry<String, Matcher<?>>... fields) {
        Stream.of(fields)
                .forEach(field -> contentBodySpec.jsonPath("$.%s".formatted(field.getKey()))
                        .value(field.getValue()));
        return this;
    }
}
