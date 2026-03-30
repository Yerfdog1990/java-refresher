
---

# 👁️ Using JSON Views in Jackson (`@JsonView`)

---

## 1. Overview

In real-world applications, the same object often needs to be **serialized differently depending on context**.

### 🔹 Problem

* Public APIs → limited data
* Admin/internal APIs → full data

👉 Traditional solution:

* Create multiple **DTOs**

❌ Leads to:

* Code duplication
* Maintenance overhead

---

### 🔹 Solution: `@JsonView`

Jackson provides the **`@JsonView` annotation** to:

* Define **multiple views** for the same class
* Control which fields are included in serialization
* Avoid creating multiple DTOs

---

## 2. The `@JsonView` Annotation

---

### 🔹 What It Does

* Controls **field visibility during serialization**
* Groups fields into **logical views**
* Only fields belonging to the **active view** are included

---

### 🧠 Key Idea

👉 “Same object → different JSON outputs”

---

### 🔹 Use Case

| Context          | Data Needed        |
| ---------------- | ------------------ |
| Public API       | Basic info         |
| Admin API        | Full details       |
| Internal systems | Debug/extra fields |

---

## 3. Defining Views

---

### 🔹 What is a View?

* A **marker interface** (empty interface)
* Used to group fields logically

---

### 🔹 Example: Views Class

```java
public class Views {

    public interface Summary {}

    public interface Detail extends Summary {}

    public interface Internal {}
}
```

---

### 🔹 Explanation

| View       | Purpose              |
| ---------- | -------------------- |
| `Summary`  | Basic fields         |
| `Detail`   | More detailed fields |
| `Internal` | Internal-only fields |

---

### 🔹 View Inheritance

```java
public interface Detail extends Summary {}
```

👉 Means:

* `Detail` includes:

    * All `Summary` fields
    * * additional fields

---

### 🧠 Key Insight

👉 Views can be **composed and extended**

---

## 4. Applying `@JsonView` to a Class

---

### 🔹 Example: Campaign Class

```java
public class Campaign {

    @JsonView(Views.Summary.class)
    private String code;

    @JsonView(Views.Summary.class)
    private String name;

    @JsonView(Views.Detail.class)
    private String description;

    private Set<Task> tasks = new HashSet<>();

    private boolean closed;

    // constructors, getters, setters
}
```

---

### 🔹 Field Mapping

| Field         | View           |
| ------------- | -------------- |
| `code`        | Summary        |
| `name`        | Summary        |
| `description` | Detail         |
| `tasks`       | ❌ Not included |
| `closed`      | ❌ Not included |

---

### 🔹 Important Rule

👉 Fields **without `@JsonView`**:

* ❌ Excluded when a view is active
* ✅ Included when no view is used

---

## 5. Serializing with an Active View

---

### 🔹 Sample Object

```java
Campaign campaign = new Campaign(
    "C1",
    "Campaign 1",
    "Description of Campaign 1",
    null,
    false
);
```

---

### 🔹 Important Configuration

```java
objectMapper = JsonMapper.builder()
    .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
    .build();
```

---

### 🧠 Why Disable Default Inclusion?

By default:

* Jackson includes **ALL fields**

    * Even those without `@JsonView`

👉 Disabling ensures:

* Only **annotated fields** are included

---

## 5.1 Summary View Example

---

### 🔹 Code

```java
String json = objectMapper
    .writerWithView(Views.Summary.class)
    .writeValueAsString(campaign);
```

---

### 🔹 Output Includes

✅ `code`
✅ `name`

---

### 🔹 Output Excludes

❌ `description`

❌ `tasks`

❌ `closed`

---

### 🧠 Insight

👉 Only fields annotated with `Summary` are serialized

---

## 5.2 Detail View Example

---

### 🔹 Code

```java
String json = objectMapper
    .writerWithView(Views.Detail.class)
    .writeValueAsString(campaign);
```

---

### 🔹 Output Includes

