package com.baeldung.ljc;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;

import com.baeldung.ljc.domain.model.Task;

class JavaCollectionsTests {

    private List<Task> tasks;

    @BeforeEach
    void setUp() {
        tasks = new ArrayList<>(List.of(
        // @formatter:off
          new Task("001", "Task A", "This is Task A", null),
          new Task("002", "Task B", "This is Task B", null),
          new Task("003", "Task C", "This is Task C", null),
          new Task("004", "Task D", "This is Task D", null)
        // @formatter:on
        ));
    }
}