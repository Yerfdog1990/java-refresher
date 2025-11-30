
---

# 📘 **LESSON NOTES – Entity Graphs in Spring Data JPA**

## 1. Introduction

When working with JPA and Spring Data, performance problems often arise because of how entity relationships are fetched. By default, JPA offers two static strategies:

* **FetchType.LAZY** – load the association only when accessed
* **FetchType.EAGER** – load the association immediately

This model is rigid. It doesn’t let you dynamically choose what to fetch depending on your use case. For example:

* Sometimes you want a `Campaign` **with tasks**
* Sometimes you need only the `Campaign` **without tasks**
* Sometimes you need tasks **with assignees**
* Sometimes you need tasks **without assignees**

Changing the fetch type on the entity cannot support these differences dynamically.

### ✔ Entity Graphs solve this problem.

**Entity Graphs allow you to define and apply runtime-specific fetch plans** that determine:

* which associations should be eagerly loaded
* which associations should remain lazy
* all **without modifying your entity mappings**
* and without triggering multiple SELECTs ("N+1 problem")

Entity Graphs make it possible to:

* optimize queries
* minimize SQL round trips
* fetch exactly what the use case requires
* avoid LazyInitializationException
* avoid global EAGER loading

---

## 2. What Are Entity Graphs?

An **Entity Graph** is a declarative fetch plan that tells JPA:

> *When loading this entity, also fetch these specific associations eagerly.*

It defines:

* **attribute nodes** (direct associations to load)
* **subgraphs** (nested associations to load)

Example of what a graph can specify:

```
Campaign
 ├── tasks
 │      └── assignee
 └── manager
```

This means: when you load a `Campaign`, also fetch **tasks**, each task’s **assignee**, and the **campaign manager**.

### Entity Graph Types

| Type      | Meaning                                                                                              |
| --------- | ---------------------------------------------------------------------------------------------------- |
| **FETCH** | Only attributes listed in the graph are fetched eagerly; everything else is forced LAZY              |
| **LOAD**  | Attributes in the graph are fetched eagerly + any fields already annotated as EAGER are also fetched |

Spring Data defaults to **FETCH**.

---

## 3. Why EntityGraphs Are Needed

Because JPA fetch types are **static**, they create problems:

### 3.1 EAGER Problems

* Fetches too much data even when not needed
* Creates huge SELECT queries
* Can cause slowdowns
* Can cause infinite recursion in bidirectional relationships

### 3.2 LAZY Problems

* Triggers extra SQL per association (N+1 problem)
* Can cause `LazyInitializationException` when accessed outside a transaction
* Hard to load nested graphs (Task → Assignee → Department)

### 3.3 What EntityGraphs Enable

✔ Ability to fetch differently for different use cases
✔ One query instead of N+1
✔ No need to change entity annotations
✔ Much safer than making everything EAGER

---

## 4. Basic Model Example

Let’s take the typical domain:

### Campaign

```java
@Entity
public class Campaign {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    private Set<Task> tasks = new HashSet<>();
}
```

### Task

```java
@Entity
public class Task {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    private Worker assignee;
}
```

### Worker

```java
@Entity
public class Worker {
    @Id
    @GeneratedValue
    private Long id;

    private String firstName;
    private String lastName;
}
```

---

## 5. Type 1 — Using JPA’s @NamedEntityGraph (Static Graphs)

You can define a named graph right on the entity.

### Example: Load Campaign WITH Tasks

```java
@Entity
@NamedEntityGraph(
    name = "campaign-with-tasks",
    attributeNodes = {
        @NamedAttributeNode("tasks")
    }
)
public class Campaign {
    ...
}
```

Explanation:

* The name of the graph is `"campaign-with-tasks"`
* The attribute node `"tasks"` means:

  > When this graph is applied, load tasks eagerly.

---

## 6. Type 2 — Using Subgraphs (Nested Fetching)

Now suppose tasks have an assignee.

We want:

Campaign → Tasks → Assignee

```java
@Entity
@NamedEntityGraph(
    name = "campaign-with-tasks-and-assignee",
    attributeNodes = {
        @NamedAttributeNode(value = "tasks", subgraph = "task-assignee")
    },
    subgraphs = {
        @NamedSubgraph(
            name = "task-assignee",
            attributeNodes = {
                @NamedAttributeNode("assignee")
            }
        )
    }
)
public class Campaign {
    ...
}
```

