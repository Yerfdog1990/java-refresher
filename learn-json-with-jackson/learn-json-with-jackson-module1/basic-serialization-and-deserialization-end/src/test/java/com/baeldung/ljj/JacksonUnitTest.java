package com.baeldung.ljj;

import com.baeldung.ljj.domain.model.Campaign;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JacksonUnitTest {

    private Campaign campaign = new Campaign("CAMP1", "CampaignName", "Description");
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void whenSerializingCampaign_thenCorrectJson() throws JsonProcessingException {

        String json = objectMapper.writeValueAsString(campaign);

        System.out.println(json);

        assertTrue(json.contains("\"code\":\"CAMP1\""));
        assertTrue(json.contains("\"name\":\"CampaignName\""));
        assertTrue(json.contains("\"description\":\"Description\""));
        assertTrue(json.contains("\"tasks\":[]"));
        assertTrue(json.contains("\"closed\":false"));
    }

    @Test
    public void whenWritingCampaignToFile_thenFileContainsCorrectJson() throws IOException {

        Path tempFile = Files.createTempFile("campaign", ".json");
        File file = tempFile.toFile();
        file.deleteOnExit();

        objectMapper.writeValue(file, campaign);

        assertTrue(file.exists());
        String content = Files.readString(tempFile);
        assertTrue(content.contains("\"code\":\"CAMP1\""));
        assertTrue(content.contains("\"name\":\"CampaignName\""));
        assertTrue(content.contains("\"description\":\"Description\""));
        assertTrue(content.contains("\"tasks\":[]"));
        assertTrue(content.contains("\"closed\":false"));
    }

    @Test
    public void whenSerializingCampaignList_thenCorrectJsonArray() throws JsonProcessingException {

        List<Campaign> campaigns = List.of(new Campaign("CAMP1", "CampaignName", "Description"), new Campaign("CAMP2", "SecondCampaign", "SecondDescription"));

        String json = objectMapper.writeValueAsString(campaigns);

        System.out.println(json);

        assertTrue(json.startsWith("["));
        assertTrue(json.contains("\"code\":\"CAMP1\""));
        assertTrue(json.contains("\"code\":\"CAMP2\""));
    }

    @Test
    public void whenDeserializingJsonToCampaign_thenCorrectObject() throws JsonProcessingException {

        String json = """
            {
                "code" : "CAMP1",
                "name" : "CampaignName",
                "description" : "Description",
                "tasks" : [ ],
                "closed" : false
            }
            """;

        Campaign result = objectMapper.readValue(json, Campaign.class);

        System.out.println(result);

        assertEquals("CAMP1", result.getCode());
        assertEquals("CampaignName", result.getName());
        assertEquals("Description", result.getDescription());
        assertFalse(result.isClosed());
        assertTrue(result.getTasks().isEmpty());
    }

    @Test
    public void whenDeserializingJsonFromFile_thenCorrectObject() throws IOException {

        Path tempFile = Files.createTempFile("campaign", ".json");
        File file = tempFile.toFile();
        file.deleteOnExit();

        String json = """
                {
                    "code" : "CAMP1",
                    "name" : "CampaignName",
                    "description" : "Description",
                    "tasks" : [ ],
                    "closed" : false
                }
            """;
        Files.writeString(tempFile, json);

        Campaign result = objectMapper.readValue(file, Campaign.class);

        System.out.println(result);

        assertEquals("CAMP1", result.getCode());
        assertEquals("CampaignName", result.getName());
        assertEquals("Description", result.getDescription());
        assertFalse(result.isClosed());
        assertTrue(result.getTasks().isEmpty());
    }

    @Test
    public void whenDeserializingJsonArrayToList_thenCorrectList() throws JsonProcessingException {

        String jsonArray = """
                [
                    {
                        "code" : "CAMP1",
                        "name" : "CampaignName",
                        "description" : "Description",
                        "tasks" : [ ],
                        "closed" : false
                    },
                    {
                        "code" : "CAMP2",
                        "name" : "SecondCampaign",
                        "description" : "SecondDescription",
                        "tasks" : [ ],
                        "closed" : false
                    }
                ]
            """;

        List<Campaign> resultList = objectMapper.readValue(jsonArray, new TypeReference<List<Campaign>>() {
        });

        System.out.println(resultList);

        assertEquals(2, resultList.size());
        assertEquals("CAMP1", resultList.get(0).getCode());
        assertEquals("CAMP2", resultList.get(1).getCode());
    }
}
