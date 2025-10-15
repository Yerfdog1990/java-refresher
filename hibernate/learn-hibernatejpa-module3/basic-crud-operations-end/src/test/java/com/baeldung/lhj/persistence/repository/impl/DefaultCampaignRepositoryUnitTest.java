package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.extension.CloseResourcesExtension;
import com.baeldung.lhj.persistence.model.Campaign;
import com.baeldung.lhj.persistence.repository.CampaignRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
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

    @Test
    void givenExistingCampaign_whenUpdate_thenCampaignUpdated() {
        // given
        Campaign existingCampaign = new Campaign("C-3", "Campaign 3", "Campaign 3 Description");
        campaignRepository.save(existingCampaign);
        Long campaignId = existingCampaign.getId();

        // when
        String updatedName = "Updated Campaign 3";
        String updatedDescription = "Updated Campaign 3 Description";
        existingCampaign.setName(updatedName);
        existingCampaign.setDescription(updatedDescription);
        campaignRepository.update(campaignId, existingCampaign);

        // then
        Campaign retrievedCampaign = campaignRepository.findById(campaignId).get();
        Assertions.assertEquals(updatedName, retrievedCampaign.getName());
        Assertions.assertEquals(updatedDescription, retrievedCampaign.getDescription());
    }

    @Test
    void givenExistingCampaign_whenDeleteById_thenCampaignRemoved() {
        // given
        Campaign existingCampaign = new Campaign("C-4", "Campaign 4", "Campaign 4 Description");
        campaignRepository.save(existingCampaign);
        Long campaignId = existingCampaign.getId();

        // when
        campaignRepository.deleteById(campaignId);

        // then
        Optional<Campaign> retrievedCampaign = campaignRepository.findById(campaignId);
        Assertions.assertTrue(retrievedCampaign.isEmpty());
    }

}
