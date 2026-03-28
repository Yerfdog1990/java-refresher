package com.baeldung.ljj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.baeldung.ljj.domain.model.Address;
import com.baeldung.ljj.domain.model.Campaign;
import com.baeldung.ljj.domain.model.Task;
import com.baeldung.ljj.domain.model.TaskStatus;
import tools.jackson.databind.ObjectMapper;

class JacksonUnitTest {

    final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void givenCampaignWithTasks_whenSerialize_thenNestedArrayPresent() throws Exception {
        // given
        Campaign campaign = new Campaign("C100", "Campaign 100", "Nested example");
        Task t1 = new Task("T1", "Task 1", "First", null, TaskStatus.TO_DO, null);
        Task t2 = new Task("T2", "Task 2", "Second", null, TaskStatus.TO_DO, null);
        campaign.getTasks()
                .add(t1);
        campaign.getTasks()
                .add(t2);
        Address address = new Address("Street", "City", "10A103", "US");
        campaign.setAddress(address);

        // when
        String json = objectMapper.writeValueAsString(campaign);
        System.out.println(json);

        // then
        assertTrue(json.contains("\"code\":\"C100\""));
        assertTrue(json.contains("\"code\":\"T1\""));
        assertTrue(json.contains("\"code\":\"T2\""));
    }

    @Test
    void givenCampaignJsonString_whenDeserialize_thenCampaignAndTasksObjects() throws Exception {
        // given
        String json = """
                {
                  "code": "C200",
                  "name": "Campaign 200",
                  "description": "Nested example",
                  "tasks": [
                    { "code": "T1", "name": "Task 1", "description": "First", "status": "TO_DO" },
                    { "code": "T2", "name": "Task 2", "description": "Second", "status": "TO_DO" }
                  ]
                }""";
        System.out.println(json);

        // when
        Campaign campaign = objectMapper.readValue(json, Campaign.class);

        // then
        assertEquals("C200", campaign.getCode());
        assertEquals(2, campaign.getTasks()
                .size());
    }

    // Uncomment to see the example of '6.1. Easiest Fix: Ignore the Back-Reference With @JsonIgnore'
    // @Test
    void givenCampaignIgnoringBackLink_whenSerialize_thenOk() throws Exception {
        // given
        Campaign campaign = new Campaign("C400", "Campaign 400", "Nested example");
        Task t1 = new Task("T1", "Task 1", "First", null, TaskStatus.TO_DO, null);
        Task t2 = new Task("T2", "Task 2", "Second", null, TaskStatus.TO_DO, null);
        campaign.getTasks()
                .add(t1);
        campaign.getTasks()
                .add(t2);
        for (Task t : campaign.getTasks()) {
            t.setCampaign(campaign);
        }

        // when
        String json = objectMapper.writeValueAsString(campaign);
        System.out.println(json);

        // then
        assertTrue(json.contains("\"code\":\"C400\""));
        assertTrue(json.contains("\"tasks\""));

        Campaign restored = objectMapper.readValue(json, Campaign.class);
        assertEquals("C400", restored.getCode());
        Optional<Task> firstTask = restored.getTasks()
                .stream()
                .findFirst();
        assertTrue(firstTask.isPresent());
        assertNull(firstTask.get()
                .getCampaign());
    }

    @Test
    void givenCampaignWithBidirectionalLinksHandled_wheSerialize_thenOK() throws Exception {
        // given
        Campaign campaign = new Campaign("C400", "Campaign 400", "Nested example");
        Task t1 = new Task("T1", "Task 1", "First", null, TaskStatus.TO_DO, null);
        Task t2 = new Task("T2", "Task 2", "Second", null, TaskStatus.TO_DO, null);
        campaign.getTasks()
                .add(t1);
        campaign.getTasks()
                .add(t2);
        for (Task t : campaign.getTasks()) {
            t.setCampaign(campaign);
        }

        // when
        String json = objectMapper.writeValueAsString(campaign);
        System.out.println(json);

        // then
        assertTrue(json.contains("\"code\":\"C400\""));
        assertTrue(json.contains("\"tasks\""));

        Campaign restored = objectMapper.readValue(json, Campaign.class);
        assertEquals("C400", restored.getCode());
        Optional<Task> firstTask = restored.getTasks()
                .stream()
                .findFirst();
        assertTrue(firstTask.isPresent());
        assertEquals(restored, firstTask.get()
                .getCampaign());
    }
}
