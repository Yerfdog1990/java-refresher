
# **Spring Data and Declarative Transaction Management**

- ✔ Includes CRUD examples
- ✔ Covers how Spring Data uses transactions
- ✔ Explains @Transactional in detail
- ✔ Shows rollback rules
- ✔ Demonstrates service-layer transaction boundaries
- ✔ Ends with a full **Declarative vs Programmatic comparison table**

You can paste this directly into a Markdown file.

---

# # **1. What Is a Transaction and Why Do We Need One?**

A **transaction** is a *unit of work* composed of one or more operations that must be treated atomically:

- ✔ Either **all operations succeed**
- ✖ Or **none** are applied

There is **no partial application** of a transaction.

### **Why transactions matter**

* Ensure **data integrity**
* Guarantee **consistency** across multiple operations
* Prevent partial updates
* Allow rollback on error
* Improve performance (batching, fewer flushes)

### **ACID properties** (brief recall)

| Property        | Meaning                                           |
| --------------- | ------------------------------------------------- |
| **Atomicity**   | All-or-nothing execution                          |
| **Consistency** | Data remains valid before & after the transaction |
| **Isolation**   | Transactions don’t interfere with each other      |
| **Durability**  | Changes survive system failures                   |

### **Manual transaction operations**

Traditionally, database transaction management requires:

1. **Begin**
2. **Commit**
3. **Rollback on error**

Doing this manually everywhere is verbose and error-prone.

### **How Spring helps**

Spring abstracts this away through:

* **Declarative transaction management (@Transactional)**
* **Programmatic management using PlatformTransactionManager**
* **Repositories that are transactional by default** (CRUD operations)

This greatly reduces boilerplate and centralizes consistency rules.

---

# # **2. Initial Setup**

To observe transaction behavior in logs, add the following to `application.properties`:

```properties
logging.level.org.springframework.orm.jpa=DEBUG
logging.level.org.springframework.transaction.interceptor=TRACE
spring.jpa.show-sql=true
```

### **Spring Boot**

If `spring-boot-starter-data-jpa` is present:

✔ Transaction management is **auto-configured**
✔ A default `PlatformTransactionManager` is registered

### **Non–Spring Boot**

You must explicitly enable it:

```java
@Configuration
@EnableTransactionManagement
public class AppConfig { }
```

---

# # **3. Transaction Management in Spring Data**

All built-in repository methods in Spring Data JPA are **already transactional**:

* `save`
* `findAll`
* `findById`
* `existsById`
* `deleteById`
* etc.

### Example — Observing a repository method transaction

```java
public class SpringDataAndTransactionsApp implements ApplicationRunner {

    @Autowired
    private CampaignRepository campaignRepository;

    @Override
    public void run(ApplicationArguments args) {
        Campaign c = campaignRepository.findById(1L).get();
    }
}
```

Log output:

```
Creating new transaction with name [...SimpleJpaRepository.findById]:
PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly
Hibernate: select ...
Completing transaction...
Committing JPA transaction...
```

Spring automatically:

* opened a transaction
* executed SQL
* committed it

---

# # **4. Declarative Transaction Management with @Transactional**

Declarative transactions involve annotating methods or classes with:

```java
@Transactional
```

At runtime, Spring wraps the method call in a proxy responsible for:

* starting a transaction
* committing on normal execution
* rolling back on exceptions

---

# # **5. Example Scenario: Ending a Campaign**

We implement a service to:

* close a finished campaign
* create a continuation campaign
* move all unfinished tasks to the continuation

These multiple steps must occur **within one transaction**.

### **Service Layer (transaction boundary)**

```java
@Service
public class CampaignService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Transactional
    public void endCampaign(Campaign campaign) {

        Set<Task> unfinishedTasks = campaign.getTasks().stream()
            .filter(t -> t.getStatus() != TaskStatus.DONE)
            .collect(Collectors.toSet());

        if (!unfinishedTasks.isEmpty()) {

            Campaign continuationCampaign = new Campaign(
                campaign.getCode() + "-CONT",
                campaign.getName() + " - Cont",
                campaign.getDescription());

            campaignRepository.save(continuationCampaign);

            unfinishedTasks.forEach(t -> t.setCampaign(continuationCampaign));
            taskRepository.saveAll(unfinishedTasks);
        }
    }
}
```

