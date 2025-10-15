package com.baeldung.lhj.persistence.repository.impl;

import java.util.List;
import java.util.Optional;

import com.baeldung.lhj.persistence.model.Task;
import com.baeldung.lhj.persistence.model.TaskStatus;
import com.baeldung.lhj.persistence.repository.TaskRepository;
import com.baeldung.lhj.persistence.util.JpaUtil;

import jakarta.persistence.EntityManager;

public class DefaultTaskRepository implements TaskRepository {

    public DefaultTaskRepository() {
    }

    @Override
    public Optional<Task> findById(Long id) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            Task retrievedTask = entityManager.find(Task.class, id);
            return Optional.ofNullable(retrievedTask);
        }
    }

    @Override
    public Task save(Task task) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            entityManager.getTransaction().begin();
            entityManager.persist(task);
            entityManager.getTransaction().commit();
            return task;
        }
    }

    @Override
    public List<Task> findAll() {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            return entityManager
                .createQuery("SELECT t FROM Task t", Task.class)
                .getResultList();
        }
    }

    @Override
    public List<Task> findByStatuses(List<TaskStatus> statuses) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            return entityManager
                .createQuery("SELECT t FROM Task t WHERE t.status IN (:statuses)", Task.class)
                .setParameter("statuses", statuses)
                .getResultList();
        }
    }

    @Override
    public List<Task> findByWorkerEmail(String email) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            return entityManager
                .createQuery("SELECT t FROM Task t WHERE t.assignee.email =: email", Task.class)
                .setParameter("email", email)
                .getResultList();
        }
    }

    @Override
    public int holdTasksByCampaignId(Long campaignId) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            entityManager.getTransaction().begin();
            int updatedCount = entityManager
                .createQuery("UPDATE Task t SET t.status = :onHoldStatus WHERE t.campaign.id = :campaignId")
                .setParameter("onHoldStatus", TaskStatus.ON_HOLD)
                .setParameter("campaignId", campaignId)
                .executeUpdate();
            entityManager.getTransaction().commit();
            return updatedCount;
        }
    }

}