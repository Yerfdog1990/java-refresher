package com.baeldung.lhj.persistence.repository;

import com.baeldung.lhj.persistence.model.Campaign;

import java.util.Optional;

public interface CampaignRepository {
    Campaign save(Campaign campaign);

    Optional<Campaign> findById(Long id);

    void update(Long id, Campaign campaign);

    void deleteById(Long id);
}