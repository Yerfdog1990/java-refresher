
# **Lesson Notes: Comments in Java**

---

## **1. What Are Comments?**

In Java, a **comment** is a note written inside the source code that is **ignored by the compiler**.
Comments are meant for **humans**, not the computer — they help explain what the code does.

When the program runs, all comments are **skipped** and do not affect the program’s output.

**Purpose of Comments:**

* Explain the logic behind code.
* Make code easier for others (and your future self) to understand.
* Temporarily disable code during testing.
* Add documentation or reminders.

---

## **2. Two Kinds of Comments in Java**

Java supports **two main types** of comments:

| **Type**                | **Syntax**           | **Description**                                        |
| ----------------------- | -------------------- | ------------------------------------------------------ |
| **Single-line comment** | `// comment text`    | Used for short notes; runs to the end of the line.     |
| **Multi-line comment**  | `/* comment text */` | Used for longer explanations that span multiple lines. |

---

### **A. Multi-line Comments (`/* ... */`)**

A **multi-line comment** starts with `/*` and ends with `*/`.
Everything between these symbols is ignored by the compiler.

**Example:**

```java
public class Home {
   public static void main(String[] args) {
      /*
      Now we'll display the phrase 'Amigo is the Best'
      */
      System.out.print("Amigo ");
      System.out.print("is the ");
      System.out.print("Best");
   }
}
```

**Explanation:**

* The comment begins with `/*` and ends with `*/`.
* It can span multiple lines.
* It’s useful for writing **detailed explanations** or temporarily disabling a block of code.

---

### **B. Single-line Comments (`//`)**

A **single-line comment** begins with `//`.
Everything after the `//` on that line is ignored by the compiler.

**Example:**

```java
public class Home {
   public static void main(String[] args) {
      System.out.print("Amigo ");
      System.out.print("is the "); // This is also a comment
      System.out.print("Best");
   }
}
```

**Explanation:**

* The comment ends automatically at the end of the line.
* Ideal for **short notes** or **inline explanations** beside code.

---

### **Examples Compared**

| **Type**            | **Example**                                       | **Explanation**                              |
| ------------------- | ------------------------------------------------- | -------------------------------------------- |
| Single-line comment | `// Display greeting message`                     | Explains one line of code                    |
| Multi-line comment  | `/* This section prints the name and greeting */` | Explains multiple lines or a section of code |

---

## **3. Best Practices for Writing Comments**

✅ **Use comments to:**

* Explain *why* the code does something (not just *what* it does).
* Clarify complex logic or formulas.
* Describe method purpose, parameters, and expected results.
* Document special conditions or assumptions.

❌ **Avoid:**

* Obvious comments (e.g., `// Add 1 to x` right after `x = x + 1;`)
* Outdated comments that no longer match the code.
* Using comments as an excuse for unclear code.

---

## **4. Programmer Humor: Real-Life Comments**

Programmers sometimes leave funny or sarcastic comments in their code, such as:

```java
// I'm not responsible for this code. I was forced to write it against my will.

// Dear future me, please forgive me for this code.

// If I see something like this once more, I’ll have a complete mental breakdown.

// When I wrote this, only God and I understood what I was doing.
// Now only God knows.

// Magic. Don’t touch.
```

These humorous comments remind us that writing clear, understandable code is important —
because the person reading it next might be **you** in the future!

---

## **5. Why Comments Matter**

* They **improve readability** of code for you and other programmers.
* They **reduce confusion** during debugging or updates.
* They are **ignored by the JVM**, so they don’t affect program performance.
* They help **document programs**, especially in larger projects.

---

## **6. Example: Combining Both Types**

```java
public class Greeting {
    public static void main(String[] args) {
        // This program displays a greeting message
        
        /*
         The following three commands will print:
         "Hello Java Learner!"
         on one line.
        */
        System.out.print("Hello ");
        System.out.print("Java ");
        System.out.print("Learner!");
    }
}
```

**Output:**

```
Hello Java Learner!
```

---

## **7. Summary**

| **Concept**             | **Description**                                                   |
| ----------------------- | ----------------------------------------------------------------- |
| **Comment**             | Text in code ignored by the compiler                              |
| **Single-line Comment** | Begins with `//`, used for short notes                            |
| **Multi-line Comment**  | Begins with `/*` and ends with `*/`, used for longer explanations |
| **Purpose**             | Explain code logic, document behavior, or disable code            |
| **Ignored by JVM**      | Comments are not executed or displayed in the output              |

---

### **Key Takeaways**

* Use comments to **explain and document** your code clearly.
* The compiler **skips comments**, so they do not affect program output.
* Prefer **single-line comments** for brief notes and **multi-line comments** for detailed explanations.
* Avoid unnecessary or confusing comments — clarity in your code matters most.

---