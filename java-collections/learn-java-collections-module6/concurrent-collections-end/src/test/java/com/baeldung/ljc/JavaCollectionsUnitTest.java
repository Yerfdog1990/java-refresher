package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.Test;

import com.baeldung.ljc.domain.model.Task;

class JavaCollectionsUnitTest {

    @Test
    void givenSynchronizedTaskList_whenMultipleThreadsAdd_thenAllTasksRecordedButSlower() throws InterruptedException {
        List<String> list = Collections.synchronizedList(new ArrayList<>());
        Runnable writer = () -> {
            for (int i = 0; i < 100; i++) { list.add("Task-" + i);
            }
        };

        Thread t1 = new Thread(writer);
        Thread t2 = new Thread(writer);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        assertEquals(200, list.size());
    }

    @Test
    void givenConcurrentTaskCache_whenMultipleThreadsInitialize_thenTaskCreatedOnce() throws InterruptedException {
        ConcurrentHashMap<String, Task> taskCache = new ConcurrentHashMap<>();
        Runnable worker = () -> taskCache.computeIfAbsent("001", id -> new Task(id, "Generated Task", "Created once", null));

        Thread t1 = new Thread(worker);
        Thread t2 = new Thread(worker);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        assertEquals(1, taskCache.size());
        assertEquals("Generated Task", taskCache.get("001").getName());
    }

    @Test
    void givenObserverList_whenIteratingAndAdding_thenIterationUsesSnapshot() throws InterruptedException {
        CopyOnWriteArrayList<String> observers = new CopyOnWriteArrayList<>();
        observers.add("Observer A");
        observers.add("Observer B");

        List<String> seenDuringIteration = new ArrayList<>();

        Thread reader = new Thread(() -> {
            for (String obs : observers) {
                seenDuringIteration.add(obs);
                try {
                    Thread.sleep(50); // simulate slow iteration
                } catch (InterruptedException ignored) {}
            }
        });

        Thread writer = new Thread(() -> {
            observers.add("New Observer A");
            observers.add("New Observer B");
        });

        reader.start();
        writer.start();
        reader.join();
        writer.join();

        // The iterator only saw the original snapshot even if we add in the list new items
        assertEquals(List.of("Observer A", "Observer B"), seenDuringIteration);

        // The new observers were added, but only visible after iteration
        assertTrue(observers.contains("New Observer A"));
        assertTrue(observers.contains("New Observer B"));
    }
}