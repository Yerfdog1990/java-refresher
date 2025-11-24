
---

# **Spring Data Sorting**

**Sorting query results with Spring Data JPA**

---

# **1. Introduction**

Sorting is one of the most essential operations when retrieving data. Users rarely want raw, unordered lists—sorted results improve readability, navigation, and user experience.

Spring Data JPA supports sorting in **two major ways**:

1. **Derived query methods that embed sorting rules inside the method name**
2. **Using the `Sort` parameter**, which allows dynamic and multi-field sorting

In this lesson, you’ll learn:

* How Spring Data applies database-level `ORDER BY` automatically
* How sorting works with **derived queries**, **Sort parameters**, **TypedSort**, and **nested properties**
* How to combine sorting with filtering
* How to specify **multiple sorting criteria**
* How to fall back to **Sort.unsorted()**
* Best practices and pitfalls

---

# **2. How Spring Data Sorting Works (Conceptual)**

Spring Data does **not** sort entities in memory. Instead:

### ✔ It appends `ORDER BY` to the SQL or JPQL.

### ✔ The database engine performs the sort (fast + optimized).

### ✔ Spring ensures the properties you specify in sorting exist on the domain model.

Sorting types:

* **Ascending (default)** → `ORDER BY column ASC`
* **Descending** → `ORDER BY column DESC`

Sorting behavior:

* **NULL handling** depends on the database (nulls first or last).
* **Multiple sorting criteria** are appended in order.

---

# **3. The Repository Used in This Lesson**

```java
public interface TaskRepository extends PagingAndSortingRepository<Task, Long> {

    // We will add custom methods here
}
```

> Even though we extend `PagingAndSortingRepository`, *all sort features also work with JpaRepository*.

---

# **4. Sorting With Derived Query Methods**

Spring Data allows sorting by embedding keywords into method names.

### **4.1. Basic syntax**

```java
List<Task> findAllByOrderByDueDateAsc();
List<Task> findAllByOrderByDueDateDesc();
```

### **Example: Sort tasks by due date descending**

```java
public interface TaskRepository extends PagingAndSortingRepository<Task, Long> {

    List<Task> findAllByOrderByDueDateDesc();
}
```

### **Calling the method**

```java
public void run(ApplicationArguments args) {
    List<Task> tasks = taskRepository.findAllByOrderByDueDateDesc();
    tasks.forEach(System.out::println);
}
```

✔ Output will show tasks ordered by latest due date first.

---

# **5. Derived Query Sorting With Multiple Fields**

Sorting becomes more powerful when using *multiple* fields.

### **Example: Sort by dueDate DESC, then assignee.lastName ASC**

```java
List<Task> findAllByOrderByDueDateDescAssigneeLastNameAsc();
```

⚠ **Important rule**
When using multiple sort fields, each field **must** specify direction (`Asc` or `Desc`).

Otherwise Spring cannot parse it and throws an error.

### **Usage**

```java
List<Task> tasks = taskRepository
    .findAllByOrderByDueDateDescAssigneeLastNameAsc();
```

---

# **6. Sorting With a `Sort` Parameter (Dynamic Sorting)**

Derived queries are static.
Sometimes you need “runtime sorting” based on user inputs, API params, or UI options.

### **6.1. Add a Sort parameter**

```java
List<Task> findByNameContaining(String keyword, Sort sort);
```

### **Creating a Sort instance**

```java
Sort sort = Sort.by(Direction.DESC, "dueDate")
                .and(Sort.by(Direction.ASC, "assignee.lastName"));
```

### **Use the Sort in a query**

```java
List<Task> tasks =
    taskRepository.findByNameContaining("Task", sort);
```

✔ This produces SQL like:

```sql
ORDER BY due_date DESC, assignee.last_name ASC
```

---

# **7. Using TypedSort (Type-Safe Sorting)**

To avoid mistakes from using raw strings like `"assignee.lastName"`, Spring Data offers:

### **TypedSort (method reference–based sorting)**

```java
TypedSort<Task> typed = Sort.sort(Task.class);

Sort sortByDueDate =
    typed.by(Task::getDueDate).descending();

Sort sortByAssigneeLastName =
    typed.by(t -> t.getAssignee().getLastName()).ascending();

Sort finalSort = sortByDueDate.and(sortByAssigneeLastName);
```

### **Call repository**

```java
List<Task> tasks = taskRepository.findByNameContaining("Task", finalSort);
```

✔ This is type-safe
✔ No risk of typos
✔ No magic strings

---

# **8. Sorting + Paging Together**

Sorting integrates directly with Pageable.

**Examples:**

### Sorted by name

```java
Pageable p = PageRequest.of(0, 10, Sort.by("name"));
```

### Sorted by price DESC then name ASC

```java
Pageable p = PageRequest.of(
    0, 10,
    Sort.by("price").descending()
        .and(Sort.by("name").ascending())
);
```

### Call repository

```java
Page<Task> tasks = taskRepository.findAll(p);
```

---

# **9. Passing No Sort (Sort.unsorted)**

If a method requires `Sort`, but you want no order:

```java
Sort noSort = Sort.unsorted();
List<Task> tasks = taskRepository.findByNameContaining("Task", noSort);
```

✔ Equivalent to no ORDER BY clause.

---

# **10. Best Practices & Common Pitfalls**

### ✔ Use Derived Sorting for:

* Simple sorting with static rules
* Two or three ordered fields

### ✔ Use Sort parameters for:

* REST APIs
* End-user configurable sorting
* Multi-field dynamic sorting

### ✔ Use TypedSort for:

* Complex nested properties
* Avoiding string-based field names

### ⚠ Avoid:

* Overly long derived method names (too many sort fields)
* Mixing `OrderBy` inside method name + `Sort` parameter (OrderBy wins)
* Typos in property names when using strings

---

# **11. Mini-Diagram: Sorting Mechanisms Overview**

```
                SPRING DATA SORTING
------------------------------------------------------
1. Derived Query Method Sorting
   - findAllByOrderByDueDateDesc()
   - findAllByOrderByNameAscStatusDesc()

2. Sort Parameter (Dynamic)
   - Sort.by("dueDate").descending()

3. TypedSort (Type-safe)
   - Sort.sort(Task.class).by(Task::getDueDate)
```

---

# **12. Summary**

In this lesson you learned:

* How Spring Data sorts results using database ORDER BY
* How to define sorting in derived query methods
* How to sort with the `Sort` parameter
* How to combine sorting with filtering
* How to use TypedSort for safe, IDE-friendly sorting
* How to use multiple sorting criteria and nested properties
* How to use Sort.unsorted()

Sorting is a core tool in building scalable, user-friendly systems—Spring Data makes it both powerful and clean.

---


