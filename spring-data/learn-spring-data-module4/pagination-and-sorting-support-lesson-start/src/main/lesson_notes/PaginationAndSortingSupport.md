
---

# ⭐ **Lesson 1: Pagination and Sorting Support**

### *Spring Data JPA — Module on Data Access Patterns*

---

# 1. **Introduction**

In earlier lessons, we focused on basic repository queries—returning full lists of records.
However, returning *all results* at once is rarely a good idea:

* Large datasets → slow rendering
* Increased memory and bandwidth usage
* Poor UX (users scroll endlessly)
* No control over ordering

**Pagination** solves this by dividing data into small chunks (“pages”), while **sorting** allows ordering results based on one or more entity fields.

Together, these features provide:

- ✔ A smoother UI experience
- ✔ Better performance
- ✔ Cleaner, more controlled data retrieval
- ✔ Less database load

---

# 2. **Spring Data’s Out-of-the-Box Support**

Spring Data provides paging and sorting capabilities through:

## **`PagingAndSortingRepository`**

```java
public interface PagingAndSortingRepository<T, ID> extends Repository<T, ID> {

    Iterable<T> findAll(Sort sort);

    Page<T> findAll(Pageable pageable);
}
```

### **Method 1 — `findAll(Sort sort)`**

* Sorts all entities using one or more fields.
* Returns `Iterable<T>`.

### **Method 2 — `findAll(Pageable pageable)`**

* Returns a **Page<T>**, containing:

    * current page content
    * total elements
    * total pages
    * page number, size, etc.

---

# 📌 **Important Note**

You **do not need** to extend `PagingAndSortingRepository` to use paging/sorting.

Spring Data will apply pagination/sorting to **any repository method** that receives:

* a `Sort` object, or
* a `Pageable` object

Example:

```java
List<Product> findAllByPrice(double price, Pageable pageable);
```

---

# 3. **Pagination Example — Task Entity**

### **Repository**

```java
public interface TaskRepository 
        extends CrudRepository<Task, Long>, 
                PagingAndSortingRepository<Task, Long> {
}
```

### **Data Preloaded in the DB**

```sql
INSERT INTO Task(id, uuid, name, due_date, description, campaign_id, status) 
VALUES (1, uuid(), 'Task 1', '2025-01-12', 'Task 1 Description', 1, 0);

INSERT INTO Task(id, uuid, name, due_date, description, campaign_id, status) 
VALUES (2, uuid(), 'Task 2', '2025-02-10', 'Task 2 Description', 1, 0);

INSERT INTO Task(id, uuid(), name, due_date, description, campaign_id, status) 
VALUES (3, uuid(), 'Task 3', '2025-03-16', 'Task 3 Description', 1, 0);

INSERT INTO Task(id, uuid, name, due_date, description, campaign_id, status, assignee_id) 
VALUES (4, uuid(), 'Task 4', '2025-06-25', 'Task 4 Description', 2, 0, 1);
```

---

# 4. **Paginating — Getting 2 Tasks at a Time**

## **Step 1 — Create Pageable**

```java
Pageable firstPage = PageRequest.of(0, 2);  // page index 0, size 2
```

## **Step 2 — Use `findAll(Pageable)`**

```java
Page<Task> tasksPage1 = taskRepository.findAll(firstPage);

LOG.info("Page 1 of All Tasks:");
tasksPage1.forEach(task -> LOG.info(task.toString()));
```

### **Result**

```
Page 1 results = Task 1, Task 2
```

---

## **Next Page**

```java
Pageable secondPage = PageRequest.of(1, 2);

Page<Task> tasksPage2 = taskRepository.findAll(secondPage);
```

### **Result**

```
Page 2 results = Task 3, Task 4
```

---

# 5. **Sorting All Entities**

### **Descending by name**

```java
Sort sortByNameDesc = Sort.by(Direction.DESC, "name");

Iterable<Task> tasksSorted = taskRepository.findAll(sortByNameDesc);
```

### **Sample output**

```
Task 4, Task 3, Task 2, Task 1
```

---

# 6. **Pagination + Sorting Together**

Most real-world queries require *both*.

### **Example — 1st page of 2 tasks, sorted by name desc**

```java
Pageable pageSorted = PageRequest.of(
        0,               // page index
        2,               // size
        Sort.by("name").descending()
);

Page<Task> results = taskRepository.findAll(pageSorted);
```

### **Result**

```
Task 4, Task 3
```

---

# 7. **Conceptual Diagram (Text-Based)**

```
                ┌──────────────────────────────────┐
                │          Repository Layer        │
                └──────────────────────────────────┘
                          │ receives
                          ▼
                ┌──────────────────────────────────┐
                │    Pageable / Sort Parameters    │
                └──────────────────────────────────┘
                           │ builds SQL
                           ▼
     ┌────────────────────────────────────────────────────────────┐
     │ Generated SQL Example:                                     │
     │ SELECT * FROM task                                         │
     │ ORDER BY name DESC                                         │
     │ LIMIT 2 OFFSET 0                                           │
     └────────────────────────────────────────────────────────────┘
                           │ returns
                           ▼
                ┌──────────────────────────────────┐
                │    Page<T> or Iterable<T>        │
                └──────────────────────────────────┘
```

---

# 8. **Pagination Example with Product Entity**

### **Product entity**

```java
@Entity
public class Product {
    @Id
    private long id;
    private String name;
    private double price;
}
```

### **Repository**

```java
public interface ProductRepository 
        extends PagingAndSortingRepository<Product, Integer> {

    List<Product> findAllByPrice(double price, Pageable pageable);
}
```

---

# 9. **Pagination — Example**

```java
Pageable page = PageRequest.of(0, 3);
Page<Product> products = productRepository.findAll(page);
```

---

# 10. **Sorting + Pagination**

```java
Pageable sortedByPriceDesc = PageRequest.of(
        0, 
        5, 
        Sort.by("price").descending()
);

Page<Product> result = productRepository.findAll(sortedByPriceDesc);
```

---

# 11. **Page vs Slice vs List**

| Return Type  | Includes data? | Knows total count? | Extra query?  | Use Case             |
| ------------ | -------------- | ------------------ | ------------- | -------------------- |
| **Page<T>**  | ✔              | ✔                  | ✔ count query | UI with page numbers |
| **Slice<T>** | ✔              | ✘                  | ✘             | Infinite scroll      |
| **List<T>**  | ✔              | ✘                  | ✘             | Small datasets       |

---

# 12. **Spring Boot 2 vs Spring Data 3 Notes**

### **Boot 2**

`PagingAndSortingRepository` automatically extends `CrudRepository`.

### **Spring Data 3**

You choose the base interface:

* `CrudRepository`
* `ListCrudRepository`
* `PagingAndSortingRepository`
* `JpaRepository`

This gives greater control over what operations your repo exposes.

---

# 13. **Conclusion**

In this lesson, you learned:

- ✔ How to paginate results using `Pageable`
- ✔ How to sort results using `Sort`
- ✔ How to combine pagination + sorting
- ✔ How `Page`, `Slice`, and `List` differ
- ✔ How to use these features in custom queries
- ✔ How Spring Boot 2 and Spring Data 3 differ

Pagination and sorting are foundational tools that prepare you for more advanced query customizations coming in later lessons.

---


