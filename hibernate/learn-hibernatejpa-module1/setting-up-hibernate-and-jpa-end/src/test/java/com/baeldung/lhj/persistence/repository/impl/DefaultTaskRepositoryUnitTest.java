package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.persistence.model.Campaign;
import com.baeldung.lhj.persistence.model.Task;
import com.baeldung.lhj.persistence.model.TaskStatus;
import com.baeldung.lhj.persistence.repository.CampaignRepository;
import com.baeldung.lhj.persistence.repository.TaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DefaultTaskRepositoryUnitTest {
    CampaignRepository campaignRepository = new DefaultCampaignRepository();
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

}
