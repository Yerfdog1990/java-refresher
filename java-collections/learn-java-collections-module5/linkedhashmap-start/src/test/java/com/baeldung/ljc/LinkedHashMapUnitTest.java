package com.baeldung.ljc;

import java.util.LinkedHashMap;
import java.util.Map;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;

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
}
