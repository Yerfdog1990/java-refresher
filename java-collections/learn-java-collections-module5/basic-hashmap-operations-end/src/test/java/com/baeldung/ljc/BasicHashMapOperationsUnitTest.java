package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.baeldung.ljc.domain.model.Task;

class BasicHashMapOperationsUnitTest {

    private Map<String, Task> taskMap;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {

        task1 = new Task("T001", "Complete Report", "Finish quarterly sales report", LocalDate.of(2050, 7, 15));
        task2 = new Task("T002", "Schedule Meeting", "Arrange team sync-up", LocalDate.of(2050, 7, 10));
        task3 = new Task("T003", "Review Code", "Review pull requests for Project X", LocalDate.of(2050, 7, 12));

        Map<String, Task> tasks = Map.ofEntries(Map.entry(task1.getCode(), task1), Map.entry(task2.getCode(), task2), Map.entry(task3.getCode(), task3));

        taskMap = new HashMap<>(tasks);
    }

    @Test
    void givenHashMap_whenCheckingKeyValuePresence_thenKeyPresenceVerified() {

        assertFalse(taskMap.isEmpty());
        assertTrue(taskMap.containsKey(task1.getCode()));
        assertTrue(taskMap.containsValue(task1));
    }

    @Test
    void givenHashMap_whenUsingGet_thenEntryReturned() {

        assertEquals(task1, taskMap.get("T001"));
    }

    @Test
    void givenHashMap_whenUsingGetOrDefault_thenDefaultReturned() {

        assertEquals(task1, taskMap.getOrDefault("T999", task1));
    }

    @Test
    void givenHashMap_whenUsingPut_thenEntryAdded() {
        Task newTask4 = new Task("T004", "New Task Name", "New Task Description", LocalDate.of(2050, 7, 15));

        taskMap.put(newTask4.getCode(), newTask4);

        assertEquals(4, taskMap.size());
        assertTrue(taskMap.containsKey("T004"));
        assertTrue(taskMap.containsValue(newTask4));
    }

    @Test
    void givenHashMap_whenUsingPutIfAbsent_thenEntryAddedIfAbsent() {
        Task newTask = new Task("TNEW", "Different Name", "Different Description", LocalDate.of(2050, 7, 15));

        // key exists with a non-null value -> no effect
        Task oldTask1 = taskMap.putIfAbsent("T001", newTask);
        assertNotEquals("Different Name", taskMap.get("T001")
            .getName());
        assertSame(task1, oldTask1);

        // key doesn't exist -> adds
        assertFalse(taskMap.containsKey("T005"));
        Task oldNonExistingTask5 = taskMap.putIfAbsent("T005", newTask);

        assertEquals("Different Name", taskMap.get("T005")
            .getName());
        assertNull(oldNonExistingTask5);

        // key exists but value is null -> adds
        taskMap.put("T006", null);
        assertTrue(taskMap.containsKey("T006"));
        Task oldNullTask6 = taskMap.putIfAbsent("T006", newTask);

        assertEquals("Different Name", taskMap.get("T006")
            .getName());
        assertNull(oldNullTask6);
    }

    @Test
    void givenHashMap_whenUsingReplace_thenValueReplaced() {
        Task task1_replacement = new Task("T001", "Start Report", "Start quarterly sales report", LocalDate.of(2050, 7, 15));
        Task oldValue = taskMap.replace("T001", task1_replacement);
        assertSame(task1_replacement, taskMap.get("T001"));
        assertSame(task1, oldValue);

        // nothing happens and null return for non-existing key
        Task oldNonExistingValue = taskMap.replace("T999", task1_replacement);
        assertNull(oldNonExistingValue);
        assertFalse(taskMap.containsKey("T999"));
    }

    @Test
    void givenHashMap_whenUsingReplaceConditionally_thenValueReplaced() {
        Task task1_replacement = new Task("T001", "Different name", "Different description", LocalDate.of(2050, 7, 15));
        // Replace ONLY if the current value equals task1.
        boolean replaced1 = taskMap.replace("T001", task1, task1_replacement);
        boolean replaced2 = taskMap.replace("T002", new Task("T002", null, null, null), task1_replacement);
        boolean replaced3 = taskMap.replace("T003", task1, task1_replacement);
        assertEquals("Different name", taskMap.get("T001")
            .getName());
        assertEquals("Different name", taskMap.get("T002")
            .getName());
        assertNotEquals("Different name", taskMap.get("T003")
            .getName());
        assertTrue(replaced1);
        assertTrue(replaced2);
        assertFalse(replaced3);
    }

    @Test
    void givenHashMap_whenUsingPutForExistingKey_thenValueReplaced() {
        Task task1_replacement = new Task("T001", "Start Report", "Start quarterly sales report", LocalDate.of(2050, 7, 15));
        Task oldValue = taskMap.put("T001", task1_replacement);
        assertSame(task1_replacement, taskMap.get("T001"));
        assertSame(task1, oldValue);
    }

    @Test
    void givenHashMap_whenUsingPutAll_thenAllEntriesAdded() {
        // @formatter:off
        Map<String, Task> newTaskMap = new HashMap<>(Map.of(
            "T001", new Task("T001", "Different name", "Different description", null),
            "T007", new Task("T007", "Another task", "Another description", null)
            ));
        // @formatter:on

        newTaskMap.putAll(taskMap);
        
        assertEquals(4, newTaskMap.size());
        assertNotEquals("Different name", newTaskMap.get("T001")
            .getName());
    }

    @Test
    void givenHashMap_whenUsingReplaceAll_thenAllEntriesReplaced() {

        taskMap.replaceAll((code, task) -> {
            String newDescription = task.getDescription() + " - experimental task";
            task.setDescription(newDescription);
            return task;
        });

        assertTrue((task1.getDescription()).equals("Finish quarterly sales report - experimental task"));
        assertTrue((task2.getDescription()).equals("Arrange team sync-up - experimental task"));
        assertTrue((task3.getDescription()).equals("Review pull requests for Project X - experimental task"));
    }

    @Test
    void givenHashMap_whenUsingClear_thenAllEntriesRemoved() {

        taskMap.clear();
        assertEquals(0, taskMap.size());
    }

    @Test
    void givenHashMap_whenUsingRemove_thenEntryRemoved() {
        Task removed = taskMap.remove("T001");
        assertSame(task1, removed);
        assertEquals(2, taskMap.size());
        assertFalse(taskMap.containsKey("T001"));
    }

    @Test
    void givenHashMap_whenUsingRemoveConditionally_thenEntryRemovedConditionally() {
        taskMap.remove("T001", task1);

        assertEquals(2, taskMap.size());
        assertFalse(taskMap.containsKey("T001"));
        assertFalse(taskMap.containsValue(task1));

        taskMap.remove("T003", task2);
        assertEquals(2, taskMap.size());
        assertTrue(taskMap.containsKey("T003"));
        assertTrue(taskMap.containsValue(task3));
    }
}
