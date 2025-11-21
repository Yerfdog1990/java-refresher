
---

# **📘 Lesson Notes: FlushModeType (AUTO vs COMMIT) in JPA & Hibernate**

## **1. Introduction**

In JPA and Hibernate, **FlushModeType** controls **when the persistence context (1st-level cache) synchronizes changes with the database**.

Flushing occurs when Hibernate generates SQL INSERT/UPDATE/DELETE statements based on the in-memory changes.

The two JPA-defined flush modes are:

* **FlushModeType.AUTO (default)**
* **FlushModeType.COMMIT**

Hibernate additionally provides:

* **FlushModeType.ALWAYS**
* **FlushModeType.MANUAL**

But this lesson focuses on the core **AUTO vs COMMIT**, then explains their interaction with queries, transactions, and programmatic flush().

---

# **2. What Does “Flushing” Actually Mean?**

Flushing moves changes from the **persistence context → SQL statements**.
It does **NOT** commit the transaction.

### **Flushing vs Committing**

| Action       | Effect                                                  |
| ------------ | ------------------------------------------------------- |
| **flush()**  | Writes SQL to DB, but DB may rollback later             |
| **commit()** | Finalizes the transaction; persists all flushed changes |

---

# **3. FlushModeType.AUTO**

### **3.1 Definition**

AUTO is the *default* and **strongly consistent** mode.

Hibernate/JPA will **flush automatically**:

1. **Before transaction commit**
2. **Before any query whose result *may depend* on pending changes**

### **3.2 Why flush before queries?**

Because JPA must guarantee:

> “Queries within a transaction must see all pending changes.”

So queries do not return stale results.

### **3.3 How Hibernate determines whether to flush before a query**

Hibernate checks the **query space**:

* If query touches tables that have pending changes → **flush**
* If query touches unrelated tables → **skip flush**

Example:

```java
// pending change (ChessPlayer)
entityManager.persist(player);

// Query touches ChessTournament table → no flush
entityManager.createQuery("SELECT t FROM ChessTournament t").getResultList();

// Query touches ChessPlayer → flush is required
entityManager.createQuery("SELECT p FROM ChessPlayer p").getResultList();
```

Hibernate determines this by analyzing the SQL it will generate.

### **3.4 AUTO with Native Queries**

Native queries have **no automatic query-space detection**, so Hibernate flushes by default unless you explicitly synchronize:

```java
Query query = em.createNativeQuery("SELECT * FROM tournament");
query.unwrap(SynchronizeableQuery.class)
     .addSynchronizedEntityClass(ChessTournament.class);
```

---

# **4. FlushModeType.COMMIT**

### **4.1 Definition**

COMMIT **only guarantees** that flush will happen **at transaction commit**.

Before running a query:

* The provider **may** flush
* But it is **not required**
* Therefore query results may be **stale or missing** in-context changes

### **4.2 Example showing inconsistency**

```java
entityManager.setFlushMode(FlushModeType.COMMIT);
entityManager.persist(customer);

List<Customer> list = entityManager
        .createQuery("SELECT c FROM Customer c", Customer.class)
        .getResultList();

// Newly persisted customer NOT returned!
```

The entity is in the persistence context but **not flushed to the DB**, so the query results ignore it.

### **4.3 When COMMIT is useful**

* High-throughput write-heavy systems
* Long-running analytical transactions
* Read-mostly transactions where immediate visibility is not required
* Batch write operations

---

# **5. Side-by-Side Comparison Table**

| Feature             | AUTO                   | COMMIT                        |
| ------------------- | ---------------------- | ----------------------------- |
| Default             | ✔ Yes                  | ✘ No                          |
| Flush before query? | ✔ Required if relevant | ✘ Not required                |
| Flush at commit?    | ✔ Yes                  | ✔ Yes                         |
| Consistency         | Strong                 | Weaker                        |
| Performance         | Slightly slower        | Faster (fewer flushes)        |
| Typical Use         | Most applications      | Carefully optimized workloads |

---

# **6. Code Examples: AUTO vs COMMIT**

