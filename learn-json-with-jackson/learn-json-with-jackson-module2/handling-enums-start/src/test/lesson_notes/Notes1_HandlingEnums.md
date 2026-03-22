
---

# Handling Enums

## 1. Overview

Enums are a convenient way to define fixed sets of values in Java, but when converting them to JSON, there are several ways to represent them — each with its own trade-off between:

* **Human readability**
* **Long-term stability**

Jackson provides multiple approaches to handle enums, including:

* Default behavior
* ObjectMapper-level configuration
* Enum-level annotations
* Advanced customization

### Required Modules

To begin:

* Import:

  ```
  handling-enums-start
  ```

* Full implementation:

  ```
  handling-enums-end
  ```

---

## 2. ObjectMapper-Level Enum Configuration

By default, Jackson:

* **Serializes enums using their constant name**
* **Deserializes using the same name**

---

## 2.1 Serializing and Deserializing Without Annotations

### Enum Definition

```java id="g8c2o9"
public enum TaskStatus {

    TO_DO("To Do"), 
    IN_PROGRESS("In Progress"), 
    ON_HOLD("On Hold"),
    DONE("Done");

    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }
}
```

---

### Serialize Enum

```java id="k7q2h5"
@Test
void whenSerializingEnum_thenWritesName() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(TaskStatus.IN_PROGRESS);
    assertEquals("\"IN_PROGRESS\"", json);
}
```

* Output: `"IN_PROGRESS"`
* Matches enum constant name exactly

---

### Note

In real applications, enums are usually fields:

```json
{
  ...
  "status": "IN_PROGRESS"
}
```

---

### Deserialize Enum

```java id="b8k9d2"
@Test
void whenDeserializingMatchingName_thenSucceeds() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    TaskStatus taskStatus = objectMapper.readValue("\"ON_HOLD\"", TaskStatus.class);
    assertEquals(TaskStatus.ON_HOLD, taskStatus);
}
```

* Jackson matches JSON string to enum constant name

---

## 2.2 Serializing to Enum Ordinal

Jackson can serialize enums as **numeric index values**.

### Enable Feature

```java id="r4d1t7"
@Test
void whenUsingIndexFlag_thenWritesOrdinal() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
    String json = mapper.writeValueAsString(TaskStatus.ON_HOLD);
    assertEquals("2", json);
}
```

* `ON_HOLD` → index `2`
* No quotes → numeric value

---

### Deserialize Ordinal

```java id="x7m3p1"
@Test
void whenUsingIndexFlag_thenDeserializeOrdinal() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
    TaskStatus taskStatus = mapper.readValue("2", TaskStatus.class);
    assertEquals(TaskStatus.ON_HOLD, taskStatus);
}
```

---

### Important Limitation

* Enum order matters
* Reordering constants changes meaning → **unstable**

---

## 2.3 Serializing With toString()

Jackson can serialize enums using `toString()`.

---

### Override toString()

```java id="u1z8n6"
public enum TaskStatus {

    TO_DO("To Do"),
    IN_PROGRESS("In Progress"),
    ON_HOLD("On Hold"),
    DONE("Done");

    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }

    // ...

    @Override
    public String toString() {
        return label;
    } 
}
```

---

### Enable Feature

```java id="c5n2w8"
@Test
void whenUsingToStringFlag_thenWritesLabel() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    String json = mapper.writeValueAsString(TaskStatus.DONE);
    assertEquals("\"Done\"", json);
}
```

* Output uses label field

---

## 3. Enum-Level Customization: Serialization

Jackson allows customization directly inside enums using annotations.

---

## 3.1 The @JsonValue Annotation

Controls how enum is serialized.

---

### Example

```java id="l3v7o2"
public enum TaskStatusJsonValue {

    // ...

    @JsonValue
    private final String label;

    // ...
}
```

---

### Test

