
---

# **📘 Lesson Notes: Sorting With `@Query` in Spring Data JPA**

Sorting is a key requirement when retrieving data from the database. While derived queries and `Sort` parameters are commonly used, the `@Query` annotation gives us *full control* over the SQL/JPQL executed by Spring Data JPA.

In this lesson, we explore **how sorting works inside explicit `@Query` methods**, including:

- ✔ Sorting via JPQL `ORDER BY`
- ✔ Dynamic sorting using `Sort` parameters
- ✔ Mixing both (static + dynamic)
- ✔ Sorting in native SQL queries
- ✔ Limitations and rule interactions
- ✔ Complete working code examples

---

# **1. Why Use `@Query` for Sorting?**

`@Query` is useful when:

* Your sort logic requires **complex joins**.
* You want to control exactly how the query is written.
* You prefer writing explicit JPQL/SQL instead of using method names.
* You need dynamic sorting *when JPQL is static*.

Spring Data JPA fully supports sorting inside `@Query`, with a few important rules we will uncover later.

---

# **2. Sorting Using JPQL with ORDER BY**

The simplest form is adding `ORDER BY` directly inside the JPQL query.

### **📌 Example: Sort Tasks by Due Date DESC**

**Repository:**

```java
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("select t from Task t order by t.dueDate desc")
    List<Task> allTasksSortedByDueDate();
}
```

**Key Points**

* The sort column must match a **field name** in the entity.
* JPQL allows nested sorting like `t.assignee.lastName`.

### **Using It in Code**

```java
List<Task> tasks = taskRepository.allTasksSortedByDueDate();

tasks.forEach(t -> LOG.info("{}", t));
```

---

# **3. Sorting Dynamically Using a Sort Parameter**

When we need the sorting rules to be **runtime dynamic**, we can pass in a `Sort` object.

### **📌 Example: Add Sort Parameter to JPQL**

```java
@Query("select t from Task t")
List<Task> allTasks(Sort sort);
```

Spring Data detects the `Sort` argument, and *automatically appends* the appropriate `ORDER BY` clause.

### **Usage Example**

```java
Sort sort = Sort.by(Direction.DESC, "dueDate");

List<Task> tasks = taskRepository.allTasks(sort);
```

This produces SQL like:

```
order by t.due_date desc
```

**Note:**
The Sort parameter applies **even though** the JPQL does not include ORDER BY.

---

# **4. Mixing Static ORDER BY + Dynamic Sort Parameter**

This is where developers get confused — but the rule is simple:

## **🟦 RULE:**

### The static JPQL `ORDER BY` is always applied first.

### The Sort parameter adds **secondary ordering**, appended after JPQL.

---

### **📌 Example: JPQL + Sort Together**

```java
@Query("select t from Task t order by t.dueDate desc")
List<Task> allTasksSortedByDueDate(Sort sort);
```

Usage:

```java
Sort sortByLastName = Sort.by(Direction.ASC, "assignee.lastName");

List<Task> tasks = taskRepository.allTasksSortedByDueDate(sortByLastName);
```

### **Resulting SQL**

```
order by task0_.due_date desc, worker1_.last_name asc
```

**Important behaviors:**

* JPQL always takes precedence
* Sort parameter does **not override** JPQL
* Sort parameter only *extends* the ORDER BY chain
* For nested sorting, Spring performs the required **JOIN**

---

# **5. Sorting With Native Queries**

Native SQL queries also support sorting, but with a major limitation.

### **📌 Rule for Native Queries**

- ✔ You **can** sort using ORDER BY
- ❌ You **cannot** apply dynamic `Sort` parameters
- ❌ Mixing `Sort` + `nativeQuery = true` = ❗ Exception

---

### **Example: Native Query Sorting**

```java
@Query(value = "select * from task t order by t.due_date desc",
       nativeQuery = true)
List<Task> allTasksSortedByDueDateDesc();
```

Usage:

```java
List<Task> tasks = taskRepository.allTasksSortedByDueDateDesc();
```

Spring Data will:

* Execute your native SQL
* Run additional JPQL queries to fetch associations (because of EAGER fetch)

---

# **6. Best Practices When Sorting With @Query**

### ✔ Prefer JPQL + Sort parameter

Gives flexibility + readability.

### ✔ Avoid mixing ORDER BY + Sort unless you know precedence rules.

### ✔ Do NOT try to add dynamic sort to native queries.

### ✔ Use nested properties carefully

`assignee.lastName` requires joins.

### ✔ Avoid using column names in JPQL

Always use entity field names (column names only for native SQL).

---

# **7. Complete Example Repository**

```java
public interface TaskRepository extends JpaRepository<Task, Long> {

    // 1. Static JPQL Sorting
    @Query("select t from Task t order by t.dueDate desc")
    List<Task> allTasksSortedByDueDate();

    // 2. Dynamic Sort using Sort parameter
    @Query("select t from Task t")
    List<Task> allTasks(Sort sort);

    // 3. JPQL ORDER BY + Sort mixed
    @Query("select t from Task t order by t.dueDate desc")
    List<Task> allTasksSortedByDueDate(Sort sort);

    // 4. Native SQL with static ORDER BY
    @Query(value = "select * from task t order by t.due_date desc",
           nativeQuery = true)
    List<Task> allTasksSortedByDueDateDesc();
}
```

---

# **8. Summary Table**

| Approach             | Supports Dynamic Sort? | Supports Nested Sort?  | Works in Native Queries? | Notes                    |
| -------------------- | ---------------------- | ---------------------- | ------------------------ | ------------------------ |
| JPQL ORDER BY        | ❌ No                   | ✔ Yes                  | ❌ No                     | Good for fixed ordering  |
| JPQL + Sort          | ✔ Yes                  | ✔ Yes                  | ❌ No                     | Best for dynamic sorting |
| JPQL ORDER BY + Sort | ✔ Yes (as secondary)   | ✔ Yes                  | ❌ No                     | JPQL sort always first   |
| Native SQL ORDER BY  | ❌ No                   | ✔ But must write joins | ✔ Yes                    | Must hardcode ORDER BY   |

---

# **9. Exercise**

### **1️⃣ Write a JPQL query that sorts by campaign.code ASC and dueDate DESC**

```java
@Query("select t from Task t order by t.campaign.code asc, t.dueDate desc")
List<Task> customSort1();
```

### **2️⃣ Write a Sort parameter version for dynamic multi-level sorting**

```java
Sort sort = Sort.by("campaign.code").ascending()
                .and(Sort.by("dueDate").descending());
```

### **3️⃣ Try mixing static + dynamic sorting and predict the SQL**

> JPQL ORDER BY → first
> Sort parameter → appended

---

