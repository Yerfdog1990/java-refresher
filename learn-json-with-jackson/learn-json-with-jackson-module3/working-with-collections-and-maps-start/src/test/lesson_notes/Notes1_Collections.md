
---

# **Working with Collections (Jackson Databind)**

## **1. Overview**

In real-world applications, data is often grouped into collections rather than single objects. Jackson provides seamless support for serializing and deserializing common Java collection types such as:

* **List** (ordered collection)
* **Set** (unique elements, optional ordering)
* **Map** (key-value pairs)

Using the **ObjectMapper**, we can easily convert these collections to JSON and back.

---

# **2. Working With Lists**

## **2.1 What is a List?**

A **List**:

* Is an **ordered collection**
* Allows **duplicate elements**
* Maintains **insertion order**
* Typically uses **generics** (e.g., `List<Task>`)

---

## **2.2 Serializing a List to JSON**

### ✅ Code Example

```java
@Test
void givenListOfTasks_whenSerializing_thenJsonArrayPreservesOrder() 
  throws JsonProcessingException {
    List<Task> tasks = List.of(task1, task2);
    String json = objectMapper.writeValueAsString(tasks);
    System.out.println(json);

    assertTrue(json.contains("\"code\":\"T1\""));
    assertTrue(json.contains("\"code\":\"T2\""));
    assertTrue(json.indexOf("\"code\":\"T1\"") < json.indexOf("\"code\":\"T2\""));
}
```

### 🔍 Key Points

* `writeValueAsString()` converts the list into a **JSON array**
* Order is preserved because:

    * Lists maintain **insertion order**
    * Jackson respects the **iteration order**

### 📌 Resulting JSON

```json
[
  {"code":"T1","name":"Task 1","description":"Description of Task 1","dueDate":null,"status":"TO_DO","campaign":null},
  {"code":"T2","name":"Task 2","description":"Description of Task 2","dueDate":null,"status":"TO_DO","campaign":null}
]
```

✔ `"T1"` appears before `"T2"` → order preserved

---

## **2.3 Deserializing JSON Into a List**

### ✅ Code Example

```java
@Test
void givenJsonTaskArray_whenDeserializingToListOfTasks_thenCorrect() throws Exception {

    String json = """
        [
            {"code":"T1","name":"Task 1","description":"Description of Task 1","dueDate":null,"status":"TO_DO","campaign":null},
            {"code":"T2","name":"Task 2","description":"Description of Task 2","dueDate":null,"status":"TO_DO","campaign":null}
        ]
        """;

    List<Task> tasks = objectMapper.readValue(json, new TypeReference<List<Task>>() {
    });

    assertTrue(tasks instanceof ArrayList);
    assertEquals(2, tasks.size());
    assertEquals("T1", tasks.get(0).getCode());
    assertEquals("T2", tasks.get(1).getCode());
}
```

### 🔍 Key Points

* Use **`TypeReference<List<Task>>`** due to **type erasure**
* Default implementation: **`ArrayList`**
* Order from JSON is preserved in the list

---

# **3. Working With Sets**

## **3.1 What is a Set?**

A **Set**:

* Does **not allow duplicates**
* May or may not preserve order depending on implementation:

| Implementation  | Order Behavior            |
| --------------- | ------------------------- |
| `HashSet`       | No guaranteed order       |
| `LinkedHashSet` | Preserves insertion order |
| `TreeSet`       | Sorted order              |

✔ Use **LinkedHashSet or TreeSet** for deterministic output

---

## **3.2 Serializing a Set to JSON**

### ✅ Code Example

```java
@Test
void givenSetOfTasks_whenSerializing_thenJsonArrayWithoutOrderGuarantee() 
  throws Exception {
    Set<Task> tasks = new HashSet<>();
    tasks.add(task1);
    tasks.add(task2);
    String json = objectMapper.writeValueAsString(tasks);

    assertTrue(json.contains("\"code\":\"T1\""));
    assertTrue(json.contains("\"code\":\"T2\""));
}
```

### 🔍 Key Points

* Serialized as a **JSON array**
* **Order is NOT guaranteed** with `HashSet`
* Only verify **presence**, not position

---

## **3.3 Deserializing JSON Into a Set**

### ✅ Code Example

```java
@Test
void givenJsonTaskArray_whenDeserializingToSet_thenOk() throws Exception {
    String json = """
        [
            {"code":"T1","name":"Task 1","description":"Description of Task 1","dueDate":null,"status":"TO_DO","campaign":null},
            {"code":"T2","name":"Task 2","description":"Description of Task 2","dueDate":null,"status":"TO_DO","campaign":null}
        ]
        """;

    Set<Task> tasks = objectMapper.readValue(json, new TypeReference<Set<Task>>() {
    });

    assertTrue(tasks instanceof HashSet);
    assertEquals(2, tasks.size());
    assertTrue(tasks.stream().anyMatch(task -> "T1".equals(task.getCode())));
    assertTrue(tasks.stream().anyMatch(task -> "T2".equals(task.getCode())));
}
```

