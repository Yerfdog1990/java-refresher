

# **Lesson Notes: Variable Types and Primitive Data Types**

---

## **1. What Are Variable Types?**

In Java, variables can hold different kinds of data.
To specify the kind of data a variable can store, Java provides **data types**.

The most basic types are called **primitive data types**, which define the kind of values a variable can hold — such as numbers, characters, or true/false values.

---

## **2. Categories of Primitive Data Types**

| **Category**             | **Data Types**                 | **Description**                             |
| ------------------------ | ------------------------------ | ------------------------------------------- |
| **Integer types**        | `byte`, `short`, `int`, `long` | Used for whole numbers (no decimals)        |
| **Floating-point types** | `float`, `double`              | Used for numbers with decimals              |
| **Character type**       | `char`                         | Used for single characters                  |
| **Boolean type**         | `boolean`                      | Used for logical values (`true` or `false`) |

---

## **3. Integer Types**

Used for storing **whole numbers** (both positive and negative).

| **Type** | **Size** | **Range**                       | **Example**            |
| -------- | -------- | ------------------------------- | ---------------------- |
| `byte`   | 1 byte   | -128 to 127                     | `byte a = 100;`        |
| `short`  | 2 bytes  | -32,768 to 32,767               | `short b = 32000;`     |
| `int`    | 4 bytes  | -2,147,483,648 to 2,147,483,647 | `int c = 100000;`      |
| `long`   | 8 bytes  | Very large range                | `long d = 123456789L;` |

> **Note:** Use an uppercase `L` at the end of long values to indicate the `long` type.

---

## **4. Floating-Point Types**

Used for **numbers with decimal points**.

| **Type** | **Size** | **Description**                             | **Example**                |
| -------- | -------- | ------------------------------------------- | -------------------------- |
| `float`  | 4 bytes  | Less precision                              | `float price = 10.25f;`    |
| `double` | 8 bytes  | More precision (recommended for most cases) | `double salary = 1200.50;` |

> **Important:** Always add an **‘f’** or **‘F’** to a `float` value to tell Java that it’s a floating-point number.

---

## **5. Character Type**

Used for **single characters**, such as letters or symbols.

| **Type** | **Size** | **Description**                                           | **Example**         |
| -------- | -------- | --------------------------------------------------------- | ------------------- |
| `char`   | 2 bytes  | Stores one character, enclosed in **single quotes (' ')** | `char grade = 'A';` |

---

## **6. Boolean Type**

Used to store **logical values** — either `true` or `false`.

| **Type**  | **Size** | **Description**                     | **Example**                 |
| --------- | -------- | ----------------------------------- | --------------------------- |
| `boolean` | 1 bit    | Holds only `true` or `false` values | `boolean isJavaFun = true;` |

---

## **7. Declaring Variables**

To **create** a variable, you must declare its **type** and **name**.

**Syntax:**

```java
type name;
```

**Examples:**

| **Command**      | **Explanation**                                    |
| ---------------- | -------------------------------------------------- |
| `String s;`      | Creates a text variable `s` that can store text    |
| `int x;`         | Creates an integer variable `x`                    |
| `int a, b, c;`   | Creates three integer variables: `a`, `b`, and `c` |
| `double salary;` | Creates a decimal number variable named `salary`   |

---

### **Important Notes**

* You **cannot** create two variables with the same name in the same method.
* Variable names **cannot** include spaces or special characters like `+` or `-`.
* Use **Latin letters and digits only**.
* Java is **case-sensitive** (`int a;` ≠ `Int a;`).

---

## **8. Declaring and Assigning in One Step**

You can **create and assign** a value to a variable in one line — this is called **initialization**.

| **Compact Code**          | **Equivalent Long Code**     |
| ------------------------- | ---------------------------- |
| `int a = 5;`              | `int a; a = 5;`              |
| `int b = 6;`              | `int b; b = 6;`              |
| `String s = "I'm Amigo";` | `String s; s = "I'm Amigo";` |

This compact form makes your code cleaner and easier to read.

---

## **9. The `int` Type**

The `int` data type stores **whole numbers** and supports arithmetic operations like addition, subtraction, multiplication, and division.

### **Examples:**

```java
int x = 1;
int y = x * 2;
int z = 5 * y * y + 2 * y + 3;
```

**Explanation:**

* `x` = 1
* `y` = 2
* `z` = 5×2×2 + 2×2 + 3 = 27

Another example:

```java
int a = 64;
int b = a / 8;
int c = b / 4;
int d = c * 3;
```

**Result:**

* `a = 64`
* `b = 8`
* `c = 2`
* `d = 6`

---

## **10. The `String` Type**

The `String` type is **not primitive** — it’s a **reference type** used to store text (a sequence of characters).

### **Examples:**

```java
String s = "Amigo";
String numberString = "123";
String codeName = "Bond 007";
```

**Explanation:**

* `s` contains `"Amigo"`
* `numberString` contains `"123"`
* `codeName` contains `"Bond 007"`

---

### **Joining Strings (Concatenation)**

You can join (concatenate) strings using the `+` operator.

```java
String s = "Amigo" + " is the best";
```

Now `s` contains `"Amigo is the best"`.

You can also combine strings with numbers:

```java
int x = 333;
String s = "Amigo" + x;
```

`s` now contains `"Amigo333"` — the number is automatically converted to text before joining.

---

## **11. Displaying Variables on the Screen**

To display variables or text on the screen, use:

```java
System.out.println();
```

**Examples:**

| **Code**                                                     | **Output** |
| ------------------------------------------------------------ | ---------- |
| `System.out.println("Amigo");`                               | Amigo      |
| `System.out.println("Ami" + "go");`                          | Amigo      |
| `java<br>String s = "Amigo";<br>System.out.println(s);`      | Amigo      |
| `java<br>String s = "Am";<br>System.out.println(s + "igo");` | Amigo      |

---

## **12. Summary**

| **Concept**              | **Description**                                                   |
| ------------------------ | ----------------------------------------------------------------- |
| **Primitive Data Types** | Basic data types built into Java (`int`, `char`, `boolean`, etc.) |
| **Integer Types**        | Store whole numbers                                               |
| **Floating-Point Types** | Store decimal numbers                                             |
| **Character Type**       | Stores single characters (`'A'`, `'b'`)                           |
| **Boolean Type**         | Stores `true` or `false` values                                   |
| **String Type**          | Stores text; supports concatenation using `+`                     |
| **Variable Declaration** | `type name;`                                                      |
| **Initialization**       | `type name = value;`                                              |
| **Display Output**       | `System.out.println(variableName);`                               |

---

### **Key Takeaways**

* Java has **8 primitive data types** and **1 commonly used reference type (`String`)**.
* Every variable has a **type**, **name**, and **value**.
* You can perform arithmetic with numeric types and concatenation with strings.
* Always use meaningful and unique variable names.
* Variables must be declared before use and follow Java’s **case sensitivity** rules.

---
