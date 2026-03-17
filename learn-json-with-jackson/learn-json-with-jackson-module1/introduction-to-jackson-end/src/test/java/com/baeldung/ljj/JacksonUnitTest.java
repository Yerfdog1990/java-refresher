package com.baeldung.ljj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.baeldung.ljj.domain.model.Campaign;
import tools.jackson.databind.ObjectMapper;

class JacksonUnitTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void whenSerializingCampaign_thenCorrectJsonGenerated() throws Exception {
        Campaign campaign = new Campaign("C001", "Campaign 1", "Serialization");
        String jsonResult = objectMapper.writeValueAsString(campaign);

        System.out.println(jsonResult);
        assertTrue(jsonResult.contains("\"code\":\"C001\""));
        assertTrue(jsonResult.contains("\"name\":\"Campaign 1\""));
    }

    @Test
    void whenDeserializingJson_thenCorrectCampaignObjectGenerated() throws Exception {
        String jsonCampaign = """
                {
                  "code": "%s",
                  "name": "%s",
                  "description": "%s"
                }
                """.formatted("C001", "Campaign 1", "Deserialization");
        Campaign campaign = objectMapper.readValue(jsonCampaign, Campaign.class);

        assertEquals("C001", campaign.getCode());
        assertEquals("Campaign 1", campaign.getName());
    }
}