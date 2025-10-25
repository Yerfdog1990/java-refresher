
---

# 🧩 Java Lesson Notes: **String Literals and Escape Characters**

---

## 🧠 1. Introduction

A **string literal** in Java is a **sequence of characters enclosed in double quotes ("")**.
It represents a constant string value stored in memory. For example:

```java
String greeting = "Hello, World!";
```

String literals are **immutable**, meaning their value cannot be changed once created.

---

## ⚙️ 2. Core Rules for String Literals

### A. Encapsulation

* A **string literal**: `"Java Programming"`
* A **char literal**: `'J'` (notice the single quotes)

```java
String language = "Java"; // String literal
char letter = 'J';        // Char literal
```

---

### B. Immutability

Strings in Java are **immutable** — modifying a string creates a **new object**.

```java
String name = "Alice";
name.concat(" Smith"); // Creates a new String, not modifying 'name'
System.out.println(name); // Output: Alice

name = name.concat(" Smith");
System.out.println(name); // Output: Alice Smith
```

---

### C. The String Pool (Memory Optimization)

Java stores string literals in a special memory area called the **string pool**.
If two identical string literals appear in the code, they both point to the **same memory location**.

```java
String s1 = "Java";
String s2 = "Java";
String s3 = new String("Java"); // Explicitly creates a new object

System.out.println(s1 == s2); // true (same reference in string pool)
System.out.println(s1 == s3); // false (different memory)
System.out.println(s1.equals(s3)); // true (same content)
```

---

### D. `==` vs. `.equals()`

| Operator / Method | Compares                      | Works for       | Example         |
| ----------------- | ----------------------------- | --------------- | --------------- |
| `==`              | References (memory addresses) | Object identity | `s1 == s2`      |
| `.equals()`       | Values (string content)       | String content  | `s1.equals(s2)` |

```java
String a = "hello";
String b = new String("hello");

System.out.println(a == b);      // false
System.out.println(a.equals(b)); // true
```

---

### E. The `intern()` Method — Working with the String Pool

The **`intern()`** method is part of the standard Java `String` class.
It allows developers to explicitly interact with the **string constant pool** — optimizing memory usage by ensuring identical strings share the same memory reference.

---

#### 🧩 How `intern()` Works

When `intern()` is called on a `String`:

1. The JVM checks if an identical string already exists in the **string pool**.
2. If it does, it returns the **reference** to that string.
3. If not, it **adds** the string to the pool and returns a reference to the newly added instance.

---

#### 🧱 String Literals vs. `new String()`

| Creation Type      | Description                                                                                                                          |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------ |
| **String Literal** | Automatically interned. Example: `String s = "hello";` — JVM checks the pool first.                                                  |
| **Using `new`**    | Always creates a new object on the **heap**, even if the same literal exists in the pool. Example: `String s = new String("hello");` |

This means there can be **two distinct objects** in memory:

* One in the **string pool**
* One in the **heap**

---

#### 💡 Example: Understanding `intern()`

```java
public class InternExample {
    public static void main(String[] args) {
        String s1 = "CodeGym";              // Automatically interned
        String s2 = new String("CodeGym");  // New object in heap
        String s3 = s2.intern();            // Reference to the interned string

        System.out.println(s1 == s2);      // false (different memory)
        System.out.println(s1 == s3);      // true (both from pool)
        System.out.println(s2 == s3);      // false
        System.out.println(s1.equals(s2)); // true (same content)
        System.out.println(s1.equals(s3)); // true
    }
}
```

---

#### ✅ Key Takeaways about `intern()`

| Concept                  | Description                                                                                                                                |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------ |
| **Memory Optimization**  | Reduces memory by reusing identical strings in the pool.                                                                                   |
| **Performance**          | Interned strings can be compared with `==` since they share the same reference. However, always prefer `.equals()` for content comparison. |
| **Automatic vs. Manual** | String literals are automatically interned, while the `intern()` method explicitly adds strings to the pool.                               |

**Use Case:**
`intern()` is particularly useful when you deal with large numbers of repeated strings (e.g., reading many identical tokens or identifiers from data files).

