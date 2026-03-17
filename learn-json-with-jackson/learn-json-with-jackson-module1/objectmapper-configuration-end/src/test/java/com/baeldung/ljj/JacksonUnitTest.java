package com.baeldung.ljj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.baeldung.ljj.domain.model.Campaign;
import com.baeldung.ljj.domain.model.CampaignWithAnnotations;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

class JacksonUnitTest {

    @Test
    void givenDefaultObjectMapperInstance_whenSerializingAnObject_thenReturnJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Campaign campaign = new Campaign("A1", "JJ", "");
        String result = mapper.writeValueAsString(campaign);

        System.out.println(result);
    }

    @Test
    void givenEnableFeatureToggle_thenBehaviorIsAdjusted() throws JsonProcessingException {
        ObjectMapper customMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        String result = customMapper.writeValueAsString(new Campaign("A1", "JJ", ""));

        System.out.println(result);
        assertTrue(result.contains("\n"));
    }

    @Test
    void givenDisableFeatureToggle_thenBehaviorIsAdjusted() throws JsonProcessingException {
        class EmptyClass {
        }

        ObjectMapper defaultMapper = new ObjectMapper();
        assertThrows(InvalidDefinitionException.class, () -> defaultMapper.writeValueAsString(new EmptyClass()));

        ObjectMapper customMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        String customOutput = customMapper.writeValueAsString(new EmptyClass());
        assertEquals("{}", customOutput);
    }

    @Test
    void givenConfigureFeatureToggle_thenBehaviorIsAdjusted() throws JsonProcessingException {
        String json = """
            {"code":"X1","name":"Extra","description":"-", "extraField":"ignored"}
            """;

        ObjectMapper defaultMapper = new ObjectMapper();
        assertThrows(UnrecognizedPropertyException.class, () -> defaultMapper.readValue(json, Campaign.class));

        ObjectMapper customMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Campaign campaignResult = customMapper.readValue(json, Campaign.class);
        // no exception and the fields were set
        assertEquals("X1", campaignResult.getCode());
    }

    @Test
    void givenAnObjectWithNON_NULLFlagSet_whenSerializedTheObject_thenNullValuesExcluded() throws Exception {
        Campaign exampleCampaign = new Campaign("A1", "JJ", null);

        ObjectMapper defaultMapper = new ObjectMapper();
        String defaultOutput = defaultMapper.writeValueAsString(exampleCampaign);
        assertTrue(defaultOutput.contains("null"));

        ObjectMapper customMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String output = customMapper.writeValueAsString(exampleCampaign);
        System.out.println(output);
        assertFalse(output.contains("null"));
    }

    @Test
    void givenGlobalAlways_thenClassNonNull_andFieldAlways_shouldRespectAnnotationPrecedence() throws Exception {
        // Global: include everything (even nulls)
        ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.ALWAYS);

        // code = null (class-level NON_NULL should drop it)
        // description = null (field-level ALWAYS should force include)
        CampaignWithAnnotations campaign = new CampaignWithAnnotations(null, "JJ", null);

        String json = mapper.writeValueAsString(campaign);
        System.out.println(json);

        assertTrue(json.contains("\"description\":null"));
        assertFalse(json.contains("\"code\":"));
    }
}