package com.baeldung.ljc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

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
    void whenIteratingWithIteratorHasNextAndNext_thenCorrect() {
        List<String> iteratedNames = new ArrayList<>();

        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            iteratedNames.add(task.getName());
            System.out.println(task.getName());
        }

        assertEquals(List.of("Task A", "Task B", "Task C", "Task D"), iteratedNames);
    }

    @Test
    void whenNoNextElementAndCallNext_thenThrowNoSuchElementException() {
        Iterator<Task> iterator = tasks.iterator();
        for (int i = 1; i <= tasks.size(); i++) {
            iterator.next();
        }
        assertFalse(iterator.hasNext()); // no "next" element anymore
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

    @Test
    void whenRemovingTaskDuringIteration_thenCorrect() {
        Iterator<Task> iterator = tasks.iterator();
        Task removedTask = null;
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getName()
                .equals("Task C")) {
                removedTask = task;
                iterator.remove();
            }
        }
        assertEquals(3, tasks.size());
        assertEquals("Task C", removedTask.getName());
        assertFalse(tasks.contains(removedTask));
    }

    @Test
    void whenUsingForEachRemaining_thenIterateFromCurrentPosition() {
        Iterator<Task> iterator = tasks.iterator();
        // skip tasks before "Task B" (inclusive)
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getName()
                .equals("Task B")) {
                break;
            }
        }
        List<String> remainingNames = new ArrayList<>();
        System.out.println("Tasks after [ Task B ]:");
        iterator.forEachRemaining(task -> {
            remainingNames.add(task.getName());
            System.out.println(task.getName());
        });
        assertEquals(List.of("Task C", "Task D"), remainingNames);
    }

    @Test
    void whenUsingListIterator_thenReverseTraversalCorrect() {
        List<String> reverseIteratedNames = new ArrayList<>();
        ListIterator<Task> iterator = tasks.listIterator(tasks.size());
        while (iterator.hasPrevious()) {
            Task task = iterator.previous();
            reverseIteratedNames.add(task.getName());
            System.out.println(task.getName());
        }
        assertEquals(List.of("Task D", "Task C", "Task B", "Task A"), reverseIteratedNames);
    }

    @Test
    void whenUsingListIteratorAdd_ElementGetsInserted() {
        ListIterator<Task> iterator = tasks.listIterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getName()
                .equals("Task C")) {
                iterator.add(new Task("042", "Magic Task", "This is a magic task!", null));
            }
        }
        assertEquals(5, tasks.size());
        assertEquals("Magic Task", tasks.get(3)
            .getName());
        System.out.println("After calling add() in ListIterator's iterations:");
        for (Task task : tasks) {
            System.out.println(task.getName() + " - " + task.getDescription());
        }
    }

    @Test
    void whenUsingListIteratorSet_ElementIsUpdated() {
        ListIterator<Task> iterator = tasks.listIterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getName()
                .equals("Task C")) {
                iterator.set(new Task("042", "Magic Task", "This is a magic task!", null));
            }
        }
        assertEquals(4, tasks.size());
        assertEquals("Magic Task", tasks.get(2)
            .getName());
        System.out.println("After calling set() in ListIterator's iterations:");
        for (Task task : tasks) {
            System.out.println(task.getName() + " - " + task.getDescription());
        }
    }

    @Test
    void whenUsingIterableForEach_ElementDataIsConsumed() {
        // Arrange
        List<String> collectedCodes = new ArrayList<>();
        List<String> expectedCodes = List.of("001", "002", "003", "004");

        // Act: Use forEach to consume data from each task and add it to another list
        tasks.forEach(task -> collectedCodes.add(task.getCode()));

        // Assert: Verify the new list contains exactly the codes from the tasks
        assertNotNull(collectedCodes);
        assertEquals(4, collectedCodes.size(), "The collected list should have 4 codes");
        assertEquals(expectedCodes, collectedCodes, "The collected codes should match the expected codes");
    }

    @Test
    void whenUsingIterableForEach_shouldSetDueDateForAllTasks() {
        // Arrange
        LocalDate expectedDueDate = LocalDate.of(2050, 12, 31);

        // Act: Use forEach to modify each task
        tasks.forEach(task -> task.setDueDate(expectedDueDate));

        // Assert: Verify that every task's due date was updated
        tasks.forEach(task -> {
            assertNotNull(task.getDueDate(), "Due date should not be null");
            assertEquals(expectedDueDate, task.getDueDate(), "Due date should match the expected date");
        });
    }
}