---

## 🧩 3. Escape Characters in Java Strings

Some characters cannot be typed directly inside a string (like quotes, newlines, tabs).
These are handled using **escape sequences**, written with a **backslash (\)**.

| Escape Sequence | Meaning         | Example               | Output           |
| --------------- | --------------- | --------------------- | ---------------- |
| `\n`            | Newline         | `"Hello\nWorld"`      | Hello<br>World   |
| `\t`            | Tab             | `"A\tB"`              | A B              |
| `\\`            | Backslash       | `"C:\\Program Files"` | C:\Program Files |
| `\"`            | Double quote    | `"She said \"Hi\""`   | She said "Hi"    |
| `\'`            | Single quote    | `'I\'m fine'`         | I'm fine         |
| `\b`            | Backspace       | `"Hello\bWorld"`      | HellWorld        |
| `\r`            | Carriage return | `"Hello\rWorld"`      | World            |

### Example:

```java
public class EscapeExample {
    public static void main(String[] args) {
        System.out.println("Hello\nWorld");       // newline
        System.out.println("Column1\tColumn2");   // tab
        System.out.println("C:\\Users\\Admin");   // backslash
        System.out.println("\"Quoted Text\"");    // double quotes
    }
}
```

---

## 🔗 4. String Concatenation

You can combine strings using the **`+` operator**.
If one operand is a string, Java automatically converts the other operand into a string.

```java
String name = "Alice";
int age = 25;
String message = "Name: " + name + ", Age: " + age;
System.out.println(message);
// Output: Name: Alice, Age: 25
```

### 💡 Operator Overloading

The `+` operator behaves differently based on operands:

```java
System.out.println(10 + 20);        // 30 (integer addition)
System.out.println("10" + 20);      // "1020" (string concatenation)
```

### ⚠️ Performance Tip

Each `+` creates a new `String` in memory.
Use `StringBuilder` or `StringBuffer` for repeated concatenations (like in loops).

```java
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 3; i++) {
    sb.append("Hi ");
}
System.out.println(sb.toString()); // Output: Hi Hi Hi 
```

---

## 🧾 5. Text Blocks (Java 13+)

**Text blocks** simplify the creation of multi-line strings, eliminating most escape sequences and concatenations.

### Syntax:

Enclosed in triple quotes (`"""`):

```java
String html = """
    <html>
        <body>
            <h1>Hello, Java!</h1>
        </body>
    </html>
    """;

System.out.println(html);
```

### ✅ Features:

* Maintain **natural formatting**
* Automatically handle **line breaks**
* Reduce need for **escape sequences**

Example comparison:

```java
// Before Java 13
String json = "{\n\t\"name\": \"Alice\",\n\t\"age\": 25\n}";

// Using Text Blocks
String jsonBlock = """
    {
        "name": "Alice",
        "age": 25
    }
    """;
```

---

## 🧮 6. Summary Table

| Concept          | Description                           | Example                      |
| ---------------- | ------------------------------------- | ---------------------------- |
| String Literal   | Sequence of chars in double quotes    | `"Hello"`                    |
| Immutable        | Cannot be modified                    | `"Hi".concat("!")`           |
| String Pool      | Stores reused string literals         | `"Java" == "Java"` → true    |
| Escape Sequences | Special symbols like `\n`, `\t`, etc. | `"Line1\nLine2"`             |
| Concatenation    | Combine strings using `+`             | `"A" + "B"`                  |
| Text Blocks      | Multiline string syntax (Java 13+)    | `""" ... """`                |
| Regex Escaping   | Double escaping in string + regex     | `"\\."` to match literal `.` |
| intern()         | Adds string explicitly to pool        | `s.intern()`                 |

---

## 🧭 7. Key Takeaways

* String literals are **immutable** and automatically stored in the **string pool**.
* The **`intern()`** method helps explicitly manage string pooling for **memory efficiency**.
* Escape sequences represent **special or unprintable characters** inside string literals.
* Use **text blocks** for cleaner, multi-line strings (Java 13+).
* Use `.equals()` for content comparison — not `==`, unless comparing interned strings.

---


