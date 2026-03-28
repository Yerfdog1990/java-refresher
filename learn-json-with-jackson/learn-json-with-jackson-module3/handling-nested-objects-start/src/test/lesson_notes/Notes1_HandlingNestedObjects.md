
---

# **Handling Nested Objects in Jackson**

## **1. Overview**

Most real-world JSON payloads are **nested**, meaning one object contains other objects. These relationships can vary in:

### **a. Direction of Relationship**

* **Unidirectional**

    * Object A references Object B
    * Object B does **not** reference Object A

* **Bidirectional**

    * Object A references Object B
    * Object B also references Object A

### **b. Cardinality (Multiplicity)**

Defines how many instances relate:

* **One-to-One**

    * Example: One country ↔ one capital

* **One-to-Many**

    * Example: One campaign → many tasks

* **Many-to-One**

    * Example: Many campaigns → one address

* **Many-to-Many**

    * Example: Many teachers ↔ many courses

### ⚠️ Key Insight

* **Cardinality does NOT affect Jackson serialization/deserialization**
* **Direction DOES affect it**, especially with bidirectional links

---

## **2. Java Objects Relationship Modeling**

### **Domain Model**

```java
public class Campaign {

    private String code;

    private String name;

    private String description;

    @JsonManagedReference
    private Set<Task> tasks = new HashSet<>();

    @JsonUnwrapped
    private Address address;

    private boolean closed;

    public Campaign(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
}
```

```java
public class Task {

    private String code;

    private String name;

    private String description;

    private LocalDate dueDate;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "code")
    private TaskStatus status;

    @JsonBackReference
    private Campaign campaign;

    public Task(String code, String name, String description, LocalDate dueDate, TaskStatus status, Campaign campaign) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.campaign = campaign;
    }
}
```

```java
public class Address {
    private String street;
    private String city;
    private String postalCode;
    private String country;

    public Address(String street, String city, String postalCode, String country) {
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }
}
```

### **Identified Relationships**

1. **Campaign ↔ Task**

    * Type: **Bidirectional**
    * Cardinality: **One-to-Many**
    * One campaign → many tasks
    * Each task → one campaign

2. **Campaign → Address**

    * Type: **Unidirectional**
    * Cardinality: **One-to-One**
    * Campaign has one address
    * Address does NOT reference campaign

---

## **3. Basic Serialization of Nested Objects**

### **Scenario**

* Campaign has tasks
* No back-reference (`Task.campaign = null`)
* Structure is strictly: **Parent → Children**

### **Test Example**

```java
@Test
void givenCampaignWithTasks_whenSerialize_thenNestedArrayPresent()
  throws Exception {

    Campaign campaign = new Campaign("C100", "Campaign 100", "Nested example");

    Task t1 = new Task("T1", "Task 1", "First", null, TaskStatus.TO_DO, null);
    Task t2 = new Task("T2", "Task 2", "Second", null, TaskStatus.TO_DO, null);

    campaign.getTasks().add(t1);
    campaign.getTasks().add(t2);

    Address address = new Address("Street", "City", "10A103", "US");
    campaign.setAddress(address);

    String json = objectMapper.writeValueAsString(campaign);

    assertTrue(json.contains("\"code\":\"C100\""));
    assertTrue(json.contains("\"code\":\"T1\""));
    assertTrue(json.contains("\"code\":\"T2\""));
}
```

### **Resulting JSON**

```json
{
  "code":"C100",
  "name":"Campaign 100",
  "description":"Nested example",
  "tasks":[
    {"code":"T1","name":"Task 1","description":"First","status":"TO_DO"},
    {"code":"T2","name":"Task 2","description":"Second","status":"TO_DO"}
  ],
  "address": {
    "street": "Street",
    "city": "City",
    "postalCode": "10A103",
    "country": "US"
  },
  "closed": false
}
```

### ✅ Key Observations

* `tasks` → serialized as **JSON array**
* `address` → serialized as **nested object**
* No parent reference → no recursion problem

---

## **3.1 @JsonUnwrapped Annotation (Flattening)**

### **Purpose**

Flattens nested objects into the parent JSON

### **Usage**

```java
public class Campaign {

    @JsonUnwrapped
    private Address address;
}
```

### **Flattened JSON Output**

```json
{
  "code":"C100",
  "name":"Campaign 100",
  "description":"Nested example",
  "tasks":[ ... ],
  "street": "Street",
  "city": "City",
  "postalCode": "10A103",
  "country": "US",
  "closed": false
}
```

### ⚠️ Important Notes

* Removes `"address"` wrapper
* Fields appear at **top level**
* ❌ Does NOT work for collections (e.g., `Set<Task>`)
* Field names must be **clear to avoid conflicts**

---

## **4. Basic Deserialization of Nested Objects**

