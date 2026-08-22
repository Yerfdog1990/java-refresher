package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class JavaCollectionsTests {

    @Test
    void whenArrayIsCreated_thenSizeIsFixed() {
        String[] languages = new String[3];
        languages[0] = "Java";
        languages[1] = "Kotlin";
        languages[2] = "Python";

        // exception raised when adding the 4th element
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> languages[3] = "C++");

        // to add more elements, we need to create a new array
        String[] moreLanguages = new String[4];
        System.arraycopy(languages, 0, moreLanguages, 0, languages.length);
        moreLanguages[3] = "C++";

        assertArrayEquals(new String[] { "Java", "Kotlin", "Python", "C++" }, moreLanguages);
    }

    @Test
    void whenLookupElementsInArray_thenWeMustLoopThroughTheArray() {
        String[] languages = new String[3];
        languages[0] = "Java";
        languages[1] = "Kotlin";
        languages[2] = "Python";

        boolean hasPython = false;
        // loop through the array and check every element
        for (int i = 0; i < languages.length; i++) {
            if (languages[i].equals("Python")) {
                hasPython = true;
                break;
            }
        }

        assertTrue(hasPython);
    }

    @Test
    void whenNeglectingTheCovarianceIssueInArray_thenExceptionWillRaise() {
        String[] strings = new String[3];
        Object[] objects = strings; // this is allowed because arrays are covariant

        // adding Strings to objects is ok:
        objects[0] = "Java";
        objects[1] = "Kotlin";

        // exception raised when adding a number to objects, although it's Object[]
        assertThrows(ArrayStoreException.class, () -> objects[2] = 42);
    }
}