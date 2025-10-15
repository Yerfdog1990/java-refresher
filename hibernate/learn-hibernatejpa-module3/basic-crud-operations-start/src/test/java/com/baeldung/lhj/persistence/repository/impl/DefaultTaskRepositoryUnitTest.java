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

class DefaultTaskRepositoryUnitTest {
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
    void givenExistingTask_whenUpdate_thenTaskUpdated() {
        // given
        Campaign campaign = new Campaign("CTASK-3", "Campaign 3", "Campaign 3 Description");
        campaignRepository.save(campaign);

        Task existingTask = new Task("Task 3", "Task 3 Description", LocalDate.now(), campaign, TaskStatus.TO_DO, null);
        taskRepository.save(existingTask);
        Long taskId = existingTask.getId();

        // when
        String updatedName = "Updated Task 3";
        String updatedDescription = "Updated Task 3 Description";
        LocalDate updatedDueDate = LocalDate.now().plusDays(10);
        TaskStatus updatedStatus = TaskStatus.IN_PROGRESS;
        existingTask.setName(updatedName);
        existingTask.setDescription(updatedDescription);
        existingTask.setDueDate(updatedDueDate);
        existingTask.setStatus(updatedStatus);
        taskRepository.update(taskId, existingTask);

        // then
        Task retrievedTask = taskRepository.findById(taskId).get();
        Assertions.assertEquals(updatedName, retrievedTask.getName());
        Assertions.assertEquals(updatedDescription, retrievedTask.getDescription());
        Assertions.assertEquals(updatedDueDate, retrievedTask.getDueDate());
        Assertions.assertEquals(updatedStatus, retrievedTask.getStatus());
    }

    @Test
    void givenExistingTask_whenDeleteById_thenTaskRemoved() {
        // given
        Campaign campaign = new Campaign("CTASK-4", "Campaign 4", "Campaign 4 Description");
        campaignRepository.save(campaign);

        Task existingTask = new Task("Task 4", "Task 4 Description", LocalDate.now(), campaign, TaskStatus.TO_DO, null);
        taskRepository.save(existingTask);
        Long taskId = existingTask.getId();

        // when
        taskRepository.deleteById(taskId);

        // then
        Optional<Task> retrievedTask = taskRepository.findById(taskId);
        Assertions.assertTrue(retrievedTask.isEmpty());
    }

}
