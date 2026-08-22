package com.baeldung.ljs;

import com.baeldung.ljs.domain.model.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JavaStreamsUnitTest {
    @Test
    void givenAListOfTasks_whenFilteringByPriority_thenOnlyHighPriorityTasksAreReturned() {
        List<Task> tasks = List.of(
            new Task("T001", "Year 10 Physics", "Physical quantities and measurement", LocalDate.of(2024, 6, 15)),
            new Task("T002", "Year 10 Chemistry", "Chemical reactions", LocalDate.of(2024, 6, 16)),
            new Task("T003", "Year 10 Biology", "Cell structure", LocalDate.of(2024, 6, 17))
        );
        Predicate<Task> isHighPriority = task -> task.getDueDate().isBefore(LocalDate.of(2024, 6, 16));
        List<Task> highPriorityTasks = tasks.stream()
            .filter(isHighPriority)
            .toList();
        IO.println("High priority tasks: " + highPriorityTasks);
        assertEquals(1, highPriorityTasks.size());
    }
}