```java id="t9x4b6"
@Test
void whenUsingJsonValue_thenLabelWritten() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(TaskStatusJsonValue.IN_PROGRESS);
    assertEquals("\"In Progress\"", json);
}
```

* Uses `label` instead of constant name

---

## 3.2 Using @JsonValue on Methods

We can serialize enums into complex structures.

---

### Example

```java id="h2d6k1"
public enum TaskStatusObjectMap {

    // ...

    @JsonValue
    public Map<String, String> toJson() {
        Map<String, String> map = new HashMap<>();
        map.put("label", label);
        return map;
    }
}
```

---

### Test

```java id="m8q5r3"
@Test
void whenJsonValueOnEnum_thenMapKeysAreLabel() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    TaskStatusObjectMap label = TaskStatusObjectMap.TO_DO;
    String json = mapper.writeValueAsString(label);

    String expectedJson = "{\"label\":\"To Do\"}";
    assertEquals(expectedJson, json);
}
```

---

### Key Insight

* Enum serialized as JSON object
* Useful for structured APIs

---

## 3.3 The @JsonProperty Annotation

Used to customize **specific enum constants**.

---

### Example

```java id="p7k1v4"
public enum TaskStatusJsonProperty {

    TO_DO("To Do"),
    IN_PROGRESS("In Progress"),
    ON_HOLD("On Hold"),
    @JsonProperty("JsonProperty Done")
    DONE("Done");
}
```

---

### Test

```java id="z6y3n8"
@Test
void whenUsingJsonProperty_thenLabelWritten() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(TaskStatusJsonProperty.DONE);
    assertEquals("\"JsonProperty Done\"", json);
}
```

---

### Important Note

* Works only with default behavior
* Overridden by:

    * `@JsonValue`
    * ObjectMapper configurations

---

## 4. Enum-Level Customization: Deserialization

Jackson provides:

* `@JsonEnumDefaultValue`
* `@JsonCreator`

---

## 4.1 Using @JsonEnumDefaultValue

Provides fallback for unknown values.

---

### Example

```java id="y2c8l0"
@JsonEnumDefaultValue
TO_DO("To Do"), 
```

---

### Enable Feature + Test

```java id="g9r4b1"
@Test
void whenUsingDefaultValue_thenFallsBackToToDo() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper().enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
    TaskStatus status = mapper.readValue("\"UNKNOWN\"", TaskStatus.class);
    assertEquals(TaskStatus.TO_DO, status);
}
```

---

## 4.2 @JsonCreator Annotation

Allows custom deserialization logic.

---

### Example

```java id="e5m2x9"
@JsonCreator
public static TaskStatusJsonValue fromValue(String label) {
    for (TaskStatusJsonValue taskStatus : TaskStatusJsonValue.values()) {
        if (taskStatus.label.equalsIgnoreCase(label)) {
            return taskStatus;
        }
    }
    throw new IllegalArgumentException("Invalid Task Status: " + label);
}
```

---

### Test

```java id="k3v9s7"
@Test
void whenUsingJsonCreator_thenLabelParsed() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    TaskStatusJsonValue taskStatus = mapper.readValue("\"On HoLd\"", TaskStatusJsonValue.class);
    assertEquals(TaskStatusJsonValue.ON_HOLD, taskStatus);
}
```

---

### Key Benefit

* Case-insensitive matching
* Custom validation logic

---

## 5. Advanced Enum Handling

We can serialize enums as JSON objects using `@JsonFormat`.

---

### Example

```java id="b4f8w6"
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TaskStatusJsonObject {

    // ...
}
```

---

### Test

```java id="n1u6c5"
@Test
void givenTaskStatusEnum_whenSerializing_thenJsonObjectIsProduced() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    TaskStatus label = TaskStatusJsonObject.TO_DO;
    String json = mapper.writeValueAsString(label);

    String expectedJson = "{\"label\":\"To Do\"}";
    assertEquals(expectedJson, json);
}
```

---

### Important Note

