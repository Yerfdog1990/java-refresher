
---

# 📘 **Spring Data JPA: Querydsl (Query DSL)**

## 1. Introduction

**Querydsl** is a powerful, open-source library for constructing **type-safe**, **SQL-like**, fluent database queries in Java.
Instead of writing fragile string-based JPQL/HQL queries, Querydsl allows you to build queries using:

* **Generated metamodel classes (“Q-types”)**
* **A fluent API**
* **Compile-time safety**
* **IDE auto-completion**

Querydsl supports many backends (JPA, SQL, MongoDB, Lucene, JDO, Hibernate Search), but this lesson focuses on **Querydsl with Spring Data JPA**.

---

## 2. Why Querydsl?

### ✔ Benefits

* **Type-safe queries** (no runtime errors due to misspelled fields)
* **Auto-completion** for all entity names and fields
* **Fluent & readable API**
* Supports **dynamic queries**
* Fully integrates with **Spring Data JPA**

### ✔ Compared to JPQL and Criteria API

| Query Type             | Pros                                                      | Cons                                   |
| ---------------------- | --------------------------------------------------------- | -------------------------------------- |
| **JPQL / HQL Strings** | Simple for small queries                                  | Not type-safe, prone to runtime errors |
| **Criteria API**       | Type-safe                                                 | Very verbose, hard to read             |
| **Querydsl**           | Type-safe, readable fluent syntax, dynamic query building | Requires generating Q-types            |

---

## 3. Installing Querydsl

### 3.1 Maven Dependencies (Spring Boot 3 / Spring Data 3 — Jakarta)

```xml
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
    <version>${querydsl.version}</version>
    <classifier>jakarta</classifier>
    <scope>provided</scope>
</dependency>

<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
    <version>${querydsl.version}</version>
    <classifier>jakarta</classifier>
</dependency>
```

Set version in:

```xml
<properties>
    <querydsl.version>5.0.0</querydsl.version>
</properties>
```

---

### 3.2 Generating Q-Types (APT processing)

Querydsl generates meta-model classes (QUser, QTask, QCampaign…) inside:

```
target/generated-sources/annotations
```

To enable this, add:

```xml
<plugin>
    <groupId>com.mysema.maven</groupId>
    <artifactId>apt-maven-plugin</artifactId>
    <version>1.1.3</version>
    <executions>
        <execution>
            <goals>
                <goal>process</goal>
            </goals>
            <configuration>
                <outputDirectory>target/generated-sources/annotations</outputDirectory>
                <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Generate the classes:

```
mvn clean compile
```

---

## 4. Understanding Q-Types (Generated Classes)

Given an entity:

```java
@Entity
public class Task {
    @Id Long id;
    private String name;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne
    private Campaign campaign;
}
```

Querydsl generates:

```java
public class QTask extends EntityPathBase<Task> {
    public static final QTask task = new QTask("task");
    public final StringPath name;
    public final DatePath<LocalDate> dueDate;
    public final EnumPath<TaskStatus> status;
    public final QCampaign campaign;
}
```

The fields are strongly typed:

* `StringPath`
* `DatePath<LocalDate>`
* `EnumPath<TaskStatus>`
* `QCampaign` for relations

---

## 5. Enabling Querydsl in Spring Data

```java
public interface TaskRepository
        extends CrudRepository<Task, Long>,
                QuerydslPredicateExecutor<Task> {
}
```

`QuerydslPredicateExecutor` adds:

```java
Optional<T> findOne(Predicate predicate);
Iterable<T> findAll(Predicate predicate);
long count(Predicate predicate);
boolean exists(Predicate predicate);
Iterable<T> findAll(Predicate predicate, Sort sort);
Iterable<T> findAll(Predicate predicate, OrderSpecifier<?>... orders);
```

---

# 6. Building Predicates (Querydsl Conditions)

Predicates represent the query filters.

Create a reusable predicate class:

```java
public final class TaskPredicates {
    private TaskPredicates() {}

    public static BooleanExpression hasStatus(TaskStatus status) {
        return QTask.task.status.eq(status);
    }

    public static BooleanExpression nameContains(String text) {
        return QTask.task.name.containsIgnoreCase(text);
    }

    public static BooleanExpression dueBefore(LocalDate date) {
        return QTask.task.dueDate.before(date);
    }

