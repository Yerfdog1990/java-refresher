
## **Lesson Notes: Commands and Your First Program**

## **1. Commands**

### **Definition**

A **program** in Java is a set (or list) of **commands** that are executed one after another in sequence:

* The **first command** is executed,
* Then the **second**,
* Then the **third**, and so on.
  When all commands have been executed, the program ends.

Each command instructs the computer to perform a specific task.

---

### **Who Executes the Commands**

Different performers understand different sets of commands:

* A **dog** understands “Sit” or “Speak.”
* A **cat** understands “Shoo.”
* A **human** might understand “Stop! Or I’ll shoot!”
* A **robot** might respond to “Work! Work, you robot scum!”

Similarly, in Java, commands are executed by the **Java Virtual Machine (JVM)**.
The **JVM** is a special program that can understand and execute Java programs.

---

### **Example Command**

```java
System.out.println("Robots are friends to humans");
```

This command tells the **JVM** to display the message:

```
Robots are friends to humans
```

---

## **2. The Simplest Command**

Before diving deeper, the lesson introduced two important principles that form the foundation of Java programming.

### **Principle 1: One Command per Line**

* In Java, each command is written on a **new line**.
* Each command ends with a **semicolon (;)**.

**Example:**
If you want to display the same phrase three times:

```java
System.out.println("Robots are friends to humans");
System.out.println("Robots are friends to humans");
System.out.println("Robots are friends to humans");
```

Each line represents a separate command.
A program made of these three lines has **three commands**.

---

### **Principle 2: Commands Must Be Inside Methods and Classes**

A program **cannot** consist only of commands.
Every command must belong to a **method**, and every method must belong to a **class**.

To understand this structure, consider the analogy of a **house**:

* A **class** is like a *house*.
* A **method** is like a *room* inside that house.
* A **command** is like *furniture* inside the room.

So, Java programs are built in this hierarchy:

> **Classes → contain Methods → which contain Commands**

---

## **3. Structure of a Typical Java Program**

### **Classes**

A Java program is made up of **classes**.

* Each class is stored in its own **.java file**,
  and the file name must match the class name.

**Examples:**

* A `House` class → `House.java` file
* A `Cat` class → `Cat.java` file

Each file contains **code written in the Java language**, including:

* The **class name**
* The **class body**, enclosed in **curly braces `{}`**

---

### **Basic Example**

```java
public class House
{
    // Class body
}
```

Inside the class body, we can define **variables** (also known as **fields**) and **methods** (also known as **functions**).

---

### **Example with Variables and Methods**

```java
public class House {

    int a;
    int b;

    public static void main(String[] args) {
        System.out.print(1);
    }

    public static double pi() {
        return 3.14;
    }
}
```

**Explanation:**

* `a` and `b` are **variables**.
* `main()` and `pi()` are **methods**.

    * The `main()` method runs when the program starts.
    * The `pi()` method returns a number (3.14).

---

## **4. The main() Method**

### **Purpose**

The **`main()` method** is the **entry point** of every Java program.
When you run a program, the JVM looks for the `main()` method and starts executing from there.

### **Rules**

* The `main()` method must always be written exactly like this:

```java
public static void main(String[] args)
```

* Every Java program must have **at least one class** that contains a `main()` method.
* A class without a `main()` method cannot be executed directly.

---

### **Minimal Java Program**

The simplest possible Java program looks like this:

```java
public class House
{
    public static void main(String[] args)
    {
    }
}
```

**Explanation:**

* `public class House` → defines the class.
* `public static void main(String[] args)` → defines the main method.
* The curly braces `{ }` indicate that the main method is currently **empty**, containing **no commands**.
* Although this program does nothing, it is **valid** and will compile successfully.

---

### **How Execution Works**

When you run the program:

1. The JVM finds the `main()` method.
2. It executes the commands inside the method, if any.
3. If there are no commands, the program ends immediately.

---

### **Adding a Command**

We can add a command inside the `main()` method:

```java
public class House
{
    public static void main(String[] args)
    {
        System.out.println("Robots are friends to humans");
    }
}
```

**Output:**

```
Robots are friends to humans
```

Now the program performs an action—displaying a message.

---

## **5. Summary**

| Concept             | Description                                                     |
| ------------------- | --------------------------------------------------------------- |
| **Command**         | An instruction to the JVM, such as `System.out.println()`       |
| **Program**         | A list of commands executed in order                            |
| **Principle 1**     | Each command ends with a semicolon and is written on a new line |
| **Principle 2**     | Commands exist inside methods, and methods exist inside classes |
| **Class**           | The blueprint of a program, stored in a `.java` file            |
| **main() Method**   | The entry point where program execution begins                  |
| **Minimal Program** | A class with a `main()` method, even if it contains no commands |

---

### **Key Takeaways**

* The JVM executes all Java commands.
* Every Java program must contain at least one class and one `main()` method.
* Commands are always placed inside methods.
* Each command ends with a semicolon.
* The first program may not do much—but it’s the foundation of everything in Java.