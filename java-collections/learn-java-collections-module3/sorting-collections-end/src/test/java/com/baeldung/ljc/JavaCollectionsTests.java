package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.baeldung.ljc.domain.model.Task;

class JavaCollectionsTests {

    private List<Task> tasks;

    @BeforeEach
    void setUp() {
        tasks = new ArrayList<>(List.of(
        // @formatter:off
          new Task("T001", "A- Past Task", "Due date in far past", LocalDate.of(2000, 4, 15)),
          new Task("T002", "B- Far-Future Task", "Due date in far future", LocalDate.of(2070, 5, 10)),
          new Task("T003", "C- Mid-Future Task", "Due date in near future", LocalDate.of(2050, 12, 1))
        // @formatter:on
        ));
    }

    @Test
    void whenComparingTasks_thenOlderDueDateComesFirst() {
        Task pastDueTask = tasks.get(0);
        Task futureDueTask = tasks.get(1);

        int result = pastDueTask.compareTo(futureDueTask);

        assertTrue(result < 0);
    }

    @Test
    void whenUsingNaturalOrdering_thenSortTasksByDueDateAscending() {
        Collections.sort(tasks);

        assertEquals("A- Past Task", tasks.get(0)
            .getName());
        assertEquals("C- Mid-Future Task", tasks.get(1)
            .getName());
        assertEquals("B- Far-Future Task", tasks.get(2)
            .getName());
    }

    @Test
    void whenUsingCustomComparator_thenSortTasksByName() {
        Comparator<Task> nameComparator = new Comparator<>() {

            @Override
            public int compare(Task task1, Task task2) {
                return task1.getName()
                    .compareTo(task2.getName());
            }
        };

        tasks.sort(nameComparator);
        // Collections.sort(tasks, nameComparator);

        assertEquals("A- Past Task", tasks.get(0)
            .getName());
        assertEquals("B- Far-Future Task", tasks.get(1)
            .getName());
        assertEquals("C- Mid-Future Task", tasks.get(2)
            .getName());
    }

    @Test
    void whenUsingLambdaComparator_thenSortTasksByName() {
        tasks.sort((t1, t2) -> t1.getName()
            .compareTo(t2.getName()));

        assertEquals("A- Past Task", tasks.get(0)
            .getName());
        assertEquals("B- Far-Future Task", tasks.get(1)
            .getName());
        assertEquals("C- Mid-Future Task", tasks.get(2)
            .getName());
    }

    @Test
    void whenBuildingComparatorUsingHelperMethods_thenSortTasksByNameAndCodeReversed() {
        tasks.add(new Task("T004", "B- Far-Future Task", "Due date even further in the future", LocalDate.of(2080, 5, 10)));
        Comparator<Task> comparator = Comparator.comparing(Task::getName)
            .thenComparing(Task::getCode)
            .reversed();

        Collections.sort(tasks, comparator);

        assertEquals("C- Mid-Future Task", tasks.get(0)
            .getName());
        assertEquals("Due date even further in the future", tasks.get(1)
            .getDescription());
        assertEquals("Due date in far future", tasks.get(2)
            .getDescription());
        assertEquals("A- Past Task", tasks.get(3)
            .getName());
    }
}
