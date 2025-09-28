package com.baeldung.lm.service;

import java.util.List;
import java.util.Optional;

import com.baeldung.lm.domain.model.Campaign;

public interface CampaignService {

    List<Campaign> findCampaigns();

    Optional<Campaign> findById(Long id);

    Campaign save(Campaign campaign);

    Optional<Campaign> updateCampaign(Long id, Campaign campaign);
}
