
---

# **Lesson Notes: Transactions and @Transactional in Spring Framework**

---

## **1. Introduction**

In modern enterprise applications, **transaction management** is a crucial concept that ensures **data consistency**, **reliability**, and **integrity** when performing multiple database operations.

Spring provides a powerful and flexible transaction management framework that integrates seamlessly with **JPA**, **Hibernate**, and other persistence technologies.

This lesson explores what transactions are, how they work in Spring, how to configure and use the `@Transactional` annotation, and the best practices for managing transactions effectively.

---

## **2. Understanding Transactions**

### **2.1 What Are Transactions?**

A **transaction** is a **group of operations** that must either **all succeed** or **all fail** as a single logical unit of work.

**Example:**
When purchasing a book from Amazon, two operations occur:

1. The order is placed successfully.
2. The user’s account is debited.

If one operation fails (e.g., payment error), both must fail.
If both succeed, the transaction is **committed**.
If any operation fails, the system performs a **rollback**, undoing all previously executed operations in that transaction.

This ensures **atomicity**, meaning the transaction acts as a single indivisible operation.

---

### **2.2 Why Rollback?**

**Rollback** is the process of reversing already executed steps of a transaction if any instruction within it fails.
It ensures that **partial updates** do not corrupt the database and that the system remains in a consistent state.

---

## **3. Setting Up Transaction Management**

### **3.1 Dependencies**

Spring’s transaction support is provided by the `spring-tx` library, which is automatically included when using Spring Data JPA:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

### **3.2 Debugging Transactions**

To observe transaction logs from Hibernate, enable debugging in your `application.properties`:

```properties
logging.level.org.hibernate.engine.transaction.internal=DEBUG
```

This helps visualize when transactions start, commit, or roll back.

---

## **4. Transaction Management in Action**

### **Scenario Example**

Suppose we need to create a **Project** and its associated **Task** in a single operation.
If either the project or the task creation fails, **neither** should persist in the database.

This operation must be handled as a **single transaction**.

---

### **4.1 Implementing the Service Layer**

#### **IProjectService Interface**

```java
public interface IProjectService {
    void createProjectWithTasks();
}
```

#### **ProjectServiceImpl Implementation**

```java
@Service
public class ProjectServiceImpl implements IProjectService {

    private final IProjectRepository projectRepository;
    private final ITaskService taskService;

    public ProjectServiceImpl(IProjectRepository projectRepository, ITaskService taskService) {
        this.projectRepository = projectRepository;
        this.taskService = taskService;
    }

    @Override
    public void createProjectWithTasks() {
        Project project = new Project("Project 1", LocalDate.now());
        Project newProject = projectRepository.save(project);

        Task task1 = new Task("Task 1", "Project 1 Task 1", LocalDate.now(), LocalDate.now().plusDays(7));
        taskService.save(task1);

        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);
        newProject.setTasks(tasks);

        projectRepository.save(newProject);
    }
}
```

This method:

1. Creates and saves a project.
2. Creates and saves a task.
3. Associates the task with the project.

All these should succeed **together** or **fail together**.

---

### **4.2 Running the Demo**

```java
@SpringBootApplication
public class LsApp {

    private static final Logger LOG = LoggerFactory.getLogger(LsApp.class);

    @Autowired
    IProjectService projectService;

    @Autowired
    ITaskService taskService;

    @PostConstruct
    public void postConstruct() {
        try {
            projectService.createProjectWithTasks();
        } catch (Exception e) {
            LOG.error("Error occurred while creating project with tasks", e);
        }

        LOG.info("Fetching all projects:");
        projectService.findAll().forEach(p -> LOG.info(p.toString()));

        LOG.info("Fetching all tasks:");
        taskService.findAll().forEach(t -> LOG.info(t.toString()));
    }
}
```

If everything works, both the Project and Task are created successfully.
However, if an error occurs in creating a Task, we expect the **whole transaction** to roll back.

---

## **5. Using @Transactional**

### **5.1 Basic Usage**

To ensure both operations act as a single transaction, annotate the method with `@Transactional`:

```java
@Transactional
@Override
public void createProjectWithTasks() {
    // operations as shown earlier
}
```

If a **runtime exception** occurs, Spring automatically **rolls back** the transaction.

For example, modifying `TaskServiceImpl`:

```java
@Override
public Task save(Task task) {
    throw new RuntimeException("Unable to save task");
}
```

**Result:**
The transaction is rolled back and no Project or Task is persisted.

---

### **5.2 Rollback for Checked Exceptions**

By default, `@Transactional` rolls back only for **unchecked (runtime)** exceptions.
To include **checked exceptions**, specify them explicitly using the `rollbackFor` attribute.

