package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.extension.CloseResourcesExtension;
import com.baeldung.lhj.persistence.model.Campaign;
import com.baeldung.lhj.persistence.repository.CampaignRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

@ExtendWith(CloseResourcesExtension.class)
class DefaultCampaignRepositoryUnitTest {
    CampaignRepository campaignRepository = new DefaultCampaignRepository();

    @Test
    void givenExistingCampaign_whenFindById_thenCampaignRetrieved() {
        // given
        Campaign existingCampaign = new Campaign("C-1", "Campaign 1", "Campaign 1 Description");
        campaignRepository.save(existingCampaign);

        // when
        Campaign retrievedCampaign = campaignRepository.findById(existingCampaign.getId()).get();

        // then
        Assertions.assertEquals(existingCampaign, retrievedCampaign);
    }

    @Test
    void givenExistingCampaign_whenFindByNonExistingId_thenNoCampaignRetrieved() {
        // given
        Campaign existingCampaign = new Campaign("C-2", "Campaign 2", "Campaign 2 Description");
        campaignRepository.save(existingCampaign);

        // when
        Optional<Campaign> retrievedCampaign = campaignRepository.findById(99L);

        // then
        Assertions.assertTrue(retrievedCampaign.isEmpty());
    }
}