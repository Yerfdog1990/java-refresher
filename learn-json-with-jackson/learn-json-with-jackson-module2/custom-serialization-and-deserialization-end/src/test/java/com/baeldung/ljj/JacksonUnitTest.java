package com.baeldung.ljj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.baeldung.ljj.domain.model.Campaign;
import com.baeldung.ljj.domain.model.Task;
import com.baeldung.ljj.domain.model.TaskStatus;
import com.baeldung.ljj.serialization.CampaignToCodeSerializer;
import com.baeldung.ljj.serialization.CodeToCampaignDeserializer;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

class JacksonUnitTest {

    @Test
    void givenTask_whenSerializing_thenCustomSerializerUsed() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Campaign.class, new CampaignToCodeSerializer());

        JsonMapper mapper = JsonMapper.builder()
                .addModule(module)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();

        Campaign campaign = new Campaign("C1", "Campaign one", "This is Campaign one");
        Task task = new Task("T1", "Task one", "This is Task one", LocalDate.of(2050, 1, 1), TaskStatus.TO_DO, campaign);
        String json = mapper.writeValueAsString(task);

        assertTrue(json.contains("\"campaign\" : \"C1\""));
        assertFalse(json.contains("\"name\" : \"Campaign one\""));

        System.out.println(json);
    }

    @Test
    void givenJsonWithCustomStatus_whenDeserializing_thenCustomDeserializerUsed() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Campaign.class, new CodeToCampaignDeserializer());

        JsonMapper mapper = JsonMapper.builder()
                .addModule(module)
                .build();

        String json = """
                  {
                  "code" : "T1",
                  "name" : "Task one",
                  "description" : "This is Task one",
                  "dueDate" : [ 2050, 1, 1 ],
                  "status" : "TO_DO",
                  "campaign" : "C1"
                }
                """;

        Task task = mapper.readValue(json, Task.class);
        assertNotNull(task.getCampaign());
        assertEquals("C1", task.getCampaign()
                .getCode());
        assertNull(task.getCampaign()
                .getName());
        assertNull(task.getCampaign()
                .getDescription());
    }

    @Test
    void givenTask_whenSerializing_thenAnnotationSerializerUsed() {
        JsonMapper mapper = JsonMapper.builder().build();

        // no further module is added to mapper
        Campaign campaign = new Campaign("C1", "Campaign one", "This is Campaign one");
        Task task = new Task("T1", "Task one", "This is Task one", LocalDate.of(2050, 1, 1), TaskStatus.TO_DO, campaign);
        String json = mapper.writeValueAsString(task);

        assertTrue(json.contains("\"campaign\":\"C1\""));
        assertFalse(json.contains("\"name\":\"Campaign 1\""));

        System.out.println(json);
    }

    @Test
    void givenJsonWithCustomCampaign_whenDeserializing_thenAnnotationDeserializerUsed() {
        JsonMapper mapper = JsonMapper.builder().build();
        String json = """
                  {
                  "code" : "T1",
                  "name" : "Task one",
                  "description" : "This is Task one",
                  "dueDate" : [ 2050, 1, 1 ],
                  "status" : "TO_DO",
                  "campaign" : "C1"
                }
                """;
        Task task = mapper.readValue(json, Task.class);
        assertNotNull(task.getCampaign());
        assertEquals("C1", task.getCampaign()
                .getCode());
        assertNull(task.getCampaign()
                .getName());
        assertNull(task.getCampaign()
                .getDescription());
    }

    @Test
    void givenAnnotationSerializerAndDeserializer_whenWorkingWithStandaloneCampaign_thenCustomSerializerSerializerNotUsed() {
        JsonMapper mapper = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();
        Campaign campaign = new Campaign("C2", "Campaign two", "This is Campaign two.");

        // serializing
        String json = mapper.writeValueAsString(campaign);

        assertTrue(json.contains("\"code\" : \"C2\""));
        assertTrue(json.contains("\"name\" : \"Campaign two\""));

        System.out.println(json);

        // deserializing
        Campaign deserialized = mapper.readValue(json, Campaign.class);
        assertEquals(campaign.getCode(), deserialized.getCode());
        assertEquals(campaign.getName(), deserialized.getName());
        assertEquals(campaign.getDescription(), deserialized.getDescription());
        assertEquals(campaign.getTasks(), deserialized.getTasks());

    }
}
