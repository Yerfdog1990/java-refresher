
---

# 📘 **Spring Data JPA: Building Queries with Specifications**

---

# 1. **Introduction**

Spring Data JPA provides multiple ways to define queries:

* Derived queries (`findByStatusAndDueDate`)
* JPQL (`@Query`)
* Native SQL
* Criteria API
* **Specifications** (the focus of this lesson)

Specifications were inspired by the Domain-Driven Design (DDD) concept of "Specification," which Eric Evans defines as:

1. A way to **describe conditions** for object creation
2. A way to **validate** if an object satisfies a condition
3. A way to **select** objects from a collection

Spring Data uses Specifications for #3 — **selecting database entities based on flexible and composable predicates**.

---

# 2. **Why Do We Need Specifications?**

Traditional approaches break down when:

### ❌ Too many dynamic filters

You would need dozens of methods:

```
findByStatus
findByStatusAndAssigneeId
findByStatusAndDueBefore
findByStatusAndAssigneeIdAndDueAfter
...
```

### ❌ Queries repeat criteria

Changing one condition means updating many queries.

### ❌ Limited flexibility

Query method parameters must be known at compile time.

### ❌ Complex queries lead to long method names

`findByStatusAndDueDateBetweenAndAssigneeIdIsNullOrderByPriorityDesc()`

---

# ✔ Specifications solve all of these.

* They allow **atomic, reusable predicates**
* They can be **combined dynamically** (AND, OR, NOT)
* They separate **criteria logic** from repository definitions
* They read like business rules
* They work with **pagination, sorting**, and **joins**

---

# 3. **Enabling Specifications**

To use Specifications, you must extend:

```java
public interface TaskRepository 
       extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
}
```

`JpaSpecificationExecutor` provides:

* `findOne(Specification spec)`
* `findAll(Specification spec)`
* `findAll(Specification spec, Pageable pageable)`
* `findAll(Specification spec, Sort sort)`
* `count(Specification spec)`
* `delete(Specification spec)` (JPA 2.1 CriteriaDelete support)

---

# 4. **Specification Interface Overview**

```java
@FunctionalInterface
public interface Specification<T> extends Serializable {

    Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb);
}
```

Where:

* **root** → entity
* **query** → top-level CriteriaQuery
* **cb** → helper to build predicates (`equal`, `lessThan`, `like`, etc.)

Since it's a functional interface, we can use lambda expressions.

---

# 5. **Example Entity (Task)**

```java
@Entity
public class Task {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Worker assignee;

    // getters and setters
}
```

---

# 6. **TaskSearchRequest – Custom Search / Filter DTO**

This class is **created by you** to hold optional search criteria.

**It is not provided by Spring.**

```java
public class TaskSearchRequest {

    private TaskStatus status;
    private LocalDate dueBefore;
    private LocalDate dueAfter;
    private Long assigneeId;
    private Long campaignId; // if needed
    private String keyword;

    public TaskSearchRequest() { }

    // Getters and setters
}
```

### ✔ Why this DTO?

* Bundles many optional fields
* Supports automatic binding from query parameters
* Easy to expand
* Clean method signatures
* Ideal for building dynamic specifications

Example URL:

```
/tasks/search?status=IN_PROGRESS&assigneeId=3&keyword=bug
```

---

# 7. **Specification Builder Class**

Put all predicate-building logic in one class.

```java
public class TaskSpecifications {

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<Task> dueBefore(LocalDate date) {
        return (root, query, cb) ->
                cb.lessThan(root.get("dueDate"), date);
    }

    public static Specification<Task> dueAfter(LocalDate date) {
        return (root, query, cb) ->
                cb.greaterThan(root.get("dueDate"), date);
    }

    public static Specification<Task> assignedTo(Long workerId) {
        return (root, query, cb) ->
                cb.equal(root.get("assignee").get("id"), workerId);
    }

    public static Specification<Task> nameContains(String keyword) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
    }
}
```

---

# 8. **Building Dynamic Specifications From TaskSearchRequest**

```java
@Service
public class TaskService {

    @Autowired
    private TaskRepository repo;

    public List<Task> search(TaskSearchRequest request) {

        Specification<Task> spec = Specification.where(null);

        if (request.getStatus() != null)
            spec = spec.and(TaskSpecifications.hasStatus(request.getStatus()));

        if (request.getDueBefore() != null)
            spec = spec.and(TaskSpecifications.dueBefore(request.getDueBefore()));

        if (request.getDueAfter() != null)
            spec = spec.and(TaskSpecifications.dueAfter(request.getDueAfter()));

        if (request.getAssigneeId() != null)
            spec = spec.and(TaskSpecifications.assignedTo(request.getAssigneeId()));

        if (request.getKeyword() != null)
            spec = spec.and(TaskSpecifications.nameContains(request.getKeyword()));

        return repo.findAll(spec);
    }
}
```

