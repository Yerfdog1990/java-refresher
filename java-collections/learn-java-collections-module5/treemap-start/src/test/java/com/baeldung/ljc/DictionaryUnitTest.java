package com.baeldung.ljc;

import org.junit.jupiter.api.BeforeEach;
import java.util.TreeMap;


class DictionaryUnitTest {

    private TreeMap<String, String> dictionary;

    @BeforeEach
    void setUp() {
        dictionary = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        dictionary.put("Compiler", "A program that translates code from a high-level language to a lower-level language.");
        dictionary.put("API", "Application Programming Interface; a set of rules allowing different applications to communicate.");
        dictionary.put("Tree", "A data structure consisting of nodes in a parent-child relationship.");
        dictionary.put("Binary", "A numeric system that only uses two digits, 0 and 1.");
        dictionary.put("Algorithm", "A set of step-by-step procedures for solving a problem or accomplishing a task.");
    }
}
