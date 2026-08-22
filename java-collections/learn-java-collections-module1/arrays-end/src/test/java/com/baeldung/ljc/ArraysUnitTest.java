package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ArraysUnitTest {

    @Test
    void whenCreated_thenLengthIsFixed() {
        int[] scores = new int[5];

        assertEquals(5, scores.length);
    }

    @Test
    void whenInitializedWithValues_thenSizeMatchesNumberOfEntries() {
        int[] numbers = { 10, 20, 30, 40, 50 };

        assertEquals(5, numbers.length);
    }

    @Test
    void whenAccessingFirstNumber_thenCorrectValueReturned() {
        int[] numbers = { 10, 20, 30, 40, 50 };

        int firstNumber = numbers[0];

        System.out.println("The first number is: " + firstNumber);
        assertEquals(10, firstNumber);
    }

    @Test
    void whenScoreIsAdjusted_thenRevisedValueIsStored() {
        int[] scores = { 5, 10, 15, 20, 25 };

        System.out.println("The second score is initially: " + scores[1]);
        assertEquals(10, scores[1]);

        scores[1] = 25;
        System.out.println("The second score is now: " + scores[1]);
        assertEquals(25, scores[1]);
    }

    @Test
    void whenIteratingUsingForLoop_thenAllNumbersAreVisited() {
        int[] numbers = { 10, 20, 30, 40, 50 };

        for (int i = 0; i < numbers.length; i++) {
            System.out.println("Element at index " + i + ": " + numbers[i]);
        }
    }

    @Test
    void whenUsingEnhancedForLoop_thenAllNumbersAreVisited() {
        int[] numbers = { 10, 20, 30, 40, 50 };
        for (int number : numbers) {
            System.out.print(number + " ");
        }
    }
}