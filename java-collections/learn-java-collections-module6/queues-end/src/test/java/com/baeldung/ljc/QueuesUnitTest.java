package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;

import org.junit.jupiter.api.Test;

import com.baeldung.ljc.domain.model.Task;

class QueuesUnitTest {


    @Test
    void givenQueueMethods_whenComparingAddAndOffer_thenBothAddElements() {
        Queue<Task> taskQueue = new LinkedList<>();

        taskQueue.add(new Task("001", "Task added with add()", "Using add method", null));

        taskQueue.offer(new Task("002", "Task added with offer()", "Using offer method", null));

        assertEquals(2, taskQueue.size());
    }

    @Test
    void givenBoundedArrayDeque_whenComparingAddAndOffer_thenOfferReturnsFalseAddThrows() {
        int capacity = 2;
        ArrayDeque<Task> boundedQueue = new ArrayDeque<>(capacity);

        boundedQueue.offer(new Task("001", "First Task", "First task", null));
        boundedQueue.offer(new Task("002", "Second Task", "Second task", null));

        boolean offerResult = (boundedQueue.size() < capacity) && boundedQueue.offer(new Task("003", "Task added with offer()", "Using offer method", null));
        assertFalse(offerResult);

        assertThrows(IllegalStateException.class, () -> {
            if (boundedQueue.size() >= capacity) {
                throw new IllegalStateException("Queue full");
            }
            boundedQueue.add(new Task("003", "Task added with add()", "Using add method", null));
        });

        assertEquals(2, boundedQueue.size());
    }

    @Test
    void givenNonEmptyQueue_whenComparingElementAndPeek_thenBothReturnHead() {
        Queue<Task> taskQueue = new LinkedList<>();
        Task testTask = new Task("001", "Test Task", "Test description", null);
        taskQueue.offer(testTask);

        Task peekedTask = taskQueue.peek();
        assertEquals("Test Task", peekedTask.getName());

        Task elementTask = taskQueue.element();
        assertEquals("Test Task", elementTask.getName());

        assertEquals(1, taskQueue.size());
    }

    @Test
    void givenEmptyQueue_whenComparingElementAndPeek_thenDifferentBehavior() {
        Queue<Task> emptyQueue = new LinkedList<>();

        Task peekedTask = emptyQueue.peek();
        assertNull(peekedTask);

        assertThrows(NoSuchElementException.class, () -> {
            emptyQueue.element();
        });
    }

    @Test
    void givenNonEmptyQueue_whenComparingRemoveAndPoll_thenBothRemoveHead() {
        Queue<Task> taskQueue = new LinkedList<>();
        taskQueue.offer(new Task("001", "First Task", "First", null));
        taskQueue.offer(new Task("002", "Second Task", "Second", null));

        Task polledTask = taskQueue.poll();
        assertEquals("First Task", polledTask.getName());

        Task removedTask = taskQueue.remove();
        assertEquals("Second Task", removedTask.getName());

        assertTrue(taskQueue.isEmpty());
    }

    @Test
    void givenEmptyQueue_whenComparingRemoveAndPoll_thenDifferentBehavior() {
        Queue<Task> emptyQueue = new LinkedList<>();

        Task polledTask = emptyQueue.poll();
        assertNull(polledTask);

        assertThrows(NoSuchElementException.class, () -> {
            emptyQueue.remove();
        });
    }

    @Test
    void givenPriorityQueue_whenUsingNaturalOrdering_thenTasksOrderedByDueDate() {
        PriorityQueue<Task> naturalOrder = new PriorityQueue<>();

        naturalOrder.offer(new Task("003", "Write report", "Write quarterly report", LocalDate.of(2025, 10, 25)));
        naturalOrder.offer(new Task("001", "Deploy application", "Deploy to production", LocalDate.of(2025, 10, 15)));
        naturalOrder.offer(new Task("002", "Schedule meeting", "Schedule team meeting", LocalDate.of(2025, 10, 20)));

        Task firstTask = naturalOrder.poll();
        assertEquals("001", firstTask.getCode());

        Task secondTask = naturalOrder.poll();
        assertEquals("002", secondTask.getCode());

        Task thirdTask = naturalOrder.poll();
        assertEquals("003", thirdTask.getCode());
    }

    @Test
    void givenPriorityQueue_whenUsingCustomOrdering_thenTasksOrderedByNameLength() {
        PriorityQueue<Task> byLength = new PriorityQueue<>(Comparator.comparingInt(task -> task.getName()
            .length()));

        byLength.offer(new Task("001", "Write documentation", "Write user documentation", null));
        byLength.offer(new Task("002", "Review code", "Review pull request", null));
        byLength.offer(new Task("003", "Test", "Run unit tests", null));

        Task firstTask = byLength.poll();
        assertEquals("Test", firstTask.getName());

        Task secondTask = byLength.poll();
        assertEquals("Review code", secondTask.getName());

        Task thirdTask = byLength.poll();
        assertEquals("Write documentation", thirdTask.getName());
    }

}