This fetches:

* Campaign
* Campaign.tasks
* Task.assignee

all in **one single SQL query** with joins.

---

## 7. Applying a NamedEntityGraph in Spring Data

```java
public interface CampaignRepository extends CrudRepository<Campaign, Long> {

    @EntityGraph(value = "campaign-with-tasks", type = EntityGraphType.FETCH)
    Iterable<Campaign> findByNameContaining(String name);
}
```

This forces:

* Campaign.tasks to be eagerly loaded
* Everything else stays lazy unless included in the graph

### The generated SQL becomes:

```sql
SELECT ...
FROM campaign
LEFT JOIN task t ON t.campaign_id = campaign.id
WHERE campaign.name LIKE ?
```

Instead of two queries or an N+1 cascade.

---

## 8. Common Issue: LazyInitializationException

Sometimes tasks contain:

```java
@ManyToOne(fetch = FetchType.EAGER)
private Worker assignee;
```

But if the EntityGraphType is **FETCH**, all unspecified relations are forced lazy, meaning EAGER is ignored.

This causes:

```
LazyInitializationException: could not initialize proxy - no Session
```

To fix this, you must:

* Either use EntityGraphType.LOAD
* Or include `assignee` via subgraph
* Or annotate your method/class with @Transactional

---

## 9. Type 3 — Defining Ad-Hoc Entity Graphs (Dynamic Graphs)

Spring Data lets you define graphs inline without NamedEntityGraph.

### Example:

```java
public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = {"assignee", "campaign"})
    List<Task> findByStatus(TaskStatus status);
}
```

This will:

* Fetch Task.assignee
* Fetch Task.campaign
* Keep all other fields lazy

Generated SQL:

```sql
SELECT ...
FROM task
LEFT JOIN campaign ON ...
LEFT JOIN worker ON ...
WHERE status = ?
```

---

Great — here is **PART 2** of the full lesson notes on **Entity Graphs in Spring Data JPA**.
These notes are copy-ready for Markdown documents, structured as a professional learning module, and include all details you asked for.

---

# 10. Using Entity Graphs with Spring Data Repository Methods

Entity Graphs integrate seamlessly with Spring Data repository method signatures.

There are **two ways** to apply entity graphs:

---

## 10.1 Using a Named Entity Graph

```java
@EntityGraph(value = "campaign-with-tasks")
Campaign findById(Long id);
```

This method will always apply the `"campaign-with-tasks"` graph automatically.

---

## 10.2 Using Dynamic Ad-Hoc Graphs

```java
@EntityGraph(attributePaths = {"tasks", "tasks.assignee"})
List<Campaign> findByStatus(Status status);
```

This approach does not require `@NamedEntityGraph` on the entity.
It is fully inline and extremely flexible.

---

# 11. Entity Graph Type: FETCH vs LOAD

Understanding `EntityGraphType.FETCH` vs `EntityGraphType.LOAD` is crucial.

| Type                | Behavior                                                                                                 |
| ------------------- | -------------------------------------------------------------------------------------------------------- |
| **FETCH (default)** | Only attributes in the graph are fetched eagerly; everything else becomes LAZY (even if mapped as EAGER) |
| **LOAD**            | Attributes in the graph are eagerly fetched, **plus** all fields mapped as EAGER                         |

Example:

```java
@EntityGraph(value = "campaign-with-tasks", type = EntityGraphType.LOAD)
Campaign findByCode(String code);
```

This respects the entity’s annotations:

* Default EAGER fields → still EAGER
* Graph fields → EAGER
* Others → LAZY

Use **LOAD** if you want to preserve normal EAGER mappings.

Use **FETCH** (default) if you want full control of what is eagerly loaded.

---

# 12. Using Entity Graphs with EntityManager (Manual JPA Use)

Entity graphs were originally designed for JPA's `EntityManager`.
Spring Data simply exposes them declaratively.

Let’s see how to use them manually.

---

## 12.1 Using EntityManager.find with EntityGraph (FETCH)

```java
EntityGraph<?> graph = em.getEntityGraph("campaign-with-tasks");

Map<String, Object> hints = new HashMap<>();
hints.put("javax.persistence.fetchgraph", graph);

Campaign campaign = em.find(Campaign.class, 1L, hints);
```

