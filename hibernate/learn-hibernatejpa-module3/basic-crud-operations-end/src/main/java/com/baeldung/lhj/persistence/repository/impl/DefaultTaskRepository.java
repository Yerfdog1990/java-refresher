package com.baeldung.lhj.persistence.repository.impl;

import java.util.Optional;

import com.baeldung.lhj.persistence.model.Task;
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
    public void update(Long id, Task task) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            entityManager.getTransaction().begin();

            Task retrievedTask = entityManager.find(Task.class, id);
            retrievedTask.setName(task.getName());
            retrievedTask.setDescription(task.getDescription());
            retrievedTask.setDueDate(task.getDueDate());
            retrievedTask.setStatus(task.getStatus());

            entityManager.getTransaction().commit();
        }
    }

    @Override
    public void deleteById(Long id) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            entityManager.getTransaction().begin();

            Task retrievedTask = entityManager.find(Task.class, id);
            entityManager.remove(retrievedTask);

            entityManager.getTransaction().commit();
        }
    }

}