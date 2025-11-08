
---

## 🧠 Lesson Notes: The `Arrays` Class in Java

### 1. What is an array?
An array is a collection of homogeneous elements (same data type) stored in contiguous memory locations.
The **`Arrays` class** is a utility class in the `java.util` package that provides **static methods** for common operations on arrays — such as sorting, searching, comparing, and converting arrays to strings.

**Package:**

```java
import java.util.Arrays;
```
---

### 2. Creating Arrays

Arrays can be created using either **array literals** or the **`new`** keyword.

```java
int[] numbers = {5, 2, 8, 1, 3};
String[] names = new String[]{"Alice", "Bob", "Charlie"};
```
Alternatively, 
```java
int[] numbers = new int[5];
```
This creates an array that can hold 5 integers, each accessible through indices `0` to `4`.
---

### **3. Array Declaration Methods**

**Approach 1: Fixed-size declaration**

```java
int[] marks = new int[5];
marks[0] = 90;
marks[1] = 85;
```

**Approach 2: Dynamic declaration**

```java
int[] marks = {90, 85, 70, 60, 95};
```

Choose this when you already know the data values.

---

### **4. Accessing and Modifying Array Elements**

Each element is accessed using its **index**:

```java
System.out.println(marks[0]);  // Prints 90
marks[2] = 75;                 // Updates third element
```

If you attempt to access an index outside the array bounds, Java throws:

```
ArrayIndexOutOfBoundsException
```
---

### **5. Finding Array Length**

Use the `length` property:

```java
System.out.println("Array size: " + marks.length);
```

---

### **6. Looping Through Arrays**

**Using a traditional for loop:**

```java
for (int i = 0; i < marks.length; i++) {
    System.out.println(marks[i]);
}
```

**Using an enhanced for loop (for-each):**

```java
for (int score : marks) {
    System.out.println(score);
}
```

This approach eliminates index management and is more concise.

---

### **7. Two-Dimensional Arrays**

A two-dimensional array represents data in **rows and columns**:

```java
int[][] matrix = {
    {10, 20},
    {30, 40},
    {50, 60}
};
System.out.println(matrix[2][1]); // Prints 60
```

**Finding dimensions:**

```java
System.out.println(matrix.length);       // Number of rows
System.out.println(matrix[0].length);    // Number of columns
```

**Nested loops to traverse:**

```java
for (int i = 0; i < matrix.length; i++) {
    for (int j = 0; j < matrix[i].length; j++) {
        System.out.print(matrix[i][j] + " ");
    }
    System.out.println();
}
```

---

---

### 8. Important Methods of the `Arrays` Class

| **Method**           | **Description**                                                | **Example Code**                                                                                                                           |
|----------------------| -------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| **`sort()`**         | Sorts the array in ascending order.                            | `java int[] arr = {5, 1, 4, 2}; Arrays.sort(arr); System.out.println(Arrays.toString(arr)); // [1, 2, 4, 5] `                              |
| **`binarySearch()`** | Searches for a key in a **sorted** array using binary search.  | `java int[] arr = {1, 2, 4, 5}; int index = Arrays.binarySearch(arr, 4); System.out.println("Found at index: " + index); `                 |
| **`equals()`**       | Compares two arrays for equality (checks elements and order).  | `java int[] a = {1, 2, 3}; int[] b = {1, 2, 3}; System.out.println(Arrays.equals(a, b)); // true `                                         |
| **`copyOf()`**       | Copies the specified array to a new array of the given length. | `java int[] a = {1, 2, 3}; int[] copy = Arrays.copyOf(a, 5); System.out.println(Arrays.toString(copy)); // [1, 2, 3, 0, 0] `               |
| **`copyOfRange()`**  | Copies a specific range from the array.                        | `java int[] a = {10, 20, 30, 40, 50}; int[] sub = Arrays.copyOfRange(a, 1, 4); System.out.println(Arrays.toString(sub)); // [20, 30, 40] ` |
| **`fill()`**         | Fills an array with a specific value.                          | `java int[] marks = new int[5]; Arrays.fill(marks, 100); System.out.println(Arrays.toString(marks)); // [100, 100, 100, 100, 100] `        |
| **`toString()`**     | Converts an array into a readable string form.                 | `java String[] fruits = {"Apple", "Banana", "Cherry"}; System.out.println(Arrays.toString(fruits)); `                                      |
| **`asList()`**       | Converts an array into a fixed-size `List`.                    | `java String[] arr = {"A", "B", "C"}; List<String> list = Arrays.asList(arr); System.out.println(list); // [A, B, C] `                     |
| **`deepEquals()`**   | Compares **nested arrays** for equality.                       | `java int[][] arr1 = {{1, 2}, {3, 4}}; int[][] arr2 = {{1, 2}, {3, 4}}; System.out.println(Arrays.deepEquals(arr1, arr2)); // true `       |
| **`deepToString()`** | Returns a deep string representation of nested arrays.         | `java int[][] arr = {{1, 2}, {3, 4}}; System.out.println(Arrays.deepToString(arr)); // [[1, 2], [3, 4]] `                                  |
| **`setAll()`**       | Assigns values to each element using a lambda expression.      | `java int[] nums = new int[5]; Arrays.setAll(nums, i -> i * i); System.out.println(Arrays.toString(nums)); // [0, 1, 4, 9, 16] `           |
| **`parallelSort()`** | Uses parallel threads to sort large arrays faster.             | `java int[] bigArray = {9, 3, 6, 1, 8, 2}; Arrays.parallelSort(bigArray); System.out.println(Arrays.toString(bigArray)); `                 |

---

### 9. Example Program: Using `Arrays` Methods

```java
import java.util.Arrays;

public class ArrayExample {
    public static void main(String[] args) {
        int[] numbers = {5, 2, 8, 1, 3};

        // Sort the array
        Arrays.sort(numbers);
        System.out.println("Sorted: " + Arrays.toString(numbers));

        // Search for a number
        int index = Arrays.binarySearch(numbers, 3);
        System.out.println("3 found at index: " + index);

        // Copy elements
        int[] copy = Arrays.copyOf(numbers, 7);
        System.out.println("Copy: " + Arrays.toString(copy));

        // Fill an array
        Arrays.fill(copy, 10);
        System.out.println("Filled: " + Arrays.toString(copy));
    }
}
```

---

### 10. Summary

* The `Arrays` class is part of `java.util` and helps with **sorting**, **searching**, **copying**, and **comparing** arrays.
* Most methods are **static**, so you can call them directly using `Arrays.methodName()`.
* Methods like `deepEquals()` and `deepToString()` are useful for **multidimensional arrays**.
* Prefer `Arrays.asList()` for converting to a list, but remember the list is **fixed-size**.

---