
---

# **Lesson Notes: Spring Expression Language (SpEL)**

## **1. Introduction**

In Spring Framework, **SpEL (Spring Expression Language)** is a powerful expression language that enables querying and manipulating objects at runtime. It is widely used throughout the Spring ecosystem — in Spring Core, Spring Security, Spring Data, and Spring Web — to dynamically evaluate values, call methods, and access bean properties.

SpEL can be used both:

* **Declaratively** — within annotations or XML configuration.
* **Programmatically** — through the `ExpressionParser` API.

SpEL expressions are often used with the `@Value` annotation to dynamically inject computed values into Spring beans.

---

## **2. Spring Bean Naming Convention**

In Spring, by default, the **bean name** (or ID) is derived from the class name but with the **first letter in lowercase**.

For example:

```java
@Component
public class SpELBeanA { }
```

By default, this bean’s name becomes **`spELBeanA`**, not **`SpELBeanA`**.

Hence, in SpEL expressions, you must always refer to the bean using its correct **ID**:

```java
@Value("#{spELBeanB.prop1}")  // ✅ correct
private Integer value;
```

If you mistakenly write `@Value("#{SpELBeanB.prop1}")`, you’ll get an error like:

> `org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'SpELBeanB' available`

This happens because Spring looks for a bean with the exact name specified.

---

## **3. SpEL Syntax**

A typical SpEL expression begins with:

* `#` → denotes the start of an expression.
* `{}` → encloses the expression content.

Example:

```java
@Value("#{2 + 3}")   // Evaluates to 5
private Integer result;
```

### Difference Between `#{}` and `${}`

| Symbol | Purpose              | Example                    | Description                                 |
| :----- | :------------------- | :------------------------- | :------------------------------------------ |
| `${}`  | Property placeholder | `@Value("${server.port}")` | Retrieves value from application.properties |
| `#{}`  | SpEL expression      | `@Value("#{2 + 3}")`       | Evaluates an expression dynamically         |

---

## **4. Adding SpEL Support**

SpEL is provided by the **spring-expression** library, which is automatically included when you use **Spring Boot Starter Web**.

You don’t need to add it manually — it comes transitively through:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

---

## **5. Basic SpEL Expressions**

Let’s create a test project under package `com.example.spel`.

### **Step 1: Create a Bean**

```java
package com.example.spel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SpELBeanA {

    @Value("#{2 + 3}")
    private Integer add;

    @Value("#{'Learn ' + 'Spring'}")
    private String addString;

    @Value("#{2 == 2}")
    private boolean equal;

    @Value("#{3 > 2 ? 'a' : 'b'}")
    private String ternary;

    public Integer getAdd() { return add; }
    public String getAddString() { return addString; }
    public boolean isEqual() { return equal; }
    public String getTernary() { return ternary; }
}
```

### **Step 2: Create a Test**

```java
package com.example.spel;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class SpELTest {

    @Autowired
    private SpELBeanA spELBeanA;

    @Test
    public void whenSpELBeanA_thenAllResolvedCorrectly() {
        assertNotNull(spELBeanA);
        System.out.println("Addition Result: " + spELBeanA.getAdd());
        System.out.println("Concatenated String: " + spELBeanA.getAddString());
        System.out.println("Logical Result: " + spELBeanA.isEqual());
        System.out.println("Ternary Result: " + spELBeanA.getTernary());
    }
}
```

Running this test shows:

```
Addition Result: 5
Concatenated String: Learn Spring
Logical Result: true
Ternary Result: a
```

---

## **6. Accessing Another Bean Using SpEL**

Let’s now explore bean-to-bean references.

### **Step 1: Create Second Bean**

```java
package com.example.spel;

import org.springframework.stereotype.Component;

@Component
public class SpELBeanB {

    private Integer prop1 = 10;

    public Integer getProp1() {
        return prop1;
    }
}
```

### **Step 2: Reference `SpELBeanB` from `SpELBeanA`**

```java
@Value("#{spELBeanB.prop1}")  // ✅ note lowercase 's'
private Integer otherBeanProperty;
```

If we incorrectly write:

```java
@Value("#{SpELBeanB.prop1}")  // ❌ wrong case
```

the test fails with:

> `Cannot resolve property or field 'SpELBeanB' - no such bean found`

### **Step 3: Validate with a Test**

When you rerun the test in debug mode, the value of `otherBeanProperty` will correctly resolve to `10`.

---

## **7. Advanced Expression Examples**

### **Arithmetic**

```java
@Value("#{19 + 1}") private int add;        // 20
@Value("#{36 / 2}") private int divide;     // 18
@Value("#{37 % 10}") private int modulo;    // 7
```

### **Relational**

```java
@Value("#{1 > 0}") private boolean greater; // true
@Value("#{1 == 2}") private boolean equal;  // false
```

### **Logical**

```java
@Value("#{true and false}") private boolean result1; // false
@Value("#{250 > 200 && 200 < 4000}") private boolean result2; // true
```

### **Conditional**

```java
@Value("#{2 > 1 ? 'A' : 'B'}") private String result; // "A"
```

### **Regex**

```java
@Value("#{'100' matches '\\d+'}") private boolean numeric; // true
```

---

## **8. Working with Collections**

You can access elements in Lists or Maps directly using SpEL:

```java
@Component("carPark")
public class CarPark {
    private Map<String, String> carsByDriver = Map.of(
        "Driver1", "Model1",
        "Driver2", "Model2"
    );

    public Map<String, String> getCarsByDriver() {
        return carsByDriver;
    }
}

@Component
public class Garage {
    @Value("#{carPark.carsByDriver['Driver1']}")
    private String driver1Car;  // "Model1"
}
```

