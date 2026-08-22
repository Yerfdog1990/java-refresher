package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class JavaCollectionsTests {

    @Test
    void givenArrayList_whenUsingSizeMethod_thenArrayListSizeReturned() {
        List<String> tasks = new ArrayList<>();
        assertEquals(0, tasks.size());
    }

    @Test
    void givenDefaultArrayListConstructor_whenInitializing_thenEmptyListCreated() {
        List<String> tasks = new ArrayList<>();
        assertEquals(0, tasks.size());
    }

    @Test
    void givenArrayListConstructorForInitialCapacity_whenInitializing_thenEmptyListCreated() {
        List<String> tasks = new ArrayList<>(5);
        assertEquals(0, tasks.size());
    }

    @Test
    void givenArrayListConstructorForExistingCollection_whenInitializing_thenEmptyListCreated() {
        List<String> subtasks = new ArrayList<>(20);
        List<String> tasks = new ArrayList<>(subtasks);
        assertEquals(0, tasks.size());
    }

    @Test
    void givenArraysAsList_whenWrappedInArrayList_thenNoExceptionThrown() {
        assertDoesNotThrow(() -> {
            List<String> days = Arrays.asList("Monday", "Tuesday", null);
            List<String> modifiableDays = new ArrayList<>(days);
            assertEquals(3, modifiableDays.size());
        });
    }

    @Test
    void givenListOf_whenWrappedInArrayList_thenNoExceptionThrown() {
        List<Integer> numbers = List.of(1, 2, 3);
        assertDoesNotThrow(() -> {
            List<Integer> modifiableNumbers = new ArrayList<>(numbers);
            assertEquals(3, modifiableNumbers.size());
        });
    }

    @Test
    void givenListOf_whenUsingNullElement_thenExceptionThrown() {
        assertThrows(NullPointerException.class, () -> {
            List<String> days = List.of("Monday", "Tuesday", null);
        });
    }
}
