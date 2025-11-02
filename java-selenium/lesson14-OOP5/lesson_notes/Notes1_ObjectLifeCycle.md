
---

# **Lesson Notes: The Object Lifecycle in Java**

### **Last Updated:** November 2025

---

## **1. Introduction**

In Java, everything revolves around **objects**. Objects represent entities with state and behavior, created from classes that act as blueprints.

Understanding the **`Object` class** (the root of Java’s class hierarchy) and the **object lifecycle** (creation, usage, and destruction) is fundamental to mastering Java programming.

This lesson explains:

* How objects are created, used, and destroyed,
* How memory is managed automatically,
* And how the `Object` class methods shape object behavior.

---

## **2. The `Object` Class Overview**

Every class in Java implicitly extends the `java.lang.Object` class, making it the **ancestor of all classes**.

### **2.1 Core Methods of the Object Class**

| Method                              | Description                                                              |
| ----------------------------------- | ------------------------------------------------------------------------ |
| `equals(Object obj)`                | Compares two objects for logical equality.                               |
| `hashCode()`                        | Generates a hash code for use in hashing data structures like `HashMap`. |
| `toString()`                        | Returns a string representation of the object.                           |
| `getClass()`                        | Returns the runtime class of the object.                                 |
| `clone()`                           | Creates and returns a copy (clone) of the object.                        |
| `wait()`, `notify()`, `notifyAll()` | Support inter-thread communication and synchronization.                  |
| `finalize()`                        | Called before an object is destroyed (deprecated in Java 9+).            |

---

## **3. Creating Objects**

In Java, objects are **instances of classes**. Creating an object involves three main steps: **declaration**, **instantiation**, and **initialization**.

```java
Rectangle rect = new Rectangle();
```

This statement performs three actions:

1. **Declaration:**
   `Rectangle rect;` declares a variable `rect` of type `Rectangle`.

2. **Instantiation:**
   `new Rectangle()` allocates memory for the object in the heap.

3. **Initialization:**
   The constructor `Rectangle()` initializes the object’s state.

---

### **3.1 Declaring an Object**

Declarations inform the compiler about a variable’s type and name but do **not create an object**.

```java
Rectangle rect;  // Declaration only
```

At this point, `rect` holds no object reference. To assign an object, use the `new` operator later.

---

### **3.2 Instantiating an Object**

The `new` operator allocates memory and calls a constructor to initialize the new object.

```java
Rectangle rect = new Rectangle(100, 200);
```

Here:

* `new Rectangle(100, 200)` instantiates a `Rectangle` object.
* The constructor initializes its width and height.
* The result (a reference) is assigned to the variable `rect`.

---

### **3.3 Initializing an Object**

Constructors initialize objects when created.
A class can define multiple constructors with different parameters.

```java
public class Rectangle {
    int width, height;
    Point origin;

    public Rectangle() {}
    public Rectangle(int w, int h) { width = w; height = h; }
    public Rectangle(Point p) { origin = p; }
    public Rectangle(Point p, int w, int h) { origin = p; width = w; height = h; }
}
```

Depending on arguments, the compiler automatically selects the appropriate constructor:

```java
Rectangle r1 = new Rectangle(100, 200);       // width, height
Rectangle r2 = new Rectangle(new Point(44,78)); // origin
Rectangle r3 = new Rectangle();               // no arguments
```

---

## **4. Using Objects**

After creation, objects can be **used** through their variables (fields) and **methods**.

### **4.1 Accessing Object Variables**

You can refer to an object’s field using the dot (`.`) operator.

```java
rect.origin = new Point(15, 37);
rect.width = 50;
rect.height = 100;
int area = rect.width * rect.height;
```

General syntax:

```java
objectReference.variableName
```

> Example: `bob.height * bob.width` accesses Bob’s dimensions independently of `rect`.

---

### **4.2 Calling Object Methods**

Methods define an object’s behavior.
To call a method, use the same dot notation:

```java
rect.move(15, 37);           // moves the rectangle
int area = rect.area();      // calls area() and stores the result
```

General syntax:

```java
objectReference.methodName(arguments);
```

Example using a temporary object:

```java
int area = new Rectangle(100, 50).area();
```

> Here, the `Rectangle` object is created and used immediately — but since no variable holds its reference, it becomes **eligible for garbage collection** right after use.

---

## **5. The Object Lifecycle**

Objects in Java go through several distinct stages:

