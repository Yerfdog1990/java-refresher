package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.persistence.model.Worker;
import com.baeldung.lhj.persistence.repository.WorkerRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class DefaultWorkerRepository implements WorkerRepository {

    private Set<Worker> workers;

    public DefaultWorkerRepository() {
        this.workers = new HashSet<>();
    }

    @Override
    public Optional<Worker> findById(Long id) {
        return workers.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst();
    }

    @Override
    public Worker save(Worker worker) {
        Long workerId = worker.getId();
        if (workerId == null) {
            worker.setId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
        } else {
            findById(workerId).ifPresent(workers::remove);
        }
        workers.add(worker);
        return worker;
    }
}