* Do NOT combine with `@JsonValue`
* `@JsonValue` takes precedence

---

# Final Summary

Jackson provides multiple strategies for handling enums:

### Default

* Uses enum name

### ObjectMapper Config

* Ordinal (`WRITE_ENUMS_USING_INDEX`)
* toString (`WRITE_ENUMS_USING_TO_STRING`)

### Enum-Level Customization

* `@JsonValue` → full control
* `@JsonProperty` → specific constants

### Deserialization

* `@JsonEnumDefaultValue` → fallback
* `@JsonCreator` → custom logic

### Advanced

* `@JsonFormat(shape = OBJECT)` → serialize as JSON object

---

These approaches allow you to balance:

* **Readability**
* **Flexibility**
* **Backward compatibility**

---

# Enum Handling Strategies Comparison Table

| Strategy                        | Configuration Type                                             | Serialization Output    | Deserialization Behavior      | Advantages                                  | Disadvantages                             | Best Use Case                                  |
| ------------------------------- | -------------------------------------------------------------- | ----------------------- | ----------------------------- | ------------------------------------------- | ----------------------------------------- | ---------------------------------------------- |
| **Default (Name-Based)**        | None                                                           | `"IN_PROGRESS"`         | Matches exact enum name       | Simple, stable, predictable                 | Not user-friendly (not readable labels)   | Most APIs where internal names are acceptable  |
| **Ordinal (Index-Based)**       | `SerializationFeature.WRITE_ENUMS_USING_INDEX`                 | `2`                     | Uses index to map enum        | Compact, useful for legacy systems          | Very fragile (order changes break data)   | Legacy systems, CSV, binary protocols          |
| **toString() Strategy**         | `WRITE_ENUMS_USING_TO_STRING` + override `toString()`          | `"Done"`                | Uses `toString()` value       | More readable than default                  | Depends on method logic, less explicit    | When enum has meaningful string representation |
| **@JsonValue (Field)**          | Enum-level annotation                                          | `"In Progress"`         | Uses annotated field value    | Clean, readable, consistent                 | Only one field allowed                    | When enums have labels/descriptions            |
| **@JsonValue (Method)**         | Enum-level annotation                                          | `{"label":"To Do"}`     | Uses method return            | Very flexible, supports objects             | More complex, harder to maintain          | APIs needing structured enum output            |
| **@JsonProperty**               | Enum constant annotation                                       | `"JsonProperty Done"`   | Matches annotated value       | Customizes specific values                  | Limited flexibility, overridden by others | When only a few constants need custom names    |
| **@JsonEnumDefaultValue**       | Enum constant + `READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE` | Same as chosen strategy | Unknown values map to default | Prevents errors on unknown values           | May hide invalid data issues              | Backward compatibility, evolving APIs          |
| **@JsonCreator**                | Static factory method                                          | Depends on logic        | Custom parsing logic          | Full control (case-insensitive, validation) | More code, manual implementation          | Complex matching rules or validation           |
| **@JsonFormat(shape = OBJECT)** | Class-level annotation                                         | `{"label":"To Do"}`     | Maps object fields            | Structured, extensible                      | Cannot combine with `@JsonValue`          | Rich API responses with metadata               |

---

# Key Insights

### 1. Most Stable Options

* Default (name-based)
* `@JsonValue` (field-based)

### 2. Most Flexible Options

* `@JsonCreator`
* `@JsonValue` (method)

### 3. Most Readable Output

* `@JsonValue`
* `toString()`

### 4. Most Risky Option

* Ordinal (index-based) ❌
  → breaks if enum order changes

---

# Quick Recommendation Guide

* **Simple API** → Default
* **Readable labels** → `@JsonValue`
* **Custom parsing** → `@JsonCreator`
* **Backward compatibility** → `@JsonEnumDefaultValue`
* **Structured JSON output** → `@JsonFormat(shape = OBJECT)`

---


