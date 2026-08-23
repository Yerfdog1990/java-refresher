# Lesson 2 (Module 2): Intermediate and Terminal Operations

Source: Baeldung – Intermediate and Terminal Operations (Learn Java Streams course)

## Modules

- Start: [intermediate-and-terminal-operations-start](https://github.com/Baeldung/learn-java-streams/tree/module2/intermediate-and-terminal-operations-start)
- End (reference solution): [intermediate-and-terminal-operations-end](https://github.com/Baeldung/learn-java-streams/tree/module2/intermediate-and-terminal-operations-end)

## 1. Overview

This lesson covers the main differences between **intermediate** and **terminal** operations in streams, the `Stream`'s **lazy evaluation** feature, and the fact that streams are closed upon consumption and cannot be reused.

## 2. Intermediate Operations

**Intermediate operations** are methods that transform one `Stream` into another. For example, mapping a `Stream<Task>` with `map()` to each `Task`'s code produces a `Stream<String>` of just the codes.

```java
class JavaStreamsUnitTest {

    private final Collection<Task> tasks = List.of(...);

    @Test
    void whenMappingStream_thenReturnsAStreamOfTaskCodes() {
        Stream<Task> taskStream = tasks.stream();
        Stream<String> taskCodeStream = taskStream.map(Task::getCode);
        // ...
    }
}
```

Intermediate operations don't do any processing right away — instead, they build a pipeline of operations to be carried out later. This is **lazy evaluation** (covered in more detail in a later module). For now, the key point: nothing happens unless a terminal operation is present. Intermediate operations alone never trigger execution.

Common intermediate operations: `filter()`, `map()`, `flatMap()`, `distinct()`, `sorted()`, `peek()`, `limit()`, `skip()`. Future lessons look at each of these in detail — how they shape and refine data as it flows through a stream.

## 3. Terminal Operations

**Terminal operations** are methods that produce a result or a side effect from a `Stream`. Unlike intermediate operations, a terminal operation triggers the actual processing of the whole stream pipeline.

Common terminal operations: `forEach()`, `reduce()`, `collect()`, `count()`, `anyMatch()`, `allMatch()`, `findFirst()`, `findAny()`. Future lessons dive deeper into each.

Adding the `toList()` terminal operation to the earlier example triggers execution of `map()` on all elements and collects the results into a `List<String>`:

```java
@Test
void whenMappingStream_thenReturnsAStreamOfTaskCodes() {
    Stream<Task> taskStream = tasks.stream();
    Stream<String> taskCodeStream = taskStream.map(Task::getCode);
    List<String> codes = taskCodeStream.toList();

    List<String> expectedCodes = List.of("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8");
    assertTrue(codes.containsAll(expectedCodes));
}
```

The Stream API's fluent nature allows a more concise, often-preferred style — inlining the intermediate variables directly into the pipeline:

```java
@Test
void whenMappingStream_thenReturnsAStreamOfTaskCodes() {
    List<String> codes = tasks.stream()
      .map(Task::getCode)
      .toList();

    List<String> expectedCodes = List.of("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8");
    assertTrue(codes.containsAll(expectedCodes));
}
```

## 4. Stream Reuse

Once a terminal operation executes, the stream is considered **consumed** and can't be used again — any further attempt to operate on it throws an exception.

Trying to `forEach()` over `taskCodeStream` after it's already been consumed by `toList()` throws an `IllegalStateException` with the message *"stream has already been operated upon or closed"*. This can be demonstrated with JUnit's `assertThrows()`, which catches and verifies exceptions thrown by the code under test:

```java
@Test
void whenTryingToUseAConsumedStream_thenExceptionIsThrown() {
    Stream<String> taskCodeStream = tasks.stream()
        .map(Task::getCode);

    List<String> codes = taskCodeStream.toList();

    assertThrows(
      IllegalStateException.class,
      () -> taskCodeStream.forEach(System.out::println)
    );
}
```

Simply put: a stream can only be used once. Further processing requires a **new** stream.

The fix — after collecting a `Stream` into a `List`, stream *that* list again, creating a fresh `Stream`:

```java
@Test
void whenCollectingAStreamAndStreamingAgain_thenNoExceptionIsThrown() {
    Stream<String> taskCodeStream = tasks.stream()
        .map(Task::getCode);

    List<String> codes = taskCodeStream.toList();

    assertDoesNotThrow(() ->
      codes.stream() // <-- a new stream is created
        .forEach(System.out::println));
}
```

## 5. Conclusion

Two categories of stream operations: **intermediate** and **terminal**. Intermediate operations are lazily evaluated — nothing happens immediately. Terminal operations are eagerly evaluated and trigger execution of the entire pipeline.

Another key difference: a stream pipeline can chain zero, one, or multiple intermediate operations, but it needs **exactly one** terminal operation, which closes the stream and prevents any further operations on it.