---

# 9. **Controller Layer Example**

```java
@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService service;

    @GetMapping("/search")
    public List<Task> search(TaskSearchRequest request) {
        return service.search(request);
    }
}
```

Spring will automatically map query parameters to the fields in `TaskSearchRequest`.

---

# 10. **Combining Specifications**

Spring provides:

* `where()`
* `and()`
* `or()`
* `not()`
* `allOf()`
* `anyOf()`

Example:

```java
Specification<Task> spec =
        where(hasStatus(TaskStatus.IN_PROGRESS))
        .and(dueBefore(LocalDate.now()))
        .or(assignedTo(5L));
```

---

# 11. **Advanced: Delete Using Specifications**

JPA 2.1 added `CriteriaDelete`, used by Spring:

```java
Specification<Task> oldCancelled =
        hasStatus(TaskStatus.CANCELLED)
       .and(dueBefore(LocalDate.of(2024, 1, 1)));

repo.delete(oldCancelled);
```

Spring will generate:

```sql
DELETE FROM task WHERE status='CANCELLED' AND dueDate < '2024-01-01'
```

---

# 12. **CRUD Operations Using Specifications**

## ✔ Create (does not use specs)

```java
public Task create(Task task) {
    return repo.save(task);
}
```

## ✔ Read (using specifications)

```java
public List<Task> getOverdue(Long workerId) {
    return repo.findAll(
        assignedTo(workerId)
          .and(dueBefore(LocalDate.now()))
    );
}
```

## ✔ Update (retrieve by spec → modify → save)

```java
public int completeOverdueTasks(Long workerId) {
    Specification<Task> spec =
            assignedTo(workerId)
            .and(dueBefore(LocalDate.now()));

    List<Task> tasks = repo.findAll(spec);

    tasks.forEach(t -> t.setStatus(TaskStatus.COMPLETED));

    repo.saveAll(tasks);
    return tasks.size();
}
```

## ✔ Delete (via specification)

```java
repo.delete(hasStatus(TaskStatus.CANCELLED));
```

---

# 13. **Pagination + Specifications**

```java
public Page<Task> search(TaskSearchRequest request, Pageable pageable) {
    Specification<Task> spec = buildSpecFrom(request);
    return repo.findAll(spec, pageable);
}
```

---

# 14. **Sorting + Specifications**

```java
Sort sort = Sort.by("dueDate").descending();
repo.findAll(spec, sort);
```

---

# 15. **Specifications vs Criteria API**

Under the hood, Specifications are built on top of JPA’s Criteria API:

* They translate to `CriteriaBuilder` calls
* They are more readable
* They combine better
* They avoid boilerplate like:

```java
CriteriaBuilder cb = entityManager.getCriteriaBuilder();
CriteriaQuery<Task> q = cb.createQuery(Task.class);
Root<Task> root = q.from(Task.class);
...
```

Specifications hide all that.

---

# 16. **Full Example: Dynamic Search With Multiple Optional Filters**

Request:

```
/tasks/search?status=IN_PROGRESS&keyword=bug&dueBefore=2025-06-01
```

Will execute a query similar to:

```sql
SELECT * FROM task
WHERE status = 'IN_PROGRESS'
  AND lower(name) LIKE '%bug%'
  AND due_date < '2025-06-01'
```

---

# 17. **When NOT To Use Specifications**

Use simple queries when:

* Only one filter exists
* No dynamic logic needed
* The query is static and trivial
* You need database-specific features (Specifications = Criteria API = JPQL-based)

---

# 18. **Final Summary**

Spring Data Specifications provide:

| Feature                     | Supported |
| --------------------------- | --------- |
| Dynamic predicates          | ✔         |
| Optional filters            | ✔         |
| AND/OR/NOT combinations     | ✔         |
| Pagination                  | ✔         |
| Sorting                     | ✔         |
| Delete by Specification     | ✔         |
| CRUD support                | ✔         |
| Type-safe, reusable filters | ✔         |

Specifications are best for:

* Search screens
* Dashboards with filters
* Complex, dynamic data retrieval requirements
* Combine rules at runtime

---

