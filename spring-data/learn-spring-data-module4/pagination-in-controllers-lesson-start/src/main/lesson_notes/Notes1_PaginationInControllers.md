
---

# 📘 **Lesson Notes — Spring Data: Using Pagination in Controllers**

Pagination becomes essential when exposing REST APIs that return potentially large lists of records. Loading all entities at once is inefficient, slow, and not user-friendly. Spring Web + Spring Data JPA make pagination seamless using the **Page**, **Pageable**, and **Sort** abstractions.

This lesson explains:

* Page as resource vs representation
* Controller-level pagination
* Manual vs automatic Pageable resolution
* Sorting + pagination
* Global pagination configuration
* URL design best practices
* Understanding Page metadata
* Avoiding recursion with Jackson annotations

---

# **1. Page as a Resource or Representation**

In REST design, there are two possible interpretations:

### **1. Treating a Page as a Resource (❌ Not Recommended)**

Example URI:

```
/tasks/page/1
```

Problems:

* A page is *not* a domain entity.
* A page cannot be uniquely identified across time.
* Pages are ephemeral representations generated on demand.
* Harder to manipulate individual items inside a page.

### **2. Treating a Page as a Representation (✔ Recommended)**

Use standard filtering on the resource URI:

```
/tasks?page=1&size=20
```

Benefits:

* Clean, resource-oriented URIs
* Flexible (e.g., pagination, filtering, sorting)
* Matches Spring conventions
* Easy browser + client usability

➡ **Conclusion:**
**Always put pagination parameters in the query string**, not the URI path.

---

# **2. Initial Setup**

### **The Repository**

```java
public interface TaskRepository extends PagingAndSortingRepository<Task, Long> {

    Page<Task> findByCampaignId(long id, Pageable pageable);
}
```

This provides:

* Pagination support
* Sorting support
* A custom paginated method: `findByCampaignId`

---

# **3. Base Controller Before Pagination**

```java
@RestController
@RequestMapping("/campaigns/{id}/tasks")
public class CampaignTaskController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping
    public Page<Task> list(@PathVariable("id") long id) {
        Pageable pageable = Pageable.unpaged();
        return taskRepository.findByCampaignId(id, pageable);
    }
}
```

Currently:

* Always returns a full list (one page).
* No pagination criteria applied.

---

# **4. Pagination With Manual Mapping of Query Parameters**

### **Step 1 — Add `page` and `size` parameters**

```java
@GetMapping
public Page<Task> list(
        @PathVariable("id") long id,
        @RequestParam(name = "page", required = true, defaultValue = "0") int page,
        @RequestParam(name = "size", required = true, defaultValue = "2") int size) {

    Pageable pageable = PageRequest.of(page, size);
    return taskRepository.findByCampaignId(id, pageable);
}
```

### Example Request

```
GET /campaigns/1/tasks?page=0&size=2
```

Response includes metadata:

* `totalPages`
* `totalElements`
* `pageNumber`
* `pageSize`
* `sort`

### Example JSON (simplified)

```json
{
  "content": [ { "id": 1, "name": "Task 1" }, { "id": 2, "name": "Task 2" } ],
  "totalElements": 9,
  "totalPages": 5,
  "size": 2,
  "number": 0
}
```

---

# **5. Automatic Pageable Mapping (Recommended)**

Spring MVC can automatically map pagination params into a `Pageable` object.

### Replace manual params using:

```java
@GetMapping
public Page<Task> list(
        @PathVariable("id") long id,
        Pageable pageable) {

    return taskRepository.findByCampaignId(id, pageable);
}
```

Now Spring resolves:

```
?page=1&size=2&sort=name,desc
```

into a `Pageable` object.

### Adding default pagination:

```java
@GetMapping
public Page<Task> list(
        @PathVariable("id") long id,
        @PageableDefault(page = 0, size = 2) Pageable pageable) {

    return taskRepository.findByCampaignId(id, pageable);
}
```

---

# **6. Global Pagination Configuration**

Set pagination defaults in **application.properties**:

```properties
spring.data.web.pageable.default-page-size=20
spring.data.web.pageable.page-parameter=page
spring.data.web.pageable.size-parameter=size
spring.data.web.pageable.one-indexed-parameters=true
```

### One-indexed parameters example:

```
/campaigns/1/tasks?page=1&size=2
```

instead of:

```
?page=0
```

---

# **7. Adding Sorting to Pagination**

Spring uses this URL syntax:

```
?sort=property,direction
```

Example:

```
?page=1&size=2&sort=dueDate,desc
```

### Controller (no change required)

```java
@GetMapping
public Page<Task> list(
        @PathVariable("id") long id,
        Pageable pageable) {

    return taskRepository.findByCampaignId(id, pageable);
}
```

### Adding default sorting

```java
@GetMapping
public Page<Task> list(
        @PathVariable("id") long id,
        @PageableDefault(page = 0, size = 2)
        @SortDefault(sort = "id", direction = Sort.Direction.ASC)
        Pageable pageable) {

    return taskRepository.findByCampaignId(id, pageable);
}
```

### Multiple sorting criteria

```
?sort=dueDate,desc&sort=assignee.lastName,asc
```

Spring will create:

```java
Sort.by("dueDate").descending()
       .and(Sort.by("assignee.lastName").ascending());
```

---

# **8. Understanding Page Metadata**

A `Page<T>` contains:

* `content` → list of items
* `totalElements`
* `totalPages`
* `number` → page index
* `size` → items per page
* `sort` info
* `first` / `last` flags
* `numberOfElements`

This metadata helps clients build page navigation UI.

---

# **9. Preventing Infinite Recursion in JSON Output**

A Task belongs to a Campaign
A Campaign contains Tasks

Bidirectional relations cause Jackson recursion:

```
Campaign -> Task -> Campaign -> Task -> ...
```

Solution:

### On Campaign:

```java
@JsonManagedReference
@OneToMany(mappedBy = "campaign", fetch = FetchType.EAGER)
private Set<Task> tasks;
```

### On Task:

```java
@JsonBackReference
@ManyToOne(optional = false)
private Campaign campaign;
```

You avoid StackOverflow during JSON serialization.

---

# **10. Putting It All Together — Final Controller**

```java
@RestController
@RequestMapping("/campaigns/{id}/tasks")
public class CampaignTaskController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping
    public Page<Task> list(
            @PathVariable("id") long id,
            @PageableDefault(page = 0, size = 2)
            @SortDefault(sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {

        return taskRepository.findByCampaignId(id, pageable);
    }
}
```

---

# **11. Example Requests for Testing**

### First page, size=2

```
GET /campaigns/1/tasks?page=1&size=2
```

### Sorted descending

```
GET /campaigns/1/tasks?sort=dueDate,desc
```

### Multiple sort fields

```
GET /campaigns/1/tasks?sort=dueDate,desc&sort=assignee.lastName,asc
```

### One-indexed enabled

```
?page=1&size=2
```

---

# **12. Summary**

| Feature              | Supported By                                   | Notes                      |
| -------------------- | ---------------------------------------------- | -------------------------- |
| Pagination           | `Pageable`, `Page<T>`                          | Auto-mapped via Spring MVC |
| Sorting              | `Sort` inside `Pageable`                       | `sort=field,asc`           |
| Default values       | `@PageableDefault`, properties                 | Method-level or global     |
| JSON recursion       | `@JsonManagedReference` / `@JsonBackReference` | Prevents infinite loops    |
| Custom page sizes    | URL parameters                                 | `?size=50`                 |
| Multiple sort fields | Multiple `sort=` params                        | Flexible ordering          |

---