| Stage                          | Description                                                                     |
| ------------------------------ | ------------------------------------------------------------------------------- |
| **1. Class Loading**           | The JVM loads the class bytecode into memory.                                   |
| **2. Object Creation**         | The `new` operator allocates memory on the heap and calls a constructor.        |
| **3. Object in Use**           | The object performs actions while it’s referenced.                              |
| **4. Dereferencing**           | When references are removed or go out of scope, the object becomes unreachable. |
| **5. Garbage Collection**      | The GC automatically reclaims the object’s memory.                              |
| **6. Finalization (optional)** | Before destruction, the GC may call `finalize()` for cleanup.                   |

### **Code Example**

```java
public class Car {
    String model;

    public Car(String model) { this.model = model; }

    public static void main(String[] args) {
        Car lamborghini = new Car("Lamborghini Diablo");
        lamborghini = null; // No references → eligible for GC
    }
}
```

---

## **6. Garbage Collection**

Java’s **garbage collector (GC)** automatically frees memory used by unreferenced objects.

It works in two phases:

1. **Mark:** Finds all objects that are still reachable.
2. **Sweep:** Deletes unmarked (unreachable) objects and reclaims memory.

> The JVM runs GC in a **low-priority background thread**. It can be triggered when memory is low or when the system is idle.

### **Code Illustration**

```java
public class Cat {
    protected void finalize() throws Throwable {
        System.out.println("A Cat object is destroyed!");
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100000; i++) {
            Cat c = new Cat();
            c = null;
        }
    }
}
```

> ⚠️ Note: The `finalize()` method may not always be called before program exit.
> From Java 9 onward, `finalize()` is deprecated — use **try-with-resources** or explicit cleanup instead.

---

## **7. The finalize() Method**

The **`finalize()`** method allows an object to clean up before destruction.
Defined in `Object`, it can be overridden to release resources (like files or sockets).

```java
protected void finalize() throws Throwable {
    try {
        System.out.println("Cleaning up before GC...");
    } finally {
        super.finalize();
    }
}
```

Example from a `Stack` class:

```java
protected void finalize() throws Throwable {
    items = null;
    super.finalize();
}
```

> Always call `super.finalize()` last to allow the superclass to perform its cleanup.

---

## **8. Cloning Objects**

The **`clone()`** method in the `Object` class allows you to create an identical copy of an object.

### **Example**

```java
public class Customer implements Cloneable {
    private int id;
    private String name;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
```

To perform a **deep copy**, clone referenced objects as well:

```java
public class Customer implements Cloneable {
    Activity activity;

    @Override
    public Customer clone() throws CloneNotSupportedException {
        Customer copy = (Customer) super.clone();
        copy.activity = (Activity) activity.clone();
        return copy;
    }
}
```

---

## **9. Object Serialization**

Serialization converts an object into a byte stream so it can be stored or sent across a network.
Deserialization recreates the object later.

```java
class Student implements Serializable {
    private static final long serialVersionUID = 42L;
    String name;
}
```

Serialization Example:

```java
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("student.ser"));
out.writeObject(new Student());
out.close();
```

Deserialization:

```java
ObjectInputStream in = new ObjectInputStream(new FileInputStream("student.ser"));
Student s = (Student) in.readObject();
in.close();
```

---

## **10. Diagram: Java Object Lifecycle**

```
 ┌──────────────────────────────────────┐
 │        Class Loaded (.class)         │
 └──────────────────────────────────────┘
                    │
                    ▼
 ┌──────────────────────────────────────┐
 │     Object Created (new + ctor)      │
 └──────────────────────────────────────┘
                    │
                    ▼
 ┌──────────────────────────────────────┐
 │      Object in Use (Alive State)     │
 └──────────────────────────────────────┘
                    │
                    ▼
 ┌──────────────────────────────────────┐
 │  Reference Lost / Set to null        │
 └──────────────────────────────────────┘
                    │
                    ▼
 ┌──────────────────────────────────────┐
 │ Eligible for Garbage Collection (GC) │
 └──────────────────────────────────────┘
                    │
                    ▼
 ┌──────────────────────────────────────┐
 │ Optional finalize() Call             │
 └──────────────────────────────────────┘
                    │
                    ▼
 ┌──────────────────────────────────────┐
 │ Object Destroyed / Memory Freed      │
 └──────────────────────────────────────┘
```

---

## **11. Summary**

* All Java classes inherit from the `Object` class.
* Objects are created via **declaration**, **instantiation**, and **initialization**.
* Objects can be accessed and manipulated through fields and methods.
* The **object lifecycle** includes creation, use, dereferencing, garbage collection, and finalization.
* The **Garbage Collector** reclaims memory automatically.
* **finalize()** is for cleanup but should be avoided in favor of explicit resource management.
* Java’s memory management eliminates manual deletion, reducing memory leaks and improving reliability.

---
