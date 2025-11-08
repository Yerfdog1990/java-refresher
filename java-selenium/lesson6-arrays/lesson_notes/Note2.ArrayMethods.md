
---

# 📚 Java Arrays Class: Lesson Notes

The **Arrays class** in Java (`java.util.Arrays`) is a **utility class** that provides powerful static methods for performing operations such as **sorting**, **searching**, **comparing**, **filling**, and **copying arrays**.

It’s part of the **Java Collections Framework** utilities, designed to make array handling easier and more efficient.

---

## 🌟 Key Features of the Arrays Class

1. **Utility Class**

    * **Definition:** All methods in the `Arrays` class are **static**, meaning they can be called directly using the class name (e.g., `Arrays.sort(array)`).
    * **Purpose:** Offers built-in support for common array operations like sorting, searching, and comparison.
    * **Import Required:**

      ```java
      import java.util.Arrays;
      ```

2. **Supports Both Primitive and Object Arrays**

    * Works for arrays of **primitives** (`int[]`, `double[]`, etc.) and **objects** (`String[]`, `Integer[]`, etc.).

3. **Not for Creating Arrays**

    * The class **does not create** arrays; it only provides **methods to manipulate** them.

---

## 🛠️ Commonly Used Arrays Methods

The `Arrays` class methods can be grouped into several categories:

1. **Printing and Conversion**
2. **Sorting**
3. **Searching**
4. **Comparing and Filling**
5. **Copying and Resizing**

---

## 1️⃣ Printing and Conversion Methods

| Method                | Description                                                                                     |
| :-------------------- | :---------------------------------------------------------------------------------------------- |
| `toString(array)`     | Returns a **readable string** representation of a one-dimensional array.                        |
| `deepToString(array)` | Returns a string representation of **multi-dimensional** arrays.                                |
| `asList(T... a)`      | Converts an array to a **fixed-size List** backed by the array (modifications reflect in both). |

### Example

```java
import java.util.Arrays;
import java.util.List;

public class ArrayPrinting {
    public static void main(String[] args) {
        int[] intArray = { 10, 20, 30, 40 };
        String[] strArray = { "apple", "banana", "cherry" };

        // 1. toString() for 1D array
        System.out.println("Int Array: " + Arrays.toString(intArray)); 
        // Output: [10, 20, 30, 40]

        // 2. deepToString() for 2D array
        int[][] matrix = { {1, 2}, {3, 4} };
        System.out.println("2D Array: " + Arrays.deepToString(matrix)); 
        // Output: [[1, 2], [3, 4]]

        // 3. asList() – convert array to list
        List<String> fruits = Arrays.asList(strArray);
        System.out.println("List: " + fruits);
    }
}
```

---

## 2️⃣ Sorting Methods

| Method                            | Description                                                                   |
| :-------------------------------- | :---------------------------------------------------------------------------- |
| `sort(array)`                     | Sorts the array into **ascending order**.                                     |
| `sort(array, fromIndex, toIndex)` | Sorts a **specific range** of an array.                                       |
| `parallelSort(array)`             | Sorts the array using **parallel processing** (faster on multi-core systems). |

### Example

```java
import java.util.Arrays;

public class ArraySorting {
    public static void main(String[] args) {
        int[] numbers = { 9, 2, 5, 1, 8, 3 };

        // 1. Sort the whole array
        Arrays.sort(numbers);
        System.out.println("Sorted: " + Arrays.toString(numbers));
        // Output: [1, 2, 3, 5, 8, 9]

        // 2. Sort only part of the array
        int[] partial = { 9, 2, 5, 1, 8, 3 };
        Arrays.sort(partial, 1, 4);  // Sorts elements 1–3
        System.out.println("Partial Sort: " + Arrays.toString(partial));
        // Output: [9, 1, 2, 5, 8, 3]

        // 3. Parallel Sort
        int[] bigArray = { 10, 4, 7, 3, 9, 1 };
        Arrays.parallelSort(bigArray);
        System.out.println("Parallel Sorted: " + Arrays.toString(bigArray));
    }
}
```

---

## 3️⃣ Searching Methods

| Method                     | Description                                                        |
| :------------------------- | :----------------------------------------------------------------- |
| `binarySearch(array, key)` | Searches a **sorted array** for the given key using binary search. |
| **Returns:**               | The index of the key if found; otherwise `-(insertionPoint) - 1`.  |

> ⚠️ **Important:** The array **must be sorted** before using `binarySearch()`, or results will be undefined.

### Example

```java
import java.util.Arrays;

public class ArraySearching {
    public static void main(String[] args) {
        int[] sorted = { 1, 3, 5, 7, 9 };

        int found = Arrays.binarySearch(sorted, 5);
        System.out.println("Index of 5: " + found); // Output: 2

        int notFound = Arrays.binarySearch(sorted, 6);
        System.out.println("Search for 6: " + notFound); // Output: -4
    }
}
```

