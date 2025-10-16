package com.baeldung.lhj.persistence.repository.impl;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import com.baeldung.lhj.persistence.model.Task;
import com.baeldung.lhj.persistence.repository.TaskRepository;
import com.baeldung.lhj.persistence.util.JpaUtil;

public class DefaultTaskRepository implements TaskRepository {

    @Override
    public Optional<Task> findById(Long id) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            return Optional.ofNullable(entityManager.find(Task.class, id));
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
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Task> cq = cb.createQuery(Task.class);
            Root<Task> rootEntry = cq.from(Task.class);
            cq.select(rootEntry);
            TypedQuery<Task> allQuery = entityManager.createQuery(cq);
            return allQuery.getResultList();
        }
    }

    @Override
    public List<Task> findByNameContainingAndAssigneeId(String name, Long assigneeId) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            Query query = entityManager.createQuery("FROM Task t WHERE t.name LIKE :name AND t.assignee.id = :assigneeId");
            query.setParameter("name", "%" + name + "%");
            query.setParameter("assigneeId", assigneeId);
            return query.getResultList();
        }
    }
}
