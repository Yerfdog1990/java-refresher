
# **Lesson Notes: Java Variables and Data Types**

---

## **1. Java Variables and Data Types**

The instructor began by explaining that variables and data types are the foundation of Java programming. Every Java program manipulates **data**, and variables serve as **containers** to store that data.

### **What is a Variable?**

* A **variable** is a name given to a memory location that stores data.
* It acts as a container to hold different kinds of information such as numbers, characters, or strings.
* The value of a variable **can change** during program execution — hence the term *variable*.

**Example:**

```java
int x = 100;
```

Here,

* `x` → variable name
* `int` → data type
* `100` → value assigned to the variable

You can change `x` later:

```java
x = 200;
```

Now `x` holds `200` instead of `100`.

---

## **2. Declaring Variables in Java**

Variables in Java must be **declared with a data type** before use.
The syntax for declaring a variable is:

```java
dataType variableName = value;
```

### **Examples:**

```java
int age = 30;           // integer type
float price = 10.25f;   // decimal number
String name = "John";   // string type
char grade = 'A';       // single character
boolean isPassed = true;// boolean type
```

### **Rules and Concepts:**

* Each statement ends with a semicolon (`;`).
* Declaration and assignment can be done separately:

  ```java
  int a;
  a = 100;
  ```
* Or combined in one line:

  ```java
  int a = 100;
  ```
* Once a variable is declared with a data type, you **should not redeclare** it using the same name in the same scope.

---

## **3. Primitive Data Types**

Java provides several **primitive data types** to define the kind of data a variable can hold.

### **Categories of Primitive Data Types**

| Category                 | Data Types                     | Description                             |
| ------------------------ | ------------------------------ | --------------------------------------- |
| **Integer types**        | `byte`, `short`, `int`, `long` | Used for whole numbers without decimals |
| **Floating-point types** | `float`, `double`              | Used for numbers with decimals          |
| **Character type**       | `char`                         | Used for single characters              |
| **Boolean type**         | `boolean`                      | Used for true/false values              |

---

### **Integer Types**

| Type    | Size    | Range                           | Example                |
| ------- | ------- | ------------------------------- | ---------------------- |
| `byte`  | 1 byte  | -128 to 127                     | `byte a = 100;`        |
| `short` | 2 bytes | -32,768 to 32,767               | `short b = 32000;`     |
| `int`   | 4 bytes | -2,147,483,648 to 2,147,483,647 | `int c = 100000;`      |
| `long`  | 8 bytes | Very large range                | `long d = 123456789L;` |

---

### **Floating-Point Types**

| Type     | Size    | Description                               | Example                    |
| -------- | ------- | ----------------------------------------- | -------------------------- |
| `float`  | 4 bytes | Used for decimal numbers (less precision) | `float price = 10.25f;`    |
| `double` | 8 bytes | Used for decimal numbers (more precision) | `double salary = 1200.50;` |

---

### **Character Type**

| Type   | Size    | Description                                        | Example             |
| ------ | ------- | -------------------------------------------------- | ------------------- |
| `char` | 2 bytes | Holds a single character enclosed in single quotes | `char grade = 'A';` |

---

### **Boolean Type**

| Type      | Size  | Description                         | Example                     |
| --------- | ----- | ----------------------------------- | --------------------------- |
| `boolean` | 1 bit | Holds only `true` or `false` values | `boolean isJavaFun = true;` |

---

## **4. Variable Naming Conventions**

When naming variables, Java follows strict naming conventions and rules to ensure code readability and maintainability.

### **Rules for Naming Variables**

* Must **start with a letter**, `$`, or `_` (cannot start with a number).
* Cannot contain spaces.
* Cannot use **Java keywords** (e.g., `class`, `public`, `int`).
* Java is **case-sensitive** (`Age` and `age` are different).
* Must be meaningful and descriptive.

### **Best Practices**

| Good Practice                        | Example                                      |
| ------------------------------------ | -------------------------------------------- |
| Use **camelCase** for variable names | `itemPrice`, `studentName`                   |
| Use **UPPER_CASE** for constants     | `PI = 3.1416;`                               |
| Make names meaningful                | `int studentCount = 25;`                     |
| Avoid short or unclear names         | ❌ `int x = 5;` ✅ `int numberOfStudents = 5;` |

---

## **5. Examples Demonstrated in Class**

### **Declaring and Printing Variables**

```java
public class VariablesDemo {
    public static void main(String[] args) {
        int a = 100;
        int b = 200;
        int c = 300;

        System.out.println("The value of a is: " + a);
        System.out.println("The value of b is: " + b);
        System.out.println("The value of c is: " + c);
    }
}
```

**Output:**

```
The value of a is: 100
The value of b is: 200
The value of c is: 300
```

### **Concatenation Example**

Combining text and variables using the `+` operator:

```java
System.out.println("Total: " + (a + b + c));
```

### **Multiple Variable Declaration**

You can declare multiple variables in one line if they share the same data type:

```java
int x = 10, y = 20, z = 30;
```

---

## **6. Summary**

By the end of this lesson, students understood:

* The concept and purpose of **variables** in Java.
* How to **declare, assign, and modify** variables.
* Different **primitive data types** and their ranges.
* The importance of **variable naming conventions** for readability and maintainability.
* How to print and concatenate variable values in console output.