This will eagerly fetch only the attributes defined inside the graph.

---

## 12.2 Using EntityManager.find with EntityGraph (LOAD)

```java
hints.put("javax.persistence.loadgraph", graph);
```

This loads thegraph attributes + all EAGER fields.

---

## 12.3 Using Entity Graph with JPQL

```java
EntityGraph<?> graph = em.getEntityGraph("post-entity-graph");

Post post = em.createQuery(
        "SELECT p FROM Post p WHERE p.id = :id", Post.class)
    .setParameter("id", id)
    .setHint("javax.persistence.fetchgraph", graph)
    .getSingleResult();
```

---

## 12.4 Using Entity Graph with Criteria API

```java
EntityGraph<Post> graph = em.getEntityGraph("post-entity-graph-with-comment-users");

CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Post> cq = cb.createQuery(Post.class);
Root<Post> root = cq.from(Post.class);
cq.where(cb.equal(root.get("id"), id));

TypedQuery<Post> query = em.createQuery(cq);
query.setHint("javax.persistence.loadgraph", graph);

Post post = query.getSingleResult();
```

---

# 13. SQL Visualization: How Entity Graphs Reduce Query Count

To see the impact, enable SQL debugging:

```properties
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```

This will show how queries change:

### Without Entity Graph

* 1 query for Campaign
* N queries for Tasks
* N queries for Assignees
  Result:

```
1 + N + N SELECT statements
```

### With Entity Graph

* Single joined query:

```sql
SELECT c.*, t.*, w.*
FROM campaign c
LEFT JOIN task t ON ...
LEFT JOIN worker w ON ...
```

Result:

```
1 SINGLE SELECT
```

Massive performance improvement.

---

# 14. Practical Example: N+1 Problem Solved by EntityGraph

### Scenario:

Display campaigns with tasks and assignees.

### ❌ WITHOUT Entity Graph

```java
List<Campaign> campaigns = repo.findAll();

for (Campaign c : campaigns) {
    for (Task t : c.getTasks()) {
        System.out.println(t.getAssignee().getName());
    }
}
```

Produces:

* 1 SELECT for campaigns
* 1 SELECT per campaign for tasks
* 1 SELECT per task for worker

= **N+1 explosion**

---

### ✔ WITH Entity Graph

Repository:

```java
@EntityGraph(attributePaths = {"tasks", "tasks.assignee"})
List<Campaign> findAll();
```

Result:

```
ONE JOIN QUERY
NO N+1
NO LazyInitializationException
```

---

# 15. Real-World Example: CampaignService (Complete Flow)

This example shows Entity Graphs preventing lazy loading failures in service layers.

### Repository

```java
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    @EntityGraph(value = "campaign-with-tasks-and-assignee")
    Optional<Campaign> findById(Long id);
}
```

### Service Layer

```java
@Service
public class CampaignService {

    @Autowired
    private CampaignRepository repo;

    @Transactional(readOnly = true)
    public Campaign loadWithGraph(Long id) {
        return repo.findById(id).orElseThrow();
    }
}
```

### Controller Layer

```java
@GetMapping("/campaign/{id}")
public ResponseEntity<?> getCampaign(@PathVariable Long id) {
    return ResponseEntity.ok(service.loadWithGraph(id));
}
```

Result:

* Instant availability of nested relations
* No lazy exceptions
* No N+1 queries

---

# 16. Using Entity Graphs to Control Nested Fetching (Subgraphs)

Sometimes you need multi-level control:

Campaign ⇒ Task ⇒ Assignee ⇒ Supervisor

```java
@NamedEntityGraph(
    name = "campaign-full-graph",
    attributeNodes = {
        @NamedAttributeNode(value = "tasks", subgraph = "task-subgraph")
    },
    subgraphs = {
        @NamedSubgraph(
            name = "task-subgraph",
            attributeNodes = {
                @NamedAttributeNode(value = "assignee", subgraph = "worker-subgraph")
            }
        ),
        @NamedSubgraph(
            name = "worker-subgraph",
            attributeNodes = {
                @NamedAttributeNode("supervisor")
            }
        )
    }
)
@Entity
public class Campaign {}
```

Spring Data repository:

```java
@EntityGraph(value = "campaign-full-graph")
List<Campaign> findByActiveTrue();
```

