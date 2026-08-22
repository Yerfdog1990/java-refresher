package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.baeldung.ljc.domain.model.Task;

class TreeMapUnitTest {

    private TreeMap<String, Task> taskMap;
    private Task taskA, taskB, taskC;

    @BeforeEach
    void setUp() {

        taskA = new Task("TaskA", "Complete Report", "Finish quarterly sales report", LocalDate.of(2050, 7, 15));
        taskB = new Task("TaskB", "Schedule Meeting", "Arrange team sync-up", LocalDate.of(2050, 7, 10));
        taskC = new Task("TaskC", "Review Code", "Review pull requests for project", LocalDate.of(2050, 7, 12));

        taskMap = new TreeMap<>();

        taskMap.put(taskB.getCode(), taskB);
        taskMap.put(taskC.getCode(), taskC);
        taskMap.put(taskA.getCode(), taskA);
    }

    @Test
    void givenTreeMapOfTasksAddedOutOfOrder_whenIterate_thenTasksSortedByKey() {
   
        NavigableSet<String> keySet = taskMap.navigableKeySet();
        Iterator<String> keyIterator = keySet.iterator();

        assertEquals("TaskA", keyIterator.next());
        assertEquals("TaskB", keyIterator.next());
        assertEquals("TaskC", keyIterator.next());
    }

    @Test
    void givenCustomSortedTreeMapOfTasks_whenIterate_thenKeysSortedByTaskNameReturned() {
   
        Map<String, Task> initialTaskMap = new HashMap<>();
        initialTaskMap.put(taskA.getCode(), taskA);
        initialTaskMap.put(taskB.getCode(), taskB);
        initialTaskMap.put(taskC.getCode(), taskC);

        Comparator<String> byName = Comparator.comparing(code -> initialTaskMap.get(code)
            .getName());
        TreeMap<String, Task> byNameMap = new TreeMap<>(byName);
        byNameMap.putAll(initialTaskMap);
        NavigableSet<String> keySet = byNameMap.navigableKeySet();
        Iterator<String> keyIterator = keySet.iterator();
        assertEquals("Complete Report", byNameMap.get(keyIterator.next()).getName());
        assertEquals("Review Code", byNameMap.get(keyIterator.next()).getName());
        assertEquals("Schedule Meeting", byNameMap.get(keyIterator.next()).getName());
    }

    @Test
    void givenTreeMapOfTasks_whenPutAndRemoveTask_thenNaturalOrderPreserved() {
        
        Task newTask = new Task("TaskD", "Start Project", "Start a new project", LocalDate.of(2050, 7, 03));  
        taskMap.put(newTask.getCode(), newTask);
        assertEquals("TaskD", taskMap.lastKey());

        taskMap.remove("TaskA");
        assertEquals("TaskB", taskMap.firstKey());
    }

    @Test
    void givenTreeMapOfTasks_whenFindFirstTask_thenFirstTaskReturned() {
        assertEquals(taskA, taskMap.firstEntry().getValue());
    }

    @Test
    void givenTreeMapOfTasks_whenSubMapOfTasks_thenSubMapOfTasksReturned() {
        
        SortedMap<String, Task> subMap = taskMap.subMap("TaskA", "TaskC");

        assertEquals(2, subMap.size());
        assertTrue(subMap.containsKey("TaskA"));
        assertTrue(subMap.containsKey("TaskB"));
        assertFalse(subMap.containsKey("TaskC"));
    }
}
