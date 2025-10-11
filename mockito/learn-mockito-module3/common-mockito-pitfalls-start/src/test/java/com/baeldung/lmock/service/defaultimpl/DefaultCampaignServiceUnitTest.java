package com.baeldung.lmock.service.defaultimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.baeldung.lmock.domain.model.Campaign;
import com.baeldung.lmock.persistence.repository.CampaignRepository;

class DefaultCampaignServiceUnitTest {

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private DefaultCampaignService campaignService;

    /*
    5. Unnecessary Stubbing
    Stubbing methods unnecessarily, especially in the setup phase of tests, is another common issue.
    This happens when we stub methods that our tests never actually invoke.

    For instance, let’s take a look at the DefaultCampaignServiceUnitTest class from the initial project and try to identify the problem:

    class DefaultCampaignServiceUnitTest {

        @Mock
        private CampaignRepository campaignRepository;

        @InjectMocks
        private DefaultCampaignService campaignService;

        @BeforeEach
        void setup() {
            MockitoAnnotations.openMocks(this);

            Campaign c1 = new Campaign("code 1", "name 1", "description 1");
            Campaign c2 = new Campaign("code 2", "name 2", "description 2");

            when(campaignRepository.findAll()).thenReturn(List.of(c1, c2));
            when(campaignRepository.findById(1L)).thenReturn(Optional.of(c1));
            when(campaignRepository.findById(2L)).thenReturn(Optional.of(c2));
        }

        @Test
        void whenFindCampaigns_thenReturnAllCampaigns() {
            List<Campaign> campaigns = campaignService.findCampaigns();

            assertEquals(2, campaigns.size());
        }
    }
    Copy
    In this test, we use the MockitoAnnotations.openMocks(this) approach to initialize mocks instead of the MockitoExtension.
    This is because Mockito discourages unnecessary stubbing, and the extension will fail tests if it detects any.

    So, for our demonstration, we’ve switched to using the openMocks() method.

    In the @BeforeEach method, we stub campaignRepository.findAll(), findById(1L), and findById(2L).
    However, the only test we have calls findAll(), so the other stubs aren’t needed.

    Unnecessary stubbing is considered a bad practice in Mockito.
    To fix this, we should move stubbing into the specific tests that need it, and remove any stubs that we’re not using:
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenFindCampaigns_thenReturnAllCampaigns() {
        Campaign c1 = new Campaign("code 1", "name 1", "description 1");
        Campaign c2 = new Campaign("code 2", "name 2", "description 2");

        when(campaignRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Campaign> campaigns = campaignService.findCampaigns();
        assertEquals(2, campaigns.size());
    }
}