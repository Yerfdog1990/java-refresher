
---

# 📘 **Lesson Notes: Using Query by Example in Spring Data JPA**

---

# **1. Introduction**

**Query by Example (QBE)** is a querying technique that lets developers search their database by providing an **example object**, instead of writing SQL or JPQL queries.

It is:

* **Dynamic** → query is generated according to non-null fields
* **Declarative** → you specify *what* to match, not *how*
* **Non-invasive** → no custom query strings
* **Flexible** → can be reused, easily extended

Spring Data JPA supports QBE natively via:

1. **Probe** – an entity instance containing filter values
2. **ExampleMatcher** – defines how fields should match (startsWith, ignoreCase, ignore nulls…)
3. **Example<T>** – a combination of Probe + ExampleMatcher

---

# **2. Repository Setup**

To use QBE, the repository must extend:

```java
public interface TaskRepository extends JpaRepository<Task, Long> {}
public interface CampaignRepository extends JpaRepository<Campaign, Long> {}
```

`JpaRepository` extends:

```
QueryByExampleExecutor<T>
```

giving us:

```java
<S extends T> Optional<S> findOne(Example<S> example);
<S extends T> Iterable<S> findAll(Example<S> example);
<S extends T> Iterable<S> findAll(Example<S> example, Sort sort);
<S extends T> Page<S> findAll(Example<S> example, Pageable pageable);
<S extends T> long count(Example<S> example);
<S extends T> boolean exists(Example<S> example);
```

---

# **3. Key Concepts**

## **3.1 Probe**

A domain object with **fields set that you want to filter by**.

Example:

```java
Campaign probe = new Campaign();
probe.setName("Campaign 1");
```

## **3.2 ExampleMatcher**

Defines **how** the probe's fields should match.

Examples:

* Ignore case
* Contains / startsWith / endsWith
* Ignore nulls
* Configure specific fields

## **3.3 Example**

Pairing of:

```
Example = Probe + ExampleMatcher
```

Used when calling `findAll(example)`.

---

# **4. Basic QBE Usage Example**

## **Step 1 — Create a Probe**

```java
Campaign probe = new Campaign();
probe.setName("Campaign 1");
```

## **Step 2 — Create an Example**

```java
Example<Campaign> example = Example.of(probe);
```

Spring uses the default matcher:

* Case-sensitive
* Exact match
* Ignore nulls

## **Step 3 — Query the Repository**

```java
Optional<Campaign> result = campaignRepository.findOne(example);
```

Produces SQL like:

```sql
SELECT * FROM campaign WHERE name = 'Campaign 1';
```

---

# **5. Customizing the ExampleMatcher**

To make QBE useful, we must customize matching behavior.

---

## **5.1 Ignore Case**

```java
ExampleMatcher matcher = ExampleMatcher.matchingAll()
    .withIgnoreCase();
```

```java
Example<Campaign> example = Example.of(probe, matcher);
```

---

## **5.2 Partial Match (startsWith, contains, endsWith)**

```java
ExampleMatcher matcher = ExampleMatcher.matching()
    .withMatcher("name",
        ExampleMatcher.GenericPropertyMatchers.startsWith().ignoreCase()
    );
```

---

## **5.3 Ignoring properties (e.g., generated fields)**

```java
ExampleMatcher matcher = ExampleMatcher.matching()
    .withIgnorePaths("id", "uuid");
```

This is essential when:

* Fields are auto-generated
* You don’t want them to be included in filtering

---

## **5.4 Combining Multiple Matchers**

```java
ExampleMatcher matcher = ExampleMatcher.matching()
    .withMatcher("description", m -> m.contains().ignoreCase())
    .withMatcher("dueDate", m -> m.exact())
    .withIgnorePaths("uuid", "status");
```

---

# **6. Query by Example With Multiple Properties**

```java
Task probe = new Task();
probe.setDescription("Description");
probe.setDueDate(LocalDate.of(2025, 3, 16));

ExampleMatcher matcher = ExampleMatcher.matching()
    .withMatcher("description", m -> m.endsWith().ignoreCase())
    .withMatcher("dueDate", m -> m.exact())
    .withIgnorePaths("uuid");

Example<Task> example = Example.of(probe, matcher);

Optional<Task> result = taskRepository.findOne(example);
```

---

# **7. FetchableFluentQuery — Advanced QBE Features**

You can customize projections, sorting, or shape of results:

```java
Optional<Task> match = taskRepository.findBy(example, q ->
        q.sortBy(Sort.by("dueDate").descending())
         .first()
);
```

You can also:

