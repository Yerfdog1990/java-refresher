
---

# **Lesson Notes: Types of Interfaces in Java**

**Last Updated:** 14 March 2023
**Subject:** Object-Oriented Programming in Java
**Topic:** Interfaces in Java

---

## **1. Introduction to Interfaces in Java**

In Java, an **interface** is a **reference type**, similar to a class, that defines a set of **abstract methods** (methods without bodies) that a class can implement. Interfaces provide a way to achieve **abstraction** and **multiple inheritance** in Java.

An interface may contain:

* **Constants** (implicitly `public`, `static`, and `final`)
* **Abstract methods** (implicitly `public` and `abstract`)
* **Default methods** (with a body)
* **Static methods**
* **Nested types**

Writing an interface is similar to writing a class; however, a **class describes the state and behavior** of objects, while an **interface specifies behavior** that a class **must implement**.

---

## **2. Similarities Between Interfaces and Classes**

* An interface can contain any number of methods.
* An interface can declare constants (`public static final` by default).
* The interface name must match the filename (ending with `.java`).
* After compilation, a `.class` file is generated for each interface.
* Interfaces can belong to packages, and their bytecode follows the same package structure.

---

## **3. Declaring an Interface**

To declare an interface, the `interface` keyword is used.

### **Syntax Example**

```java
public interface NameOfTheInterface {
    // constant declarations
    // abstract method declarations
}
```

### **Example: Simple Interface Implementation**

```java
interface GFG {
    void printInfo();
}

class Avi implements GFG {
    public void printInfo() {
        String name = "Avi";
        int age = 23;
        System.out.println(name);
        System.out.println(age);
    }
}

public class InterfaceExample {
    public static void main(String[] args) {
        Avi s = new Avi();
        s.printInfo();
    }
}
```

**Output:**

```
Avi
23
```

---

## **4. Properties of an Interface**

* An interface is **implicitly abstract**—no need to use the `abstract` keyword.
* All methods inside an interface are **implicitly public and abstract**.
* Variables declared inside an interface are **public, static, and final**.
* Interfaces cannot have constructors.

---

### **Example: Interface Implementation**

```java
interface Car {
    void display();
}

class Model implements Car {
    public void display() {
        System.out.println("I’m a Car");
    }

    public static void main(String[] args) {
        Model obj = new Model();
        obj.display();
    }
}
```

**Output:**

```
I’m a Car
```

---

## **5. Types of Interfaces in Java**

There are two main types of interfaces in Java:

1. **Functional Interface**
2. **Marker Interface**

Additionally, Java provides some **built-in marker interfaces**, including `Cloneable`, `Serializable`, and `Remote`.

---

### **5.1 Functional Interface**

A **Functional Interface** is an interface that contains **exactly one abstract method**.
It can, however, include **default**, **static**, and **public methods** (from `Object` class).

#### **Common Functional Interfaces:**

* `Runnable` – contains `run()` method
* `ActionListener` – contains `actionPerformed()` method
* `ItemListener` – contains `itemStateChanged()` method

#### **Example: Functional Interface Implementation**

```java
interface Writable {
    void write(String text);
}

public class FunctionalInterfaceExample implements Writable {
    public void write(String text) {
        System.out.println(text);
    }

    public static void main(String[] args) {
        FunctionalInterfaceExample obj = new FunctionalInterfaceExample();
        obj.write("GFG - Geeks For Geeks");
    }
}
```

**Output:**

```
GFG - Geeks For Geeks
```

---

### **5.2 Marker Interface**

A **Marker Interface** is an empty interface — it has **no methods, fields, or constants**.
Its purpose is to **signal** to the JVM or compiler that the implementing class has a specific property.

#### **Examples of Marker Interfaces:**

* `Serializable`
* `Cloneable`
* `Remote`

#### **Example: Simple Marker Interface**

```java
public interface MarkerInterface {
    // empty
}
```

Marker interfaces can be replaced by:

1. **Internal Flags** – used to indicate specific operations within code.
2. **Annotations** – modern alternative to marker interfaces in Java.

---

## **6. Built-in Marker Interfaces in Java**

### **6.1 Cloneable Interface**

The `Cloneable` interface belongs to the `java.lang` package.
It indicates that a class allows **object cloning** using the `clone()` method.

> **Note:** A class that implements `Cloneable` must **override** the `clone()` method as a public method.

#### **Example: Implementing Cloneable Interface**

```java
class ABC implements Cloneable {
    int x;
    String y;

    public ABC(int x, String y) {
        this.x = x;
        this.y = y;
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

public class CloneExample {
    public static void main(String[] args) throws CloneNotSupportedException {
        ABC p = new ABC(10, "We Are Reading GFG Now");
        ABC q = (ABC) p.clone();

        System.out.println(q.x);
        System.out.println(q.y);
    }
}
```

**Output:**

```
10
We Are Reading GFG Now
```

---

### **6.2 Serializable Interface**

The `Serializable` interface belongs to the `java.io` package.
It enables **serialization**, a process of converting an object into a **byte stream** to store or transmit it.

#### **Serialization Concepts**

* **Serialization:** Converting an object into a byte stream.
* **Deserialization:** Converting a byte stream back into an object.

A class must implement `Serializable` to allow its objects to be serialized and deserialized.

---

### **6.3 Remote Interface**

The `Remote` interface belongs to the `java.rmi` package.
It marks an object as **remote**, allowing it to be accessed from another JVM (Java Virtual Machine).

A **Remote interface** defines methods that can be invoked **remotely** from a non-local virtual machine.

---

## **7. Summary Table**

| **Interface Type**       | **Definition**                  | **Example Interface**           | **Purpose**                                             |
| ------------------------ | ------------------------------- | ------------------------------- | ------------------------------------------------------- |
| **Functional Interface** | Contains one abstract method    | Runnable, ActionListener        | Enables lambda expressions and single-method operations |
| **Marker Interface**     | Empty interface with no methods | Serializable, Cloneable, Remote | Provides metadata about a class                         |
| **Cloneable**            | Built-in marker interface       | java.lang.Cloneable             | Enables object cloning                                  |
| **Serializable**         | Built-in marker interface       | java.io.Serializable            | Enables object serialization                            |
| **Remote**               | Built-in marker interface       | java.rmi.Remote                 | Enables remote method invocation                        |

---

## **8. Key Takeaways**

* Interfaces define **contracts** for classes to follow.
* A class **implements** an interface to provide the actual behavior.
* **Functional interfaces** support **lambda expressions** in Java 8 and later.
* **Marker interfaces** provide metadata to the JVM or frameworks.
* Built-in marker interfaces (`Cloneable`, `Serializable`, `Remote`) are widely used in core Java libraries.

---