This fetches 4 levels deep in one query.

---

# 17. Entity Graphs vs `JOIN FETCH`

Many developers ask:
“Why use Entity Graphs when JPQL supports JOIN FETCH?”

### JOIN FETCH Example

```java
@Query("SELECT c FROM Campaign c JOIN FETCH c.tasks")
List<Campaign> findAllWithTasks();
```

### Key Differences

| Feature                                    | JOIN FETCH | ENTITY GRAPH |
| ------------------------------------------ | ---------- | ------------ |
| **Declarative / Reusable**                 | No         | Yes          |
| **Dynamic selection of fields**            | No         | Yes          |
| **Supports subgraphs (nested fetch)**      | Hard       | Easy         |
| **Overrides default fetch strategy**       | Yes        | Yes          |
| **Applies to Spring Data derived queries** | No         | Yes          |
| **Clean separation of concerns**           | No         | Yes          |
| **Complex graphs with multiple levels**    | Hard       | Easy         |

### Summary:

JOIN FETCH is good for **single-use**, simple joins.
Entity Graphs are better for **clean reusable fetch plans**.

---

# 18. Common Pitfalls and How to Avoid Them

### ❌ 1. Forgetting to include nested attributes

Leads to LazyInitializationException.

✔ Fix using subgraph.

---

### ❌ 2. Using EntityGraphType.FETCH but expecting EAGER fields to load

FETCH overrides all fetches, including EAGER.

✔ Use EntityGraphType.LOAD if you want to preserve EAGER mappings.

---

### ❌ 3. Applying EntityGraph on a method but also relying on toString()

Your `toString()` accesses lazy fields → causes lazy load outside transaction.

✔ Avoid accessing associations in toString().
✔ Or include those fields in the graph.

---

### ❌ 4. Mixing lazy collections with serialization

JSON serialization tries to access lazy attributes → LazyInitializationException.

✔ Use Entity Graphs
✔ or DTO projections.

---

Great — here is **PART 2** of the full lesson notes on **Entity Graphs in Spring Data JPA**.
These notes are copy-ready for Markdown documents, structured as a professional learning module, and include all details you asked for.

---

# 📘 **Entity Graphs in Spring Data JPA — PART 2**

## (Using Entity Graphs with Queries, Persistence Operations, FETCH vs LOAD, SQL Visualization, Performance, and Examples)

---

# 10. Using Entity Graphs with Spring Data Repository Methods

Entity Graphs integrate seamlessly with Spring Data repository method signatures.

There are **two ways** to apply entity graphs:

---

## 10.1 Using a Named Entity Graph

```java
@EntityGraph(value = "campaign-with-tasks")
Campaign findById(Long id);
```

This method will always apply the `"campaign-with-tasks"` graph automatically.

---

## 10.2 Using Dynamic Ad-Hoc Graphs

```java
@EntityGraph(attributePaths = {"tasks", "tasks.assignee"})
List<Campaign> findByStatus(Status status);
```

This approach does not require `@NamedEntityGraph` on the entity.
It is fully inline and extremely flexible.

---

# 11. Entity Graph Type: FETCH vs LOAD

Understanding `EntityGraphType.FETCH` vs `EntityGraphType.LOAD` is crucial.

| Type                | Behavior                                                                                                 |
| ------------------- | -------------------------------------------------------------------------------------------------------- |
| **FETCH (default)** | Only attributes in the graph are fetched eagerly; everything else becomes LAZY (even if mapped as EAGER) |
| **LOAD**            | Attributes in the graph are eagerly fetched, **plus** all fields mapped as EAGER                         |

Example:

```java
@EntityGraph(value = "campaign-with-tasks", type = EntityGraphType.LOAD)
Campaign findByCode(String code);
```

This respects the entity’s annotations:

* Default EAGER fields → still EAGER
* Graph fields → EAGER
* Others → LAZY

Use **LOAD** if you want to preserve normal EAGER mappings.

Use **FETCH** (default) if you want full control of what is eagerly loaded.

---

# 12. Using Entity Graphs with EntityManager (Manual JPA Use)

Entity graphs were originally designed for JPA's `EntityManager`.
Spring Data simply exposes them declaratively.

Let’s see how to use them manually.

---

## 12.1 Using EntityManager.find with EntityGraph (FETCH)

