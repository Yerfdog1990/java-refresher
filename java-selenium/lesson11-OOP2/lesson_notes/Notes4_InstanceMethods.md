
---

## **Lesson Notes: Instance Methods in Java**

### **1. Introduction**

In Java, **methods** are blocks of code designed to perform specific tasks.
An **instance method** is a type of method that **belongs to an instance of a class**—that is, it operates on the data (fields or attributes) of a particular object created from that class.

Instance methods are the most common type of methods in Java and are used to **define the behavior** of objects.

---

### **2. Definition**

An **instance method** is a non-static method that can access:

* **Instance variables** (non-static fields)
* **Other instance methods**
* **Static variables and methods** (if needed)

They are invoked using an **object reference**.

**Syntax:**

```java
accessModifier returnType methodName(parameterList) {
    // method body
}
```

**Example:**

```java
public class Student {
    String name;
    int marks;

    // Instance method
    public void displayInfo() {
        System.out.println("Student Name: " + name);
        System.out.println("Marks: " + marks);
    }
}
```

In this example, `displayInfo()` is an **instance method** because it uses the instance variables `name` and `marks`.

---

### **3. Characteristics of Instance Methods**

1. Declared **without** the `static` keyword.
2. Can access both **instance variables** and **static variables**.
3. Require an **object** to be called.
4. Each object may produce different results depending on its **instance data**.

---

### **4. Calling Instance Methods**

Instance methods must be called using an **object reference**.

**Example:**

```java
public class Main {
    public static void main(String[] args) {
        Student s1 = new Student();
        s1.name = "Alice";
        s1.marks = 85;

        s1.displayInfo();  // Calling instance method using object reference
    }
}
```

**Output:**

```
Student Name: Alice
Marks: 85
```

---

### **5. Types of Instance Methods**

Instance methods can be broadly classified into three categories:

| **Type**                   | **Purpose**                                   | **Example**            |
| -------------------------- | --------------------------------------------- | ---------------------- |
| **Accessor (Getter)**      | To retrieve the value of an instance variable | `getName()`            |
| **Mutator (Setter)**       | To modify the value of an instance variable   | `setName(String name)` |
| **Utility/General Method** | To perform operations using instance data     | `calculateAverage()`   |

**Example:**

```java
public class Student {
    private String name;
    private int marks;

    // Setter (Mutator)
    public void setName(String name) {
        this.name = name;
    }

    // Getter (Accessor)
    public String getName() {
        return name;
    }

    // General method
    public void showDetails() {
        System.out.println("Name: " + name + ", Marks: " + marks);
    }
}
```

---

### **6. Difference Between Instance and Static Methods**

| **Feature**                      | **Instance Method**     | **Static Method** |
| -------------------------------- | ----------------------- | ----------------- |
| **Belongs to**                   | Object (instance)       | Class             |
| **Requires object to call**      | Yes                     | No                |
| **Access to instance variables** | Yes                     | No                |
| **Declared with `static`**       | No                      | Yes               |
| **Example**                      | `student.displayInfo()` | `Math.sqrt(25)`   |

---

### **7. Example Program**

```java
public class Rectangle {
    double length;
    double width;

    // Instance method to calculate area
    public double calculateArea() {
        return length * width;
    }

    // Instance method to display details
    public void displayDetails() {
        System.out.println("Length: " + length);
        System.out.println("Width: " + width);
        System.out.println("Area: " + calculateArea());
    }

    public static void main(String[] args) {
        Rectangle rect = new Rectangle();
        rect.length = 10.5;
        rect.width = 4.2;

        rect.displayDetails();
    }
}
```

**Output:**

```
Length: 10.5
Width: 4.2
Area: 44.1
```

---

### **8. Key Points to Remember**

* Instance methods are tied to **objects**, not classes.
* They are used to **manipulate instance variables**.
* You must **create an object** before calling them.
* They can call both **other instance methods** and **static methods**.

---

### **9. Summary**

* **Instance methods** define the behavior of **individual objects**.
* They help achieve **encapsulation** by allowing controlled access to instance data.
* Understanding instance methods is essential for mastering **object-oriented programming** in Java.

---

