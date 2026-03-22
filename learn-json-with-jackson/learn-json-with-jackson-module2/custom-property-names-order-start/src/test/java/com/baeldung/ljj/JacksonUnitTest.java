package com.baeldung.ljj;

import com.baeldung.ljj.domain.model.Campaign;
import com.baeldung.ljj.domain.model.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JacksonUnitTest {

    final JsonMapper objectMapper = new JsonMapper();

    @Test
    // Serializing With Jackson
    void givenFieldAndGetter_whenSerializing_thenJacksonBehaviorChecked() throws JsonProcessingException {
        // Given
        Campaign campaign = new Campaign("CODE01", "Campaign Name", "Description");

        // When
        String json = objectMapper.writeValueAsString(campaign);
        System.out.println(json);

        // Then
        assertTrue(json.contains("\"code\""));
        assertFalse(json.contains("\"theCode\""));
        assertTrue(json.contains("\"code\":\"code01\""));
    }

    @Test
    // Renaming Properties with @JsonProperty
    void givenPropertyWithCustomName_whenSerializing_thenCustomNameAppearsInJson() throws JsonProcessingException {
        // Given
        Campaign original = new Campaign("T101", "Rename", "Custom property name");

        // When
        String json = objectMapper.writeValueAsString(original);

        // Then
        assertTrue(json.contains("\"is_closed\""));
        assertFalse(json.contains("\"closed\""));
    }

    @Test
    // Serialization Order with @JsonPropertyOrder
    void givenJsonPropertyOrder_whenSerializing_thenKeysAppearInSequence() throws JsonProcessingException {
        // Given
        Campaign original = new Campaign("T101", "Rename", "JSON property order");

        // When
        String json = objectMapper.writeValueAsString(original);
        System.out.println(json);
        int idxCode = json.indexOf("\"code\"");
        int idxName = json.indexOf("\"name\"");
        int idxDescription = json.indexOf("\"description\"");
        int idxClosed = json.indexOf("\"is_closed\"");
        int idxTasks = json.indexOf("\"tasks\"");

        // Then
        assertTrue(idxCode < idxName);
        assertTrue(idxName < idxDescription);
        assertTrue(idxDescription < idxClosed);
        assertTrue(idxClosed < idxTasks);
    }

    @Test
    // Producing a Single-Value JSON Output with @JsonValue
    void givenJsonValue_whenSerializingTask_thenSingleStringProduced() throws JsonProcessingException {
        Task task = new Task("T101", "Task101", "JSON value example", LocalDate.now(), null, null);
        Campaign original = new Campaign("T102", "Campaign102", "Big Campaign");
        original.setTasks(Set.of(task));

        String json = objectMapper.writeValueAsString(original);

        String expectedJson = """
      {"code":"t102","name":"Campaign102","description":"Big Campaign","is_closed":false,"tasks":["Task: code= T101, name=Task101, description=JSON value example."]}""";
        assertEquals(expectedJson, json);
    }
}
