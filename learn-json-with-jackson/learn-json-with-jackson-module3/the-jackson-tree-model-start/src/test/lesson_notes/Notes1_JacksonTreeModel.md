
---

# 🌳 The Jackson Tree Model 
## 1. Overview

The **Jackson Tree Model** provides a flexible way to handle **dynamic or unknown JSON structures**.

* Unlike **Data Binding**, which maps JSON → Java classes
* The Tree Model:

    * Does **not require predefined classes**
    * Represents JSON as a **tree of nodes**
    * Allows **navigation, inspection, and modification**

👉 Best used when:

* JSON structure is **unknown**
* Only part of JSON is needed
* Working with **external or inconsistent APIs**

---

## 2. Understanding Jackson’s Tree Model

### 🔹 Problem with Data Binding

* Requires **matching Java classes**
* Fails if structure differs

👉 Example issue:

```json
{
  "code": "C-001",
  "details": {
    "name": "Campaign 1",
    "description": "Campaign 1 description",
    "closed": false
  }
}
```

* Domain model expects:

```java
class Campaign {
    String name;
    String description;
    boolean closed;
}
```

❌ Mismatch → causes failure

---

### 🔹 Tree Model Solution

Jackson introduces:

* `JsonNode` → core class
* Represents JSON as a **tree structure**

### Key Characteristics:

* Immutable base (`JsonNode`)
* Each JSON element is a node:

    * Object → ObjectNode
    * Array → ArrayNode
    * String → TextNode
    * Number → NumericNode
    * Boolean → BooleanNode
    * null → NullNode

---

### 🔹 Parsing JSON into Tree

```java
ObjectMapper objectMapper = new ObjectMapper();
JsonNode rootNode = objectMapper.readTree(CAMPAIGN_WITH_TASKS);
```

* `rootNode` = entire JSON tree
* Entry point for navigation

---

## 3. Navigating the Tree Model

---

### 3.1 Reading Fields

```java
JsonNode campaignCodeNode = rootNode.get("code");
String campaignCode = campaignCodeNode.asText();
```

### 🔹 Access Nested Fields

```java
JsonNode detailsNode = rootNode.get("details");

String campaignName = detailsNode.get("name").asText();
String campaignDescription = detailsNode.get("description").asText();
boolean campaignIsClosed = detailsNode.get("closed").asBoolean();
```

### Key Methods:

* `get("field")` → access child node
* `asText()` → convert to String
* `asBoolean()` → convert to boolean

---

### 3.2 Null and Non-Existing Fields

Handling missing data is critical.

#### 🔹 Using `get()`

```java
JsonNode node = rootNode.get("nonExistingField"); // null
node.asText(); // ❌ NullPointerException
```

---

#### 🔹 Using `path()` (SAFE)

```java
JsonNode node = rootNode.path("nonExistingField");
```

* Returns **MissingNode (not null)**

```java
node.isMissingNode(); // true
node.asText();       // ""
node.textValue();    // null
```

---

#### 🔹 Null Fields

```java
JsonNode nullableFieldNode = rootNode.get("nullableField");
```

* Returns **NullNode**

```java
nullableFieldNode.asText();   // "null"
nullableFieldNode.textValue(); // null
```

---

### ✅ Key Takeaways

| Case                   | Result                   |
| ---------------------- | ------------------------ |
| `get()` missing field  | null (unsafe)            |
| `path()` missing field | MissingNode (safe)       |
| JSON null              | NullNode                 |
| `asText()`             | Always returns String    |
| `textValue()`          | Returns null if not text |

👉 **Best Practice:** Use `path()` for safe navigation

---

### 3.3 Working With Arrays

#### 🔹 Access Array

```java
JsonNode tasksNode = rootNode.path("tasks");

tasksNode.isArray(); // true
tasksNode.size();    // 2
```

---

#### 🔹 Access by Index

