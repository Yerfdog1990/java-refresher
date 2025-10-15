package com.baeldung.lhj.persistence.repository;

import com.baeldung.lhj.persistence.model.Worker;

import java.util.List;
import java.util.Optional;

public interface WorkerRepository {
    Worker save(Worker worker);

    Optional<Worker> findById(Long id);

    List<Worker> findWorkersWithActiveTasks();

    List<Worker> findAllOrderByFirstName();

}