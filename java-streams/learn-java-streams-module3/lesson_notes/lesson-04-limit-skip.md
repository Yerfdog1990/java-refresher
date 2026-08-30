# Lesson 4 (Module 3): limit(), skip()

Source: Baeldung – limit(), skip() (Learn Java Streams course)

## Modules

- Start: [limit-skip-start](https://github.com/Baeldung/learn-java-streams/tree/module3/limit-skip-start)
- End (reference solution): [limit-skip-end](https://github.com/Baeldung/learn-java-streams/tree/module3/limit-skip-end)

## 1. Overview

Often only a *slice* of a dataset is needed. Streams provide two operations for this: `limit(n)` and `skip(n)`, usable alone or together to describe the exact window of elements to process. This lesson covers both with real-world examples such as offset-based pagination and trimming large collections.

## 2. Limiting Elements with `limit()`

`limit()` restricts the number of elements flowing through a stream pipeline — handy for sampling data, previewing large collections, or implementing pagination efficiently.

```java
@Test
void givenTasks_whenLimit3Elements_thenReturn3Elements() {
    List<Task> firstThreeElements = new ArrayList<>();
    tasks.stream()
        .limit(3)
        .forEach(task -> {
            System.out.println(task);
            firstThreeElements.add(task);
        });

    assertEquals(3, firstThreeElements.size());
}
```

`limit(3)` short-circuits the pipeline so at most three elements — in the source's encounter order — are processed. Useful in REST APIs, UI table views, or any situation where data is fetched or processed in chunks.

### 2.1. Different Values for `limit()`

- A value **greater than or equal to** the source size has no effect — all elements are returned.
- `limit(0)` returns an **empty stream**, discarding everything.
- A **negative** value throws `IllegalArgumentException`.

```java
@Test
void givenTasks_whenLimitWasSetWithDifferentValues() {
    List<Task> result = new ArrayList<>();
    tasks.stream()
        .limit(tasks.size() + 1)
        .forEach(task -> {
            System.out.println(task);
            result.add(task);
        });
    assertEquals(result.size(), tasks.size());

    List<Task> result1 = new ArrayList<>();
    tasks.stream()
        .limit(0)
        .forEach(task -> {
            System.out.println(task);
            result1.add(task);
        });
    assertEquals(0, result1.size());
    assertThrows(IllegalArgumentException.class, () -> tasks.stream().limit(-1).toList());
}
```

## 3. Skipping Elements with `skip()`

In contrast to `limit(n)`, which restricts how many elements pass through, `skip(m)` **discards the first `m` elements** and processes only the remainder — useful with large collections when a known number of initial items should be ignored.

```java
@Test
void givenTasks_whenSkip2Elements_thenReturnElementsStartingAtIndex2() {
    List<Task> elementsWithoutFirst2 = new ArrayList<>();
    tasks.stream()
        .skip(2)
        .forEach(task -> {
            System.out.println(task);
            elementsWithoutFirst2.add(task);
        });
    assertEquals(tasks.get(2), elementsWithoutFirst2.get(0));
    assertEquals(6, elementsWithoutFirst2.size());
}
```

`skip()` removes the specified number of elements *before* later operations process them — the rest of the pipeline never sees the discarded items.

### 3.1. Different Values for `skip()`

- A value **greater than or equal to** the source size discards everything, returning an **empty stream**.
- `skip(0)` has **no effect** — the stream is unchanged.
- A **negative** value throws `IllegalArgumentException`.

```java
@Test
void givenTasks_whenSkipWasSetWithDifferentValues() {
    List<Task> result = new ArrayList<>();
    tasks.stream()
        .skip(tasks.size() + 1)
        .forEach(task -> {
            System.out.println(task);
            result.add(task);
        });
    assertEquals(0, result.size());
    List<Task> result1 = new ArrayList<>();
    tasks.stream()
        .skip(0)
        .forEach(task -> {
            System.out.println(task);
            result1.add(task);
        });
    assertEquals(tasks.size(), result1.size());
    assertThrows(IllegalArgumentException.class, () -> tasks.stream().skip(-1).toList());
}
```

## 4. Combining `limit()` and `skip()` in One Pipeline

Combining the two is especially useful for extracting a specific slice from a data stream — most classically, **pagination**: retrieving a fixed number of elements starting from a given offset. Fetching page 2, with a page size of 3:

```java
@Test
void givenTasks_whenUsingLimitAndSkip_thenReturnPage2() {
    int page = 2;
    int pageSize = 3;
    List<Task> result = new ArrayList<>();
    tasks.stream()
        .skip(offset(page, pageSize))
        .limit(pageSize)
        .forEach(task -> {
            System.out.println(task);
            result.add(task);
        });
    assertEquals(3, result.size());
}

static long offset(int page, int size) {
    return (long) (page - 1) * size;
}
```

The pagination math is pulled into an `offset()` helper method to keep the fluent pipeline clean.

**Ordering matters for the source, too.** `skip()`/`limit()` rely on the stream's **encounter order**. For ordered sources (`List`, `LinkedHashSet`, `TreeSet`, `LinkedHashMap`), results are consistent and deterministic. For unordered sources (`HashSet`, `HashMap`, `ConcurrentHashMap`), the slice isn't well-defined — force an order first with `sorted()`.

### 4.1. Why Does Order Matter?

`limit()` and `skip()` are evaluated in the order they appear in the pipeline — swapping them yields a different slice.

**`limit()` then `skip()`:**

```java
@Test
void givenTasks_whenUsingLimitFirstAndSkip_thenReturn1Element() {
    List<Task> result = new ArrayList<>();
    tasks.stream()
        .limit(3)
        .skip(2)
        .forEach(task -> {
            System.out.println(task);
            result.add(task);
        });
    assertEquals(1, result.size());
}
```

Step by step, starting from `T1, T2, T3, T4, T5, T6, T7, T8`:

1. `limit(3)` retains only `T1, T2, T3`.
2. `skip(2)` discards the first two of *those*, leaving `T3`.
3. Final result: `T3`.

**`skip()` then `limit()`:**

```java
@Test
void givenTasks_whenSkipFirstAndLimit_thenReturn3Elements() {
    List<Task> result = new ArrayList<>();
    tasks.stream()
        .skip(2)
        .limit(3)
        .forEach(task -> {
            System.out.println(task);
            result.add(task);
        });

    assertEquals(3, result.size());
}
```

Step by step, starting from the same list:

1. `skip(2)` discards the first two elements, leaving `T3, T4, T5, T6, T7, T8`.
2. `limit(3)` retains only the first three of *those*: `T3, T4, T5`.
3. Final result: `T3, T4, T5`.

These operations are **not commutative** — swapping their order changes the data each subsequent step sees. Each step transforms what the next step operates on.

**For pagination, the correct order is `skip()` first, then `limit()`.**

Also worth noting (per the methods' Javadoc, though parallel pipelines aren't covered in depth here): `limit()` and `skip()` can be costly for **ordered parallel** pipelines, since they must respect encounter order to determine the "first/next" elements.

## 5. Conclusion

`limit(n)` restricts a stream to the first `n` elements; `skip(m)` discards the first `m` elements and processes the rest. Combined, they enable practical patterns like pagination — but the order in which `skip()` and `limit()` appear in the pipeline matters, since the two are not commutative.