### **Scenario**

Convert JSON → Java object

### **Test Example**

```java
@Test 
void givenCampaignJsonString_whenDeserialize_thenCampaignAndTasksObjects()
  throws Exception { 

    String json = """
    {
      "code": "C200",
      "name": "Campaign 200",
      "description": "Nested example",
      "tasks": [
        { "code": "T1", "name": "Task 1", "description": "First", "status": "TO_DO" },
        { "code": "T2", "name": "Task 2", "description": "Second", "status": "TO_DO" }
      ]
    }
    """;

    Campaign campaign = objectMapper.readValue(json, Campaign.class);

    assertEquals("C200", campaign.getCode());
    assertEquals(2, campaign.getTasks().size());
}
```

### ✅ Key Points

* `readValue()` maps JSON → Java objects
* Automatically:

    * Creates `Campaign`
    * Creates `Task` objects
    * Populates `tasks` collection

### 🔁 With @JsonUnwrapped

* Works seamlessly in reverse
* Flattened fields are mapped back to nested object

---

## **5. Bidirectional Links and Infinite Recursion**

### ❗ Problem

When both sides reference each other:

```java
campaign → tasks → campaign → tasks → ...
```

### 🔥 Result

* Infinite loop
* Exception:

```
JsonMappingException: Document nesting depth exceeds maximum allowed
```

---

## **6. Solutions for Bidirectional Relationships**

---

### **6.1 Solution 1: @JsonIgnore (Simplest)**

### **Idea**

Ignore back-reference during serialization

```java
class Task {
    @JsonIgnore
    private Campaign campaign;
}
```

### ✅ Behavior

* Tasks serialized normally
* `campaign` field in Task is ignored
* No infinite recursion

### ✔ When to Use

* Client does NOT need parent reference in JSON

---

### **6.2 Solution 2: @JsonManagedReference / @JsonBackReference**

### **Purpose**

* Keep relationship in memory
* Avoid recursion in JSON

### **Parent (Forward Side)**

```java
class Campaign {
    @JsonManagedReference
    private Set<Task> tasks = new HashSet<>();
}
```

### **Child (Back Side)**

```java
class Task {
    @JsonBackReference
    private Campaign campaign;
}
```

### ✅ Behavior

* JSON:

    * Includes `tasks`
    * Excludes `campaign` inside Task
* Deserialization:

    * Restores **bidirectional link in memory**

### ✔ When to Use

* Need relationship after deserialization
* But don’t need back-reference in JSON

---

### **6.3 Solution 3: @JsonIdentityInfo (Advanced)**

### **Purpose**

Serialize references using **IDs**

### **Campaign**

```java
@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class,
  property = "code"
)
class Campaign { }
```

### **Task**

```java
@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class,
  property = "code"
)
class Task { }
```

### **Resulting JSON**

```json
{
  "code": "C400",
  "tasks": [
    {
      "code": "T1",
      "campaign": "C400"
    }
  ]
}
```

### ✅ Behavior

* First occurrence → full object
* Subsequent references → ID only

### 🔁 Deserialization

* Jackson:

    * Tracks objects by ID
    * Reuses existing instances
    * Rebuilds full object graph

### ✔ Advantages

* Handles circular references
* Supports complex graphs (e.g., many-to-many)
* Reduces JSON size

### ✔ When to Use

* Clients need explicit references
* Complex object graphs
* Caching / diff / merging scenarios

---

## **7. Summary & Key Takeaways**

### ✅ Relationship Insights

* Cardinality → no effect on Jackson
* Direction → critical for serialization

### ✅ Serialization

* Unidirectional → works out of the box
* Nested objects → become JSON objects/arrays

### ✅ Deserialization

* Automatic mapping via `readValue()`
* Handles collections and nested structures

### ⚠️ Bidirectional Problems

* Causes infinite recursion
* Must be handled explicitly

### ✅ Solutions Comparison

| Approach                                     | JSON Output        | Keeps Relationship in Memory | Use Case       |
| -------------------------------------------- | ------------------ | ---------------------------- | -------------- |
| `@JsonIgnore`                                | No back-reference  | ❌ No                         | Simple APIs    |
| `@JsonManagedReference / @JsonBackReference` | One-direction only | ✅ Yes                        | Most common    |
| `@JsonIdentityInfo`                          | Uses IDs           | ✅ Yes                        | Complex graphs |

---

## **8. Conclusion**

Handling nested objects in Jackson requires understanding **object relationships**, especially **direction**. While unidirectional relationships work automatically, bidirectional ones must be carefully managed to avoid infinite recursion.

Jackson provides flexible solutions:

* Simple ignoring
* Managed references
* Identity-based serialization

Choosing the right approach depends on:

* API requirements
* Complexity of relationships
* Client needs for data structure

---

