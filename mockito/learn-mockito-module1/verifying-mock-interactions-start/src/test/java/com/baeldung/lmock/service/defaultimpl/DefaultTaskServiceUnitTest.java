package com.baeldung.lmock.service.defaultimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.baeldung.lmock.domain.model.Campaign;
import com.baeldung.lmock.domain.model.Task;
import com.baeldung.lmock.domain.model.TaskStatus;
import com.baeldung.lmock.persistence.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class DefaultTaskServiceUnitTest {

    @Mock
    TaskRepository taskRepository;

    @InjectMocks
    DefaultTaskService taskService;

    /*
    2. Verifying Method Calls
    Verification ensures our logic invokes the methods we expect on a dependency.
    While stubbing focuses on returning data from a mock, verification checks whether certain calls occur. Common use cases include:

    Confirming a side-effect method (such as persisting or deleting data) was invoked
    Ensuring certain calls never happen in error scenarios
    Measuring how many times a method is invoked (once, multiple times, or never)
    In our codebase, services like DefaultTaskService or DefaultWorkerService depend on repository interfaces to store or update domain objects.
    We can easily verify the interactions with the repositories using Mockito.

    For example, if DefaultTaskService is supposed to create, find, or update a Task by invoking the TaskRepository, we can mock the dependency and inject it into the tested component, as we demonstrated in the previous lessons.

    Let’s take a look at the givenExistingTask_whenFindById_thenTaskRetrieved test of the DefaultTaskServiceUnitTest class.
    Here, we can use Mockito.verify(taskRepository).findById(1L) to check that the mock’s findById() method is effectively called with the expected parameter as part of the execution of the tested method.
    Additionally, we can use a static import for org.mockito.Mockito.verify to keep the test short and simple:
     */
    @Test
    void givenExistingTask_whenFindById_thenTaskRetrieved() {
        //given
        Task existingTask = new Task("Task 1", "Task 1 Description", LocalDate.now(), new Campaign("C1-CODE", "Campaign 1", "Campaign 1 Description"),
            TaskStatus.TO_DO, null);
        existingTask.setId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        // when
        Optional<Task> retrievedTask = taskService.findById(1L);

        // then
        assertEquals("Task 1", retrievedTask.get()
            .getName());
        verify(taskRepository).findById(1L);
        /*
        As we can see, verify(taskRepository).findById(1L) confirms that the findById() method was invoked exactly once with the correct parameter.
        If it wasn’t called, or if it was called with a different parameter, then the test fails.
         */
    }

    /*
    3. Argument Matchers
    We are already familiar with argument matchers such as any(), anyLong(), and argThat(). Until now, we have only used them to define stubbing rules, but they can also help us verify mock interactions.

    In our givenNonExistingTaskId_whenFindById_thenEmptyOptionalRetrieved() test, we verify that trying to fetch a nonexistent Task returns an empty Optional.
    We can use verify(taskRepository).findById(anyLong()) to enrich the test with an additional verification.
    Similarly, the argThat() matcher allows us to validate that the argument, such as the task ID, meets a custom predicate or flexible condition:
     */
    @Test
    void givenNonExistingTaskId_whenFindById_thenEmptyOptionalRetrieved() {
        // when
        Optional<Task> retrievedTask = taskService.findById(99L);

        // then
        assertTrue(retrievedTask.isEmpty());
        verify(taskRepository).findById(argThat(id -> id > 50L && id < 100L));
    }

    /*
        Note that when verifying methods with multiple parameters, we can’t mix exact values and argument matchers.

        For example, the searchTask method in TaskService takes two arguments:
        A task name as a string and an assignee ID as a number.
        Let’s create a new test case within the DefaultTaskServiceUnitTest class and verify that
        the taskRepository method findByNameContainingAndAssigneeId is called with the correct task name and an assignee ID greater than 50.

        It might be tempting to verify the mock using the exact task name and a matcher for the assignee ID:

        verify(taskRepository).findByNameContainingAndAssigneeId("name", argThat(id -> id > 50L)); // <-- this will fail!

        However, doing this will cause the test to fail with Mockito’s InvalidUseOfMatcherException.
        Mockito requires us to use either all exact values or all argument matchers when verifying method calls.
        To fix this, we can wrap the name parameter with the eq() matcher so that both arguments use matches:
    */

    @Test
    void whenSearchTasks_thenRepositoryIsInvoked() {
        // when
        List<Task> tasks = taskService.searchTasks("name", 100L);

        // then
        assertTrue(tasks.isEmpty());
        verify(taskRepository)
                .findByNameContainingAndAssigneeId(eq("name"), argThat(id -> id > 50L));
    }

    /*
    4. Verifying Interaction Sequences and Frequencies
    We learned how to verify a single interaction with a mock and check its parameters. However, in real scenarios, we often deal with multiple interactions, sometimes in a specific order.
    Mockito offers features to test such cases effectively.

    4.1. Verifying Frequencies
    By default, Mockito’s verify() method checks that the method is called exactly once. If the method is called more than once, the test will fail.

    In other words, when taskRepository.save() is invoked multiple times, we’ll notice that a simple verification like verify(taskRepository).save(any()) will fail.

    For such cases, we can specify the expected number of invocations using the overloaded Mockito.verify() method with a VerificationMode.
    For the verification mode, we’ll use the static factory method Mockito.times(2). To keep it simple, we can import org.mockito.Mockito.times as a static function.

    Let’s create a new test, whenCreateAndUpdate_thenSaveIsInvokedTwice(), that uses the TaskService to create and then update a Task.
    Additionally, let’s use times(2) to verify the save() method was invoked twice:
     */

    @Test
    void whenCreateAndUpdate_thenRepositoryIsInvokedTwice() {
        //given
        Task task = new Task("Task 1", "Task 1 Description", LocalDate.now(),
                new Campaign("C2-CODE", "Campaign 2", "Campaign 2 Description"), TaskStatus.TO_DO, null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // when
        taskService.create(task);
        task.setId(1L);
        task.setName("Task 1 Updated");
        taskService.updateTask(task.getId(), task);

        // then
        verify(taskRepository, times(2)).save(any());
    }
    /*
    Similarly, we can create VerificationModes instances with other static factory methods, such as atLeast() or never().
    For example, we can use never() to check that save() is not invoked if the Task we’re trying to update does not exist.

    Let’s illustrate this with a new test where we update a non-existent Task instance.
    We’ll create it as a new test in the DefaultTaskServiceUnitTest class and name it whenUpdatingNonExistingTask_thenRepositoryIsNotInvoked():
     */

    @Test
    void whenUpdatingNonExistingTask_thenRepositoryIsNotInvoked() {
        //given
        Task task = new Task("Task 1", "Task 1 Description", LocalDate.now(),
                new Campaign("C2-CODE", "Campaign 2", "Campaign 2 Description"), TaskStatus.TO_DO, null);

        // when
        taskService.updateTask(999L, task);

        // then
        verify(taskRepository, never()).save(any());
    }

    /*
    4.2. Verifying Sequences
    We can use Mockito’s InOrder API to test that multiple calls occur in a specific sequence.
    We’ll create an InOrder instance using the static method Mockito.inOrder() and then perform all the verification via its API.

    For example, for the givenExistingTask_whenUpdateStatus_thenUpdatedTaskSaved() test, we can verify that TaskRepository‘s findById() was invoked first, followed by save():
     */
    @Test
    void givenExistingTask_whenUpdateStatus_thenUpdatedTaskSaved() {
        //given
        Task existingTask = new Task("Task 2", "Task 2 Description", LocalDate.now(), new Campaign("C2-CODE", "Campaign 2", "Campaign 2 Description"),
            TaskStatus.TO_DO, null);
        existingTask.setId(2L);

        // when
        when(taskRepository.findById(2L))
          .thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask))
          .thenReturn(existingTask);

        Optional<Task> retrievedTask = taskService.updateStatus(2L, TaskStatus.IN_PROGRESS);

        // then
        assertEquals(TaskStatus.IN_PROGRESS, retrievedTask.get().getStatus());

        // Create InOrder verifier for the taskRepository
        InOrder inOrder = inOrder(taskRepository);

        // Verify the sequence of method calls
        inOrder.verify(taskRepository).findById(2L);
        inOrder.verify(taskRepository).save(existingTask);
    }

    /*
    This allows us to verify the full sequence of interactions with the taskRepository mock and ensure the invocations occur in the correct order.

    Overall, verifying interactions with mocked dependencies is an essential feature of any mocking framework.
    However, it’s worth taking into account that overusing it can lead to tight coupling and fragile tests.
     */
}
