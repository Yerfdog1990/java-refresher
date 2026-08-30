# Lesson 1 (Module 3): Filter

Source: Baeldung – Filter (Learn Java Streams course)

## Modules

- Start: [filter-start](https://github.com/Baeldung/learn-java-streams/tree/module3/filter-start)
- End (reference solution): [filter-end](https://github.com/Baeldung/learn-java-streams/tree/module3/filter-end)

## 1. Overview

When working with streams, often only a subset of elements matching a particular condition needs processing. This lesson covers the `filter()` operation: how it behaves in various scenarios, different ways to define conditions, and a few common gotchas to keep pipelines clean and readable.

## 2. Anatomy of `filter()`

`filter()` narrows a stream down to only those elements that satisfy a given condition. Method signature:

```java
<T> Stream<T> filter(Predicate<? super T> predicate)
```

Key points:

- **Predicate-based** — the operation requires a `Predicate`: a function that receives each stream element and returns a `boolean` deciding whether the element is kept (`true`) or discarded (`false`).
- **Type consistency** — the generic parameter `T` carries forward unchanged, so the input stream's element type is also the output type.
- **Predicate flexibility** — since the parameter is `? super T`, the predicate can be defined on `T` itself or on any of its supertypes, allowing predicates written for broader categories to be reused.

Like other intermediate operations, `filter()` should not modify the underlying data source. And, like any functional interface, the `Predicate` can be expressed inline as a lambda or as a method reference.

## 3. A First Look at `filter()`

### 3.1. Filtering Examples

The `JavaStreamsUnitTest` test class in the start module has a predefined collection of `Task` objects.

To obtain only the tasks whose due date is after 2027:

```java
@Test
void givenTaskStream_whenFilteringTasksWithDueDateAfter2027_thenReturnFilteredList() {
    List<Task> result = new ArrayList<>();
    tasks.stream()
        .filter(task -> task.getDueDate()
            .getYear() > 2027)
        .forEach(result::add);

    assertEquals(5, result.size());
}
```

`filter()` narrows the stream so only matching tasks continue downstream. The `forEach()` terminal operation then executes the pipeline and performs a side effect — here, adding each matching task to a new `List`.

Since `filter()` accepts a `Predicate` (a functional interface), a method reference can be passed instead, for better readability:

```java
static boolean isDueAfter2027(Task task) {
    return task.getDueDate()
        .getYear() > 2027;
}

@Test
void givenTaskStream_whenFilteringTasksWithMethodReference_thenReturnFilteredList() {
    List<Task> result = new ArrayList<>();
    tasks.stream()
        .filter(JavaStreamsUnitTest::isDueAfter2027)
        .forEach(result::add);

    assertEquals(5, result.size());
}
```

Because the helper method is `static` and side-effect-free, it keeps the stream pipeline purely functional and easy to read.

### 3.2. Using Multiple Filters

Multiple `filter()` calls can be chained in a pipeline.

**Combining predicates in a single step** — when the conditions are simple and can be applied at the same stage, `Predicate.and()` combines them:

```java
@Test
void givenStream_whenFilteringUsingMoreThanOnePredicate_thenReturnFutureTasks() {
    Predicate<Task> isConstructionTask = t -> t.getName()
        .contains("construction");

    List<Task> result = new ArrayList<>();
    tasks.stream()
        .filter(isConstructionTask.and(JavaStreamsUnitTest::isDueAfter2027))
        .forEach(result::add);

    assertEquals(2, result.size());
}
```

**Chaining separate `filter()` stages** — useful when conditions aren't conceptually related, or when it's helpful to transform, debug, or otherwise prepare the data flow in between:

```java
@Test
void givenTaskStream_whenFilteringWithMultipleConditions_thenReturnFutureTasks() {
    List<Task> result = new ArrayList<>();
    tasks.stream()
        .filter(JavaStreamsUnitTest::isDueAfter2027)
        // potentially other operations
        .filter(t -> t.getName().contains("construction"))
        .forEach(result::add);

    assertEquals(2, result.size());
}
```

Both approaches yield the same result — the choice comes down to readability and whether extra steps are needed between filters.

## 4. Conclusion

The `filter()` intermediate operation narrows a stream down to elements matching a given `Predicate`. Covered here: inline lambdas, method references, combining predicates with `and()`, and chaining multiple `filter()` calls for independent conditions.
