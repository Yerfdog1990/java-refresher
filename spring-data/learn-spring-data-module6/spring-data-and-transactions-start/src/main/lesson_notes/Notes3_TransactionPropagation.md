
# **Transaction Propagation and Isolation in Spring @Transactional**

- ✔ Includes all details you provided
- ✔ Expanded explanations
- ✔ Clean code examples
- ✔ High-quality Markdown formatting
- ✔ Suitable for documentation, tutorials, or learning materials

---

# # **1. Introduction**

In this tutorial, we explore the `@Transactional` annotation in Spring.
Specifically, we study:

* Transaction boundaries
* Propagation behaviors
* Isolation levels
* How Spring creates, commits, suspends, or rolls back transactions
* Code examples for each concept

---

# # **2. What Is @Transactional?**

`@Transactional` allows you to wrap a method or class in a transactional boundary.

It enables configuration of:

* **Propagation**
* **Isolation**
* **Timeouts**
* **Read-only transactions**
* **Rollback rules**
* **Transaction manager selection**

## **2.1. Implementation Details**

Spring manages transactions using:

- ✔ AOP proxies (default)
- ✔ Bytecode manipulation (for advanced usage)

Because Spring relies on proxies:

* **Internal method calls bypass @Transactional**
* Only **public** methods are eligible

When a transactional method is invoked, Spring wraps it like this:

```java
createTransactionIfNecessary();
try {
    callMethod();
    commitTransactionAfterReturning();
} catch (exception) {
    completeTransactionAfterThrowing();
    throw exception;
}
```

## **2.2. How to Use @Transactional**

You can place the annotation on:

1. Interface
2. Superclass
3. Class
4. Method
   (High → low priority: class method > superclass method > interface method)

Example: Interface-level annotation

```java
@Transactional
public interface TransferService {
    void transfer(String user1, String user2, double val);
}
```

Class-level annotation:

```java
@Service
@Transactional
public class TransferServiceImpl implements TransferService {
    @Override
    public void transfer(String user1, String user2, double val) {
        // logic here
    }
}
```

Method-level override:

```java
@Transactional
public void transfer(String user1, String user2, double val) {
    // method logic
}
```

**Important:**
Spring **ignores** `@Transactional` on:

* `private` methods
* `protected` methods
* Internal method calls (`this.someMethod()`)

---

# # **3. Transaction Propagation**

Propagation defines **what happens when a transactional method calls another transactional method**.

Spring uses:

```
TransactionManager.getTransaction()
```

… to either:

* create a new transaction, or
* join an existing one, or
* run without any transaction

Below are all propagation behaviors.

---

## # **3.1. REQUIRED (Default)**

If a transaction exists → join it
Else → create a new one

```java
@Transactional(propagation = Propagation.REQUIRED)
public void requiredExample(String user) { }
```

Equivalent to:

```java
@Transactional
public void requiredExample(String user) { }
```

**Pseudo-code:**

```
if (existing transaction)
    use it
else
    start a new transaction
```

---

## # **3.2. SUPPORTS**

If a transaction exists → join
If not → run without transaction

```java
@Transactional(propagation = Propagation.SUPPORTS)
public void supportsExample(String user) { }
```

Pseudo-code:

```
if (existing transaction)
    join it
else
    execute non-transactionally
```

---

## # **3.3. MANDATORY**

Requires an active transaction.
If none exists → throws exception

```java
@Transactional(propagation = Propagation.MANDATORY)
public void mandatoryExample(String user) { }
```

Pseudo-code:

```
if (existing transaction)
    join it
else
    throw IllegalTransactionStateException
```

---

## # **3.4. NEVER**

Prohibits running inside a transaction.
If a transaction exists → throws an exception

```java
@Transactional(propagation = Propagation.NEVER)
public void neverExample(String user) { }
```

Pseudo-code:

```
if (existing transaction)
    throw IllegalTransactionStateException
else
    run non-transactionally
```

---

## # **3.5. NOT_SUPPORTED**

If a transaction exists → **suspend it**
Then run non-transactionally

```java
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public void notSupportedExample(String user) { }
```

**Notes:**

