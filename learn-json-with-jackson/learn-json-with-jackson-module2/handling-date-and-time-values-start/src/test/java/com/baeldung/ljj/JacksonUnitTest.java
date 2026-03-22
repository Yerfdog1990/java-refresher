package com.baeldung.ljj;

import java.io.IOException;
import java.time.*;
import java.util.TimeZone;

import com.baeldung.ljj.domain.model.Task;
import com.baeldung.ljj.domain.model.TaskStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import com.baeldung.ljj.domain.model.Campaign;

import static org.junit.jupiter.api.Assertions.*;

class JacksonUnitTest {

    final ObjectMapper defaultObjectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void givenCampaignJsonString_whenReadValueByObjectMapper_thenConvertedToCampaignObject() throws JsonProcessingException {
        //given
        String campaignJson = "{\"code\": \"C1\", \"name\": \"Campaign 1\", \"description\": \"The description of Campaign 1\"}";

        // when
        Campaign campaign = defaultObjectMapper.readValue(campaignJson, Campaign.class);

        // then
        assertNotNull(campaign);
        assertEquals("C1", campaign.getCode());
        assertEquals("Campaign 1", campaign.getName());
        assertEquals("The description of Campaign 1", campaign.getDescription());
    }

    @Test
    void givenCampaignJsonFile_whenReadValueByObjectMapper_thenConvertedToCampaignObject() throws IOException {
        //given

        // when
        Campaign campaign = defaultObjectMapper.readValue(getClass().getClassLoader()
                .getResourceAsStream("campaign.json"), Campaign.class);

        // then
        assertNotNull(campaign);
        assertEquals("C2", campaign.getCode());
        assertEquals("Campaign 2", campaign.getName());
        assertEquals("The description of Campaign 2", campaign.getDescription());
    }

    @Test
    void whenUsingDefaultMapper_thenSerializeAsArray() throws JsonProcessingException {
        Task task = new Task("T1", "Task 1", "The Task 1", LocalDate.of(2045, 3, 1), TaskStatus.TO_DO, null);
        String json = defaultObjectMapper.writeValueAsString(task);

        System.out.println(json);
        assertTrue(json.contains("[2045,3,1]"));
    }

    @Test
    void whenUsingDefaultMapper_thenDeserializeFromArray1() throws JsonProcessingException {
        String json = """
        {
          "code": "T1",
          "name": "Task 1",
          "description": "Task 1",
          "dueDate": [2045,3,1],
          "status": "TO_DO"
        }""";
        Task task = defaultObjectMapper.readValue(json, Task.class);
        System.out.println(task);

        assertEquals(task.getDueDate(), LocalDate.of(2045, 3, 1));
    }

    @Test
    void whenUsingDefaultMapper_thenSerializeDateTimeAsArray() throws JsonProcessingException {
        LocalDateTime dateTime = LocalDateTime.of(2045, 6, 28, 10, 10, 50, 1234);
        String dateTimeJson = defaultObjectMapper.writeValueAsString(dateTime);
        assertEquals("[2045,6,28,10,10,50,1234]", dateTimeJson);

        LocalDateTime dateTimeWithZeros = LocalDateTime.of(2045, 6, 28, 10, 0, 0, 0);
        String dateTimeWithZerosJson = defaultObjectMapper.writeValueAsString(dateTimeWithZeros);
        assertEquals("[2045,6,28,10,0]", dateTimeWithZerosJson);
    }

    @Test
    void whenUsingDefaultMapper_thenDeserializeFromArray2() throws JsonProcessingException {
        LocalDateTime dateTime = defaultObjectMapper.readValue("[2045,6,28,10,10]", LocalDateTime.class);
        assertEquals(LocalDateTime.of(2045, 6, 28, 10, 10), dateTime);

        System.out.println(dateTime);
    }

    @Test
    // Configuring ISO-8601 Date Serialization
    void whenUsingIsoObjectMapper_thenSerializeAsIsoFormat() throws JsonProcessingException {
        ObjectMapper isoObjectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        LocalDate date = LocalDate.of(2045, 3, 1);
        LocalDateTime dateTime = LocalDateTime.of(2045, 6, 28, 10, 10, 0);
        LocalDateTime preciseDateTime = LocalDateTime.of(2045, 6, 28, 10, 10, 50, 1234);
        String dateStr = isoObjectMapper.writeValueAsString(date);
        String dateTimeStr = isoObjectMapper.writeValueAsString(dateTime);
        String preciseDateTimeStr = isoObjectMapper.writeValueAsString(preciseDateTime);
        assertEquals("\"2045-03-01\"", dateStr);
        assertEquals("\"2045-06-28T10:10:00\"", dateTimeStr);
        assertEquals("\"2045-06-28T10:10:50.000001234\"", preciseDateTimeStr);
    }

