package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JavaCollectionsTests {

    private List<String> myList;

    @BeforeEach
    void setUp() {
        myList = new ArrayList<>(List.of("A", "B", "C", "D", "E"));
    }

    // adding
    @Test
    void whenCallingAddOnAnArrayList_thenGetExpectedResult() {
        myList.add("F");
        myList.add("G");
        assertEquals(List.of("A", "B", "C", "D", "E", "F", "G"), myList);
    }

    @Test
    void whenCallingAddWithIndexOnAnArrayList_thenGetExpectedResult() {
        myList.add(1, "B_0");
        assertEquals(List.of("A", "B_0", "B", "C", "D", "E"), myList);
    }

    @Test
    void whenCallingAddFirstAndAddLastOnAnArrayList_thenGetExpectedResult() {
        myList.addFirst("A_0");
        assertEquals(List.of("A_0", "A", "B", "C", "D", "E"), myList);

        myList.addLast("F");
        assertEquals(List.of("A_0", "A", "B", "C", "D", "E", "F"), myList);

    }

    // retrieving
    @Test
    void whenCallingGetOnAnArrayList_thenGetExpectedResult() {
        String firstElement = myList.get(0);
        assertEquals("A", firstElement);

        String theThirdElement = myList.get(2);
        assertEquals("C", theThirdElement);

        // when the given index is out of the range [0, size)
        assertThrows(IndexOutOfBoundsException.class, () -> myList.get(-5));
        assertThrows(IndexOutOfBoundsException.class, () -> myList.get(42));
    }

    @Test
    void whenCallingGetFirstAndGetLastOnAnArrayList_thenGetExpectedResult() {
        String firstElement = myList.getFirst();
        assertEquals("A", firstElement);

        String theThirdElement = myList.getLast();
        assertEquals("E", theThirdElement);
    }

    @Test
    void whenCallingGetGetFirstAndGetLastOnAnEmptyArrayList_thenGetExpectedExceptions() {
        List<String> emptyList = new ArrayList<>();

        // get()
        assertThrows(IndexOutOfBoundsException.class, () -> emptyList.get(0));

        // getFirst() and getLast()
        assertThrows(NoSuchElementException.class, () -> emptyList.getFirst());
        assertThrows(NoSuchElementException.class, () -> emptyList.getLast());

    }

    // checking for containment
    @Test
    void whenCallingContainsOnAnArrayList_thenGetExpectedResult() {
        boolean containsB = myList.contains("B");
        assertTrue(containsB);

        boolean containsX = myList.contains("X");
        assertFalse(containsX);
    }

    @Test
    void whenCallingIndexOfOnAnArrayList_thenGetExpectedResult() {
        int indexOfB = myList.indexOf("B");
        assertEquals(1, indexOfB); // 1 indicates B exists in myList, its index is 1

        int indexOfX = myList.indexOf("X");
        assertEquals(-1, indexOfX); // -1 indicates X doesn't exist in myList

    }

    // removing
    @Test
    void whenCallingRemoveByIndexOnAnArrayList_thenGetExpectedResult() {
        String removedB = myList.remove(1);
        assertEquals("B", removedB);
        assertEquals(List.of("A", "C", "D", "E"), myList);

        // when the given index is out of the range [0, size)
        assertThrows(IndexOutOfBoundsException.class, () -> myList.remove(42));
    }

    @Test
    void whenCallingRemoveByElementOnAnArrayList_thenGetExpectedResult() {
        boolean removeD = myList.remove("D");
        assertTrue(removeD);
        assertEquals(List.of("A", "B", "C", "E"), myList);

        // when removing a non-existing element
        boolean removeX = myList.remove("X");
        assertFalse(removeX);
        assertEquals(List.of("A", "B", "C", "E"), myList); // the list is unchanged
    }

    @Test
    void whenCallingRemoveByElementOnAnArrayListWithDuplicateElements_thenGetExpectedResult() {
        myList.add("D"); // add another "D"
        assertEquals(List.of("A", "B", "C", "D", "E", "D"), myList);

        boolean removeD = myList.remove("D");
        assertTrue(removeD);
        assertEquals(List.of("A", "B", "C", "E", "D"), myList); // the first "D" is removed

        boolean removeD2 = myList.remove("D");
        assertTrue(removeD2);
        assertEquals(List.of("A", "B", "C", "E"), myList); // the second "D" is removed
    }

    @Test
    void whenCallingRemoveFirstAndRemoveLastOnAnArrayList_thenGetExpectedResult() {
        String firstRemoved = myList.removeFirst();
        assertEquals("A", firstRemoved);
        assertEquals(List.of("B", "C", "D", "E"), myList);

        String lastRemoved = myList.removeLast();
        assertEquals("E", lastRemoved);
        assertEquals(List.of("B", "C", "D"), myList);
    }

    @Test
    void whenCallingRemovingMethodsOnAnEmptyArrayList_thenGetExpectedExceptions() {
        List<String> emptyList = new ArrayList<>();
        // remove() by index
        assertThrows(IndexOutOfBoundsException.class, () -> emptyList.remove(0));

        // remove() by element
        boolean removeX = emptyList.remove("X");
        assertFalse(removeX);

        // removeFirst() and removeLast()
        assertThrows(NoSuchElementException.class, () -> emptyList.removeFirst());
        assertThrows(NoSuchElementException.class, () -> emptyList.removeLast());
    }

    // updating
    @Test
    void whenCallingSetOnAnArrayList_thenGetExpectedResult() {
        String previousAt1 = myList.set(1, "B_1");
        assertEquals("B", previousAt1);
        assertEquals(List.of("A", "B_1", "C", "D", "E"), myList);

        previousAt1 = myList.set(1, "B_2");
        assertEquals("B_1", previousAt1);
        assertEquals(List.of("A", "B_2", "C", "D", "E"), myList);

        assertThrows(IndexOutOfBoundsException.class, () -> myList.set(42, "X"));
    }

    // printing all elements in an ArrayList
    @Test
    void whenPrintingArrayListByToString_thenGetExpectedResult() {
        // using the toString() method
        System.out.println(myList);
    }

    @Test
    void whenPrintingArrayListByLooping_thenGetExpectedResult() {
        // looping through elements
        for (int i = 0; i < myList.size(); i++) {
            System.out.printf("index %d: %s%n", i, myList.get(i));
        }
    }
}