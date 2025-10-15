package com.baeldung.lhj.persistence.repository.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.baeldung.lhj.persistence.model.Campaign;
import com.baeldung.lhj.persistence.repository.CampaignRepository;

public class DefaultCampaignRepository implements CampaignRepository {

    private Set<Campaign> campaigns;

    public DefaultCampaignRepository() {
        this.campaigns = new HashSet<>();
    }

    @Override
    public Optional<Campaign> findById(Long id) {
        return campaigns.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst();
    }

    @Override
    public Campaign save(Campaign campaign) {
        Long campaignId = campaign.getId();
        if (campaignId == null) {
            campaign.setId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
        } else {
            findById(campaignId).ifPresent(campaigns::remove);
        }
        campaigns.add(campaign);
        return campaign;
    }

    @Override
    public void update(Long id, Campaign campaign) {
        deleteById(id);
        campaign.setId(id);
        campaigns.add(campaign);
    }

    @Override
    public void deleteById(Long id) {
        Campaign existingCampaign = findById(id).orElseThrow(IllegalArgumentException::new);
        campaigns.remove(existingCampaign);
    }

}