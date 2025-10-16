package com.baeldung.lhj.persistence.repository.impl;

import java.util.Optional;

import jakarta.persistence.EntityManager;

import com.baeldung.lhj.persistence.model.Worker;
import com.baeldung.lhj.persistence.repository.WorkerRepository;
import com.baeldung.lhj.persistence.util.JpaUtil;

public class DefaultWorkerRepository implements WorkerRepository {

    @Override
    public Optional<Worker> findById(Long id) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            return Optional.ofNullable(entityManager.find(Worker.class, id));
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