```java
EntityGraph<?> graph = em.getEntityGraph("campaign-with-tasks");

Map<String, Object> hints = new HashMap<>();
hints.put("javax.persistence.fetchgraph", graph);

Campaign campaign = em.find(Campaign.class, 1L, hints);
```

This will eagerly fetch only the attributes defined inside the graph.

---

## 12.2 Using EntityManager.find with EntityGraph (LOAD)

```java
hints.put("javax.persistence.loadgraph", graph);
```

This loads thegraph attributes + all EAGER fields.

---

## 12.3 Using Entity Graph with JPQL

```java
EntityGraph<?> graph = em.getEntityGraph("post-entity-graph");

Post post = em.createQuery(
        "SELECT p FROM Post p WHERE p.id = :id", Post.class)
    .setParameter("id", id)
    .setHint("javax.persistence.fetchgraph", graph)
    .getSingleResult();
```

---

## 12.4 Using Entity Graph with Criteria API

```java
EntityGraph<Post> graph = em.getEntityGraph("post-entity-graph-with-comment-users");

CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Post> cq = cb.createQuery(Post.class);
Root<Post> root = cq.from(Post.class);
cq.where(cb.equal(root.get("id"), id));

TypedQuery<Post> query = em.createQuery(cq);
query.setHint("javax.persistence.loadgraph", graph);

Post post = query.getSingleResult();
```

---

# 13. SQL Visualization: How Entity Graphs Reduce Query Count

To see the impact, enable SQL debugging:

```properties
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```

This will show how queries change:

### Without Entity Graph

* 1 query for Campaign
* N queries for Tasks
* N queries for Assignees
  Result:

```
1 + N + N SELECT statements
```

### With Entity Graph

* Single joined query:

```sql
SELECT c.*, t.*, w.*
FROM campaign c
LEFT JOIN task t ON ...
LEFT JOIN worker w ON ...
```

Result:

```
1 SINGLE SELECT
```

Massive performance improvement.

---

# 14. Practical Example: N+1 Problem Solved by EntityGraph

### Scenario:

Display campaigns with tasks and assignees.

### ❌ WITHOUT Entity Graph

```java
List<Campaign> campaigns = repo.findAll();

for (Campaign c : campaigns) {
    for (Task t : c.getTasks()) {
        System.out.println(t.getAssignee().getName());
    }
}
```

Produces:

* 1 SELECT for campaigns
* 1 SELECT per campaign for tasks
* 1 SELECT per task for worker

= **N+1 explosion**

---

### ✔ WITH Entity Graph

Repository:

```java
@EntityGraph(attributePaths = {"tasks", "tasks.assignee"})
List<Campaign> findAll();
```

Result:

```
ONE JOIN QUERY
NO N+1
NO LazyInitializationException
```

---

# 15. Real-World Example: CampaignService (Complete Flow)

This example shows Entity Graphs preventing lazy loading failures in service layers.

### Repository

```java
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    @EntityGraph(value = "campaign-with-tasks-and-assignee")
    Optional<Campaign> findById(Long id);
}
```

### Service Layer

```java
@Service
public class CampaignService {

    @Autowired
    private CampaignRepository repo;

    @Transactional(readOnly = true)
    public Campaign loadWithGraph(Long id) {
        return repo.findById(id).orElseThrow();
    }
}
```

### Controller Layer

```java
@GetMapping("/campaign/{id}")
public ResponseEntity<?> getCampaign(@PathVariable Long id) {
    return ResponseEntity.ok(service.loadWithGraph(id));
}
```

Result:

* Instant availability of nested relations
* No lazy exceptions
* No N+1 queries

---

# 16. Using Entity Graphs to Control Nested Fetching (Subgraphs)

Sometimes you need multi-level control:

Campaign ⇒ Task ⇒ Assignee ⇒ Supervisor

```java
@NamedEntityGraph(
    name = "campaign-full-graph",
    attributeNodes = {
        @NamedAttributeNode(value = "tasks", subgraph = "task-subgraph")
    },
    subgraphs = {
        @NamedSubgraph(
            name = "task-subgraph",
            attributeNodes = {
                @NamedAttributeNode(value = "assignee", subgraph = "worker-subgraph")
            }
        ),
        @NamedSubgraph(
            name = "worker-subgraph",
            attributeNodes = {
                @NamedAttributeNode("supervisor")
            }
        )
    }
)
@Entity
public class Campaign {}
```

