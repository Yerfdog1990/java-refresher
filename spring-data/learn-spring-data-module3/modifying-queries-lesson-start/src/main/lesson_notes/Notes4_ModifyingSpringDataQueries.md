
---

# Lesson Notes: Modifying Spring Data Queries Using `@Modifying` Annotation

## 1. Goals

In this lesson, we will learn:

* How to use the `@Modifying` annotation with Spring Data JPA.
* When and why this annotation is necessary for modifying queries.
* How to execute modifying queries safely within transactions.
* How to handle advanced persistence context behaviors like automatic flushing and clearing.

---

## 2. Introduction to `@Modifying`

Spring Data JPA allows the creation of custom queries using the `@Query` annotation. By default, these queries are **select queries**.

If we want to create **queries that modify data** (insert, update, delete) or perform **DDL operations**, Spring needs to handle them differently. That’s where the `@Modifying` annotation comes in.

* **Purpose**: Marks a repository method as a modifying query.
* **Applicable to**:

    * `INSERT`, `UPDATE`, `DELETE` queries.
    * DDL queries like `ALTER TABLE`.

---

## 3. Basic Usage Example

### Deleting Completed Tasks

```java
public interface TaskRepository extends CrudRepository<Task, Long> {

    @Modifying
    @Query("delete Task t where t.status = com.baeldung.lsd.persistence.model.TaskStatus.DONE")
    int deleteCompletedTasks(); // returns number of affected records
}
```

Key points:

* Return type can be `void`, `int`, or `Integer`.
* `@Modifying` is mandatory for any DML/DDL operation.
* Spring does **not automatically detect** modifying queries without this annotation.

### Calling the Method

```java
@Override
@Transactional
public void run(ApplicationArguments args) throws Exception {
    int deletedRecords = taskRepository.deleteCompletedTasks();
    LOG.info("Number of Records Deleted: {}", deletedRecords);
}
```

* `@Transactional` ensures **data integrity** during modifying operations.
* Without `@Transactional`, Spring might throw runtime exceptions.

### Adding Initial Data

```sql
INSERT INTO Task(id, uuid, name, due_date, description, campaign_id, status) 
VALUES (5, uuid(), 'Task 5', '2025-05-17', 'Task 5 Description', 1, 3);
```

* The status `3` corresponds to `TaskStatus.DONE` enum.

---

## 4. What Happens If You Skip `@Modifying`

Removing `@Modifying` results in an error:

```
org.hibernate.hql.internal.QueryExecutionRequestException: Not supported for DML operations
```

Reason:

* Spring defaults to **select queries** for `@Query`.
* Modifying queries bypass standard data access APIs, so Spring needs explicit indication to handle persistence context correctly.

---

## 5. Support for Native Queries

`@Modifying` also works with **native SQL queries**:

```java
public interface WorkerRepository extends CrudRepository<Worker, Long> {

    @Modifying
    @Query(value = "ALTER TABLE Worker ADD COLUMN active INT NOT NULL DEFAULT 1", nativeQuery = true)
    void addActiveColumn();
}
```

* `nativeQuery = true` allows raw SQL execution.
* Can be used for schema-altering operations.

---

## 6. Advanced: Persistence Context Behavior

When modifying entities **within the same transaction**, cached entities in the **first-level persistence context** may not reflect database changes immediately.

### Problem Example

```java
Optional<Task> taskOptional = taskRepository.findById(1L);
if (taskOptional.isPresent()) {
    Task task = taskOptional.get();
    task.setStatus(TaskStatus.DONE);

    int deletedCompletedRecords = taskRepository.deleteCompletedTasks();
    LOG.info("Number of Records Deleted: {}", deletedCompletedRecords);

    Optional<Task> taskCompleted = taskRepository.findById(1L);
    LOG.info("Completed Task: {}", taskCompleted);
}
```

* Task may still appear in logs because it resides in the persistence context, **even though deleted from DB**.

### Solution: Clearing the Persistence Context

```java
@Modifying(clearAutomatically = true)
@Query("delete Task t where t.status = com.baeldung.lsd.persistence.model.TaskStatus.DONE")
int deleteCompletedTasks();
```

* `clearAutomatically = true`: clears persistence context after query execution.
* `flushAutomatically = true` (optional): flushes pending changes before execution.

**Result**: Deleted tasks are no longer accessible in the same transaction.

---

## 7. Summary

* `@Modifying` is **required** for DML/DDL queries in Spring Data JPA.
* Always use `@Transactional` when performing modifying operations.
* Can work with both **JPQL** and **native SQL queries**.
* Advanced flags:

    * `clearAutomatically = true`: clears persistence context.
    * `flushAutomatically = true`: flushes pending changes before execution.
* Without `@Modifying`, Spring assumes queries are select operations, causing runtime errors.

---

### Recommended Next Steps

* Review **persistence context**, **EntityManager**, and **flush modes**.
* Experiment with modifying queries in transactions that also read entities.
* Explore native queries for schema modifications.

---

