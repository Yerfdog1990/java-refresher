package com.baeldung.ljj;

import com.baeldung.ljj.domain.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;


class JacksonUnitTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    // Serializing Without Annotations
    void whenSerializingEnum_thenWritesName() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(TaskStatus.IN_PROGRESS);
        assertEquals("\"IN_PROGRESS\"", json);

        System.out.println(json);
    }

    @Test
    // Deserializing Without Annotations
    void whenDeserializingMatchingName_thenSucceeds() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        TaskStatus taskStatus = objectMapper.readValue("\"ON_HOLD\"", TaskStatus.class);
        assertEquals(TaskStatus.ON_HOLD, taskStatus);
    }

    @Test
    // Serializing to Enum Ordinal
    void whenUsingIndexFlag_thenWritesOrdinal() throws JsonProcessingException {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(SerializationFeature.WRITE_ENUMS_USING_INDEX)
                .build();
        String json = mapper.writeValueAsString(TaskStatus.ON_HOLD);
        System.out.println(json);
        assertEquals("2", json);
    }

    @Test
    // Deserializing to Enum Ordinal
    void whenUsingIndexFlag_thenDeserializeOrdinal() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
        TaskStatus taskStatus = mapper.readValue("2", TaskStatus.class);
        assertEquals(TaskStatus.ON_HOLD, taskStatus);
    }

    @Test
    // Serializing With toString()
    void whenUsingToStringFlag_thenWritesLabel() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        String json = mapper.writeValueAsString(TaskStatus.DONE);

        System.out.println(json);
        assertEquals("\"Done\"", json);
    }

    @Test
    void whenUsingJsonValue_thenLabelWritten() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(TaskStatusJsonValue.IN_PROGRESS);
        System.out.println(json);
        assertEquals("\"In Progress\"", json);
    }

    @Test
    void whenJsonValueOnEnum_thenMapKeysAreLabel() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        TaskStatusObjectMap label = TaskStatusObjectMap.TO_DO;
        String json = mapper.writeValueAsString(label);
        System.out.println(json);

        String expectedJson = "{\"label\":\"To Do\"}";
        assertEquals(expectedJson, json);
    }

    @Test
    // The @JsonProperty Annotation used to give DONE a different value for serialization
        // instead of the constant name or passed-in label.
    void whenUsingJsonProperty_thenLabelWritten() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(TaskStatusJsonProperty.DONE);

        System.out.println(json);
        assertEquals("\"JsonProperty Done\"", json);
    }

    @Test
    // Using @JsonEnumDefaultValue
    void whenUsingDefaultValue_thenFallsBackToToDo() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
        TaskStatus status = mapper.readValue("\"UNKNOWN\"", TaskStatus.class);
        System.out.println(status);

        assertEquals(TaskStatus.TO_DO, status);
    }

    @Test
    // The @JsonCreator marks a static factory method used during deserialization.
        // It allows us to define custom matching rules and handle invalid inputs.
    void whenUsingJsonCreator_thenLabelParsed() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        TaskStatusJsonValue taskStatus = mapper.readValue("\"On HoLd\"", TaskStatusJsonValue.class);
        assertEquals(TaskStatusJsonValue.ON_HOLD, taskStatus);
    }

    @Test
    // Advanced Enum Handling
        //Notably, we can use the @JsonFormat annotation at the class level with the JsonFormat.Shape.OBJECT option.
        // This serializes the enum constant as an object.
    void givenTaskStatusEnum_whenSerializing_thenJsonObjectIsProduced() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        TaskStatusJsonObject label = TaskStatusJsonObject.TO_DO;
        String json = mapper.writeValueAsString(label);
        System.out.println(json);

        String expectedJson = "{\"label\":\"To Do\"}";
        assertEquals(expectedJson, json);
    }

}
