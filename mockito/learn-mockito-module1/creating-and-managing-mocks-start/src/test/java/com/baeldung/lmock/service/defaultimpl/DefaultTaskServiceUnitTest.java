package com.baeldung.lmock.service.defaultimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.baeldung.lmock.domain.model.Campaign;
import com.baeldung.lmock.domain.model.Task;
import com.baeldung.lmock.domain.model.TaskStatus;
import com.baeldung.lmock.persistence.repository.TaskRepository;
import com.baeldung.lmock.persistence.repository.inmemory.InMemoryTaskRepository;
import org.mockito.Mockito;

class DefaultTaskServiceUnitTest {

    TaskRepository taskRepository;
    DefaultTaskService taskService;

    @BeforeEach
    void setupDataSource() {
        //given
//        Task existingTask = new Task("Task 1", "Task 1 Description", LocalDate.now(), new Campaign("C1-CODE", "Campaign 1", "Campaign 1 Description"),
//            TaskStatus.TO_DO, null);
//        existingTask.setId(1L);
//        taskRepository = new InMemoryTaskRepository(new HashSet<>(List.of(existingTask)));
//
//        taskService = new DefaultTaskService(taskRepository);

        taskRepository = Mockito.mock(TaskRepository.class);
        taskService = new DefaultTaskService(taskRepository);

        /*
        Creating a Mock Object
        We can simplify our tests by replacing the in-memory repository with a mock version of TaskRepository.
        This mock will behave exactly as we define it, without requiring any real database operations or implementation overhead:

        We just instructed Mockito to generate a mock version of TaskRepository. The mock object behaves like a real object, but doesn’t execute any real logic.

        Internally, when we create a mock with Mockito, it uses ByteBuddy to generate a subclass of the class or interface we want to mock.
        This class has a generated name and overrides methods to provide mock behavior.
        When we debug the mock in the IDE, we can see a class name like TaskRepository$MockitoMock$5, confirming it’s not a real implementation, but a dynamically generated test double.
         */
    }

    @Test
    void givenExistingTask_whenFindById_thenTaskRetrieved() {
        Task existingTask = new Task("Task 1", "Task 1 Description", LocalDate.now(),
                new Campaign("C1-CODE", "Campaign 1", "Campaign 1 Description"),
                TaskStatus.TO_DO, null);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        Optional<Task> retrievedTask = taskService.findById(1L);

        assertEquals("Task 1", retrievedTask.get().getName());
    }
    /*
    Note: here we’re using a static import for the org.mockito.Mockito.when method.

    Notably, we call the service as usual, with minimal setup. All we care about regarding the repository dependency is how it responds to the interaction with our test subject.
    In this case, we tell it to return a specific object when findById(1L) is called. This is called stubbing, which we’ll talk about more in a future lesson.

    This approach is beneficial because:
        -We remove the need for a real database or instantiating an actual implementation
        -We can easily control the behavior
        -Our test becomes fast, more reliable, and focused only on the service logic
    In other words, by using a mock, we keep our tests lightweight and decoupled from external intricacies.
     */
}
