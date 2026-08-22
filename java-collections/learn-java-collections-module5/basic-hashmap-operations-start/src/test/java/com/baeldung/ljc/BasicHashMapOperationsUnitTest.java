package com.baeldung.ljc;

import com.baeldung.ljc.domain.model.Task;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;

class BasicHashMapOperationsUnitTest {

    private Map<String, Task> taskMap;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        
        task1 = new Task("T001", "Complete Report", "Finish quarterly sales report", LocalDate.of(2025, 7, 15));
        task2 = new Task("T002", "Schedule Meeting", "Arrange team sync-up", LocalDate.of(2025, 7, 10));
        task3 = new Task("T003", "Review Code", "Review pull requests for Project X", LocalDate.of(2025, 7, 12));
        
        Map<String, Task> tasks = Map.ofEntries(
          Map.entry(task1.getCode(), task1),
          Map.entry(task2.getCode(), task2),
          Map.entry(task3.getCode(), task3)  
        );

        taskMap = new HashMap<>(tasks);
    }
}
