package com.baeldung.ljj;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.baeldung.ljj.domain.model.Campaign;
import com.baeldung.ljj.domain.model.Views;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

class JacksonUnitTest {

    ObjectMapper objectMapper = new ObjectMapper();
    Campaign campaign = new Campaign("C1", "Campaign 1", "Description of Campaign 1", null, false);

    @Test
    void givenCampaign_whenSerializingWithSummaryView_thenOnlySummaryFieldsAreIncluded() {
        objectMapper = JsonMapper.builder()
                .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .build();
        String json = objectMapper.writerWithView(Views.Summary.class)
                .writeValueAsString(campaign);

        assertTrue(json.contains("\"code\""));
        assertTrue(json.contains("\"name\""));
        assertFalse(json.contains("\"description\""));
        assertFalse(json.contains("\"tasks\""));
        assertFalse(json.contains("\"closed\""));
    }

    @Test
    void givenCampaign_whenSerializingWithDetailView_thenSummaryAndDetailFieldsAreIncluded() {
        objectMapper = JsonMapper.builder()
                .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .build();
        String json = objectMapper.writerWithView(Views.Detail.class)
                .writeValueAsString(campaign);

        assertTrue(json.contains("\"code\""));
        assertTrue(json.contains("\"name\""));
        assertTrue(json.contains("\"description\""));
        assertFalse(json.contains("\"tasks\""));
        assertFalse(json.contains("\"closed\""));
    }

    @Test
    void givenCampaign_whenSerializingWithoutView_thenAllFieldsAreIncluded() {
        String json = objectMapper.writeValueAsString(campaign);

        assertTrue(json.contains("\"code\""));
        assertTrue(json.contains("\"name\""));
        assertTrue(json.contains("\"description\""));
        assertTrue(json.contains("\"tasks\""));
        assertTrue(json.contains("\"closed\""));
    }

    @Test
    void givenFieldInMultipleViews_whenSerializingWithEachView_thenFieldIsIncluded() {
        objectMapper = JsonMapper.builder()
                .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .build();
        String summaryJson = objectMapper.writerWithView(Views.Summary.class)
                .writeValueAsString(campaign);
        assertTrue(summaryJson.contains("\"code\""));

        String internalJson = objectMapper.writerWithView(Views.Internal.class)
                .writeValueAsString(campaign);
        assertTrue(internalJson.contains("\"code\""));
    }

}