---

## 4️⃣ Comparing and Filling Methods

| Method                         | Description                                                               |
| :----------------------------- | :------------------------------------------------------------------------ |
| `equals(array1, array2)`       | Returns `true` if arrays contain the **same elements in the same order**. |
| `deepEquals(array1, array2)`   | Checks **nested arrays** for deep equality.                               |
| `fill(array, value)`           | Assigns the specified value to **every element**.                         |
| `fill(array, from, to, value)` | Assigns a value to a **range** of elements.                               |

### Example

```java
import java.util.Arrays;

public class ArrayComparingAndFilling {
    public static void main(String[] args) {
        int[] a = { 10, 20 };
        int[] b = { 10, 20 };
        int[] c = { 20, 10 };

        // 1. equals()
        System.out.println("a equals b: " + Arrays.equals(a, b)); // true
        System.out.println("a equals c: " + Arrays.equals(a, c)); // false

        // 2. fill()
        int[] nums = new int[5];
        Arrays.fill(nums, 100);
        System.out.println("After fill: " + Arrays.toString(nums)); // [100, 100, 100, 100, 100]

        Arrays.fill(nums, 1, 3, 50);
        System.out.println("After partial fill: " + Arrays.toString(nums)); // [100, 50, 50, 100, 100]
    }
}
```

---

## 5️⃣ Copying and Resizing Methods

| Method                         | Description                                                      |
| :----------------------------- | :--------------------------------------------------------------- |
| `copyOf(array, newLength)`     | Copies the array into a **new array** with the specified length. |
| `copyOfRange(array, from, to)` | Copies a **specific range** from one array into a new one.       |

### Example

```java
import java.util.Arrays;

public class ArrayCopying {
    public static void main(String[] args) {
        int[] original = {10, 20, 30, 40, 50};

        // 1. Copy and resize
        int[] newCopy = Arrays.copyOf(original, 3);
        System.out.println("Copy of length 3: " + Arrays.toString(newCopy)); 
        // Output: [10, 20, 30]

        // 2. Copy range
        int[] rangeCopy = Arrays.copyOfRange(original, 2, 5);
        System.out.println("Copy of range 2–5: " + Arrays.toString(rangeCopy)); 
        // Output: [30, 40, 50]
    }
}
```

---

## 6️⃣ Advanced Parallel Operations (Java 8+)

| Method                             | Description                                                                       |
| :--------------------------------- | :-------------------------------------------------------------------------------- |
| `parallelPrefix(array, op)`        | Performs a **cumulative computation** (prefix sum) using the specified operation. |
| `setAll(array, generator)`         | Initializes all elements using a **function of the index**.                       |
| `parallelSetAll(array, generator)` | Same as `setAll()` but uses **parallel threads** for faster processing.           |

### Example

```java
import java.util.Arrays;
import java.util.function.IntBinaryOperator;

public class ArrayParallelPrefix {
    public static void main(String[] args) {
        int[] data = {1, 2, 3, 4, 5};

        // Perform prefix sum
        Arrays.parallelPrefix(data, (x, y) -> x + y);
        System.out.println("Parallel Prefix: " + Arrays.toString(data)); 
        // Output: [1, 3, 6, 10, 15]
    }
}
```

---

## 7️⃣ Stream Integration (Java 8+)

| Method               | Description                                                             |
| :------------------- | :---------------------------------------------------------------------- |
| `stream(array)`      | Converts an array to a sequential **Stream** for functional operations. |
| `spliterator(array)` | Returns a **Spliterator** for use with streams.                         |

### Example

```java
import java.util.Arrays;

public class ArrayStream {
    public static void main(String[] args) {
        int[] numbers = {1, 2, 3, 4, 5};
        int sum = Arrays.stream(numbers)
                        .filter(n -> n % 2 == 0)
                        .sum();

        System.out.println("Sum of even numbers: " + sum); // Output: 6
    }
}
```

---

## ✅ Summary

| Operation         | Method Example                     |
| :---------------- | :--------------------------------- |
| Print Array       | `Arrays.toString(array)`           |
| Sort Array        | `Arrays.sort(array)`               |
| Search Element    | `Arrays.binarySearch(array, key)`  |
| Compare Arrays    | `Arrays.equals(a1, a2)`            |
| Fill Array        | `Arrays.fill(array, val)`          |
| Copy Array        | `Arrays.copyOf(array, len)`        |
| Convert to List   | `Arrays.asList(array)`             |
| Parallel Compute  | `Arrays.parallelPrefix(array, op)` |
| Stream Conversion | `Arrays.stream(array)`             |

---

