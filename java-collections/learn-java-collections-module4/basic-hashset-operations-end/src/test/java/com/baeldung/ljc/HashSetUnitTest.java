package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.baeldung.ljc.domain.model.Task;

class HashSetUnitTest {

    private final Task task01 = new Task("T01", "Another Task 01", "Another Task with code T01", null);
    private final Task task02 = new Task("T02", "Another Task 02", "Another Task with code T02", null);
    private final Task task03 = new Task("T03", "Another Task 03", "Another Task with code T03", null);
    private final Task task04 = new Task("T04", "Another Task 04", "Another Task with code T04", null);

    private final Task task05 = new Task("T05", "Task 05", "This is Task E", null);
    private final Task task06 = new Task("T06", "Task 06", "This is Task F", null);
    private final Task task07 = new Task("T07", "Task 07", "This is Task G", null);

    private Set<Task> tasks;

    @BeforeEach
    void resetTasks() {
        tasks = new HashSet<>(List.of(
        // @formatter:off
            new Task("T01", "Task 01", "This is Task A", null),
            new Task("T02", "Task 02", "This is Task B", null),
            new Task("T03", "Task 03", "This is Task C", null),
            new Task("T04", "Task 04", "This is Task D", null)
            // @formatter:on
        ));
    }

    @Test
    void whenCallSizeOnTasks_thenCorrect() {
        assertEquals(4, tasks.size());
    }

    @Test
    void whenCallEqualsBetweenTwoSets_thenCorrect() {
        Set<Task> anotherSet = Set.of(task04, task01, task03, task02);
        assertTrue(anotherSet.equals(tasks));
        assertEquals(anotherSet, tasks);
    }

    @Test
    void whenIteratingOverTasksUsingForEach_thenCorrect() {
        tasks.forEach(task -> System.out.println(task.getName()));
    }

    @Test
    void whenCallPrintLnOnTasks_thenCorrect() {
        System.out.println(tasks);
    }

    @Test
    void whenAddOneTaskToTasks_thenCorrect() {
        // adding task05
        boolean addedTask05 = tasks.add(task05);
        assertTrue(addedTask05);
        assertEquals(Set.of(task01, task02, task03, task04, task05), tasks);

        // adding task01
        boolean addedTask01 = tasks.add(task01);
        assertFalse(addedTask01);
        assertEquals(Set.of(task01, task02, task03, task04, task05), tasks);
    }

    @Test
    void whenAddMultipleTaskToTasks_thenCorrect() {
        // adding task01, task05 and task06
        boolean addedTask010506 = tasks.addAll(List.of(task01, task05, task06));
        assertTrue(addedTask010506);
        assertEquals(Set.of(task01, task02, task03, task04, task05, task06), tasks);

        // adding Task05, task06
        boolean addedTask0506 = tasks.addAll(Set.of(task05, task06));
        assertFalse(addedTask0506);
        assertEquals(Set.of(task01, task02, task03, task04, task05, task06), tasks);
    }

    @Test
    void whenCheckExistenceUsingContains_thenCorrect() {
        assertTrue(tasks.contains(task01));
        assertFalse(tasks.contains(task05));
    }

    @Test
    void whenCheckExistenceUsingContainsAll_thenCorrect() {
        assertTrue(tasks.containsAll(List.of(task01, task02)));
        assertFalse(tasks.containsAll(List.of(task01, task07)));
    }

    @Test
    void whenRemoveTaskFromTasks_thenCorrect() {
        boolean removedTask01 = tasks.remove(task01);
        assertTrue(removedTask01);
        assertEquals(Set.of(task02, task03, task04), tasks);

        boolean removedTask07 = tasks.remove(task07);
        assertFalse(removedTask07);
        assertEquals(Set.of(task02, task03, task04), tasks);
    }

    @Test
    void whenRemoveAllTasksFromTasks_thenCorrect() {
        // remove task01 and task05
        boolean removedTask0105 = tasks.removeAll(List.of(task01, task05));
        assertTrue(removedTask0105);
        assertEquals(Set.of(task02, task03, task04), tasks);

        // remove task01 and task07
        boolean removedTask0107 = tasks.removeAll(List.of(task01, task07));
        assertFalse(removedTask0107);
        assertEquals(Set.of(task02, task03, task04), tasks);
    }

    @Test
    void whenRemoveWithRemoveIf_thenCorrect() {
        // remove tasks by a condition
        tasks.removeIf(task -> task.getDescription()
            .endsWith("Task A") || task.getName()
                .contains("03"));
        System.out.println(tasks);
    }

    @Test
    void whenClearTasks_thenCorrect() {
        tasks.clear();
        assertTrue(tasks.isEmpty());
    }

    private Task findByCode(Set<Task> set, String code) {
        for (Task t : set) {
            if (t.getCode()
                .equals(code))
                return t;
        }
        return null;
    }

    @Test
    void whenRemoveThenAddTasks_thenTheElementGetsReplaced() {
        // can't replace directly:
        tasks.add(task01);
        assertNotSame(task01, findByCode(tasks, "T01"));

        // have to remove, then add
        tasks.removeIf(task -> task.getCode()
            .equals("T01"));
        tasks.add(task01);
        assertSame(task01, findByCode(tasks, "T01"));
    }

    @Test
    void whenGetIntersectionBetweenASetAndAnotherCollection_thenCorrect() {
        List<Task> tasks010205 = List.of(task01, task02, task05);
        boolean retainAllReturn = tasks.retainAll(tasks010205);
        assertTrue(retainAllReturn);
        assertEquals(Set.of(task01, task02), tasks);
    }

    @Test
    void whenMutatingElementsInSet_thenUnexceptedResult() {
        Set<Task> myTasks = new HashSet<>();

        Task t01 = new Task("T01", "Task 01", "This is Task 01", null);
        myTasks.add(t01);
        assertTrue(myTasks.contains(t01));

        // modify t01.code
        t01.setCode("TA");
        assertFalse(myTasks.contains(t01));

        // add a new taskTA to the set
        Task ta = new Task("TA", "Task TA", "This is Task TA", null);
        myTasks.add(ta);
        assertEquals(2, myTasks.size());

        System.out.println("Finally, myTasks contains:\n" + myTasks);
    }

}