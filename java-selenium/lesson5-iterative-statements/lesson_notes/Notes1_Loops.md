
# **Java Loops and Jump Statements**

## **1. Introduction to Loops**

A **loop** (or **iterative statement**) allows a set of instructions to run **repeatedly** until a certain condition is met.
Loops make code **shorter**, **more efficient**, and **easier to maintain**.

---

## **2. Components of a Loop**

Every loop (whether `for`, `while`, or `do-while`) includes three essential parts:

| **Component**           | **Purpose**                                          | **Example**  |
| ----------------------- | ---------------------------------------------------- | ------------ |
| **Initialization**      | Sets the starting value of the loop control variable | `int i = 1;` |
| **Condition**           | Tests whether the loop should continue running       | `i <= 5`     |
| **Increment/Decrement** | Updates the variable after each iteration            | `i++`        |

> 🔁 The loop runs **as long as** the condition is true.

---

## **3. Types of Loops in Java**

### **A. while Loop (Entry-Controlled Loop)**

* The condition is checked **before** executing the loop body.
* If the condition is false initially, the loop body will **not execute** at all.

**Syntax:**

```java
// Initialization
while (condition) {
    // Statements
    // Increment or Decrement
}
```

**Example:**

```java
int i = 1;
while (i <= 5) {
    System.out.println("Value: " + i);
    i++;
}
```

**Output:**

```
Value: 1
Value: 2
Value: 3
Value: 4
Value: 5
```

---

### **B. do-while Loop (Exit-Controlled Loop)**

* The loop body runs **at least once**, even if the condition is false.
* The condition is checked **after** the loop executes.

**Syntax:**

```java
// Initialization
do {
    // Statements
    // Increment or Decrement
} while (condition);  // Note the semicolon
```

**Example:**

```java
int count = 1;
do {
    System.out.println("Count: " + count);
    count++;
} while (count <= 5);
```

**Output:**

```
Count: 1
Count: 2
Count: 3
Count: 4
Count: 5
```

---

### **C. for Loop**

* Combines initialization, condition, and update **in a single line**.
* Most commonly used for loops with a **known number of iterations**.

**Syntax:**

```java
for (initialization; condition; increment/decrement) {
    // Statements
}
```

**Example:**

```java
for (int j = 1; j <= 5; j++) {
    System.out.println("Number: " + j);
}
```

**Output:**

```
Number: 1
Number: 2
Number: 3
Number: 4
Number: 5
```

---

### **D. Enhanced for Loop (for-each Loop)**

* Used for iterating over **arrays** or **collections** (like `ArrayList`).
* Removes the need for a loop counter variable.

**Syntax:**

```java
for (DataType variable : arrayOrCollection) {
    // Use variable
}
```

**Example:**

```java
int[] numbers = {10, 20, 30};
for (int num : numbers) {
    System.out.println("Element: " + num);
}
```

**Output:**

```
Element: 10
Element: 20
Element: 30
```

---

## **4. Jump Statements in Loops**

Jump statements change the normal flow of a loop:

| **Statement**  | **Description**                                            | **Example**             |
| -------------- | ---------------------------------------------------------- | ----------------------- |
| **`break`**    | Immediately **exits** the loop when a condition is met.    | `if (i == 3) break;`    |
| **`continue`** | **Skips** the current iteration and moves to the next one. | `if (i == 3) continue;` |

**Example using both:**

```java
for (int i = 1; i <= 5; i++) {
    if (i == 3) continue;  // Skip 3
    if (i == 5) break;     // Stop when i == 5
    System.out.println(i);
}
```

**Output:**

```
1
2
4
```

---

## **✅ Summary**

| **Loop Type** | **Condition Check** | **Executes at Least Once?** | **Common Use**                           |
| ------------- | ------------------- | --------------------------- | ---------------------------------------- |
| `while`       | Before loop body    | ❌                           | When the number of iterations is unknown |
| `do-while`    | After loop body     | ✅                           | When the loop should run at least once   |
| `for`         | Before loop body    | ❌                           | When the number of iterations is known   |
| `for-each`    | Automatic iteration | ✅                           | Iterating over arrays or collections     |

---
