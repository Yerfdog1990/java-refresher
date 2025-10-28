Here’s a complete, academic-style set of **lesson notes on Static Methods** written in the same structure and tone as the previous “Instance Methods” lesson:

---

## **Lesson Notes: Static Methods in Java**

### **1. Introduction**

In Java, methods can either belong to an **instance** of a class or to the **class itself**.
A **static method** is one that belongs to the **class**, not to any specific object. This means it can be called **without creating an instance** of the class.

Static methods are commonly used for **utility operations**, **mathematical computations**, or **shared functionality** that does not depend on object data.

---

### **2. Definition**

A **static method** is declared using the keyword `static` within a class.
It can be called directly using the **class name** instead of an object reference.

**Syntax:**

```java
accessModifier static returnType methodName(parameterList) {
    // method body
}
```

**Example:**

```java
public class MathUtils {
    public static int square(int number) {
        return number * number;
    }
}
```

Here, `square()` is a **static method** because it is defined with the `static` keyword and can be accessed using the class name.

---

### **3. Characteristics of Static Methods**

1. Declared using the `static` keyword.
2. Belong to the **class**, not to any object.
3. Can be called **without creating an instance** of the class.
4. **Cannot access instance variables or instance methods** directly.
5. Can access **static variables and other static methods**.
6. Memory for static methods is allocated only **once**, when the class is loaded.

---

### **4. Calling Static Methods**

Static methods can be called in two ways:

* Using the **class name** (recommended)
* Using an **object reference** (allowed but discouraged)

**Example:**

```java
public class Main {
    public static void main(String[] args) {
        // Recommended way
        int result = MathUtils.square(5);
        System.out.println("Square: " + result);

        // Not recommended, but valid
        MathUtils util = new MathUtils();
        System.out.println("Square: " + util.square(6));
    }
}
```

**Output:**

```
Square: 25
Square: 36
```

---

### **5. Use Cases of Static Methods**

Static methods are particularly useful when:

* The operation **does not depend on instance variables**.
* You want to create **utility/helper classes** (e.g., `Math`, `Arrays`, `Collections`).
* You need **shared logic** across all objects of a class.

**Example:**

```java
public class TemperatureConverter {
    public static double celsiusToFahrenheit(double celsius) {
        return (celsius * 9 / 5) + 32;
    }
}
```

This method can be called directly as:

```java
double result = TemperatureConverter.celsiusToFahrenheit(25);
System.out.println(result);  // Output: 77.0
```

---

### **6. Difference Between Static and Instance Methods**

| **Feature**                       | **Static Method** | **Instance Method**     |
| --------------------------------- | ----------------- | ----------------------- |
| **Belongs to**                    | Class             | Object (instance)       |
| **Accessed using**                | Class name        | Object reference        |
| **Can access instance variables** | No                | Yes                     |
| **Can access static variables**   | Yes               | Yes                     |
| **Requires object creation**      | No                | Yes                     |
| **Declared with `static`**        | Yes               | No                      |
| **Example**                       | `Math.sqrt(16)`   | `student.displayInfo()` |

---

### **7. Example Program**

```java
public class Calculator {
    // Static method for addition
    public static int add(int a, int b) {
        return a + b;
    }

    // Static method for multiplication
    public static int multiply(int a, int b) {
        return a * b;
    }

    // Instance method
    public void showResult(int value) {
        System.out.println("Result: " + value);
    }

    public static void main(String[] args) {
        // Calling static methods using class name
        int sum = Calculator.add(10, 5);
        int product = Calculator.multiply(10, 5);

        // Displaying results using instance method
        Calculator calc = new Calculator();
        calc.showResult(sum);
        calc.showResult(product);
    }
}
```

**Output:**

```
Result: 15
Result: 50
```

---

### **8. Restrictions of Static Methods**

* **Cannot use `this` or `super`** keywords (since they belong to an instance).
* **Cannot access non-static (instance) members** directly.
* **Cannot be overridden** in a subclass (but can be hidden).

**Example of invalid code:**

```java
public class Example {
    int number = 10;

    public static void showNumber() {
        // System.out.println(number);  // ❌ Error: Cannot access instance variable from static context
    }
}
```

---

### **9. Common Examples of Static Methods in Java**

1. `Math.pow()`, `Math.sqrt()`, `Math.max()`
2. `Collections.sort()`
3. `Arrays.toString()`
4. `Integer.parseInt()`
5. `System.out.println()`

These methods do not depend on object data and can be called directly using the class name.

---

### **10. Key Points to Remember**

* Static methods belong to the **class**, not the object.
* They are used when behavior is **shared** by all instances.
* They **cannot access instance variables** directly.
* Use them for **utility operations** or **general-purpose logic**.
* Best practice: call them using the **class name** for clarity.

---

### **11. Summary**

* A **static method** defines a behavior that belongs to the **class as a whole**.
* It is declared using the `static` keyword and can be called **without creating an object**.
* Static methods are essential for **utility classes** and **shared functionality** that doesn’t depend on object data.

---
