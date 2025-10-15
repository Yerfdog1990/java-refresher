package com.baeldung.lhj.persistence.repository;

import com.baeldung.lhj.persistence.model.Campaign;

import java.util.List;
import java.util.Optional;

public interface CampaignRepository {
    Campaign save(Campaign campaign);

    Optional<Campaign> findById(Long id);

    List<Campaign> findAll();

    List<Campaign> findByNameOrDescriptionContaining(String text);

    int deleteCampaignsWithoutTasks();
}