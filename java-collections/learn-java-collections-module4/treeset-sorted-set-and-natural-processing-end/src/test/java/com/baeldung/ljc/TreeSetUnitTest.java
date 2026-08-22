package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import com.baeldung.ljc.domain.model.Task;

public class TreeSetUnitTest {

    @Test
    void whenUsingNaturalOrder_thenStringsSorted() {
        TreeSet<String> languages = new TreeSet<>();
        languages.add("es");
        languages.add("fr");
        languages.add("de");

        List<String> ordered = new ArrayList<>(languages);

        assertEquals(List.of("de", "es", "fr"), ordered);
    }

    @Test
    void whenUsingTaskComparator_thenTasksSortedByCodeLength() {
        Comparator<Task> byCodeLength = Comparator.comparingInt(t -> t.getCode()
          .length());
        TreeSet<Task> tasks = new TreeSet<>(byCodeLength);
        tasks.add(new Task("02", "B", "B", null));
        tasks.add(new Task("010", "C", "C", null));
        tasks.add(new Task("1", "A", "A", null));

        List<String> codes = new ArrayList<>();
        for (Task task : tasks) {
            String code = task.getCode();
            codes.add(code);
        }

        assertEquals(List.of("1", "02", "010"), codes);
    }

    @Test
    void whenUsingRangeQueries_thenCorrectElementsReturned() {
        TreeSet<Integer> scores = new TreeSet<>();
        scores.add(55);
        scores.add(70);
        scores.add(85);
        scores.add(95);
        scores.add(100);

        Integer floor = scores.floor(73);
        assertEquals(70, floor);

        Integer ceiling = scores.ceiling(73);
        assertEquals(85, ceiling);

        SortedSet<Integer> mid = scores.subSet(70, true, 95, false);
        assertEquals(List.of(70, 85), new ArrayList<>(mid));
    }

    @Test
    void whenComparingThreeSets_thenOrdersDiffer() {
        List<String> data = List.of("es", "fr", "de");

        Set<String> hash = new HashSet<>(data);
        List<String> hashOrder = new ArrayList<>(hash);

        LinkedHashSet<String> linked = new LinkedHashSet<>(data);
        List<String> linkedOrder = new ArrayList<>(linked);

        TreeSet<String> tree = new TreeSet<>(data);
        List<String> treeOrder = new ArrayList<>(tree);

        assertNotEquals(hashOrder, linkedOrder);
        assertNotEquals(linkedOrder, treeOrder);
        assertEquals(List.of("de", "es", "fr"), treeOrder);
    }

    @Test
    void whenBuildingLeaderboard_thenTopScoresDescend() {
        TreeSet<Integer> scores = new TreeSet<>(List.of(68, 42, 73, 91, 55));

        Iterator<Integer> it = scores.descendingIterator();
        List<Integer> topThree = List.of(it.next(), it.next(), it.next());

        assertEquals(List.of(91, 73, 68), topThree);
    }
}
