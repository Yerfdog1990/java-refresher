package com.baeldung.ljc;

import com.baeldung.ljc.domain.model.Task;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class HashSetUnitTest {

    @Test
    void givenDefaultHashSetConstructor_whenInitializing_thenEmptyHashSetCreated() {
        Set<String> defaultSet = new HashSet<>();

        assertNotNull(defaultSet);
        assertEquals(0, defaultSet.size());
    }

    @Test
    void givenInitialCapacity_whenCreatingHashSet_thenEmptySetCreated() {
        Set<Integer> capacitySet = new HashSet<>(20);

        assertNotNull(capacitySet);
    }

    @Test
    void givenInitialCapacityAndLoadFactor_whenCreatingHashSet_thenEmptySetCreated() {
        Set<Double> customSet = new HashSet<>(5, 0.8f);

        assertNotNull(customSet);
    }

    @Test
    void givenCollection_whenCreatingHashSet_thenHashSetCopied() {
        List<Character> characterList = Arrays.asList('A', 'B', 'C', 'A', 'D'); // Note 'A' is duplicated
        Set<Character> characterSet = new HashSet<>(characterList);

        assertEquals(4, characterSet.size());
    }

    @Test
    void givenTaskEntity_whenUsingTaskAsElement_thenNewHashSetCreated() {
        Task task1 = new Task("T001", "Complete Report", "Finish the quarterly sales report", LocalDate.of(2025, 7, 15));
        Task task2 = new Task("T002", "Schedule Meeting", "Arrange a meeting with the client", LocalDate.of(2025, 7, 20));
        Task task3 = new Task("T001", "Review Code", "Review the new feature's code", LocalDate.of(2025, 7, 18)); // Duplicate taskCode
        Task task4 = new Task("T003", "Prepare Presentation", "Create slides for the project update", LocalDate.of(2025, 7, 25));

        List<Task> taskList = Arrays.asList(task1, task2, task3, task4);
        Set<Task> taskSet = new HashSet<>(taskList);

        assertEquals(3, taskSet.size());
    }

    @Test
    void givenASetOfObjects_whenUsingSetOfMethod_thenSetCreated() {
        Set<String> fruitSet = Set.of("Apple", "Banana", "Orange");

        assertEquals(3, fruitSet.size());
    }

    @Test
    void givenASingleObject_whenUsingCollectionsSingletonMethod_thenSetCreated() {
        Set<String> fruitSet = Collections.singleton("Apple");

        assertEquals(1, fruitSet.size());
    }
}
