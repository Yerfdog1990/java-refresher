package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;

import com.baeldung.ljc.domain.model.Task;

class LegacyCollectionsUnitTest {

    private final Task task01 = new Task("T01", "Task 01", "This is the task 01.", null);
    private final Task task02 = new Task("T02", "Task 02", "This is the task 02.", null);
    private final Task task03 = new Task("T03", "Task 03", "This is the task 03.", null);
    private final Task task04 = new Task("T04", "Task 04", "This is the task 04.", null);

    private static final int THREADS = 50;
    private static final int ELEMENTS_PER_THREAD = 10000;

    @Test
    void whenAddingAndRetrievingWithVector_thenMaintainsOrder() {
        Vector<Task> vector = new Vector<>();
        vector.add(task01);
        vector.add(task02);
        vector.add(task03);
        vector.add(task02);

        assertEquals(4, vector.size());
        assertEquals(task01, vector.get(0));
        assertEquals(task02, vector.get(1));
        assertEquals(task03, vector.get(2));
        assertEquals(task02, vector.get(3));
    }

    @Test
    void whenUsingVector_thenItIsThreadSafe() throws InterruptedException {
        List<Integer> list = new Vector<>();
        // List<Integer> list = new ArrayList<>(); // If we use ArrayList here, it would make the test fail due to lack of synchronization
        CountDownLatch latch = new CountDownLatch(THREADS);

        for (int i = 0; i < THREADS; i++) {
            new Thread(() -> {
                for (int j = 0; j < ELEMENTS_PER_THREAD; j++) {
                    list.add(j);
                }
                latch.countDown();
            }).start();
        }

        latch.await();
        assertEquals(THREADS * ELEMENTS_PER_THREAD, list.size()); // For ArrayList this would fail!
    }

    @Test
    void whenPushingAndPoppingWithStack_thenFollowsLifoOrder() {
        Stack<Task> stack = new Stack<>();
        stack.push(task01);
        stack.push(task02);
        stack.push(task03);
        stack.push(task04);

        assertEquals(4, stack.size());

        assertEquals(task04, stack.pop());
        assertEquals(3, stack.size());
        assertFalse(stack.contains(task04));

        assertEquals(task03, stack.pop());
        assertEquals(2, stack.size());
        assertFalse(stack.contains(task03));

        assertEquals(task02, stack.peek());
        assertEquals(2, stack.size());
        assertTrue(stack.contains(task02));

    }

    @Test
    void whenPuttingAndGettingWithHashTable_thenWorksButNoNullsAllowed() {
        Hashtable<String, Task> hashtable = new Hashtable<>();
        hashtable.put("the 1st task", task01);
        hashtable.put("the 2nd task", task02);
        hashtable.put("the 3rd task", task03);

        assertEquals(task01, hashtable.get("the 1st task"));
        assertEquals(task02, hashtable.get("the 2nd task"));
        assertEquals(task03, hashtable.get("the 3rd task"));

        assertThrows(NullPointerException.class, () -> hashtable.put(null, task04));
        assertThrows(NullPointerException.class, () -> hashtable.put("the 4th task", null));
    }
}