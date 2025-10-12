package com.baeldung.lhj.persistence.repository;

import com.baeldung.lhj.persistence.model.Worker;

import java.util.Optional;

public interface WorkerRepository {
    Optional<Worker> findById(Long id);

    Worker save(Worker worker);
}