
---

# **Lesson: Static Keyword in Java**

---

## **Lesson Overview**

The `static` keyword in Java is a **non-access modifier** primarily used for memory management. It allows members—such as variables, methods, blocks, and nested classes—to belong to the class itself rather than to individual instances.
This means that all objects of the class share the same static member, which helps optimize memory and supports class-level behavior.

---

## **Learning Objectives**

By the end of this lesson, learners should be able to:

1. Explain the purpose and characteristics of the `static` keyword in Java.
2. Differentiate between static and non-static members.
3. Use `static` variables, methods, and blocks effectively in code.
4. Demonstrate how `static` works in nested classes.
5. Understand how `static` enables **lazy initialization** in the **Singleton pattern**.
6. Identify advantages and disadvantages of using static members.

---

## **Detailed Explanation**

### **1. What is the static Keyword in Java?**

The `static` keyword in Java defines members that belong to the class itself rather than to objects of that class.
This means that all instances share the same copy of a static member, making it ideal for values or behaviors common to all objects.

Static members are loaded once when the class is first loaded into memory.

#### **The static keyword can be applied to:**

* **Variables** — for class-level shared data.
* **Methods** — callable without creating an object.
* **Blocks** — executed once when the class loads.
* **Nested Classes** — for static inner classes independent of outer class instances.

---

### **2. Characteristics of Static Members**

1. Static variables and methods use memory only once and share it across all instances.
2. Static members are accessed via the **class name**, not through objects.
3. Static members cannot directly access non-static members.
4. Static methods cannot be overridden because they belong to the class, not an object.
5. Static members exist from the time the class loads until the class is unloaded.

---

## **Example 1: Accessing a Static Method Without an Object**

```java
class Geeks {
    static void displayMessage() {
        System.out.println("Static method called without object.");
    }

    public static void main(String[] args) {
        displayMessage();  // Direct call, no object created
    }
}
```

**Output**

```
Static method called without object.
```

---

## **Example 2: Static Block**

Static blocks are executed once when the class is loaded. They are useful for initializing static variables.

```java
class Geeks {
    static int a = 10;
    static int b;

    static {
        System.out.println("Static block initialized.");
        b = a * 4;
    }

    public static void main(String[] args) {
        System.out.println("from main");
        System.out.println("Value of a : " + a);
        System.out.println("Value of b : " + b);
    }
}
```

**Output**

```
Static block initialized.
from main
Value of a : 10
Value of b : 40
```

---

## **Example 3: Static Variables**

Static variables are class-level variables shared among all instances.

```java
class Geeks {
    static int a = initialize();

    static {
        System.out.println("Inside static block");
    }

    static int initialize() {
        System.out.println("Static variable initialized.");
        return 20;
    }

    public static void main(String[] args) {
        System.out.println("Value of a : " + a);
        System.out.println("from main");
    }
}
```

**Output**

```
Static variable initialized.
Inside static block
Value of a : 20
from main
```

---

## **Example 4: Static Methods and Restrictions**

Static methods can only access other static members directly and cannot refer to `this` or `super`.

```java
class Geeks {
    static int a = 10;
    int b = 20;

    static void m1() {
        a = 20;
        System.out.println("from m1");

        // b = 10; // ❌ Compilation error
        // m2();   // ❌ Compilation error
    }

    void m2() {
        System.out.println("from m2");
    }
}
```

---

## **Example 5: Static Variables and Methods in a Practical Scenario**

```java
class Student {
    String name;
    int rollNo;
    static String collegeName;
    static int counter = 0;

    public Student(String name) {
        this.name = name;
        this.rollNo = setRollNo();
    }

    static int setRollNo() {
        counter++;
        return counter;
    }

    static void setCollege(String name) {
        collegeName = name;
    }

    void getStudentInfo() {
        System.out.println("Name: " + this.name);
        System.out.println("Roll No: " + this.rollNo);
        System.out.println("College: " + collegeName);
    }

    public static void main(String[] args) {
        Student.setCollege("XYZ");
        Student s1 = new Student("Geek1");
        Student s2 = new Student("Geek2");

        s1.getStudentInfo();
        s2.getStudentInfo();
    }
}
```

