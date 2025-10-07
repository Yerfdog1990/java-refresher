package com.baeldung.lmock.service.defaultimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.baeldung.lmock.domain.model.Campaign;
import com.baeldung.lmock.domain.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.baeldung.lmock.domain.model.Task;
import com.baeldung.lmock.persistence.repository.TaskRepository;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultTaskServiceUnitTest {

    @Mock
    TaskRepository taskRepository;
    @InjectMocks
    DefaultTaskService taskService;

    @Test
    void givenStubbedFindById_whenCalledMultipleTimes_thenSameResults() {
        // when
        Optional<Task> result1 = taskService.findById(2L);
        Optional<Task> result2 = taskService.findById(2L);
        Optional<Task> result3 = taskService.findById(2L);

        // then
        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
        assertTrue(result3.isEmpty());
    }

    /*
    4. Handling Multiple and Consecutive Stubbing
    Sometimes, we want the same method to behave differently across multiple calls. For instance, the first call might return a specific object, and the next one might return something else, such as a different object, null, or even throw an exception.

    NB: This approach helps simulate dynamic behavior or test how the service reacts to different data over repeated calls.
    Let’s see this in action by opening the DefaultTaskServiceUnitTest class.
    Since we don’t have a service method that invokes a repository method multiple times in a row, we’ll simulate that behavior directly in a new test, calling the mock successively to observe the different outcomes:
     */
    @Test
    void givenStubbedFindById_whenCalledMultipleTimes_thenDifferentResults() {
        Campaign campaign = new Campaign();
        Task firstTask = new Task("First", "desc", LocalDate.now(), campaign, TaskStatus.TO_DO, null);
        firstTask.setId(100L);
        Task secondTask = new Task("Second", "desc2", LocalDate.now(), campaign, TaskStatus.TO_DO, null);
        secondTask.setId(200L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(firstTask))
                .thenReturn(Optional.of(secondTask))
                .thenReturn(Optional.empty());

        Optional<Task> result1 = taskService.findById(1L);
        Optional<Task> result2 = taskService.findById(1L);
        Optional<Task> result3 = taskService.findById(1L);

        assertTrue(result1.isPresent());
        assertEquals(100L, result1.get().getId());
        assertTrue(result2.isPresent());
        assertEquals(200L, result2.get().getId());
        assertTrue(result3.isEmpty());
    }

    /*
    5. Using Argument Matchers
    Mockito argument matchers allow us to stub or verify method calls with flexible rather than exact arguments.
    They’re especially useful when we don’t know the precise value of an argument at test time or when we want tests to be more readable and less brittle.

    For example, consider a test that verifies the searchTasks method in DefaultTaskService.
    If we don’t care about the specific string or ID used in the search, only that the service calls the repository’s findByNameContainingAndAssigneeId(), we can use matchers when stubbing:
     */
    @Test
    void givenAnyStringAndAnyLong_whenSearchTasks_thenReturnsEmptyList() {
        when(taskRepository.findByNameContainingAndAssigneeId(anyString(), anyLong()))
                .thenReturn(Collections.emptyList());

        String searchName = "test";
        Long assigneeId = 1L;
        List<Task> actualTasks = taskService.searchTasks(searchName, assigneeId);

        assertNotNull(actualTasks, "The result should not be null.");
        assertTrue(actualTasks.isEmpty(), "The returned list should be empty.");
    }
    /*
    Here we stub the repository call, being flexible about the arguments passed by using anyString() and anyLong() for each corresponding parameter.
    Then, we call the service method, capture its return value, and perform assertions as usual.

    5.1. Common Argument Matchers
    All argument matcher methods are provided by the org.mockito.ArgumentMatchers class.
    Here are some of the most commonly used ones:

    Argument Matcher -> Description
    any() ->	Matches anything, including nulls and varargs.
    any(Class type), isA(Class type) -> Matches any object of the given type (including subtypes), excluding nulls. The difference between the two is purely semantic.
    anyString(), anyInt(), anyLong(), anyDouble(), anyBoolean() -> Match any non-null value of the corresponding primitive wrapper type.
    anyList(), anySet(), anyMap() -> Match any non-null collection of the respective type.
    eq(boolean value), eq(byte value), eq(char value), eq(double value), eq(float value), eq(int value), eq(long value), eq(short value), eq(T value)	Argument of the given type that is equal to the given value.
    argThat(ArgumentMatcher matcher) -> Allows creating custom argument matchers.
    isNull() -> Matches a null argument.
    isNotNull() -> Matches any non-null argument.
    contains(String substring) -> Matches a string that contains the given substring.
    startsWith(String prefix) -> Matches a string that starts with the given prefix.
    endsWith(String suffix) -> Matches a string that ends with the given suffix.
     */

    /*
    5.2. Mixing Flexible and Exact Stubbing
    It’s important to note that we cannot mix raw values and matchers in the same stubbing.
    Doing so will cause an InvalidUseOfMatchersException.
    For instance, let’s test searchTasks, using any() for the first argument and a raw literal value for the second:
     */
    @Test
    void givenAnySearchAndExactAssigneeId_whenSearchTasks_thenRepositoryIsCalled() {
       // when(taskRepository.findByNameContainingAndAssigneeId(any(), 1L))
               // .thenReturn(Collections.emptyList()); // -> Running this test would throw an InvalidUseOfMatchersException at the stubbing step.Running this test would throw an InvalidUseOfMatchersException at the stubbing step.

        when(taskRepository.findByNameContainingAndAssigneeId(any(), eq(1L)))
                .thenReturn(Collections.emptyList()); // -> To fix it, we use the eq() matcher for the second argument:

        String searchName = "test";
        Long assigneeId = 1L;
        List<Task> result = taskService.searchTasks(searchName, assigneeId);

        assertNotNull(result, "The result should not be null.");
        assertTrue(result.isEmpty());
    }
}
