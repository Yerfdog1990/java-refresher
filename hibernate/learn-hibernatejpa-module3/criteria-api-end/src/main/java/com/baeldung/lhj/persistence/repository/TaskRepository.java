package com.baeldung.lhj.persistence.repository;

import com.baeldung.lhj.persistence.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task save(Task task);

    Optional<Task> findById(Long id);

    List<Task> findAll();

    List<Task> findAndOrderByFields(String filterField, Object filterValue, String sortField, boolean sortAscending);

    List<Task> findByWorkerEmailImplicitJoin(String email);

    List<Task> findByWorkerEmailExplicitJoin(String email);

    int holdTasksByCampaignId(Long campaignId);
}