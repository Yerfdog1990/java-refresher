package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.persistence.model.WorkerPerformance;
import com.baeldung.lhj.persistence.repository.WorkerPerformanceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class DefaultWorkerPerformanceRepositoryUnitTest {
    WorkerPerformanceRepository workerPerformanceRepository = new DefaultWorkerPerformanceRepository();

    @Test
    void givenExistingWorkerPerformance_whenFindById_thenWorkerPerformanceRetrieved() {
        // given
        WorkerPerformance existingWorkerPerformance = new WorkerPerformance();
        existingWorkerPerformance.setRating(8);
        existingWorkerPerformance.setTasksCompleted(40);
        existingWorkerPerformance.setCompletionRate(92.5);
        workerPerformanceRepository.save(existingWorkerPerformance);

        // when
        WorkerPerformance retrievedWorkerPerformance = workerPerformanceRepository.findById(existingWorkerPerformance.getId()).get();

        // then
        Assertions.assertEquals(existingWorkerPerformance, retrievedWorkerPerformance);
    }

    @Test
    void givenExistingWorkerPerformance_whenFindByNonExistingId_thenNoWorkerPerformanceRetrieved() {
        // given
        WorkerPerformance existingWorkerPerformance = new WorkerPerformance();
        existingWorkerPerformance.setRating(8);
        existingWorkerPerformance.setTasksCompleted(40);
        existingWorkerPerformance.setCompletionRate(92.5);
        workerPerformanceRepository.save(existingWorkerPerformance);

        // when
        Optional<WorkerPerformance> retrievedPerformance = workerPerformanceRepository.findById(99L);

        // then
        Assertions.assertTrue(retrievedPerformance.isEmpty());
    }
}