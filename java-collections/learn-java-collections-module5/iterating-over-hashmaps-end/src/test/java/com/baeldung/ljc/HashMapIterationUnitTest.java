package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.baeldung.ljc.domain.model.Task;

class HashMapIterationUnitTest {

    private Map<String, Task> taskMap;

    @BeforeEach
    void setUp() {
        
        Task task1 = new Task("T001", "Complete Report", "Finish quarterly sales report", LocalDate.of(2050, 7, 15));
        Task task2 = new Task("T002", "Schedule Meeting", "Arrange team sync-up", LocalDate.of(2050, 7, 10));
        Task task3 = new Task("T003", "Review Code", "Review pull requests for Project X", LocalDate.of(2050, 7, 12));
        
        Map<String, Task> tasks = Map.ofEntries(
          Map.entry(task1.getCode(), task1),
          Map.entry(task2.getCode(), task2),
          Map.entry(task3.getCode(), task3)  
        );

        taskMap = new HashMap<>(tasks);
    }

    @Test
    void givenMapOfTasks_whenIterateEntries_thenTasksReturned() {
        for (Map.Entry<String, Task> entry: taskMap.entrySet()) {
            String key = entry.getKey();
            Task value = entry.getValue();
            System.out.println("Key: %s maps to Task: %s".formatted(key, value));
            assertEquals(key, value.getCode());
        }
    }

    @Test
    void givenMapOfTasks_whenIterateKeySet_thenTasksReturned() {
        for(String code: taskMap.keySet()) {
            System.out.println("Key: %s".formatted(code));
            assertTrue(code.startsWith("T"));
        }
    }

    @Test
    void givenMapOfTasks_whenIterateValuesSet_thenTaskDueDateCorrect() {
        for(Task task: taskMap.values()) {
            assertTrue(task.getDueDate().getYear() == 2050);
        }
    }

    @Test
    void givenMapOfTasks_whenRedefineTaskEntry_thenValueSetSafely() {
        
        for (Map.Entry<String, Task> entry: taskMap.entrySet()) {
            entry.setValue(new Task(entry.getKey(), "new name","new description",null)); 
        }

        Task updatedTask = taskMap.get("T001");
        assertEquals("new name", updatedTask.getName());
        assertEquals("new description", updatedTask.getDescription());
    }

    @Test
    void givenMapOfTasks_whenUpdateTaskDescription_thenTaskModified() {

        String updatedDescription = "This description has been updated.";

        for (Task task : taskMap.values()) {
            task.setDescription(updatedDescription);
        }

        Task updatedTask = taskMap.get("T001");
        assertEquals(updatedDescription, updatedTask.getDescription(), "The task's description should be updated in the map.");
    }

    @Test
    void givenMapOfTasks_whenRemoveTaskUsingIterator_thenTaskRemovedSafely() {
        
        Iterator<Map.Entry<String, Task>> iterator = taskMap.entrySet().iterator();
         while (iterator.hasNext()) {
             Map.Entry<String, Task> entry = iterator.next();
             if ("T001".equals(entry.getKey())) {
                 iterator.remove();
             }
        }
        
        assertFalse(taskMap.containsKey("T001"));
        assertEquals(2, taskMap.size());
    }

    @Test
    void givenMapOfTasks_whenPutTaskUsingIterator_thenThrowsException() {
        
        assertThrows(ConcurrentModificationException.class, () -> {
            Iterator<Map.Entry<String, Task>> iterator = taskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                iterator.next();
                taskMap.put("T004", new Task("T004", "New Task", "This is a new task", LocalDate.now()));
            }
        });
    }
}
