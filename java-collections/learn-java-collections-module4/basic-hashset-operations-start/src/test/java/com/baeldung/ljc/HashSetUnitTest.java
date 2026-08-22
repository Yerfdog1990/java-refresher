package com.baeldung.ljc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;

import com.baeldung.ljc.domain.model.Task;

class HashSetUnitTest {

    private final Task task01 = new Task("T01", "Another Task 01", "Another Task with code T01", null);
    private final Task task02 = new Task("T02", "Another Task 02", "Another Task with code T02", null);
    private final Task task03 = new Task("T03", "Another Task 03", "Another Task with code T03", null);
    private final Task task04 = new Task("T04", "Another Task 04", "Another Task with code T04", null);

    private final Task task05 = new Task("T05", "Task 05", "This is Task E", null);
    private final Task task06 = new Task("T06", "Task 06", "This is Task F", null);
    private final Task task07 = new Task("T07", "Task 07", "This is Task G", null);

    private Set<Task> tasks;

    @BeforeEach
    void resetTasks() {
        tasks = new HashSet<>(List.of(
            // @formatter:off
            new Task("T01", "Task 01", "This is Task A", null),
            new Task("T02", "Task 02", "This is Task B", null),
            new Task("T03", "Task 03", "This is Task C", null),
            new Task("T04", "Task 04", "This is Task D", null)
            // @formatter:on
        ));
    }
}