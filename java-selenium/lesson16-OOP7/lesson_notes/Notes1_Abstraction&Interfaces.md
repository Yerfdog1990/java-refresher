
---

## **Lesson Title: Abstraction and Interface in Java**

### **1. Introduction**

In Java, *abstraction* is one of the four foundational pillars of Object-Oriented Programming (OOP), along with **encapsulation**, **inheritance**, and **polymorphism**.
Abstraction focuses on **hiding the internal implementation details** and **exposing only the essential features** of an object.
Java supports abstraction through two constructs:

* **Abstract Classes** (partial abstraction)
* **Interfaces** (complete abstraction)

---

### **2. Understanding Abstraction**

**Definition:**
Abstraction in Java is the process of hiding complex implementation details and showing only the necessary functionality to the user.
It emphasizes *what an object does*, rather than *how it does it.*

#### **Key Points**

* Abstract classes can contain both abstract (unimplemented) and concrete (implemented) methods.
* Abstraction promotes modularity and code reusability.
* It allows changes to be made to internal code without affecting external behavior.

#### **Real-Life Analogy**

A **television remote control** is an example of abstraction.
When you press the power or volume button, you don’t need to know how the remote transmits signals to the TV — you just use the simplified interface.

---

### **3. Abstract Classes in Java**

An **abstract class** is declared using the `abstract` keyword.
It cannot be instantiated directly and may contain:

* Abstract methods (without implementation).
* Concrete methods (with implementation).
* Constructors and member variables.

#### **Example: Abstract Class and Partial Abstraction**

```java
abstract class Shape {
    String color;

    // Abstract method (no body)
    abstract double area();

    // Concrete method
    public String getColor() {
        return color;
    }

    // Constructor
    public Shape(String color) {
        this.color = color;
    }
}

class Circle extends Shape {
    double radius;

    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * Math.pow(radius, 2);
    }
}

class Rectangle extends Shape {
    double length, width;

    public Rectangle(String color, double length, double width) {
        super(color);
        this.length = length;
        this.width = width;
    }

    @Override
    double area() {
        return length * width;
    }
}

public class TestAbstraction {
    public static void main(String[] args) {
        Shape s1 = new Circle("Red", 2.5);
        Shape s2 = new Rectangle("Blue", 4, 6);

        System.out.println(s1.getColor() + " Circle area: " + s1.area());
        System.out.println(s2.getColor() + " Rectangle area: " + s2.area());
    }
}
```

#### **Output**

```
Red Circle area: 19.63
Blue Rectangle area: 24.0
```

#### **Explanation**

* `Shape` defines the structure but not the details.
* Subclasses `Circle` and `Rectangle` provide their specific implementation for `area()`.

---

### **4. Interface in Java**

An **interface** is a blueprint of a class that achieves **100% abstraction**.
It defines a set of abstract methods that a class must implement.

#### **Key Features**

* All methods in interfaces are abstract by default (until Java 8).
* Variables in interfaces are implicitly `public static final`.
* A class implements an interface using the `implements` keyword.
* Supports **multiple inheritance** through interfaces.
* From Java 8 onward, interfaces can have **default** and **static** methods.

#### **Example: Interface and Full Abstraction**

```java
interface Shape {
    double calculateArea();
}

class Circle implements Shape {
    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    public double calculateArea() {
        return Math.PI * radius * radius;
    }
}

class Rectangle implements Shape {
    private double length, width;

    public Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    public double calculateArea() {
        return length * width;
    }
}

public class InterfaceDemo {
    public static void main(String[] args) {
        Shape circle = new Circle(5);
        Shape rectangle = new Rectangle(4, 6);

        System.out.println("Area of Circle: " + circle.calculateArea());
        System.out.println("Area of Rectangle: " + rectangle.calculateArea());
    }
}
```

#### **Output**

```
Area of Circle: 78.54
Area of Rectangle: 24.0
```

---

### **5. Achieving Multiple Inheritance with Interfaces**

Java does not support multiple inheritance with classes (to avoid ambiguity) but allows it through interfaces.

```java
interface Add {
    int add(int a, int b);
}

interface Subtract {
    int sub(int a, int b);
}

class Calculator implements Add, Subtract {
    public int add(int a, int b) {
        return a + b;
    }

    public int sub(int a, int b) {
        return a - b;
    }
}

public class MultipleInheritance {
    public static void main(String[] args) {
        Calculator calc = new Calculator();
        System.out.println("Addition: " + calc.add(5, 3));
        System.out.println("Subtraction: " + calc.sub(5, 3));
    }
}
```

#### **Output**

```
Addition: 8
Subtraction: 2
```

---

### **6. New Interface Features (Java 8–9 Updates)**

#### **Default Methods (Java 8)**

Allow backward compatibility by providing method implementations inside interfaces.

```java
interface TestInterface {
    default void display() {
        System.out.println("Default display method in interface");
    }
}

class TestClass implements TestInterface {}

public class DefaultMethodDemo {
    public static void main(String[] args) {
        TestClass obj = new TestClass();
        obj.display();
    }
}
```

#### **Static Methods (Java 8)**

Can be called directly via the interface name.

```java
interface Utility {
    static void greet() {
        System.out.println("Hello from static interface method!");
    }
}

public class StaticMethodDemo {
    public static void main(String[] args) {
        Utility.greet();
    }
}
```

#### **Private Methods (Java 9)**

Allow code reuse within the interface itself, accessible only inside the interface.

---

### **7. Comparison Between Abstract Class and Interface**

| Feature               | Abstract Class                       | Interface                                                       |
| --------------------- | ------------------------------------ | --------------------------------------------------------------- |
| **Abstraction Level** | Partial                              | Full (100%)                                                     |
| **Keyword**           | `abstract`                           | `interface`                                                     |
| **Instantiation**     | Cannot be instantiated               | Cannot be instantiated                                          |
| **Method Type**       | Both abstract and concrete           | Abstract (default), static, or default                          |
| **Variables**         | Instance variables allowed           | Only `public static final` constants                            |
| **Constructors**      | Allowed                              | Not allowed                                                     |
| **Inheritance**       | Single inheritance                   | Multiple inheritance                                            |
| **Access Modifiers**  | Can be private, protected, or public | All methods are public                                          |
| **Use Case**          | When subclasses share common code    | When classes share common behavior but differ in implementation |

---

### **8. Advantages of Abstraction**

* Reduces complexity and increases clarity.
* Promotes code reusability and maintainability.
* Improves security by exposing only necessary details.
* Simplifies large systems by dividing them into smaller modules.

### **9. Disadvantages**

* May add unnecessary abstraction layers.
* Can reduce flexibility and performance in certain contexts.

---

### **10. Summary**

* **Abstraction** helps simplify complex logic by exposing only necessary behavior.
* **Abstract classes** allow partial abstraction, combining abstract and concrete methods.
* **Interfaces** enable complete abstraction and multiple inheritance.
* Since Java 8 and 9, interfaces can have **default**, **static**, and **private** methods.
* Choosing between an abstract class and an interface depends on whether you need shared state or only a behavioral contract.

---

