package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.baeldung.ljc.domain.model.Task;

class LinkedHashSetUnitTest {


    @Test
    void whenUsingLinkedHashSet_thenElementsPreserveInsertionOrder() {
        LinkedHashSet<String> mySet = new LinkedHashSet<>();
        mySet.add("one");
        mySet.add("two");
        mySet.add("three");
        mySet.add("four");
        assertEquals("[one, two, three, four]", mySet.toString());
    }


    @Test
    void whenUsingLinkedHashSetToMaintainWorkflow_thenCorrect() {
        Task codingTask = new Task("C01", "Coding", "Implement all features of the project.", null);
        Task reviewTask = new Task("R01", "Code Review", "Review the implementation.", null);
        Task testingTask = new Task("T01", "Testing", "Test the implementation.", null);
        Task deploymentTask = new Task("D01", "Deployment", "Deploy to the production environment.", null);

        List<Task> expectedTasks = List.of(codingTask, reviewTask, testingTask, deploymentTask);

        LinkedHashSet<Task> workflow = new LinkedHashSet<>();
        workflow.add(codingTask);
        workflow.add(reviewTask);
        workflow.add(testingTask);
        workflow.add(deploymentTask);

        // keep the insertion order
        List<Task> tasks = new ArrayList<>(workflow);
        assertEquals(expectedTasks, tasks);

        // getFirst() and getLast() are available
        assertEquals(codingTask, workflow.getFirst());
        assertEquals(deploymentTask, workflow.getLast());

        // Iteration order is predictable
        Iterator<Task> iterator = workflow.iterator();
        assertEquals(codingTask, iterator.next());
        assertEquals(reviewTask, iterator.next());
        assertEquals(testingTask, iterator.next());
        assertEquals(deploymentTask, iterator.next());

        // No duplicates allowed, insertion order is still preserved
        workflow.add(reviewTask);
        workflow.add(testingTask);
        List<Task> tasksAfterInsertDuplicate = new ArrayList<>(workflow);
        assertEquals(expectedTasks, tasksAfterInsertDuplicate);

        // Remove an element, insertion order is still preserved
        workflow.remove(reviewTask);
        List<Task> expectedTasksAfterRemoval = List.of(codingTask, testingTask, deploymentTask);
        List<Task> tasksAfterRemoval = new ArrayList<>(workflow);
        assertEquals(expectedTasksAfterRemoval, tasksAfterRemoval);
    }

}