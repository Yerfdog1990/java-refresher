package com.baeldung.rwsb.commons.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class SimpleRequestBodyBuilder {
    private final ObjectNode jsonNodeTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    private SimpleRequestBodyBuilder(String jsonTemplate)
            throws JsonMappingException, JsonProcessingException {
        super();
        this.jsonNodeTemplate = (ObjectNode) mapper.readTree(jsonTemplate);
    }

    public static SimpleRequestBodyBuilder fromResource(Resource inputJsonFile) throws IOException {
        String jsonTemplate = readResource(inputJsonFile);
        return new SimpleRequestBodyBuilder(jsonTemplate);
    }

    public SimpleRequestBodyBuilder with(String field, String value) throws IOException {
        JsonNode nodeValue = TextNode.valueOf(value);
        this.jsonNodeTemplate.set(field, nodeValue);
        return this;
    }

    private static String readResource(Resource resource) {
        try {
            Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read resource", e);
        }
    }

    public String build() {
        return jsonNodeTemplate.toString();
    }
}
