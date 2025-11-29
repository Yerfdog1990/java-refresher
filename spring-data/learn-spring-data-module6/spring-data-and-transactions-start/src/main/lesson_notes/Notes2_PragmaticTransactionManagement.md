
---

# # **Programmatic Transaction Management in Spring**

Spring’s `@Transactional` annotation provides a convenient declarative way to demarcate transactional boundaries. Behind the scenes, Spring uses AOP proxies that automatically begin, commit, suspend, or roll back transactions according to the annotation configuration.

However, declarative transaction management—while elegant—is not always the ideal solution. There are scenarios where we must **manually control transaction boundaries**, especially when dealing with:

* slow external I/O,
* multiple different resources,
* custom commit/rollback logic,
* advanced error-handling workflows.

This lesson presents **programmatic transaction management** using:

* `TransactionTemplate`
* `PlatformTransactionManager`

We will also include **full CRUD examples** using programmatic transactions.

---

# # **1. Overview**

Using `@Transactional` looks like this:

```java
@Transactional
public void initialPayment(PaymentRequest request) {
    savePaymentRequest(request); // DB
    callThePaymentProviderApi(request); // External API
    updatePaymentState(request); // DB
    saveHistoryForAuditing(request); // DB
}
```

Declarative transactions are simple, but sometimes this simplicity causes problems.

When the method is called:

1. The transactional aspect creates a new EntityManager.
2. A JDBC connection is borrowed from the pool.
3. The first database operation happens.
4. Then you call an **external API**, but the JDBC connection is **kept open**.
5. After the API completes, remaining DB operations execute with the same connection.

If the external API is slow, then **database connections are held for too long**.

---

# # **2. Trouble in Paradise**

## **2.1. The Problem**

Mixing **database I/O** and **remote API I/O** inside a single `@Transactional` method is dangerous because:

* The transaction holds a JDBC connection for its full duration.
* A slow external I/O operation blocks that connection.
* Many concurrent calls lead to connection pool exhaustion.

Example:

```
10 concurrent calls × 1 slow API → 10 DB connections stuck waiting
```

If your pool size is 10, your application becomes unresponsive.

So either:

1. Separate DB work from external API calls
   **OR**
2. Use manual transaction management to limit the scope of actual DB transactions.

This is where **programmatic transaction management** helps.

---

# # **3. Using TransactionTemplate**

`TransactionTemplate` provides a callback-style API for executing code within a transaction *only when needed*.
It uses a `PlatformTransactionManager` behind the scenes.

## **3.1. Setting Up TransactionTemplate**

```java
@Autowired
private PlatformTransactionManager transactionManager;

private TransactionTemplate transactionTemplate;

@BeforeEach
void setUp() {
    transactionTemplate = new TransactionTemplate(transactionManager);
}
```

Spring Boot automatically configures:

* `JpaTransactionManager` (JPA)
* `DataSourceTransactionManager` (JDBC)
* others depending on your setup

### **3.1.1. Sample Domain Model**

```java
@Entity
public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    private Long amount;

    @Column(unique = true)
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    private State state;

    public enum State {
        STARTED, FAILED, SUCCESSFUL
    }

    // getters and setters
}
```

### **Testing Setup**

```java
@DataJpaTest
@ActiveProfiles("test")
@Transactional(propagation = NOT_SUPPORTED)
public class ManualTransactionIntegrationTest {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private EntityManager entityManager;

    private TransactionTemplate transactionTemplate;

    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }
}
```

Transactional support is disabled for the test class so that we can manage transactions manually.

---

# # **3.2. Transactions With Results**

`TransactionTemplate.execute()` allows returning a value from the transaction.

```java
Long id = transactionTemplate.execute(status -> {
    Payment payment = new Payment();
    payment.setAmount(1000L);
    payment.setReferenceNumber("Ref-1");
    payment.setState(Payment.State.SUCCESSFUL);

    entityManager.persist(payment);

    return payment.getId();
});
```

After execution:

```java
Payment payment = entityManager.find(Payment.class, id);
assertThat(payment).isNotNull();
```

### **Rollback Example (Constraint Violation)**

```java
transactionTemplate.execute(status -> {
    Payment first = new Payment();
    first.setAmount(1000L);
    first.setReferenceNumber("Ref-1");

    Payment second = new Payment();
    second.setAmount(2000L);
    second.setReferenceNumber("Ref-1"); // duplicate value

    entityManager.persist(first); // ok
    entityManager.persist(second); // fails → triggers rollback

    return null;
});
```

Since the second insert fails, **both payments are rolled back**.

### **Manual Rollback**

```java
transactionTemplate.execute(status -> {
    Payment payment = new Payment();
    payment.setAmount(1000L);
    payment.setReferenceNumber("Ref-1");

    entityManager.persist(payment);

    status.setRollbackOnly();

    return null;
});
```

---