### 🔍 Key Points

* Default implementation: **HashSet**
* Use **stream checks** instead of index-based assertions
* Use `LinkedHashSet` in `TypeReference` if order matters

---

# **4. Working With Maps**

## **4.1 What is a Map?**

A **Map**:

* Stores **key-value pairs**
* Keys become **JSON property names**
* Values become **JSON values**

---

## **4.2 Serializing a Map to JSON**

### ✅ Code Example

```java
@Test
void givenMapOfTasks_whenSerializing_thenOk() throws Exception {
    Map<String, Task> byCode = new LinkedHashMap<>();
    byCode.put("T1", task1);
    byCode.put("T2", task2);

    String json = objectMapper.writeValueAsString(byCode);
    System.out.println(json);

    assertTrue(json.contains("\"T1\""));
    assertTrue(json.contains("\"T2\""));
}
```

### 🔍 Key Points

* `LinkedHashMap` preserves **insertion order**
* JSON output is an **object**, not an array

### 📌 Resulting JSON

```json
{
  "T1": { "code": "T1", "name": "Task 1", "description": "Task 1 description", "status": "TO_DO" },
  "T2": { "code": "T2", "name": "Task 2", "description": "Task 2 description", "status": "TO_DO" }
}
```

---

### ⚙️ Important Behavior

* Keys are converted using **`toString()`**
* Non-string keys require **custom KeyDeserializer**

---

### ⚙️ Useful Features

* `SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS` → sort keys
* `SerializationFeature.WRITE_NULL_MAP_VALUES` → skip null values

---

## **4.3 Deserializing JSON Into a Map**

### ✅ Code Example

```java
@Test 
void givenJson_whenDeserializingToMap_thenKeysAndValuesAreRestored() throws Exception { 
    String json = """
        {
          "T1": { "code": "T1", "name": "Task 1", "description": "Task 1 description", "status": "TO_DO" },
          "T2": { "code": "T2", "name": "Task 2", "description": "Task 2 description", "status": "TO_DO" }
        }
        """;
    
    Map<String, Task> tasks = objectMapper.readValue(json, new TypeReference<Map<String, Task>>() {}); 

    assertTrue(tasks instanceof LinkedHashMap);
    assertTrue(tasks.containsKey("T1")); 
    assertTrue(tasks.containsKey("T2")); 
    assertEquals("Task 2", tasks.get("T2").getName()); 
}
```

### 🔍 Key Points

* Default implementation: **LinkedHashMap**
* Preserves **JSON key order**
* Keys and values are fully restored

---

# **5. Nested and Complex Structures**

## **5.1 Serializing Nested Collections**

### ✅ Code Example

```java
@Test
void givenMapOfLists_whenSerializing_thenNestedShapeIsHandled() throws Exception {
    Map<String, List<Task>> groups = new LinkedHashMap<>();
    groups.put("todo", List.of(task1, task2));
    groups.put("empty", Collections.emptyList());

    String json = objectMapper.writeValueAsString(groups);
    System.out.println(json);

    assertTrue(json.contains("\"todo\""));
    assertTrue(json.contains("\"empty\""));
}
```

### 📌 JSON Output

```json
{
  "todo": [
    { "code": "T1", "name": "Task 1", "description": "Task 1 description", "status": "TO_DO" },
    { "code": "T2", "name": "Task 2", "description": "Task 2 description", "status": "TO_DO" }
  ],
  "empty": []
}
```

---

## **5.2 Deserializing Nested Structures**

### ✅ Code Example

```java
@Test
void givenMapOfLists_whenDeserializing_thenNestedShapeIsHandled() throws Exception {
    String json = """
        {
            "todo": [
              { "code": "T1", "name": "Task 1", "description": "Task 1 description", "status": "TO_DO" },
              { "code": "T2", "name": "Task 2", "description": "Task 2 description", "status": "TO_DO" }
            ],
            "empty": []
          }
        """;

    Map<String, List<Task>> restored = objectMapper.readValue(
      json, new TypeReference<Map<String, List<Task>>>() { });

    assertEquals(2, restored.get("todo").size());
    assertTrue(restored.get("empty").isEmpty());
}
```

### 🔍 Key Points

* Use **nested `TypeReference`**
* Jackson correctly reconstructs:

    * Map keys
    * List values
    * Inner objects
* Handles empty collections safely

---

# **6. Key Takeaways (Summary)**

### ✅ Serialization

* `writeValueAsString()` converts collections → JSON
* Lists → JSON arrays (ordered)
* Sets → JSON arrays (order depends on implementation)
* Maps → JSON objects (key-value structure)

### ✅ Deserialization

* `readValue()` converts JSON → Java objects
* **TypeReference is essential** for generic types
* Default implementations:

    * List → `ArrayList`
    * Set → `HashSet`
    * Map → `LinkedHashMap`

### ✅ Ordering Rules

* List → always ordered
* Set → depends on implementation
* Map → use `LinkedHashMap` for stable order

### ✅ Nested Structures

* Fully supported
* Require proper **generic type definition**

---

