package com.baeldung.ljj;

import com.baeldung.ljj.domain.model.Campaign;
import com.baeldung.ljj.domain.model.CampaignWithIgnoreUnknown;
import com.baeldung.ljj.domain.model.CampaignWithSetUnknownProperties;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JacksonUnitTest {

    @Test
    void givenUnknownProperty_whenUsingStrictMapper_thenFail() {
        JsonMapper mapper = JsonMapper.builder().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
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
    void givenDefaultMapper_thenDeserializationSucceeds() {
        JsonMapper mapper = JsonMapper.builder().build();
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
    void givenJsonIgnorePropertiesConfiguredToIgnoreUnknown_thenDeserializationSucceeds() {
        JsonMapper mapper = JsonMapper.builder().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
        String json = """
                {
                  "code": "C2",
                  "name": "Campaign 2",
                  "description": "The description of Campaign 2",
                  "budget": 500
                }
                """;
        CampaignWithIgnoreUnknown campaign = mapper.readValue(json, CampaignWithIgnoreUnknown.class);
        assertEquals("C2", campaign.getCode());
    }

    @Test
    void givenJsonAnySetterConfiguredToRecordUnknown_thenDeserializationSucceeds() {
        JsonMapper mapper = JsonMapper.builder().build();
        String json = """
                {
                  "code": "C2",
                  "name": "Campaign 2",
                  "description": "The description of Campaign 2",
                  "budget": 500
                }
                """;
        CampaignWithSetUnknownProperties campaign = mapper.readValue(json, CampaignWithSetUnknownProperties.class);
        assertTrue(campaign.getUnknownProperties().containsKey("budget"));
    }
}