* True suspension requires **JTA Transaction Manager**
* Others simulate it (e.g., JDBC or JPA)

---

## # **3.6. REQUIRES_NEW**

Always creates a new transaction.
If a transaction exists → suspend it

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void requiresNewExample(String user) { }
```

Pseudo-code:

```
if (existing transaction)
    suspend it
start new transaction
```

**Important:**
Actual suspension only fully supported with JTA.

---

## # **3.7. NESTED**

If a transaction exists → create a **savepoint**
If not → behave like REQUIRED

```java
@Transactional(propagation = Propagation.NESTED)
public void nestedExample(String user) { }
```

Supported by:

* `DataSourceTransactionManager`
* Some JTA providers
* `JpaTransactionManager` (only with JDBC driver savepoints)

Savepoints allow partial rollbacks inside one larger transaction.

---

# # **4. Transaction Isolation**

Isolation controls how a transaction interacts with concurrent transactions.

Isolation levels prevent the following anomalies:

| Anomaly            | Description                           |
| ------------------ | ------------------------------------- |
| Dirty Read         | Reading uncommitted data              |
| Nonrepeatable Read | Same row returns different values     |
| Phantom Read       | Range query returns different results |

Spring defines five isolation levels:

```
DEFAULT
READ_UNCOMMITTED
READ_COMMITTED
REPEATABLE_READ
SERIALIZABLE
```

### **Important: Isolation applies only when a new transaction is created.**

If an existing transaction is re-used, the original isolation level is kept.

You can enforce validation:

```java
transactionManager.setValidateExistingTransaction(true);
```

Pseudo-code:

```
if (isolation != DEFAULT)
    if (current isolation != isolation)
        throw IllegalTransactionStateException
```

---

## # **4.2. READ_UNCOMMITTED**

* Allows dirty reads
* Allows non-repeatable reads
* Allows phantom reads

```java
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public void log(String message) { }
```

Not supported by:

* Postgres
* Oracle

They automatically upgrade to READ_COMMITTED.

---

## # **4.3. READ_COMMITTED**

Prevents:

✔ Dirty reads
Allows:

✖ Non-repeatable reads
✖ Phantom reads

```java
@Transactional(isolation = Isolation.READ_COMMITTED)
public void log(String message) { }
```

Default for: Postgres, SQL Server, Oracle

---

## # **4.4. REPEATABLE_READ**

Prevents:

✔ Dirty reads
✔ Non-repeatable reads
Allows:

✖ Phantom reads

```java
@Transactional(isolation = Isolation.REPEATABLE_READ)
public void log(String message) { }
```

Default for MySQL.
**Not supported** by Oracle.

Also prevents **lost updates** because rows are locked at read time.

---

## # **4.5. SERIALIZABLE**

Highest isolation level.
Prevents:

✔ Dirty reads
✔ Non-repeatable reads
✔ Phantom reads

But:

* Lowest concurrency
* Highest locking overhead
* Executes effectively in serial order

```java
@Transactional(isolation = Isolation.SERIALIZABLE)
public void log(String message) { }
```

---

# # **5. Complete Code Example Combining Propagation + Isolation**

```java
@Service
public class AccountService {

    @Autowired
    private AccountRepository repo;

    @Transactional(
        propagation = Propagation.REQUIRES_NEW,
        isolation = Isolation.REPEATABLE_READ,
        timeout = 10,
        rollbackFor = Exception.class
    )
    public void updateBalance(String username, double amount) {

        Account account = repo.findByUsername(username);

        account.setBalance(account.getBalance() + amount);

        repo.save(account);
    }
}
```

---

# # **6. Conclusion**

In this lesson, we explored:

* How `@Transactional` works under the hood
* Transaction proxy limitations (e.g., internal method calls)
* The seven propagation behaviors:

    * REQUIRED
    * SUPPORTS
    * MANDATORY
    * NEVER
    * NOT_SUPPORTED
    * REQUIRES_NEW
    * NESTED
* ACID isolation levels and supported anomalies
* How Spring validates and applies isolation rules

Understanding transaction propagation and isolation is essential for building reliable, concurrency-safe, and scalable Spring applications.

---
