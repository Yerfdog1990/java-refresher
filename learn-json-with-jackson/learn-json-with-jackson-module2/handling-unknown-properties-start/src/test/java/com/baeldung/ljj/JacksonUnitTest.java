package com.baeldung.ljj;


import com.baeldung.ljj.domain.model.Campaign;
import com.baeldung.ljj.domain.model.CampaignWithIgnoreUnknown;
import com.baeldung.ljj.domain.model.CampaignWithSetUnknownProperties;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import static org.junit.jupiter.api.Assertions.*;

class JacksonUnitTest {

    @Test
    // The Problem in Practice: Failing on an Unknown Property
    void givenUnknownProperty_whenUsingDefaultMapper_thenFail() {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        String json = """
            {
              "code": "C2",
              "name": "Campaign 2",
              "description": "The description of Campaign 2",
              "budget": 100
            }
            """;
        assertThrows(UnrecognizedPropertyException.class, () -> mapper.readValue(json, Campaign.class));
    }

    @Test
    // Handling Unknown Properties Using the ObjectMapper
    void givenMapperConfiguredToIgnoreUnknown_thenDeserializationSucceeds() throws JacksonException {
        ObjectMapper mapper = JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();
        String json = """
            {
              "code": "C2",
              "name": "Campaign 2",
              "description": "The description of Campaign 2",
              "budget": 500
            }
            """;
        Campaign campaign = mapper.readValue(json, Campaign.class);
        assertEquals("C2", campaign.getCode());
    }

    @Test
    // Class-Level Control with @JsonIgnoreProperties
    void givenJsonIgnorePropertiesConfiguredToIgnoreUnknown_thenDeserializationSucceeds() throws JacksonException {
        ObjectMapper mapper = new ObjectMapper();
        String json = """
            {
              "code": "C2",
              "name": "Campaign 2",
              "description": "The description of Campaign 2",
              "budget": 500
            }
            """;
        CampaignWithIgnoreUnknown campaign = mapper.readValue(json, CampaignWithIgnoreUnknown.class);

        System.out.println(campaign);
        assertEquals("C2", campaign.getCode());
    }

    @Test
    // Capturing Unknown Fields with @JsonAnySetter
    void givenJsonAnySetterConfiguredToRecordUnknown_thenDeserializationSucceeds() throws JacksonException {
        ObjectMapper mapper = new ObjectMapper();
        String json = """
            {
              "code": "C2",
              "name": "Campaign 2",
              "description": "The description of Campaign 2",
              "budget": 500
            }
            """;
        CampaignWithSetUnknownProperties campaign = mapper.readValue(json, CampaignWithSetUnknownProperties.class);

        System.out.println(campaign);
        assertTrue(campaign.getUnknownProperties().containsKey("budget"));
    }
}