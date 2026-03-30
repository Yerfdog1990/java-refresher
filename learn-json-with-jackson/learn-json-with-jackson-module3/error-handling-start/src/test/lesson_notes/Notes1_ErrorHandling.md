
---

# ⚠️ Error Handling in Jackson 

---

## 1. Overview

In real-world applications, JSON data is rarely perfect. Common issues include:

* Extra fields
* Missing values
* Incorrect data types
* Structural mismatches

---

### 🔹 Default Jackson Behavior

Jackson has a **mixed strictness model**:

| Aspect                           | Behavior     |
| -------------------------------- | ------------ |
| Structure (fields, shape)        | ❌ Strict     |
| Type coercion (some conversions) | ✅ Permissive |

---

### 🎯 Goal of Error Handling

* Understand **common exceptions**
* Configure Jackson to be:

    * ✅ More **lenient** (tolerant)
    * ✅ More **strict** (enforce contract)
* Build **robust and resilient applications**

---

## 2. Common Jackson Exceptions

---

### 🔹 2.1 `UnrecognizedPropertyException`

📌 **Cause:**

* JSON contains fields **not present** in Java class

---

### 🔹 2.2 `MismatchedInputException`

📌 **Cause:**

* JSON structure **does not match expected type**

    * Example: object instead of array

---

### 🔹 2.3 `InvalidDefinitionException`

📌 **Cause:**

* Jackson cannot **instantiate a class**

    * Missing default constructor
    * Invalid creator
    * Missing serializers

---

## 3. Handling `UnrecognizedPropertyException`

---

### 🔹 3.1 Failing Example

```java
@Test
void givenUnknownProperty_whenUsingDefaultMapper_thenFail() {
    ObjectMapper mapper = new ObjectMapper();

    String json = """
      {
        "code": "C2",
        "name": "Campaign 2",
        "description": "The description of Campaign 2",
        "budget": 100
      }
    """;

    assertThrows(UnrecognizedPropertyException.class,
        () -> mapper.readValue(json, Campaign.class));
}
```

---

### ❌ Problem

* JSON contains `"budget"`
* `Campaign` class does **not** have this field

👉 Jackson fails strictly

---

### 🔹 3.2 Solution: Ignore Unknown Fields

```java
ObjectMapper mapper = new ObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
```

```java
Campaign campaign = mapper.readValue(json, Campaign.class);
```

---

### ✅ Result

* Unknown fields are **ignored**
* Known fields are still mapped

---

### 🧠 Key Insight

👉 This is essential when:

* Consuming **external APIs**
* Handling **evolving schemas**

---

## 4. Handling `MismatchedInputException`

---

### 🔹 4.1 Failing Example

```java
@Test
void givenObjectToArrayProperty_whenUsingDefaultMapper_thenFail() {
    ObjectMapper mapper = new ObjectMapper();

    String json = """
      {
        "code": "C2",
        "name": "Campaign 2",
        "description": "The description of Campaign 2",
        "tasks": {
          "code": 101,
          "name": "Task A"
        }
      }
    """;

    assertThrows(MismatchedInputException.class,
        () -> mapper.readValue(json, Campaign.class));
}
```

---

### ❌ Problem

* `tasks` expected: **Array (Set<Task>)**
* JSON provides: **Single Object**

👉 Jackson cannot convert object → collection

---

### 🔹 4.2 Solution: Accept Single Value as Array

```java
ObjectMapper mapper = new ObjectMapper()
    .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
```

```java
Campaign campaign = mapper.readValue(json, Campaign.class);
```

---

### ✅ Result

* Single object → treated as **array with one element**

---

### 🧠 Key Insight

👉 Useful when:

* API responses are **inconsistent**
* Sometimes return:

    * Single object
    * OR array

---

### ⚠️ Best Practice

* Prefer fixing API contract
* Use this feature only when necessary

---

## 5. Handling `InvalidDefinitionException`

---

### 🔹 5.1 Failing Example

```java
@Test
void givenNoDefaultConstructor_whenUsingDefaultMapper_thenFail() {
    ObjectMapper mapper = new ObjectMapper();

    String json = """
      {
        "code": "C2",
        "name": "Campaign 2",
        "description": "The description of Campaign 2"
      }
    """;

    assertThrows(InvalidDefinitionException.class,
        () -> mapper.readValue(json, Task.class));
}
```

---

### ❌ Problem

* `Task` class has **no default constructor**

👉 Jackson cannot instantiate object

---

### 🔹 5.2 Solution: Add Default Constructor

```java
public class Task {

    public Task() {
    }

    // fields, getters, setters
}
```

---

### ✅ Result

```java
Task task = mapper.readValue(json, Task.class);
```

✔ Deserialization succeeds

---

### 🧠 Key Insight

👉 Jackson needs:

* A way to **create objects**
* Default constructor OR custom creator

---

## 6. Other Common Errors

---

### 🔹 6.1 `JsonParseException`

📌 **Cause:**

* Invalid JSON syntax

Examples:

* Missing quotes
* Missing commas
* Unclosed braces

---

### 🔹 6.2 `InvalidFormatException`

📌 **Cause:**

* Value cannot be converted to expected type

Example:

```json
{ "age": "abc" }  // expected integer
```

---

### ❌ Result

* Jackson cannot convert `"abc"` → integer

---

## 7. Configuration Strategies

---

### 🔹 Make Jackson More Lenient

| Feature                               | Purpose                |
| ------------------------------------- | ---------------------- |
| `FAIL_ON_UNKNOWN_PROPERTIES = false`  | Ignore extra fields    |
| `ACCEPT_SINGLE_VALUE_AS_ARRAY = true` | Handle object as array |

---

### 🔹 Keep Jackson Strict (Default)

* Enforces API contracts
* Prevents silent data issues

---

## 🧠 Best Practices

---

### ✅ 1. Validate Input Early

* Ensure JSON structure is correct before parsing

---

### ✅ 2. Use Lenient Mode Carefully

* Helps with external APIs
* Avoid hiding real problems

---

### ✅ 3. Understand Exceptions

| Exception                       | Meaning               |
| ------------------------------- | --------------------- |
| `UnrecognizedPropertyException` | Extra fields          |
| `MismatchedInputException`      | Wrong structure       |
| `InvalidDefinitionException`    | Object creation issue |
| `JsonParseException`            | Invalid JSON syntax   |
| `InvalidFormatException`        | Wrong data type       |

---

### ✅ 4. Design Resilient Systems

* Expect:

    * Missing fields
    * Extra fields
    * Format inconsistencies

---

## 8. Final Summary

---

### 🔹 Key Takeaways

* Jackson is:

    * **Strict with structure**
    * **Flexible with types (sometimes)**

* Most common issues:

    * Unknown fields
    * Structure mismatch
    * Object creation problems

---

### 🔹 Solutions

* Configure `ObjectMapper`
* Fix Java class definitions
* Validate JSON input

---

## 🧩 Final Insight

Error handling in Jackson is about **balancing strictness and flexibility**:

* Too strict → breaks easily
* Too lenient → hides problems

👉 The goal is to:

* Handle **real-world messy data**
* While maintaining **data integrity**

---

