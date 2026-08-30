# Lesson 2 (Module 3): map(), flatMap()

Source: Baeldung – map(), flatMap() (Learn Java Streams course)

## Modules

- Start: [map-and-flatmap-start](https://github.com/Baeldung/learn-java-streams/tree/module3/map-and-flatmap-start)
- End (reference solution): [map-and-flatmap-end](https://github.com/Baeldung/learn-java-streams/tree/module3/map-and-flatmap-end)

## 1. Overview

Streams often need to derive new data from existing elements — extracting a field, projecting into another type, or expanding into multiple values. This lesson covers two key methods for that: `map()` and `flatMap()`. They reshape the data flowing through a stream in different ways and form the basis for many practical stream pipelines.

## 2. `map()`

`map()` applies a transformation function to each element, producing a new stream of results. "Transformation" doesn't mean modifying the original elements — each element is *projected* into a new form, which may be the same type or a different one.

For every input element, `map()` produces exactly one output element, so the resulting stream always has the same number of elements as the original. The transformation can change the element's type or value: extracting a field, converting data types, or applying a calculation.

### 2.1. Applying a Simple Transformation

`map()` is often used for straightforward element-wise transformations — a math operation on each number, adjusting text formatting, or any method that produces one result per element.

Squaring each number in a list:

```java
@Test
void givenNumbers_whenSquared_thenReturnSquaredList() {
    List<Integer> numbers = List.of(1, 2, 3, 4, 5);
    List<Integer> expected = List.of(1, 4, 9, 16, 25);
    List<Integer> actual = new ArrayList<>();

    numbers.stream()
      .map(n -> n * n)
      .forEach(actual::add);

    Assertions.assertEquals(expected, actual);
}
```

`map()` applies the square operation to each integer one at a time; `forEach()` (the terminal operation) collects the results into a new list for verification.

`map()` doesn't have to preserve the element type — it can convert elements into a completely different type:

```java
@Test
void givenPrices_whenFormattedToDollarString_thenReturnFormattedList() {
    List<Double> prices = List.of(10.5, 20.0, 99.99);
    List<String> expected = List.of("$10.50", "$20.00", "$99.99");
    List<String> actual = new ArrayList<>();

    prices.stream()
      .map(price -> String.format(Locale.US, "$%.2f", price))
      .forEach(actual::add);

    Assertions.assertEquals(expected, actual);
}
```

Here `map()` converts a `List<Double>` into a `List<String>` by formatting each number as a dollar amount.

### 2.2. Extracting a Single Attribute

A common use of `map()` is extracting a specific field from each object in a POJO list — either to collect those values directly, or to prepare for further stream operations focused on that field.

Given a `Task` class where each task has a unique code via `getCode()`, collecting all task codes:

```java
@Test
void givenTasks_whenMappedToCodes_thenReturnCodeList() {
    List<String> codes = new ArrayList<>();
    List<String> expectedCodes = List.of("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8");

    tasks.stream()
      .map(Task::getCode)
      .forEach(codes::add);

    Assertions.assertEquals(expectedCodes, codes);
}
```

The method reference `Task::getCode` transforms each `Task` into its corresponding code. Since `map()` enforces a one-to-one mapping, the resulting stream has the same number of elements as the original list — now each element is a `String` representing the task's code.

### 2.3. Chaining Multiple Transformations

`map()` can be called multiple times in a pipeline, performing a series of transformations in sequence — useful when data needs multiple processing steps, each refining or changing the previous output:

```java
@Test
void givenWords_whenChainedMapApplied_thenReturnTransformedList() {
    List<String> fruits = List.of("apple", "banana", "cherry");
    List<String> expected = List.of("Elppa", "Ananab", "Yrrehc");
    List<String> actual = new ArrayList<>();

    fruits.stream()
      .map(fruit -> new StringBuilder(fruit).reverse().toString())
      .map(str -> Character.toUpperCase(str.charAt(0)) + str.substring(1))
      .forEach(actual::add);

    Assertions.assertEquals(expected, actual);
}
```

Each `map()` stage processes the output of the previous one to build the final result — here, reversing each word, then capitalizing its first letter.

## 3. `flatMap()`

**The problem `map()` runs into.** The `Task` model has a `List<String> labels` field. To gather all labels from every task into a single collection, a first attempt with `map()`:

```java
@Test
void givenTasks_whenMapToLabels_thenGetListOfLists() {
    Set<String> expected = Set.of(
        "home", "construction", "school", "reparation",
        "restaurant", "restoration", "street", "bridge", "factory"
    );
    List<List<String>> tasksOutput = new ArrayList<>();

    // Extract the labels field (Stream<List<String>>)
    tasks.stream()
        .map(task -> task.getLabels())
        .forEach(tasksOutput::add);

    // Merge all the label Strings in a single set
    Set<String> allDistinctLabels = new HashSet<>();
    for (List<String> taskLabels : tasksOutput) {
        allDistinctLabels.addAll(taskLabels);
    }

    assertEquals(expected, allDistinctLabels);
}
```

`map()` here yields a `Stream<List<String>>` — effectively a "list of lists." Not ideal for working directly with the labels in the pipeline. (`Set::addAll` could shortcut the merge inside `forEach`, but that still leaves a `Stream<List<String>>` shape and limits further per-label processing in the pipeline.)

This is where `flatMap()` helps: it unwraps each `List<String>` into its elements and merges them into a single `Stream<String>` — combining mapping and flattening in one step, especially useful for nested collections.

### 3.1. Flattening Nested Fields

```java
@Test
void givenTasks_whenFlatMappedToLabels_thenReturnDistinctLabelList() {
    Set<String> expected = Set.of(
            "home", "construction", "school", "reparation",
            "restaurant", "restoration", "street", "bridge", "factory"
    );
    Set<String> actual = new HashSet<>();

    tasks.stream()
        .flatMap(task -> task.getLabels().stream())
        .forEach(actual::add);

    assertEquals(expected, actual);
}
```

`flatMap()` transforms each `Task` into a `Stream<String>` of its labels and flattens them into a single stream. Conceptually: `Stream<List<String>> → Stream<String>`.

An equivalent style, splitting the transform and the flatten into two steps:

```java
tasks.stream()
    .map(Task::getLabels)
    .flatMap(Collection::stream)
    // ...
```

Both are valid — use whichever reads more clearly.

### 3.2. Combining `flatMap()` and `map()`

`flatMap()` and `map()` combine well when nested collections need flattening *and* their elements need transforming. Flattening all labels, then uppercasing them:

```java
@Test
void givenTasks_whenFlatMappedAndMapped_thenReturnUppercaseLabels() {
    Set<String> expected = Set.of(
            "HOME", "CONSTRUCTION", "SCHOOL", "REPARATION",
            "RESTAURANT", "RESTORATION", "STREET", "BRIDGE", "FACTORY"
    );

    Set<String> actual = new HashSet<>();

    tasks.stream()
        .flatMap(task -> task.getLabels()
            .stream())
        .map(String::toUpperCase)
        .forEach(actual::add);

    assertEquals(expected, actual);
}
```

`flatMap()` first flattens all label lists into a single stream; `map()` then transforms each label to uppercase. Without `flatMap()`, the pipeline would be stuck handling lists of labels rather than the labels themselves, complicating further transformations.

## 4. Conclusion

`map()` transforms each element one-to-one — changing values, converting types, or extracting fields from objects. `flatMap()` goes a step further, both mapping and flattening nested structures into a single stream — particularly useful when working with collections nested inside objects. Together they're a powerful combination for reshaping data in stream pipelines.