    @Test
    // ZonedDateTime Serialization
    void givenZonedDateTime_whenSerializing_thenJSONFormattedUsingContextTimezone() throws JsonProcessingException {
        final ObjectMapper isoObjectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setTimeZone(TimeZone.getTimeZone("America/New_York"));

        ZonedDateTime zonedDateTime =
                ZonedDateTime.of(2045, 6, 28, 10, 10, 10, 0,
                ZoneId.of("Europe/Berlin"));

        String defaultMapperJson = defaultObjectMapper.writeValueAsString(zonedDateTime);
        String isoMapperJson = isoObjectMapper.writeValueAsString(zonedDateTime);

        assertEquals("2382250210.000000000", defaultMapperJson);
        assertEquals("\"2045-06-28T04:10:10-04:00\"", isoMapperJson); // Adjusts to context time zone
    }

    @Test
    // If we want to preserve the original zone and offset from the ZonedDateTime,
        // we need to disable a few additional features, as shown below:
    void givenZonedDateTime_whenSerializingNowAdjustingTZ_thenJSONFormattedUsingDateTimeTz()
            throws JsonProcessingException {
        final ObjectMapper isoObjectMapperNotAdjustingZone
                = new ObjectMapper().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE)
                .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .setTimeZone(TimeZone.getTimeZone("America/New_York"));

        ZonedDateTime zonedDateTime
                = ZonedDateTime.of(2045, 6, 28, 10, 10, 10, 0, ZoneId.of("Europe/Berlin"));
        ZonedDateTime sameZonedDateTimeinUTC = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);

        String isoMapperJson = isoObjectMapperNotAdjustingZone.writeValueAsString(zonedDateTime);
        String isoMapperJsonUTC = isoObjectMapperNotAdjustingZone.writeValueAsString(sameZonedDateTimeinUTC);

        assertEquals("\"2045-06-28T10:10:10+02:00\"", isoMapperJson);
        assertEquals("\"2045-06-28T08:10:10Z\"", isoMapperJsonUTC);
    }

    @Test
    // ZonedDateTime Deserialization
    void givenIsoZonedTimeFields_whenUsingIsoMapperNotAdjusting_thenDeserializeWithDateTimeZone()
            throws JsonProcessingException {
        final ObjectMapper isoObjectMapperNotAdjustingZone
                = new ObjectMapper().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE)
                .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .setTimeZone(TimeZone.getTimeZone("America/New_York"));

        String isoTimezoneJson = "\"2045-06-28T10:10:10+02:00\"";

        ZonedDateTime zonedDateTimeFromIso
                = isoObjectMapperNotAdjustingZone.readValue(isoTimezoneJson, ZonedDateTime.class);

        ZonedDateTime expectedZonedDateTimeWithOffset
                = ZonedDateTime.of(2045, 6, 28, 10, 10, 10, 0, ZoneOffset.of("+02:00"));

        // Passes to input time offset now
        assertEquals(ZoneOffset.of("+02:00"), zonedDateTimeFromIso.getZone());
        assertNotEquals(ZoneId.of("Europe/Berlin"), zonedDateTimeFromIso.getZone());
        assertEquals(expectedZonedDateTimeWithOffset, zonedDateTimeFromIso);
    }

    @Test
    // Field-Level Formatting With @JsonFormat
    void whenUsingJsonFormatAnnotation_thenDeserializeInSpecificFormat() throws JsonProcessingException {
        final ObjectMapper isoObjectMapperNotAdjustingZone
                = new ObjectMapper().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE)
                .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .setTimeZone(TimeZone.getTimeZone("America/New_York"));

        String json = """
        {
            "date":"01-03-2045",
            "zonedDateTime":"2045-06-28T10:10:10+02:00[Europe/Berlin]"
        }
        """;
        DateWrapper dateWrapper = isoObjectMapperNotAdjustingZone.readValue(json, DateWrapper.class);

        assertEquals(LocalDate.of(2045, 3, 1), dateWrapper.getDate());
        assertNotEquals(ZoneOffset.of("+02:00"), dateWrapper.getZonedDateTime().getZone());
        assertEquals(ZoneId.of("Europe/Berlin"), dateWrapper.getZonedDateTime().getZone());
        assertEquals(ZonedDateTime.of(2045, 6, 28, 10, 10, 10, 0, ZoneId.of("Europe/Berlin")), dateWrapper.getZonedDateTime());
    }
}
