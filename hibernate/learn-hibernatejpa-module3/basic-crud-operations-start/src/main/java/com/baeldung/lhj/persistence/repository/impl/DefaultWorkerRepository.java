package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.persistence.model.Worker;
import com.baeldung.lhj.persistence.repository.JpaUtil;
import com.baeldung.lhj.persistence.repository.WorkerRepository;
import jakarta.persistence.EntityManager;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class DefaultWorkerRepository implements WorkerRepository {

    // Creating a Worker Record
    @Override
    public Worker save(Worker worker) {
        try(EntityManager entityManager = JpaUtil.getEntityManager()) {
            entityManager.getTransaction().begin();
            entityManager.persist(worker);
            entityManager.getTransaction().commit();
            return worker;
        }
    }

    // Fetching a Worker Record
    @Override
    public Optional<Worker> findById(Long id) {
        try(EntityManager entityManager = JpaUtil.getEntityManager()) {
            Worker retrievedWorker = entityManager.find(Worker.class, id);
            return Optional.ofNullable(retrievedWorker);
        }
    }

    // Updating a Worker Record
    @Override
    public void update(Long id, Worker worker) {
        try(EntityManager entityManager = JpaUtil.getEntityManager()) {
            entityManager.getTransaction().begin();
            Worker retrievedWorker = entityManager.find(Worker.class, id);
            retrievedWorker.setFirstName(worker.getFirstName());
            retrievedWorker.setLastName(worker.getLastName());
            // entityManager.merge(retrievedWorker); -> We don’t have to call any update, merge, or persist method explicitly for this case since JPA automatically detects the changes made to the Worker instance and synchronizes them with the database.
            entityManager.getTransaction().commit();
        }
    }

    // Deleting a Worker Record
    @Override
    public void deleteById(Long id) {
        try(EntityManager entityManager = JpaUtil.getEntityManager()) {
            Worker retrievedWorker = entityManager.find(Worker.class, id);
            entityManager.getTransaction().begin();
            entityManager.remove(retrievedWorker);
            entityManager.getTransaction().commit();
        }
    }

}