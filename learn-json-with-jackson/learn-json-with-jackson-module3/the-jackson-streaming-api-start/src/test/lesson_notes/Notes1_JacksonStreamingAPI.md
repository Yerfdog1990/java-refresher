
---

# ⚡ Introduction to the Jackson Streaming API

---

## 1. Overview

The **Jackson Streaming API** is a **low-level approach** for processing JSON data **programmatically**.

* Unlike:

    * **Data Binding** → maps JSON ↔ Java objects
    * **Tree Model** → represents JSON as a node tree

👉 The Streaming API:

* Works directly with **JSON streams**
* Processes data **sequentially**
* Requires **manual control of structure**

---

## 2. Why Use the Streaming API?

The Streaming API is designed for **performance and scalability**.

---

### 🔹 2.1 Processing Large JSON Files

* Reads/writes JSON **one piece at a time**
* Does **NOT load entire JSON into memory**

✅ Benefits:

* Constant memory usage
* Prevents `OutOfMemoryError`
* Ideal for:

    * Millions of records
    * Large logs
    * Data pipelines

---

### 🔹 2.2 Performance-Critical Applications

* Works at the **lowest level**
* Avoids:

    * Object creation overhead
    * Reflection
    * Mapping costs

✅ Result:

* Faster execution
* Lower latency

---

### 🔹 2.3 Reactive & Asynchronous Systems

* Sequential, **event-driven processing**
* Fits well with:

    * Spring WebFlux
    * Vert.x

✅ Advantage:

* Process data **as it arrives**
* No need to wait for full payload

---

### ⚠️ Trade-Off

| Advantage        | Disadvantage              |
| ---------------- | ------------------------- |
| High performance | More complex              |
| Low memory usage | Verbose code              |
| Fine control     | Manual structure handling |

---

## 3. Reading JSON with `JsonParser`

---

### 🔹 Core Concepts

* **JsonParser** → reads JSON stream
* Breaks JSON into **tokens**

### 🔹 JsonToken Types

Each JSON element becomes a token:

* `START_OBJECT`
* `END_OBJECT`
* `START_ARRAY`
* `END_ARRAY`
* `FIELD_NAME`
* `VALUE_STRING`, etc.

---

### 🔹 Navigation

* `nextToken()` → move to next token
* `currentName()` → current field name
* `getText()` → current value

---

### 🔹 Example JSON (tasks.json)

```json
[
  { "code": "T1", "name": "Task 1", "status": "TO_DO" },
  { "code": "T2", "name": "Task 2", "status": "IN_PROGRESS" },
  ...
  { "code": "T1000", "name": "Task 1000", "status": "DONE" }
]
```

* Contains **1000 Task objects**
* Evenly distributed statuses

---

### 🔹 Full Parsing Example

```java
@Test
void whenParsingTasksWithJsonParser_thenOnHoldTasksCountIsCorrect() throws IOException {
    int onHoldTasks = 0;
    URL resource = getClass().getClassLoader().getResource("tasks.json");

    JsonFactory jsonFactory = new JsonFactory();
    JsonParser jsonParser = jsonFactory.createParser(resource);

    if (jsonParser.nextToken() == JsonToken.START_ARRAY) {
        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {

            TaskStatus taskStatus = null;

            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jsonParser.currentName();

                if ("status".equals(fieldName)) {
                    jsonParser.nextToken();
                    taskStatus = TaskStatus.valueOf(jsonParser.getText());
                }
            }

            if (TaskStatus.ON_HOLD.equals(taskStatus)) {
                onHoldTasks++;
            }
        }
    }

    jsonParser.close();
    assertEquals(250, onHoldTasks);
}
```

---

### 🔹 Step-by-Step Explanation

1. **Create Parser**

```java
JsonFactory jsonFactory = new JsonFactory();
JsonParser jsonParser = jsonFactory.createParser(resource);
```

---

2. **Check JSON Structure**

```java
jsonParser.nextToken() == JsonToken.START_ARRAY
```

✔ Confirms root is an array

---

3. **Iterate Over Array**

```java
while (jsonParser.nextToken() != JsonToken.END_ARRAY)
```

✔ Processes each object one by one

---

4. **Process Each Object**

```java
while (jsonParser.nextToken() != JsonToken.END_OBJECT)
```

✔ Reads fields inside object

---

5. **Read Field Name**

```java
String fieldName = jsonParser.currentName();
```

---

6. **Read Field Value**

```java
jsonParser.nextToken();
taskStatus = TaskStatus.valueOf(jsonParser.getText());
```

---

7. **Apply Logic**

