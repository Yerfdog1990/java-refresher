package com.baeldung.ljj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.baeldung.ljj.domain.model.Campaign;
import com.baeldung.ljj.domain.model.Task;
import com.baeldung.ljj.domain.model.TaskWithoutEmptyConstructor;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.json.JsonMapper;

class JacksonUnitTest {

    @Test
    void givenUnknownProperty_whenUsingDefaultMapper_thenFail() {
        JsonMapper mapper = JsonMapper.builder().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
        String json = """
                {
                    "code": "C2",
                    "name": "Campaign 2",
                    "description": "The description of Campaign 2",
                    "budget": 100
                }
                """;
        assertThrows(UnrecognizedPropertyException.class, () -> mapper.readValue(json, Campaign.class));
    }

    @Test
    void givenMapperConfiguredToIgnoreUnknown_thenDeserializationSucceeds() {
        JsonMapper mapper = JsonMapper.builder().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
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
    void givenObjectToArrayProperty_whenUsingDefaultMapper_thenFail() {
        JsonMapper mapper = JsonMapper.builder().build();
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
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, Campaign.class));
    }

    @Test
    void givenObjectToArrayProperty_whenMapperConfiguredToAcceptObjectAsArray_thenSuccess() {
        JsonMapper mapper = JsonMapper.builder().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).build();
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
    void givenNoDefaultConstructor_whenUsingDefaultMapper_thenFail() {
        JsonMapper mapper = JsonMapper.builder().build();
        String json = """
                {
                    "code": "C2",
                    "name": "Campaign 2",
                    "description": "The description of Campaign 2"
                }
                """;
        assertThrows(InvalidDefinitionException.class, () -> mapper.readValue(json, TaskWithoutEmptyConstructor.class));
    }

    @Test
    void givenDefaultConstructorAdded_whenUsingDefaultMapper_thenSuccess() {
        JsonMapper mapper = JsonMapper.builder().build();
        String json = """
                {
                    "code": "C2",
                    "name": "Campaign 2",
                    "description": "The description of Campaign 2"
                }
                """;
        Task task = mapper.readValue(json, Task.class);
        assertEquals("C2", task.getCode());
    }
}