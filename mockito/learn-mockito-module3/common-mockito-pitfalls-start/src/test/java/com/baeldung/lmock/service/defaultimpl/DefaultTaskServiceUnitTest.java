package com.baeldung.lmock.service.defaultimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

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
import com.baeldung.lmock.service.TaskService;

@ExtendWith(MockitoExtension.class)
class DefaultTaskServiceUnitTest {

    @Mock
    TaskRepository taskRepository;

    @InjectMocks
    DefaultTaskService taskService;

    /*
    2. Mocking Too Much
    It’s common to encounter scenarios where our tests rely heavily on mocks, sometimes leading us into complex test setups.
    Overreliance on mocks can indicate deeper design problems, such as overly complex components with too many dependencies.
    Such setups often result in fragile tests that easily break with minor changes.

    To address this, we can consider breaking down larger components into smaller, more cohesive ones.
    Smaller, well-designed components generally have fewer dependencies, making them easier to test.

    On the other hand, excessive mocking may be a sign that our test scope is too narrow.
    Broadening the scope of our tests can naturally reduce the number of mocks we need, making our tests more robust and reflective of real-world usage.

    Additionally, it’s important to recognize when mocks are unnecessary.
    Common anti-patterns include mocking simple data objects or even the object under test itself.

    In the following sections, we’ll explore practical strategies to identify and eliminate such cases, ultimately leading to cleaner tests with fewer, more meaningful mocks.

    3. Mocking Data Objects
    A common pitfall is mocking data transfer or simple value objects.

    For example, let’s open the DefaultTaskServiceUnitTest class and have a look at the givenATaskWithId5_whenFindById_thenTaskRetrieved test method:
    @Test
    void givenATaskWithId5_whenFindById_thenTaskRetrieved() {
        Task mockedTask = mock(); // anti-pattern

        when(mockedTask.getId()).thenReturn(5L);
        when(mockedTask.getName()).thenReturn("Task 5");
        when(taskRepository.findById(5L)).thenReturn(Optional.of(mockedTask));

        Task retrievedTask = taskService.findById(5L).get();

        assertEquals("Task 5", retrievedTask.getName());
    }
    Copy
    Here, we may be tempted to mock a Task object to verify the correct workings of DefaultTaskService.

    However, mocking the Task instance is unnecessary and reflects a common testing issue.
    Data objects should be created directly rather than mocked, as this keeps tests simpler and more reliable.
    Let’s update the test method givenATaskWithId5_whenFindById_thenTaskRetrieved to fix this issue and create an actual Task object:
     */

    @Test
    void givenATaskWithId5_whenFindById_thenTaskRetrieved() {
        Task actualTask = new Task("Task 5", "Task 5 Description", LocalDate.now(),
                new Campaign("C5-CODE", "Campaign 5", "Campaign 5 Description"), TaskStatus.TO_DO, null);

        when(taskRepository.findById(5L)).thenReturn(Optional.of(actualTask));

        Task retrievedTask = taskService.findById(5L).get();

        assertEquals("Task 5", retrievedTask.getName());
    }

