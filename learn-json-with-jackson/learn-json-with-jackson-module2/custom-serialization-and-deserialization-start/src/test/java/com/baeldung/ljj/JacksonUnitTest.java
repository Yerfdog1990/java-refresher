package com.baeldung.ljj;

import com.baeldung.ljj.domain.model.Campaign;
import com.baeldung.ljj.domain.model.Task;
import com.baeldung.ljj.domain.model.TaskStatus;
import com.baeldung.ljj.serialization.CampaignToCodeSerializer;
import com.baeldung.ljj.serialization.CodeToCampaignDeserializer;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
}
