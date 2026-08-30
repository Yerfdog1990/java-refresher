# Lesson 3 (Module 3): sorted(), distinct()

Source: Baeldung – sorted(), distinct() (Learn Java Streams course)

## Modules

- Start: [sorted-distinct-start](https://github.com/Baeldung/learn-java-streams/tree/module3/sorted-distinct-start)
- End (reference solution): [sorted-distinct-end](https://github.com/Baeldung/learn-java-streams/tree/module3/sorted-distinct-end)

## 1. Overview

Many pipelines need uniqueness and ordering fixed before meaningful downstream work can happen. This lesson covers deduplication and ordering of streams: first implementing `equals()`/`hashCode()` as a prerequisite for `distinct()`, then examining `sorted()` and the impact of operation order.

## 2. Eliminating Duplicates With `distinct()`

Before sorting, the stream needs to understand what a "duplicate" is. `distinct()` relies on `equals()` and `hashCode()`.

### 2.1. Add `equals()` and `hashCode()`

In `Task.java`, defining equality so tasks sharing the same code are considered duplicates:

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Task other)) return false;
    return Objects.equals(this.code, other.code);
}

@Override
public int hashCode() {
    return Objects.hash(code);
}
```

Now `distinct()` can remove duplicates according to `code`. Streams preserve **encounter order**, so the first occurrence of a given code "wins."

### 2.2. Verify Deduplication and Encounter Order

Repeating code `T1` with different dates verifies which instance is retained:

```java
@Test
void givenDuplicateCodesAndDifferentDates_whenDistinct_thenKeepsFirstEncountered() {
    List<Task> sample = List.of(
        new Task("T1", "Alpha", "...", LocalDate.of(2026, 1, 1)),
        new Task("T1", "Beta",  "...", LocalDate.of(2024, 1, 1)),
        new Task("T2", "Gamma", "...", LocalDate.of(2025, 3, 15))
    );

    List<Task> unique = new ArrayList<>();

    sample.stream()
      .distinct()
      .forEach(unique::add);

    assertEquals(2, unique.size());
    assertEquals("T1", unique.get(0).getCode());
    assertEquals(LocalDate.of(2026, 1, 1), unique.get(0).getDueDate());
    assertEquals("T2", unique.get(1).getCode());
}
```

This validates both the equality decision and `distinct()`'s encounter-order behavior — the *first* `T1` ("Alpha", 2026) survives, not the second.

### 2.3. `distinct()` After a Mapping Step

A common pattern: map, flatten, normalize, then deduplicate. Collecting distinct words from task descriptions:

```java
@Test
void givenTaskDescriptions_whenMappingWordsAndDistinct_thenAllWordsAreUnique() {
    List<String> words =
      tasks.stream()
        .map(Task::getDescription)
        .flatMap(desc -> Arrays.stream(desc.split("\\s+")))
        .map(w -> w.replaceAll("[^A-Za-z']","")) // strip punctuation
        .filter(w -> !w.isBlank())
        .map(String::toLowerCase)
        .distinct()
        .toList();

    assertEquals(Set.copyOf(words).size(), words.size());
}
```

This shows `distinct()` in a realistic pipeline shape — after `map()` and `flatMap()`, before the terminal operation.

## 3. Sorting With `sorted()`

With duplicates under control, elements can now be put in the order needed downstream.

### 3.1. Natural Ordering With `Comparable`

Teaching `Task` to compare by `dueDate` via `Comparable`:

```java
public class Task implements Comparable<Task>{
    // ...

