package com.baeldung.ljc;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;

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
}
