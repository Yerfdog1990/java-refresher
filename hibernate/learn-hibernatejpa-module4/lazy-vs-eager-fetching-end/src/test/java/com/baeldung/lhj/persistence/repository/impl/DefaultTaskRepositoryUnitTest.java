package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.extension.CloseResourcesExtension;
import com.baeldung.lhj.persistence.model.Campaign;
import com.baeldung.lhj.persistence.model.Task;
import com.baeldung.lhj.persistence.model.TaskStatus;
import com.baeldung.lhj.persistence.model.Worker;
import com.baeldung.lhj.persistence.repository.CampaignRepository;
import com.baeldung.lhj.persistence.repository.TaskRepository;
import com.baeldung.lhj.persistence.repository.WorkerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(CloseResourcesExtension.class)
class DefaultTaskRepositoryUnitTest {
    CampaignRepository campaignRepository = new DefaultCampaignRepository();
    TaskRepository taskRepository = new DefaultTaskRepository();
    WorkerRepository workerRepository = new DefaultWorkerRepository();

    @Test
    void givenExistingTask_whenFindById_thenTaskRetrieved() {
        // given
        Campaign campaign = new Campaign("CTASK-1", "Campaign 1", "Campaign 1 Description");
        campaignRepository.save(campaign);

        Task existingTask = new Task("Task 1", "Task 1 Description", LocalDate.now(), campaign, TaskStatus.TO_DO, null);
        taskRepository.save(existingTask);

        // when
        Task retrievedTask = taskRepository.findById(existingTask.getId()).get();

        // then
        Assertions.assertEquals(existingTask, retrievedTask);
    }

    @Test
    void givenExistingTask_whenFindByNonExistingId_thenNoTaskRetrieved() {
        // given
        Campaign campaign = new Campaign("CTASK-2", "Campaign 2", "Campaign 2 Description");
        campaignRepository.save(campaign);

        Task existingTask = new Task("Task 2", "Task 2 Description", LocalDate.now(), campaign, TaskStatus.TO_DO, null);
        taskRepository.save(existingTask);

        // when
        Optional<Task> retrievedTask = taskRepository.findById(99L);

        // then
        Assertions.assertTrue(retrievedTask.isEmpty());
    }

    @Test
    void givenExistingTask_whenFindAllTasks_thenNonEmptyListRetrieved() {
        // given
        Campaign campaign = new Campaign("CTASK-3", "Campaign 3", "Campaign 3 Description");
        campaignRepository.save(campaign);

        Task task3 = new Task("Task 3", "Task 3 Description", LocalDate.now(), campaign, TaskStatus.TO_DO, null);
        taskRepository.save(task3);
        Task task4 = new Task("Task 4", "Task 4 Description", LocalDate.now(), campaign, TaskStatus.TO_DO, null);
        taskRepository.save(task4);

        // when
        List<Task> retrievedTasks = taskRepository.findAll();

        // then
        Assertions.assertFalse(retrievedTasks.isEmpty());
    }

    @Test
    void givenExistingTasks_whenFindByNameContainingAndAssigneeId_thenNonEmptyListRetrieved() {
        // given
        Campaign campaign = new Campaign("CTASK-4", "Campaign 4", "Campaign 4 Description");
        campaignRepository.save(campaign);

        Worker worker = new Worker("johnTest3@test.com", "John", "Doe");
        workerRepository.save(worker);

        Task task5 = new Task("Task 5", "Task 5 Description", LocalDate.now(), campaign, TaskStatus.TO_DO, worker);
        taskRepository.save(task5);
        Task task6 = new Task("Task 6", "Task 6 Description", LocalDate.now(), campaign, TaskStatus.TO_DO, null);
        taskRepository.save(task6);

        // when
        List<Task> retrievedTasks = taskRepository.findByNameContainingAndAssigneeId("Task", worker.getId());

        // then
        Assertions.assertEquals(1, retrievedTasks.size());
    }
}
