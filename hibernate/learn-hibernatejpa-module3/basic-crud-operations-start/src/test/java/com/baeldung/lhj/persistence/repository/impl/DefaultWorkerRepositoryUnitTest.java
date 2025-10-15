package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.persistence.model.Worker;
import com.baeldung.lhj.persistence.repository.WorkerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

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

    @Test
    void givenExistingWorker_whenUpdate_thenWorkerUpdated() {
        // given
        Worker existingWorker = new Worker("johnTest3@test.com", "John", "Doe");
        workerRepository.save(existingWorker);
        Long workerId = existingWorker.getId();

        // when
        String updatedFirstName = "Jane";
        String updatedLastName = "Smith";
        existingWorker.setFirstName(updatedFirstName);
        existingWorker.setLastName(updatedLastName);
        workerRepository.update(workerId, existingWorker);

        // then
        Worker retrievedWorker = workerRepository.findById(workerId).get();
        Assertions.assertEquals(updatedFirstName, retrievedWorker.getFirstName());
        Assertions.assertEquals(updatedLastName, retrievedWorker.getLastName());
    }

    @Test
    void givenExistingWorker_whenDeleteById_thenWorkerRemoved() {
        // given
        Worker existingWorker = new Worker("johnTest4@test.com", "John", "Doe");
        workerRepository.save(existingWorker);
        Long workerId = existingWorker.getId();

        // when
        workerRepository.deleteById(workerId);

        // then
        Optional<Worker> retrievedWorker = workerRepository.findById(workerId);
        Assertions.assertTrue(retrievedWorker.isEmpty());
    }

}
