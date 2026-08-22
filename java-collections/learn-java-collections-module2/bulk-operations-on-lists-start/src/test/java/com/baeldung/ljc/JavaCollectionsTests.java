package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class JavaCollectionsTests {

    @Test
    public void whenAddingAllElementsAtEnd_thenListContainsThem() {
        List<String> tasks = new ArrayList<>();
        tasks.add("Task A");
        List<String> newTasks = List.of("Task B", "Task C", "Task D");

        for (int i = 0; i < newTasks.size(); i++) {
            tasks.add(newTasks.get(i));
        }

        assertEquals(4, tasks.size());
        assertEquals("Task B", tasks.get(1));
    }

    @Test
    public void whenAddingAllElementsAtIndex_thenListContainsThem() {
        List<String> tasks = new ArrayList<>(List.of("Task A", "Task E"));
        List<String> itemsToInsert = List.of("Task B", "Task C", "Task D");

        int insertionIndex = 1;
        for (int i = 0; i < itemsToInsert.size(); i++) {
            tasks.add(insertionIndex + i, itemsToInsert.get(i));
        }

        assertEquals(5, tasks.size());
        assertEquals("Task E", tasks.getLast());
        assertEquals("Task D", tasks.get(3));
    }

    @Test
    public void whenExhaustivelyRemoving_thenListNotContainingAny() {
        List<String> tasks = new ArrayList<>(List.of("Task A", "Task B", "Task C", "Task B"));
        List<String> toRemove = List.of("Task B", "Task D");
        assertTrue(tasks.contains("Task B"));

        for (int i = 0; i < toRemove.size(); i++) {
            String elementToRemove = toRemove.get(i);
            while (tasks.contains(elementToRemove)) {
                tasks.remove(elementToRemove);
            }
        }

        assertEquals(2, tasks.size());
        assertFalse(tasks.contains("Task B"));
        assertFalse(tasks.contains("Task D"));
    }

    @Test
    public void whenRemovingElementsConditionally_thenListNotContainingAny() {
        List<String> tasks = new ArrayList<>(List.of("Task Important", "Task Minor", "Task Urgent", "Task Normal"));
        assertTrue(tasks.contains("Task Minor"));

        for (int i = tasks.size() - 1; i >= 0; i--) {
            String task = tasks.get(i);
            if (task.contains("Minor") || task.contains("Normal")) {
                tasks.remove(i);
            }
        }

        assertEquals(2, tasks.size());
        assertFalse(tasks.contains("Task Minor"));
        assertTrue(tasks.contains("Task Important"));
    }

    @Test
    public void whenClearingAList_thenListIsEmpty() {
        List<String> tasks = new ArrayList<>(List.of("Task A", "Task B", "Task C"));
        
        while (!tasks.isEmpty()) {
            tasks.remove(0);
        }

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void whenReplacingAllElements_thenAllElementsTransformed() {
        List<String> tasks = new ArrayList<>(List.of("Task A", "Task B", "Task C"));

        for (int i = 0; i < tasks.size(); i++) {
            String originalTask = tasks.get(i);
            tasks.set(i, originalTask + " - Done");
        }

        assertEquals("Task A - Done", tasks.get(0));
        assertEquals("Task B - Done", tasks.get(1));
        assertEquals("Task C - Done", tasks.get(2));
        assertEquals(3, tasks.size());
    }
}
