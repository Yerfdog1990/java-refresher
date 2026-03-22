package com.baeldung.ljj;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.baeldung.ljj.domain.model.TaskStatus;
import com.baeldung.ljj.domain.model.TaskStatusJsonObject;
import com.baeldung.ljj.domain.model.TaskStatusJsonProperty;
import com.baeldung.ljj.domain.model.TaskStatusJsonValue;
import com.baeldung.ljj.domain.model.TaskStatusObjectMap;
import tools.jackson.databind.cfg.EnumFeature;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.json.JsonMapper;

class JacksonUnitTest {

    @Test
    void whenSerializingEnum_thenWritesName() {
        JsonMapper mapper = JsonMapper.builder().build();
        String json = mapper.writeValueAsString(TaskStatus.IN_PROGRESS);
        assertEquals("\"In Progress\"", json);
    }

    @Test
    void whenDeserializingMatchingName_thenSucceeds() {
        JsonMapper mapper = JsonMapper.builder().build();
        TaskStatus taskStatus = mapper.readValue("\"On Hold\"", TaskStatus.class);
        assertEquals(TaskStatus.ON_HOLD, taskStatus);
    }

    @Test
    void whenUsingIndexFlag_thenWritesOrdinal() {
        JsonMapper mapper = JsonMapper.builder().enable(EnumFeature.WRITE_ENUMS_USING_INDEX).build();
        String json = mapper.writeValueAsString(TaskStatus.ON_HOLD);
        assertEquals("2", json);
    }

    @Test
    void whenUsingIndexFlag_thenDeserializeOrdinal() {
        JsonMapper mapper = JsonMapper.builder().enable(EnumFeature.WRITE_ENUMS_USING_INDEX).build();
        TaskStatus taskStatus = mapper.readValue("2", TaskStatus.class);
        assertEquals(TaskStatus.ON_HOLD, taskStatus);
    }

    @Test
    void whenUsingToStringFlag_thenWritesLabel() {
        JsonMapper mapper = JsonMapper.builder().enable(EnumFeature.WRITE_ENUMS_USING_TO_STRING).build();
        String json = mapper.writeValueAsString(TaskStatus.DONE);
        assertEquals("\"Done\"", json);
    }

    @Test
    void whenUsingJsonValue_thenLabelWritten() {
        JsonMapper mapper = JsonMapper.builder().build();
        String json = mapper.writeValueAsString(TaskStatusJsonValue.IN_PROGRESS);
        assertEquals("\"In Progress\"", json);
    }

    @Test
    void whenUsingJsonProperty_thenLabelWritten() {
        JsonMapper mapper = JsonMapper.builder().build();
        String json = mapper.writeValueAsString(TaskStatusJsonProperty.DONE);
        assertEquals("\"JsonProperty Done\"", json);
    }

    @Test
    void whenUsingJsonCreator_thenLabelParsed() {
        JsonMapper mapper = JsonMapper.builder().build();
        TaskStatusJsonValue taskStatus = mapper.readValue("\"On HoLd\"", TaskStatusJsonValue.class);
        assertEquals(TaskStatusJsonValue.ON_HOLD, taskStatus);
    }

    @Test
    void whenSerializing_thenJsonObjectIsProduced() {
        JsonMapper mapper = JsonMapper.builder().build();
        TaskStatusJsonObject label = TaskStatusJsonObject.TO_DO;
        String json = mapper.writeValueAsString(label);
        String expectedJson = "{\"label\":\"To Do\"}";
        assertEquals(expectedJson, json);
    }

    @Test
    void whenUsingDefaultValue_thenFallsBackToToDo() {
        JsonMapper mapper = JsonMapper.builder().enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE).build();
        TaskStatus status = mapper.readValue("\"UNKNOWN\"", TaskStatus.class);
        assertEquals(TaskStatus.TO_DO, status);
    }

    @Test
    void whenJsonValueOnEnum_thenMapKeysAreLabel() {
        JsonMapper mapper = JsonMapper.builder().build();
        TaskStatusObjectMap label = TaskStatusObjectMap.TO_DO;
        String json = mapper.writeValueAsString(label);
        String expectedJson = "{\"label\":\"To Do\"}";
        assertEquals(expectedJson, json);
    }
}
