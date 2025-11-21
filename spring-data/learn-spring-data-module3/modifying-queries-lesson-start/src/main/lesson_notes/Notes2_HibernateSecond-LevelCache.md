
---

# **📘 LESSON NOTES: Hibernate Second-Level Cache (L2 Cache)**

*(Complete, In-Depth, Production-Level Guide)*

---

# **1. Introduction**

Modern enterprise applications repeatedly read similar reference data—roles, product catalogs, configuration items, metadata. Database trips are often the *slowest* part of any request. Hibernate mitigates this via multilayer caching:

* **First-Level Cache (L1)** – Mandatory, Session-scoped, NOT shared.
* **Second-Level Cache (L2)** – Optional, SessionFactory-scoped, shared across sessions.
* **Query Cache** – Optional, stores *query results* (id lists), not entity states.

The L2 cache boosts performance when entities are read frequently but updated infrequently.

---

# **2. What Is the Hibernate Second-Level Cache?**

Hibernate provides a *cache abstraction*, but **no implementation**.
You plug in a provider: Ehcache, Hazelcast, Infinispan, Redis, NCache, etc.

### **How it works**

When Hibernate loads an entity by ID:

1. **Check L1 (Session cache)**
2. **If not found → check L2 (SessionFactory cache)**
3. **If not found → query DB**
4. Cache returned entity:

    * In L1 (for this session)
    * In L2 (shared for future sessions)

### **Key Characteristics**

| Feature                     | L1 Cache         | L2 Cache         |
| --------------------------- | ---------------- | ---------------- |
| Scope                       | Session          | SessionFactory   |
| Mandatory                   | Yes              | No               |
| Data Shared Across Sessions | ❌ No             | ✅ Yes            |
| Stores Entities?            | Managed Entities | Dehydrated State |
| Survives Session Close?     | ❌ No             | ✅ Yes            |

---

# **3. Why Use a Second-Level Cache?**

### **✔ Reduces DB load**

Repeated lookups (by ID) no longer hit DB.

### **✔ Improves application throughput**

Reduced DB contention → more concurrency.

### **✔ Transparent**

Hibernate manages invalidation & consistency automatically (for normal updates).

### **✔ Works across multiple sessions**

Great for web apps under load.

---

# **4. How Entities Are Stored in L2 Cache**

Entities are **not stored as Java objects.**
They are stored in **"dehydrated”** arrays of primitive values:

```
<entity-id> → [property1, property2, property3, ...]
```

Example:

```
Person L2 Cache Region
1 → ["John", "Q", "Public", null]
2 → ["Joey", "D", "Public", 1]
```

This reduces memory use and prevents sharing live objects between sessions (dangerous).

---

# **5. Configuring Second-Level Cache**

Hibernate requires:

1. **RegionFactory** – the adapter for the provider
2. **Cache provider** – Ehcache, Hazelcast, Infinispan, etc.
3. **Cache configuration file** – e.g., ehcache.xml
4. **Entity annotations** – @Cacheable + @Cache

### **Maven dependencies (Hibernate 6.x + Ehcache)**

```xml
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-jcache</artifactId>
    <version>6.5.2.Final</version>
</dependency>

<dependency>
    <groupId>org.ehcache</groupId>
    <artifactId>ehcache</artifactId>
    <version>3.10.8</version>
    <classifier>jakarta</classifier>
</dependency>
```

---

# **6. Hibernate Properties (L2 Cache Activation)**

```properties
hibernate.cache.use_second_level_cache=true
hibernate.cache.region.factory_class=org.hibernate.cache.jcache.internal.JCacheRegionFactory
hibernate.javax.cache.provider=org.ehcache.jsr107.EhcacheCachingProvider
hibernate.javax.cache.uri=ehcache.xml
```

### **To disable L2 cache**

```properties
hibernate.cache.use_second_level_cache=false
```

---

# **7. Making Entities Cacheable**

### **Entity Example**

```java
@Entity
@Cacheable
@org.hibernate.annotations.Cache(
    usage = CacheConcurrencyStrategy.READ_WRITE
)
public class Foo {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
}
```

