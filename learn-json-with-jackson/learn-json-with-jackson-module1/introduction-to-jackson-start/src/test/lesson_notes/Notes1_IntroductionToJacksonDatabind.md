
---

# **Introduction to Jackson**

## **1. Overview of Jackson**

Jackson is one of the most widely used libraries in the Java ecosystem for working with JSON. It is commonly used either directly or indirectly through frameworks like Spring.

Jackson provides a **simple yet powerful API** for:

* **Serializing** Java objects into JSON
* **Deserializing** JSON into Java objects

The main advantage of Jackson is that it requires **minimal configuration** while offering high flexibility and performance.

---

## **2. The POJO Class Example**

To understand how Jackson works, we use a **POJO (Plain Old Java Object)** called `Campaign`.

```java
public class Campaign {

    private String code;

    private String name;

    private String description;

    private Set<Task> tasks = new HashSet<>();

    private boolean closed;

    public Campaign(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public Campaign() {
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    @Override
    public String toString() {
        return "Campaign [code=" + code + ", name=" + name + ", description=" + description + ", closed=" + closed + "]";
    }
}
```

### **Key Observations**

* Contains private fields (encapsulation)
* Uses **getters and setters** for access
* Includes a **no-argument constructor** (important for Jackson)
* Includes a **parameterized constructor**
* Overrides `toString()` for readable output

---

## **3. JSON Processing Approaches in Jackson**

Jackson supports three main approaches:

### **3.1 Data Binding (Most Common)**

* Maps JSON ↔ Java objects automatically
* Uses POJOs
* Easy and intuitive
* **Focus of this course**

### **3.2 Tree Model**

* Uses `JsonNode`
* Represents JSON as a tree structure
* Useful for dynamic or unknown JSON formats

### **3.3 Streaming API**

* Low-level API
* Reads/writes JSON token by token
* High performance and low memory usage
* Suitable for large datasets

---

## **4. Maven Setup for Jackson**

To use Jackson in a Maven project, add the following dependency:

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>${jackson.version}</version>
</dependency>
```

### **Explanation**

* `jackson-databind` includes:

    * Core JSON processing
    * Data binding functionality
* `${jackson.version}` should be defined in your project properties

---

## **5. ObjectMapper (Core Class)**

### **What is ObjectMapper?**

`ObjectMapper` is the **central class** in Jackson responsible for:

* Serialization (Java → JSON)
* Deserialization (JSON → Java)

### **Creating an ObjectMapper**

```java
class JacksonUnitTest {
    ObjectMapper objectMapper = new ObjectMapper();
}
```

### **Key Points**

* Easy to instantiate
* Default configuration works for most use cases
* Highly customizable (covered in advanced topics)

---

## **6. Serialization (Java Object → JSON)**

### **Example Code**

```java
@Test
void whenSerializingCampaign_thenCorrectJsonGenerated() throws Exception {
    Campaign campaign = new Campaign("C001", "Campaign 1", "Serialization");
    String jsonResult = objectMapper.writeValueAsString(campaign);

    System.out.println(jsonResult);
    assertTrue(jsonResult.contains("\"code\":\"C001\""));
    assertTrue(jsonResult.contains("\"name\":\"Campaign 1\""));
}
```

### **Explanation**

* `writeValueAsString()` converts Java object → JSON string
* Jackson automatically:

    * Reads getters
    * Converts fields to JSON properties

### **Example Output**

```json
{
    "code":"C001",
    "name":"Campaign 1",
    "description":"Serialization",
    "tasks":[],
    "closed":false
}
```

### **Key Notes**

* Field names match JSON keys
* Default values:

    * `tasks` → empty array
    * `closed` → false

---

## **7. Deserialization (JSON → Java Object)**

### **Example Code**

```java
@Test
void whenDeserializingJson_thenCorrectCampaignObjectGenerated() throws Exception {
    String jsonCampaign = """
        {
          "code": "%s",
          "name": "%s",
          "description": "%s"
        }
        """.formatted("C001", "Campaign 1", "Deserialization");

    Campaign campaign = objectMapper.readValue(jsonCampaign, Campaign.class);

    assertEquals("C001", campaign.getCode());
    assertEquals("Campaign 1", campaign.getName());
}
```

### **Explanation**

* `readValue()` converts JSON → Java object
* Jackson:

    * Matches JSON keys with Java field names
    * Uses setters or reflection to populate values

### **Key Notes**

* Field names must match (or be configured otherwise)
* No-argument constructor is required
* Missing fields are ignored (default values used)

---

## **8. How Jackson Works (Summary)**

### **Serialization Process**

1. Java object is passed to `ObjectMapper`
2. Jackson reads getters
3. Converts object into JSON format

### **Deserialization Process**

1. JSON string is passed to `ObjectMapper`
2. Jackson creates object instance
3. Maps JSON fields to Java fields

---

## **9. Advantages of Jackson**

* Easy to use with minimal setup
* High performance
* Flexible and customizable
* Widely supported in frameworks
* Supports multiple processing styles

---

## **10. Key Takeaways**

* Jackson is a **powerful JSON processing library**
* `ObjectMapper` is the **core class**
* Supports:

    * Data Binding (most common)
    * Tree Model
    * Streaming API
* Serialization → `writeValueAsString()`
* Deserialization → `readValue()`
* Works seamlessly with standard Java POJOs

---

## **11. Conclusion**

Jackson provides a **simple yet powerful way** to handle JSON in Java applications. With minimal configuration, developers can easily convert between Java objects and JSON. As seen in the examples, Jackson automatically maps fields and handles most of the complexity behind the scenes.

In more advanced scenarios, its behavior can be customized to handle complex data structures, validation, and formatting.

---

