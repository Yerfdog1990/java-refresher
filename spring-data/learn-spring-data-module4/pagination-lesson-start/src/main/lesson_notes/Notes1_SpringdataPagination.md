
---

# **Spring Data Pagination — Detailed Lesson Notes**

## **1. Introduction**

When working with large datasets in Spring applications, it’s rarely efficient—or even safe—to retrieve everything at once. Spring Data JPA provides first-class support for **pagination**, **slicing**, **sorting**, and **limiting** query results.

In this lesson, we explore:

* Pagination fundamentals
* The `Page` and `Slice` interfaces
* Using derived queries with pagination
* Custom JPQL queries with pagination
* Sorting and limiting results
* When to use `Page`, `Slice`, or other return types
* Practical Java examples

Before running any example, ensure SQL logging is enabled so you can see generated queries:

```properties
spring.jpa.show-sql=true
```

---

# **2. Pagination Core Concepts**

Spring Data pagination centers around three core building blocks:

### **2.1 `PageRequest` (implements `Pageable`)**

Used to specify:

* page number (0-based)
* page size
* sort operations (optional)

Example:

```java
Pageable pageable = PageRequest.of(0, 5); // page 0, size 5
```

---

# **3. The `Page` Interface**

The most informative pagination abstraction.

### **3.1 What `Page<T>` Provides**

`Page` extends `Slice`, but adds **total count information**:

```java
public interface Page<T> extends Slice<T> {
    int getTotalPages();
    long getTotalElements();
}
```

A `Page<T>` instance contains:

* current page content
* number of elements in current page
* total pages
* total elements (via extra `COUNT(*)` query)
* navigation helpers: `hasNext()`, `hasPrevious()`

### **3.2 Repository Example Using Page**

```java
public interface TaskRepository extends PagingAndSortingRepository<Task, Long> {
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);
}
```

### **3.3 Using `Page` in Your Application**

```java
Pageable twoTaskPage = PageRequest.of(0, 2);

Page<Task> page = taskRepository.findByStatus(TaskStatus.TO_DO, twoTaskPage);

LOG.info("Tasks Returned:\n {}", page.getContent());
LOG.info("Total Tasks: {}", page.getTotalElements());
LOG.info("Total Pages: {}", page.getTotalPages());
```

### **3.4 SQL Generated**

Spring executes:

```sql
select count(t1_0.id) from task t1_0 where t1_0.status=?
```

This **extra COUNT query** is the key difference between `Page` and `Slice`.

---

# **4. The `Slice` Interface**

`Slice<T>` is a lighter, more efficient pagination abstraction:

```java
public interface Slice<T> {
    boolean hasNext();
    boolean hasPrevious();
    Pageable nextPageable();
    Pageable previousPageable();
}
```

A `Slice` **does NOT**:

* know the total number of elements
* execute a `COUNT(*)` query

Instead, it retrieves **pageSize + 1** rows to know whether there is a next slice.

### **4.1 Repository Example with Slice**

```java
public interface TaskRepository extends PagingAndSortingRepository<Task, Long> {
    Slice<Task> findByNameLike(String name, Pageable pageable);
}
```

### **4.2 Using Slice in Code**

```java
Pageable pageRequest = PageRequest.of(0, 2);

Slice<Task> slice = taskRepository.findByNameLike("Task%", pageRequest);

LOG.info("Slice Content:\n {}", slice.getContent());
LOG.info("Has Next Slice: {}", slice.hasNext());
LOG.info("Has Previous Slice: {}", slice.hasPrevious());

Slice<Task> nextSlice = taskRepository.findByNameLike("Task%", slice.nextPageable());
LOG.info("Next Slice:\n {}", nextSlice.getContent());
```

### **4.3 SQL Generated**

```sql
select t1_0.id, ... 
from task t1_0 
where t1_0.name like ? escape '\' offset ? rows fetch first ? rows only
```

No COUNT query is issued.

---

# **5. Pagination With Custom JPQL Queries**

Spring Data automatically appends pagination clauses (`offset…fetch`) to JPQL queries.

### **5.1 Repository Example**

```java
@Query("select t from Task t where t.name like ?1")
Page<Task> allTasksByName(String name, Pageable pageable);
```

### **5.2 Using It**

```java
Pageable pageTwo = PageRequest.of(1, 2);

Page<Task> page = taskRepository.allTasksByName("Task%", pageTwo);

LOG.info("Page Two Results:\n {}", page.getContent());
```

