package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.persistence.model.Worker;
import com.baeldung.lhj.persistence.repository.WorkerRepository;
import com.baeldung.lhj.persistence.util.JpaUtil;

import jakarta.persistence.EntityManager;

import java.util.Optional;

public class DefaultWorkerRepository implements WorkerRepository {

    public DefaultWorkerRepository() {
    }

    @Override
    public Optional<Worker> findById(Long id) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            Worker retrievedWorker = entityManager.find(Worker.class, id);
            return Optional.ofNullable(retrievedWorker);
        }
    }

    @Override
    public Worker save(Worker worker) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            entityManager.getTransaction().begin();
            entityManager.persist(worker);
            entityManager.getTransaction().commit();
            return worker;
        }
    }
}
