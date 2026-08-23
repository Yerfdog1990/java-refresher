# Lesson 1 (Module 2): Creating Streams

Source: Baeldung – Creating Streams (Learn Java Streams course)

## Modules

- Start: [creating-streams-start](https://github.com/Baeldung/learn-java-streams/tree/module2/creating-streams-start)
- End (reference solution): [creating-streams-end](https://github.com/Baeldung/learn-java-streams/tree/module2/creating-streams-end)

## 1. Overview

Every stream pipeline starts with a **source**. This lesson covers the core factory methods the JDK provides for creating `Stream` instances, how different kinds of data can serve as sources, and walks through practical examples for each.

## 2. Streams from Collections

The `Collection` interface provides a `stream()` method to create a stream from its elements — letting us process collection data functionally.

Pre-defined test data in `JavaStreamsUnitTest`:

```java
class JavaStreamsUnitTest {

    private final Task task1
      = new Task("T1", "John's house construction", "Construction of John's house in LA", LocalDate.of(2024, 1, 1));
    // ...

    private final Collection<Task> tasks
      = List.of(task1, task2, task3, task4, task5, task6, task7, task8, task9);
}
```

Creating a stream from the collection:

```java
@Test
void whenConvertingTaskCollectionToStream_thenReturnCorrectCount() {
    Stream<Task> taskStream = tasks.stream();
    assertEquals(9, taskStream.count());
}
```

Calling `stream()` on the `tasks` collection creates a stream. `count()` is used here just to confirm the stream was created and contains the expected number of elements. It may look similar to `Collection.size()`, but it works differently under the hood — since this lesson focuses only on stream creation, those details aren't important yet.

## 3. Streams from Arrays

`Arrays.stream()` converts an array into a stream for further processing or manipulation. The resulting stream is **backed by the array** — it operates on the array's data without copying it.

```java
@Test
void whenCreatingStreamFromArray_thenReturnCount() {
    Task[] taskArray = { task1, task2, task3, task4 };
    Stream<Task> arrayTasksToStream = Arrays.stream(taskArray);

    assertEquals(4, arrayTasksToStream.count());
}
```

An array is built from the first four pre-defined `Task` objects and converted to a stream with `Arrays.stream()`.

For **primitive arrays**, `Arrays.stream()` returns type-specific streams (e.g. `IntStream`, `LongStream`, `DoubleStream`) designed for efficiency — covered in more detail in a future lesson.

## 4. Creating a Stream from Varargs

`Stream.of()` is a convenient static factory method that accepts a single value or variable arguments (varargs) of the same type. It ensures type safety by inferring the stream's type from the arguments.

```java
@Test
void whenCreatingStreamUsingStreamOf_thenReturnCount() {
    Stream<Task> taskStream = Stream.of(task5, task6, task7, task8, task9);
    assertEquals(5, taskStream.count());
}
```

A stream is created from the last five pre-defined `Task` objects as varargs, then `count()` verifies it holds five elements.

**Watch out for `null`:** passing `null` as the *sole* argument to `Stream.of()` throws a `NullPointerException`. This happens because it resolves to the non-varargs overload, which expects a non-null value. To safely handle a potentially-null single element, use `Stream.ofNullable()` instead — it returns an empty stream if the value is `null`.

## 5. Creating an Empty Stream

A stream can be empty, containing no elements — useful as a default or fallback when no data is available. `Stream.empty()` creates one directly:

```java
@Test
void whenCreatingAnEmptyStream_thenReturnZero() {
    Stream<Integer> emptyStream = Stream.empty();
    assertEquals(0, emptyStream.count());
}
```

An empty stream of `Integer` elements is created and its count verified as zero.

Alternatively, `Stream.of()` with no arguments also produces an empty stream:

```java
@Test
void whenNoArgument_thenReturnZero() {
    Stream<String> emptyStream = Stream.of();
    assertEquals(0, emptyStream.count());
}
```

Since no arguments are passed to `Stream.of()`, the resulting stream is empty.

## 6. Conclusion

This lesson covered the core ways to create `Stream` instances: `Collection.stream()`, `Arrays.stream()` for array-backed streams, `Stream.of()` for streams from explicit values (and its `null`-safe cousin `Stream.ofNullable()`), and `Stream.empty()` / `Stream.of()` with no arguments for empty streams.

These factory methods are the foundation for working with the Streams API — the next lessons build on them with intermediate and terminal operations.
