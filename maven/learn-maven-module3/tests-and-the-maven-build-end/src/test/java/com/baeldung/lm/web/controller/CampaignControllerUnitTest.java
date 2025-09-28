package com.baeldung.lm.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.baeldung.lm.domain.model.Campaign;
import com.baeldung.lm.web.dto.CampaignDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.baeldung.lm.service.CampaignService;

@ExtendWith(MockitoExtension.class)
public class CampaignControllerUnitTest {

    @Mock
    CampaignService campaignService;

    CampaignController campaignController;

    @BeforeEach
    public void setupDataSource() {
        campaignController = new CampaignController(campaignService);

    }

    @Test
    public void givenNonExistingCampaignId_whenFindOne_thenNotFoundExceptionRaised() {
        // given
        when(campaignService.findById(3L)).thenReturn(Optional.empty());

        // when
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            campaignController.findOne(3L);
        });

        // then
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void givenExistingCampaignId_whenFindOne_thenReturnNotNull() {
        // given
        Campaign c = new Campaign("CAMPAIGN-1", "Campaign 1 Name", "Lorem ipsum dolor sit amet");
        when(campaignService.findById(1L)).thenReturn(Optional.of(c));

        // when
        // CampaignDto foundCampaign = campaignController.findOne(2L);
        CampaignDto foundCampaign = campaignController.findOne(1L);

        // then
        assertThat(foundCampaign).isNotNull();
    }

}
