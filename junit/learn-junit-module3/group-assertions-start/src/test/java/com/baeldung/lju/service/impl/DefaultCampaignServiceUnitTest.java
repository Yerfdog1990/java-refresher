package com.baeldung.lju.service.impl;

import com.baeldung.lju.domain.model.Campaign;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DefaultCampaignServiceUnitTest {

    private DefaultCampaignService service = new DefaultCampaignService();

    @Test
    void givenCampaigns_whenFindCampaignById_thenReturnCampaign_andAssert() {
        //Given - save campaign for the test
        Campaign campaign = new Campaign("CA-123", "Social Media Campaign", "Campaign through FB, Twitter");

        var created = service.create(campaign);

        // When
        Optional<Campaign> foundCampaign = service.findById(created.getId());

        // Then

        //Individual assertions
        //assertTrue(foundCampaign.isPresent());
        //assertEquals("Social Media Campaign", foundCampaign.get().getName());
        //assertEquals("CA-123", foundCampaign.get().getCode());
        //assertEquals("New Social Media Campaign", foundCampaign.get().getName());
        //assertEquals("CA-999", foundCampaign.get().getCode());
        //assertFalse(foundCampaign.get().isClosed());

        // Group assertions
        // This approach ensures that all assertions execute, providing a comprehensive view of all issues in a single test run instead of requiring multiple reruns.
        assertAll("Asserting for campaign CA-123",
                () -> assertTrue(foundCampaign.isPresent()),
                () -> assertEquals("New Social Media Campaign", foundCampaign.get().getName()),// Set to fail
                () -> assertEquals("CA-999", foundCampaign.get().getCode()), // Set to fail
                // () -> assertEquals("Social Media Campaign", foundCampaign.get().getName()),
                // () -> assertEquals("CA-123", foundCampaign.get().getCode()),
                () -> assertFalse(foundCampaign.get().isClosed())
        );
        }
}
