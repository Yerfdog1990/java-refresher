package com.baeldung.ljj;

import com.baeldung.ljj.domain.model.Campaign;
import com.baeldung.ljj.domain.model.PrivateCampaign;
import com.baeldung.ljj.domain.model.PublicCampaign;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class JacksonUnitTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    // Test @JsonInclude at Field Level
    void whenUsingJsonIncludeAtFieldLevel_thenFieldOmitted() throws JsonProcessingException {
        Campaign campaign = new Campaign(null, "My Campaign", "Description of campaign 01");
        String json = objectMapper.writeValueAsString(campaign);

        System.out.println("JSON: " + json);
        assertFalse(json.contains("code"));
    }

    @Test
    // Test @JsonInclude at Class Level
    void whenUsingJsonIncludeAtClassLevelWithEmptyOption_thenOmitEmptyField() throws JsonProcessingException {
        Campaign campaign = new Campaign("C01", "", "Description of campaign 01");
        String json = objectMapper.writeValueAsString(campaign);

        System.out.println("JSON: " + json);
        assertFalse(json.contains("name"));
    }

    @Test
    // Using the @JsonIgnore Annotation
    void whenUsingJsonIgnore_thenOmitTheAnnotatedField() throws JsonProcessingException {
        Campaign campaign = new Campaign("C01", "My Campaign", "Description of campaign 01");
        campaign.setClosed(true);
        String json = objectMapper.writeValueAsString(campaign);

        System.out.println("JSON: " + json);
        assertFalse(json.contains("closed"));
    }

    @Test
    // Test using the @JsonProperty Annotation
    void whenUsingJsonPropertyWithReadOption_thenOmitEmptyField() throws JsonProcessingException {
        Campaign campaign = new Campaign("C01", "My Campaign", "Description of campaign 01");
        String json = objectMapper.writeValueAsString(campaign);

        System.out.println("JSON: " + json);
        assertFalse(json.contains("description"));
    }

    @Test
    // Test the @JsonIgnoreProperties annotation at class level
    void whenUsingJsonIgnorePropertiesAtClassLevel_thenOmitSpecifiedFields() throws JsonProcessingException {
        PrivateCampaign campaign = new PrivateCampaign("PC01", "My Private Campaign",
                "Description of private campaign 01");
        String json = objectMapper.writeValueAsString(campaign);

        System.out.println("JSON: " + json);
        assertFalse(json.contains("name"));
        assertFalse(json.contains("closed"));
    }

    @Test
    // Test how the @JsonIgnoreProperties annotation at class level affects deserialization
    void whenUsingJsonIgnorePropertiesAtClassLevel_thenOmitSpecifiedFieldsDuringSerializationAndReplaceWithDefault() throws JsonProcessingException {
        String json = """
        {"code": "PC01", "name": "My Private Campaign", "description": "Description of private campaign 01", "tasks": [], "closed": true}
        """;
        PrivateCampaign campaign = objectMapper.readValue(json, PrivateCampaign.class);

        System.out.println("PrivateCampaign: " + campaign);
        assertNull(campaign.getName());
        assertFalse(campaign.isClosed());
    }

    @Test
    // Test the @JsonIncludeProperties annotation at class level
    void whenUsingJsonIncludePropertiesAtClassLevel_thenOmitNonSpecifiedFields() throws JsonProcessingException {
        PublicCampaign publicCampaign =
                new PublicCampaign("PUC01", "My Public Campaign", "Description of public campaign 01");
        String json = objectMapper.writeValueAsString(publicCampaign);

        System.out.println("JSON: " + json);
        assertTrue(json.contains("name"));
        assertFalse(json.contains("code"));
    }

    @Test
    // Test how the @JsonIncludeProperties annotation at class level affects deserialization
    void whenUsingJsonIncludePropertiesAtClassLevel_thenOmitNonSpecifiedFieldsDuringDeserialization() throws Exception {
        String json = """
            {"code": "PUC01", "name": "My Public Campaign", "description": "Description of public campaign 01"}
            """;
        PublicCampaign publicCampaign = objectMapper.readValue(json, PublicCampaign.class);

        System.out.println("PublicCampaign: " + publicCampaign);
        assertEquals("My Public Campaign", publicCampaign.getName());
        assertNotSame("PUC01", publicCampaign.getCode());
    }
}