package com.baeldung.lhj.persistence.repository.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.baeldung.lhj.persistence.model.Task;
import com.baeldung.lhj.persistence.repository.TaskRepository;

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
    public void update(Long id, Task task) {
        deleteById(id);
        task.setId(id);
        tasks.add(task);
    }

    @Override
    public void deleteById(Long id) {
        Task existingTask = findById(id).orElseThrow(IllegalArgumentException::new);
        tasks.remove(existingTask);
    }

}