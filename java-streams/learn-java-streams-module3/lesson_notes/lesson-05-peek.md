# Lesson 5 (Module 3): peek()

Source: Baeldung – peek() (Learn Java Streams course)

## Modules

- Start: [peek-start](https://github.com/Baeldung/learn-java-streams/tree/module3/peek-start)
- End (reference solution): [peek-end](https://github.com/Baeldung/learn-java-streams/tree/module3/peek-end)

## 1. Overview

The flow of elements through a stream pipeline can be tricky to follow. Sometimes it's necessary to inspect what's actually passing through at a specific stage without altering the pipeline's behavior. This lesson covers `peek()`, which lets us observe elements as they flow through.

## 2. The Stream `peek()` API

`peek()` is an intermediate operation that attaches a `Consumer` to observe elements as they move through the pipeline. Whatever action is supplied (such as logging) runs on each element, but the element itself is left untouched and continues downstream as if nothing happened.

Its primary purpose is **non-interfering** actions such as logging or debugging. In production code, `peek()` should not introduce side effects or modify elements. Mutating state inside `peek()` is technically possible but considered bad practice — it goes against its intended role of observation only.

## 3. Debugging With `peek()`

`peek()` can trace the flow of data at different pipeline stages. Confirming that two tasks have a name starting with `"J"`:

```java
@Test
void givenTaskStream_whenUsingPeekToDebug_thenLogTaskName() {
    AtomicInteger counter = new AtomicInteger();
    tasks.stream()
      .filter(task -> task.getName().startsWith("J"))
      .peek(task -> LOG.log(Level.INFO, task.getName()))
      .forEach(task -> {
          counter.incrementAndGet();
      });

    assertEquals(2, counter.get());
 }
```

`peek()` logs the task names after filtering, helping verify which elements are passing through at that stage. In more complex pipelines — especially with unknown data — inserting `peek()` at specific points can give valuable insight into how elements evolve and help explain the final output.

**Resist the temptation to validate inside `peek()`** — e.g. throwing an exception when a condition isn't met. `peek()` is meant to stay non-interfering; such checks belong in other stream operations (filtering, combining conditions), not in `peek()` used for control flow.

## 4. Common Pitfalls and Limitations

Misusing `peek()` can cause confusing behavior, subtle bugs, or performance issues.

### 4.1. Keeping `peek()` Non-Interfering

An anti-pattern — mutating elements inside `peek()`:

```java
@Test
void givenTaskStream_whenUsingPeekToModifyTasks_thenCorruptedState() {
    AtomicInteger alteredCounter = new AtomicInteger();
    tasks.stream()
      .peek(task -> {
            if (task.getName().startsWith("J")) {
                task.setName("CORRUPTED");
            }
      })
      .filter(task -> "CORRUPTED".equals(task.getName()))
      .forEach(task -> alteredCounter.incrementAndGet());

    assertTrue(alteredCounter.get() > 0);
}
```

This "works," but it mutates the underlying data instead of just observing it, breaking `peek()`'s intended purpose. **If elements need to be transformed, the right operator is `map()`.**

Side effects that mutate *external* state can also be misleading:

```java
@Test
void givenTaskStream_whenUsingPeekForSideEffects_thenMutateExternalState() {
    AtomicInteger counter = new AtomicInteger();
    tasks.stream()
      .peek(task -> counter.incrementAndGet())
      .forEach(task -> {});

    assertEquals(tasks.size(), counter.get());
}
```

Here the stream's outcome depends on a side effect — this violates the non-interference principle, and in **parallel streams** it could even cause race conditions. Logging is a safe side effect; anything that mutates state should be avoided.

### 4.2. Performance Limitations

Heavy logging or complex computations inside `peek()` can significantly impact performance on large datasets, since `peek()` runs for *every* element — even simple operations add up.

A common safeguard: gate logging behind a log-level check, so `peek()` only does work when detailed logging is actually enabled:

```java
@Test
void givenTaskStream_whenUsingGatedLoggingWithPeek_thenEfficientInspection() {
    AtomicInteger counter = new AtomicInteger();
    Stream<Task> taskStream = tasks.stream()
        .filter(task -> task.getName()
            .startsWith("J"));

    if (LOG.isLoggable(Level.FINE)) {
        taskStream = taskStream.peek(
          task -> LOG.log(Level.FINE, () -> "Filtered task: " + task.getName() + " ID: " + task.getCode()));
    }

    Stream<String> nameStream = taskStream.map(Task::getName);

    if (LOG.isLoggable(Level.FINE)) {
        nameStream = nameStream.peek(name -> LOG.log(Level.FINE, () -> "Mapped name: " + name));
    }

    nameStream.forEach(task -> counter.incrementAndGet());

    assertEquals(2, counter.get());
}
```

Logging is skipped entirely when the `FINE` level isn't enabled, avoiding extra overhead. To actually see the logs in the console, the level needs to be raised on both the logger and its console handler:

```java
@BeforeEach
void setUp() {
    LOG.setLevel(Level.FINE);

    for (Handler handler : LOG.getParent().getHandlers()) {
        if (handler instanceof ConsoleHandler) {
            handler.setLevel(Level.FINE);
        }
    }
}
```

## 5. Conclusion

`peek()` is useful for debugging and inspecting stream elements at various pipeline stages. Key pitfalls: it must stay non-interfering (no mutating elements or external state — use `map()` for transformations), and it carries a real performance cost on large datasets, best mitigated with gated logging so no work happens when detailed logging isn't enabled.
