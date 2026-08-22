package com.baeldung.ljc;

import java.util.TreeMap;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
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
}
 
