package com.baeldung.ljj;

import com.baeldung.ljj.domain.model.Campaign;
import com.baeldung.ljj.domain.model.Views;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JacksonUnitTest {

    ObjectMapper objectMapper = new ObjectMapper();
    Campaign campaign = new Campaign(
            "C1", "Campaign 1", "Description of Campaign 1", null, false);

    @Test
    // The Summary View Example
    void givenCampaign_whenSerializingWithSummaryView_thenOnlySummaryFieldsAreIncluded() throws JacksonException {
        objectMapper = JsonMapper.builder()
                .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .build();

        String json = objectMapper.writerWithView(Views.Summary.class).writeValueAsString(campaign);

        assertTrue(json.contains("\"code\""));
        assertTrue(json.contains("\"name\""));
        assertFalse(json.contains("\"description\""));
        assertFalse(json.contains("\"tasks\""));
        assertFalse(json.contains("\"closed\""));
    }

    @Test
    // The Detail View Example
    void givenCampaign_whenSerializingWithDetailView_thenSummaryAndDetailFieldsAreIncluded() throws JacksonException {
        objectMapper = JsonMapper.builder()
                .disable(MapperFeature.DEFAULT_VIEW_INCLUSION).build();

        String json = objectMapper.writerWithView(Views.Detail.class)
                .writeValueAsString(campaign);

        assertTrue(json.contains("\"code\""));
        assertTrue(json.contains("\"name\""));
        assertTrue(json.contains("\"description\""));
        assertFalse(json.contains("\"tasks\""));
        assertFalse(json.contains("\"closed\""));
    }

    @Test
    // Default Behavior Without a View
    void givenCampaign_whenSerializingWithoutView_thenAllFieldsAreIncluded() throws JacksonException {
        String json = objectMapper.writeValueAsString(campaign);

        assertTrue(json.contains("\"code\""));
        assertTrue(json.contains("\"name\""));
        assertTrue(json.contains("\"description\""));
        assertTrue(json.contains("\"tasks\""));
        assertTrue(json.contains("\"closed\""));
    }

    @Test
    void givenFieldInMultipleViews_whenSerializingWithEachView_thenFieldIsIncluded() throws JacksonException {
        objectMapper = JsonMapper.builder()
                .disable(MapperFeature.DEFAULT_VIEW_INCLUSION).build();

        String summaryJson = objectMapper
                .writerWithView(Views.Summary.class)
                .writeValueAsString(campaign);
        assertTrue(summaryJson.contains("\"code\""));

        String internalJson = objectMapper
                .writerWithView(Views.Internal.class)
                .writeValueAsString(campaign);
        assertTrue(internalJson.contains("\"code\""));
    }
}