### **Collections Need Their Own @Cache**

```java
@OneToMany
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
private Set<Bar> bars;
```

---

# **8. Cache Concurrency Strategies**

| Strategy                 | Characteristics                     | Use Case                                         |
| ------------------------ | ----------------------------------- | ------------------------------------------------ |
| **READ_ONLY**            | Fastest, no updates                 | Static reference data                            |
| **NONSTRICT_READ_WRITE** | Eventual consistency                | Low-update entities where stale reads acceptable |
| **READ_WRITE**           | Strong consistency using soft locks | Frequently-read, occasionally-updated data       |
| **TRANSACTIONAL**        | XA transaction support              | Distributed systems needing atomic cache+DB      |

---

# **9. Cache Region Management (Ehcache Example)**

**ehcache.xml**

```xml
<cache-template name="entities">
    <resources>
        <heap unit="entries">1000</heap>
    </resources>
</cache-template>

<cache alias="com.example.model.Foo" uses-template="entities" />
```

---

# **10. Query Cache**

L2 cache caches **entities by ID**.
Query cache caches **list of IDs for a given query + parameters**.

### Enable Query Cache

```properties
hibernate.cache.use_query_cache=true
```

### Enable Caching for a Specific Query

```java
entityManager.createQuery("from Foo")
     .setHint("org.hibernate.cacheable", true)
     .getResultList();
```

### Important

Query cache stores:

* Query → list of IDs
  But values must exist in **Entity L2 Cache**, or Hibernate issues SELECT per ID.

---

# **11. Cache Invalidation**

### **Hibernate manages invalidation for:**

* Inserts
* Updates
* Deletes
* HQL DML
  → Specific affected entities are evicted.

### **Native SQL** = Invalidates *all regions*

Unless explicitly synchronized:

```java
NativeQuery nq = entityManager
    .createNativeQuery("update FOO set ...");
nq.unwrap(org.hibernate.query.NativeQuery.class)
    .addSynchronizedEntityClass(Foo.class);
nq.executeUpdate();
```

---

# **12. Internal Rules for Cached Collections**

Hibernate caches **only the IDs** of collection elements.

Region naming:

```
EntityName.propertyName
e.g., Foo.bars
```

You should also enable L2 caching for the contained entities.

---

# **13. Pitfalls & Best Practices**

## **PITFALL 1 — Query Cache Creates Many DB Hits**

If query results expire faster than entity cache entries →
Hibernate may perform **1 SELECT per ID**.

✔ Keep query cache TTL ≥ entity cache TTL.

---

## **PITFALL 2 — @Inheritance Issues**

Caching strategy from parent class applies to all subclasses.
Hibernate **ignores subclass @Cache annotations**.

---

## **PITFALL 3 — SingletonEhCacheRegionFactory**

If used, may ignore per-region Ehcache configs.
Use:

✔ EhCacheRegionFactory (recommended)

---

# **14. Verifying Cache Is Working**

```java
@Autowired
EntityManagerFactory emf;

Cache cache = emf.getCache();

Foo foo = fooService.create(new Foo());
fooService.findOne(foo.getId());

assertTrue(cache.contains(Foo.class, foo.getId()));
```

---

# **15. Best Practices Summary**

### ✔ Best Candidates for L2 Cache

* Reference tables (countries, roles, product readonly configs)
* Low-update entities
* Entities often fetched by ID

### ✔ Avoid Caching

* High update tables
* Entities with large graphs that rarely repeat
* Queries with high parameter variability

### ✔ Always Cache Collections + Entities Together

### ✔ Disable eviction on default-update-timestamps-region

Needed by query cache for consistency.

---

# **16. Conclusion**

Hibernate’s second-level cache is a powerful performance tool when used correctly:

* Reduces DB load dramatically
* Shared across sessions
* Transparent operations
* Requires careful configuration
* Must be matched with correct concurrency strategy
* Works best with relatively static reference data

By combining L2 cache + Query Cache + a solid provider such as Ehcache or Hazelcast, applications achieve large scalability improvements.

---