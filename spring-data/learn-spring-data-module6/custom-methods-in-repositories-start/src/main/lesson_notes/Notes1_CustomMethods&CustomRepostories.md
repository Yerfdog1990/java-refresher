
---

# # **📘 Lesson Notes: Spring Data JPA — Custom Methods & Custom Repository Implementations**

Spring Data JPA gives developers powerful tools for creating queries with very little code. However, there are many cases where:

* You need **custom query logic** not supported by derived queries
* You must **control how queries are built**
* You want custom behavior **shared across multiple repositories**
* You must **override default CRUD methods**
* You want to build a **custom base repository**
* You need repository fragments for **modular compositions**

This lesson covers *all* these scenarios with detailed explanations and examples.

---

# # **1. Why Custom Repository Methods?**

Spring Data gives us default CRUD operations and derived queries like:

```java
List<Task> findByStatus(TaskStatus status);
```

But this is not enough when:

* Query logic is dynamic or complex
* We must prepare parameters before executing a query
* The query requires multiple wildcard rules
* The underlying JPA query is not representable by method naming rules
* We want to override Spring Data default behavior

This is where **Custom Repository Methods** and **Repository Fragments** become essential.

---

# # **2. Defining a Custom Repository**

Custom repository logic is implemented by:

1. Creating a **custom repository interface**
2. Creating an **implementation class** using `EntityManager`
3. Adding that interface to the main repository
4. Following the naming convention:

   ```
   <InterfaceName>Impl
   ```

---

## ## **2.1 Step 1 — Create Custom Repository Interface**

```java
public interface CustomTaskRepository {
    List<Task> search(String searchParam);
}
```

This interface declares your custom method.

---

## ## **2.2 Step 2 — Extend Your Main Repository**

```java
public interface TaskRepository 
        extends CrudRepository<Task, Long>, CustomTaskRepository {
}
```

Now `TaskRepository` has BOTH:

✔ CRUD methods
✔ Custom search method

---

## ## **2.3 Step 3 — Implement Custom Logic**

Create a class:

```
CustomTaskRepositoryImpl
```

> Must match the interface name + `"Impl"`
> e.g. `CustomTaskRepository` → `CustomTaskRepositoryImpl`

```java
public class CustomTaskRepositoryImpl implements CustomTaskRepository {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Task> search(String searchParam) {

        String searchQueryParam =
            "%" + String.join("%", searchParam.split(" ")) + "%";

        TypedQuery<Task> query = entityManager.createQuery(
            "SELECT t FROM Task t WHERE t.description LIKE ?1", Task.class
        );

        query.setParameter(1, searchQueryParam);

        return query.getResultList();
    }
}
```

### 🔍 Explanation

If the user searches:

```
"Description Task 3"
```

It becomes:

```
"%Description%Task%3%"
```

This matches:

> “This Description belongs to Task 3”

---

## ## **2.4 Step 4 — Using Your Custom Method**

```java
@SpringBootApplication
public class CustomMethodsInRepositoriesApp implements ApplicationRunner {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public void run(ApplicationArguments args) {
        List<Task> tasks = taskRepository.search("Description Task 3");

        tasks.forEach(t -> 
            System.out.println("Match: " + t.getDescription())
        );
    }
}
```

---

# # **3. Overriding Base Repository Methods (e.g. findAll)**

Spring Data allows overriding CRUD operations.

### Example use case

Always return tasks that are **NOT** `DONE`.

---

## ## Step 1 — Declare Overridden Method

```java
public interface CustomTaskRepository {
    List<Task> findAll();
}
```

---

## ## Step 2 — Implement It

```java
@Override
public List<Task> findAll() {
    TypedQuery<Task> query = entityManager.createQuery(
        "SELECT t FROM Task t WHERE t.status != ?1",
        Task.class
    );

    query.setParameter(1, TaskStatus.DONE);

    return query.getResultList();
}
```

---

## ## Step 3 — Use It

```java
int openTasks = taskRepository.findAll().size();
System.out.println("Open tasks: " + openTasks);
```

---

# # **4. The Fragment-Based Custom Repository Model (Modern & Recommended)**

Spring Data encourages **repository fragments** instead of single-implementation naming.

---

## ## Example of Fragments

### Fragment 1

```java
interface HumanRepository {
    void someHumanMethod(User user);
}

class HumanRepositoryImpl implements HumanRepository {
    @Override
    public void someHumanMethod(User user) {
        // logic
    }
}
```

### Fragment 2

```java
interface ContactRepository {
    void someContactMethod(User user);
    User anotherContactMethod(User user);
}

class ContactRepositoryImpl implements ContactRepository {
    // implementations
}
```

