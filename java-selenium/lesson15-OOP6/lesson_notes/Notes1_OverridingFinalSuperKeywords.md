
---

# **Lesson Notes: Method Overriding, final & super Keywords in Java**

---

## **1. Introduction**

In Java, inheritance allows one class (subclass or child class) to acquire the properties and behaviors of another class (superclass or parent class).
To manage and customize this inheritance behavior, Java provides three important mechanisms:

* **Method Overriding** – Redefining inherited methods to change behavior.
* **`final` keyword** – Restricting modification or inheritance.
* **`super` keyword** – Accessing parent class variables, methods, and constructors.

These features support **polymorphism, encapsulation, and controlled extensibility** in object-oriented programming (OOP).

---

## **2. Method Overriding**

### **2.1 Definition**

**Method Overriding** occurs when a **subclass provides a specific implementation** of a method already defined in its superclass.
It enables **runtime polymorphism**, where the method call is resolved based on the actual object type at runtime.

---

### **2.2 Rules for Method Overriding**

* The overriding method must have:

    * The **same name**, **parameters**, and **return type** (or covariant subtype).
* It cannot have a **more restrictive access modifier**.
* It cannot throw **broader checked exceptions** than the parent version.
* It must **not be static or final**.
* The `@Override` annotation is used to verify the correctness of overriding.

---

### **2.3 Example: Basic Method Overriding**

```java
class Animal {
    void makeSound() {
        System.out.println("Animal makes a sound.");
    }
}

class Dog extends Animal {
    @Override
    void makeSound() {
        System.out.println("Dog barks.");
    }
}

public class TestOverride {
    public static void main(String[] args) {
        Animal a = new Dog();  // Upcasting
        a.makeSound();         // Executes Dog’s overridden method
    }
}
```

**Output:**

```
Dog barks.
```

**Explanation:**
Though `a` is of type `Animal`, it points to a `Dog` object. Hence, the `Dog` version of `makeSound()` is called at runtime (dynamic binding).

---

### **2.4 Using `super` in Method Overriding**

`super` can be used to call the parent version of an overridden method.

```java
class Parent {
    void show() {
        System.out.println("Parent class show() method");
    }
}

class Child extends Parent {
    @Override
    void show() {
        super.show(); // Calls parent method
        System.out.println("Child class show() method");
    }
}

public class SuperExample {
    public static void main(String[] args) {
        Child obj = new Child();
        obj.show();
    }
}
```

**Output:**

```
Parent class show() method
Child class show() method
```

---

### **2.5 Covariant Return Type Example**

In overriding, a method in the subclass can return a **subtype** of the parent’s return type.

```java
class Parent {
    Parent getObject() {
        System.out.println("Parent object returned");
        return new Parent();
    }
}

class Child extends Parent {
    @Override
    Child getObject() {
        System.out.println("Child object returned");
        return new Child();
    }
}

public class CovariantDemo {
    public static void main(String[] args) {
        Parent obj = new Child();
        obj.getObject();
    }
}
```

**Output:**

```
Child object returned
```

---

### **2.6 Special Cases in Overriding**

| Case                | Description                                        |
| ------------------- | -------------------------------------------------- |
| **Private methods** | Cannot be overridden; they are hidden.             |
| **Static methods**  | Cannot be overridden; they are hidden instead.     |
| **Final methods**   | Cannot be overridden; compiler error if attempted. |

---

## **3. The `final` Keyword**

### **3.1 Definition**

The **`final` keyword** is a **non-access modifier** used to prevent changes.
It can be applied to **variables**, **methods**, or **classes**.

---

### **3.2 Uses of `final`**

| Context            | Meaning                                               |
| ------------------ | ----------------------------------------------------- |
| **final variable** | Value cannot be reassigned. Used to define constants. |
| **final method**   | Cannot be overridden by subclasses.                   |
| **final class**    | Cannot be extended (inherited).                       |

---

### **3.3 Example: Final Variable**

```java
public class ConstantExample {
    public static void main(String[] args) {
        final double PI = 3.14159;
        System.out.println("Value of PI: " + PI);
        // PI = 3.14; // Error: cannot assign a value to final variable
    }
}
```

---

### **3.4 Example: Final Method**

```java
class Vehicle {
    final void start() {
        System.out.println("Vehicle starting...");
    }
}

class Car extends Vehicle {
    // void start() {} // Error: Cannot override final method
}
```

---

### **3.5 Example: Final Class**

```java
final class Utility {
    void display() {
        System.out.println("This is a final class.");
    }
}

// class SubUtility extends Utility {} // Error: cannot inherit from final class
```

---

### **3.6 Example: Final Reference Variable**

Even though the reference cannot change, the **object’s internal state** can.

```java
public class FinalReferenceDemo {
    public static void main(String[] args) {
        final StringBuilder sb = new StringBuilder("Hello");
        sb.append(" Java");
        System.out.println(sb);

        // sb = new StringBuilder("Hi"); // Error: cannot reassign final reference
    }
}
```

**Output:**

```
Hello Java
```

---

## **4. The `super` Keyword**

