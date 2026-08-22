package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.SequencedCollection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.baeldung.ljc.domain.model.Task;
import com.baeldung.ljc.service.TaskHistoryManager;

class TaskHistorySystemUnitTest {

    private Task taskA, taskB, taskC;

    @BeforeEach
    void setUp() {
        taskA = new Task("TaskA", "Complete Report", "Finish quarterly sales report", LocalDate.of(2050, 7, 15));
        taskB = new Task("TaskB", "Schedule Meeting", "Arrange team sync-up", LocalDate.of(2050, 7, 10));
        taskC = new Task("TaskC", "Review Code", "Review pull requests for project", LocalDate.of(2050, 7, 12));
    }

    private void runCommonTests(SequencedCollection<Task> backingCollection) {
        TaskHistoryManager manager = new TaskHistoryManager(backingCollection);

        manager.addTask(taskA);
        manager.addTask(taskB);
        manager.addTask(taskC);
        
        assertEquals(taskA, manager.getFirstTask());
        assertEquals(taskC, manager.getLastTask());

        SequencedCollection<Task> recentHistory = manager.getRecentHistory();
        assertEquals(taskC, recentHistory.getFirst());
        assertEquals(taskA, recentHistory.getLast());
        
        assertEquals(taskC, manager.undoLastTask());
        assertEquals(taskB, manager.getLastTask());
    }
    
    @Test
    void givenTaskHistoryManager_whenBackedByList_thenOk() {
        runCommonTests(new ArrayList<>());
    }
    
    @Test
    void givenTaskHistoryManager_whenBackedBySet_thenOk() {
        runCommonTests(new LinkedHashSet<>());
    }
}
