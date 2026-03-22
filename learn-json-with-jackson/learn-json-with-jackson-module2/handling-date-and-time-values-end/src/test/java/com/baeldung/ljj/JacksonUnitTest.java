package com.baeldung.ljj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import com.baeldung.ljj.domain.model.Campaign;
import com.baeldung.ljj.domain.model.Task;
import com.baeldung.ljj.domain.model.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

class JacksonUnitTest {

    final JsonMapper defaultObjectMapper = JsonMapper.builder().build();
    final JsonMapper timestampObjectMapper = JsonMapper.builder()
            .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
            .build();
    final JsonMapper isoObjectMapper = JsonMapper.builder()
            .defaultTimeZone(TimeZone.getTimeZone("America/New_York"))
            .build();
    final JsonMapper isoObjectMapperNotAdjustingZone = JsonMapper.builder()
            .disable(DateTimeFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE)
            .disable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
            .disable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            .defaultTimeZone(TimeZone.getTimeZone("America/New_York"))
            .build();

    @Test
    void givenCampaignJsonString_whenReadValueByObjectMapper_thenConvertedToCampaignObject() {
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

    // Default date and time mappings

    @Test
    void whenUsingDefaultMapper_thenLocalDateAsIsoString() {
        LocalDate date = LocalDate.of(2045, 3, 1);
        String json = defaultObjectMapper.writeValueAsString(date);
        assertEquals("\"2045-03-01\"", json);
    }

    @Test
    void whenUsingTimestampMapper_thenLocalDateAsArray() {
        LocalDate date = LocalDate.of(2045, 3, 1);
        String json = timestampObjectMapper.writeValueAsString(date);
        assertEquals("[2045,3,1]", json);
    }

    @Test
    void whenUsingDefaultMapper_thenSerializeAsIsoString() {
        Task task = new Task("T1", "Task 1", "The Task 1", LocalDate.of(2045, 3, 1), TaskStatus.TO_DO, null);
        String json = defaultObjectMapper.writeValueAsString(task);
        assertTrue(json.contains("\"2045-03-01\""));
    }

    @Test
    void whenUsingTimestampMapper_thenSerializeAsArray() {
        Task task = new Task("T1", "Task 1", "The Task 1", LocalDate.of(2045, 3, 1), TaskStatus.TO_DO, null);
        String json = timestampObjectMapper.writeValueAsString(task);
        assertTrue(json.contains("[2045,3,1]"));
    }

    @Test
    void whenUsingTimestampMapper_thenSerializeDateTimeAsArray() {
        LocalDateTime dateTime = LocalDateTime.of(2045, 6, 28, 0, 0, 50, 1234);
        String dateTimeJson = timestampObjectMapper.writeValueAsString(dateTime);
        assertEquals("[2045,6,28,0,0,50,1234]", dateTimeJson);

        LocalDateTime dateTimeWithZeros = LocalDateTime.of(2045, 6, 28, 10, 0, 0);
        String dateTimeWithZerosJson = timestampObjectMapper.writeValueAsString(dateTimeWithZeros);
        assertEquals("[2045,6,28,10,0]", dateTimeWithZerosJson);
    }

    @Test
    void whenUsingDefaultMapper_thenDeserializeFromIsoString() {
        String json = """
            {
                "code": "T1",
                "name": "Task 1",
                "description": "Task 1",
                "dueDate": "2045-03-01",
                "status": "TO_DO"
            }""";
        Task task = defaultObjectMapper.readValue(json, Task.class);
        assertEquals(task.getDueDate(), LocalDate.of(2045, 3, 1));
    }

    @Test
    void whenUsingTimestampMapper_thenDeserializeDateArray() {
        String json = """
            {
                "code": "T1",
                "name": "Task 1",
                "description": "Task 1",
                "dueDate": [2045,3,1],
                "status": "TO_DO"
            }""";
        Task task = timestampObjectMapper.readValue(json, Task.class);
        assertEquals(task.getDueDate(), LocalDate.of(2045, 3, 1));
    }

    @Test
    void whenUsingDefaultMapper_thenDeserializeFromArray() {
        LocalDateTime dateTime = defaultObjectMapper.readValue("[2045,6,28,10,10]", LocalDateTime.class);
        assertEquals(LocalDateTime.of(2045, 6, 28, 10, 10), dateTime);
    }

    // Using ISO-8601

    @Test
    void whenUsingIso8601Format_thenSerializeAsIsoFormat() {
        LocalDate date = LocalDate.of(2045, 3, 1);
        LocalDateTime dateTime = LocalDateTime.of(2045, 6, 28, 10, 10, 0);
        LocalDateTime preciseDateTime = LocalDateTime.of(2045, 6, 28, 10, 10, 50, 1234);
        String dateStr = isoObjectMapper.writeValueAsString(date);
        String dateTimeStr = isoObjectMapper.writeValueAsString(dateTime);
        String preciseDateTimeStr = isoObjectMapper.writeValueAsString(preciseDateTime);
        assertEquals("\"2045-03-01\"", dateStr);
        assertEquals("\"2045-06-28T10:10:00\"", dateTimeStr);
        assertEquals("\"2045-06-28T10:10:50.000001234\"", preciseDateTimeStr);

        System.out.println(isoObjectMapper.writeValueAsString(dateTime.toInstant(ZoneOffset.UTC)));
    }

    @Test
    void whenUsingIso8601Format_thenSerializeAsIsoFormatIncMillis() {
        LocalDate date = LocalDate.of(2045, 3, 1);
        LocalDateTime dateTime = LocalDateTime.of(2045, 6, 28, 10, 10, 0, 987654321);

        String dateStr = isoObjectMapper.writeValueAsString(date);
        String dateTimeStr = isoObjectMapper.writeValueAsString(dateTime);

        assertEquals("\"2045-06-28T10:10:00.987654321\"", dateTimeStr);
        assertEquals("\"2045-03-01\"", dateStr);
    }

    @Test
    void whenUsingIso8601Formats_thenDeserializeFromIsoFormat() {
        String isoDateTime = "\"2045-06-28T10:10:00\"";
        String isoDate = "\"2045-03-01\"";
        LocalDateTime dateTime = isoObjectMapper.readValue(isoDateTime, LocalDateTime.class);
        LocalDate date = isoObjectMapper.readValue(isoDate, LocalDate.class);
        assertEquals(LocalDate.of(2045, 3, 1), date);
        assertEquals(LocalDateTime.of(2045, 6, 28, 10, 10), dateTime);
    }

    // Time Zones - serialization

    @Test
    void givenZonedDateTime_whenSerializing_thenJSONFormattedUsingContextTimezone() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2045, 6, 28, 10, 10, 10, 0, ZoneId.of("Europe/Berlin"));

