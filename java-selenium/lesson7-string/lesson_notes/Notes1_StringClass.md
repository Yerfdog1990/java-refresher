
-----

# 📚 Java String Class: Lesson Notes

The **String class** in Java ($\text{java.lang.String}$) is used to create and manipulate sequences of characters. It is fundamental to Java programming.

## 🌟 Key Features of the String Class

1.  **Immutable**
    * **Definition:** Once a $\text{String}$ object is created, its value *cannot* be changed. Any operation that appears to modify a $\text{String}$ (like concatenation or case conversion) actually returns a **new** $\text{String}$ object.
    * **Why?** Immutability ensures thread safety and allows $\text{String}$ objects to be stored in the **String Constant Pool** for memory efficiency.
    * **Implication:** Direct character modification is impossible.
    * **Example (Why direct change fails):**
      ```java
      public class StringImmutability {
          public static void main(String[] args) {
              String text = "hello";
              // text.charAt(0) = 'H';  // This would cause a compile-time error
              
              // Instead, you use methods that return a new String:
              String newText = text.toUpperCase(); // A new String "HELLO" is created
              System.out.println("Original String: " + text);    // Output: hello
              System.out.println("New String: " + newText);      // Output: HELLO
          }
      }
      ```
    -----
2.  **Thread-Safe**
    * Because $\text{String}$ objects are immutable, they are **inherently thread-safe**. Multiple threads can safely access the same $\text{String}$ object simultaneously without the need for external synchronization.
    -----
3.  **Implements Interfaces**
    * The $\text{String}$ class implements three key interfaces:
        * **$\text{Serializable}$:** Allows string objects to be converted into a byte stream (serialization).
        * **$\text{CharSequence}$:** Provides a uniform, read-only access to a sequence of character values (e.g., using $\text{charAt()}$ and $\text{length()}$).
        * **$\text{Comparable<String>}$:** Enables comparing two strings lexicographically using the $\text{compareTo()}$ method.

-----

## 🛠️ String Constructors in Java

While strings are most commonly created using **string literals** (e.g., $\text{String s = "Hello";}$), the $\text{String}$ class provides various constructors to create new objects from different data sources.

| Constructor | Description |
| :--- | :--- |
| $\text{String()}$ | Creates an empty string. |
| $\text{String(String original)}$ | Creates a new $\text{String}$ object that is a copy of the given string. |
| $\text{String(char[] value)}$ | Creates a string from a character array. |
| $\text{String(char[] value, int offset, int count)}$ | Creates a string from a subarray of a character array. |
| $\text{String(byte[] bytes)}$ | Decodes a byte array using the platform's default charset to create a string. |

### Example of String Constructors

```java
public class StringConstructorExample {
    public static void main(String[] args) {
        // 1. Using a String Literal (Most common)
        String strLiteral = "Hello Literal";
        System.out.println("Literal: " + strLiteral);

        // 2. Using new keyword (allocates memory in the heap, not the constant pool)
        String strNew = new String("Hello New");
        System.out.println("Using new: " + strNew);

        // 3. From a character array
        char[] charArray = { 'J', 'A', 'V', 'A' };
        String strFromChars = new String(charArray);
        System.out.println("From char array: " + strFromChars);

        // 4. From a subarray of characters
        String strSubArray = new String(charArray, 1, 3); // 'A', 'V', 'A'
        System.out.println("From char subarray: " + strSubArray);

        // 5. From a byte array (ASCII values: 72='H', 101='e', 108='l', 111='o')
        byte[] byteArray = { 72, 101, 108, 108, 111 };
        String strFromBytes = new String(byteArray);
        System.out.println("From byte array: " + strFromBytes);
    }
}
```

**Output:**

```
Literal: Hello Literal
Using new: Hello New
From char array: JAVA
From char subarray: AVA
From byte array: Hello
```

-----

## 💻 Essential String Methods and Examples

The $\text{String}$ class offers a rich set of methods for various operations.

### 1\. Character Access and Length

| Method | Description | Return Type |
| :--- | :--- | :--- |
| $\text{length()}$ | Returns the number of characters in the string. | $\text{int}$ |
| $\text{charAt(int index)}$ | Returns the $\text{char}$ value at the specified index (0-based). | $\text{char}$ |
| $\text{toCharArray()}$ | Converts the string to a new character array. | $\text{char[]}$ |

```java
public class AccessMethods {
    public static void main(String[] args) {
        String s = "Coding";

        // length()
        System.out.println("Length: " + s.length()); // Output: 6

        // charAt(index)
        System.out.println("Char at index 1: " + s.charAt(1)); // Output: o (index 0 is 'C')

        // toCharArray()
        char[] arr = s.toCharArray();
        System.out.println("Character array: " + arr[0] + ", " + arr[5]); // Output: C, g
    }
}
```

### 2\. Comparison Methods

