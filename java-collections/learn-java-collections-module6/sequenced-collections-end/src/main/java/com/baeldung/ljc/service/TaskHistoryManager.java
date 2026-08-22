package com.baeldung.ljc.service;

import java.util.SequencedCollection;
import java.util.stream.Collectors;
import com.baeldung.ljc.domain.model.Task;

public class TaskHistoryManager {

    private final SequencedCollection<Task> history;

    public TaskHistoryManager(SequencedCollection<Task> history) {
        this.history = history;
    }

    public void addTask(Task task) {
        history.addLast(task);
    }

    public Task getLastTask() {
        if (history.isEmpty()) return null;
        return history.getLast();
    }

    public Task getFirstTask() {
        if (history.isEmpty()) return null;
        return history.getFirst();
    }

    public Task undoLastTask() {
        if (history.isEmpty()) return null;
        return history.removeLast();
    }

    public SequencedCollection<Task> getRecentHistory() {
        return history.reversed();
    }

    @Override
    public String toString() {
        return history.stream().map(Task::getCode).collect(Collectors.joining(" -> "));
    }
}