```java
if (TaskStatus.ON_HOLD.equals(taskStatus)) {
    onHoldTasks++;
}
```

---

8. **Close Parser**

```java
jsonParser.close();
```

✔ Frees system resources

---

### ✅ Key Insight

👉 JSON is processed:

* **Sequentially**
* **Token by token**
* Without storing full structure

---

## 4. Writing JSON with `JsonGenerator`

---

### 🔹 Core Concepts

* **JsonGenerator** → writes JSON stream
* Writes **tokens sequentially**

---

### 🔹 Key Methods

| Method               | Purpose            |
| -------------------- | ------------------ |
| `writeStartObject()` | `{`                |
| `writeEndObject()`   | `}`                |
| `writeStartArray()`  | `[`                |
| `writeEndArray()`    | `]`                |
| `writeStringField()` | Write string field |
| `writeNumberField()` | Write number       |
| `writeObjectField()` | Write object       |

---

### 🔹 Target JSON Structure

```json
{
  "code": "C-001",
  "name": "Campaign 1",
  "tasks": [
    { "code": "T-001", "name": "Task 1", "status": "TO_DO" },
    { "code": "T-002", "name": "Task 2", "status": "IN_PROGRESS" }
  ]
}
```

---

### 🔹 Full Writing Example

```java
@Test
void whenWritingCampaignWithTasksUsingJsonGenerator_thenExpectedJsonStructureIsWritten() throws IOException {

    File outputFile = new File("src/test/resources/campaign-with-tasks.json");

    ObjectMapper objectMapper = new ObjectMapper();
    JsonFactory jsonFactory = new JsonFactory();

    JsonGenerator jsonGenerator =
        jsonFactory.createGenerator(outputFile, JsonEncoding.UTF8);

    jsonGenerator.setCodec(objectMapper);
    jsonGenerator.useDefaultPrettyPrinter();

    jsonGenerator.writeStartObject();

    jsonGenerator.writeStringField("code", "C-001");
    jsonGenerator.writeStringField("name", "Campaign 1");

    jsonGenerator.writeFieldName("tasks");
    jsonGenerator.writeStartArray();

    jsonGenerator.writeStartObject();
    jsonGenerator.writeStringField("code", "T-001");
    jsonGenerator.writeStringField("name", "Task 1");
    jsonGenerator.writeObjectField("status", TaskStatus.TO_DO);
    jsonGenerator.writeEndObject();

    jsonGenerator.writeStartObject();
    jsonGenerator.writeStringField("code", "T-002");
    jsonGenerator.writeStringField("name", "Task 2");
    jsonGenerator.writeObjectField("status", TaskStatus.IN_PROGRESS);
    jsonGenerator.writeEndObject();

    jsonGenerator.writeEndArray();
    jsonGenerator.writeEndObject();

    jsonGenerator.close();

    String writtenJson = Files.readString(outputFile.toPath());

    assertEquals(
        objectMapper.readTree(EXPECTED_JSON),
        objectMapper.readTree(writtenJson)
    );
}
```

---

### 🔹 Step-by-Step Explanation

1. **Create Generator**

```java
JsonGenerator jsonGenerator =
    jsonFactory.createGenerator(outputFile, JsonEncoding.UTF8);
```

---

2. **Configure**

```java
jsonGenerator.setCodec(objectMapper);
jsonGenerator.useDefaultPrettyPrinter();
```

* `setCodec()` → serialize complex types (e.g., enums)
* Pretty print → readable JSON

---

3. **Start Root Object**

```java
jsonGenerator.writeStartObject();
```

---

4. **Write Fields**

```java
jsonGenerator.writeStringField("code", "C-001");
jsonGenerator.writeStringField("name", "Campaign 1");
```

---

5. **Write Array**

```java
jsonGenerator.writeFieldName("tasks");
jsonGenerator.writeStartArray();
```

---

6. **Write Nested Objects**

```java
jsonGenerator.writeStartObject();
// fields...
jsonGenerator.writeEndObject();
```

---

7. **Close Structures**

```java
jsonGenerator.writeEndArray();
jsonGenerator.writeEndObject();
```

---

8. **Close Generator**

```java
jsonGenerator.close();
```

✔ Ensures output is flushed and resources released

---

### ✅ Key Insight

👉 JSON must be written in **correct structural order**:

* Start → Write fields → End

---

## 5. Final Summary

---

### 🔹 Streaming API vs Other Approaches