### **5.3 SQL Generated**

```sql
select t1_0.id, ...
from task t1_0 
where t1_0.name like ? escape ''
offset ? rows fetch first ? rows only
```

---

# **6. Pagination With Native SQL Queries**

Native queries do **NOT** support Spring Data automatic pagination modification.

You must manually include:

* `offset :offset`
* `fetch first :limit rows only`

Example:

```java
@Query(value = """
    SELECT * FROM task 
    WHERE name like :name 
    OFFSET :offset ROWS FETCH FIRST :limit ROWS ONLY
""", nativeQuery = true)
List<Task> allTasksByNameNative(
    @Param("name") String name,
    @Param("offset") int offset,
    @Param("limit") int limit);
```

---

# **7. Sorting**

Sorting can be applied alone or combined with pagination.

## **7.1 Basic Sorting**

```java
Sort sort = Sort.by("firstname").ascending()
    .and(Sort.by("lastname").descending());
```

## **7.2 Type-Safe Sorting**

```java
TypedSort<Person> person = Sort.sort(Person.class);

Sort sort = person.by(Person::getFirstname).ascending()
    .and(person.by(Person::getLastname).descending());
```

---

# **8. Limiting Query Results**

Spring Data supports keywords:

* `Top`
* `First`

```java
User findFirstByOrderByLastnameAsc();
User findTopByLastnameOrderByAgeDesc(String lastname);
List<User> findTop10ByLastname(String lastname);
```

Or using `Limit`:

```java
List<User> findByLastname(String lastname, Limit limit);
```

Example:

```java
List<User> users = repo.findByLastname("Smith", Limit.of(3));
```

---

# **9. Choosing Between Page, Slice, List, Stream**

| Return Type | Fetch Size             | Pros                      | Cons              |
| ----------- | ---------------------- | ------------------------- | ----------------- |
| `List<T>`   | all results            | easy                      | memory-heavy      |
| `Page<T>`   | pageSize + total count | UI-friendly               | extra COUNT query |
| `Slice<T>`  | pageSize + 1           | lightweight               | no total count    |
| `Stream<T>` | streamed               | low memory                | must close stream |
| `Window<T>` | efficient scrolling    | better than offset paging | more complex      |

### **Guidelines**

* Use **Page** → when building UI with page numbers
* Use **Slice** → infinite scroll, newsfeeds
* Use **List** → small datasets
* Use **Stream** → extremely large datasets
* Use **Window** → high-performance cursor-based pagination

---

# **10. Full Working Java Example**

### **Entity**

```java
@Entity
public class Task {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    // getters and setters
}
```

### **Repository**

```java
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Slice<Task> findByNameLike(String name, Pageable pageable);

    @Query("select t from Task t where t.name like ?1")
    Page<Task> allTasksByName(String name, Pageable pageable);
}
```

### **Runner Example**

```java
@Component
public class AppRunner implements CommandLineRunner {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public void run(String... args) {

        Pageable firstTwo = PageRequest.of(0, 2);

        Page<Task> page = taskRepository.findByStatus(TaskStatus.TO_DO, firstTwo);
        System.out.println("Page Content: " + page.getContent());
        System.out.println("Total Elements: " + page.getTotalElements());

        Slice<Task> slice = taskRepository.findByNameLike("Task%", firstTwo);
        System.out.println("Slice Content: " + slice.getContent());
        System.out.println("Has Next Slice: " + slice.hasNext());

        Page<Task> customPage = taskRepository.allTasksByName("Task%", firstTwo);
        System.out.println("Custom Query Page: " + customPage.getContent());
    }
}
```

---

# **11. Summary**

| Concept                    | Best Use                                                      |
| -------------------------- | ------------------------------------------------------------- |
| **Page**                   | UIs needing page numbers, total items                         |
| **Slice**                  | Infinite scroll / feeds                                       |
| **PageRequest**            | Create pagination + sorting                                   |
| **Sort**                   | Ordering results                                              |
| **Limit**                  | Fetch only top K results                                      |
| **Custom JPQL + Pageable** | Spring handles pagination                                     |
| **Native SQL + Pageable**  | Spring does *not* modify queries → manual pagination required |

Spring Data’s pagination API is powerful, flexible, and designed for high-performance data access with minimal effort.

---


