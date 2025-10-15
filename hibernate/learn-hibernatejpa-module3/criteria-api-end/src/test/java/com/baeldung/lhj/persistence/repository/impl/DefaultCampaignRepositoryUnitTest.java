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
    void givenExistingCampaigns_whenFindByAll_thenAllCampaignsRetrieved() {
        // given
        int numberOfCampaigns = 10;
        for (int i = 0; i < numberOfCampaigns; i++) {
            Campaign campaign = new Campaign("C-00%d".formatted(i), "Campaign 00%d".formatted(i), "Campaign 00%d Description".formatted(i));
            campaignRepository.save(campaign);
        }

        // when
        List<Campaign> retrievedCampaigns = campaignRepository.findAll();

        // then
        Assertions.assertTrue(retrievedCampaigns.size() >= numberOfCampaigns);
    }

    @Test
    void givenExistingCampaigns_whenFindByNameOrDescriptionContaining_thenMatchingCampaignsReturned() {
        // given
        Campaign christmasSaleCampaign = new Campaign("C-3", "Christmas Sale Campaign", "This is not a Genetic Campaign");
        Campaign genericCampaign = new Campaign("C-4", "Generic Campaign", "This is not for Christmas");
        campaignRepository.save(christmasSaleCampaign);
        campaignRepository.save(genericCampaign);

        // when
        List<Campaign> retrievedCampaigns = campaignRepository.findByNameOrDescriptionContaining("Christmas");

        // then
        Assertions.assertEquals(2, retrievedCampaigns.size());
        Assertions.assertTrue(retrievedCampaigns.containsAll(List.of(christmasSaleCampaign, genericCampaign)));
    }

    @Test
    void givenCampaignsWithoutTasks_whenDeleteCampaignsWithoutTasks_thenCampaignDeleted() {
        // given
        Campaign existingCampaign = new Campaign("C-5", "Campaign 5", "Campaign 5 Description");
        campaignRepository.save(existingCampaign);

        // when
        campaignRepository.deleteCampaignsWithoutTasks();

        // then
        Optional<Campaign> retrievedCampaign = campaignRepository.findById(existingCampaign.getId());
        Assertions.assertTrue(retrievedCampaign.isEmpty());
    }

}