        String timestampMapperJson = timestampObjectMapper.writeValueAsString(zonedDateTime);
        String isoMapperJson = isoObjectMapper.writeValueAsString(zonedDateTime);

        assertEquals("2382250210.000000000", timestampMapperJson);
        assertEquals("\"2045-06-28T04:10:10-04:00\"", isoMapperJson); // Adjusts to context time zone
    }

    @Test
    void givenZonedDateTime_whenSerializingWithNoContextTimeZone_thenJSONFormattedUsingDateTimezone() {
        final JsonMapper isoObjectMapperWithoutTimezone = JsonMapper.builder().build();

        ZonedDateTime zonedDateTime = ZonedDateTime.of(2045, 6, 28, 10, 10, 10, 0, ZoneId.of("Europe/Berlin"));

        String isoMapperJson = isoObjectMapperWithoutTimezone.writeValueAsString(zonedDateTime);

        assertEquals("\"2045-06-28T10:10:10+02:00\"", isoMapperJson); // Maintains date time zone
    }

    @Test
    void givenZonedDateTime_whenSerializingNowAdjustingTZ_thenJSONFormattedUsingDateTimeTz() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2045, 6, 28, 10, 10, 10, 0, ZoneId.of("Europe/Berlin"));
        ZonedDateTime sameZonedDateTimeinUTC = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);

        String isoMapperJson = isoObjectMapperNotAdjustingZone.writeValueAsString(zonedDateTime);
        String isoMapperJsonUTC = isoObjectMapperNotAdjustingZone.writeValueAsString(sameZonedDateTimeinUTC);

        assertEquals("\"2045-06-28T10:10:10+02:00\"", isoMapperJson);
        assertEquals("\"2045-06-28T08:10:10Z\"", isoMapperJsonUTC);
    }

    // Time Zones - deserialization

    @Test
    void givenEpochZonedTimeFields_whenUsingDefaultMappers_thenDeserializeWithoutTimeZone() {
        String epochTimezoneJson = "2382250210.000000000";

        ZonedDateTime zonedDateTimeFromDefault = defaultObjectMapper.readValue(epochTimezoneJson, ZonedDateTime.class);

        ZonedDateTime expectedZonedDateTimeWithOffset = ZonedDateTime.of(2045, 6, 28, 10, 10, 10, 0, ZoneOffset.of("+02:00"));
        ZonedDateTime expectedZonedDateTimeInUTC = ZonedDateTime.of(2045, 6, 28, 8, 10, 10, 0, ZoneOffset.UTC);

        assertEquals(expectedZonedDateTimeInUTC, zonedDateTimeFromDefault);
        assertEquals(ZoneOffset.UTC, zonedDateTimeFromDefault.getZone());
        assertNotEquals(expectedZonedDateTimeWithOffset, zonedDateTimeFromDefault);
    }

    @Test
    void givenIsoZonedTimeFields_whenUsingIsoMapperAdjusting_thenDeserializeWithContextTimeZone() {
        String isoTimezoneJson = "\"2045-06-28T10:10:10+02:00\"";

        ZonedDateTime zonedDateTimeFromIso = isoObjectMapper.readValue(isoTimezoneJson, ZonedDateTime.class);

        ZonedDateTime expectedZonedDateTimeWithOffset = ZonedDateTime.of(2045, 6, 28, 4, 10, 10, 0, ZoneOffset.of("-04:00"));

        // Passes to context time zone by default too!
        assertEquals(ZoneId.of("America/New_York"), zonedDateTimeFromIso.getZone());
        assertEquals(expectedZonedDateTimeWithOffset.toInstant(), zonedDateTimeFromIso.toInstant());
        assertNotEquals(expectedZonedDateTimeWithOffset, zonedDateTimeFromIso);
    }

    @Test
    void givenIsoZonedTimeFields_whenUsingIsoMapperNotAdjusting_thenDeserializeWithDateTimeZone() {
        String isoTimezoneJson = "\"2045-06-28T10:10:10+02:00\"";

        ZonedDateTime zonedDateTimeFromIso = isoObjectMapperNotAdjustingZone.readValue(isoTimezoneJson, ZonedDateTime.class);

        ZonedDateTime expectedZonedDateTimeWithOffset = ZonedDateTime.of(2045, 6, 28, 10, 10, 10, 0, ZoneOffset.of("+02:00"));

        // Passes to input time offset now
        assertEquals(ZoneOffset.of("+02:00"), zonedDateTimeFromIso.getZone());
        assertNotEquals(ZoneId.of("Europe/Berlin"), zonedDateTimeFromIso.getZone());
        assertEquals(expectedZonedDateTimeWithOffset, zonedDateTimeFromIso);
    }

    // Field-level formatting with annotations

    @Test
    void whenUsingJsonFormatAnnotation_thenDeserializeInSpecificFormat() {
        String json = """
            {
                "date":"01-03-2045",
                "zonedDateTime":"2045-06-28T10:10:10+02:00[Europe/Berlin]"
            }
            """;
        DateWrapper dateWrapper = isoObjectMapperNotAdjustingZone.readValue(json, DateWrapper.class);

        assertEquals(LocalDate.of(2045, 3, 1), dateWrapper.getDate());
        assertNotEquals(ZoneOffset.of("+02:00"), dateWrapper.getZonedDateTime()
                .getZone());
        assertEquals(ZoneId.of("Europe/Berlin"), dateWrapper.getZonedDateTime()
                .getZone());
        assertEquals(ZonedDateTime.of(2045, 6, 28, 10, 10, 10, 0, ZoneId.of("Europe/Berlin")), dateWrapper.getZonedDateTime());
    }

    @Test
    void givenDataWrapper_whenUsingNotAdjustingTzMapper_thenClassSerializedAsPerFieldsFormat() {
        LocalDate date = LocalDate.of(2045, 3, 1);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2045, 6, 28, 10, 10, 10, 0, ZoneId.of("Europe/Berlin"));

        DateWrapper dateWrapper = new DateWrapper(date, zonedDateTime);
        String json = isoObjectMapperNotAdjustingZone.writeValueAsString(dateWrapper);

        System.out.println(json);

        assertTrue(json.contains("01-03-2045"));
        assertTrue(json.contains("2045-06-28T10:10:10+02:00[Europe/Berlin]"));
    }

    @Test
    void whenSerializingVariousJavaTimeTypes_thenObserveFormats() {
        LocalTime localTime = LocalTime.of(15, 45, 30, 123_000_000); // 15:45:30.123
        OffsetDateTime offsetDateTime = OffsetDateTime.of(2045, 6, 28, 10, 10, 10, 0, ZoneOffset.ofHours(-2));
        Instant instant = Instant.parse("2045-06-28T10:10:10-02:00");
        Duration duration = Duration.of(90, ChronoUnit.MINUTES); // PT1H30M - 1 hour 30 minutes
        Period period = Period.of(2, 6, 15); // P2Y6M15D - 2 years, 6 months, and 15 days.

        // --- Using timestampObjectMapper (array or numeric output) ---
        String localTimeTimestamp = timestampObjectMapper.writeValueAsString(localTime);
        String offsetDateTimeTimestamp = timestampObjectMapper.writeValueAsString(offsetDateTime);
        String instantTimestamp = timestampObjectMapper.writeValueAsString(instant);
        String durationTimestamp = timestampObjectMapper.writeValueAsString(duration);
        String periodTimestamp = timestampObjectMapper.writeValueAsString(period);

        System.out.println("OffsetDateTime (timestamp): " + offsetDateTimeTimestamp);

        assertEquals("[15,45,30,123000000]", localTimeTimestamp);
        assertEquals("2382264610.000000000", offsetDateTimeTimestamp);
        assertEquals("2382264610.000000000", instantTimestamp);
        assertEquals("5400.000000000", durationTimestamp);
        assertEquals("\"P2Y6M15D\"", periodTimestamp);

        // --- Using isoObjectMapperNotAdjustingZone (ISO strings, no zone context) ---
        String localTimeIso = isoObjectMapperNotAdjustingZone.writeValueAsString(localTime);
        String offsetDateTimeIso = isoObjectMapperNotAdjustingZone.writeValueAsString(offsetDateTime);
        String instantIso = isoObjectMapperNotAdjustingZone.writeValueAsString(instant);
        String durationIso = isoObjectMapperNotAdjustingZone.writeValueAsString(duration);
        String periodIso = isoObjectMapperNotAdjustingZone.writeValueAsString(period);

        assertEquals("\"15:45:30.123\"", localTimeIso);
        assertEquals("\"2045-06-28T10:10:10-02:00\"", offsetDateTimeIso);
        assertEquals("\"2045-06-28T12:10:10Z\"", instantIso);
        assertEquals("\"PT1H30M\"", durationIso); // Note we're disabling DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS for this
        assertEquals("\"P2Y6M15D\"", periodIso);
    }

    @Test
    void whenUsingLegacyDateType_thenSerializeInSpecificFormat() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        JsonMapper objectMapper = JsonMapper.builder().defaultDateFormat(df).build();

        Date date = df.parse("2045/06/01 10:00:00");
        String json = objectMapper.writeValueAsString(date);
        assertEquals("\"2045/06/01 10:00:00\"", json);
    }

}

class DateWrapper {

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX'['VV']'")
    private ZonedDateTime zonedDateTime;

    public DateWrapper() {
    }

    public DateWrapper(LocalDate date, ZonedDateTime zonedDateTime) {
        super();
        this.date = date;
        this.zonedDateTime = zonedDateTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }
}
