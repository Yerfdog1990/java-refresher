package com.baeldung.lhj.persistence.repository;

import java.util.Optional;

import com.baeldung.lhj.persistence.model.Worker;

public interface WorkerRepository {
    Optional<Worker> findById(Long id);

    Worker save(Worker worker);
}
