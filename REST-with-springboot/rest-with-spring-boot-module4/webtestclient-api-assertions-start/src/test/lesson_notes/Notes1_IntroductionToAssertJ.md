# Introduction to AssertJ

---

# 1. AssertJ Overview

AssertJ is an open-source, community-driven library used for writing **fluent and rich assertions in Java tests**.

It focuses on:

* Readable test code
* Expressive assertions
* Helpful and detailed error messages
* Strong IDE auto-completion support

---

## 1.1 AssertJ Modules

AssertJ is composed of several modules:

### Core Module

Provides assertions for JDK types:

* `String`
* `Iterable`
* `Stream`
* `Path`
* `File`
* `Map`
* Numbers
* Exceptions
* and more

### Additional Modules

* **Guava module** → `Multimap`, `Optional`
* **Joda Time module** → `DateTime`, `LocalDateTime`
* **Neo4J module** → `Path`, `Node`, `Relationship`
* **DB module** → `Table`, `Row`, `Column`
* **Swing module** → functional UI testing for Swing apps

This lesson focuses on the **AssertJ Core** module.

---

# 2. AssertJ Core

## 2.1 What is AssertJ Core?

AssertJ Core is the main module that:

* Provides fluent assertions
* Improves readability
* Produces detailed error messages
* Is easy to use with IDE auto-completion

Latest Javadoc:
[https://www.javadoc.io/doc/org.assertj/assertj-core/3.27.7](https://www.javadoc.io/doc/org.assertj/assertj-core/3.27.7)

Each assertion is documented with examples.

---

# 3. Maven Dependency

To use AssertJ in your project, add:

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.26.0</version>
    <scope>test</scope>
</dependency>
```

Notes:

* This includes only the **core assertions**
* Additional modules must be added separately
* For Java 7 and earlier → use version 2.x.x

---

# 4. Getting Started

Once the library is on your classpath, add:

```java
import static org.assertj.core.api.Assertions.*;
```

This enables the `assertThat()` entry point.

---

# 5. Writing Assertions

All assertions begin with:

```java
assertThat(actual)
```

⚠ Important:

This does **not** assert anything yet:

```java
assertThat(anyReferenceOrValue);
```

Assertions only happen when you chain methods like:

```java
assertThat(value).isEqualTo(expected);
```

---

# 6. Fluent Assertion Examples

From the official documentation:

```java
assertThat(frodo)
  .isNotEqualTo(sauron)
  .isIn(fellowshipOfTheRing);

assertThat(frodo.getName())
  .startsWith("Fro")
  .endsWith("do")
  .isEqualToIgnoringCase("frodo");

assertThat(fellowshipOfTheRing)
  .hasSize(9)
  .contains(frodo, sam)
  .doesNotContain(sauron);
```

These examples demonstrate:

* Method chaining
* Fluent readability
* Expressive testing style

---

# 7. Assertions by Type

---

## 7.1 Object Assertions

Given:

```java
public class Dog { 
    private String name; 
    private Float weight;
}

Dog fido = new Dog("Fido", 5.25);
Dog fidosClone = new Dog("Fido", 5.25);
```

### Reference Equality (default)

```java
assertThat(fido).isEqualTo(fidosClone);
```

This fails unless `equals()` is overridden.

---

### Field-by-Field Comparison

```java
assertThat(fido)
  .isEqualToComparingFieldByFieldRecursively(fidosClone);
```

This compares:

* `name`
* `weight`

recursively field by field.

---

## 7.2 Boolean Assertions

```java
assertThat("".isEmpty()).isTrue();
```

Available:

* `isTrue()`
* `isFalse()`

---

## 7.3 Iterable / Array Assertions

Given:

```java
List<String> list = Arrays.asList("1", "2", "3");
```

Examples:

```java
assertThat(list).contains("1");
assertThat(list).isNotEmpty();
assertThat(list).startsWith("1");
```

Chaining multiple assertions:

```java
assertThat(list)
  .isNotEmpty()
  .contains("1")
  .doesNotContainNull()
  .containsSequence("2", "3");
```

---

## 7.4 Character Assertions

```java
assertThat(someCharacter)
  .isNotEqualTo('a')
  .inUnicode()
  .isGreaterThanOrEqualTo('b')
  .isLowerCase();
```

Supports:

* Unicode checks
* Comparison
* Case validation

---

## 7.5 Class Assertions

```java
assertThat(Runnable.class).isInterface();
```

Assignable check:

```java
assertThat(Exception.class)
  .isAssignableFrom(NoSuchElementException.class);
```

Used for:

* Interface checks
* Annotation presence
* Finality checks

---

## 7.6 File Assertions

```java
assertThat(someFile)
  .exists()
  .isFile()
  .canRead()
  .canWrite();
```

Supports:

* Existence
* Type (file/directory)
* Permissions
* Content checks

---

## 7.7 Numeric Assertions

```java
assertThat(5.1).isEqualTo(5, withPrecision(1d));
```

This compares numbers using an offset (precision).

Useful for:

* Floating point comparisons
* Tolerance-based equality

---

## 7.8 InputStream Assertions

```java
assertThat(given).hasSameContentAs(expected);
```

Only one specific assertion:

* `hasSameContentAs()`

---

## 7.9 Map Assertions

```java
assertThat(map)
  .isNotEmpty()
  .containsKey(2)
  .doesNotContainKeys(10)
  .contains(entry(2, "a"));
```

Supports:

* Key checks
* Value checks
* Entry validation

---

## 7.10 Throwable Assertions

```java
assertThat(ex)
  .hasNoCause()
  .hasMessageEndingWith("c");
```

Supports:

* Message inspection
* Cause validation
* Stacktrace analysis

---

# 8. Custom Assertion Descriptions

You can add a custom message using `as()`:

```java
assertThat(person.getAge())
  .as("%s's age should be equal to 100", person.getName())
  .isEqualTo(100);
```

Failure output:

```
[Alex's age should be equal to 100] expected:<100> but was:<34>
```

This improves:

* Test readability
* Debugging clarity

---

# 9. Java 8 Features

AssertJ integrates smoothly with Java 8 lambdas.

Java 7 style:

```java
assertThat(fellowshipOfTheRing)
  .filteredOn("race", HOBBIT)
  .containsOnly(sam, frodo, pippin, merry);
```

Java 8 style:

```java
assertThat(fellowshipOfTheRing)
  .filteredOn(character -> 
      character.getRace().equals(HOBBIT))
  .containsOnly(sam, frodo, pippin, merry);
```

Benefits:

* Functional filtering
* Cleaner syntax
* More expressive tests

---

# 10. Why AssertJ is Powerful

Compared to older assertion libraries, AssertJ provides:

* Fluent chaining
* Rich error messages
* Strong IDE support
* Type-specific assertions
* Lambda support
* Modular extensibility

---

# 11. Key Takeaways

* All assertions start with `assertThat()`
* Assertions are fluent and chainable
* Each type has specialized assertion methods
* You can customize failure messages
* Java 8 lambdas make filtering powerful
* AssertJ improves readability and maintainability of tests

---

# Final Summary

AssertJ Core provides:

* A rich, fluent assertion API
* Type-specific validation methods
* Detailed failure output
* Excellent IDE auto-completion
* Clean and expressive test code

It is now the de facto standard assertion library in modern Spring and REST API testing projects.

Understanding AssertJ deeply will significantly improve the quality and clarity of your unit and integration tests.

---