```java
JsonNode firstTaskNode = tasksNode.get(0);

firstTaskNode.path("code").asText();   // T-001
firstTaskNode.path("name").asText();   // Task 1
```

---

#### 🔹 Iterating Over Array

```java
int inProgressCount = 0;

for (JsonNode taskNode : tasksNode) {
    String status = taskNode.path("status").asText();

    if (TaskStatus.IN_PROGRESS.toString().equals(status)) {
        inProgressCount++;
    }
}
```

✔ `JsonNode` implements `Iterable`

---

## 3.4 Converting JsonNode → Java Object

Sometimes we want **partial data binding**.

---

### 🔹 Successful Conversion

```java
JsonNode firstTaskNode = rootNode.path("tasks").get(0);

Task firstTask = objectMapper.treeToValue(firstTaskNode, Task.class);
```

✔ Works because structure matches

---

### 🔹 Failed Conversion

```java
objectMapper.treeToValue(rootNode, Campaign.class);
```

❌ Throws:

```java
UnrecognizedPropertyException
```

Reason:

* JSON structure ≠ Java class

---

### 🔹 Validation Before Conversion

Use:

```java
node.isObject();
node.isArray();
node.isTextual();
node.isNumber();
node.has("field");
```

---

### 🔹 Lenient Conversion (Ignore Unknown Fields)

```java
ObjectMapper lenientMapper = new ObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
```

```java
Campaign campaign = lenientMapper.treeToValue(rootNode, Campaign.class);
```

✔ Only matching fields are populated
✔ Others ignored

---

## 4. Manipulating the Tree Model

### 🔹 Important Concept

* `JsonNode` → **immutable**
* Mutable subclasses:

    * `ObjectNode`
    * `ArrayNode`

---

### 🔹 Transforming JSON Structure

#### Step 1: Extract values

```java
JsonNode detailsNode = rootNode.path("details");

String name = detailsNode.path("name").asText();
String description = detailsNode.path("description").asText();
boolean closed = detailsNode.path("closed").asBoolean();
```

---

#### Step 2: Modify Root

```java
ObjectNode rootObjectNode = (ObjectNode) rootNode;

rootObjectNode.put("name", name);
rootObjectNode.put("description", description);
rootObjectNode.put("closed", closed);

rootObjectNode.remove("details");
rootObjectNode.remove("nullableField");
```

---

#### Step 3: Modify Array

```java
ArrayNode tasksNode = (ArrayNode) rootObjectNode.path("tasks");

tasksNode.forEach(taskNode -> {
    ObjectNode taskObjectNode = (ObjectNode) taskNode;
    taskObjectNode.put("status", TaskStatus.DONE.toString());
});
```

---

#### Step 4: Convert Back to Object

```java
Campaign modifiedCampaign = objectMapper.treeToValue(rootObjectNode, Campaign.class);
```

✔ Now structure matches domain model

---

### 🔹 Creating Tree From Scratch

```java
ObjectNode obj = objectMapper.createObjectNode();
ArrayNode arr = objectMapper.createArrayNode();
```

* Build JSON programmatically

---

## 5. Summary & Key Insights

### 🔹 When to Use Tree Model

✔ Unknown JSON structure
✔ Partial data extraction
✔ JSON transformation
✔ Working with external APIs

---

### 🔹 Advantages

* No need for Java classes
* Flexible and dynamic
* Easy navigation
* Supports transformation

---

### 🔹 Limitations

* More manual than Data Binding
* Less type-safe

---

## 🧠 Final Takeaway

The **Jackson Tree Model** is a powerful alternative to Data Binding that allows you to:

* Parse JSON into a **tree structure**
* Navigate using `JsonNode`
* Safely handle missing data with `path()`
* Work with arrays easily
* Convert parts into Java objects
* Modify JSON dynamically

👉 It’s ideal for **real-world scenarios** where JSON is:

* messy
* inconsistent
* or only partially relevant

---

