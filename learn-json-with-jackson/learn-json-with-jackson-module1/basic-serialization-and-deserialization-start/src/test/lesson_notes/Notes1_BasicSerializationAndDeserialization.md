
---

# **Basic Serialization and Deserialization**

## **1. Introduction**

In modern **REST-driven applications**, data typically:

* Leaves the JVM as **JSON**
* Returns back as **JSON**

Jackson enables seamless conversion between:

* Java objects (POJOs)
* JSON strings or files

---

## **Learning Objectives**

By the end of this topic, you should be able to:

* Convert a **single POJO or a collection** into JSON
* Convert JSON back into Java objects with **type safety**
* Understand the **minimal requirements** for Jackson to work without annotations

---

# **2. Serialization: Java → JSON**

## **2.1 What is Serialization?**

Serialization is the process of converting a Java object into a format suitable for:

* Storage
* Transmission
* API communication

### Example Format:

* JSON (most common)

---

## **Deserialization (Reverse Process)**

Deserialization converts:

* JSON → Java Object

---

## **Why It Matters**

* Enables communication between systems
* Used in REST APIs
* Allows persistent storage of objects

---

# **2.2 Requirements for Jackson**

For Jackson to work **without annotations or extra configuration**, a class must have:

### **1. Public No-Argument Constructor**

* Required for object creation during deserialization

### **2. Accessible Properties**

* Either:

    * Public fields
    * OR getters and setters

---

## **Example: Campaign Class**

```java
public class Campaign {
    private String code;
    private String name;
    private String description;
    private Set<Task> tasks = new HashSet<>();
    private boolean closed;

    public Campaign() {
    }

    // additional constructors
    // getters and setters
}
```

---

## **Key Concept: Automatic Mapping**

Jackson maps:

```text
Java field → JSON key
```

Example:

```text
code → "code"
name → "name"
```

👉 No annotations needed if names match.

---

# **2.3 Java Object → JSON String**

## **Example Test**

```java
public class JacksonUnitTest {
    private Campaign campaign = new Campaign("CAMP1", "CampaignName", "Description");
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void whenSerializingCampaign_thenCorrectJson() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(campaign);

        System.out.println(json);

        assertTrue(json.contains("\"code\":\"CAMP1\""));
        assertTrue(json.contains("\"name\":\"CampaignName\""));
        assertTrue(json.contains("\"description\":\"Description\""));
        assertTrue(json.contains("\"tasks\":[]"));
        assertTrue(json.contains("\"closed\":false"));
    }
}
```

---

## **Explanation**

* `writeValueAsString()` converts object → JSON string
* Jackson:

    * Reads fields/getters
    * Converts to JSON automatically

---

## **Generated JSON**

```json
{
  "code" : "CAMP1",
  "name" : "CampaignName",
  "description" : "Description",
  "tasks" : [ ],
  "closed" : false
}
```

---

## **Important Observations**

* `tasks` → empty array (`[]`)
* `closed` → defaults to `false`
* No annotations required

---

# **2.4 Writing JSON to a File**

## **Example**

```java
@Test
public void whenWritingCampaignToFile_thenFileContainsCorrectJson() throws IOException {
    Path tempFile = Files.createTempFile("campaign", ".json");
    File file = tempFile.toFile();
    file.deleteOnExit();

    objectMapper.writeValue(file, campaign);

    assertTrue(file.exists());
    String content = Files.readString(tempFile);
    
    assertTrue(content.contains("\"code\":\"CAMP1\""));
    assertTrue(content.contains("\"name\":\"CampaignName\""));
    assertTrue(content.contains("\"description\":\"Description\""));
    assertTrue(content.contains("\"tasks\":[]"));
    assertTrue(content.contains("\"closed\":false"));
}
```

---

## **Explanation**

* `writeValue(file, object)` writes JSON directly to file
* `Files.createTempFile()` avoids name conflicts
* `deleteOnExit()` cleans up automatically

---

# **2.5 Serializing Collections (List → JSON Array)**

## **Example**

```java
@Test
public void whenSerializingCampaignList_thenCorrectJsonArray() throws JsonProcessingException {
    List<Campaign> campaigns = List.of(
        new Campaign("CAMP1", "CampaignName", "Description"),
        new Campaign("CAMP2", "SecondCampaign", "SecondDescription")
    );
    
    String json = objectMapper.writeValueAsString(campaigns);
    
    System.out.println(json);
    
    assertTrue(json.startsWith("["));
    assertTrue(json.contains("\"code\":\"CAMP1\""));
    assertTrue(json.contains("\"code\":\"CAMP2\""));
}
```

---

## **Generated JSON**

