package com.baeldung.ljj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.baeldung.ljj.domain.model.Campaign;
import com.baeldung.ljj.domain.model.Task;
import com.baeldung.ljj.domain.model.TaskStatus;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

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
                  "status": "IN_PROGRESS"
                }
              ]
            }""";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void whenReadingFieldsFromJsonNode_thenCorrectValuesExtracted() {
        JsonNode rootNode = objectMapper.readTree(CAMPAIGN_WITH_TASKS);

        JsonNode campaignCodeNode = rootNode.get("code");
        String campaignCode = campaignCodeNode.asString();
        assertEquals("C-001", campaignCode);

        JsonNode detailsNode = rootNode.get("details");
        String campaignName = detailsNode.get("name").asString();
        String campaignDescription = detailsNode.get("description").asString();
        boolean campaignIsClosed = detailsNode.get("closed").asBoolean();

        assertEquals("Campaign 1", campaignName);
        assertEquals("Campaign 1 description", campaignDescription);
        assertFalse(campaignIsClosed);
    }

    @Test
    void whenReadingNonExistentFieldWithGet_thenNullReturned() {
        JsonNode rootNode = objectMapper.readTree(CAMPAIGN_WITH_TASKS);

        // get on non-existing field
        JsonNode nonExistingFieldNode = rootNode.get("nonExistingField"); // null

        assertThrows(NullPointerException.class, () -> nonExistingFieldNode.asString());

        // path on non-existing field
        JsonNode nonExistingUsingPathNode = rootNode.path("nonExistingField"); // MissingNode
        assertTrue(nonExistingUsingPathNode.isMissingNode());
        assertEquals("", nonExistingUsingPathNode.asString());
        assertThrows(tools.jackson.databind.exc.JsonNodeException.class, () -> nonExistingUsingPathNode.stringValue());

        // get on null field
        JsonNode nullableFieldNode = rootNode.get("nullableField"); // NullNode

        assertEquals("", nullableFieldNode.asString());
        assertNull(nullableFieldNode.stringValue());
    }

    @Test
    void whenReadingArrayFromJsonNode_thenElementsAccessibleByIndex() {
        JsonNode rootNode = objectMapper.readTree(CAMPAIGN_WITH_TASKS);

        JsonNode tasksNode = rootNode.path("tasks");
        assertTrue(tasksNode.isArray());
        assertEquals(2, tasksNode.size());

        JsonNode firstTaskNode = tasksNode.get(0);
        assertEquals("T-001", firstTaskNode.path("code")
                .asString());
        assertEquals("Task 1", firstTaskNode.path("name")
                .asString());
        assertEquals(TaskStatus.IN_PROGRESS.toString(), firstTaskNode.path("status")
                .asString());

        JsonNode secondTaskNode = tasksNode.get(1);
        assertEquals("T-002", secondTaskNode.path("code")
                .asString());
        assertEquals("Task 2", secondTaskNode.path("name")
                .asString());
        assertEquals(TaskStatus.IN_PROGRESS.toString(), secondTaskNode.path("status")
                .asString());
    }

    @Test
    void whenReadingArrayFromJsonNode_thenElementsAccessibleByIteration() {
        JsonNode rootNode = objectMapper.readTree(CAMPAIGN_WITH_TASKS);

        JsonNode tasksNode = rootNode.path("tasks");
        int inProgressCount = 0;
        for (JsonNode taskNode : tasksNode) {
            String status = taskNode.path("status")
                    .asString();
            if (TaskStatus.IN_PROGRESS.toString()
                    .equals(status)) {
                inProgressCount++;
            }
        }
        assertEquals(2, inProgressCount);
    }

    @Test
    void whenConvertingJsonNodeToCorrectObject_thenObjectProperlyDeserialized() {
        JsonNode rootNode = objectMapper.readTree(CAMPAIGN_WITH_TASKS);

        JsonNode firstTaskNode = rootNode.path("tasks")
                .get(0);
        Task firstTask = objectMapper.treeToValue(firstTaskNode, Task.class);

        assertEquals("T-001", firstTask.getCode());
        assertEquals("Task 1", firstTask.getName());
        assertEquals(TaskStatus.IN_PROGRESS, firstTask.getStatus());
    }

    @Test
    void whenConvertingJsonNodeToIncorrectObject_thenExceptionThrown() {
        ObjectMapper strictMapper = JsonMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();

        JsonNode rootNode = strictMapper.readTree(CAMPAIGN_WITH_TASKS);

        assertThrows(UnrecognizedPropertyException.class, () -> strictMapper.treeToValue(rootNode, Campaign.class));
    }

    @Test
    void whenConvertingJsonNodeWithUnknownPropertiesUsingLenientMapper_thenConversionSucceeds() {
        ObjectMapper lenientMapper = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();

        JsonNode rootNode = lenientMapper.readTree(CAMPAIGN_WITH_TASKS);

        Campaign campaign = lenientMapper.treeToValue(rootNode, Campaign.class);

        assertNotNull(campaign);
        assertEquals("C-001", campaign.getCode());
        assertNull(campaign.getDescription());
        assertFalse(campaign.getTasks()
                .isEmpty());
    }

    @Test
    void whenManipulatingTreeModel_thenJsonModifiedCorrectly() {
        JsonNode rootNode = objectMapper.readTree(CAMPAIGN_WITH_TASKS);

        JsonNode detailsNode = rootNode.path("details");
        String campaignName = detailsNode.path("name")
                .asString();
        String campaignDescription = detailsNode.path("description")
                .asString();
        boolean campaignIsClosed = detailsNode.path("closed")
                .asBoolean();

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

        Campaign modifiedCampaign = objectMapper.treeToValue(rootObjectNode, Campaign.class);
        assertEquals("Campaign 1 description", modifiedCampaign.getDescription());
        modifiedCampaign.getTasks().forEach(t -> assertEquals(TaskStatus.DONE, t.getStatus()));
    }
}