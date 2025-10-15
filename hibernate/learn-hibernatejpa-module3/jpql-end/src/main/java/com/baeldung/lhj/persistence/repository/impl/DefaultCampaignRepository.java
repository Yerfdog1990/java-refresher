package com.baeldung.lhj.persistence.repository.impl;

import java.util.List;
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
    public List<Campaign> findAll() {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            return entityManager
                .createQuery("SELECT c FROM Campaign c", Campaign.class)
                .getResultList();
        }
    }

    @Override
    public Optional<Campaign> findByCodeAndName(String code, String name) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            Campaign result = entityManager
                .createQuery("SELECT c FROM Campaign c WHERE c.code = ?1 AND c.name = ?2", Campaign.class)
                .setParameter(1, code)
                .setParameter(2, name)
                .getSingleResult();
            return Optional.ofNullable(result);
        }
    }

    @Override
    public int deleteCampaignsWithoutTasks() {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            entityManager.getTransaction().begin();
            int deletedCount = entityManager
              .createQuery("DELETE FROM Campaign c WHERE c.tasks IS EMPTY")
              .executeUpdate();
            entityManager.getTransaction().commit();
            return deletedCount;
        }
    }
}