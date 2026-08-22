package com.baeldung.ljc;

import java.time.LocalDate;
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
            new Task("T001", "A- Past Task", "Due date in far past", LocalDate.of(2000, 4, 15)),
            new Task("T002", "B- Far-Future Task", "Due date in far future", LocalDate.of(2070, 5, 10)),
            new Task("T003", "C- Mid-Future Task", "Due date in near future", LocalDate.of(2050, 12, 1))
        // @formatter:on
        ));
    }
}