Spring Data repository:

```java
@EntityGraph(value = "campaign-full-graph")
List<Campaign> findByActiveTrue();
```

This fetches 4 levels deep in one query.

---

# 17. Entity Graphs vs `JOIN FETCH`

Many developers ask:
“Why use Entity Graphs when JPQL supports JOIN FETCH?”

### JOIN FETCH Example

```java
@Query("SELECT c FROM Campaign c JOIN FETCH c.tasks")
List<Campaign> findAllWithTasks();
```

### Key Differences

| Feature                                    | JOIN FETCH | ENTITY GRAPH |
| ------------------------------------------ | ---------- | ------------ |
| **Declarative / Reusable**                 | No         | Yes          |
| **Dynamic selection of fields**            | No         | Yes          |
| **Supports subgraphs (nested fetch)**      | Hard       | Easy         |
| **Overrides default fetch strategy**       | Yes        | Yes          |
| **Applies to Spring Data derived queries** | No         | Yes          |
| **Clean separation of concerns**           | No         | Yes          |
| **Complex graphs with multiple levels**    | Hard       | Easy         |

### Summary:

JOIN FETCH is good for **single-use**, simple joins.
Entity Graphs are better for **clean reusable fetch plans**.

---

# 18. Common Pitfalls and How to Avoid Them

### ❌ 1. Forgetting to include nested attributes

Leads to LazyInitializationException.

✔ Fix using subgraph.

---

### ❌ 2. Using EntityGraphType.FETCH but expecting EAGER fields to load

FETCH overrides all fetches, including EAGER.

✔ Use EntityGraphType.LOAD if you want to preserve EAGER mappings.

---

### ❌ 3. Applying EntityGraph on a method but also relying on toString()

Your `toString()` accesses lazy fields → causes lazy load outside transaction.

✔ Avoid accessing associations in toString().
✔ Or include those fields in the graph.

---

### ❌ 4. Mixing lazy collections with serialization

JSON serialization tries to access lazy attributes → LazyInitializationException.

✔ Use Entity Graphs
✔ or DTO projections.

---

# 19. Creating Dynamic Entity Graphs at Runtime

Sometimes you need a fetch plan that depends on runtime parameters.
JPA allows you to **build entity graphs programmatically** without annotations.

### Example: Dynamic Graph for Campaign

```java
EntityGraph<Campaign> graph = em.createEntityGraph(Campaign.class);
graph.addAttributeNodes("tasks");

Subgraph<Task> taskGraph = graph.addSubgraph("tasks");
taskGraph.addAttributeNodes("assignee");
```

Use it in a query:

```java
Map<String, Object> hints = Map.of(
    "javax.persistence.fetchgraph", graph
);

Campaign c = em.find(Campaign.class, id, hints);
```

### When to use dynamic graphs:

* Conditional fetching based on request parameters
* API endpoints where the client selects fields
* Avoid multiple named graphs for minor variations

---

# 20. Complete Real-World Example: Campaign Management System

We’ll build a **complete flow** using 3 entities:

* **Campaign**
* **Task**
* **Worker**

Relationships:

* Campaign ➝ Task (*OneToMany*)
* Task ➝ Worker (*ManyToOne*)

---

## 20.1 Entities

### Campaign Entity

```java
@NamedEntityGraph(
    name = "campaign.tasks.assignee",
    attributeNodes = {
        @NamedAttributeNode(value = "tasks", subgraph = "task-subgraph")
    },
    subgraphs = {
        @NamedSubgraph(
            name = "task-subgraph",
            attributeNodes = {
                @NamedAttributeNode("assignee")
            }
        )
    }
)
@Entity
public class Campaign {

    @Id @GeneratedValue
    private Long id;

    private String title;

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();
}
```

### Task Entity

```java
@Entity
public class Task {

    @Id @GeneratedValue
    private Long id;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    private Worker assignee;
}
```

### Worker Entity

```java
@Entity
public class Worker {

    @Id @GeneratedValue
    private Long id;

    private String name;
}
```

---

# 20.2 Repository

```java
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    @EntityGraph(value = "campaign.tasks.assignee")
    Optional<Campaign> findDetailedById(Long id);

    @EntityGraph(attributePaths = {"tasks"})
    List<Campaign> findByActiveTrue();
}
```

