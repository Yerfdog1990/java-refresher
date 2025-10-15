package com.baeldung.lhj.persistence.repository.impl;

import java.util.Optional;

import com.baeldung.lhj.persistence.model.Campaign;
import com.baeldung.lhj.persistence.repository.CampaignRepository;
import com.baeldung.lhj.persistence.util.JpaUtil;

import jakarta.persistence.EntityManager;

public class DefaultCampaignRepository implements CampaignRepository {

    public DefaultCampaignRepository() {
    }

    @Override
    public Optional<Campaign> findById(Long id) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            Campaign retrievedCampaign = entityManager.find(Campaign.class, id);
            return Optional.ofNullable(retrievedCampaign);
        }
    }

    @Override
    public Campaign save(Campaign campaign) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            entityManager.getTransaction().begin();
            entityManager.persist(campaign);
            entityManager.getTransaction().commit();
            return campaign;
        }
    }

    @Override
    public void update(Long id, Campaign campaign) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            entityManager.getTransaction().begin();

            Campaign retrievedCampaign = entityManager.find(Campaign.class, id);
            retrievedCampaign.setName(campaign.getName());
            retrievedCampaign.setDescription(campaign.getDescription());

            entityManager.getTransaction().commit();
        }
    }

    @Override
    public void deleteById(Long id) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            entityManager.getTransaction().begin();

            Campaign retrievedCampaign = entityManager.find(Campaign.class, id);
            entityManager.remove(retrievedCampaign);

            entityManager.getTransaction().commit();
        }
    }

}