package com.baeldung.lsd.persistence.repository;

import com.baeldung.lsd.persistence.projection.WorkerOpen;
import org.springframework.data.repository.CrudRepository;

import com.baeldung.lsd.persistence.model.Worker;

import java.util.List;

public interface WorkerRepository extends CrudRepository<Worker, Long> {
    // Open Projections
    List<WorkerOpen> findByFirstName(String firstName);
}
