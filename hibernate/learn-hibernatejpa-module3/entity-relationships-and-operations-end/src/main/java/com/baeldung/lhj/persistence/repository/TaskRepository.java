package com.baeldung.lhj.persistence.repository;

import com.baeldung.lhj.persistence.model.Task;

import java.util.Optional;

public interface TaskRepository {
    Optional<Task> findById(Long id);

    Task save(Task task);
}
