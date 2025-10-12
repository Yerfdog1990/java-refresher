package com.baeldung.lhj.persistence.repository;

import com.baeldung.lhj.persistence.model.WorkerPerformance;

import java.util.Optional;

public interface WorkerPerformanceRepository {
    Optional<WorkerPerformance> findById(Long id);

    WorkerPerformance save(WorkerPerformance workerPerformance);
}
