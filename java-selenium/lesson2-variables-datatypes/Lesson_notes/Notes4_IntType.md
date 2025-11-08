
# **Lesson Notes: The int Type — Whole Numbers**

---

## **1. The int Type**

The **`int`** type in Java is used to store **whole numbers** — numbers without decimals.

The word **“int”** is short for **integer**, which means a complete number (positive, negative, or zero).

### **Range of Values**

An `int` variable can store values from **–2,147,483,648** to **+2,147,483,647**.

That’s roughly **–2 billion to +2 billion.**

**Example:**

```java
int age = 25;
int year = 2025;
int temperature = -10;
```

---

### **Interesting Fact: Why This Range?**

The range comes from how the computer stores data in memory.

* The `int` type uses **4 bytes** of memory.
* Each **byte** = **8 bits**, and each bit can store a **0** or **1**.
* Therefore, an `int` has **32 bits total**, which means it can represent
  **2³² = 4,294,967,296** distinct values.
* Half of them are used for **negative numbers**, and half for **positive** ones.
  → Range = **–2,147,483,648 to +2,147,483,647**

---

## **2. Creating an int Variable**

To create an integer variable, use the syntax:

```java
int variableName;
```

**Examples:**

| **Statement**      | **Description**                                   |
| ------------------ | ------------------------------------------------- |
| `int x;`           | Creates a variable `x` that can store an integer. |
| `int count;`       | Creates a variable `count`.                       |
| `int currentYear;` | Creates a variable `currentYear`.                 |

**Important Rules:**

* Java is **case-sensitive**.
  `int color` and `int Color` are two different variables.
* The keyword **`int`** must always be in **lowercase**.
  Writing `Int` or `INT` will cause an error.

---

## **3. Shorthand for Creating Multiple Variables**

If you need several `int` variables, you can declare them all in one line:

```java
int a, b, c;
```

This saves space and makes your code cleaner.

### **Examples:**

| **Long form**                   | **Shorthand**           |
| ------------------------------- | ----------------------- |
| `int x; int y; int z;`          | `int x, y, z;`          |
| `int day; int month; int year;` | `int day, month, year;` |

---

## **4. Assigning Values to int Variables**

To store a value in an `int` variable, use the **assignment operator** `=`.

```java
variableName = value;
```

**Examples:**

```java
int a;
a = 5;

int b;
b = 2 * 1000 * 1000 * 1000; // 2 billion

int c;
c = -10000000; // negative ten million
```

⚠️ **Be careful with large numbers!**

This code will **not compile**:

```java
int d;
d = 3000000000; // ❌ Error: number too large for int
```

`3,000,000,000` exceeds the maximum value of an `int` (2,147,483,647).

---

## **5. Shorthand for Creating and Initializing Variables**

You can declare and assign a value in a single line — this is called **initialization**.

### **Syntax:**

```java
int variableName = value;
```

**Examples:**

```java
int a = 5;
int b = 2 * 1000 * 1000 * 1000; // 2 billion
int c = -10000000;
```

⚠️ Again, this will cause an error:

```java
int d = 3000000000; // ❌ too large for int
```

---

## **6. Declaring and Initializing Multiple Variables**

You can also initialize several variables of the same type on one line:

```java
int a = 5, b = 10, c = a + b;
```

**Explanation:**

* `a` = 5
* `b` = 10
* `c` = 15 (because `a + b = 15`)

This method keeps code compact and readable.

---

## **7. Example Program**

```java
public class IntExample {
    public static void main(String[] args) {
        int age = 25;
        int birthYear = 2000;
        int currentYear = 2025;
        int yearsLived = currentYear - birthYear;

        System.out.println("Age: " + age);
        System.out.println("Birth Year: " + birthYear);
        System.out.println("Years Lived: " + yearsLived);
    }
}
```

**Output:**

```
Age: 25
Birth Year: 2000
Years Lived: 25
```

---

## **8. Summary**

| **Concept**                      | **Description**                       | **Example**      |
| -------------------------------- | ------------------------------------- | ---------------- |
| **Type keyword**                 | Declares a variable for whole numbers | `int x;`         |
| **Range**                        | –2,147,483,648 to +2,147,483,647      | `int num = 100;` |
| **Memory used**                  | 4 bytes (32 bits)                     |                  |
| **Assigning values**             | Use `=` to store data                 | `x = 10;`        |
| **Declaration + Initialization** | Create and assign in one line         | `int a = 5;`     |
| **Multiple declarations**        | Combine same-type variables           | `int a, b, c;`   |

---

### **Key Takeaways**

* Use **`int`** for storing whole numbers.
* The valid range is **–2,147,483,648** to **+2,147,483,647**.
* Java is **case-sensitive** — always write `int` in lowercase.
* You can declare multiple `int` variables in one line.
* Avoid exceeding the range — large numbers may require **`long`** instead.

---
