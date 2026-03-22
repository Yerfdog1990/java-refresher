
---

# **Custom Property Names and Order**

## **1. Overview**

When working with external APIs, we often need to adapt our JSON structure to match specific naming conventions or formatting requirements. Jackson provides several annotations that allow us to customize how our Java objects are represented in JSON.

In this lesson, we’ll learn how to use:

* `@JsonProperty`
* `@JsonPropertyOrder`
* `@JsonValue`

These annotations help us to:

* Rename properties
* Control their output order
* Serialize an entire object as a single value

---

## **2. Serializing With Jackson**

Let’s start with a walk-through of how serialization/deserialization works with Jackson.

### **Basic POJO Example**

```java
public class Campaign {
    private String code;
    private String name;
    private String description;

    // constructors and other setters and getters

    public String getCode() {
        return code;
    }
}
```

### **Default Jackson Behavior**

When calling:

```java
objectMapper.writeValueAsString(campaign);
```

Jackson discovers properties using default settings:

* Uses JavaBean getters to define JSON property names

    * `getName()` → `"name"`
    * `isClosed()` → `"closed"`
* Includes public fields
* Ignores private/protected fields unless configured

---

### **Changing Getter Name**

```java
public String getTheCode() {
    return code.toLowerCase();
}
```

### **Test Case**

```java
@Test
void givenFieldAndGetter_whenSerializing_thenJacksonBehaviorChecked() throws JsonProcessingException {
    // Given
    Campaign campaign = new Campaign("CODE01", "Campaign Name", "Description");

    // When
    String json = objectMapper.writeValueAsString(campaign);
    System.out.println(json);

    // Then
    assertFalse(json.contains("\"code\""));
    assertTrue(json.contains("\"theCode\""));
}
```

### **Resulting JSON**

```json
{"name":"Campaign Name","description":"Description","is_closed":false,"tasks":[],"theCode":"code01"}
```

---

## **3. Renaming Properties with `@JsonProperty`**

The `@JsonProperty` annotation allows us to control JSON property names.

### **Applying on Field**

```java
public class Campaign {

    @JsonProperty
    private String code;

    // ...
}
```

### **Effect**

Now the JSON includes:

```json
{"code":"CODE01", ...}
```

---

### **Custom Name with `@JsonProperty`**

```java
public class Campaign {

    @JsonProperty
    private String code;

    @JsonProperty("code")
    public String getTheCode() {
        return code.toLowerCase();
    }
}
```

### **Important Rule**

* If both field and getter define the same property →
  **method annotation takes precedence**

---

### **Updated Test Assertion**

```java
assertTrue(json.contains("\"code\":\"code01\""));
```

### **Result**

```json
{"code":"code01","name":"Campaign Name","description":"Description","is_closed":false,"tasks":[]}
```

---

### **Best Practice**

Although Jackson primarily uses getters:

* It is often clearer to apply `@JsonProperty` directly to the field
* Jackson links field and getter automatically

---

### **Example: Snake Case Naming**

```java
@JsonProperty("is_closed")
private boolean closed;
```

### **Test Case**

```java
@Test
void givenPropertyWithCustomName_whenSerializing_thenCustomNameAppearsInJson() throws JsonProcessingException {
    // Given
    Campaign original = new Campaign("T101", "Rename", "Custom property name");

    // When
    String json = objectMapper.writeValueAsString(original);

    // Then
    assertTrue(json.contains("\"is_closed\""));
    assertFalse(json.contains("\"closed\""));
}
```

### **Key Points**

* `@JsonProperty` rewrites the JSON key
* Old name is omitted

---

### **Deserialization Behavior**

During:

```java
objectMapper.readValue(json, Campaign.class);
```

Jackson binds JSON keys based on:

* Setter name
* `@JsonProperty` annotation

If the key doesn’t match (e.g., `"closed"` instead of `"is_closed"`):

* It may be ignored
* Or throw `UnrecognizedPropertyException`

---

## **4. Controlling Serialization Order**

### **Default Behavior**

