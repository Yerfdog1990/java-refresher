package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


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

    @Test
    void givenDictionary_whenGetWords_thenWordsInAlphabeticalOrder() {
       
        assertEquals("Algorithm", dictionary.firstKey());
        assertEquals("Tree", dictionary.lastKey());
   
        String afterCompiler = dictionary.higherKey("Compiler");
        assertEquals("Tree", afterCompiler, "Tree should come after Compiler");
    }

    @Test
    void givenDictionary_whenRangeLookup_thenSubsetOfWordsReturned() {
        SortedMap<String, String> earlyTerms = dictionary.headMap("C");
    
        assertEquals(3, earlyTerms.size());  
        assertFalse(earlyTerms.containsKey("Compiler"));
        assertTrue(earlyTerms.containsKey("Binary"));
    }
}
