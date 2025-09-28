package campain.controller;

import com.baeldung.lm.domain.model.Campaign;
import com.baeldung.lm.service.CampaignService;
import com.baeldung.lm.web.controller.CampaignController;
import com.baeldung.lm.web.dto.CampaignDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignControllerUnitTest {
    @Mock
    private CampaignService campaignService;

    @InjectMocks
    private CampaignController campaignController;

    @BeforeEach
    void setUp() {
        // Any additional setup can go here
    }

    @Test
    public void givenExistingCampaignId_whenFindOne_thenReturnNotNull() {
        Campaign c = new Campaign("CAMPAIGN-1", "Campaign 1 Name", "Lorem ipsum dolor sit amet");
        when(campaignService.findById(1L)).thenReturn(Optional.of(c));

        CampaignDto foundCampaign = campaignController.findOne(2L); // Test set to fail intentionally. The parameter should be 1L instead of 2L.

        assertThat(foundCampaign).isNotNull();
    }
}