package com.baeldung.ljj;

import com.baeldung.ljj.domain.model.Campaign;
import com.baeldung.ljj.domain.model.Task;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JacksonUnitTest {

    JsonMapper objectMapper = JsonMapper.builder().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true).build();

    @Test
    // UnrecognizedPropertyException exception is thrown when a property exists in the JSON payload,
        // but there is no corresponding field in the Java class.
    void givenUnknownProperty_whenUsingDefaultMapper_thenFail() {
        String json = """
      {
        "code": "C2",
        "name": "Campaign 2",
        "description": "The description of Campaign 2",
        "budget": 100
      }
    """;
        assertThrows(UnrecognizedPropertyException.class, () -> objectMapper.readValue(json, Campaign.class));
    }

    @Test
    // The Solution
    void givenMapperConfiguredToIgnoreUnknown_thenDeserializationSucceeds() throws JacksonException {
        JsonMapper mapper = JsonMapper.builder().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build();
        String json = """
      {
        "code": "C2",
        "name": "Campaign 2",
        "description": "The description of Campaign 2",
        "budget": 100
      }
    """;
        Campaign campaign = mapper.readValue(json, Campaign.class);
        assertEquals("C2", campaign.getCode());
    }

    @Test
    // MismatchedInputException is a general-purpose error for when the structure of the JSON data doesn’t align
        // with what the Java class expects (e.g., object vs array).
    void givenObjectToArrayProperty_whenUsingDefaultMapper_thenFail() {
        String json = """
      {
        "code": "C2",
        "name": "Campaign 2",
        "description": "The description of Campaign 2",
        "tasks": {
          "code": 101,
          "name": "Task A"
        }
      }
    """;
        assertThrows(MismatchedInputException.class, () -> objectMapper.readValue(json, Campaign.class));
    }

    @Test
    // The Solution
    void givenObjectToArrayProperty_whenMapperConfiguredToAcceptObjectAsArray_thenSuccess() throws JacksonException {
        JsonMapper mapper = JsonMapper.builder().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true).build();
        String json = """
      {
        "code": "C2",
        "name": "Campaign 2",
        "description": "The description of Campaign 2",
        "tasks": {
          "code": 101,
          "name": "Task A"
        }
      }
    """;
        Campaign campaign = mapper.readValue(json, Campaign.class);
        assertEquals(1, campaign.getTasks().size());
    }

    @Test
    // InvalidDefinitionException is thrown when Jackson cannot construct or serialize
        // a type due to missing or invalid definitions.
    void givenNoDefaultConstructor_whenUsingDefaultMapper_thenFail() {
        ObjectMapper mapper = new ObjectMapper();
        String json = """
      {
        "code": "C2",
        "name": "Campaign 2",
        "description": "The description of Campaign 2"
      }
    """;
        assertThrows(InvalidDefinitionException.class, () -> mapper.readValue(json, Task.class));
    }

    @Test
    void givenDefaultConstructorAdded_whenUsingDefaultMapper_thenSuccess() throws JacksonException {
        ObjectMapper mapper = new ObjectMapper();
        String json = """
      {
        "code": "C2",
        "name": "Campaign 2",
        "description": "The description of Campaign 2"
      }
    """;
        Task task = mapper.readValue(json, Task.class);
        assertEquals("C2",task.getCode());
    }
}