### Combine Them

```java
interface UserRepository
        extends CrudRepository<User, Long>,
                HumanRepository,
                ContactRepository {
}
```

Each fragment acts like a plug-in module.

---

# # **5. Customizing the Implementation Postfix**

Default postfix:

```
Impl
```

To change:

```java
@EnableJpaRepositories(repositoryImplementationPostfix = "RepoPostfix")
```

Then implementation must follow:

```
CustomTaskRepositoryRepoPostfix
```

---

# # **6. Creating a Custom Base Repository (Affects ALL repositories)**

Sometimes you want a custom method **available in every repository**.

---

## ## Step 1 — Define Base Repository Interface

```java
@NoRepositoryBean
public interface ExtendedRepository<T, ID extends Serializable>
        extends JpaRepository<T, ID> {

    List<T> findByAttributeContainsText(String attributeName, String text);
}
```

`@NoRepositoryBean` prevents Spring from instantiating this interface directly.

---

## ## Step 2 — Implement Base Class

```java
public class ExtendedRepositoryImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID>
        implements ExtendedRepository<T, ID> {

    private final EntityManager entityManager;

    public ExtendedRepositoryImpl(
            JpaEntityInformation<T, ?> entityInfo,
            EntityManager entityManager) {

        super(entityInfo, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public List<T> findByAttributeContainsText(String attributeName, String text) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(getDomainClass());
        Root<T> root = cq.from(getDomainClass());

        cq.where(cb.like(root.get(attributeName), "%" + text + "%"));

        return entityManager.createQuery(cq).getResultList();
    }
}
```

---

## ## Step 3 — Configure Spring to Use This Base Class

```java
@EnableJpaRepositories(
    basePackages = "com.example.repositories",
    repositoryBaseClass = ExtendedRepositoryImpl.class
)
public class JpaConfig {}
```

---

## ## Step 4 — Use the New Base Repository

```java
public interface StudentRepository
        extends ExtendedRepository<Student, Long> {
}
```

Now you can call:

```java
studentRepository.findByAttributeContainsText("name", "john");
```

---

# # **7. Using JpaContext (When Multiple EntityManagers Exist)**

If you have multiple persistence units:

```java
class UserRepositoryImpl implements UserRepositoryCustom {

    private final EntityManager em;

    @Autowired
    public UserRepositoryImpl(JpaContext context) {
        this.em = context.getEntityManagerByManagedType(User.class);
    }
}
```

This ensures the repository automatically uses the correct EntityManager for its domain type.

---

# # **8. Example CRUD Operations Using Custom Repository + Specification**

Below is a complete example using both:

### ✔ Custom Repository

### ✔ Specification

### ✔ CRUD Operations

---

## ## Entity

```java
@Entity
public class Task {

    @Id @GeneratedValue
    private Long id;

    private String description;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private Long assigneeId;
}
```

---

## ## Specification Example

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

    public static Specification<Task> assignedTo(Long userId) {
        return (root, query, cb) ->
                cb.equal(root.get("assigneeId"), userId);
    }
}
```

---

## ## CRUD + Specification Search Method

```java
public List<Task> search(TaskSearchRequest request) {

    Specification<Task> spec = Specification.where(null);

    if (request.getStatus() != null)
        spec = spec.and(hasStatus(request.getStatus()));

    if (request.getDueBefore() != null)
        spec = spec.and(dueBefore(request.getDueBefore()));

    if (request.getAssigneeId() != null)
        spec = spec.and(assignedTo(request.getAssigneeId()));

    return taskRepository.findAll(spec);
}
```

---

# # **9. What Is TaskSearchRequest? (Search DTO)**

**Important:**
`TaskSearchRequest` is **a custom DTO**, NOT part of Spring Data.

Used to carry optional filters.

```java
public class TaskSearchRequest {

    private TaskStatus status;
    private LocalDate dueBefore;
    private LocalDate dueAfter;
    private Long assigneeId;
    private Long campaignId;
    private String keyword;

    // getters and setters
}
```

Spring automatically maps query parameters to it:

```
/tasks?status=DONE&assigneeId=4&dueBefore=2025-11-30
```

---

# # **10. Summary**

### ✔ Custom repository methods let you define complex query logic

### ✔ Repository fragments allow modular custom behavior

### ✔ You can override base CRUD methods

### ✔ Custom base repositories extend default functionality for all repos

### ✔ JpaContext helps with multiple EntityManagers

### ✔ Specifications + SearchDTO = dynamic and scalable search APIs

### ✔ Custom implementations always use **EntityManager**

---
