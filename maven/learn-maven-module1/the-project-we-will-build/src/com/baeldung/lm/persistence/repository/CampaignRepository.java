package com.baeldung.lm.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baeldung.lm.domain.model.Campaign;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
}