### **Observations:**

* Multiple repository calls → single atomic transaction
* Repositories' internal transactions detect the parent transaction and **join it**
* SQL is not committed until method completes

---

# # **6. CRUD Operations Under Transactions**

Below are CRUD operations using Spring Data repositories inside a declarative transaction.

---

## **6.1. Create**

```java
@Transactional
public Campaign createCampaign(Campaign c) {
    return campaignRepository.save(c);
}
```

---

## **6.2. Read**

```java
@Transactional(readOnly = true)
public List<Campaign> getActiveCampaigns() {
    return campaignRepository.findByActiveTrue();
}
```

* `readOnly = true` enables performance optimizations
* No flushing occurs

---

## **6.3. Update**

```java
@Transactional
public Campaign updateName(Long id, String newName) {
    Campaign c = campaignRepository.findById(id)
        .orElseThrow(RuntimeException::new);

    c.setName(newName);

    return campaignRepository.save(c);
}
```

Updates occur automatically through JPA dirty checking.

---

## **6.4. Delete**

```java
@Transactional
public void deleteCampaign(Long id) {
    campaignRepository.deleteById(id);
}
```

---

# # **7. Transaction Visibility in Debug Mode**

A breakpoint inside the transaction shows:

* Campaign not yet flushed to DB
* SQL not committed
* Repositories may participate in the same parent transaction

This is expected behavior because the transaction commits **only after the @Transactional method completes**.

---

# # **8. Marking Custom Finder Methods as Transactional**

Spring Data marks CRUD methods as transactional, but **not custom queries**:

```java
public interface CampaignRepository extends CrudRepository<Campaign, Long> {

    @Transactional(readOnly = true)
    Iterable<Campaign> findByNameContaining(String name);
}
```

This ensures:

✔ Consistency
✔ Safety
✔ Read-only optimization

---

# # **9. Rollback Rules**

By default:

### Spring rolls back on:

* **RuntimeException**
* **Error**

### Spring does NOT roll back on:

* **Checked exceptions** (Exception)

### Example: Adding an I/O error that triggers rollback

```java
private void writeToExternalLog() {
    throw new IOError(null); // Error subclass → rollback
}
```

Logs show:

```
Initiating transaction rollback
Rolling back JPA transaction...
```

---

## **9.1. Preventing rollback for specific exceptions**

```java
@Transactional(noRollbackFor = IOError.class)
public void endCampaign(Campaign campaign) {
    ...
}
```

Now the transaction **commits**, even though `writeToExternalLog()` throws an error.

---

## **9.2. Forcing rollback for checked exceptions**

```java
@Transactional(rollbackFor = IOException.class)
public void endCampaign(Campaign campaign) {
    ...
}
```

---

# # **10. Declarative vs Programmatic Transaction Management**

Below is a full comparison table.

---

# **Declarative vs Programmatic Transaction Management**

| Feature                              | Declarative (@Transactional)            | Programmatic (TransactionTemplate / PlatformTransactionManager) |
| ------------------------------------ | --------------------------------------- | --------------------------------------------------------------- |
| **Boilerplate**                      | Very low                                | High                                                            |
| **Readability**                      | Very high                               | Medium                                                          |
| **Transaction boundaries**           | Defined by annotation                   | Defined manually in code                                        |
| **Best location**                    | Service layer                           | Special cases only                                              |
| **Rollback rules**                   | Configurable via annotation             | Configurable inside code                                        |
| **Mixing with non-DB I/O**           | Not ideal (holds DB connection)         | Better control                                                  |
| **Suspending/resuming transactions** | Automatic (REQUIRES_NEW, NOT_SUPPORTED) | Fully manual                                                    |
| **Use cases**                        | 95% of enterprise apps                  | Special cases: batching, low-level resource usage               |
| **Risk of misuse**                   | Internal calls bypassing proxy          | Incorrect manual handling                                       |

