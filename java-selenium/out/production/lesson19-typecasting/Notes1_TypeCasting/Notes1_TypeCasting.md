## 📝 Java Type Casting: Lesson Notes

### What is Type Casting?

**Type casting** in Java is the process of converting one data type into another. This conversion can be performed on both **primitive data types** and **objects**.

#### Why is Type Casting Required?

Java has different primitive data types that require different amounts of memory space. Type casting is required to solve **compatibility issues** that arise when assigning a value of one data type to a variable of another data type. If the data types are already compatible, the compiler performs the casting automatically.

-----

### 1\. Primitive Type Casting

There are two types of type casting for primitive data types: **Widening** and **Narrowing**.

#### A. Widening Type Casting (Implicit/Automatic)

**Widening type casting** involves converting a **smaller** data type to a **larger** data type, which is done automatically by the Java compiler. This process is also known as **Implicit** or **Automatic Type Casting** because it does not require an explicit command from the programmer and involves no loss of information.

**Conversion Order (Smallest to Largest):**
$$\text{byte} \rightarrow \text{short} \rightarrow \text{char} \rightarrow \text{int} \rightarrow \text{long} \rightarrow \text{float} \rightarrow \text{double}$$

| Type | Size (Bytes) |
| :--- | :--- |
| **byte** | 1 |
| **short** | 2 |
| **char** | 2 |
| **int** | 4 |
| **long** | 8 |
| **float** | 4 |
| **double** | 8 |

**Code Example: Widening Type Casting**
This example demonstrates automatic conversion from a smaller type (`int`) to a larger type (`long` and `float`).

```java
//Automatic type conversion
public class WideningExample {
   public static void main(String[] args) {

       int i = 100;
       System.out.println("int value: " + i); // Output: int value: 100

       // int to long type (Automatic)
       long l = i; 
       System.out.println("int to long value: " + l); // Output: int to long value: 100

       // long to float type (Automatic)
       float f = l; 
       System.out.println("int to float value: " + f); // Output: int to float value: 100.0
       
       char c = 'a';
       // char to int type (Automatic - converts to ASCII value)
       i = c;
       System.out.println("char to int value: " + i); // Output: char to int value: 97
   }
}
```

#### B. Narrowing Type Casting (Explicit/Manual)

**Narrowing type casting** involves converting a **larger** data type to a **smaller** data type. This process is known as **Explicit** or **Manual Type Casting** because it must be done manually by the programmer.

**Syntax:** To perform a narrowing cast, you must write the **target data type in parentheses `()`** before the variable or value being cast.

$$\text{TargetDataType } \text{newVar} = \text{(TargetDataType) } \text{initialVar;}$$

**Risk:** Narrowing casting may result in a **possible loss of precision** because the larger value cannot fully fit into the smaller data type.

**Conversion Order (Largest to Smallest):**
$$\text{double} \rightarrow \text{float} \rightarrow \text{long} \rightarrow \text{int} \rightarrow \text{char} \rightarrow \text{short} \rightarrow \text{byte}$$

**Code Example: Narrowing Type Casting**
This example demonstrates manual conversion, which results in a loss of the fractional part.

```java
//Manual Type Conversion
public class NarrowingExample {
   public static void main(String[] arg) {

       // double is a large type
       double d = 97.04;
       System.out.println("double value: " + d); // Output: double value: 97.04

       // Narrowing type casting from double to long (Manual)
       long l = (long) d; // Fractional part (.04) is lost
       System.out.println("long value: " + l); // Output: long value: 97

       // Narrowing type casting from long to int (Manual)
       int i = (int) l; 
       System.out.println("int value: " + i); // Output: int value: 97

       // Narrowing type casting from int to char (Manual - converts ASCII value to char)
       char c = (char) i;
       System.out.println("char value: " + c); // Output: char value: a
   }
}
```

-----

### 2\. Object Type Casting

Type casting also applies to object types, specifically when dealing with inheritance, known as **Upcasting** and **Downcasting**.

#### A. Upcasting (Explicit or Implicit)

**Upcasting** is the conversion of a **subclass object** into a **superclass type**. It happens automatically when assigning a subclass object to a superclass reference, but can also be done explicitly.

**Result:** Upcasting allows you to treat an object in a generalized way, useful for polymorphism. The upcasted reference can only access methods and fields defined in the superclass, even if they are overridden in the subclass.

**Code Example: Explicit Upcasting**

```java
class Animal {
    void makeSound() {
        System.out.println("Some generic animal sound");
    }
}

class Dog extends Animal {
    void makeSound() {
        System.out.println("Bark!"); // Overridden method
    }
}

public class UpcastingExample {
    public static void main(String[] args) {
        Dog dog = new Dog();
        Animal animal = (Animal) dog; // Explicit upcasting
        animal.makeSound(); // Output: Bark! (Polymorphism in action)
    }
}
```

#### B. Downcasting (Explicit and Checked)

**Downcasting** is the conversion of a **superclass reference** back into a **subclass reference**. It is the opposite of upcasting and **must be done explicitly**.

**Risk:** If the object being cast is not actually an instance of the target subclass, a **`ClassCastException`** will be thrown at runtime.

**Safe Practice:** Always use the `instanceof` operator to verify the object's true type before attempting downcasting.

**Code Example: Explicit Downcasting**

```java
class Animal {
    // ...
}

class Dog extends Animal {
    void wagTail() {
        System.out.println("Tail wagging!"); // Dog-specific method
    }
}

public class DowncastingExample {
    public static void main(String[] args) {
        Animal animal = new Dog(); // Implicit Upcasting

        if (animal instanceof Dog) { // Check for safe downcasting
            Dog dog = (Dog) animal; // Explicit downcasting
            dog.wagTail(); // Output: Tail wagging! (Accesses subclass-specific method)
        } else {
            System.out.println("Downcasting not possible!");
        }
    }
}
```

-----

### 3\. Type Casting Between Numeric Types and Strings

Converting between numeric primitives and `String` objects is a common requirement in Java, particularly for I/O operations.

#### A. Converting Numeric Types to Strings

This converts a numeric value to its string representation.

| Method | Example |
| :--- | :--- |
| **`String.valueOf()`** | `String str1 = String.valueOf(123);` |
| **Concatenation** | `String str2 = 123 + "";` |

**Code Example:**

```java
public class NumericToStringExample {
    public static void main(String[] args) {
        int number = 123;
        String str1 = String.valueOf(number);
        System.out.println("Using String.valueOf(): " + str1); 

        String str2 = number + "";
        System.out.println("Using concatenation: " + str2); 
    }
}
// Output:
// Using String.valueOf(): 123
// Using concatenation: 123
```

#### B. Converting Strings to Numeric Types

This converts a string representation into a numeric primitive type.

| Method | Example |
| :--- | :--- |
| **`Integer.parseInt()`** | `int intValue = Integer.parseInt("456");` |
| **`Double.parseDouble()`** | `double doubleValue = Double.parseDouble("456.78");` |

**Note:** If the string is not a valid numeric representation, these methods will throw a **`NumberFormatException`**.

**Code Example:**

```java
public class StringToNumericExample {
    public static void main(String[] args) {
        String str = "456";
        
        // Convert to int
        int intValue = Integer.parseInt(str);
        System.out.println("Integer value: " + intValue); 

        // Convert to double
        double doubleValue = Double.parseDouble(str);
        System.out.println("Double value: " + doubleValue); 
    }
}
// Output:
// Integer value: 456
// Double value: 456.0
```