    @Override
    public int compareTo(Task other) {
        return this.getDueDate().compareTo(other.getDueDate());
    }
}
```

With that in place, `sorted()` uses the natural ordering:

```java
@Test
void givenTasks_whenSortedNaturally_thenOrderedByDueDateAscending() {
    List<String> orderedCodes =
      tasks.stream()
        .sorted()
        .map(Task::getCode)
        .toList();

    assertEquals(List.of("T1", "T2", "T3", "T8", "T4", "T7", "T5", "T6"), orderedCodes);
}
```

**Important caveat:** the natural ordering here (by date) and `equals()` (by code) aren't consistent with each other. That's acceptable for this lesson, but for ordered collections such as `TreeSet`, the comparator order *must* align with equality to avoid subtle bugs — otherwise a sorted set violates the `Set` contract.

### 3.2. Simple In-Line Comparator

For an ad-hoc ordering without touching the domain class, pass a `Comparator` as a lambda:

```java
@Test
void givenTasks_whenSortedWithInlineLambda_thenOrderedByNameAscending() {
    List<String> orderedCodes =
      tasks.stream()
        .sorted((t1, t2) -> t1.getName().compareTo(t2.getName()))
        .map(Task::getCode)
        .toList();

    assertEquals(List.of("T5", "T3", "T7", "T8", "T1", "T4", "T2", "T6"), orderedCodes);
}
```

An equivalent method-reference form:

```java
.sorted(Comparator.comparing(Task::getName))
```

This gives local control over ordering without changing `Task` itself.

### 3.3. Advanced Sorting

The full `Comparator` API supports multi-key sorting and flipping order with `reversed()`:

```java
@Test
void givenTasks_whenSortedByDateThenCodeDescending_thenComparatorReversed() {
    Comparator<Task> byDateThenCodeDescending =
      Comparator.comparing(Task::getDueDate)
        .thenComparing(Task::getCode)
        .reversed();

    List<String> orderedCodes =
      tasks.stream()
        .sorted(byDateThenCodeDescending)
        .map(Task::getCode)
        .toList();

    assertEquals(List.of("T6", "T5", "T7", "T4", "T8", "T3", "T2", "T1"), orderedCodes);
}
```

## 4. Combining `distinct()` and `sorted()`

Real-world data often needs deduplication *and* sorting. The order in which `distinct()` and `sorted()` are applied can affect both performance and accuracy.

### 4.1. Example: Merging Sources

Combining the pre-defined `tasks` list with a secondary source, where some tasks share a code but should collapse to a single, ordered result:

```java
@Test
void givenDuplicatedData_whenDistinctAndSort_thenUniqueAndOrdered() {
    Task task0 = new Task("T0", "New Task", "New task description", LocalDate.of(2030, 1, 1));
    Task secondaryTask2 = new Task("T2", "Different Name", "Different Description", LocalDate.of(2030, 1, 1));
    List<Task> secondarySource = List.of(task1, secondaryTask2, task0);

    List<Task> distinctSortedTasks =
        Stream.concat(tasks.stream(), secondarySource.stream())
              .distinct()
              .sorted(Comparator.comparing(Task::getCode))
              .toList();

    assertEquals(9, distinctSortedTasks.size());
    assertSame(task1, distinctSortedTasks.get(1));
    assertSame(task0, distinctSortedTasks.get(0));
    assertEquals("T2", distinctSortedTasks.get(2).getCode());
    assertNotEquals(secondaryTask2.getName(), distinctSortedTasks.get(2).getName());
}
```

The merge:

- `secondaryTask2` repeats `T2`, but with different field values.
- The secondary source also includes the very same `task1` instance from the primary list, plus a brand-new `T0` task not present in the pre-defined list.

Calling `distinct()` before `sorted()` demonstrates two behaviors at once: deduplication is driven by `equals()`/`hashCode()` on `code` and preserves encounter order, so the *primary* `T2` survives (not `secondaryTask2`); then only the survivors are ordered by the given criteria.

### 4.2. Why Order Matters

If all duplicates (e.g. same code) also share the same sort key (e.g. `dueDate`), then `distinct()` before `sorted()` or `sorted()` before `distinct()` produce the same output.

If duplicates differ in their sort keys, the order of operations matters. In general, it's more efficient to apply `distinct()` first — it reduces the number of elements before the (typically more expensive) sort. But sorting first can make sense too — e.g. to let the "latest update" of an entity win, sort by timestamp first, then apply `distinct()`.

**A known JDK pitfall:** per [JDK-8223933](https://bugs.openjdk.org/browse/JDK-8223933), when `equals()` and the sort order are inconsistent, sorted streams may apply an optimization that only compares *adjacent* elements — which can let non-adjacent duplicates survive.

Best practices to avoid this:

- Prefer `distinct()` before `sorted()`.
- Or ensure the sort order is consistent with `equals()` — for instance, by always including the equality-defining field as the last sorting criterion.

## 5. Conclusion

Implementing `equals()`/`hashCode()` enables `distinct()`; `sorted()` supports both natural ordering (`Comparable`) and custom comparators (inline lambdas, method references, and composed multi-key comparators with `reversed()`). The order of `distinct()` and `sorted()` in a pipeline can affect correctness and performance — the best practice is applying `distinct()` first to shrink the dataset before sorting, or otherwise keeping the sort order consistent with equality.
