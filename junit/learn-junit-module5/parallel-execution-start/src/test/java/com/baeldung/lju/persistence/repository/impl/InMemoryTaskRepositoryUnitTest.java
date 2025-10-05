package com.baeldung.lju.persistence.repository.impl;

import com.baeldung.lju.domain.model.Campaign;
import com.baeldung.lju.domain.model.Task;
import com.baeldung.lju.domain.model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.parallel.ResourceLock;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;



class InMemoryTaskRepositoryUnitTest {
    static final InMemoryTaskRepository taskRepository = new InMemoryTaskRepository(new HashSet<>());
    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        System.out.printf("[%s] %s%n", Thread.currentThread()
                .getName(), testInfo.getDisplayName());
    }

    @AfterEach
    void afterEach(TestInfo testInfo) {
        System.out.printf("[%s] FINISHED %s%n", Thread.currentThread()
                .getName(), testInfo.getDisplayName());
    }

    /*
    5. Race Conditions in Tests
    One of the main challenges when executing tests concurrently is dealing with race conditions.
    If multiple tests attempt to modify the same shared resource simultaneously, they can interfere with each other, causing failures and leading to flaky tests.
    We can protect against race conditions using JUnit 5’s lock mechanism, via the @ResourceLock annotation.

    5.1. Reproducing the Issue
    Let’s start by creating a test class prone to race condition errors.
    For example, we can write a class to assert that saving, updating, and retrieving a Task object work as expected.
    This time, the tests will share a common InMemoryTaskRepository instance – stored in a static field:
     */
    @Test
    @ResourceLock("task-repository")
    void givenExistingTask_whenUpdatingItsName_thenTaskCanBeFoundByTheNewName() {
        // given
        Task originalTask = aTask("task-1");
        taskRepository.save(originalTask);

        // when
        originalTask.setName("updated-task-1");
        taskRepository.save(originalTask);

        // then
        List<Task> tasks = taskRepository.findByNameContainingAndAssigneeId("updated-task-1", null);
        assertEquals(1, tasks.size());
    }

    @Test
    @ResourceLock("task-repository")
    void givenExistingTask_whenUpdatingItsDescription_thenTaskWasUpdated() {

        // given
        Task originalTask = aTask("task-2");
        taskRepository.save(originalTask);

        // when
        originalTask.setDescription("updated description");
        taskRepository.save(originalTask);

        // then
        Task updatedTask = taskRepository.findById(originalTask.getId()).get();
        assertEquals("updated description", updatedTask.getDescription());
    }

    static Task aTask(String name) {
        return new Task(name, "description", LocalDate.now(), new Campaign(), TaskStatus.TO_DO, null);
    }
    /*
    Now, let’s run the test with parallelism enabled and the execution mode set to concurrent. At first, everything might seem fine.

    However, after re-running the test multiple times, we may observe occasional failures caused by a ConcurrentModificationException.
    This occurs when both tests attempt to update the shared taskRepository’s HashMap concurrently:

    java.util.ConcurrentModificationException
        at java.base/java.util.HashMap$KeySpliterator.tryAdvance(HashMap.java:1738)
        [ ... ]
        at java.base/java.util.stream.ReferencePipeline.findFirst(ReferencePipeline.java:647)
        at com.baeldung.lju.persistence.repository.impl.InMemoryTaskRepository.findById(InMemoryTaskRepository.java:34)
        at com.baeldung.lju.persistence.repository.impl.InMemoryTaskRepositoryUnitTest.givenExistingTask_whenUpdatingItsDescription_thenTaskWasUpdated(InMemoryTaskRepositoryUnitTest.java:63)

    Ideally, we should keep our unit tests isolated and avoid sharing state between them, thereby preventing race conditions.
     */

    /*
    5.2. Using Resource Locks
    To better understand the current behavior, let’s add the @BeforeEach block that prints to the console each test before executing.
    Additionally, let’s add the equivalent method for the @AfterEach annotation:

    As a result, we’ll see that our tests are starting and finishing concurrently, independent from each other:

    [ForkJoinPool-1-worker-3] givenExistingTask_whenUpdatingItsName_thenTaskCanBeFoundByTheNewName()
    [ForkJoinPool-1-worker-2] givenExistingTask_whenUpdatingItsDescription_thenTaskWasUpdated()
    [ForkJoinPool-1-worker-3] FINISHED givenExistingTask_whenUpdatingItsName_thenTaskCanBeFoundByTheNewName()
    [ForkJoinPool-1-worker-2] FINISHED givenExistingTask_whenUpdatingItsDescription_thenTaskWasUpdated()

    To avoid this race condition, we simply need to annotate the competing tests with @ResourceLock and provide the same value for the annotation:

    @Test
    @ResourceLock("task-repository")
    void givenExistingTask_whenUpdatingItsName_thenTaskCanBeFoundByTheNewName() {
        // ...
    }

    @Test
    @ResourceLock("task-repository")
    void givenExistingTask_whenUpdatingItsDescription_thenTaskWasUpdated() {
        // ...
    }

    This simple change ensures that the two annotated tests run sequentially while allowing the others to run concurrently.
    In other words, one annotated test will start only after the other finishes, even though they are executed on different threads:

    [ForkJoinPool-1-worker-2] givenExistingTask_whenUpdatingItsDescription_thenTaskWasUpdated()
    [ForkJoinPool-1-worker-2] FINISHED givenExistingTask_whenUpdatingItsDescription_thenTaskWasUpdated()
    [ForkJoinPool-1-worker-3] givenExistingTask_whenUpdatingItsName_thenTaskCanBeFoundByTheNewName()
    [ForkJoinPool-1-worker-3] FINISHED givenExistingTask_whenUpdatingItsName_thenTaskCanBeFoundByTheNewName()
    Copy
    Needless to say, running the competing tests sequentially prevents race conditions.
    As a result, the test class can be re-run multiple times without failures.
     */
}