---

## **9. Programmatic SpEL Evaluation**

You can also evaluate expressions **outside of configuration**, using the `SpelExpressionParser` API.

```java
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class SpELDemo {
    public static void main(String[] args) {
        ExpressionParser parser = new SpelExpressionParser();

        Expression exp = parser.parseExpression("'Hello World'.concat('!!!')");
        String message = exp.getValue(String.class);

        System.out.println(message);  // Output: Hello World!!!
    }
}
```

You can also access properties dynamically:

```java
Car car = new Car();
car.setModel("Model 3");

Expression exp = parser.parseExpression("model");
String result = exp.getValue(car, String.class);
System.out.println(result);  // Output: Model 3
```

---

## **10. Key Takeaways**

* SpEL enables **dynamic evaluation** of values and expressions at runtime.
* The expression syntax is **`#{...}`**, while **`${...}`** is used for property placeholders.
* Always use **correct bean naming** — Spring bean names start with a lowercase letter by default.
* SpEL supports **arithmetic, logical, conditional, regex, and collection operations**.
* You can use SpEL **both declaratively** (in annotations/XML) and **programmatically** via `ExpressionParser`.

---

✅ **Summary**
SpEL is a fundamental feature in Spring that enhances flexibility and reduces hardcoded configuration by allowing runtime expression evaluation.
Understanding SpEL gives you more control over dynamic bean configuration, property access, and conditional logic in Spring applications.

---
Perfect 👍 Here’s an **added section** for your lesson notes —
a professional **“Troubleshooting SpEL”** guide that fits academically and practically after the main notes you already have.

---

## **11. Troubleshooting SpEL: Common Errors and Fixes**

Even though Spring Expression Language (SpEL) is straightforward once understood, beginners often encounter a few common errors related to **bean naming**, **syntax**, or **data type mismatches**.
Below is a table summarizing these issues, their causes, and recommended fixes.

---

### **Table: Common SpEL Errors and How to Fix Them**

| **Error Message / Symptom**                                                                                       | **Cause**                                                                                                                                           | **Fix / Example**                                                                                                    |
| :---------------------------------------------------------------------------------------------------------------- | :-------------------------------------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------- |
| `org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'SpELBeanB' available`            | Bean name used in the expression is incorrect. By default, Spring converts the first letter of the class name to lowercase when registering a bean. | Use the correct lowercase bean ID. <br> ✅ `@Value("#{spELBeanB.prop1}")` instead of ❌ `@Value("#{SpELBeanB.prop1}")` |
| `org.springframework.expression.spel.SpelEvaluationException: EL1007E: Property or field 'xyz' cannot be found`   | The property or method you are trying to access does not exist in the target bean.                                                                  | Double-check property names and ensure they have a **getter** method.                                                |
| `org.springframework.expression.ParseException: Expression definition expected after '#'`                         | Missing curly braces `{}` or incorrect syntax in expression.                                                                                        | Use correct syntax: `#{...}` instead of `#...`. <br> ✅ `@Value("#{2 + 3}")`                                          |
| `org.springframework.expression.spel.SpelEvaluationException: EL1004E: Method call: Method xyz() cannot be found` | Attempting to call a method that does not exist or has the wrong parameters.                                                                        | Verify method name and argument types match those defined in the class.                                              |
| `Type conversion error: cannot convert value of type ...`                                                         | The evaluated expression returns a type different from the field it’s assigned to.                                                                  | Ensure expression result matches the target field type. Example: don’t assign a `String` to an `Integer` field.      |
| `IllegalStateException: Failed to load ApplicationContext`                                                        | Often caused by unresolved SpEL expressions or bean dependency loops.                                                                               | Check for circular dependencies and invalid SpEL references. Run in debug mode for detailed trace.                   |

---

### **Example: Common Bean Naming Mistake**

```java
@Component
public class SpELBeanB {
    private int prop1 = 10;
    public int getProp1() { return prop1; }
}
```

Wrong ❌

```java
@Value("#{SpELBeanB.prop1}")   // Fails: Spring bean name is lowercase
private int value;
```

Correct ✅

```java
@Value("#{spELBeanB.prop1}")
private int value;
```

---

### **Pro Tip:**

If you are unsure of your bean names during debugging, you can list all beans currently managed by the Spring context:

```java
@Autowired
private ApplicationContext context;

@PostConstruct
public void printBeanNames() {
    Arrays.stream(context.getBeanDefinitionNames())
          .filter(name -> name.contains("spEL"))
          .forEach(System.out::println);
}
```

This will print the actual bean IDs that Spring has registered — helping you verify naming consistency.

---

### **Key Lessons from Common Mistakes**

1. ✅ Always use **lowercase-first bean names** when referencing with SpEL.
2. ✅ Ensure properties have **public getters** if you want to access them via SpEL.
3. ✅ Match **data types** between your SpEL result and target field.
4. ✅ Use `#{}` syntax for expressions, `${}` for property placeholders.
5. ✅ Use debugging or logging to inspect registered bean names and values.

---

### **12. Summary**

By understanding and handling these common issues:

* You’ll avoid typical runtime crashes caused by missing beans or properties.
* Your SpEL expressions will resolve smoothly at startup.
* You’ll write cleaner, more dynamic Spring code that’s easy to maintain and debug.

---