* Jackson preserves **field declaration order**
* Fields take precedence over getters

---

### **Ways to Control Order**

1. ObjectMapper configuration
2. `@JsonPropertyOrder`
3. `@JsonProperty(index = ...)` (less recommended)

---

## **4.1. Serialization Order with ObjectMapper Configuration**

Relevant `MapperFeature` options:

* `SORT_PROPERTIES_ALPHABETICALLY` → alphabetical order
* `SORT_CREATOR_PROPERTIES_FIRST` → constructor properties first
* `SORT_CREATOR_PROPERTIES_BY_DECLARATION_ORDER` → preserve declaration order

---

## **4.2. Serialization Order with `@JsonPropertyOrder`**

### **Alphabetical Order**

```java
@JsonPropertyOrder(alphabetic = true)
public class Campaign {
    // ...
}
```

---

### **Custom Order**

```java
@JsonPropertyOrder({ "code", "name", "description", "closed", "tasks" })
public class Campaign { 
    // ... 
}
```

---

### **Test Case**

```java
@Test
void givenJsonPropertyOrder_whenSerializing_thenKeysAppearInSequence() throws JsonProcessingException {
    // Given
    Campaign original = new Campaign("T101", "Rename", "JSON property order");

    // When
    String json = objectMapper.writeValueAsString(original);
    System.out.println(json);

    int idxCode = json.indexOf("\"code\"");
    int idxName = json.indexOf("\"name\"");
    int idxDescription = json.indexOf("\"description\"");
    int idxClosed = json.indexOf("\"is_closed\"");
    int idxTasks = json.indexOf("\"tasks\"");

    // Then
    assertTrue(idxCode < idxName);
    assertTrue(idxName < idxDescription);
    assertTrue(idxDescription < idxClosed);
    assertTrue(idxClosed < idxTasks);
}
```

### **Explanation**

* Extract index of each property
* Verify correct sequence using `assertTrue`

---

### **Important Note**

Even if property is renamed:

```java
@JsonProperty("is_closed")
private boolean closed;
```

You can use either:

```java
@JsonPropertyOrder({ "closed", ... })
```

or

```java
@JsonPropertyOrder({ "is_closed", ... })
```

Both will work.

---

## **5. Producing a Single-Value JSON Output with `@JsonValue`**

Sometimes we want an object to be serialized as a single value.

### **Example**

```java
@JsonValue
public String toJsonValue() {
    return "Task: code= " + code + ", name=" + name + ", description=" + description + ".";
}
```

---

### **Effect**

* Entire object becomes a single string in JSON
* Other fields are ignored

---

### **Test Case**

```java
@Test
void givenJsonValue_whenSerializingTask_thenSingleStringProduced() throws JsonProcessingException {
    Task task = new Task("T101", "Task101", "JSON value example", LocalDate.now(), null, null);
    Campaign original = new Campaign("T102", "Campaign102", "Big Campaign");
    original.setTasks(Set.of(task));

    String json = objectMapper.writeValueAsString(original);

    String expectedJson = """
      {"code":"T102","name":"Campaign102","description":"Big Campaign","is_closed":false,"tasks":["Task: code= T101, name=Task101, description=JSON value example."]}""";

    assertEquals(expectedJson, json);
}
```

---

### **Key Observation**

* `Task` is serialized as:

```json
"Task: code= T101, name=Task101, description=JSON value example."
```

* Not as a JSON object

---

### **Conclusion for @JsonValue**

Once `@JsonValue` is present:

* The method’s return value becomes the **entire JSON representation**

---

## **6. Conclusion**

In this lesson, we explored how Jackson allows customization of JSON output:

* `@JsonProperty`

    * Renames properties
    * Controls inclusion

* `@JsonPropertyOrder`

    * Defines serialization order

* `@JsonValue`

    * Serializes objects as single values

These annotations are essential when working with APIs that require specific JSON formats.

In the next steps, more annotations like:

* `@JsonInclude`
* `@JsonIgnore`

will be introduced to further control JSON serialization behavior.

---