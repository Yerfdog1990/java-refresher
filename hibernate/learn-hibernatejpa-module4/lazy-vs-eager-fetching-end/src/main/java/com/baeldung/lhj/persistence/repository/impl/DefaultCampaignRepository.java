package com.baeldung.lhj.persistence.repository.impl;

import java.util.List;
import java.util.Optional;

import com.baeldung.lhj.persistence.model.Campaign;
import com.baeldung.lhj.persistence.repository.CampaignRepository;
import com.baeldung.lhj.persistence.util.JpaUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class DefaultCampaignRepository implements CampaignRepository {

    @Override
    public Optional<Campaign> findById(Long id) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            return Optional.ofNullable(entityManager.find(Campaign.class, id));
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
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Campaign> cq = cb.createQuery(Campaign.class);
            Root<Campaign> rootEntry = cq.from(Campaign.class);
            cq.select(rootEntry);
            TypedQuery<Campaign> allQuery = entityManager.createQuery(cq);
            return allQuery.getResultList();
        }
    }

}