### **4.1 Definition**

The `super` keyword refers to the **parent class** and allows access to its **fields**, **methods**, and **constructors**.

---

### **4.2 Example 1: Accessing Parent Variable**

```java
class Vehicle {
    int speed = 120;
}

class Car extends Vehicle {
    int speed = 180;

    void displaySpeed() {
        System.out.println("Child speed: " + speed);
        System.out.println("Parent speed: " + super.speed);
    }
}

public class SuperVariableDemo {
    public static void main(String[] args) {
        new Car().displaySpeed();
    }
}
```

**Output:**

```
Child speed: 180
Parent speed: 120
```

---

### **4.3 Example 2: Accessing Parent Method**

```java
class Person {
    void message() {
        System.out.println("This is the Person class");
    }
}

class Student extends Person {
    void message() {
        System.out.println("This is the Student class");
    }

    void display() {
        message();
        super.message();
    }
}

public class SuperMethodDemo {
    public static void main(String[] args) {
        new Student().display();
    }
}
```

**Output:**

```
This is the Student class
This is the Person class
```

---

### **4.4 Example 3: Calling Parent Constructor**

```java
class Parent {
    Parent() {
        System.out.println("Parent constructor called");
    }
}

class Child extends Parent {
    Child() {
        super(); // must be the first statement
        System.out.println("Child constructor called");
    }
}

public class SuperConstructorDemo {
    public static void main(String[] args) {
        new Child();
    }
}
```

**Output:**

```
Parent constructor called
Child constructor called
```

---

## **5. Relationship Between Overriding, `final`, and `super`**

| Concept        | Purpose                   | Behavior                             |
| -------------- | ------------------------- | ------------------------------------ |
| **Overriding** | Redefines a parent method | Enables runtime polymorphism         |
| **`final`**    | Restricts change          | Prevents overriding/inheritance      |
| **`super`**    | Refers to parent class    | Calls parent members or constructors |

---

## **6. Comparison: Method Overriding vs Method Overloading**

| Feature                  | **Method Overloading**                                                              | **Method Overriding**                                                  |
| ------------------------ | ----------------------------------------------------------------------------------- | ---------------------------------------------------------------------- |
| **Definition**           | Multiple methods in the **same class** with the same name but different parameters. | A method in the **subclass** redefines a method in the **superclass**. |
| **Purpose**              | Achieve **compile-time polymorphism (static binding)**.                             | Achieve **runtime polymorphism (dynamic binding)**.                    |
| **Inheritance**          | Not required; occurs in the same class.                                             | Requires inheritance between superclass and subclass.                  |
| **Parameter List**       | Must be **different** (type, number, or order).                                     | Must be **identical**.                                                 |
| **Return Type**          | Can be same or different (no conflict).                                             | Must be same or **covariant**.                                         |
| **Access Modifier**      | Can be any.                                                                         | Cannot be more restrictive than parent.                                |
| **Static/Final Methods** | Can be overloaded.                                                                  | Cannot be overridden if static or final.                               |
| **Binding Time**         | Compile time.                                                                       | Runtime.                                                               |
| **Exception Rules**      | No restriction.                                                                     | Cannot throw broader checked exceptions.                               |
| **Example**              | `void add(int a, int b)` and `void add(double a, double b)`                         | Parent: `void show()` → Child: `void show()`                           |

---

### **Example Comparison**

```java
// Overloading (same class)
class MathUtils {
    int add(int a, int b) {
        return a + b;
    }
    double add(double a, double b) {
        return a + b;
    }
}

// Overriding (inheritance)
class Parent {
    void show() {
        System.out.println("Parent show()");
    }
}
class Child extends Parent {
    @Override
    void show() {
        System.out.println("Child show()");
    }
}

public class CompareDemo {
    public static void main(String[] args) {
        MathUtils m = new MathUtils();
        System.out.println("Sum: " + m.add(5, 10));  // Overloading

        Parent p = new Child();
        p.show();  // Overriding
    }
}
```

**Output:**

```
Sum: 15
Child show()
```

---

## **7. Summary**

| Keyword/Concept       | Description                                   | Example Use              |
| --------------------- | --------------------------------------------- | ------------------------ |
| **Method Overriding** | Redefines parent method to customize behavior | `@Override void show()`  |
| **`final` variable**  | Value cannot be reassigned                    | `final int MAX = 10;`    |
| **`final` method**    | Cannot be overridden                          | `final void display()`   |
| **`final` class**     | Cannot be inherited                           | `final class Utility {}` |
| **`super` keyword**   | Refers to parent class                        | `super.show();`          |

---

### **8. Real-Life Analogy**

* **Overriding:** A child modifies a family rule to suit themselves.
* **Overloading:** A person with the same name handles different tasks based on context.
* **final:** A “Do Not Edit” tag on code.
* **super:** Referring to “ask your parent” for original rules.

---

✅ **In summary:**

* **Overriding** provides flexibility through polymorphism.
* **`final`** ensures immutability and control.
* **`super`** preserves parent behavior when needed.
* Together, they balance **extensibility** and **stability** in Java’s OOP design.

---

