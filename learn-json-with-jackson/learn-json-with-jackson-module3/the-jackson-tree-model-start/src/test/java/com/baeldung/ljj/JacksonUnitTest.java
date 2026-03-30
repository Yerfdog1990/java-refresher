package com.baeldung.ljj;

import com.baeldung.ljj.domain.model.Campaign;
import com.baeldung.ljj.domain.model.Task;
import com.baeldung.ljj.domain.model.TaskStatus;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JacksonUnitTest {
    private static final String CAMPAIGN_WITH_TASKS = """
            {
              "code": "C-001",
              "nullableField": null,
              "details": {
                "name": "Campaign 1",
                "description": "Campaign 1 description",
                "closed": false
              },
              "tasks": [
                {
                  "code": "T-001",
                  "name": "Task 1",
                  "status": "IN_PROGRESS"
                },
                {
                  "code": "T-002",
                  "name": "Task 2",
                  "status": "TO_DO"
                }
              ]
            }""";

    private static final String CAMPAIGN_WITH_UNKNOWN_PROPERTIES = """
            {
              "code": "C-001",
              "name": "Campaign 1",
              "description": "Campaign 1 description",
              "closed": false,
              "unknownProperty": "should cause exception",
              "anotherUnknownField": 123
            }""";

    final JsonMapper jsonMapper = new JsonMapper();

    @Test
    // Reading Fields
    void whenReadingFieldsFromJsonNode_thenCorrectValuesExtracted() throws JacksonException {
        JsonNode rootNode = jsonMapper.readTree(CAMPAIGN_WITH_TASKS);

        JsonNode campaignCodeNode = rootNode.get("code");
        String campaignCode = campaignCodeNode.asString();
        assertEquals("C-001", campaignCode);

        JsonNode nullableNode = rootNode.get("nullableField");
        String nullableValue = nullableNode.asString();
        assertEquals("", nullableValue);

        JsonNode detailsNode = rootNode.get("details");
        String campaignName = detailsNode.get("name").asString();
        String campaignDescription = detailsNode.get("description").asString();
        boolean campaignIsClosed = detailsNode.get("closed").asBoolean();

        assertEquals("Campaign 1", campaignName);
        assertEquals("Campaign 1 description", campaignDescription);
        assertFalse(campaignIsClosed);
    }

    @Test
    // Null and Non-Existing Fields
    void whenReadingNonExistentFieldWithGet_thenNullReturned() throws JacksonException {
        JsonNode rootNode = jsonMapper.readTree(CAMPAIGN_WITH_TASKS);

        // get() on non-existing field
        JsonNode nonExistingFieldNode = rootNode.get("nonExistingField"); // null
        assertThrows(NullPointerException.class, () -> nonExistingFieldNode.asString());

        // path() on non-existing field
        JsonNode nonExistingUsingPathNode = rootNode.path("nonExistingField"); // MissingNode
        assertTrue(nonExistingUsingPathNode.isMissingNode());
        assertEquals("", nonExistingUsingPathNode.asString());
        assertEquals("", nonExistingUsingPathNode.asString());

        // get() on null field
        JsonNode nullableFieldNode = rootNode.get("nullableField"); // NullNode
        assertEquals("", nullableFieldNode.asString());
        assertNull(nonExistingUsingPathNode.isMissingNode() ? null : nonExistingUsingPathNode.stringValue());
    }

    @Test
    // Working With Arrays
    void whenReadingArrayFromJsonNode_thenElementsAccessibleByIndex() throws JacksonException {
        JsonNode rootNode = jsonMapper.readTree(CAMPAIGN_WITH_TASKS);

        JsonNode tasksNode = rootNode.path("tasks");
        assertTrue(tasksNode.isArray());
        assertEquals(2, tasksNode.size());

        JsonNode firstTaskNode = tasksNode.get(0);
        assertEquals("T-001", firstTaskNode.path("code").asString());
        assertEquals("Task 1", firstTaskNode.path("name").asString());
        assertEquals(TaskStatus.IN_PROGRESS.toString(), firstTaskNode.path("status").asString());
    }

    @Test
    void whenReadingArrayFromJsonNode_thenElementsAccessibleByIteration() throws JacksonException {
        JsonNode rootNode = jsonMapper.readTree(CAMPAIGN_WITH_TASKS);

        JsonNode tasksNode = rootNode.path("tasks");
        int inProgressCount = 0;
        for (JsonNode taskNode : tasksNode) {
            String status = taskNode.path("status").asString();
            if (TaskStatus.IN_PROGRESS.toString().equals(status)) {
                inProgressCount++;
            }
            assertThat(taskNode.path("code").asString()).isIn("T-001", "T-002");
            assertThat(taskNode.path("name").asString()).matches("Task [12]");
            String taskStatus = taskNode.path("status").asString();
            assertTrue(taskStatus.equals("TO_DO") || status.equals("IN_PROGRESS"));
        }
        assertEquals(1, inProgressCount);
    }

    @Test
    void whenConvertingJsonNodeToCorrectObject_thenObjectProperlyDeserialized() throws JacksonException {
        JsonNode rootNode = jsonMapper.readTree(CAMPAIGN_WITH_TASKS);

        JsonNode firstTaskNode = rootNode.path("tasks").get(0);
        Task firstTask = jsonMapper.treeToValue(firstTaskNode, Task.class);

        assertEquals("T-001", firstTask.getCode());
        assertEquals("Task 1", firstTask.getName());
        assertEquals(TaskStatus.IN_PROGRESS, firstTask.getStatus());
    }

    @Test
    // This conversion fails if the JsonNode structure doesn’t match the target class:
    void whenConvertingJsonNodeToIncorrectObject_thenExceptionThrown() throws JacksonException {
        // Create a strict mapper that fails on unknown properties
        JsonMapper strictMapper = JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
                .build();
        
        JsonNode rootNode = strictMapper.readTree(CAMPAIGN_WITH_UNKNOWN_PROPERTIES);

        assertThrows(UnrecognizedPropertyException.class, () -> strictMapper.treeToValue(rootNode, Campaign.class));
    }

    @Test
    void whenConvertingJsonNodeWithUnknownPropertiesUsingLenientMapper_thenConversionSucceeds()
            throws JacksonException {
        JsonMapper lenientMapper
                = JsonMapper.builder().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build();

        JsonNode rootNode = lenientMapper.readTree(CAMPAIGN_WITH_TASKS);

        Campaign campaign = lenientMapper.treeToValue(rootNode, Campaign.class);

        assertNotNull(campaign);
        assertEquals("C-001", campaign.getCode());
        assertNull(campaign.getDescription());
        assertFalse(campaign.getTasks().isEmpty());
    }

    @Test
    void whenManipulatingTreeModel_thenJsonModifiedCorrectly() throws JacksonException {
        JsonNode rootNode = jsonMapper.readTree(CAMPAIGN_WITH_TASKS);

        JsonNode detailsNode = rootNode.path("details");
        String campaignName = detailsNode.path("name").asString();
        String campaignDescription = detailsNode.path("description").asString();
        boolean campaignIsClosed = detailsNode.path("closed").asBoolean();

        ObjectNode rootObjectNode = (ObjectNode) rootNode;
        rootObjectNode.put("name", campaignName);
        rootObjectNode.put("description", campaignDescription);
        rootObjectNode.put("closed", campaignIsClosed);
        rootObjectNode.remove("details");
        rootObjectNode.remove("nullableField");

        ArrayNode tasksNode = (ArrayNode) rootObjectNode.path("tasks");
        tasksNode.forEach(taskNode -> {
            ObjectNode taskObjectNode = (ObjectNode) taskNode;
            taskObjectNode.put("status", TaskStatus.DONE.toString());
        });

        Campaign modifiedCampaign = jsonMapper.treeToValue(rootObjectNode, Campaign.class);
        assertEquals("Campaign 1 description", modifiedCampaign.getDescription());
        modifiedCampaign.getTasks().forEach(t -> assertEquals(TaskStatus.DONE, t.getStatus()));
    }
}