#### **Custom Checked Exception**

```java
public class TaskNotSavedException extends Exception {
    public TaskNotSavedException(String msg) {
        super(msg);
    }
}
```

#### **Updated Service Implementation**

```java
@Transactional(rollbackFor = TaskNotSavedException.class)
@Override
public void createProjectWithTasks() throws TaskNotSavedException {
    // transactional operations
}
```

Now, even if a checked exception occurs, the transaction rolls back.

---

## **6. Configuring Transactions**

### **6.1 Java Configuration (Spring 3.1+)**

```java
@Configuration
@EnableTransactionManagement
public class PersistenceJPAConfig {

   @Bean
   public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
       // configuration details
   }

   @Bean
   public PlatformTransactionManager transactionManager() {
      JpaTransactionManager txManager = new JpaTransactionManager();
      txManager.setEntityManagerFactory(entityManagerFactory().getObject());
      return txManager;
   }
}
```

### **6.2 Spring Boot Configuration**

In Spring Boot, transaction support is **auto-configured** if `spring-data-*` or `spring-tx` dependencies exist.
No extra setup is required.

---

## **7. @Transactional Under the Hood**

Spring implements `@Transactional` using **AOP proxies**.

* When a bean is annotated with `@Transactional`, Spring creates a **proxy** around it.
* The proxy **intercepts method calls**, starting a transaction before method execution and committing or rolling it back afterward.

**Important implications:**

1. Only **public** methods are eligible for transactional interception.
2. **Self-invocation** (a method calling another `@Transactional` method within the same class) does **not** trigger a transaction.
3. The transactional behavior is applied only to **external calls** made through the proxy.

---

## **8. Local and Global Transactions**

* **Local Transactions:**
  Involve a **single resource** (e.g., one database). This is the most common scenario.

* **Global Transactions:**
  Span across **multiple resources**, such as multiple databases or a message queue.
  Spring abstracts this using `PlatformTransactionManager`, so application code remains unchanged regardless of the underlying transaction strategy.

---

## **9. Advanced Attributes of @Transactional**

| Attribute       | Description                                                                  | Example                              |
| --------------- | ---------------------------------------------------------------------------- | ------------------------------------ |
| `propagation`   | Defines how transactions behave when one transactional method calls another. | `Propagation.REQUIRES_NEW`           |
| `isolation`     | Controls how concurrent transactions interact with shared data.              | `Isolation.SERIALIZABLE`             |
| `timeout`       | Sets the maximum time (in seconds) a transaction can run before rollback.    | `timeout = 30`                       |
| `readOnly`      | Hints that the transaction will not perform any write operations.            | `readOnly = true`                    |
| `rollbackFor`   | Specifies exceptions that trigger rollback.                                  | `rollbackFor = SQLException.class`   |
| `noRollbackFor` | Specifies exceptions that **should not** trigger rollback.                   | `noRollbackFor = SQLException.class` |

---

## **10. Programmatic Transaction Management**

Though declarative transactions (via annotations) are preferred, programmatic control can be achieved using `TransactionAspectSupport`:

```java
public void createCourseProgrammatic(Course course) {
    try {
        courseDao.create(course);
    } catch (Exception e) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
}
```

This approach provides **fine-grained control**, but **increases code complexity**.

---

## **11. Common Pitfalls**

| Pitfall                                  | Description                                                                                                              |
| ---------------------------------------- | ------------------------------------------------------------------------------------------------------------------------ |
| **Self-invocation**                      | A method calling another `@Transactional` method in the same class bypasses the proxy, so the transaction isn’t applied. |
| **Non-public methods**                   | `@Transactional` on private/protected methods is ignored.                                                                |
| **Missing @EnableTransactionManagement** | Transactions won’t activate in non-Boot setups.                                                                          |
| **Read-only misunderstanding**           | The `readOnly` flag is a hint, not an enforcement. It doesn’t block writes in most JPA providers.                        |
| **Checked exceptions**                   | Do not trigger rollback unless specified with `rollbackFor`.                                                             |

---

## **12. Logging and Debugging Transactions**

To trace transaction behavior, enable:

```properties
logging.level.org.springframework.transaction=TRACE
```

This helps identify when transactions begin, commit, or roll back.

---

## **13. Summary**

* A **transaction** groups multiple operations that succeed or fail as one unit.
* **Rollback** ensures data consistency by undoing failed operations.
* Spring manages transactions declaratively using `@Transactional`.
* By default, only runtime exceptions trigger rollback.
* Use `rollbackFor` for checked exceptions.
* Spring Boot auto-configures transaction management.
* Transactions in Spring are implemented through **AOP proxies**.
* Prefer declarative transactions over programmatic ones for cleaner, maintainable code.

---

