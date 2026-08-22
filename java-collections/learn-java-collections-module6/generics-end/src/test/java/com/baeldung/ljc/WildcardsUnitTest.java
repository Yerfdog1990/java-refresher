package com.baeldung.ljc;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WildcardsUnitTest {

    // ---- Domain classes used across the wildcard examples ----

    static class Fruit {
        private final String name;

        Fruit(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }
    }

    static class Banana extends Fruit {
        Banana() {
            super("Banana");
        }
    }

    static class Apple extends Fruit {
        Apple() {
            super("Apple");
        }
    }

    static class NaturalNumber {
        private final int value;

        NaturalNumber(int value) {
            this.value = value;
        }

        int getValue() {
            return value;
        }
    }

    static class EvenNumber extends NaturalNumber {
        EvenNumber(int value) {
            super(value);
        }
    }

    // ---- Helper methods mirroring the lesson's static utility methods ----

    static double sumOfList(List<? extends Number> list) {
        double s = 0.0;
        for (Number n : list) {
            s += n.doubleValue();
        }
        return s;
    }

    static List<String> namesOf(List<? extends Fruit> fruits) {
        List<String> names = new ArrayList<>();
        for (Fruit f : fruits) {
            names.add(f.getName());
        }
        return names;
    }

    static String printList(List<?> list) {
        StringBuilder sb = new StringBuilder();
        for (Object elem : list) {
            sb.append(elem)
                    .append(" ");
        }
        return sb.toString()
                .trim();
    }

    static void addNumbers(List<? super Integer> list) {
        for (int i = 1; i <= 10; i++) {
            list.add(i);
        }
    }

    static void addBanana(List<? super Banana> list) {
        list.add(new Banana());
    }

    private static <T> void fooHelper(List<T> l) {
        l.set(0, l.getFirst());
    }

    static void foo(List<?> i) {
        fooHelper(i);
    }

    // ---- 1. Upper bounded wildcards ----

    @Test
    void givenListOfIntegers_whenSummingWithUpperBoundedWildcard_thenCorrectTotal() {
        List<Integer> li = Arrays.asList(1, 2, 3);

        assertEquals(6.0, sumOfList(li));
    }

    @Test
    void givenListOfDoubles_whenSummingWithUpperBoundedWildcard_thenCorrectTotal() {
        List<Double> ld = Arrays.asList(1.2, 2.3, 3.5);

        assertEquals(7.0, sumOfList(ld), 0.0001);
    }

    @Test
    void givenBunchOfBananas_whenReadingWithUpperBoundedWildcard_thenElementsAccessibleAsFruit() {
        List<Banana> bananas = List.of(new Banana(), new Banana());

        List<String> names = namesOf(bananas);

        assertEquals(List.of("Banana", "Banana"), names);
    }

    @Test
    void givenBunchOfBananas_whenAddingApple_thenCompileTimeErrorPrevented() {
        List<? extends Fruit> bananas = new ArrayList<>(List.of(new Banana()));
        // bananas.add(new Apple()); // Uncommenting this line causes a compile-time error:
        // incompatible types: Apple cannot be converted to CAP#1

        assertEquals(1, bananas.size());
    }

    // ---- 2. Unbounded wildcards ----

    @Test
    void givenListOfIntegers_whenPrintingWithUnboundedWildcard_thenAllElementsPrinted() {
        List<Integer> li = Arrays.asList(1, 2, 3);

        assertEquals("1 2 3", printList(li));
    }

    @Test
    void givenListOfStrings_whenPrintingWithUnboundedWildcard_thenAllElementsPrinted() {
        List<String> ls = Arrays.asList("one", "two", "three");

        assertEquals("one two three", printList(ls));
    }

    @Test
    void givenUnboundedWildcardList_whenAddingElement_thenOnlyNullIsAllowed() {
        List<?> list = new ArrayList<>(List.of("a", "b"));
        // list.add("c"); // Uncommenting this line causes a compile-time error:
        // the compiler can't confirm any non-null value matches the captured type

        assertEquals(2, list.size());
    }

    // ---- 3. Lower bounded wildcards ----

    @Test
    void givenFruitBowl_whenAddingBananaWithLowerBoundedWildcard_thenBananaAccepted() {
        List<Fruit> fruitBowl = new ArrayList<>();

        addBanana(fruitBowl);

        assertEquals(1, fruitBowl.size());
        assertEquals("Banana", fruitBowl.get(0)
                .getName());
    }

    @Test
    void givenEmptyListOfNumber_whenAddingIntegersWithLowerBoundedWildcard_thenAllTenAdded() {
        List<Number> numbers = new ArrayList<>();

        addNumbers(numbers);

        assertEquals(10, numbers.size());
        assertEquals(1, numbers.get(0));
        assertEquals(10, numbers.get(9));
    }

    @Test
    void givenEmptyListOfObject_whenAddingIntegersWithLowerBoundedWildcard_thenAllTenAdded() {
        List<Object> objects = new ArrayList<>();

        addNumbers(objects);

        assertEquals(10, objects.size());
    }

    // ---- 4. Wildcards and subtyping ----

    @Test
    void givenListOfIntegers_whenAssignedToExtendsNumberWildcard_thenRelationshipHolds() {
        List<Integer> intList = new ArrayList<>(List.of(1, 2, 3));

        List<? extends Number> numList = intList;

        assertEquals(3, numList.size());
        assertEquals(6.0, sumOfList(numList));
    }

    @Test
    void givenListOfIntegers_whenNumberListNeeded_thenElementsMustBeCopiedNotAssigned() {
        List<Integer> li = new ArrayList<>(List.of(1, 2, 3));
        // List<Number> ln = li; // compile-time error: List<Integer> is not a List<Number>

        List<Number> ln = new ArrayList<>(li);

        assertEquals(3, ln.size());
        assertEquals(1, ln.get(0));
    }

    // ---- 5. Wildcard capture and helper methods ----

    @Test
    void givenUnboundedWildcardList_whenUsingHelperMethod_thenListCompilesAndStaysUnchanged() {
        List<String> names = new ArrayList<>(List.of("Alice", "Bob"));

        foo(names); // internally captures the wildcard through fooHelper's type inference

        assertEquals(List.of("Alice", "Bob"), names);
    }

    // ---- 6. Guidelines for wildcard use ----

    @Test
    void givenListOfEvenNumbers_whenAssignedToExtendsNaturalNumberWildcard_thenNullCanBeAdded() {
        List<EvenNumber> evenNumbers = new ArrayList<>(List.of(new EvenNumber(2), new EvenNumber(4)));

        List<? extends NaturalNumber> naturalNumbers = evenNumbers;
        naturalNumbers.add(null); // the only element that's always safe to add

        assertEquals(3, naturalNumbers.size());
        assertNull(naturalNumbers.get(2));
    }

    @Test
    void givenListOfEvenNumbers_whenAssignedToExtendsNaturalNumberWildcard_thenAddingNaturalNumberFails() {
        List<EvenNumber> evenNumbers = new ArrayList<>(List.of(new EvenNumber(2)));

        List<? extends NaturalNumber> naturalNumbers = evenNumbers;
        // naturalNumbers.add(new NaturalNumber(35)); // Uncommenting this line causes a compile-time error

        assertEquals(1, naturalNumbers.size());
    }

    @Test
    void givenListOfEvenNumbers_whenAssignedToExtendsNaturalNumberWildcard_thenClearStillWorks() {
        List<EvenNumber> evenNumbers = new ArrayList<>(List.of(new EvenNumber(2), new EvenNumber(4)));

        List<? extends NaturalNumber> naturalNumbers = evenNumbers;
        naturalNumbers.clear();

        assertTrue(naturalNumbers.isEmpty());
        assertTrue(evenNumbers.isEmpty());
    }
}
