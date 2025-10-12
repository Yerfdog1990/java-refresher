package com.baeldung.lhj.persistence.repository;

import com.baeldung.lhj.persistence.model.Campaign;

import java.util.List;
import java.util.Optional;

public interface CampaignRepository {
    Optional<Campaign> findById(Long id);

    Campaign save(Campaign campaign);

    List<Campaign> findAll();
}