| Method | Description | Return Type |
| :--- | :--- | :--- |
| $\text{equals(Object anObject)}$ | Compares this string to the specified object. Returns $\text{true}$ if the contents are identical (case-sensitive). | $\text{boolean}$ |
| $\text{equalsIgnoreCase(String anotherString)}$ | Compares two strings, ignoring case differences. | $\text{boolean}$ |
| $\text{compareTo(String anotherString)}$ | Compares two strings lexicographically (alphabetically). Returns $\text{0}$ if equal, a **negative** value if this string is lexicographically smaller, and a **positive** value if larger. | $\text{int}$ |

```java
public class ComparisonMethods {
    public static void main(String[] args) {
        String s1 = "Java";
        String s2 = "java";
        String s3 = "Java";

        // equals() - Case-sensitive
        System.out.println("s1 equals s2: " + s1.equals(s2));        // Output: false
        System.out.println("s1 equals s3: " + s1.equals(s3));        // Output: true

        // equalsIgnoreCase()
        System.out.println("s1 equalsIgnoreCase s2: " + s1.equalsIgnoreCase(s2)); // Output: true

        // compareTo()
        System.out.println("s1 compareTo s2: " + s1.compareTo(s2));  // Output: Negative value ('J' < 'j')
        System.out.println("s1 compareTo s3: " + s1.compareTo(s3));  // Output: 0
        System.out.println("s2 compareTo s1: " + s2.compareTo(s1));  // Output: Positive value ('j' > 'J')
    }
}
```

### 3\. Search Methods

| Method | Description | Return Type |
| :--- | :--- | :--- |
| $\text{contains(CharSequence s)}$ | Returns $\text{true}$ if this string contains the specified sequence of characters. | $\text{boolean}$ |
| $\text{indexOf(String str)}$ | Returns the index of the first occurrence of the specified substring. Returns $\text{-1}$ if not found. | $\text{int}$ |
| $\text{lastIndexOf(String str)}$ | Returns the index of the last occurrence of the specified substring. | $\text{int}$ |
| $\text{startsWith(String prefix)}$ | Tests if the string begins with the specified prefix. | $\text{boolean}$ |
| $\text{endsWith(String suffix)}$ | Tests if the string ends with the specified suffix. | $\text{boolean}$ |

```java
public class SearchMethods {
    public static void main(String[] args) {
        String s = "Hello World World";

        // contains()
        System.out.println("Contains 'World': " + s.contains("World")); // Output: true

        // indexOf()
        System.out.println("First 'o' at index: " + s.indexOf('o'));    // Output: 4
        System.out.println("First 'World' at index: " + s.indexOf("World")); // Output: 6

        // lastIndexOf()
        System.out.println("Last 'World' at index: " + s.lastIndexOf("World")); // Output: 12

        // startsWith() and endsWith()
        System.out.println("Starts with 'He': " + s.startsWith("He")); // Output: true
        System.out.println("Ends with 'ld': " + s.endsWith("ld"));   // Output: false (ends with 'World')
    }
}
```

### 4\. Manipulation Methods

| Method | Description | Return Type |
| :--- | :--- | :--- |
| $\text{concat(String str)}$ | Concatenates the specified string to the end of this string (or use the $\text{+}$ operator). | $\text{String}$ |
| $\text{substring(int beginIndex)}$ | Returns a new string that is a substring starting from $\text{beginIndex}$ to the end. | $\text{String}$ |
| $\text{substring(int beginIndex, int endIndex)}$ | Returns a new string starting from $\text{beginIndex}$ up to (but not including) $\text{endIndex}$. | $\text{String}$ |
| $\text{replace(char oldChar, char newChar)}$ | Returns a new string resulting from replacing all occurrences of $\text{oldChar}$ with $\text{newChar}$. | $\text{String}$ |
| $\text{toUpperCase()}$ | Converts all characters to uppercase. | $\text{String}$ |
| $\text{toLowerCase()}$ | Converts all characters to lowercase. | $\text{String}$ |
| $\text{trim()}$ | Returns a string with leading and trailing whitespace removed. | $\text{String}$ |

```java
public class ManipulationMethods {
    public static void main(String[] args) {
        String s = "   Java Programming   ";
        String t = " is fun.";

        // concat()
        String combined = s.trim().concat(t);
        System.out.println("Concatenated: " + combined); // Output: Java Programming is fun.

        // substring()
        System.out.println("Substring (0 to 4): " + combined.substring(0, 4)); // Output: Java (end index is exclusive)
        System.out.println("Substring (from 5): " + combined.substring(5));    // Output: Programming is fun.

        // replace()
        String replaced = combined.replace('a', '@');
        System.out.println("Replaced 'a' with '@': " + replaced); // Output: J@v@ Progr@mming is fun.

        // toUpperCase() and toLowerCase()
        System.out.println("Uppercase: " + combined.toUpperCase()); // Output: JAVA PROGRAMMING IS FUN.

        // trim()
        System.out.println("Trimmed: '" + s.trim() + "'"); // Output: 'Java Programming'
    }
}
```