✅ `code` (from Summary)

✅ `name` (from Summary)

✅ `description` (Detail)

---

### 🔹 Output Excludes

❌ `tasks`

❌ `closed`

---

### 🧠 Key Insight

👉 View inheritance works:

* `Detail` = `Summary` + extra fields

---

## 5.3 Default Behavior (No View)

---

### 🔹 Code

```java
String json = objectMapper.writeValueAsString(campaign);
```

---

### 🔹 Output Includes

✅ All fields:

* `code`
* `name`
* `description`
* `tasks`
* `closed`

---

### 🧠 Key Insight

👉 Without a view:

* Jackson ignores `@JsonView`
* Serializes everything

---

## 5.4 Including a Field in Multiple Views

---

### 🔹 Example

```java
@JsonView({Views.Summary.class, Views.Internal.class})
private String code;
```

---

### 🔹 Behavior

* Included in:

    * Summary view
    * Internal view

---

### 🔹 Test Example

```java
String summaryJson = objectMapper
    .writerWithView(Views.Summary.class)
    .writeValueAsString(campaign);

String internalJson = objectMapper
    .writerWithView(Views.Internal.class)
    .writeValueAsString(campaign);
```

---

### 🧠 Key Insight

👉 A field can belong to **multiple views**

---

## 6. Summary of Behavior

---

### 🔹 Field Inclusion Rules

| Scenario         | Included Fields                      |
| ---------------- | ------------------------------------ |
| Active view      | Only annotated fields                |
| No view          | All fields                           |
| View inheritance | Parent + child fields                |
| Multiple views   | Field appears in all specified views |

---

## 🧠 Best Practices

---

### ✅ 1. Use Views Instead of Multiple DTOs

* Reduces duplication
* Keeps logic centralized

---

### ✅ 2. Organize Views in One Class

```java
public class Views { ... }
```

✔ Improves maintainability

---

### ✅ 3. Disable Default View Inclusion

```java
.disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
```

✔ Prevents unintended field exposure

---

### ✅ 4. Use View Inheritance Wisely

* Build complex views from simple ones

---

### ⚠️ 5. Be Careful with Sensitive Data

* Always explicitly control:

    * Internal fields
    * Security-sensitive data

---

## 🚀 Real-World Use Cases

---

### 🔹 API Design

| Endpoint           | View     |
| ------------------ | -------- |
| `/campaigns`       | Summary  |
| `/campaigns/{id}`  | Detail   |
| `/admin/campaigns` | Internal |

---

### 🔹 Data Security

* Hide sensitive fields from public APIs
* Expose full data internally

---

## 🧩 Final Insight

The `@JsonView` feature allows you to:

* Use **one class**
* Create **multiple JSON representations**
* Control **data exposure precisely**

---

### 🎯 Think of it as:

👉 “Different lenses (views) to look at the same object”

---

# ⚔️ `@JsonView` vs DTO vs `@JsonIgnore`

---

## 🧾 1. Core Idea Comparison

| Approach      | Core Idea                                   |
| ------------- | ------------------------------------------- |
| `@JsonView`   | One class → multiple JSON views             |
| DTO           | Multiple classes → multiple representations |
| `@JsonIgnore` | Permanently exclude fields                  |

---

# 🔍 2. Side-by-Side Example

## 🎯 Scenario:

We have a `Campaign` object, but:

* Public API → only `code`, `name`
* Admin API → full details

---

## 2.1 Using `@JsonView`

```java
public class Campaign {

    @JsonView(Views.Summary.class)
    private String code;

    @JsonView(Views.Summary.class)
    private String name;

    @JsonView(Views.Detail.class)
    private String description;

    private boolean closed;
}
```

```java
objectMapper.writerWithView(Views.Summary.class)
    .writeValueAsString(campaign);
```

---

### ✅ Result

* Flexible output depending on **selected view**

---

## 2.2 Using DTOs

