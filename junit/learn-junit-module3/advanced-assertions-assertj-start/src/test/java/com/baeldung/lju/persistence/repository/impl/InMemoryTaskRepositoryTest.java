package com.baeldung.lju.persistence.repository.impl;

import com.baeldung.lju.CustomDisplayNameGenerator;
import com.baeldung.lju.domain.model.Campaign;
import com.baeldung.lju.domain.model.Task;
import com.baeldung.lju.domain.model.TaskStatus;
import com.baeldung.lju.domain.model.Worker;
import com.baeldung.lju.persistence.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;

import static java.time.LocalDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class InMemoryTaskRepositoryTest {
    @Mock
    Set<Task> mockedTasks;
    TaskRepository taskRepository;

    @BeforeEach
    public void setUpRepository() {
        taskRepository = new InMemoryTaskRepository(mockedTasks);
    }
    @Test
    void givenSomeTasks_whenFindByNameContainingAndAssigneeId_thenReturnFilteredTasksUsingHamcrest() {
        // given
        Worker assignee = new Worker("jd@gmail.com", "John", "Doe");
        assignee.setId(100L);

        var task1 = new Task("BACKEND-1001", "description 1", now(), new Campaign(), TaskStatus.TO_DO, assignee);
        var task2 = new Task("BACKEND-1002", "description 2", now(), new Campaign(), TaskStatus.TO_DO, assignee);
        var task3 = new Task("FRONTEND-1003", "description 3", now(), new Campaign(), TaskStatus.TO_DO, assignee);

        // and
        TaskRepository repo = new InMemoryTaskRepository();
        repo.save(task1);
        repo.save(task2);
        repo.save(task3);

        // When
        List<Task> tasks = repo.findByNameContainingAndAssigneeId("BACKEND", 100L);

        // Then
        assertThat(tasks, hasSize(2)); // the repository fetched exactly two tasks

        assertThat(tasks, containsInAnyOrder(task1, task2)); // the returned tasks are equal to task1 and task2, regardless of their order,

        // Both tasks are assigned to the worker with an ID equal to 100,
        // None of the tasks contains “FRONTEND” in their name
        assertThat(tasks, everyItem(allOf(
                hasProperty("assignee", hasProperty("id", is(100L))),
                hasProperty("name", startsWith("BACKEND")),
                not(hasProperty("name", containsString("FRONTEND")))
        )));
    }
    @Test
    void givenSomeTasks_whenFindByNameContainingAndAssigneeId_thenReturnFilteredTasksUsingAssertJ() {
        // given
        Worker assignee = new Worker("jd@gmail.com", "John", "Doe");
        assignee.setId(100L);

        var task1 = new Task("BACKEND-1001", "description 1", now(), new Campaign(), TaskStatus.TO_DO, assignee);
        var task2 = new Task("BACKEND-1002", "description 2", now(), new Campaign(), TaskStatus.TO_DO, assignee);
        var task3 = new Task("FRONTEND-1003", "description 3", now(), new Campaign(), TaskStatus.TO_DO, assignee);

        // and
        TaskRepository repo = new InMemoryTaskRepository();
        repo.save(task1);
        repo.save(task2);
        repo.save(task3);

        // When
        List<Task> tasks = repo.findByNameContainingAndAssigneeId("BACKEND", 100L);

        // Then
        assertThat(tasks)
                .hasSize(2)
                .allMatch(task -> task.getAssignee().getId() == 100L)
                .noneMatch(task -> task.getName().contains("FRONTEND"))
                .containsExactlyInAnyOrder(task1, task2);
    }
}
