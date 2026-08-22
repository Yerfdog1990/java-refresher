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

        tasks.addAll(newTasks);

        assertEquals(4, tasks.size());
        assertEquals("Task B", tasks.get(1));
    }

    @Test
    public void whenAddingAllElementsAtIndex_thenListContainsThem() {
        List<String> tasks = new ArrayList<>(List.of("Task A", "Task E"));
        List<String> itemsToInsert = List.of("Task B", "Task C", "Task D");

        tasks.addAll(1, itemsToInsert);

        assertEquals(5, tasks.size());
        assertEquals("Task E", tasks.getLast());
        assertEquals("Task D", tasks.get(3));
    }

    @Test
    public void whenExhaustivelyRemoving_thenListNotContainingAny() {
        List<String> tasks = new ArrayList<>(List.of("Task A", "Task B", "Task C", "Task B"));
        List<String> toRemove = List.of("Task B", "Task D");
        assertTrue(tasks.contains("Task B"));

        tasks.removeAll(toRemove);

        assertEquals(2, tasks.size());
        assertFalse(tasks.contains("Task B"));
        assertFalse(tasks.contains("Task D"));
    }

    @Test
    public void whenRemovingElementsConditionally_thenListNotContainingAny() {
        List<String> tasks = new ArrayList<>(List.of("Task Important", "Task Minor", "Task Urgent", "Task Normal"));
        assertTrue(tasks.contains("Task Minor"));
        
        tasks.removeIf(task -> task.contains("Minor") || task.contains("Normal"));

        assertEquals(2, tasks.size());
        assertFalse(tasks.contains("Task Minor"));
        assertTrue(tasks.contains("Task Important"));
    }

    @Test
    public void whenClearingAList_thenListIsEmpty() {
        List<String> tasks = new ArrayList<>(List.of("Task A", "Task B", "Task C"));
        
        tasks.clear();
        
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void whenReplacingAllElements_thenAllElementsTransformed() {
        List<String> tasks = new ArrayList<>(List.of("Task A", "Task B", "Task C"));
        
        tasks.replaceAll(task -> task + " - Done");

        assertEquals("Task A - Done", tasks.get(0));
        assertEquals("Task B - Done", tasks.get(1));
        assertEquals("Task C - Done", tasks.get(2));
        assertEquals(3, tasks.size());
    }
}
