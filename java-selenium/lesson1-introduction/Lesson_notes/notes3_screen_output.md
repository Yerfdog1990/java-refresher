
## **Lesson Notes: Screen Output**

---

## **1. Parameters of the println() Method**

### **Understanding Methods and Commands**

In Java, a **method** is a group of **commands** that has been given a **name**.
The commands inside a method define specific actions to be performed by the program.

Every command in Java **ends with a semicolon (`;`)**.

**Examples of Java commands:**

| Command                                | Description (What it does)                      |
| -------------------------------------- | ----------------------------------------------- |
| `System.out.println(1);`               | Displays a number on the screen → **1**         |
| `System.out.println("Amigo");`         | Displays text on the screen → **Amigo**         |
| `System.out.println("Risha & Amigo");` | Displays text on the screen → **Risha & Amigo** |

All these examples use the same method:

```java
System.out.println();
```

The value or text inside the parentheses `()` is called the **parameter** or **argument**.
Depending on what you pass as the argument, the command performs different actions.

---

### **Important Notes**

1. **Case Sensitivity**
   Java is **case-sensitive**.

    * ✅ `System.out.println()` → Works
    * ❌ `system.out.println()` → Error

   The correct capitalization must be used for the program to run properly.

2. **Displaying Text Requires Double Quotes**
   When displaying **text**, you must enclose it in **double quotes (`" "`)**.

   ```java
   System.out.println("Hello, World!");
   ```

    * A **single quote** looks like `'`

    * A **double quote** looks like `"`
      Do **not** confuse them — they are different symbols.

   > The double-quote key is usually located next to the **Enter** key on the keyboard.

---

### **Example:**

```java
public class Example {
    public static void main(String[] args) {
        System.out.println(1);
        System.out.println("Amigo");
        System.out.println("Risha & Amigo");
    }
}
```

**Output:**

```
1
Amigo
Risha & Amigo
```

---

## **2. Differences Between println() and print()**

Java provides two commands for displaying text or numbers on the screen:

1. **`System.out.println()`** – prints output **and moves to a new line**
2. **`System.out.print()`** – prints output **but stays on the same line**

---

### **Comparison Example**

| Commands                                                                                              | Output                             |
| ----------------------------------------------------------------------------------------------------- | ---------------------------------- |
| `java<br>System.out.println("Amigo");<br>System.out.println("IsThe");<br>System.out.println("Best");` | **Amigo**<br>**IsThe**<br>**Best** |
| `java<br>System.out.print("Amigo");<br>System.out.println("IsThe");<br>System.out.print("Best");`     | **AmigoIsThe**<br>**Best**         |
| `java<br>System.out.print("Amigo");<br>System.out.print("IsThe");<br>System.out.print("Best");`       | **AmigoIsTheBest**                 |

---

### **Explanation**

* **`println()`** adds a **newline character** (`\n`) after printing, so the next output appears on a new line.
* **`print()`** does **not** move to a new line; it keeps the cursor on the same line, so subsequent text continues immediately after the previous text.

Think of `println()` as printing a message and then pressing **Enter**,
while `print()` just prints without pressing **Enter**.

---

### **Example Program**

```java
public class Amigo
{
   public static void main(String[] args)
   {
      System.out.print("Amigo ");
      System.out.print("The ");
      System.out.print("Best");
   }
}
```

**Output:**

```
Amigo The Best
```

---

### **Variation with println()**

```java
public class Amigo
{
   public static void main(String[] args)
   {
      System.out.println("Amigo ");
      System.out.println("The ");
      System.out.println("Best");
   }
}
```

**Output:**

```
Amigo 
The 
Best
```

---

### **Key Note**

> The `println()` command does not immediately start on a new line —
> it prints on the **current line** and then adds a **newline character** so that the *next* command starts on a new line.

---

## **3. Summary**

| Concept                    | Description                                                   |
| -------------------------- | ------------------------------------------------------------- |
| **Command**                | An instruction the JVM executes, e.g. `System.out.println()`  |
| **Method**                 | A group of commands with a specific name                      |
| **Parameter**              | The value or text inside parentheses `( )` passed to a method |
| **Case Sensitivity**       | Java distinguishes between uppercase and lowercase letters    |
| **`System.out.println()`** | Prints output and moves to a new line                         |
| **`System.out.print()`**   | Prints output and stays on the same line                      |
| **Text Output**            | Text must be enclosed in **double quotes (`" "`)**            |
| **Number Output**          | Numbers are printed without quotes                            |

---

### **Key Takeaways**

* `System.out.println()` and `System.out.print()` are the most commonly used commands for displaying output.
* Java commands are **case-sensitive** and must be **typed exactly**.
* Every command ends with a **semicolon (`;`)**.
* Use **double quotes** for text output and write each command on its own line.
* `println()` prints with a new line; `print()` prints on the same line.

---
