package com.baeldung.lju.service.impl;

import com.baeldung.lju.CustomDisplayNameGenerator;
import com.baeldung.lju.domain.model.Task;
import com.baeldung.lju.persistence.repository.TaskRepository;
import com.baeldung.lju.persistence.repository.WorkerRepository;
import com.baeldung.lju.service.TaskService;
import com.baeldung.lju.service.WorkerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class DefaultTaskServiceUnitTest {
    @Mock
    TaskRepository mockedRepository;

    TaskService taskService;

    @BeforeEach
    void setMockedRepository() {
        taskService = new DefaultTaskService(mockedRepository);
    }

    /*
    2. Negative Testing
    In contrast with testing the “Happy Path,” where everything goes according to plan, and the system operates under ideal conditions,
    “Negative Testing” (also known as “Unhappy/Sad Path” cases) involves testing how the system behaves under failure or unexpected conditions, including handling exceptions.

    Testing both paths is crucial for robust software development.
    With Happy Path, we can assert that the application does what it’s supposed to do.
    Negative testing allows us to verify that the application can gracefully handle errors and exceptions without crashing or behaving unpredictably.

    3. When Something Fails
    We’ll be testing the task management system application. More precisely, we’ll try to create a Task:

    public class Task {

        private Long id;

        private String name;

        // ...
    }

    Tasks have several different properties, but most importantly for us, they have the “id” field. That field identifies the Task in the data source and is assigned automatically when saving a new Task.
    That means we can’t specify the ID upfront, and consequently, our DefaultTaskService class will straightforwardly throw an exception if this scenario is met:

    public Task create(Task task) {
        if (task.getId() != null) {
            throw new IllegalArgumentException("Can't create Task with assigned 'id'");
        }
        task.setStatus(TaskStatus.TO_DO);
        task.setAssignee(null);
        return taskRepository.save(task);
    }
    Copy
    Let’s see how we would proceed to test this method properly.

    4. Asserting the Exception
    We want to create a test that will try to create a new task with an ID assigned.
    Let’s create a new com.baeldung.lju.service.impl.DefaultWorkerServiceTest class and write our test as we would do normally:

    class DefaultWorkerServiceTest {

        TaskService taskService = new DefaultTaskService(new InMemoryTaskRepository());

        @Test
        void givenId_whenCreateTask_shouldThrowAnException() {
            // given
            Task newTask = new Task();
            newTask.setId(1L);
            newTask.setName("Important task");

            // when
            taskService.create(newTask); // this won't work! It will throw an exception
        }
    }

    But in this case, this approach won’t help. If the create method throws an exception JUnit will catch it and mark the test as failed.
    To avoid it we could wrap it with the try/catch structure, run assertions in the catch clause, and throw an exception in the then clause.
    However, it would be tedious and rather hard to read.

    Fortunately, we can encapsulate the invocation of the create method in a special JUnit’s method: assertThrows.
    It takes the exception we expect to get as the first argument and the function that should throw an exception as the second argument:

    @Test
    void givenId_whenCreateTask_shouldThrowAnException() {
        // given
        Task newTask = new Task();
        newTask.setId(1L);
        newTask.setName("Important task");

        // when & then
        assertThrows(IllegalArgumentException.class, () -> taskService.create(newTask));
    }

    It will assert the exception of the given class was thrown and fail otherwise. Additionally, it will return the thrown exception, and we can use it to assert some other things, like the message:

    // when
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.create(newTask));

    // then
    assertEquals(exception.getMessage(), "Can't create Task with assigned 'id'");
    Copy
    Let’s run the tests to check everything is working as expected:
     */
    @Test
    void givenId_whenCreateTask_shouldThrowAnException() {
        // given
        Task newTask = new Task();
        newTask.setId(1L);
        newTask.setName("Important task");

        // when
        //taskService.create(newTask); // this won't work! It will throw an exception

        // when & then
        assertThrows(IllegalArgumentException.class, () -> taskService.create(newTask));
    }

    @Test
    void givenId_whenCreateTask_shouldThrowAnExceptionWithMessage() {
        // given
        Task newTask = new Task();
        newTask.setId(1L);
        newTask.setName("Important task");

        // when
        //taskService.create(newTask); // this won't work! It will throw an exception

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.create(newTask));

        // then
        assertEquals("Can't create Task with assigned 'id'", exception.getMessage());
    }
}