| Feature      | Data Binding | Tree Model | Streaming API |
| ------------ | ------------ | ---------- | ------------- |
| Ease of use  | ✅ Easy       | ⚠️ Medium  | ❌ Hard        |
| Performance  | ⚠️ Medium    | ⚠️ Medium  | ✅ High        |
| Memory usage | ❌ High       | ❌ High     | ✅ Low         |
| Flexibility  | ❌ Low        | ✅ High     | ✅ Very High   |

---

### 🔹 Key Takeaways

* **JsonParser**

    * Reads JSON **token by token**
    * Ideal for **large datasets**

* **JsonGenerator**

    * Writes JSON **sequentially**
    * Full control over output

---

### 🔹 When to Use Streaming API

✔ Very large JSON files
✔ Performance-critical systems
✔ Reactive pipelines
✔ Low-memory environments

---

### ⚠️ When NOT to Use

❌ Simple CRUD APIs
❌ When readability matters more
❌ When full object mapping is needed

---

## 🧠 Final Insight

The **Jackson Streaming API** is the most powerful and efficient way to process JSON, but also the most manual.

👉 Think of it as:

* **Data Binding** → High-level automation
* **Tree Model** → Flexible navigation
* **Streaming API** → Low-level control & performance

---

Here’s a **clear side-by-side comparison** of the three Jackson approaches — **Streaming API vs Tree Model vs Data Binding** — using the *same problem*:

> 🎯 **Goal:** Count how many tasks have status = `"ON_HOLD"` from a JSON array.

---

# ⚔️ Jackson Approaches: Side-by-Side Code Comparison

---

## 🧾 Sample JSON (Same for All)

```json
[
  { "code": "T1", "name": "Task 1", "status": "TO_DO" },
  { "code": "T2", "name": "Task 2", "status": "ON_HOLD" },
  { "code": "T3", "name": "Task 3", "status": "ON_HOLD" }
]
```

---

# 1️⃣ Data Binding (High-Level, Easiest)

### ✅ Code

```java
ObjectMapper objectMapper = new ObjectMapper();

List<Task> tasks = objectMapper.readValue(
    jsonInput,
    new TypeReference<List<Task>>() {}
);

int count = 0;
for (Task task : tasks) {
    if (TaskStatus.ON_HOLD.equals(task.getStatus())) {
        count++;
    }
}
```

---

### ✅ Characteristics

* ✔ Automatic JSON → Java mapping
* ✔ Clean and readable
* ❌ Loads entire JSON into memory
* ❌ Requires matching Java classes

---

### 🧠 Mental Model

👉 “Just give me objects, I’ll work with them”

---

# 2️⃣ Tree Model (Flexible, Medium Complexity)

---

### ✅ Code

```java
ObjectMapper objectMapper = new ObjectMapper();

JsonNode rootNode = objectMapper.readTree(jsonInput);

int count = 0;

for (JsonNode taskNode : rootNode) {
    String status = taskNode.path("status").asText();

    if ("ON_HOLD".equals(status)) {
        count++;
    }
}
```

---

### ✅ Characteristics

* ✔ No need for Java classes
* ✔ Flexible structure handling
* ✔ Safe navigation using `path()`
* ❌ Still loads entire JSON into memory

---

### 🧠 Mental Model

👉 “Let me explore the JSON like a tree”

---

# 3️⃣ Streaming API (Low-Level, Most Efficient)

---

### ✅ Code

```java
JsonFactory factory = new JsonFactory();
JsonParser parser = factory.createParser(jsonInput);

int count = 0;

if (parser.nextToken() == JsonToken.START_ARRAY) {
    while (parser.nextToken() != JsonToken.END_ARRAY) {

        String status = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.currentName();

            if ("status".equals(fieldName)) {
                parser.nextToken();
                status = parser.getText();
            }
        }

        if ("ON_HOLD".equals(status)) {
            count++;
        }
    }
}

parser.close();
```

---

### ✅ Characteristics

* ✔ Constant memory usage
* ✔ Highest performance
* ✔ Processes data incrementally
* ❌ Verbose and complex
* ❌ Manual JSON handling

---

### 🧠 Mental Model

👉 “I’ll manually walk through every JSON token”

---

# 🔍 Direct Comparison

| Feature               | Data Binding | Tree Model | Streaming API |
| --------------------- | ------------ | ---------- | ------------- |
| Code complexity       | ⭐ Easy       | ⭐⭐ Medium  | ⭐⭐⭐ Hard      |
| Readability           | ✅ High       | ⚠️ Medium  | ❌ Low         |
| Memory usage          | ❌ High       | ❌ High     | ✅ Low         |
| Performance           | ⚠️ Medium    | ⚠️ Medium  | ✅ High        |
| Requires Java classes | ✅ Yes        | ❌ No       | ❌ No          |
| Flexibility           | ❌ Low        | ✅ High     | ✅ Very High   |

