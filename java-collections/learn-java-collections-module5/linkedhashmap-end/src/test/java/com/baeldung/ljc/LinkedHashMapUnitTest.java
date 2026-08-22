package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.baeldung.ljc.domain.model.Task;

class LinkedHashMapUnitTest {

    private Map<String, Task> taskMap;
    private Task task1, task2, task3;

    @BeforeEach
    void setUp() {

        task1 = new Task("T001", "Complete Report", "Finish quarterly sales report", LocalDate.of(2050, 7, 15));
        task2 = new Task("T002", "Schedule Meeting", "Arrange team sync-up", LocalDate.of(2050, 7, 10));
        task3 = new Task("T003", "Review Code", "Review pull requests for Project X", LocalDate.of(2050, 7, 12));

        taskMap = new LinkedHashMap<>();
    }

    @Test
    void givenLinkedMapOfTasks_whenPutTasksInSpecificOrder_thenTaskInsertionOrderPreserved() {
        // Insert tasks in a specific, non-alphabetical order
        taskMap.put(task2.getCode(), task2); // 1st
        taskMap.put(task3.getCode(), task3); // 2nd
        taskMap.put(task1.getCode(), task1); // 3rd

        // Define the expected order of keys
        List<String> expectedKeyOrder = List.of("T002", "T003", "T001");

        // Get the actual order of keys from the map
        List<String> actualKeyOrder = new ArrayList<>(taskMap.keySet());

        // Assert that the actual order matches the expected insertion order
        assertEquals(expectedKeyOrder, actualKeyOrder, "LinkedHashMap should maintain insertion order.");
    }

    @Test
    void givenLinkedMapOfTasks_whenUpdateTaskForExistingKey_thenTaskInsertionOrderUnchanged() {
        // Insert tasks in a specific, non-alphabetical order
        taskMap.put(task3.getCode(), task3); // 1st
        taskMap.put(task1.getCode(), task1); // 2nd
        taskMap.put(task2.getCode(), task2); // 3rd

        // Get the initial order of keys
        List<String> initialOrder = new ArrayList<>(taskMap.keySet());

        // Update an existing element (T001)
        Task updatedTask1 = new Task("T001", "Start Report", "Start quarterly sales report", LocalDate.of(2050, 7, 15));
        taskMap.put(updatedTask1.getCode(), updatedTask1);

        // Get the new order of keys
        List<String> newOrder = new ArrayList<>(taskMap.keySet());

        // Assert that the order remains unchanged
        assertEquals(initialOrder, newOrder, "Updating an element should not alter its position.");

        // Also verify the element was actually updated
        assertEquals("Start Report", taskMap.get("T001")
            .getName());
    }

    @Test
    void givenAccessOrderedLinkedMapOfTasks_whenGetTask_thenMapAccessOrderChanged() {

        // The 'true' argument is what enables access-order behavior.
        Map<String, Task> taskMap = new LinkedHashMap<>(16, 0.75f, true);

        // Insert tasks in a specific, non-alphabetical order
        taskMap.put(task2.getCode(), task2); // 1st
        taskMap.put(task3.getCode(), task3); // 2nd
        taskMap.put(task1.getCode(), task1); // 3rd

        // This 'get()' operation is an access that will reorder the map.
        Task accessedTask = taskMap.get("T002");

        // Define the expected order after the access
        List<String> expectedOrderAfterAccess = List.of("T003", "T001", "T002");

        // Get the new order of keys
        List<String> actualOrderAfterAccess = new ArrayList<>(taskMap.keySet());

        // Assert: The accessed key 'T002' should now be the last element
        assertEquals(expectedOrderAfterAccess, actualOrderAfterAccess, "The accessed key 'T002' should move to the end.");
    }

    private static class LruCache<K, V> extends LinkedHashMap<K, V> {

        private final int capacity;

        public LruCache(int capacity) {
            // The 'true' argument enables access-order mode.
            super(capacity, 0.75f, true);
            this.capacity = capacity;
        }

        /**
         * This method is called by put and putAll after inserting a new entry.
         * It returns true if the eldest entry should be removed.
         */
        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

    @Test
    void givenTaskCache_whenPutExceedsCapacity_thenLeastRecentlyUsedTaskRemoved() {

        LruCache<String, Task> cache = new LruCache<>(3); // can update the capacity here

        cache.put(task1.getCode(), task1); // Least recently used
        cache.put(task2.getCode(), task2);
        cache.put(task3.getCode(), task3); // Most recently used 

        Task task4 = new Task("T004", "Another Task", "Another description", null);
        cache.put(task4.getCode(), task4);

        // The size is limited by the capacity we defined.
        assertEquals(3, cache.size(), "Cache size should remain equal to the defined capacity.");
        // Assert that the least recently used item has been removed.
        assertFalse(cache.containsKey(task1.getCode()), "Key 'T001' should have been removed.");
        // Assert that the other items are still present.
        assertTrue(cache.containsKey(task2.getCode()), "Task 'T002' should still be in the cache");
        assertTrue(cache.containsKey(task3.getCode()), "Task 'T003' should still be in the cache");
        assertTrue(cache.containsKey(task4.getCode()), "The new Task 'T004' is in the cache");
    }
}
