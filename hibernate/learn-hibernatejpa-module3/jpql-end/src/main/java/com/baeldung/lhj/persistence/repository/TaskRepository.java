package com.baeldung.lhj.persistence.repository;

import com.baeldung.lhj.persistence.model.Task;
import com.baeldung.lhj.persistence.model.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task save(Task task);

    Optional<Task> findById(Long id);

    List<Task> findAll();

    List<Task> findByStatuses(List<TaskStatus> statuses);

    List<Task> findByWorkerEmail(String email);

    int holdTasksByCampaignId(Long campaignId);
}