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
    WorkerRepository workerRepository = new DefaultWorkerRepository();
    TaskRepository taskRepository = new DefaultTaskRepository();

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
    void givenExistingTasks_whenFindByAll_thenAllTasksRetrieved() {
        // given
        Campaign campaign = new Campaign("CTASK-3", "Campaign 3", "Campaign 3 Description");
        campaignRepository.save(campaign);

        int numberOfTasks = 10;
        for (int i = 0; i < numberOfTasks; i++) {
            Task task = new Task("Task 00%d".formatted(i), "Task 00%d Description".formatted(i), LocalDate.now(), campaign, TaskStatus.ON_HOLD, null);
            taskRepository.save(task);
        }

        // when
        List<Task> retrievedTasks = taskRepository.findAll();

        // then
        Assertions.assertTrue(retrievedTasks.size() >= numberOfTasks);
    }

    @Test
    void givenExistingTasks_whenFindAndOrderByFields_thenCorrectlyFilteredAndSortedTasksRetrieved() {
        // given
        Campaign campaign = new Campaign("CTASK-4", "Campaign 4", "Campaign 4 Description");
        campaignRepository.save(campaign);

        Task alphaTask = new Task("Alpha Task", "Alpha Task Description", LocalDate.now(), campaign, TaskStatus.DONE, null);
        Task betaTask = new Task("Beta Task", "Beta Task Description", LocalDate.now(), campaign, TaskStatus.IN_PROGRESS, null);
        Task deltaTask = new Task("Delta Task", "Delta Task Description", LocalDate.now(), campaign, TaskStatus.TO_DO, null);
        taskRepository.save(alphaTask);
        taskRepository.save(betaTask);
        taskRepository.save(deltaTask);

        // when
        List<Task> retrievedTasks = taskRepository.findAndOrderByFields("campaign", campaign, "name", true);

        // then
        List<Task> expectedTasks = List.of(alphaTask, betaTask, deltaTask);
        Assertions.assertEquals(expectedTasks.size(), retrievedTasks.size());
        Assertions.assertTrue(retrievedTasks.containsAll(expectedTasks));
        Assertions.assertTrue(retrievedTasks.indexOf(alphaTask) < retrievedTasks.indexOf(betaTask));
        Assertions.assertTrue(retrievedTasks.indexOf(betaTask) < retrievedTasks.indexOf(deltaTask));
    }

    @Test
    void givenTasksAssignedToWorker_whenFindByWorkerEmail_thenCorrectTasksRetrieved() {
        // given
        Campaign campaign = new Campaign("CTASK-5", "Campaign 5", "Campaign 5 Description");
        campaignRepository.save(campaign);

        String email = "alex.smith@baeldung.com";
        Worker worker = new Worker(email, "Alex", "Smith");
        workerRepository.save(worker);

        Task task1 = new Task("Task 3", "Task 3 Description", LocalDate.now(), campaign, TaskStatus.TO_DO, worker);
        Task task2 = new Task("Task 4", "Task 4 Description", LocalDate.now(), campaign, TaskStatus.IN_PROGRESS, worker);
        taskRepository.save(task1);
        taskRepository.save(task2);

        // when
        List<Task> retrievedTasksExplicitJoin = taskRepository.findByWorkerEmailExplicitJoin(email);
        List<Task> retrievedTasksImplicitJoin = taskRepository.findByWorkerEmailImplicitJoin(email);

        // then
        Assertions.assertEquals(retrievedTasksExplicitJoin, retrievedTasksImplicitJoin);
        Assertions.assertEquals(2, retrievedTasksExplicitJoin.size());
        Assertions.assertTrue(retrievedTasksExplicitJoin.contains(task1));
        Assertions.assertTrue(retrievedTasksExplicitJoin.contains(task2));
    }

    @Test
    void givenTasksInCampaign_whenHoldTasksByCompaignId_thenAllTasksUpdated() {
        // given
        Campaign campaign = new Campaign("CTASK-6", "Campaign 6", "Campaign 6 Description");
        campaignRepository.save(campaign);

        Task task1 = new Task("Task 5", "Task 5 Description", LocalDate.now(), campaign, TaskStatus.TO_DO, null);
        Task task2 = new Task("Task 6", "Task 6 Description", LocalDate.now(), campaign, TaskStatus.IN_PROGRESS, null);
        taskRepository.save(task1);
        taskRepository.save(task2);

        // when
        int updatedCount = taskRepository.holdTasksByCampaignId(campaign.getId());

        // then
        Assertions.assertEquals(2, updatedCount);
        Task retrievedTask1 = taskRepository.findById(task1.getId()).get();
        Task retrievedTask2 = taskRepository.findById(task2.getId()).get();
        Assertions.assertEquals(TaskStatus.ON_HOLD, retrievedTask1.getStatus());
        Assertions.assertEquals(TaskStatus.ON_HOLD, retrievedTask2.getStatus());
    }

}