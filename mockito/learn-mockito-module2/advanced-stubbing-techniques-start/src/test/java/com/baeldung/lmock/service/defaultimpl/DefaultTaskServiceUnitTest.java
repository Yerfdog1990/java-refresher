package com.baeldung.lmock.service.defaultimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import com.baeldung.lmock.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    2. Stubbing With Callbacks and Answers
    So far, we’ve used thenReturn() or thenThrow() to control how a mock responds.
    However, for scenarios where the returned value depends on arguments or runtime conditions, Mockito provides the Answer interface.

    2.1. What Are Answers?
    An Answer<T> can inspect an invocation’s arguments and produce a dynamic result.
    This approach is helpful for advanced or complex logic during stubbing.

    For example, when we call taskRepository.save(), the Task is not only saved but also gets an ID assigned.
    However, we can’t fully replicate this behavior using mocks and thenReturn(); we would have to return a different instance or set the ID beforehand.

    Let’s create a new test in the DefaultTaskServiceUnitTest class for the discussed use case.
    After that, we’ll leverage the thenAnswer() method and the Answer API to finish implementing our test:

    2.2. Using thenAnswer()
    As we can see, we can use Answer objects by calling thenAnswer() instead of thenReturn().
    One key difference is that thenAnswer() accepts an Answer instance as a parameter.
    Since Answer is a functional interface, we can pass a lambda expression as an argument to thenAnswer().

    The single abstract method in Answer takes an InvocationOnMock object and returns the stubbed type, which, in our case, is a Task.
    The InvocationOnMock API enables us to access the mock, the stubbed method, and the arguments passed during the invocation.
    In our case, we can use getArgument(0) to extract the method’s single argument – the Task instance we want to save:
     */

    @Test
    void whenSavingTask_thenIdIsAssigned() {
        // given
        when(taskRepository.save(any()))
                .thenAnswer(invocation -> {
                    Task taskArg = invocation.getArgument(0);
                    taskArg.setId(999L);
                    return taskArg;
                });
        Task task = aTask("Test Task");

        // when
        Task savedTask = taskService.create(task);

        // then
        assertEquals(999L, savedTask.getId());
    }

    private static Task aTask(String taskName) {
        return new Task(
                taskName,
                "Test Description",
                LocalDate.now(),
                new Campaign("TEST-CODE", "Test Campaign", "Test Campaign Description"),
                TaskStatus.TO_DO,
                null
        );
    }
    /*
    Sometimes, we want a mock’s response to depend on argument values or other conditions.
    For instance, let’s pretend we want the taskRepository to return specific tasks when the ID equals 1L or 2L, and Optional.empty() otherwise.
    Until now, we handled this by chaining calls or using argument matches:
        when(taskRepository.findById(1L))
          .thenReturn(Optional.of(aTask("Task 1")));
        when(taskRepository.findById(2L))
          .thenReturn(Optional.of(aTask("Task 2")));
        when(taskRepository.findById(argThat(id -> id != 1L && id != 2L)))
          .thenReturn(Optional.empty());

    Of course, argThat is a powerful tool for matching specific arguments, but handling multiple conditions often means writing separate, repetitive stubbing.
    In contrast, the Answer API lets us express the same logic in a single, compact, and more readable block.
    Let’s add a new test case using this approach:
     */

    @Test
    void givenAnswerApiStubbing_whenFindById_thenReturnCorrectTask() {
        when(taskRepository.findById(any())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return switch (id.intValue()) {
                case 1 -> Optional.of(aTask("Task 1"));
                case 2 -> Optional.of(aTask("Task 2"));
                default -> Optional.empty();
            };
        });

        assertTrue(taskService.findById(1L).isPresent());
        assertTrue(taskService.findById(2L).isPresent());
        assertTrue(taskService.findById(3L).isEmpty());
    }

    /*
    4. Stubbing Void Methods
    We need a different API for stubbing void methods in Mockito because when(…).thenReturn(…) only works with methods that return a value.
    So, we can use doAnswer() instead, which allows us to cause side effects while not returning anything.

    Let’s look at the TaskService’s deleteTask() method. It takes a task ID and deletes the task using the repository.
    The TaskRepository’s delete() method removes the task from the in-memory storage and doesn’t return anything.
    For demonstration purposes, it also updates the Task instance, setting its ID to null after deletion, in case there is still a reference to this object.

    Since delete() is a void method that causes side effects, we may want to replicate those effects in our tests.
    However, because it returns nothing, we can’t use thenReturn() or thenAnswer() like in the previous examples.

    For void methods, the syntax is slightly different.
    We’ll define the side effect first, like throwing an exception or modifying state, then specify the mock and method call, similar to the doNothing() or doReturn() approach.

    In this case, we’ll start with doAnswer(), followed by the stubbed method invocation:
     */

    @Test
    void whenDeleteTask_thenRemoveTaskId() {
        // given
        Task task = aTask("Test Task");
        task.setId(1L);

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        doAnswer(invocation -> {
            Task taskArg = invocation.getArgument(0);
            taskArg.setId(null);
            return null;
        }).when(taskRepository).delete(task);

        // when
        taskService.deleteTask(task.getId());

        // then
        assertNull(task.getId());

        /*
        As we can see, inside doAnswer(), we capture the Task passed as an argument to the delete() method and remove its ID.
        This will allow us to simulate the behaviour of the real component and see how our application reacts.
         */
    }

    /*
    5. Deep Stubbing
    Deep stubbing allows us to stub a chain of method calls without creating separate mocks for each level.
    To use deep stubs, we need to enable this feature when we instantiate the mocked object.

    Let’s create a new test and a TaskRepository supporting deep stubs. Then, we’ll inject our mock into a new DefaultTaskService instance:

    @Test
    void givenDeepStubbing_whenGetAssigneeFullName_thenUsedDeepStubName() {
        //given
        TaskRepository taskRepository = mock(TaskRepository.class, Mockito.RETURNS_DEEP_STUBS);
        TaskService taskService = new DefaultTaskService(taskRepository);

        // ...
    }

    Now, let’s look at the getAssigneeFullName() method in TaskService.
    For a given Task ID, it returns its assignee’s name as a String.
    First, it retrieves the Task object using getById().
    Then, it gets the associated Worker assignee and combines their firstName with their lastName in uppercase.

    We can use deep stubs to stub this whole sequence of method calls:
     */

    @Test
    void givenDeepStubbing_whenGetAssigneeFullName_thenUsedDeepStubName() {
        //given
        TaskRepository taskRepository = mock(TaskRepository.class, Mockito.RETURNS_DEEP_STUBS);
        TaskService taskService = new DefaultTaskService(taskRepository);

        when(taskRepository.getById(1L)
                .getAssignee()
                .getFirstName()
        ).thenReturn("John");

        when(taskRepository.getById(1L)
                .getAssignee()
                .getLastName()
        ).thenReturn("Doe");

        // when
        String fullName = taskService.getAssigneeFullName(1L);

        // then
        assertEquals("John DOE", fullName);

        /*
        This example is for demonstration purposes and not necessarily a best practice.
        Deep stubbing can sometimes hide design issues since it leads to very fragile tests that fail if the method chain changes.

        It’s also best to avoid adding complex logic inside tests.
        Instead, we should focus on making the code more testable and use these advanced techniques only as a last resort.
         */
    }
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
        Mockito.verify(taskRepository)
            .findById(1L);
        assertEquals("Task 1", retrievedTask.get()
            .getName());
    }

    @Test
    void givenNonExistingTaskId_whenFindById_thenEmptyOptionalRetrieved() {
        // when
        Optional<Task> retrievedTask = taskService.findById(99L);

        // then
        assertTrue(retrievedTask.isEmpty());
        Mockito.verify(taskRepository)
            .findById(99L);
    }

    @Test
    void givenExistingTask_whenUpdateStatus_thenUpdatedTaskSaved() {
        //given
        Task existingTask = new Task("Task 2", "Task 2 Description", LocalDate.now(), new Campaign("C2-CODE", "Campaign 2", "Campaign 2 Description"),
            TaskStatus.TO_DO, null);
        existingTask.setId(2L);

        // when
        when(taskRepository.findById(2L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        Optional<Task> retrievedTask = taskService.updateStatus(2L, TaskStatus.IN_PROGRESS);

        // then
        Mockito.verify(taskRepository)
            .findById(2L);
        assertEquals(TaskStatus.IN_PROGRESS, retrievedTask.get()
            .getStatus());
    }

}