```java
class CampaignSummaryDTO {
    private String code;
    private String name;
}

class CampaignDetailDTO {
    private String code;
    private String name;
    private String description;
    private boolean closed;
}
```

```java
CampaignSummaryDTO dto = mapToSummary(campaign);
```

---

### ✅ Result

* Separate classes for each representation

---

## 2.3 Using `@JsonIgnore`

```java
public class Campaign {

    private String code;
    private String name;

    @JsonIgnore
    private String description;

    @JsonIgnore
    private boolean closed;
}
```

---

### ❌ Result

* Fields are **never serialized**
* No flexibility

---

# ⚖️ 3. Detailed Comparison Table

| Feature                  | `@JsonView` | DTO                 | `@JsonIgnore` |
| ------------------------ | ----------- | ------------------- | ------------- |
| Flexibility              | ✅ High      | ✅ Very High         | ❌ None        |
| Code duplication         | ✅ Low       | ❌ High              | ✅ Low         |
| Ease of use              | ⚠️ Medium   | ⚠️ Medium           | ✅ Easy        |
| Runtime control          | ✅ Yes       | ❌ No (compile-time) | ❌ No          |
| Multiple representations | ✅ Yes       | ✅ Yes               | ❌ No          |
| Maintainability          | ✅ Good      | ⚠️ Can grow complex | ✅ Simple      |
| Type safety              | ⚠️ Medium   | ✅ High              | ✅ High        |
| Best for APIs            | ✅ Yes       | ✅ Yes               | ❌ No          |

---

# 🧠 4. When to Use Each

---

## ✅ Use `@JsonView` when:

* You want **multiple views from one class**
* You want to **avoid DTO duplication**
* You need **dynamic control at runtime**

👉 Best for:

* REST APIs with different audiences
* Quick and clean implementations

---

## ✅ Use DTOs when:

* You need **strict separation of concerns**
* API structure differs significantly from domain model
* You want **full control over data transformation**

👉 Best for:

* Large applications
* Complex business logic
* Public APIs with strict contracts

---

## ❌ Use `@JsonIgnore` when:

* Field should **never be exposed**
* Sensitive/internal data

👉 Best for:

* Passwords
* Internal IDs
* Debug fields

---

# ⚡ 5. Strengths & Weaknesses

---

## 🔹 `@JsonView`

### ✅ Pros

* No duplication
* Flexible
* Supports inheritance

### ❌ Cons

* Can become hard to manage with many views
* Less explicit than DTOs

---

## 🔹 DTO

### ✅ Pros

* Clean separation
* Highly maintainable for large systems
* Clear API contracts

### ❌ Cons

* Boilerplate code
* Mapping overhead

---

## 🔹 `@JsonIgnore`

### ✅ Pros

* Simple
* Clear intent

### ❌ Cons

* Not flexible
* Cannot support multiple views

---

# 🚀 6. Real-World Usage Patterns

---

## 🔹 Small Projects

👉 Prefer:

* `@JsonView`

---

## 🔹 Large Enterprise Systems

👉 Prefer:

* DTOs

---

## 🔹 Security-Sensitive Fields

👉 Always use:

* `@JsonIgnore`

---

# 🧩 7. Combined Strategy (Best Practice)

In real-world systems, you often **combine all three**:

```java
public class User {

    private String username;

    @JsonIgnore
    private String password;

    @JsonView(Views.Public.class)
    private String email;
}
```

👉 And still use DTOs for:

* External API boundaries

---

# 🧠 Final Insight

Think of them like this:

| Tool          | Analogy                                    |
| ------------- | ------------------------------------------ |
| `@JsonView`   | Different camera angles of the same object |
| DTO           | Different objects for different purposes   |
| `@JsonIgnore` | Permanent censorship                       |

---

# 🎯 Final Recommendation

* Use **DTOs** for clean architecture
* Use **`@JsonView`** for flexibility and reduced duplication
* Use **`@JsonIgnore`** for security and permanent exclusions

---

