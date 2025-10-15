package com.baeldung.lhj.persistence.repository;

import com.baeldung.lhj.persistence.model.Task;

import java.util.Optional;

public interface TaskRepository {
    Task save(Task task);

    Optional<Task> findById(Long id);

    void update(Long id, Task task);

    void deleteById(Long id);
}