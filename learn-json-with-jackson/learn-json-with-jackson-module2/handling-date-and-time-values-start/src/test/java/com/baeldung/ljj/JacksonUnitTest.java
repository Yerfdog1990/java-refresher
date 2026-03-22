package com.baeldung.ljj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import com.baeldung.ljj.domain.model.Campaign;
import tools.jackson.databind.json.JsonMapper;

class JacksonUnitTest {

    final JsonMapper defaultObjectMapper = JsonMapper.builder().build();

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
}

class DateWrapper {

    private LocalDate date;
    private LocalDateTime dateTime;
    private ZonedDateTime zonedDateTime;

    public DateWrapper(LocalDate date, LocalDateTime dateTime, ZonedDateTime zonedDateTime) {
        super();
        this.date = date;
        this.dateTime = dateTime;
        this.zonedDateTime = zonedDateTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }
}