---

# 🧠 When to Use Each

### ✅ Use Data Binding when:

* You have a **well-defined model**
* JSON is **small/medium**
* You want **clean code**

---

### ✅ Use Tree Model when:

* JSON structure is **dynamic or unknown**
* You need **partial extraction**
* You want flexibility without full complexity

---

### ✅ Use Streaming API when:

* JSON is **very large (MBs/GBs)**
* You need **maximum performance**
* You are building:

    * Data pipelines
    * Reactive systems
    * Real-time processors

---

# ⚡ Same Task — Different Mindsets

| Approach     | Thinking Style                       |
| ------------ | ------------------------------------ |
| Data Binding | “Convert everything into objects”    |
| Tree Model   | “Navigate JSON like a structure”     |
| Streaming    | “Process JSON as a stream of events” |

---

# 🧩 Final Insight

All three approaches solve the **same problem**, but with different trade-offs:

* **Data Binding** → Productivity
* **Tree Model** → Flexibility
* **Streaming API** → Performance

---

Here’s a **clear decision flowchart** to help you choose between **Data Binding**, **Tree Model**, and **Streaming API** in Jackson.

---

# 🧭 Jackson Approach Decision Flowchart

```
START
  |
  |-- Do you have a well-defined Java model that matches JSON?
  |         |
  |         |-- YES --> Is the JSON size small/medium (fits comfortably in memory)?
  |         |               |
  |         |               |-- YES --> ✅ Use DATA BINDING
  |         |               |             (Simple, clean, maintainable)
  |         |               |
  |         |               |-- NO --> ⚠️ Consider STREAMING API
  |         |                             (Large data → avoid memory issues)
  |         |
  |         |-- NO --> Do you need flexible or partial access to JSON?
  |                         |
  |                         |-- YES --> Is the JSON size small/medium?
  |                         |               |
  |                         |               |-- YES --> ✅ Use TREE MODEL
  |                         |               |             (Flexible, no classes needed)
  |                         |               |
  |                         |               |-- NO --> ⚠️ Use STREAMING API
  |                         |                             (Dynamic + large = streaming)
  |                         |
  |                         |-- NO --> Are you optimizing for performance/memory?
  |                                         |
  |                                         |-- YES --> ✅ Use STREAMING API
  |                                         |
  |                                         |-- NO --> ✅ Use TREE MODEL
  |
END
```

---

# 🔍 Simplified Decision Rules

## ✅ Choose **Data Binding** when:

* You have **matching Java classes**
* JSON is **not huge**
* You want **clean, readable code**

👉 Best for:

* REST APIs
* CRUD applications
* Standard business logic

---

## ✅ Choose **Tree Model** when:

* JSON structure is **unknown or dynamic**
* You only need **part of the JSON**
* You want **flexibility without complexity**

👉 Best for:

* External APIs
* JSON transformation
* Partial extraction

---

## ✅ Choose **Streaming API** when:

* JSON is **very large (MBs–GBs)**
* You need **high performance**
* You want **low memory usage**

👉 Best for:

* Big data processing
* Real-time pipelines
* Reactive systems

---

# ⚡ Quick Decision Cheat Sheet

| Situation                     | Best Choice     |
| ----------------------------- | --------------- |
| Small JSON + known structure  | ✅ Data Binding  |
| Unknown structure             | ✅ Tree Model    |
| Need to modify JSON structure | ✅ Tree Model    |
| Very large JSON               | ✅ Streaming API |
| Performance-critical system   | ✅ Streaming API |
| Reactive/stream processing    | ✅ Streaming API |

---

# 🧠 Intuition Shortcut

If you're unsure, use this mental rule:

* 👉 “I want objects” → **Data Binding**
* 👉 “I want flexibility” → **Tree Model**
* 👉 “I want speed & efficiency” → **Streaming API**

---

# 🚀 Real-World Mapping

| Scenario                               | Recommended Approach |
| -------------------------------------- | -------------------- |
| Spring Boot REST API                   | Data Binding         |
| Consuming inconsistent third-party API | Tree Model           |
| Processing 10GB JSON logs              | Streaming API        |
| Transforming JSON structure            | Tree Model           |
| Real-time event stream                 | Streaming API        |

---

# 🧩 Final Insight

Think of the three approaches as levels:

```
HIGH-LEVEL        → Data Binding (easy, less control)
MID-LEVEL         → Tree Model (flexible)
LOW-LEVEL         → Streaming API (powerful, complex)
```

---