```json
[
  {
    "code" : "CAMP1",
    "name" : "CampaignName",
    "description" : "Description",
    "tasks" : [ ],
    "closed" : false
  },
  {
    "code" : "CAMP2",
    "name" : "SecondCampaign",
    "description" : "SecondDescription",
    "tasks" : [ ],
    "closed" : false
  }
]
```

---

## **Key Points**

* Lists become JSON arrays `[ ... ]`
* Each object is fully serialized
* Structure is preserved

---

# **3. Deserialization: JSON → Java**

---

## **3.1 JSON String → Java Object**

### **Example**

```java
@Test
public void whenDeserializingJsonToCampaign_thenCorrectObject() throws JsonProcessingException {
    String json = """
        {
            "code" : "CAMP1",
            "name" : "CampaignName",
            "description" : "Description",
            "tasks" : [ ],
            "closed" : false
        }
        """;
    
    Campaign result = objectMapper.readValue(json, Campaign.class);
    
    System.out.println(result);
    
    assertEquals("CAMP1", result.getCode());
    assertEquals("CampaignName", result.getName());
    assertEquals("Description", result.getDescription());
    assertFalse(result.isClosed());
    assertTrue(result.getTasks().isEmpty());
}
```

---

## **Explanation**

* `readValue()` converts JSON → Java object
* Automatically maps fields by name

---

## **Output**

```text
Campaign [code=CAMP1, name=CampaignName, description=Description, closed=false]
```

---

# **3.2 JSON File → Java Object**

## **Example**

```java
@Test
public void whenDeserializingJsonFromFile_thenCorrectObject() throws IOException {
    Path tempFile = Files.createTempFile("campaign", ".json");
    File file = tempFile.toFile();
    file.deleteOnExit(); 
    
    String json = """
        {
            "code" : "CAMP1",
            "name" : "CampaignName",
            "description" : "Description",
            "tasks" : [ ],
            "closed" : false
        }
        """;
    Files.writeString(tempFile, json);
    
    Campaign result = objectMapper.readValue(file, Campaign.class);
    
    System.out.println(result);
    
    assertEquals("CAMP1", result.getCode());
    assertEquals("CampaignName", result.getName());
    assertEquals("Description", result.getDescription());
    assertFalse(result.isClosed());
    assertTrue(result.getTasks().isEmpty());
}
```

---

## **Explanation**

* Reads JSON directly from file
* Same mapping process as string deserialization

---

# **3.3 JSON Array → List of Objects**

## **Incorrect Approach**

```java
List<Campaign> resultList = objectMapper.readValue(jsonArray, List.class);
```

### ❌ Problem:

* Causes `ClassCastException`
* Returns `List<LinkedHashMap>` instead of `List<Campaign>`

---

## **Why This Happens**

* Java uses **type erasure**
* JVM only sees:

```text
List (not List<Campaign>)
```

---

## **Correct Approach: Using TypeReference**

### **Example**

```java
@Test
public void whenDeserializingJsonArrayToList_thenCorrectList() throws JsonProcessingException {
    String jsonArray = """
        [
            {
                "code" : "CAMP1",
                "name" : "CampaignName",
                "description" : "Description",
                "tasks" : [ ],
                "closed" : false
            },
            {
                "code" : "CAMP2",
                "name" : "SecondCampaign",
                "description" : "SecondDescription",
                "tasks" : [ ],
                "closed" : false
            }
        ]
    """;
    
    List<Campaign> resultList = objectMapper.readValue(
        jsonArray,
        new TypeReference<List<Campaign>>() {}
    );
    
    System.out.println(resultList);
    
    assertEquals(2, resultList.size());
    assertEquals("CAMP1", resultList.get(0).getCode());
    assertEquals("CAMP2", resultList.get(1).getCode());
}
```

---

## **Explanation**

* `TypeReference` preserves generic type info
* Allows Jackson to correctly create `List<Campaign>`

---

# **4. Key Takeaways**

### **Serialization**

* `writeValueAsString()` → JSON string
* `writeValue()` → JSON file
* Supports single objects and collections

---

### **Deserialization**

* `readValue()` → Java object
* Works with:

    * Strings
    * Files
    * Arrays (with `TypeReference`)

---

### **Requirements**

* No-arg constructor
* Getters/setters
* Matching field names

---

### **Important Concept**

👉 Jackson works automatically when:

* Java field names = JSON keys
* Class structure follows JavaBean conventions

---

# **5. Conclusion**

Jackson makes **serialization and deserialization extremely simple** with minimal setup. By following a few structural rules, developers can convert between Java objects and JSON effortlessly.

Even without annotations, Jackson:

* Handles objects and collections
* Supports files and strings
* Maintains type safety (with `TypeReference`)

---

