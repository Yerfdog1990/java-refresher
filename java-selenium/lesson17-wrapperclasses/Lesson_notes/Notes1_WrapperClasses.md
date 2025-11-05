
---

# **Wrapper Classes in Java**

## **1. Overview**

In Java, **wrapper classes** are special classes that allow primitive data types to be represented as objects.
Each of the eight primitive types in Java has a corresponding wrapper class provided in the **`java.lang`** package.

| Primitive Type | Wrapper Class |
| -------------- | ------------- |
| `byte`         | `Byte`        |
| `short`        | `Short`       |
| `int`          | `Integer`     |
| `long`         | `Long`        |
| `float`        | `Float`       |
| `double`       | `Double`      |
| `char`         | `Character`   |
| `boolean`      | `Boolean`     |

Wrapper classes are used when an object representation of a primitive is required — for example, when working with **Collections** such as `ArrayList`, `HashMap`, etc., which can only store objects, not primitives.

---

## **2. Purpose of Wrapper Classes**

The main reasons for using wrapper classes include:

* To use **primitive types** in **object-oriented contexts** (e.g., collections or generics).
* To use **utility methods** available in wrapper classes such as parsing (`Integer.parseInt()`), conversions (`Double.valueOf()`), etc.
* To **store null values**, which primitive types cannot.
* To facilitate **autoboxing** and **unboxing** (automatic conversion between primitives and their wrapper classes).

---

## **3. Primitive to Wrapper Conversion**

There are two ways to convert a primitive value into a wrapper object:

### (a) Using the `valueOf()` method (Recommended)

```java
class PrimitiveToWrapperExample {
    public static void main(String[] args) {
        int num = 10;

        // Convert int to Integer using valueOf()
        Integer obj = Integer.valueOf(num);

        System.out.println("Primitive value: " + num);
        System.out.println("Wrapper object: " + obj);
    }
}
```

**Output:**

```
Primitive value: 10
Wrapper object: 10
```

> ✅ The `valueOf()` method is efficient because it caches commonly used values between -128 and 127.

---

### (b) Using Constructors (Deprecated since Java 9)

```java
Integer obj = new Integer(10);  // Deprecated
```

> ⚠️ The use of constructors for wrapper objects (e.g., `new Integer(10)`) is discouraged after Java 9. Use `valueOf()` instead.

---

## **4. Wrapper to Primitive Conversion**

To retrieve the primitive value from a wrapper object, use the **`xxxValue()`** methods such as `intValue()`, `doubleValue()`, etc.

```java
class WrapperToPrimitiveExample {
    public static void main(String[] args) {
        Integer obj = Integer.valueOf(50);

        // Convert Integer object to primitive int
        int num = obj.intValue();

        System.out.println("Wrapper object: " + obj);
        System.out.println("Primitive value: " + num);
    }
}
```

**Output:**

```
Wrapper object: 50
Primitive value: 50
```

---

## **5. Autoboxing and Unboxing**

From **Java 5**, the compiler automatically handles conversion between primitives and wrapper objects:

### (a) **Autoboxing**

Automatic conversion from primitive → wrapper object.

```java
import java.util.ArrayList;
import java.util.List;

public class AutoboxingExample {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();

        // Autoboxing: int → Integer
        list.add(10);
        list.add(20);

        System.out.println("ArrayList: " + list);
    }
}
```

**Output:**

```
ArrayList: [10, 20]
```

> Here, the compiler automatically converts the `int` values `10` and `20` into `Integer` objects using `Integer.valueOf()`.

---

### (b) **Unboxing**

Automatic conversion from wrapper object → primitive type.

```java
class UnboxingExample {
    public static void main(String[] args) {
        Integer obj = Integer.valueOf(30);

        // Unboxing: Integer → int
        int num = obj;

        System.out.println("Wrapper object: " + obj);
        System.out.println("Primitive value: " + num);
    }
}
```

**Output:**

```
Wrapper object: 30
Primitive value: 30
```

> ⚠️ Be careful during unboxing — if the wrapper object is `null`, a `NullPointerException` will occur.

---

## **6. Type Casting in Java (Related Concept)**

Wrapper classes are often used with **type conversion** or **type casting**.

### (a) **Widening (Implicit Type Casting)**

Converting a smaller type to a larger type — happens automatically.

```java
class WideningExample {
    public static void main(String[] args) {
        int num = 10;
        double result = num;  // int → double
        System.out.println("The integer value: " + num);
        System.out.println("The double value: " + result);
    }
}
```

**Output:**

```
The integer value: 10
The double value: 10.0
```

---

### (b) **Narrowing (Explicit Type Casting)**

Converting a larger type to a smaller type — requires explicit casting.

```java
class NarrowingExample {
    public static void main(String[] args) {
        double num = 10.99;
        int result = (int) num;  // double → int
        System.out.println("The double value: " + num);
        System.out.println("The integer value: " + result);
    }
}
```

**Output:**

```
The double value: 10.99
The integer value: 10
```

---

## **7. Conversion Between Primitive and String**

### (a) **Primitive to String**

```java
class PrimitiveToStringExample {
    public static void main(String[] args) {
        int num = 100;
        String str = String.valueOf(num);
        System.out.println("String value: " + str);
    }
}
```

### (b) **String to Primitive**

```java
class StringToPrimitiveExample {
    public static void main(String[] args) {
        String str = "123";
        int num = Integer.parseInt(str);
        System.out.println("Integer value: " + num);
    }
}
```

---

## **8. Advantages of Wrapper Classes**

1. **Object Compatibility:**
   Collections and Generics in Java work with objects only, not primitives. Wrapper classes enable primitive data types to be stored in collections.

   ```java
   // Error: ArrayList<int> not allowed
   // ArrayList<int> list = new ArrayList<>();

   ArrayList<Integer> list = new ArrayList<>(); // Works fine
   ```

2. **Utility Methods:**
   Wrapper classes provide useful methods for parsing and conversion (e.g., `parseInt()`, `valueOf()`).

3. **Null Values Support:**
   Wrapper objects can be assigned `null`, whereas primitives cannot.

   ```java
   Integer a = null;  // Valid
   // int b = null;   // Invalid
   ```

4. **Used in Autoboxing/Unboxing:**
   Enables automatic conversion between primitives and objects.

---

## **9. Summary Table**

| Concept              | Description                  | Example                    |
| -------------------- | ---------------------------- | -------------------------- |
| **Autoboxing**       | Primitive → Wrapper          | `Integer obj = 10;`        |
| **Unboxing**         | Wrapper → Primitive          | `int num = obj;`           |
| **valueOf()**        | Converts primitive to object | `Integer.valueOf(5);`      |
| **xxxValue()**       | Converts object to primitive | `obj.intValue();`          |
| **parseInt()**       | Converts String to int       | `Integer.parseInt("123");` |
| **String.valueOf()** | Converts primitive to String | `String.valueOf(123);`     |

---

## **10. Conclusion**

Wrapper classes play a vital role in Java by bridging the gap between **primitive data types** and **objects**.
They make it possible to use primitives where objects are required, such as in collections, and provide convenient methods for type conversion.
With **autoboxing** and **unboxing**, Java simplifies these conversions, making code cleaner and more intuitive.

However, developers should be aware of potential **performance overhead** due to object creation and the risk of **NullPointerException** during unboxing when wrapper objects are `null`.

---