---

# 20.3 Service Layer

```java
@Service
public class CampaignService {

    @Autowired
    private CampaignRepository repo;

    @Transactional(readOnly = true)
    public Campaign getCampaignFull(Long id) {
        return repo.findDetailedById(id)
                   .orElseThrow(() -> new RuntimeException("Not found"));
    }
}
```

---

# 20.4 Controller Layer

```java
@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    @Autowired
    private CampaignService service;

    @GetMapping("/{id}")
    public Campaign get(@PathVariable Long id) {
        return service.getCampaignFull(id);
    }
}
```

### Result

Zero lazy exceptions.
Zero N+1 queries.
Efficient, predictable SQL queries.

---

# 21. Performance Benchmark: Entity Graphs vs Lazy Loading

### Scenario

Load 1 Campaign with:

* 20 Tasks
* each with 20 Workers

Total items: 400

---

## 21.1 Lazy Loading Without Entity Graph

🟥 **N+1 explosion**

| Task                    | Query Count |
| ----------------------- | ----------- |
| Load Campaign           | 1           |
| Load Tasks              | 1           |
| Load each Task.assignee | 400         |

Total = **402 SQL queries**

---

## 21.2 With Entity Graph

🟩 **One joined SQL query**

```
SELECT c.*, t.*, w.*
FROM campaign c
LEFT JOIN task t ON ...
LEFT JOIN worker w ON ...
WHERE c.id = ?
```

Total = **1 SQL query**

---

## Result

| Approach     | Query Count | Performance      |
| ------------ | ----------- | ---------------- |
| Lazy Loading | 402         | ❌ Very slow      |
| Entity Graph | 1           | ✔ Extremely fast |

Entity graphs remove N+1 problems entirely.

---

# 22. Best Practices for Using Entity Graphs

### ✔ 1. Use EntityGraph on service-facing repository methods

Do not rely on lazy loading in a web application.

### ✔ 2. Prefer EntityGraphType.FETCH unless you *really* want EAGER fields preserved

FETCH gives full explicit control.

### ✔ 3. Do not include huge collections unnecessarily

Fetching large collections via joins may cause:

* Memory pressure
* Cartesian explosion
* Duplicates

Use pagination or batch fetching for big lists.

### ✔ 4. Use subgraphs for complex nested relationships

This keeps fetch plans clean and modular.

### ✔ 5. Avoid accessing lazy relationships in:

* `toString()`
* `equals()`
* `hashCode()`

They can trigger unwanted lazy loads.

### ✔ 6. Prefer Entity Graphs over `JOIN FETCH` for reusable queries

JOIN FETCH is good for one-off cases.

---

# 23. When NOT to Use Entity Graphs

### ❌ 1. When your relationships return thousands or millions of records

Use paging instead of large eager joins.

### ❌ 2. When you want *batch-fetching* instead of *join-fetching*

Hibernate batch fetch is better for huge trees.

### ❌ 3. When using DTO projections

Spring Data projections override fetch types.

### ❌ 4. When the fetch requirements vary too much

Sometimes using dynamic graphs is better.

---

# 24. Visual Diagram: How Entity Graphs Work

```
 ┌────────────────────────┐
 │     Campaign           │
 │ id, title              │
 └──────────┬─────────────┘
            │  (lazy)
            ▼
 ┌────────────────────────┐
 │        Task            │
 │ id, desc               │
 └──────────┬─────────────┘
            │  (lazy)
            ▼
 ┌────────────────────────┐
 │        Worker          │
 │ id, name               │
 └────────────────────────┘
```

### With Entity Graph

```
FETCH these fields:
Campaign.tasks
Task.assignee

Result:
All fetched in ONE JOIN QUERY
```

---

# 25. Final Summary 

Entity Graphs:

* Override lazy loading safely
* Prevent N+1 query performance issues
* Create predictable SQL
* Allow full control of eager vs lazy
* Support nested subgraphs
* Work with repository methods, JPQL, Criteria API, EntityManager
* Replace many JOIN FETCH queries
* Allow dynamic runtime-defined fetch plans

Entity Graphs are one of the **most powerful tools** in Spring Data JPA for optimizing database performance and eliminating lazy initialization issues.

---

