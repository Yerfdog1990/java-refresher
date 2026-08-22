package com.baeldung.ljc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.baeldung.ljc.domain.model.Container;
import com.baeldung.ljc.domain.model.Task;

class JavaCollectionsTests {

    @Test
    void givenContainer_whenHoldingTask_thenItemTypeIsTask() {
        Task task = new Task("T001", "Complete Report", "Finish quarterly sales report", LocalDate.of(2025, 7, 15));
        Container<Task> container = new Container<>(task);

        assertEquals("Complete Report", container.getItem()
            .getName());
    }

    @Test
    void givenContainer_whenHoldingStringType_thenItemTypeIsString() {
        String task = "Review project documentation";
        Container<String> container = new Container<>(task);

        assertEquals("Review project documentation", container.getItem());
    }

    @Test
    void givenTaskListWithoutGenerics_whenRetrieving_thenNeedsCasting() {
        List list = new ArrayList();
        list.add(new Task("T001", "Write tests", "Unit testing", LocalDate.of(2025, 7, 20)));
        Task task = (Task) list.get(0);

        assertEquals("Write tests", task.getName());
    }

    @Test
    void givenTaskListWithGenerics_whenRetrieving_thenNoCastingNeeded() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("T002", "Review code", "Peer review", LocalDate.of(2025, 7, 22)));

        Task task = tasks.get(0);

        assertEquals("Review code", task.getName());
    }

    @Test
    void givenGenericList_whenAddingWrongType_thenCompileError() {
        List<String> names = new ArrayList<>();
        // names.add(123); // Uncommenting this line causes a compile-time error
    }

    @Test
    void givenRawList_whenAddingDifferentTypes_thenRuntimeErrorLater() {
        List rawList = new ArrayList();
        rawList.add(new Task("T003", "Deploy", "Deploy application", LocalDate.of(2025, 7, 25)));
        rawList.add("Not a task"); // Allowed, but unsafe

        Object obj = rawList.get(1);
        assertThrows(ClassCastException.class, () -> {
            Task wrong = (Task) obj; // Fails at runtime
        });
    }

    @Test
    void givenGenericLists_whenCheckingTypes_thenTypeErasedAtCompileTime() {
        List<Task> tasks = new ArrayList<>();
        List<String> names = new ArrayList<>();

        assertEquals(tasks.getClass(), names.getClass());
    }

    @Test
    void givenRawListAssignedToGeneric_whenAccessingElement_thenCompilerWarningAndRuntimeError() {
        List rawList = new ArrayList();
        rawList.add("Not a Task");

        List<Task> tasks = rawList;
        assertThrows(ClassCastException.class, () -> {
            Task t = tasks.get(0);
        });
    }

    @Test
    void givenListOfIntegers_whenAddingPrimitive_thenAutoboxed() {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(42);

        assertEquals(42, numbers.get(0));
    }

}