### **When to choose declarative**

✔ Standard business logic
✔ Typical repository operations
✔ Service-layer workflows
✔ Simple rollback rules

### **When to choose programmatic**

✔ Mixing long-running I/O + DB work
✔ Conditional transaction behavior
✔ Manual batching
✔ Low-level control (flush timing, savepoints, etc.)

---

# # **11. Conclusion**

In this lesson we covered:

* What transactions are and why they matter
* How Spring Boot auto-configures transaction management
* How Spring Data repositories are transactional by default
* How to use `@Transactional` to build service-layer transaction boundaries
* CRUD examples under declarative transactions
* Rollback rules, noRollbackFor, rollbackFor
* Logging and debugging transaction behavior
* Marking custom queries as transactional
* Full comparison between declarative and programmatic approaches

Declarative transaction management is the preferred and most widely used mechanism, while programmatic management is reserved for special cases requiring fine-grained control.

---


# # **12. Programmatic Transaction Management (CRUD Examples Included)**

While declarative management is preferred, Spring also allows complete manual control using:

* **PlatformTransactionManager**
* **TransactionTemplate**
* **TransactionDefinition**
* **TransactionStatus**

This allows you to explicitly:

* start a transaction
* commit the transaction
* roll back on your terms

Programmatic transactions are useful for:

* fine-grained control
* custom propagation behavior
* combining DB + long-running I/O
* partial commits
* savepoints

---

# ## **12.1. Setup for Programmatic Transactions**

Inject the transaction manager:

```java
@Service
public class ProgrammaticCampaignService {

    private final PlatformTransactionManager txManager;
    private final CampaignRepository campaignRepository;
    private final TaskRepository taskRepository;

    public ProgrammaticCampaignService(
            PlatformTransactionManager txManager,
            CampaignRepository campaignRepository,
            TaskRepository taskRepository
    ) {
        this.txManager = txManager;
        this.campaignRepository = campaignRepository;
        this.taskRepository = taskRepository;
    }
}
```

---

# ## **12.2. Programmatic CRUD Using TransactionTemplate**

You can wrap operations inside `TransactionTemplate.execute()`:

```java
private final TransactionTemplate template = new TransactionTemplate(txManager);
```

---

## **12.2.1. CREATE (Programmatic)**

```java
public Campaign createCampaign(Campaign c) {
    return template.execute(status -> campaignRepository.save(c));
}
```

✔ Transaction automatically created
✔ Committed when lambda returns
✔ Rolled back if an unchecked exception is thrown

---

## **12.2.2. READ (Programmatic)**

```java
public List<Campaign> findByName(String name) {
    template.setReadOnly(true);

    return template.execute(status ->
        campaignRepository.findByNameContaining(name)
    );
}
```

✔ Optimized read-only transaction
✔ Changes cannot be flushed inside this block

---

## **12.2.3. UPDATE (Programmatic)**

```java
public Campaign updateCampaignName(Long id, String newName) {
    return template.execute(status -> {

        Campaign c = campaignRepository.findById(id)
            .orElseThrow(RuntimeException::new);

        c.setName(newName);

        return campaignRepository.save(c);
    });
}
```

✔ Same behavior as declarative—dirty checking still works
✔ Commit occurs only after the block completes

---

## **12.2.4. DELETE (Programmatic)**

```java
public void deleteCampaign(Long id) {
    template.executeWithoutResult(status -> {
        campaignRepository.deleteById(id);
    });
}
```

✔ `executeWithoutResult` is used for void operations

---

# # **12.3. Programmatic Transactions Using PlatformTransactionManager Only**

Sometimes you want *complete* control:

