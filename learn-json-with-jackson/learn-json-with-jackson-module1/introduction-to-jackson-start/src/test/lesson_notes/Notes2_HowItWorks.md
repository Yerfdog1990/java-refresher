
---

# **How Jackson Works Internally**

Jackson may look simple on the surface (just `writeValueAsString()` and `readValue()`), but internally it performs several sophisticated steps using **reflection, introspection, and annotations**.

---

# **1. High-Level Flow**

## **Serialization (Java → JSON)**

1. You pass a Java object to `ObjectMapper`
2. Jackson inspects the class structure
3. It finds properties (fields/getters)
4. Converts them into JSON format
5. Writes JSON output

## **Deserialization (JSON → Java)**

1. JSON is parsed into tokens
2. Jackson determines target class
3. Creates object instance
4. Maps JSON fields → Java fields
5. Returns populated object

---

# **2. Role of Reflection**

### **What is Reflection?**

Reflection allows Java to:

* Inspect classes at runtime
* Access fields and methods dynamically
* Create objects without explicitly calling constructors

---

## **2.1 During Serialization**

Jackson uses reflection to:

* Discover **getters** (`getName()`, `isClosed()`)
* Access **fields directly** (if configured)
* Identify data types

### Example

```java
public String getName() {
    return name;
}
```

Jackson:

* Detects this method
* Infers property name → `"name"`
* Calls it dynamically to get value

---

## **2.2 During Deserialization**

Jackson uses reflection to:

* Instantiate objects using **no-arg constructor**
* Call **setters** or assign fields directly

### Example

```java
campaign.setName("Campaign 1");
```

Internally:

* Jackson finds `setName()`
* Invokes it dynamically

---

## **2.3 Object Creation**

Jackson typically uses:

```java
Campaign campaign = Campaign.class.getDeclaredConstructor().newInstance();
```

That’s why:
👉 **A no-argument constructor is required**

---

# **3. Introspection (Bean Analysis)**

Before using reflection, Jackson performs **introspection**:

* It analyzes the class structure
* Builds a **property model**

### What Jackson Detects:

* Fields
* Getters (`getX`)
* Setters (`setX`)
* Boolean getters (`isX`)

---

## **Example Mapping**

```java
private String name;

public String getName()
public void setName(String name)
```

Jackson creates:

```
JSON property: "name"
```

---

# **4. Role of Annotations**

Annotations allow developers to **control how Jackson behaves**.

---

## **4.1 Common Annotations**

### **@JsonProperty**

Changes the JSON field name

```java
@JsonProperty("campaign_name")
private String name;
```

JSON becomes:

```json
{
  "campaign_name": "Campaign 1"
}
```

---

### **@JsonIgnore**

Excludes a field

```java
@JsonIgnore
private String description;
```

---

### **@JsonInclude**

Controls inclusion rules

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
```

---

### **@JsonCreator**

Used for constructor-based deserialization

```java
@JsonCreator
public Campaign(@JsonProperty("code") String code) {
    this.code = code;
}
```

---

### **@JsonGetter / @JsonSetter**

Customizes getter/setter behavior

---

## **4.2 Why Annotations Matter**

They override default behavior:

* Rename fields
* Ignore properties
* Handle special cases
* Customize mapping

---

# **5. Serialization Internals**

When you run:

```java
objectMapper.writeValueAsString(campaign);
```

### **Steps:**

### **Step 1: Class Inspection**

* Jackson inspects `Campaign` class
* Builds metadata (properties list)

---

### **Step 2: Serializer Selection**

* Finds appropriate serializer:

    * String → StringSerializer
    * Boolean → BooleanSerializer
    * Collections → CollectionSerializer

---

### **Step 3: Value Extraction**

* Calls getters using reflection

---

### **Step 4: JSON Generation**

* Uses `JsonGenerator` to write JSON

Example:

```json
{
  "code": "C001"
}
```

---

# **6. Deserialization Internals**

When you run:

```java
objectMapper.readValue(json, Campaign.class);
```

### **Steps:**

### **Step 1: JSON Parsing**

* JSON is parsed into tokens:

    * START_OBJECT
    * FIELD_NAME
    * VALUE_STRING

---

### **Step 2: Object Creation**

* Uses no-arg constructor

---

### **Step 3: Property Matching**

* Matches JSON keys with class properties

---

### **Step 4: Value Assignment**

* Calls setters OR sets fields directly

---

### **Step 5: Return Object**

* Fully populated object returned

---

# **7. Property Naming Strategy**

Jackson maps:

```
Java field → JSON key
```

### Default Behavior:

```java
private String campaignName;
```

JSON:

```json
"campaignName"
```

---

### Custom Strategy Example:

Snake case:

```json
"campaign_name"
```

Configured via:

```java
objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
```

---

# **8. Handling Collections and Nested Objects**

Jackson recursively processes:

* Lists
* Sets
* Nested objects

### Example:

```java
private Set<Task> tasks;
```

JSON:

```json
"tasks": []
```

---

# **9. Caching and Performance**

Jackson optimizes performance by:

* Caching class metadata
* Reusing serializers/deserializers
* Avoiding repeated reflection

---

# **10. Error Handling Internals**

Common errors:

* Missing constructor → `InvalidDefinitionException`
* Type mismatch → `MismatchedInputException`
* Unknown fields → ignored (by default)

---

# **11. Why Jackson Feels “Automatic”**

Because it:

* Uses **reflection** to inspect classes
* Applies **naming conventions**
* Uses **annotations** to customize behavior
* Handles recursion and types automatically

---

# **12. Key Internal Components**

| Component        | Role                 |
| ---------------- | -------------------- |
| ObjectMapper     | Main controller      |
| JsonParser       | Reads JSON           |
| JsonGenerator    | Writes JSON          |
| Serializer       | Converts Java → JSON |
| Deserializer     | Converts JSON → Java |
| BeanIntrospector | Analyzes classes     |

---

# **13. Summary**

Jackson internally relies on:

### **1. Reflection**

* Access fields and methods dynamically
* Create and populate objects

### **2. Introspection**

* Analyze class structure
* Build property mappings

### **3. Annotations**

* Customize behavior
* Override defaults

### **4. Processing Pipeline**

* Serialize: Object → JSON
* Deserialize: JSON → Object

---

# **Final Insight**

Jackson works like a **smart translator**:

* It **studies your class structure**
* **Applies rules and annotations**
* Converts data seamlessly between Java and JSON

---