## **6.1 Entity Definitions**

```java
@Entity
public class Customer {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private int age;
    // getters/setters
}

@Entity
public class CustomerAddress {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String street;
    private String city;

    private long customer_id;
}
```

---

## **6.2 Example: COMMIT mode – query does NOT see unflushed changes**

```java
entityManager.setFlushMode(FlushModeType.COMMIT);
transaction.begin();

Customer c = new Customer("Alice", 30);
entityManager.persist(c);

// No flush → DB not synchronized
List<Customer> result =
    entityManager.createQuery("SELECT c FROM Customer c", Customer.class)
                 .getResultList();

assert(result.isEmpty()); // OK
transaction.rollback();
```

---

## **6.3 COMMIT mode with explicit flush()**

```java
entityManager.setFlushMode(FlushModeType.COMMIT);
transaction.begin();

Customer c = new Customer("Alice", 30);
entityManager.persist(c);

entityManager.flush(); // forced synchronization

Customer found = entityManager
    .createQuery("SELECT c FROM Customer c", Customer.class)
    .getSingleResult();
```

---

## **6.4 AUTO mode – flush occurs automatically before query**

```java
entityManager.setFlushMode(FlushModeType.AUTO);
transaction.begin();

Customer c = new Customer("Alice", 30);
entityManager.persist(c);

// AUTO → flushed automatically
Customer result =
    entityManager.createQuery(
            "SELECT c FROM Customer c WHERE c.name = 'Alice'",
            Customer.class)
        .getSingleResult();
```

---

# **7. Real-World Architectural Examples**

## **7.1 E-commerce checkout**

Use **AUTO**:

* Validating cart before payment must see newest cart item updates.
* AUTO ensures queries see all unflushed changes → avoids race conditions.

✔ Strong consistency
✔ Prevents order corruption
✔ Required in transactional services

## **7.2 Batch import system**

Use **COMMIT**:

* You persist thousands of items
* Running queries inside the loop would trigger many flushes under AUTO
* COMMIT avoids unnecessary flush operations

✔ Higher throughput
✔ Flush only once at commit

## **7.3 Reporting / Analytics**

Use **COMMIT** or even Hibernate **MANUAL**:

* Long-running queries
* You don't need to see in-transaction writes

✔ Performance optimized
✔ Keeps the session stable

## **7.4 CQRS / Event-Sourcing**

Use **AUTO**:

* Commands update domain models
* Queries must reflect domain events accurately

✔ Guarantees strong transaction-level consistency

---

# **8. When Should You Use Explicit flush()?**

### **Appropriate**

* When you need an **early DB constraint check**
* When a subsequent query needs DB-generated values (PK, triggers)
* Before bulk operations:

  ```java
  entityManager.flush();
  entityManager.clear();
  ```

### **Inappropriate**

* Inside loops (causes performance collapse)
* As a substitute for proper mode selection
* For controlling commit timing (flush != commit)

---

# **9. Hibernate-Specific FlushModes (For context)**

| Hibernate Mode | Behavior                                           |
| -------------- | -------------------------------------------------- |
| ALWAYS         | Flush before **every** query (slow but consistent) |
| MANUAL         | No auto-flush; application must flush manually     |

---

# **10. Best Practices**

### ✔ Use AUTO for most services

It is the safest mode and avoids subtle bugs.

### ✔ Use COMMIT only when you truly understand the consistency tradeoffs

COMMIT can return unexpected results when queries rely on unflushed changes.

### ✔ Never rely on implicit behavior when performing bulk queries

Always `flush()` + `clear()`.

### ✔ Avoid MANUAL unless writing batch-oriented infrastructure code.

---

# **11. Conclusion**

FlushModeType controls one of the most important aspects of JPA/Hibernate behavior: **when the in-memory state is synchronized with the database**.

* **AUTO** → safest and default; consistent queries; flushes when necessary
* **COMMIT** → performance-optimized; flush only at commit; queries may see stale data

Understanding flush behavior is essential for writing correct, high-performance, and predictable persistence logic.

---