```java
public Campaign createCampaignManual(Campaign c) {

    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    def.setName("manual-transaction");
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

    TransactionStatus status = txManager.getTransaction(def);

    try {
        Campaign saved = campaignRepository.save(c);
        txManager.commit(status);       // explicitly commit
        return saved;

    } catch (Exception ex) {
        txManager.rollback(status);     // explicitly rollback
        throw ex;
    }
}
```

This gives you:

* Manual begin
* Manual commit
* Manual rollback

This is the closest equivalent to JDBC-style transaction control.

---

# # **12.4. Manual Rollback Conditions**

You can force a rollback manually:

```java
public void deleteWithChecks(Long id) {

    TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());

    try {
        Campaign c = campaignRepository.findById(id).orElseThrow();

        if (!c.getTasks().isEmpty()) {
            txManager.rollback(status); // manual rollback
            throw new IllegalStateException("Cannot delete campaign with tasks");
        }

        campaignRepository.delete(c);
        txManager.commit(status);

    } catch (Exception e) {
        txManager.rollback(status);
        throw e;
    }
}
```

✔ Gives full control over complex branching logic
✔ Declarative transactions cannot handle this granularity

---

# # **12.5. Programmatic Version of “End Campaign” Example**

Equivalent to the earlier declarative version:

```java
public void endCampaignProgrammatically(Campaign campaign) {

    TransactionStatus status =
        txManager.getTransaction(new DefaultTransactionDefinition());

    try {

        Set<Task> unfinishedTasks = campaign.getTasks().stream()
            .filter(t -> t.getStatus() != TaskStatus.DONE)
            .collect(Collectors.toSet());

        if (!unfinishedTasks.isEmpty()) {

            Campaign continuationCampaign = new Campaign(
                campaign.getCode() + "-CONT",
                campaign.getName() + " - Cont",
                campaign.getDescription());

            campaignRepository.save(continuationCampaign);

            unfinishedTasks.forEach(t -> t.setCampaign(continuationCampaign));
            taskRepository.saveAll(unfinishedTasks);
        }

        txManager.commit(status);

    } catch (Exception e) {
        txManager.rollback(status);
        throw e;
    }
}
```

✔ 100% functionally same as `@Transactional`
✔ But with explicit commit/rollback
✔ Useful when external resources must dictate transaction boundaries

---

# # **12.6. When You MUST Use Programmatic Transactions**

Use programmatic transactions when:

### **1. You need savepoints and partial rollbacks**

```java
Object savepoint = status.createSavepoint();
...
status.rollbackToSavepoint(savepoint);
```

### **2. You mix DB operations with long-running I/O**

Holding a DB connection for long periods under `@Transactional` is dangerous.

### **3. You want different branches to commit/rollback independently**

Declarative transactions cannot do this.

### **4. You need fine-grained control over propagation**

Programmatically:

* suspend a transaction
* resume it
* nest transactions
* override propagation rules at runtime

---

# # **13. Declarative vs Programmatic Transaction Management (Final Combined Table)**

| Feature                              | Declarative (@Transactional) | Programmatic (TransactionTemplate / PlatformTransactionManager) |
| ------------------------------------ | ---------------------------- | --------------------------------------------------------------- |
| **Boilerplate**                      | Minimal (best for most apps) | High — manual commit/rollback                                   |
| **Readability**                      | Very high                    | Medium to low                                                   |
| **Transaction boundaries**           | Defined declaratively        | Fully manual                                                    |
| **Rollback behavior**                | Annotation attributes        | Fully controlled in code                                        |
| **Nested logic**                     | Limited                      | Fully supported (savepoints)                                    |
| **Use within internal method calls** | Proxy limitations            | Always works                                                    |
| **Best for**                         | Standard business logic      | Complex flows requiring custom control                          |
| **Exception handling**               | Automatic                    | Manual                                                          |
| **Mixing DB + non-DB work**          | Risky for long I/O           | Fully controlled                                                |
| **Typical usage**                    | 95% of apps                  | Rare but important cases                                        |

---



