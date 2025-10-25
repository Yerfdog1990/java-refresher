
---

# 🧱 **Lesson Notes: Java `StringBuilder` Class**

---

## 🪶 **1. Introduction**

The **`StringBuilder`** class in Java provides a **mutable sequence of characters**, designed for fast and efficient modification of strings.
Unlike `String`, which is **immutable** (every modification creates a new object), `StringBuilder` allows in-place edits—making it ideal for operations such as concatenation, insertion, deletion, and replacement in single-threaded programs.

It was introduced in **Java 1.5** as a lightweight alternative to `StringBuffer`.

---

## 🌟 **2. Core Concepts and Distinctions**

| Feature           | `StringBuilder`                                | `String`                              | `StringBuffer`                       |
| :---------------- | :--------------------------------------------- | :------------------------------------ | :----------------------------------- |
| **Mutability**    | ✅ **Mutable** – can be modified                | ❌ **Immutable** – creates new objects | ✅ **Mutable**                        |
| **Thread Safety** | ❌ **Not synchronized** (fast, single-threaded) | ✅ Immutable → thread-safe             | ✅ **Synchronized** (safe but slower) |
| **Performance**   | 🚀 **Fastest** for single-threaded operations  | ❌ Slow due to new object creation     | ⚙️ Slower due to locking overhead    |

### 💡 Capacity and Efficiency

Every `StringBuilder` has an internal **capacity** that determines how many characters it can hold before needing to expand its buffer.
If the content grows beyond the capacity, Java automatically reallocates a larger buffer — typically `(oldCapacity * 2) + 2`.

> **Efficiency tip:** Because most `StringBuilder` methods return `this`, you can **chain operations**, e.g.
> `sb.append("A").append("B").append("C");`

---

## 🏗️ **3. Constructors**

You can create a `StringBuilder` using several constructors, depending on your needs:

| Constructor                       | Description                                                                    | Example                                           |
| :-------------------------------- | :----------------------------------------------------------------------------- | :------------------------------------------------ |
| `StringBuilder()`                 | Creates an empty builder with **default capacity 16**.                         | `StringBuilder sb1 = new StringBuilder();`        |
| `StringBuilder(int capacity)`     | Creates a builder with a **custom initial capacity**.                          | `StringBuilder sb2 = new StringBuilder(50);`      |
| `StringBuilder(String str)`       | Initializes builder with given string content. Capacity = `16 + str.length()`. | `StringBuilder sb3 = new StringBuilder("Hello");` |
| `StringBuilder(CharSequence seq)` | Initializes builder with another character sequence.                           | `StringBuilder sb4 = new StringBuilder(sb3);`     |

---

## ✍️ **4. Principal Modification Methods**

### 🧩 A. `append()`

Appends data (of any type) to the **end** of the sequence.
This is the most frequently used method when building strings dynamically.

| Method               | Description                                 | Example                           | Result                   |
| :------------------- | :------------------------------------------ | :-------------------------------- | :----------------------- |
| `append(String str)` | Appends a string.                           | `sb.append("World").append("!");` | `"HelloWorld!"`          |
| `append(int i)`      | Appends an integer.                         | `sb.append(2025);`                | `"2025"`                 |
| `append(Object obj)` | Appends string representation of an object. | `sb.append(new Object());`        | `"java.lang.Object@..."` |

**Example:**

```java
StringBuilder log = new StringBuilder("User: ");
log.append("Alice")
   .append(" logged in at ")
   .append(System.currentTimeMillis())
   .append(". Success: ")
   .append(true);
```

---

### 🧩 B. `insert()`

Inserts a string or value **at a specific index**, shifting existing characters rightward.

| Method                           | Description                | Example                                | Result        |
| :------------------------------- | :------------------------- | :------------------------------------- | :------------ |
| `insert(int offset, String str)` | Inserts at given index.    | `sb.insert(5, "Java");` (on `"Hello"`) | `"HellJavao"` |
| `insert(int offset, char[] str)` | Inserts a character array. | `sb.insert(2, new char[]{'X','Y'});`   | `"HeXYllo"`   |

**Example:**

```java
StringBuilder code = new StringBuilder("ABCDE");
code.insert(2, "XX"); // index 2
System.out.println(code); // ABXXCDE
```

---

### 🧩 C. `delete()` and `replace()`

| Method                                    | Description                             | Example                              | Result    |
| :---------------------------------------- | :-------------------------------------- | :----------------------------------- | :-------- |
| `delete(int start, int end)`              | Removes characters from start to end-1. | `sb.delete(3, 7);` (on `"12345678"`) | `"1238"`  |
| `deleteCharAt(int index)`                 | Removes a single character.             | `sb.deleteCharAt(2);` (on `"abcde"`) | `"abde"`  |
| `replace(int start, int end, String str)` | Replaces a section with a new string.   | `sb.replace(0, 3, "New");`           | `"Newde"` |

