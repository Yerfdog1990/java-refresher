
# **Working with Java Conditional Statements**

## **1. Introduction to Control Statements**

### **Sequential Execution**

* In Java, statements normally execute **from top to bottom** — one after another.
* This is called **sequential execution**.

### **Why We Need Control Statements**

* Control statements let us **control the flow** of a program.
* They help us:

    * Execute specific blocks of code **only if a condition is true**.
    * Repeat a block of code using **loops**.

### **Types of Control Statements**

| **Category**                      | **Examples**               | **Purpose**                      |
| --------------------------------- | -------------------------- | -------------------------------- |
| **Decision-Making (Conditional)** | `if`, `if-else`, `switch`  | Execute code based on conditions |
| **Looping**                       | `for`, `while`, `do-while` | Repeat code multiple times       |

---

## **2. The if Statement Family**

Conditional statements allow Java programs to **make decisions at runtime** — that is, to choose different paths depending on certain conditions.

### **A. Simple if Statement**

* Executes code **only if** the condition is true.

**Syntax:**

```java
if (condition) {
    // This code runs ONLY if the condition is true
}
// Program continues here regardless of condition
```

---

### **B. if-else Statement**

* Offers **two possible paths**:

    * Runs one block when the condition is **true**
    * Runs another when it’s **false**

**Syntax:**

```java
if (condition) {
    // Runs if condition is true
} else {
    // Runs if condition is false
}
```

---

### **C. if-else if-else Ladder**

* Used when there are **multiple conditions** to check.
* As soon as one condition is true, the rest are **skipped**.

**Example:** Checking whether a number is positive, negative, or zero.

```java
if (num > 0) {
    System.out.println("Positive");
} else if (num < 0) {
    System.out.println("Negative");
} else {
    System.out.println("Zero");
}
```

---

### **D. Nested if Statements**

* An **if inside another if**.
* Used when one condition depends on another.

**Example:** Finding the largest of three numbers.

```java
int a = 10, b = 20, c = 5;

if (a > b) {
    if (a > c) {
        System.out.println("A is largest");
    } else {
        System.out.println("C is largest");
    }
} else {
    if (b > c) {
        System.out.println("B is largest");
    } else {
        System.out.println("C is largest");
    }
}
```

---

## **3. The switch Statement**

### **Purpose**

* The `switch` statement is used when one variable can take **multiple possible values**.
* It is often cleaner and more organized than multiple `if-else if` statements.

### **Supported Data Types**

* Works with:
  `byte`, `short`, `char`, `int`, `String`, `enum`, and their wrapper types.

### **Syntax Example:**

Convert a **day name** into a **day number**.

```java
String weekName = "Sunday";
int weekNumber;

switch (weekName) {
    case "Sunday":
        weekNumber = 1;
        break;
    case "Monday":
        weekNumber = 2;
        break;
    // ... other days ...
    default:
        weekNumber = 0; // Default if no match found
}
```

---

### **Key Components of `switch`**

| **Component**  | **Description**                       | **Example / Note**                      |
| -------------- | ------------------------------------- | --------------------------------------- |
| **case label** | Represents a possible value to match  | `case "Sunday":`                        |
| **break**      | Ends the case and exits the switch    | Prevents “fall-through”                 |
| **default**    | Optional; runs when no case matches   | Acts like the `else` block in `if-else` |
| **Data types** | Must match the switch variable’s type | `"String"` → `"Sunday"`, integer → `1`  |

---

### ✅ **Summary**

* **if / if-else / switch** help Java make decisions.
* **switch** is best when comparing one variable to many constant values.
* Always use **break** in each case (unless fall-through is intentional).

---

