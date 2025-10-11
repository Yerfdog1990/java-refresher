package com.baeldung.lmock.service.defaultimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import com.baeldung.lmock.domain.model.Worker;
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
    1. Overview
    When developing software, a common challenge is the communication gap between business stakeholders and technical teams. These misunderstandings about requirements often lead to wasted time and effort.

    Behavior-Driven Development (BDD), a software development process, addresses this limitation and encourages collaboration between developers and business stakeholders. Using BDD, we specify the application behavior up front in a simple, human-readable language that everyone involved in the domain can understand.

    In this lesson, we’ll introduce the BDD approach and explore how to write BDD-style tests using Mockito.

    The relevant module we need to import when starting this lesson is: bdd-with-mockito-start.

    If we want to reference the fully implemented lesson, we can import: bdd-with-mockito-end.
    Link to BDD resource: https://www.baeldung.com/cs/bdd-guide
    Link to TDD resource: https://www.baeldung.com/cs/unit-testing-vs-tdd#tdd
    Link to DDD resource: https://martinfowler.com/bliki/DomainDrivenDesign.html

    2. What Is Behavior-Driven Development?
    The BDD approach evolved from Test-Driven Development (TDD) and was also influenced by Domain-Driven Design (DDD).
    While both TDD and BDD emphasize writing tests before code, BDD adds a stronger focus on collaboration and on using ubiquitous language —
    clear, shared terminology that reflects the domain — by describing the behavior of software from the user’s perspective first, in natural language,
    so that both technical and non-technical team members can align on requirements.

    A core concept in BDD is the Given-When-Then (GWT) structure, which provides a formal way to describe acceptance criteria for a user story, describing the:

    Given: initial preconditions, context, state, or behavior, that the expected outcome depends on
    When: specific action or event that is performed to trigger the expected outcome
    Then: expected outcome, the state change or side effect that happens as a result
    It’s worth noting that while the GWT format isn’t strictly required for BDD, it is widely used for expressing test scenarios.
    Mockito’s BDD-style API also follows this format, as we’ll see in the upcoming sections.

    Link to user stories: https://www.baeldung.com/cs/requirements-functional-vs-non-functional#2-user-stories

    3. Exploring a User Story
    For our demonstration, let’s imagine that, in the context of our task-management system, the product management team has created the following user story for a new functionality:

    User Story: Task Completion

    As a project manager
    I want to mark tasks as complete
    So that I can maintain accurate project status

    Acceptance Criteria:

    1. GIVEN an IN_PROGRESS task with an assignee
       WHEN I complete the task
       THEN the task status changes to DONE
         AND the task's completion time is recorded

    Here, the acceptance criteria are written using the GWT format, clearly describing the expected behavior in natural language while minimizing technical jargon. This makes it easy for both developers and project managers to agree on the requirements before any code is written.

    4. Developing the Functionality Using BDD
    Now, let’s implement this functionality following BDD principles.

    We’ll use the red-green-refactor cycle. First, we’ll write a failing test based on the expected behavior, then implement the code just enough to make it pass, and finally, refactor the method to improve code quality.

    In the Start module, we’ll find a completeTask() method in the DefaultTaskService with an empty implementation. We’ll be implementing the required functionality in this method using the BDD approach. Additionally, we’ve added a completedAt field to the Task entity for this lesson. We’ll use it to track a task’s completion time.

    4.1. Introducing BDDMockito
    Mockito provides BDD-style support through the BDDMockito class. It contains a set of static methods which are BDD-style equivalents of the regular Mockito methods we’ve been using so far.

    By using BDDMockito, we can write tests that naturally fit into the Given–When–Then style:

    Given → given() to define mock behavior
    When → the actual call to the method under test (no special BDDMockito method is needed here)
    Then → then() to verify interactions
    4.2. Writing a Test Case to Meet the Acceptance Criteria
    First, let’s start by creating a test case in DefaultTaskServiceUnitTest that mirrors the behavior described in our user story’s acceptance criteria:
     */

    @Test
    void givenInProgressTask_whenCompleteTask_thenTaskMarkedAsDoneAndCompletedAtRecorded() {
        // given
        Campaign campaign = new Campaign("C3-CODE", "Campaign 3", "Campaign 3 Description");
        Worker worker = new Worker("worker1@test.com", "Worker", "One");
        Task inProgressTask = new Task("Task 3", "Task 3 Description", LocalDate.now(), campaign , TaskStatus.IN_PROGRESS, worker);
        Long taskId = 1L;
        inProgressTask.setId(taskId);

        given(taskRepository.findById(taskId))
                .willReturn(Optional.of(inProgressTask));
        given(taskRepository.save(inProgressTask))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        taskService.completeTask(taskId);

        // then
        then(taskRepository)
                .should()
                .findById(taskId);
        then(taskRepository)
                .should()
                .save(assertArg(task -> {
                    assertEquals(TaskStatus.DONE, task.getStatus());
                    assertNotNull(task.getCompletedAt());
                    assertEquals(worker, task.getAssignee());
                }));
        /*
        Here’s what’s happening in this test:

        We use given() from BDDMockito to define the mock behavior for our external dependency (TaskRepository). The mocking functionality here is the same as in earlier lessons — only the syntax changes to match the BDD style.
        We keep our domain entities as real objects (Campaign, Worker, Task). This follows the general unit testing best practice of mocking behavior (interactions with dependencies) rather than state (the data inside our domain objects).
        The “When” step is simply calling the method under test — taskService.completeTask(taskId). There’s no special BDDMockito method for this step; it’s just the action we’re testing.
        We use then() from BDDMockito to verify that the expected interactions (findById() and save()) happened on the mock. For the second interaction, we use assertArg() to capture the saved task and make standard assertions about its state.
        We verify that the status changed to DONE, the completedAt value was recorded correctly, and the assignee remains unchanged.
        Let’s run this test now and see the result:


        As expected, our test case fails as we’ve not yet implemented completeTask().

        4.3. Implementing the Method to Pass the Test
        With a failing test in place, we’ll now write the simplest code possible in our completeTask() method to make it pass:

        @Override
        public Task completeTask(Long id) {
            Task task = taskRepository.findById(id).get();
            task.setStatus(TaskStatus.DONE);
            task.setCompletedAt(Instant.now());
            return taskRepository.save(task);
        }
        Copy
        Here, we write a straightforward implementation. We first fetch the Task using its id, then set its status and completedAt fields to DONE and Instant.now(), and finally, save the updated Task.

        We can re-execute our test case and verify that it now passes:
         */
    }
    /*
    GRASP link: https://en.wikipedia.org/wiki/GRASP_(object-oriented_design)#Information_expert

    4.4. Refactoring for Better Design
    With the test green, the red–green–refactor cycle prompts us to look for design improvements.
    Right now, the service layer is responsible for mutating the Task’s internal state.

    A better design would be to use the Information Expert pattern, which suggests that the object holding the data should be responsible for manipulating it.

    Let’s follow this principle and move the completion logic into the Task entity itself:

    public class Task {
        // ...
        public void complete() {
            this.status = TaskStatus.DONE;
            this.completedAt = Instant.now();
        }
    }
    Copy
    Here, our new method encapsulates the logic for completing a task directly within the Task entity.

    Next, we can simplify the completeTask() method in our service class:

    @Override
    public Task completeTask(Long id) {
        Task task = taskRepository.findById(id).get();
        task.complete();
        return taskRepository.save(task);
    }
    Copy
    With this refactoring, our service method is now cleaner and more focused on orchestration.

    Notice that this change is purely a design improvement and doesn’t alter the method’s behavior.
    The service layer now simply orchestrates the workflow while the domain entity handles its internal state changes.

    We can re-execute our test case to verify that it still passes.


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
