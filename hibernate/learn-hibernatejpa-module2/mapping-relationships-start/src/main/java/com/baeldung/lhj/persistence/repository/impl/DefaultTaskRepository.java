package com.baeldung.lhj.persistence.repository.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.baeldung.lhj.persistence.model.Task;
import com.baeldung.lhj.persistence.repository.TaskRepository;

import static java.util.List.copyOf;

public class DefaultTaskRepository implements TaskRepository {

    private Set<Task> tasks;

    public DefaultTaskRepository() {
        this.tasks = new HashSet<>();
    }

    @Override
    public Optional<Task> findById(Long id) {
        return tasks.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst();
    }

    @Override
    public Task save(Task task) {
        Long taskId = task.getId();
        if (taskId == null) {
            task.setId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
        } else {
            findById(taskId).ifPresent(tasks::remove);
        }
        tasks.add(task);
        return task;
    }

    @Override
    public List<Task> findAll() {
        return copyOf(tasks);
    }
}
