package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @Test
    void whenUsingTraditionalForLoop_thenCorrect() {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(tasks.get(i)
                .getName());
        }
    }

    @Test
    void whenUsingEnhancedForLoop_thenCorrect() {
        for (Task task : tasks) {
            System.out.println(task.getName());
        }
    }

    @Test
    void whenUsingEnhancedForLoopOnAnArray_thenCorrect() {
        Task[] taskArray = tasks.toArray(new Task[0]);
        for (Task task : taskArray) {
            System.out.println(task.getName());
        }
    }

    @Test
    void whenIndexIsRequiredInLoop_thenTraditionalForLoopIsSimple() {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.printf("[ %s ]'s index is: %d. ", tasks.get(i)
                .getName(), i);
            if (i > 0) {
                System.out.printf("Its previous task is [ %s ].\n", tasks.get(i - 1)
                    .getName());
            } else {
                System.out.println("It doesn't have a previous task.");
            }
        }
    }

    @Test
    void whenIndexIsRequiredInLoop_thenEnhancedForLoopNeedsIndexVariable() {
        int i = 0;
        for (Task task : tasks) {
            System.out.printf("[ %s ]'s index is: %d. ", task.getName(), i);
            if (i > 0) {
                System.out.printf("Its previous task is [ %s ].\n", tasks.get(i - 1)
                    .getName());
            } else {
                System.out.println("It doesn't have a previous task.");
            }
            i++;
        }
    }

    @Test
    void whenReverseIteration_thenTraditionalForLoopWorks() {
        for (int i = tasks.size() - 1; i >= 0; i--) {
            System.out.println(tasks.get(i)
                .getName());
        }
    }

    @Test
    void whenListIsModifiedWhileIteration_thenTraditionalForLoopCorrect() {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (i == 1) {
                tasks.add(2, new Task("42", "New Task X", "a new task", null));
            }
            if (task.getName()
                .equals("Task C")) {
                tasks.removeLast();
            }
            System.out.println(task.getName());
        }
    }

    @Test
    void whenListIsModifiedWhileIteration_thenEnhancedForLoopFails() {
        assertThrows(ConcurrentModificationException.class, () -> {
            for (Task task : tasks) {
                if (task.getName()
                    .equals("Task B")) {
                    tasks.add(new Task("42", "New Task X", "a new task", null));
                }
                System.out.println(task);
            }
        });
    }
}