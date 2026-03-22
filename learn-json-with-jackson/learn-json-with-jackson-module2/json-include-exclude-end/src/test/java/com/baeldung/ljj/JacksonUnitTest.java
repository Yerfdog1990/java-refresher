package com.baeldung.ljj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.baeldung.ljj.domain.model.Campaign;
import com.baeldung.ljj.domain.model.PrivateCampaign;
import com.baeldung.ljj.domain.model.PublicCampaign;
import tools.jackson.databind.ObjectMapper;

class JacksonUnitTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void whenUsingJsonIncludeAtFieldLevel_thenFieldOmitted() throws Exception {
        Campaign campaign = new Campaign(null, "My Campaign", "Description of campaign 01");
        String json = objectMapper.writeValueAsString(campaign);

        assertFalse(json.contains("code"));
    }

    @Test
    void whenUsingJsonIncludeAtClassLevelWithEmptyOption_thenOmitEmptyField() throws Exception {
        Campaign campaign = new Campaign("C01", "", "Description of campaign 01");
        String json = objectMapper.writeValueAsString(campaign);

        assertFalse(json.contains("name"));
    }

    @Test
    void whenUsingJsonIgnore_thenOmitTheAnnotatedField() throws Exception {
        Campaign campaign = new Campaign("C01", "My Campaign", "Description of campaign 01");
        campaign.setClosed(true);
        String json = objectMapper.writeValueAsString(campaign);

        assertFalse(json.contains("closed"));
    }

    @Test
    void whenUsingJsonPropertyWithReadOption_thenOmitEmptyField() throws Exception {
        Campaign campaign = new Campaign("C01", "My Campaign", "Description of campaign 01");
        String json = objectMapper.writeValueAsString(campaign);

        assertFalse(json.contains("description"));
    }

    @Test
    void whenUsingJsonIgnorePropertiesAtClassLevel_thenOmitSpecifiedFields() throws Exception {
        PrivateCampaign campaign = new PrivateCampaign("PC01", "My Private Campaign", "Description of private campaign 01");
        String json = objectMapper.writeValueAsString(campaign);

        assertFalse(json.contains("name"));
        assertFalse(json.contains("closed"));
    }

    @Test
    void whenUsingJsonIgnorePropertiesAtClassLevel_thenOmitSpecifiedFieldsDuringSerializationAndReplaceWithDefault() throws Exception {
        String json = """
                {"code": "PC01", "name": "My Private Campaign", "description": "Description of private campaign 01", "tasks": [], "closed": true}
                """;
        PrivateCampaign campaign = objectMapper.readValue(json, PrivateCampaign.class);

        assertNull(campaign.getName());
        assertFalse(campaign.isClosed());
    }

    @Test
    void whenUsingJsonIncludePropertiesAtClassLevel_thenOmitNonSpecifiedFields() throws Exception {
        PublicCampaign publicCampaign = new PublicCampaign("PUC01", "My Public Campaign", "Description of public campaign 01");
        String json = objectMapper.writeValueAsString(publicCampaign);

        assertTrue(json.contains("name"));
        assertFalse(json.contains("code"));
    }

    @Test
    void whenUsingJsonIncludePropertiesAtClassLevel_thenOmitNonSpecifiedFieldsDuringDeserialization() throws Exception {
        String json = """
                {"code": "PUC01", "name": "My Public Campaign", "description": "Description of public campaign 01"}
                """;
        PublicCampaign publicCampaign = objectMapper.readValue(json, PublicCampaign.class);

        assertEquals("My Public Campaign", publicCampaign.getName());
        assertNotSame("PUC01", publicCampaign.getCode());
    }

}