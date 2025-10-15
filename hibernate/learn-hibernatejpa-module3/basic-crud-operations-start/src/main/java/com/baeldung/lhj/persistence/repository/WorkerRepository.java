package com.baeldung.lhj.persistence.repository;

import com.baeldung.lhj.persistence.model.Worker;

import java.util.Optional;

public interface WorkerRepository {
    Worker save(Worker worker);

    Optional<Worker> findById(Long id);

    void update(Long id, Worker worker);

    void deleteById(Long id);
}