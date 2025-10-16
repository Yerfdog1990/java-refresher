
# **Lazy vs. Eager Fetching in JPA and Hibernate**

## **1. Overview**

When working with entity relationships, JPA and Hibernate offer different strategies to control **how and when related data is loaded** from the database.

We’ll use practical examples to explore the two primary fetching strategies, understand their differences, and highlight the performance implications of each approach.

The relevant module to import when starting this lesson is:
**`lazy-vs-eager-fetching-start`**

For a fully implemented reference version, import:
**`lazy-vs-eager-fetching-end`**

---

## **2. How Hibernate Loads Associations**

When we define relationships between entities in JPA—such as a `Campaign` having many `Task` records—we must also decide **when Hibernate should load the associated data**.

This decision is controlled by **fetch types**, which significantly affect both performance and memory usage.

There are two main fetching strategies:

* **Lazy Fetching**: Delays loading associated data until it’s needed or explicitly accessed.
  Example: If we load a `Campaign` but never access its tasks, Hibernate won’t run a query for them.
* **Eager Fetching**: Hibernate retrieves related entities **immediately** along with the parent, even if we never use them.

---

## **3. Lazy Fetching**

### **3.1 Setup**

To explore lazy fetching, we’ve set up a test environment that allows us to observe when and how SQL queries are triggered.
This setup is already defined in the **Start module**.

We first enable Hibernate’s statistics collection in `persistence.xml`:

```xml
<property name="hibernate.generate_statistics" value="true"/>
```

Setting `hibernate.generate_statistics` to `true` allows Hibernate to collect internal metrics such as the number of SQL statements executed.

Two JUnit test classes are prepared:

* **LazyFetchUnitTest**
* **EagerFetchUnitTest**

Each creates one `Worker` and ten `Campaign` entities, each linked to a `Task`.
Data and statistics are reset before each test for clean execution.

---

### **3.2 The Test Case**

For the lazy fetch case, the `Campaign` entity is configured as follows:

```java
@Entity
class Campaign {
    // other fields ...

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    private Set<Task> tasks = new HashSet<>();

    // standard getters and setters ...
}
```

Although `@OneToMany` is lazy by default, we include `fetch = FetchType.LAZY` explicitly for clarity.

**Test Example:**

```java
@Test
void whenAccessingTasksLazily_thenTwoSelectsExecute() {
    // when
    Campaign campaign = em.find(Campaign.class, 1L); // first SELECT (campaign)
    campaign.getTasks().size(); // second SELECT (tasks)

    // then
    assertEquals(2, stats.getPrepareStatementCount());
}
```

Here:

* The first query loads the `Campaign`.
* The second query fetches `Task` records when `getTasks()` is called.

**SQL Output:**

```sql
select ... from Campaign c1_0 where c1_0.id=?
select ... from Task t1_0 
left join Worker a1_0 on a1_0.id=t1_0.assignee_id 
where t1_0.campaign_id=?
```

This shows that **lazy loading** defers data retrieval until needed, preventing unnecessary queries.

> ⚠️ Lazy collections can only be initialized while the **persistence context is open**.
> Accessing them afterward throws a `LazyInitializationException`.

To avoid issues when entities are passed across layers, it’s often better to use **DTOs (Data Transfer Objects)** instead of exposing JPA entities directly.

---

## **4. Eager Fetching**

Now let’s explore **eager fetching**—the opposite of lazy loading.

The mapping in `Campaign` changes to:

```java
@OneToMany(mappedBy = "campaign", fetch = FetchType.EAGER)
private Set<Task> tasks = new HashSet<>();
```

Here, Hibernate loads all tasks **immediately** when the campaign is fetched.

We disable the `LazyFetchUnitTest` and create an eager loading test in `EagerFetchUnitTest`:

```java
@Test
void whenMappingIsEager_thenSingleSelectExecutes() {
    // because tasks() is EAGER, this call fetches campaign + tasks in one round trip
    Campaign campaign = em.find(Campaign.class, 1L);
    assertFalse(campaign.getTasks().isEmpty());

    // one SQL statement – either a join or an immediate secondary select
    assertEquals(1, stats.getPrepareStatementCount());
}
```

**SQL Output:**

```sql
select ... 
from Campaign c1_0 
left join Task t1_0 on c1_0.id=t1_0.campaign_id 
left join Worker a1_0 on a1_0.id=t1_0.assignee_id 
where c1_0.id=?
```

Hibernate uses a **LEFT JOIN** to load the `Campaign`, its `Task` entities, and even the `Worker` records in a single query.
This confirms that eager fetching retrieves **all related data in one round trip**.

---

## **5. Performance Aspects**

### **5.1 The N+1 Select Problem with Lazy Fetching**

When loading a list of campaigns and later accessing their tasks, each access triggers a new query.

If we load **N campaigns**, Hibernate performs **N+1 queries**—one for the campaigns and one per campaign’s tasks.

**Test Example:**

```java
@Test
public void whenAccessingTasksLazily_thenNPlus1ProblemOccurs() {
    String selectQuery = "SELECT c FROM Campaign c";

    List campaigns = em.createQuery(selectQuery, Campaign.class)
      .getResultList(); // first SELECT (campaigns)

    for (Campaign c : campaigns) {
        c.getTasks().size(); // each line emits a SELECT tasks where campaign_id = ?
    }

    assertEquals(11, stats.getPrepareStatementCount());
}
```

With 10 campaigns, we get 11 queries—one for campaigns, ten for their tasks—leading to **poor performance** on large datasets.

---

### **5.2 Over-Fetching with Eager Fetching**

Eager fetching loads **all related entities immediately**, often using joins.
While it prevents multiple queries, it can lead to **over-fetching**—retrieving unnecessary data.

This can increase:

* Memory usage
* Query execution time
* Result set size

Example: Loading a `Campaign` with many `Task` entities and their nested associations might result in massive joins and wasted resources.

---

### **5.3 Best Practices**

Understanding how Hibernate fetches related data helps design a **more efficient and robust persistence layer**.

JPA defines sensible defaults:

* `@ManyToOne` and `@OneToOne` → **EAGER** by default
* `@OneToMany` and `@ManyToMany` → **LAZY** by default

These defaults work well in most cases but might need adjustments.
For example:

* A **reporting screen** may require eager loading.
* A **summary view** may prefer lazy loading.

✅ **Recommended Practice:**
Keep collections **lazy by default** and use eager fetching **only when consistently needed**.

---

### **5.4 Advanced Fetch Optimization Techniques**

JPA provides several optimization mechanisms without changing entity mappings:

| **Technique**                                             | **Purpose**                                                       |
| --------------------------------------------------------- | ----------------------------------------------------------------- |
| **JOIN FETCH**                                            | Load related entities in a single query                           |
| **Entity Graphs**                                         | Dynamically control fetch plans without altering JPQL or mappings |
| **Batch Fetching (`hibernate.default_batch_fetch_size`)** | Load multiple lazy collections in fewer queries                   |

These techniques are beyond the scope of this lesson, but they are essential tools for improving fetch performance and flexibility in real-world applications.

---