**Output**

```
Name: Geek1
Roll No: 1
College: XYZ
Name: Geek2
Roll No: 2
College: XYZ
```

---

## **Static vs Non-Static Members**

| Feature              | Static Members                  | Non-Static Members            |
| -------------------- | ------------------------------- | ----------------------------- |
| Memory Allocation    | Single copy shared across class | Separate copy for each object |
| Access Method        | Accessed via class name         | Accessed via object           |
| Overriding           | Cannot be overridden            | Can be overridden             |
| Access to this/super | Not allowed                     | Allowed                       |
| Lifetime             | Exists while class is loaded    | Exists while object exists    |

---

## **Static Keyword in Lazy Initialization and Singleton Pattern**

The `static` keyword is fundamental in the **Singleton Design Pattern**, especially for **lazy initialization**, where the object is created only when first requested.
This ensures **one shared instance** across the application while saving memory.

### **Concept Overview**

* A **Singleton** restricts instantiation to one object.
* The `static` keyword stores and manages that single instance at the class level.
* **Lazy initialization** defers object creation until it’s needed, improving performance and efficiency.

### **Code Example: Lazy Singleton**

```java
public class DatabaseConnection {

    // Static variable holds single instance
    private static DatabaseConnection instance;

    private DatabaseConnection() {
        System.out.println("Database Connection established.");
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();  // Lazy initialization
        }
        return instance;
    }

    public void connect() {
        System.out.println("Connected to database.");
    }
}
```

**Memory View**

```
+---------------------------+
|   Class: DatabaseConnection   |
+---------------------------+
| static instance = null    | → Class Area
+---------------------------+

First call → new DatabaseConnection()
→ instance points to object in Heap
→ All later calls return same reference
```

### **Thread-Safe Version**

```java
public static synchronized DatabaseConnection getInstance() {
    if (instance == null) {
        instance = new DatabaseConnection();
    }
    return instance;
}
```

---

### **Diagram: Static Singleton Flow**

```
        +-----------------------------+
        | Class: DatabaseConnection   |
        +-----------------------------+
        | static instance = null      |  → stored in Class Area
        +-----------------------------+

                │
                ▼
        ┌──────────────────────┐
        │ getInstance() called │
        └──────────────────────┘
                │
          instance == null ?
                │
              (Yes)
                │
                ▼
     new DatabaseConnection()  → Object created in Heap
                │
                ▼
      instance → points to Heap Object
                │
                ▼
      All future getInstance() calls
        return same object reference
```

---

### **Key Points**

* `static` ensures a **single global instance** shared across the application.
* Lazy initialization **creates instance on demand**.
* Improves **memory efficiency**.
* Prevents creation of multiple instances.
* Thread-safety is required in concurrent environments.

---

## **Advantages of Static Keyword**

1. Efficient memory usage — single copy shared among instances.
2. Faster access — class-level reference avoids object lookup.
3. Global access — no object instantiation required.
4. Useful for defining constants (`static final`).
5. Enables patterns like **Singleton** and **Utility Classes**.

---

## **Disadvantages of Static Keyword**

1. Cannot be overridden or dynamically bound.
2. Introduces tight coupling — harder to test or mock.
3. Creates global state, increasing potential side effects.
4. Persists in memory for the program’s lifetime.
5. Overuse reduces OOP benefits such as encapsulation and polymorphism.

---

## **Summary**

* The `static` keyword defines class-level members shared by all objects.
* It is widely used for constants, utility functions, and Singleton patterns.
* Static members enhance performance but can reduce flexibility if overused.
* In the **Singleton pattern**, static enables **lazy initialization** for efficient resource use.

---

## **Exercises**

1. Write a Java class that counts how many objects have been created using a static variable.
2. Modify the Singleton example to make it thread-safe using **double-checked locking**.
3. Explain why static methods cannot use the `this` keyword.
4. Create a static nested class that prints a message without needing an instance of its outer class.
5. Discuss how static members differ in memory storage from instance members.

---
