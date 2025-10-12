package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.persistence.model.WorkerPerformance;
import com.baeldung.lhj.persistence.repository.WorkerPerformanceRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class DefaultWorkerPerformanceRepository implements WorkerPerformanceRepository {
    private Set<WorkerPerformance> workerPerformances;

    public DefaultWorkerPerformanceRepository() {
        this.workerPerformances = new HashSet<>();
    }

    @Override
    public Optional<WorkerPerformance> findById(Long id) {
        return workerPerformances.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst();
    }

    @Override
    public WorkerPerformance save(WorkerPerformance workerPerformance) {
        Long id = workerPerformance.getId();
        if (id == null) {
            workerPerformance.setId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
        } else {
            findById(id).ifPresent(workerPerformances::remove);
        }
        workerPerformances.add(workerPerformance);
        return workerPerformance;
    }
}