    /*
    4. Mocking the Object Under Test
    Another frequent mistake in testing is mocking the object that we’re trying to test.
    Doing this defeats the purpose of unit tests, as it means we’re no longer testing the actual implementation but a mocked behavior.

    To highlight this, let’s review the test method givenATaskWithId6_whenFindById_thenTaskRetrieved where we verify the TaskService‘s findById(), but in the setup, we mock the very object we’re trying to test:

    @Test
    void givenATaskWithId6_whenFindById_thenTaskRetrieved() {
        TaskService mockedTaskService = mock(); // anti-pattern

        when(mockedTaskService.findById(6L)).thenReturn(Optional.empty());

        Optional<Task> retrievedTask = mockedTaskService.findById(6L);
        assertTrue(retrievedTask.isEmpty());
    }
    Copy
    Needless to say, this is a mistake and should be avoided. Simply put, we should never mock the component we are testing, only the components around it.

    So, let’s refactor the test and use a real TaskService instance, and only mock the underlying TaskRepository:
     */
    @Test
    void givenATaskWithId6_whenFindById_thenTaskRetrieved() {
        TaskRepository mockedRepo = mock();
        TaskService actualService = new DefaultTaskService(mockedRepo);

        when(mockedRepo.findById(6L)).thenReturn(Optional.empty());

        Optional<Task> retrievedTask = actualService.findById(6L);
        assertTrue(retrievedTask.isEmpty());
    }
     /*
    6. Unnecessary Verifications
    Verifying mock interactions is valuable, but excessive or unnecessary verifications can lead to brittle tests.
    We should verify only the essential interactions relevant to our test scenario.

    We can notice this code smell in the givenExistingTask_whenUpdateStatus_thenUpdatedTaskSaved() test:

    @Test
    void givenExistingTask_whenUpdateStatus_thenUpdatedTaskSaved() {

        Task existingTask = new Task("Task 2", "Task 2 Description", LocalDate.now(),
          new Campaign("C2-CODE", "Campaign 2", "Campaign 2 Description"), TaskStatus.TO_DO, null);
        existingTask.setId(2L);

        when(taskRepository.findById(2L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        Optional<Task> retrievedTask = taskService.updateStatus(2L, TaskStatus.IN_PROGRESS);

        verify(taskRepository).findById(2L); // anti-pattern
        assertEquals(TaskStatus.IN_PROGRESS, retrievedTask.get().getStatus());
    }
    Copy
    As we can see, apart from asserting that the retrieved Task was correctly updated, we also verify that taskRepository.findById(2L) was invoked.
    However, this verification is unnecessary. We’re asserting an internal implementation detail that’s irrelevant to the behavior being tested,
    which couples the test too tightly to the code structure and makes it more fragile.

    Let’s remove the extra verification from our test:
     */

    @Test
    void givenExistingTask_whenUpdateStatus_thenUpdatedTaskSaved() {
        Task existingTask = new Task("Task 2", "Task 2 Description", LocalDate.now(),
                new Campaign("C2-CODE", "Campaign 2", "Campaign 2 Description"), TaskStatus.TO_DO, null);
        existingTask.setId(2L);

        when(taskRepository.findById(2L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        Optional<Task> retrievedTask = taskService.updateStatus(2L, TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, retrievedTask.get().getStatus());

        /*
        Verification is best used for side effects (actions that change state or interact with external systems),
        not for simple queries like findById(), especially when their absence would naturally cause the test to fail.
         */
    }

    /*
    7. Nested Mocks
    We’ve already discussed deep mocking in a previous section and noted that it should be used with caution. Nested mocks arise when a mock returns another mock. This often appears when mocking external dependencies too aggressively or when stubbing fluent APIs.

    For example, if we want to stub a Task’s name for a given ID, we can simply mock the taskRepository. However, a common tendency is to create deeply nested mocks, mocking not just the repository, but also the Task object and even the Optional wrapper around it:

    @Test
    void givenATaskWithId7_whenFindById_thenTaskRetrieved() {
        Task mockedTask = mock();
        Optional<Task> optionalMockedTask = mock();
        TaskRepository taskRepository = mock();

        when(taskRepository.findById(7L)).thenReturn(optionalMockedTask); // anti-pattern
        when(optionalMockedTask.get()).thenReturn(mockedTask);
        when(mockedTask.getName()).thenReturn("Task 7");

        // ...
    }
    Copy
    Multiple mocks can be nested together programmatically, like we did in this example, or by using Mockito RETURN_DEEP_STUBS, as demonstrated in a previous lesson.

    While this approach works, it quickly becomes complex. Each additional layer of mocking adds to the setup’s complexity, making the test harder to read and maintain.
    It can also lead to fragile tests that easily break when internal implementation details change.

    We can avoid this anti-pattern and simplify the test by using a single mock for the taskRepository.
    In other words, we’ll use real objects for the Task and the Optional wrapper:
     */

    @Test
    void givenATaskWithId7_whenFindById_thenTaskRetrieved() {
        Task actualTask = new Task("Task 7", "Task 7 Description", LocalDate.now(),
                new Campaign("C7-CODE", "Campaign 7", "Campaign 7 Description"), TaskStatus.TO_DO, null);
        when(taskRepository.findById(7L)).thenReturn(Optional.of(actualTask));

        Task retrievedTask = taskService.findById(7L).get();

        assertEquals("Task 7", retrievedTask.getName());
    }

    /*
    8. Conclusion
    In this lesson, we’ve had a look at several anti-patterns that we should avoid when using mocks in our tests, and what alternatives to opt for instead.

    By being mindful of these common pitfalls, we can write unit tests that are simpler, more reliable, and easier to maintain.
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
}