**Example:**

```java
StringBuilder text = new StringBuilder("The quick brown fox");
text.delete(4, 9);         // Remove "quick"
text.replace(4, 9, "sly"); // Replace "brown" with "sly"
System.out.println(text);  // The sly fox
```

---

## 🔄 **5. Utility and Inspection Methods**

These methods allow inspection, conversion, and management of the builder’s internal state.

---

### 🧮 A. Sequence Manipulation & Conversion

| Method       | Description                                                                                             | Example                            | Result           |
| :----------- | :------------------------------------------------------------------------------------------------------ | :--------------------------------- | :--------------- |
| `reverse()`  | Reverses all characters in place (handles Unicode surrogate pairs correctly).                           | `sb.reverse();` (on `"sdrawkcaB"`) | `"Backwards"`    |
| `toString()` | Converts builder to an **immutable `String`**. Further edits to the builder will not affect the result. | `String finalStr = sb.toString();` | `"Final String"` |

---

### 🔍 B. Indexing and Character Access

| Method                                                         | Description                                   | Example                              | Result                           |
| :------------------------------------------------------------- | :-------------------------------------------- | :----------------------------------- | :------------------------------- |
| `charAt(int index)`                                            | Returns the character at the specified index. | `sb.charAt(0);` (on `"Java"`)        | `'J'`                            |
| `setCharAt(int index, char ch)`                                | Replaces a character at the specified index.  | `sb.setCharAt(0, 'K');`              | `"Kava"`                         |
| `substring(int start, int end)`                                | Returns a substring (immutable).              | `sb.substring(1, 4);` (on `"Hello"`) | `"ell"`                          |
| `getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin)` | Copies a substring into a char array.         | `sb.getChars(0, 3, arr, 1);`         | Copies first 3 chars into array. |

---

### 🧩 C. Length, Code Points, and Navigation

| Method                                      | Description                                  | Example                              | Result                         |
| :------------------------------------------ | :------------------------------------------- | :----------------------------------- | :----------------------------- |
| `length()`                                  | Returns current number of characters.        | `sb.length();`                       | `5`                            |
| `codePointCount(int begin, int end)`        | Counts Unicode code points (handles emojis). | `sb.codePointCount(0, sb.length());` | Unicode length                 |
| `offsetByCodePoints(int index, int offset)` | Returns index moved by `offset` code points. | `sb.offsetByCodePoints(0, 2);`       | Index after 2 full characters. |

---

### 🧰 D. Buffer Management (Memory & Capacity)

| Method                       | Description                                                   | Example                  | Effect       |
| :--------------------------- | :------------------------------------------------------------ | :----------------------- | :----------- |
| `capacity()`                 | Returns total allocated buffer size.                          | `sb.capacity();`         | Default: 16  |
| `ensureCapacity(int minCap)` | Expands capacity if smaller than `minCap`.                    | `sb.ensureCapacity(50);` | Grows buffer |
| `trimToSize()`               | Shrinks capacity to match length (frees memory).              | `sb.trimToSize();`       | Reduces size |
| `setLength(int newLen)`      | Adjusts the sequence length. Truncates or pads with `\u0000`. | `sb.setLength(2);`       | `"He"`       |

---

## ⚙️ **6. Example Program**

```java
public class StringBuilderDemo {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder("Learn");
        
        sb.append(" Java");
        sb.insert(5, "ing");
        sb.replace(0, 5, "Master");
        sb.reverse();
        
        System.out.println("Result: " + sb);
        System.out.println("Length: " + sb.length());
        System.out.println("Capacity: " + sb.capacity());
    }
}
```

🧾 **Output:**

```
Result: avaJgnitsaM
Length: 10
Capacity: 21
```

---

## ⚡ **7. Performance Note: `String` vs. `StringBuilder`**

### 🔸 `String` Concatenation

```java
String result = "";
for (int i = 0; i < 1000; i++) {
    result += i;
}
```

* Creates 1000 new String objects (inefficient).

### 🔹 `StringBuilder`

```java
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append(i);
}
String result = sb.toString();
```

* Modifies the same buffer repeatedly (efficient).

---

## 🧭 **8. Summary**

| Aspect            | Key Takeaway                                             |
| :---------------- | :------------------------------------------------------- |
| **Purpose**       | Fast, mutable string manipulation                        |
| **Thread Safety** | Not synchronized (single-threaded only)                  |
| **Use Case**      | When repeatedly modifying strings                        |
| **Best Practice** | Use `StringBuilder` in loops and text construction tasks |
| **Alternative**   | Use `StringBuffer` in multi-threaded contexts            |

---

