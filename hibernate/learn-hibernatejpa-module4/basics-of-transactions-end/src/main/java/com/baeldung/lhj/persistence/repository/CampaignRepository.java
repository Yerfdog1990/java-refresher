package com.baeldung.lhj.persistence.repository;

import java.util.List;
import java.util.Optional;

import com.baeldung.lhj.persistence.model.Campaign;
import com.baeldung.lhj.persistence.model.Task;

public interface CampaignRepository {
    Optional<Campaign> findById(Long id);

    Campaign save(Campaign campaign);

    List<Campaign> findAll();

    void createCampaignWithTasks(Campaign campaign, List<Task> tasks);

    void createCampaignWithTasksSimplified(Campaign campaign, List<Task> tasks);
}
