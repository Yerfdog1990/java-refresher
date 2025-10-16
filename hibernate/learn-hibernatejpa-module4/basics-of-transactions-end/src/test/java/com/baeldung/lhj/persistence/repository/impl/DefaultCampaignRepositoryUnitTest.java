package com.baeldung.lhj.persistence.repository.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.baeldung.lhj.persistence.model.Task;
import com.baeldung.lhj.persistence.model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.baeldung.lhj.extension.CloseResourcesExtension;
import com.baeldung.lhj.persistence.model.Campaign;
import com.baeldung.lhj.persistence.repository.CampaignRepository;

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
    void givenExistingCampaign_whenFindAllCampaigns_thenNonEmptyListRetrieved() {
        // given
        Campaign campaign1 = new Campaign("C-3", "Campaign 3", "Campaign 3 Description");
        campaignRepository.save(campaign1);
        Campaign campaign2 = new Campaign("C-4", "Campaign 4", "Campaign 4 Description");
        campaignRepository.save(campaign2);

        // when
        List<Campaign> retrievedCampaigns = campaignRepository.findAll();

        // then
        Assertions.assertFalse(retrievedCampaigns.isEmpty());
    }

    @Test
    void givenCampaignAndTasks_whenCreateCampaignWithTasks_thenCampaignAndTasksPersisted() {
        // given
        Campaign campaign = new Campaign("C-5", "Campaign 5", "Campaign 5 Description");
        List<Task> tasks = List.of(
            new Task("Task 1", "Task 1 Description", LocalDate.now(), null, TaskStatus.TO_DO, null),
            new Task("Task 2", "Task 2 Description", LocalDate.now(), null, TaskStatus.TO_DO, null),
            new Task("Task 3", "Task 3 Description", LocalDate.now(), null, TaskStatus.TO_DO, null)
        );

        // when
        campaignRepository.createCampaignWithTasks(campaign, tasks);

        // then
        Optional<Campaign> retrievedCampaign = campaignRepository.findById(campaign.getId());
        Assertions.assertTrue(retrievedCampaign.isPresent());
        Assertions.assertEquals(tasks.size(), retrievedCampaign.get().getTasks().size());
        tasks.forEach(
            task -> Assertions.assertEquals(campaign, task.getCampaign())
        );
    }

}
