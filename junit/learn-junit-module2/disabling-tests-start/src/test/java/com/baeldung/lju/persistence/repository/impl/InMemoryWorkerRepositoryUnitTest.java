package com.baeldung.lju.persistence.repository.impl;

import com.baeldung.lju.domain.model.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryWorkerRepositoryUnitTest {
    InMemoryWorkerRepository workerRepository;

    @BeforeEach
    void setupDataSource() {
        Worker existingWorker = new Worker("worker1@test.com", "Worker 1 Name", "Worker 1 Lastname");
        existingWorker.setId(1L);
        workerRepository = new InMemoryWorkerRepository(new HashSet<>(Arrays.asList(existingWorker)));
    }

    // For the build process not to be affected by the failing test, we can decorate our method with the @Disabled annotation:

    @Test
    @Disabled("Disabled temporarily until feature is developed.")
    void givenExistingWorker1_whenCreateWorker2_thenWorkerCreateFollowingIdSequence() {
        Worker worker2 = new Worker("worker2@test.com", "Worker 2 Name", "Worker 2 Lastname");

        // when
        Worker createdWorker = workerRepository.save(worker2);

        // then
        assertThat(createdWorker).extracting(Worker::getId)
                .isEqualTo(2L);
    }
}