* `.one()` → expects exactly one
* `.project("name")` → partial selects
* `.as(MyDTO.class)` → projection
* `.all()` → list
* `.page(pageable)`

---

# **8. CRUD Operations Using Query by Example**

QBE primarily supports *READ operations*.
But you can integrate QBE into full CRUD workflows.

---

## **8.1 CREATE (regular save)**

```java
public Task create(Task t) {
    return taskRepository.save(t);
}
```

---

## **8.2 READ Using QBE**

### **8.2.1 Find tasks by partial name (case-insensitive)**

```java
Task probe = new Task();
probe.setName("bug");

ExampleMatcher matcher = ExampleMatcher.matching()
    .withMatcher("name", m -> m.contains().ignoreCase());

Example<Task> example = Example.of(probe, matcher);

List<Task> tasks = taskRepository.findAll(example);
```

---

### **8.2.2 Find tasks overdue and unassigned**

**Probe**

```java
Task probe = new Task();
probe.setAssignee(null);
probe.setStatus(TaskStatus.IN_PROGRESS);
```

**Matcher**

```java
ExampleMatcher matcher = ExampleMatcher.matching()
    .withIgnorePaths("id", "uuid")
    .withIgnoreNullValues()
    .withMatcher("status", m -> m.exact());
```

**Repository call**

```java
List<Task> tasks = taskRepository.findAll(Example.of(probe, matcher))
        .stream()
        .filter(t -> t.getDueDate().isBefore(LocalDate.now()))
        .toList();
```

---

## **8.3 UPDATE Using QBE (search → modify → save)**

```java
public int updateTasks(String keyword) {

    Task probe = new Task();
    probe.setDescription(keyword);

    ExampleMatcher matcher = ExampleMatcher.matching()
            .withMatcher("description", m -> m.contains().ignoreCase());

    List<Task> tasks = taskRepository.findAll(Example.of(probe, matcher));

    tasks.forEach(t -> t.setStatus(TaskStatus.COMPLETED));

    taskRepository.saveAll(tasks);

    return tasks.size();
}
```

---

## **8.4 DELETE Using QBE**

Since QBE does not support CriteriaDelete directly, we:

1. **Find all using QBE**
2. **Delete the list**

```java
public long deleteByAssigneeName(String name) {

    Task probe = new Task();
    probe.setAssignee(new Worker(name)); // OR configure matcher

    ExampleMatcher matcher = ExampleMatcher.matching()
            .withMatcher("assignee.firstName",
                m -> m.exact().ignoreCase());

    List<Task> tasks = taskRepository.findAll(Example.of(probe, matcher));

    long count = tasks.size();

    taskRepository.deleteAll(tasks);

    return count;
}
```

---

# **9. QBE, Pagination, and Sorting**

### **Find All + Pagination**

```java
Page<Task> page = taskRepository.findAll(
        Example.of(probe, matcher),
        PageRequest.of(0, 10, Sort.by("dueDate").ascending())
);
```

---

# **10. QBE vs Specifications vs Criteria API**

| Feature                  | QBE                              | Specifications          | Criteria API          |
| ------------------------ | -------------------------------- | ----------------------- | --------------------- |
| Query type               | Dynamic based on example         | Dynamic predicates      | Fully programmatic    |
| Complexity               | Very low                         | Medium                  | High                  |
| Logical AND/OR grouping  | ❌ Limited                        | ✔ Full support          | ✔ Full support        |
| Nested property matching | ❌ No                             | ✔ Yes                   | ✔ Yes                 |
| String matching          | ✔ startsWith, contains, endsWith | ✔ Full control          | ✔ Full control        |
| Supports delete/update   | ❌ No                             | ✔ Yes (CriteriaDelete)  | ✔ Yes                 |
| Best for                 | Simple queries                   | Complex dynamic queries | Advanced custom logic |

---

# **11. QBE Limitations**

1. No OR conditions
2. No nested logical groups
3. No collection matching
4. Only simple string matching
5. Only **exact matching** for non-string fields
6. Not suitable for large complex queries

---

# **12. Real-World Use Cases**

✔ Search/filter APIs
✔ Admin screens with many optional filters
✔ User-facing search forms
✔ Quick prototypes
✔ Auto-complete systems
✔ Refactoring-prone domain models

---

# **13. Summary**

Query By Example is:

* **Simple**
* **Powerful**
* **Declarative**
* **Flexible**
* Best for **simple, dynamic filter queries** with **minimal code**

It allows:

* Optional field matching
* Case-insensitive search
* Partial matching
* Pagination and sorting
* Flexible CRUD workflows

But avoid it for complex OR logic or heavy query requirements — then use **Specifications** or **Criteria API**.

---


