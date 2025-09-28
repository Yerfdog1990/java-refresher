package com.baeldung.lm.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.baeldung.lm.domain.model.Campaign;
import com.baeldung.lm.persistence.repository.CampaignRepository;

@ExtendWith(MockitoExtension.class)
public class DefaultCampaignServiceUnitTest {

    @Mock
    CampaignRepository campaignRepository;

    DefaultCampaignService campaignService;

    @BeforeEach
    public void setupDataSource() {
        campaignService = new DefaultCampaignService(campaignRepository);

    }

    @Test
    public void whenUpdateCampaign_thenOnlyAllowedFieldsChanged() {
        // given
        Campaign inputCampaign = new Campaign("UPDATED-CAMPAIGN-CODE", "Updated Campaign 3 Name", "Updated campaign description");
        Campaign campaignToUpdate = mock(Campaign.class);
        when(campaignRepository.findById(3L)).thenReturn(Optional.of(campaignToUpdate));
        Campaign updatedCampaign = mock(Campaign.class);
        when(campaignRepository.save(campaignToUpdate)).thenReturn(updatedCampaign);

        // when
        Campaign outputCampaign = campaignService.updateCampaign(3L, inputCampaign)
            .get();

        // then
        Mockito.verify(campaignRepository)
            .findById(3L);

        verify(campaignToUpdate).setName("Updated Campaign 3 Name");
        verify(campaignToUpdate).setDescription("Updated campaign description");
        verify(campaignToUpdate, never()).setCode(any());
        verify(campaignToUpdate, never()).setId(any());

        assertThat(outputCampaign).isSameAs(updatedCampaign);
    }

}
