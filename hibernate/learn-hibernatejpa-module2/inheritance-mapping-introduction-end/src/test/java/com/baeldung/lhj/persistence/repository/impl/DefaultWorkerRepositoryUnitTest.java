package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.persistence.model.FreelanceWorker;
import com.baeldung.lhj.persistence.model.FullTimeWorker;
import com.baeldung.lhj.persistence.model.Worker;
import com.baeldung.lhj.persistence.repository.WorkerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class DefaultWorkerRepositoryUnitTest {
    WorkerRepository workerRepository = new DefaultWorkerRepository();

    @Test
    void givenExistingWorker_whenFindById_thenWorkerRetrieved() {
        // given
        Worker existingWorker = new FullTimeWorker("johnTest1@test.com", "John", "Doe", 20000.00);
        workerRepository.save(existingWorker);

        // when
        Worker retrievedWorker = workerRepository.findById(existingWorker.getId()).get();

        // then
        Assertions.assertEquals(existingWorker, retrievedWorker);
    }

    @Test
    void givenExistingWorker_whenFindByNonExistingId_thenNoWorkerRetrieved() {
        // given
        Worker existingWorker = new FreelanceWorker("johnTest2@test.com", "John", "Doe", 30.00);
        workerRepository.save(existingWorker);

        // when
        Optional<Worker> retrievedWorker = workerRepository.findById(99L);

        // then
        Assertions.assertTrue(retrievedWorker.isEmpty());
    }
}
