
---

# 🧩 Java `StringBuffer` Lesson Notes

## 🧠 1. Introduction

The `StringBuffer` class in Java is a **mutable** and **thread-safe** sequence of characters.
It allows modification of strings — such as appending, inserting, deleting, or reversing — **without creating new string objects** every time.

### ✅ Key Points

* Found in the package: `java.lang`
* Implements: `Serializable`, `Appendable`, and `CharSequence`
* All major methods are **synchronized**, making it **safe for multi-threaded environments**.

---

## ⚙️ 2. Constructors

| Constructor                      | Description                                                         | Example                                         |
| -------------------------------- | ------------------------------------------------------------------- | ----------------------------------------------- |
| `StringBuffer()`                 | Creates an empty buffer with an initial capacity of 16.             | `StringBuffer sb1 = new StringBuffer();`        |
| `StringBuffer(int capacity)`     | Creates a buffer with specified capacity.                           | `StringBuffer sb2 = new StringBuffer(50);`      |
| `StringBuffer(String str)`       | Initializes with the given string (`capacity = 16 + str.length()`). | `StringBuffer sb3 = new StringBuffer("Hello");` |
| `StringBuffer(CharSequence seq)` | Copies contents of the given sequence.                              | `StringBuffer sb4 = new StringBuffer("Java!");` |

---

## ✍️ 3. Key Methods and Examples

### 🔹 `append()` — Add content to the end

Used to concatenate any data type.

```java
StringBuffer sb = new StringBuffer("Java");
sb.append(" Programming");
sb.append(101);
System.out.println(sb); // Output: Java Programming101
```

Overloaded forms include:

* `append(String str)`
* `append(int i)`
* `append(double d)`
* `append(boolean b)`
* `append(char[] str, int offset, int len)`
* `append(CharSequence s, int start, int end)`

---

### 🔹 `insert()` — Add content at a specific position

```java
StringBuffer sb = new StringBuffer("World");
sb.insert(0, "Hello ");
System.out.println(sb); // Output: Hello World
```

Other variants:

```java
sb.insert(6, 2025);        // Hello 2025World
sb.insert(5, new char[]{'_', 'X', '_'}); // Hello_X_2025World
```

---

### 🔹 `delete()` and `deleteCharAt()` — Remove characters

```java
StringBuffer sb = new StringBuffer("Programming");
sb.delete(3, 6);            // Removes 'gra'
System.out.println(sb);     // Output: Promming

sb.deleteCharAt(0);         // Removes first character
System.out.println(sb);     // Output: romming
```

---

### 🔹 `replace()` — Replace part of a sequence

```java
StringBuffer sb = new StringBuffer("Hello World");
sb.replace(6, 11, "Java");
System.out.println(sb); // Output: Hello Java
```

---

### 🔹 `substring()` and `subSequence()`

```java
StringBuffer sb = new StringBuffer("StringBuffer");
String part = sb.substring(0, 6);
System.out.println(part); // Output: String
```

---

### 🔹 `reverse()` — Reverse the entire content

```java
StringBuffer sb = new StringBuffer("ABCDE");
sb.reverse();
System.out.println(sb); // Output: EDCBA
```

---

### 🔹 `length()` and `capacity()`

```java
StringBuffer sb = new StringBuffer("Java");
System.out.println(sb.length());   // 4
System.out.println(sb.capacity()); // 20 (16 + 4)
```

---

### 🔹 `ensureCapacity()` and `trimToSize()`

```java
StringBuffer sb = new StringBuffer("Hi");
sb.ensureCapacity(50);
System.out.println(sb.capacity()); // ≥ 50
sb.trimToSize();
System.out.println(sb.capacity()); // reduced to actual length (2)
```

---

### 🔹 `setLength()` — Change buffer size

```java
StringBuffer sb = new StringBuffer("Hello");
sb.setLength(2);
System.out.println(sb); // Output: He
```

---

### 🔹 `setCharAt()` — Modify a single character

```java
StringBuffer sb = new StringBuffer("Java");
sb.setCharAt(0, 'M');
System.out.println(sb); // Output: Mava
```

---

### 🔹 `indexOf()` and `lastIndexOf()`

```java
StringBuffer sb = new StringBuffer("banana");
System.out.println(sb.indexOf("na"));      // Output: 2
System.out.println(sb.lastIndexOf("na"));  // Output: 4
```

---

### 🔹 `toString()` — Convert to immutable `String`

```java
StringBuffer sb = new StringBuffer("Mutable");
String result = sb.toString();
System.out.println(result); // Output: Mutable
```

---

## 🧩 4. Character and Unicode Methods

| Method                       | Description                            | Example                   |
| ---------------------------- | -------------------------------------- | ------------------------- |
| `charAt(int index)`          | Returns character at index.            | `sb.charAt(1)`            |
| `codePointAt(int index)`     | Returns Unicode code point at index.   | `sb.codePointAt(0)`       |
| `codePointBefore(int index)` | Returns code point before given index. | `sb.codePointBefore(2)`   |
| `codePointCount(begin, end)` | Counts Unicode points in range.        | `sb.codePointCount(0, 3)` |

Example:

```java
StringBuffer sb = new StringBuffer("Hi😊");
System.out.println(sb.codePointCount(0, sb.length())); // Output: 3
```

---

## 🧵 5. Thread Safety in `StringBuffer`

`StringBuffer` methods are **synchronized**, making them safe for concurrent modifications.

Example:

```java
StringBuffer sb = new StringBuffer();

Thread t1 = new Thread(() -> {
    for (int i = 0; i < 5; i++) sb.append("A");
});

Thread t2 = new Thread(() -> {
    for (int i = 0; i < 5; i++) sb.append("B");
});

t1.start();
t2.start();

try { t1.join(); t2.join(); } catch (InterruptedException e) {}

System.out.println(sb); 
// Output: consistent mix of A’s and B’s, no corruption
```

---

## 🧮 6. Comparison Table

| Feature       | `String`               | `StringBuilder`     | `StringBuffer`       |
| ------------- | ---------------------- | ------------------- | -------------------- |
| Mutability    | ❌ Immutable            | ✅ Mutable           | ✅ Mutable            |
| Thread-Safe   | ✅ (immutable)          | ❌ No                | ✅ Yes (synchronized) |
| Speed         | Slow for concatenation | Fast                | Moderate             |
| Introduced In | JDK 1.0                | JDK 1.5             | JDK 1.0              |
| Use Case      | Constant text          | Single-threaded app | Multi-threaded app   |

---

## 🧭 7. Summary

* **`StringBuffer`** = Mutable + Thread-Safe
* Supports efficient modification of character data.
* Use when **multiple threads** modify a shared string.
* Key methods:
  `append()`, `insert()`, `delete()`, `replace()`, `reverse()`, `toString()`.

---

## 🧩 Example: Combining Major Operations

```java
public class StringBufferDemo {
    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer("Welcome");
        sb.append(" to Java");        // Append
        sb.insert(11, " SE8");        // Insert
        sb.replace(0, 7, "Hello");    // Replace
        sb.reverse();                 // Reverse
        System.out.println(sb);       // Output: 8ES avaJ ot olleH
    }
}
```

---

