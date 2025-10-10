package com.baeldung.lmock.service.defaultimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import com.baeldung.lmock.persistence.repository.inmemory.InMemoryTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
    2.1. Capturing Arguments Using verify()
    Let’s consider the following method from the DefaultTaskService class:

    public Task create(Task task) {
        if (task.getId() != null) {
            throw new IllegalArgumentException("Can't create Task with assigned 'id'");
        }
        task.setStatus(TaskStatus.TO_DO);
        task.setAssignee(null);
        return taskRepository.save(task);
    }

    The create() method takes a Task object as an argument, performs some validation and modifications, and then saves it using taskRepository.save(task).
    To test this method, we may want to verify that the taskRepository.save() method is called with the correct Task object, ensuring the argument passed to it meets specific conditions (e.g., status is set to TaskStatus.TO_DO and assignee is null).

    Let’s see how we can use verify() to check whether the mocked object’s save() method was called with the expected arguments. We’ll add the following test to the DefaultTaskServiceUnitTest class:
     */

    @Test
    void givenNewTask_whenCreateCalled_thenTaskIsSavedWithExpectedValues() {
        // given
        Task inputTask = new Task();
        inputTask.setId(null);
        inputTask.setName("Test Task");

        Mockito.when(taskRepository.save(any(Task.class))).thenReturn(inputTask);

        // when
        Task result = taskService.create(inputTask);

        // then
        verify(taskRepository).save(Mockito.argThat(task ->
                task.getId() == null &&
                        task.getStatus() == TaskStatus.TO_DO &&
                        task.getAssignee() == null &&
                        task.getName().equals("Test Task")

                /*
                In this test, we’re using Mockito.argThat to define a custom matcher for the Task object.
                The matcher checks that the inputTask argument has the expected values:
                ID remains null as per the input, the status is set by the create() method, the assignee is set to null by the create() method, and the name is unchanged from the input.
                This approach ensures the create() method behaves correctly by checking the arguments passed to the mocked taskRepository.save().

                Let’s suppose we set inputTask.setName(“Test”); the test fails with the error:
                “Argument(s) are different! Wanted:”.Mockito’s verify() doesn’t show the actual vs. expected arguments by default, unlike assertion libraries like JUnit/AssertJ.
                 */
        ));
    }

        /*
        2.2. Capturing Arguments Using ArgumentCaptor
        ArgumentCaptor is a Mockito feature that allows us to capture the arguments passed to a mocked method for detailed inspection after the method call.
        This is particularly useful when we want to verify multiple properties of the argument or perform assertions that are too complex for an inline argThat matcher.

        Now, let’s rewrite the test using ArgumentCaptor to capture and inspect the Task object passed to the taskRepository.save():
         */
    @Test
    void givenNewTask_whenCreateCalled_thenTaskIsSavedWithArgumentCaptor() {
        // given
        Task inputTask = new Task();
        inputTask.setId(null);
        inputTask.setName("Test Task");

        Mockito.when(taskRepository.save(any(Task.class))).thenReturn(inputTask);

        // when
        Task result = taskService.create(inputTask);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());

        // then
        Task capturedTask = taskCaptor.getValue();
        assertNull(capturedTask.getId(), "Task ID should be null");
        assertEquals(TaskStatus.TO_DO, capturedTask.getStatus(), "Task status should be TO_DO");
        assertNull(capturedTask.getAssignee(), "Task assignee should be null");
        assertEquals("Test Task", capturedTask.getName(), "Task name should match input");

        /*
        The ArgumentCaptor.forClass(Task.class) creates a captor for the Task object.
        The verify(taskRepository).save(taskCaptor.capture()) captures the Task object passed to save during the create() method execution.
        And finally, the taskCaptor.getValue() retrieves the captured Task object.

        Now, we can perform multiple assertions on capturedTask (e.g., checking id, status, assignee, and name).
        This approach is more readable than inline argThat matchers, especially for complex objects.
         */
    }

    /*
    3. Static Methods
    Static methods, like utility methods, provide shared functionality in codebases and may need to be mocked in some scenarios.

    Let’s add a static method called validateTask(Task task) to the Task class:

    public class Task {
        // ...

        public static void validateTask(Task task) {
            if (task.getName() == null || task.getName().isEmpty()) {
                throw new IllegalArgumentException("Task name cannot be null or empty");
            }
        }
    }

    And then call this from the createTask() method before saving:

    public class DefaultTaskService implements TaskService {

        public Task createTask(Task task) {
            if (task.getId() != null) {
                throw new IllegalArgumentException("Can't create Task with assigned 'id'");
            }
            Task.validateTask(task);
            task.setStatus(TaskStatus.TO_DO);
            task.setAssignee(null);
            return taskRepository.save(task);
        }
    }

    When writing unit tests for createTask(), we need to test its logic without executing the real validateTask() logic, especially if it has side effects or other dependencies.
    This is where mocking static methods can be useful.

    Let’s add a unit test for createTask(), mocking the static Task.validateTask() method:
     */

    @Test
    void givenMockedStatic_whenCalled_thenSuccess() {
        try (MockedStatic<Task> validatorMock = Mockito.mockStatic(Task.class)) {
            // given
            validatorMock.when(() -> Task.validateTask(any(Task.class))).thenAnswer(invocation -> null);

            // when
            Task taskWithEmptyName = new Task();

            // then
            assertDoesNotThrow(() -> Task.validateTask(taskWithEmptyName));

            validatorMock.verify(() -> Task.validateTask(taskWithEmptyName));
        }
        /*
        The method Mockito.mockStatic(Task.class) creates a mock for all static methods of the Task class.
        The try block ensures the mock is closed after the test to prevent interference with other tests.

        Next, we configure the static Task.validateTask() method to do nothing (since it’s a void method).
        This overrides the real validation logic, allowing the test to focus on the main logic.

        Then, the test verifies that invoking the method on a test Task instance no longer throws exceptions.
        Also, it confirms the mock was properly invoked using verify(), ensuring the static method was called as expected.
         */
    }

    /*
    4. Final Classes and Methods
    In unit testing, we may need to mock a final class or method when working with APIs that belong to third-party libraries or when we aim to test our code in isolation.

    In previous versions of Mockito, this required using an additional library or configuration.

    However, starting with Mockito 5, we can mock final classes and methods without any issues, and the process is identical to non-final APIs.

    5. Constructors
    Mocking a constructor is necessary for unit testing when we want to isolate the behavior of a class under test by controlling the creation of its dependencies.
    Constructors often initialize objects with specific dependencies or configurations, and mocking them allows us to bypass real object creation, which may involve complex logic, external resources, or other side effects.

    Let’s create unit tests for mocking DefaultTaskService constructors:
     */
    @Test
    void givenMockedRepository_whenCreateConstructor_thenSuccessConstructorWithRepository() {
        // given
        TaskRepository mockRepository = Mockito.mock(TaskRepository.class);

        // when
        DefaultTaskService service = new DefaultTaskService(mockRepository);

        // then
        assertSame(mockRepository, service.getTaskRepository());
    }

    /*
    The test checks that the DefaultTaskService properly initializes when given a TaskRepository through constructor injection.
    It creates a mock repository, passes it to the constructor, and asserts that the service correctly stores and exposes this dependency through the getTaskRepository() method.
    This validates the explicit dependency injection path.

    Now, let’s examine the service’s behavior when using its no-arg constructor, which should internally create an InMemoryTaskRepository:
     */

    @Test
    void givenMockedConstructor_whenCreateDefaultConstructor_thenSuccess() {
        // given
        try (MockedConstruction<InMemoryTaskRepository> mocked = mockConstruction(InMemoryTaskRepository.class)) {
            // when
            DefaultTaskService service = new DefaultTaskService();

            // then
            assertEquals(1, mocked.constructed().size());

            TaskRepository mockRepository = mocked.constructed().get(0);
            assertSame(mockRepository, service.getTaskRepository());

            /*
            Using Mockito’s MockedConstruction API, the test intercepts and mocks the repository instantiation, and then verifies that exactly one repository instance was created and properly assigned to the service.
            We’re also using a try-with-resources block here to ensure proper cleanup.
             */
        }
    }

    /*
    6. Private Methods
    Mockito doesn’t support testing private methods.
    In simple terms, this is because unit tests are meant to verify the public API of a class, and not its implementation details.

    If we need to test a private method, this is usually indicative of a design issue.

    However, if we need to do this in rare cases, there are some workarounds available.
    Link: https://www.baeldung.com/java-unit-test-private-methods#how-to-test-private-methods-in-java
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
        verify(taskRepository)
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
        verify(taskRepository)
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
        verify(taskRepository)
            .findById(2L);
        assertEquals(TaskStatus.IN_PROGRESS, retrievedTask.get()
            .getStatus());
    }

}