    public static BooleanExpression campaignCodeEquals(String code) {
        return QTask.task.campaign.code.eq(code);
    }
}
```

---

# 7. Performing CRUD Operations with Querydsl

## 7.1 CREATE (insert)

Querydsl **cannot** insert entities (JPA limitation).
Use EntityManager:

```java
Task task = new Task(...);
entityManager.persist(task);
```

---

## 7.2 READ — Query Examples

### Example 1 — Find tasks with status = IN_PROGRESS

```java
Predicate predicate =
    QTask.task.status.eq(TaskStatus.IN_PROGRESS);

Iterable<Task> tasks = taskRepository.findAll(predicate);
```

---

### Example 2 — Combine Predicates (status + name contains)

```java
Predicate predicate = 
    TaskPredicates.hasStatus(IN_PROGRESS)
    .and(TaskPredicates.nameContains("deploy"));

Iterable<Task> tasks = taskRepository.findAll(predicate);
```

---

### Example 3 — Sorting

```java
OrderSpecifier<LocalDate> sort =
    QTask.task.dueDate.desc();

Iterable<Task> tasks = taskRepository.findAll(sort);
```

---

### Example 4 — Using Nested Properties

```java
Predicate predicate = 
    QTask.task.campaign.code.eq("C2");

Iterable<Task> tasks = taskRepository.findAll(predicate);
```

---

## 7.3 UPDATE (using Querydsl)

Querydsl allows:

```java
queryFactory.update(QTask.task)
    .where(QTask.task.id.eq(1L))
    .set(QTask.task.status, TaskStatus.DONE)
    .set(QTask.task.name, "Completed task")
    .execute();
```

**Notes:**

* No need to load entity first.
* Executes a bulk update (bypasses JPA dirty checking).
* Must clear persistence context afterwards:

```java
entityManager.clear();
```

---

## 7.4 DELETE

```java
queryFactory.delete(QTask.task)
    .where(QTask.task.status.eq(TaskStatus.CANCELLED))
    .execute();
```

**Warning:**
Omitting `.where()` deletes **all rows**.

---

# 8. Using `JPAQueryFactory` Directly

```java
@Autowired EntityManager em;

public List<Task> findOverdueTasks() {

    JPAQueryFactory query = new JPAQueryFactory(em);
    QTask task = QTask.task;

    return query.selectFrom(task)
            .where(task.dueDate.before(LocalDate.now()))
            .orderBy(task.dueDate.asc())
            .fetch();
}
```

---

# 9. Complex Queries

## 9.1 INNER JOIN

```java
QTask task = QTask.task;
QCampaign campaign = QCampaign.campaign;

List<Task> tasks = queryFactory
    .selectFrom(task)
    .innerJoin(task.campaign, campaign)
    .where(campaign.code.eq("C1"))
    .fetch();
```

---

## 9.2 Subquery

Find tasks assigned to a worker with email:

```java
QWorker worker = QWorker.worker;
QTask task = QTask.task;

List<Task> tasks = queryFactory.selectFrom(task)
    .where(task.assignee.id.in(
        JPAExpressions.select(worker.id)
            .from(worker)
            .where(worker.email.eq("john@test.com"))
    ))
    .fetch();
```

---

## 9.3 Grouping + Aggregation

Count tasks per campaign:

```java
QTask task = QTask.task;

List<Tuple> results = queryFactory
    .select(task.campaign.code, task.id.count())
    .from(task)
    .groupBy(task.campaign.code)
    .orderBy(task.id.count().desc())
    .fetch();
```

---

# 10. How Spring Data Uses Querydsl

You can write a dynamic REST search API:

```java
@GetMapping("/tasks")
public Iterable<Task> search(@QuerydslPredicate(root = Task.class)
                             Predicate predicate) {
    return taskRepository.findAll(predicate);
}
```

Users can pass parameters directly as URL query parameters:

```
/tasks?name=Test&status=IN_PROGRESS
```

---

# 11. Drawbacks & Limitations

### ❗ Drawbacks

* Requires **annotation processing** to generate Q-types.
* Must understand **JPA SQL** to write efficient queries.
* Bulk update/delete bypass JPA’s state management.
* Still an **external library** – some enterprises disallow it.

### ❗ Limitations

* Cannot perform INSERT queries via Querydsl JPA.
* Cannot join unrelated entities without JPQL.
* More complex for beginners than JPQL.

---

# 12. Summary

Querydsl provides:

* Type-safe, fluent, readable queries
* Dynamic query building
* Full Spring Data JPA integration
* Powerful filtering, sorting, grouping, and joins
* Support for bulk updates and deletes

It is one of the best choices for complex query scenarios where:

* Criteria API is too verbose
* JPQL becomes messy
* Specifications become hard to maintain

---
