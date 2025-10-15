package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.extension.CloseResourcesExtension;
import com.baeldung.lhj.persistence.model.Worker;
import com.baeldung.lhj.persistence.repository.WorkerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

@ExtendWith(CloseResourcesExtension.class)
class DefaultWorkerRepositoryUnitTest {
    WorkerRepository workerRepository = new DefaultWorkerRepository();

    @Test
    void givenExistingWorker_whenFindById_thenWorkerRetrieved() {
        // given
        Worker existingWorker = new Worker("johnTest1@test.com", "John", "Doe");
        workerRepository.save(existingWorker);

        // when
        Worker retrievedWorker = workerRepository.findById(existingWorker.getId()).get();

        // then
        Assertions.assertEquals(existingWorker, retrievedWorker);
    }

    @Test
    void givenExistingWorker_whenFindByNonExistingId_thenNoWorkerRetrieved() {
        // given
        Worker existingWorker = new Worker("johnTest2@test.com", "John", "Doe");
        workerRepository.save(existingWorker);

        // when
        Optional<Worker> retrievedWorker = workerRepository.findById(99L);

        // then
        Assertions.assertTrue(retrievedWorker.isEmpty());
    }
}