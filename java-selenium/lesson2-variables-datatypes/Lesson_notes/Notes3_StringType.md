
# **Lesson Notes: The String Type — Strings and Text**

---

## **1. The String Type**

The **`String`** type is one of the most commonly used types in Java.
It is used to **store and manipulate text**, such as names, messages, or sentences.

Unlike primitive types (`int`, `double`, etc.), `String` is a **class**, which means you can call **methods** on it to perform various operations like changing case or checking length.

✅ **Key facts:**

* `String` values are enclosed in **double quotes** (`" "`).
* `String` starts with a **capital letter**, because it is a **class** in Java.
* Every Java object can be converted to a **string representation**.

**Example:**

```java
String message = "Hello, Java!";
System.out.println(message);
```

**Output:**

```
Hello, Java!
```

---

## **2. Creating String Variables**

To create a variable that can store text, use the following syntax:

```java
String variableName;
```

### **Examples:**

| **Statement**     | **Description**                                      |
| ----------------- | ---------------------------------------------------- |
| `String name;`    | Creates a variable named `name` that can store text. |
| `String message;` | Creates a variable named `message`.                  |
| `String city;`    | Creates a variable named `city`.                     |

You can also declare multiple `String` variables in one line:

```java
String firstName, lastName, city;
```

---

## **3. Assigning Values to String Variables**

To assign text to a `String` variable, enclose the text in **double quotes**:

```java
String name = "Steve";
String city = "New York";
String message = "Hello!";
```

If you forget the quotes, the compiler will throw an error.

### **Example:**

❌ `String name = Steve;`
✅ `String name = "Steve";`

---

## **4. Initializing String Variables**

You can **declare and assign** a `String` variable in one line, or separately:

```java
String greeting = "Good Morning";  // Declaration + Initialization
```

Or

```java
String greeting;
greeting = "Good Morning";         // Separate declaration and assignment
```

You can also initialize multiple variables at once:

```java
String name1 = "Steve", name2 = "Alice", name3 = "Bob";
```

⚠️ **Important:**
If you declare a variable without assigning a value and then try to use it, the program **won’t compile**.

```java
String name;
System.out.println(name); // ❌ Error: variable 'name' might not have been initialized
```

---

## **5. String Concatenation (Joining Strings)**

You can **combine two or more strings** using the **`+` operator** — this is called **concatenation**.

### **Examples:**

```java
String firstName = "Steve";
String lastName = "Jobs";
String fullName = firstName + " " + lastName;

System.out.println(fullName);
```

**Output:**

```
Steve Jobs
```

You can also join text directly:

```java
String message = "Hello, " + "World!";
System.out.println(message);
```

**Output:**

```
Hello, World!
```

---

### **Concatenation with Numbers**

When you add a number to a string, Java **automatically converts** the number to text.

```java
int age = 25;
String info = "Age: " + age;
System.out.println(info);
```

**Output:**

```
Age: 25
```

⚠️ **Note:** The order of concatenation matters.

```java
int a = 5;
String result = a + a + "1" + a;
System.out.println(result);
```

**Output:**

```
1015
```

Explanation: `(5 + 5)` is calculated first (→ 10), then concatenated as a string: `"10" + "1" + "5"` → `"1015"`.

---

## **6. Empty Strings and Spaces**

You can represent an **empty string** (a string with no characters) like this:

```java
String empty = "";
```

And a **space** is represented by `" "`:

```java
String space = " ";
```

**Example:**

```java
String first = "Hello";
String second = "World";
System.out.println(first + " " + second);
```

**Output:**

```
Hello World
```

---

## **7. Converting Between Strings and Numbers**

### **A. Converting a Number to a String**

Just concatenate the number with an empty string:

```java
int number = 123;
String str = "" + number;
System.out.println(str); // "123"
```

Or use `String.valueOf()`:

```java
String str = String.valueOf(123);
```

### **B. Converting a String to a Number**

Use the `Integer.parseInt()` method:

```java
String text = "123";
int num = Integer.parseInt(text);
System.out.println(num + 10); // 133
```

⚠️ The string must contain only digits; otherwise, an error occurs.

---

## **8. Useful String Methods**

### **1. `length()`**

Returns the number of characters in a string.

```java
String city = "Rome";
int len = city.length(); // len = 4
```

### **2. `toLowerCase()`**

Converts all characters to lowercase.

```java
String word = "HELLO";
System.out.println(word.toLowerCase()); // hello
```

### **3. `toUpperCase()`**

Converts all characters to uppercase.

```java
String word = "java";
System.out.println(word.toUpperCase()); // JAVA
```

---

## **9. Example Program: Working with Strings**

```java
public class StringExamples {
    public static void main(String[] args) {
        String name = "Amigo";
        String city = "Rome";
        String message = "Hello, " + name + " from " + city + "!";

        System.out.println(message);
        System.out.println("Message length: " + message.length());
        System.out.println("Uppercase: " + message.toUpperCase());
        System.out.println("Lowercase: " + message.toLowerCase());
    }
}
```

**Output:**

```
Hello, Amigo from Rome!
Message length: 23
Uppercase: HELLO, AMIGO FROM ROME!
Lowercase: hello, amigo from rome!
```

---

## **10. Summary**

| **Concept**             | **Description**             | **Example**             |
| ----------------------- | --------------------------- | ----------------------- |
| String Type             | Stores text values          | `String name = "John";` |
| Concatenation           | Combines text and variables | `"Hello " + name`       |
| Empty String            | String with no characters   | `""`                    |
| Convert Number → String | `String.valueOf(123)`       | `"123"`                 |
| Convert String → Number | `Integer.parseInt("456")`   | `456`                   |
| `length()`              | Returns string length       | `"Hello".length()` → 5  |
| `toLowerCase()`         | Converts to lowercase       | `"JAVA".toLowerCase()`  |
| `toUpperCase()`         | Converts to uppercase       | `"java".toUpperCase()`  |

---

### **Key Takeaways**

* `String` is a **class**, not a primitive type.
* Always enclose string values in **double quotes**.
* You can **join strings** using the `+` operator.
* Strings can be **converted** to and from numbers.
* Common methods include `length()`, `toLowerCase()`, and `toUpperCase()`.

---