# # **3.3. Transactions Without Results**

Use `TransactionCallbackWithoutResult`:

```java
transactionTemplate.execute(new TransactionCallbackWithoutResult() {
    @Override
    protected void doInTransactionWithoutResult(TransactionStatus status) {
        Payment payment = new Payment();
        payment.setReferenceNumber("Ref-1");
        payment.setState(Payment.State.SUCCESSFUL);

        entityManager.persist(payment);
    }
});
```

---

# # **3.4. Custom Transaction Configurations**

You can modify the template:

```java
transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
transactionTemplate.setTimeout(1000);
transactionTemplate.setReadOnly(true);
```

If you need multiple configurations, create multiple templates.

---

# # **4. Using PlatformTransactionManager (Low-Level API)**

`TransactionTemplate` is a wrapper around `PlatformTransactionManager`.
We can also use the transaction manager directly for complete control.

## **4.1. Configuring Transaction Definitions**

```java
DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
definition.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
definition.setTimeout(3);
```

## **4.2. Executing Transactions Manually**

```java
TransactionStatus status = transactionManager.getTransaction(definition);

try {
    Payment payment = new Payment();
    payment.setReferenceNumber("Ref-1");
    payment.setState(Payment.State.SUCCESSFUL);

    entityManager.persist(payment);

    transactionManager.commit(status);
} catch (Exception ex) {
    transactionManager.rollback(status);
}
```

This is the most explicit level of control.

---

# # **5. CRUD Example Using Programmatic Transactions**

Assume the repository:

```java
@Repository
public class PaymentRepository {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PlatformTransactionManager txManager;

    public Payment save(Payment payment) {
        TransactionStatus tx = txManager.getTransaction(new DefaultTransactionDefinition());

        try {
            em.persist(payment);
            txManager.commit(tx);
            return payment;
        } catch (Exception e) {
            txManager.rollback(tx);
            throw e;
        }
    }

    public Payment find(Long id) {
        return em.find(Payment.class, id);
    }

    public Payment update(Long id, Long newAmount) {
        TransactionStatus tx = txManager.getTransaction(new DefaultTransactionDefinition());

        try {
            Payment p = em.find(Payment.class, id);
            p.setAmount(newAmount);
            txManager.commit(tx);
            return p;
        } catch (Exception e) {
            txManager.rollback(tx);
            throw e;
        }
    }

    public void delete(Long id) {
        TransactionStatus tx = txManager.getTransaction(new DefaultTransactionDefinition());

        try {
            Payment p = em.find(Payment.class, id);
            em.remove(p);
            txManager.commit(tx);
        } catch (Exception e) {
            txManager.rollback(tx);
            throw e;
        }
    }
}
```

This is **full manual CRUD** based on programmatic transaction control.

---

# # **6. PlatformTransactionManager — Detailed Breakdown**

Spring’s transaction infrastructure is built around:

```java
public interface PlatformTransactionManager extends TransactionManager
```

This is the central API for imperative transaction control.

## **Known Implementations**

* `JpaTransactionManager`
* `DataSourceTransactionManager`
* `HibernateTransactionManager`
* `JdbcTransactionManager`
* `JtaTransactionManager`

## **Core Methods**

### **getTransaction(TransactionDefinition definition)**

Starts or joins a transaction.

### **commit(TransactionStatus status)**

Commits the transaction — unless it is marked rollback-only.

### **rollback(TransactionStatus status)**

Rolls back the transaction.

---

# # **7. How Spring Uses PlatformTransactionManager Internally**

Both:

* `@Transactional`
* `TransactionTemplate`

are simply **abstractions** on top of `PlatformTransactionManager`.

### @Transactional Flow

```
Method call → AOP Proxy → getTransaction() → business logic → commit/rollback
```

### Using TransactionTemplate

```
execute() → getTransaction() → lambda → commit/rollback
```

### Using PlatformTransactionManager Directly

```
getTransaction() → try/catch → commit/rollback
```

This is the lowest-level control.

---

# # **8. When to Use Programmatic Transactions**

Use them when:

* Transaction scope must be smaller than your method.
* You mix DB I/O and slow external I/O.
* You need conditional or multi-step commit logic.
* You need to run partial work outside a transaction.
* You need explicit control over isolation, timeout, or nested transactions.
* You need different propagation modes within the same method.

Declarative transactions cannot express all scenarios cleanly.

---

# # **9. Summary**

In this lesson, we learned:

* Why declarative `@Transactional` sometimes causes connection pool exhaustion.
* How `TransactionTemplate` provides cleaner programmatic control.
* How to manually control commit/rollback using `PlatformTransactionManager`.
* How to configure isolation, propagation, timeouts, and read-only hints.
* How to perform CRUD operations using programmatic transactions.

With these tools, you can precisely manage transaction boundaries even in complex or performance-sensitive environments.

---
