package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.baeldung.ljc.domain.model.Task;

class CollectionsUnitTest {

    private final Task task01 = new Task("T01", "Task 01", "This is task 01", LocalDate.of(2025, 1, 1));
    private final Task task02 = new Task("T02", "Task 02", "This is task 02", LocalDate.of(2025, 2, 1));
    private final Task task03 = new Task("T03", "Task 03", "This is task 03", LocalDate.of(2025, 3, 1));
    private final Task task04 = new Task("T04", "Task 04", "This is task 04", LocalDate.of(2025, 4, 1));
    private final Task task05 = new Task("T05", "Task 05", "This is task 05", LocalDate.of(2025, 5, 1));
    private final Task task06 = new Task("T06", "Task 06", "This is task 06", LocalDate.of(2025, 6, 1));

    private List<Task> taskList;
    private List<String> stringList;

    @BeforeEach
    void resetCollections() {
        taskList = new ArrayList<>(List.of(task02, task01, task05, task04, task06, task03));
        stringList = new ArrayList<>(List.of("a", "c", "b", "e", "d"));
    }

    @Test
    void whenSortingWithComparableElements_thenCorrect() {
        Collections.sort(stringList);
        assertEquals(List.of("a", "b", "c", "d", "e"), stringList);
    }

    @Test
    void whenSortingWithComparator_thenCorrect() {
        Collections.sort(taskList, Comparator.comparing(Task::getDueDate));
        assertEquals(List.of(task01, task02, task03, task04, task05, task06), taskList);
    }

    @Test
    void whenReversingCollection_thenCorrect() {
        Collections.sort(taskList, Comparator.comparing(Task::getDueDate));
        assertEquals(List.of(task01, task02, task03, task04, task05, task06), taskList);

        Collections.reverse(taskList);
        assertEquals(List.of(task06, task05, task04, task03, task02, task01), taskList);
    }

    @Test
    void whenBinarySearchInSortedTasks_thenCorrect() {
        Collections.sort(taskList, Comparator.comparing(Task::getDueDate));
        int task04Index = Collections.binarySearch(taskList, task04, Comparator.comparing(Task::getDueDate));
        assertEquals(3, task04Index);
    }

    @Test
    void whenBinarySearchForString_thenNoComparatorNeededAndNonExistingRetrievesNegativeInsertingOrderLessOne() {
        List<String> sortedStringList = new ArrayList<>(List.of("a", "b", "d", "e"));
        int indexOfD = Collections.binarySearch(sortedStringList, "d");
        assertEquals(2, indexOfD);

        int nonExistingIndex = Collections.binarySearch(sortedStringList, "c");
        assertEquals(-3, nonExistingIndex);

        sortedStringList.add(-nonExistingIndex - 1, "c");
        assertEquals(List.of("a", "b", "c", "d", "e"), sortedStringList);
    }

    @Test
    void whenShufflingCollection_thenCorrect() {
        Collections.sort(taskList, Comparator.comparing(Task::getDueDate));
        assertEquals(List.of(task01, task02, task03, task04, task05, task06), taskList);

        System.out.println("Shuffling the list:");
        Collections.shuffle(taskList);
        System.out.println(taskList);

        System.out.println("=========================");
        System.out.println("Shuffling the list again:");
        Collections.shuffle(taskList);
        System.out.println(taskList);
    }

    @Test
    void whenFillingCollections_thenCorrect() {
        assertEquals(6, taskList.size());
        Task templateTask = new Task("T00", "{TemplateTaskName}", "This is template Task", null);
        Collections.fill(taskList, templateTask);

        assertEquals(6, taskList.size());
        assertEquals(List.of(templateTask, templateTask, templateTask, templateTask, templateTask, templateTask), taskList);
    }

    @Test
    void whenFindingMinAndMax_thenCorrect() {
        Task minTask = Collections.min(taskList, Comparator.comparing(Task::getDueDate));
        Task maxTask = Collections.max(taskList, Comparator.comparing(Task::getDueDate));
        assertEquals(task01, minTask);
        assertEquals(task06, maxTask);

        String minString = Collections.min(stringList);
        String maxString = Collections.max(stringList);
        assertEquals("a", minString);
        assertEquals("e", maxString);
    }

    @Test
    void whenCreatingEmptyCollections_thenCorrect() {
        List<String> emptyList = Collections.emptyList();
        assertTrue(emptyList.isEmpty());

        Map<String, String> emptyMap = Collections.emptyMap();
        assertTrue(emptyMap.isEmpty());

        Set<Task> emptySet = Collections.emptySet();
        assertTrue(emptySet.isEmpty());

        //emptyCollections are immutable
        assertThrows(UnsupportedOperationException.class, () -> emptyList.add("a"));
    }

    @Test
    void whenCreatingUnmodifiableCollections_thenCorrect() {
        List<Task> unmodifiableTaskList = Collections.unmodifiableList(taskList);
        assertEquals(6, unmodifiableTaskList.size());

        //unmodifiableCollections are immutable
        assertThrows(UnsupportedOperationException.class, () -> unmodifiableTaskList.add(task01));
        assertThrows(UnsupportedOperationException.class, () -> Collections.sort(unmodifiableTaskList, Comparator.comparing(Task::getDueDate)));
    }
}