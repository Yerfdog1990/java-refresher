# Pass-by-Value Vs. Pass-by-reference in Java

## Pass-by-Value in Java

Java is strictly **pass-by-value**. When you pass a variable to a method, you're actually passing a copy of the value, not the actual variable. This is true for both primitive types and object references.

### Example 1: Primitive Types (Pass-by-Value)
```java
public class PassByValueExample {
    public static void main(String[] args) {
        int number = 10;
        System.out.println("Before: " + number);  // Output: Before: 10
        modifyPrimitive(number);
        System.out.println("After: " + number);   // Output: After: 10
    }
    
    public static void modifyPrimitive(int num) {
        num = 20;  // This change is local to this method
    }
}
```
**Explanation**: The original `number` remains unchanged because only its value was passed to the method.

### Example 2: Object References (Still Pass-by-Value)
```java
class Person {
    String name;
    Person(String name) {
        this.name = name;
    }
}

public class PassByValueReference {
    public static void main(String[] args) {
        Person person = new Person("Alice");
        System.out.println("Before: " + person.name);  // Output: Before: Alice
        modifyReference(person);
        System.out.println("After modifyReference: " + person.name);  // Output: After: Bob
        reassignReference(person);
        System.out.println("After reassignReference: " + person.name); // Still "Bob"
    }
    
    public static void modifyReference(Person p) {
        p.name = "Bob";  // This affects the original object
    }
    
    public static void reassignReference(Person p) {
        p = new Person("Charlie");  // This doesn't affect the original reference
    }
}
```
**Explanation**:
1. `modifyReference` changes the object's state, which is visible to the caller.
2. `reassignReference` creates a new object and assigns it to the local parameter `p`, but this doesn't affect the original reference in `main`.

## Pass-by-Reference (Conceptual)

Java doesn't have true pass-by-reference like C++, but let's see what it would look like if it did (this is pseudo-code, not valid Java):

### Example 3: What Pass-by-Reference Would Look Like (Not Java)
```java
// This is NOT valid Java - just showing the concept
public class PassByReferenceExample {
    public static void main(String[] args) {
        int number = 10;
        System.out.println("Before: " + number);  // Output: Before: 10
        modifyByReference(&number);  // & means "address of" in C-style languages
        System.out.println("After: " + number);   // Would output: After: 20
    }
    
    // This is not valid Java
    public static void modifyByReference(int &num) {
        num = 20;  // This would modify the original variable
    }
}
```

## Key Differences

1. **Pass-by-Value**:
    - A copy of the value is passed to the method
    - Changes to the parameter don't affect the original
    - Java always uses pass-by-value

2. **Pass-by-Reference**:
    - The memory address (reference) of the variable is passed
    - Changes to the parameter directly affect the original
    - Java doesn't support this, but some languages like C++ do

## Important Note About Java Objects

When working with objects in Java, you're actually working with object references. While the reference is passed by value, the object it points to can be modified. This is why some people mistakenly say Java uses pass-by-reference for objects, but technically, it's still pass-by-value - the value being the object reference.

```java
public class ObjectExample {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder("Hello");
        System.out.println("Before: " + sb);  // Output: Before: Hello
        appendWorld(sb);
        System.out.println("After: " + sb);   // Output: After: Hello World
    }
    
    public static void appendWorld(StringBuilder builder) {
        builder.append(" World");  // Modifies the object that the reference points to
    }
}
```

In this example, the `StringBuilder` object is modified because both the original reference and the method parameter point to the same object in memory, but the reference itself was passed by value.