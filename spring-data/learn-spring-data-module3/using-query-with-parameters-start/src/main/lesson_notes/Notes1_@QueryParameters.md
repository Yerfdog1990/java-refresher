
---

# 📘 **Lesson Notes: Using `@Query` with Parameters in Spring Data JPA**

---

# ⭐ **1. Introduction**

Spring Data JPA’s `@Query` annotation allows developers to write **custom JPQL or SQL queries** directly inside repository interfaces—without implementing the methods manually.

Basic example:

```java
@Query("select c from Campaign c where c.name='Campaign 3' and c.description='About Campaign 3'")
List<Campaign> findWithNameAndDescription();
```

This works, but it has a major problem:

❌ The filter values are hardcoded → No runtime flexibility
✔ Real applications need **dynamic parameters**

Therefore, Spring Data JPA provides **two parameter binding approaches**:

1. **Positional parameters** (`?1`, `?2`, ...)
2. **Named parameters** (`:name`, `:description`)

We will explore these in depth.

---

# ⭐ **2. Positional Parameters**

Positional parameters bind query parameters based on their **numeric order**.

### ✔ Syntax

`?1`, `?2`, `?3`, ...

### Example

```java
@Query("select c from Campaign c where c.name=?1 and c.description=?2")
List<Campaign> findWithNameAndDescriptionPositionalBind(String name, String description);
```

### How binding works

* Method parameter 1 → `?1`
* Method parameter 2 → `?2`

### Usage

```java
List<Campaign> campaigns =
    campaignRepository.findWithNameAndDescriptionPositionalBind(
            "Campaign 3", "About Campaign 3");
```

### Drawbacks of positional parameters

* Harder to maintain
* Changing argument order breaks query
* Hard to know what `?5` refers to
* Prone to human error

👉 Good for small queries, but not scalable for large ones.

---

# ⭐ **3. Named Parameters**

Named parameters bind using **semantic names** rather than position.

### ✔ Syntax

`:name`, `:description`, etc.

### Example

```java
@Query("select c from Campaign c where c.name=:name and c.description=:description")
List<Campaign> findWithNameAndDescriptionNamedBind(
        @Param("description") String description,
        @Param("name") String name);
```

### Why this is better

* Order of parameters **does not matter**
* More readable and maintainable
* Less risk when refactoring method signatures

### Using without `@Param`

```java
@Query("select c from Campaign c where c.name=:name and c.description=:description")
List<Campaign> findWithNameAndDescriptionNamedBind(String description, String name);
```

⚠ **Warning:**
This relies on Java 8’s `-parameters` compiler option. Using `@Param` is safer.

---

# ⭐ **4. Using Parameters in IN Queries**

Spring Data JPA supports using a collection parameter in an `IN` clause.

### Example (positional)

```java
@Query("from Campaign c where c.code in ?1")
List<Campaign> findWithCodeIn(Collection<String> codes);
```

Usage:

```java
campaignRepository.findWithCodeIn(Set.of("C2", "C3"));
```

This binds `?1` to a **collection**, producing:

```sql
where c.code in ('C2','C3')
```

✔ Useful for batch lookup
✔ Works with both positional + named parameters

---

# ⭐ **5. Using Parameters in LIKE Queries**

LIKE queries support pattern matching using `%` and `_`.

### Simple “contains” search

```java
@Query("from Campaign c where c.description like %:keyword%")
List<Campaign> findWithDescriptionIsLike(@Param("keyword") String keyword);
```

Usage:

```java
campaignRepository.findWithDescriptionIsLike("About");
```

### Prefix + suffix LIKE example (incorrect)

```java
@Query("select c from Campaign c where c.description like :prefix%:suffix")
```

🚫 This fails because Spring rewrites `%` and parameter boundaries incorrectly.

### Correct version using `CONCAT`

```java
@Query("select c from Campaign c where c.description like CONCAT(:prefix, '%', :suffix)")
List<Campaign> findWithDescriptionWithPrefixAndSuffix(
        @Param("prefix") String prefix,
        @Param("suffix") String suffix);
```

✔ Reliable
✔ JPQL-safe
✔ Works with multiple parameters

---

# ⭐ **6. Using Parameters in Native Queries**

For SQL queries (not JPQL), set:

```java
@Query(value = "...", nativeQuery = true)
```

### Example

```java
@Query(value = "select * from campaign c where length(c.description) < :length", nativeQuery = true)
List<Campaign> findWithDescriptionIsShorterThan(@Param("length") int len);
```

Usage:

```java
campaignRepository.findWithDescriptionIsShorterThan(16);
```

### Notes

* Named parameters **may not** be supported by all database engines
* Bindings behave similarly to JPQL
* Native functions (e.g., `LENGTH`) vary between SQL dialects

---

# ⭐ **7. Parameter Sanitization (VERY Important)**

Derived queries sanitize parameters automatically.
But `@Query` **does NOT** handle sanitization for you.

### Example of UNSAFE query

```java
@Query("select c from Campaign c where c.description like :prefix%")
List<Campaign> findWithDescriptionWithPrefix(@Param("prefix") String prefix);
```

### If user enters:

```java
"%"
```

Query becomes:

```sql
where description like '%'
```

➡ Returns **EVERY row in the table**
➡ This is a form of **LIKE injection**
➡ Security risk

### Sanitization is required when:

* Input comes from UI/forms
* Query involves LIKE
* Query includes concatenation

Use:

✔ Manual escaping
✔ Spring's `escape()` SpEL function
✔ Input validation before binding

---

# ⭐ **8. Understanding Java Method Parameter Reflection**

Named parameters without `@Param` require access to **method parameter names at runtime**.

Enable this with the compiler flag:

```xml
<compilerArgument>-parameters</compilerArgument>
```

Then reflection can retrieve parameter names:

```java
Parameter p = method.getParameters()[0];
p.getName(); // returns "fullName"
```

⚠ Without this flag, names become `arg0`, `arg1`, …

---

# ⭐ **9. Why Use Query Parameters? (Best Practices Section)**

Using query parameters offers:

### ✔ Security

Protects against JPQL injection.

### ✔ Query Caching

Parameterized queries are cached and reused.

### ✔ Maintainability

No string concatenation
No dynamic query construction issues

### ✔ Flexibility

Works with:

* IN clauses
* LIKE patterns
* Native SQL
* JPQL functions

---

# ⭐ **10. Summary Table**

| Feature                  | Positional Params | Named Params |
| ------------------------ | ----------------- | ------------ |
| Readability              | ❌ Poor            | ⭐ Excellent  |
| Order sensitivity        | ⭐ Yes             | ❌ No         |
| Suitable for many params | ❌ No              | ⭐ Yes        |
| Easy to refactor         | ❌ No              | ⭐ Yes        |
| Recommended?             | Sometimes         | ⭐ Preferred  |

---

# ⭐ **11. Full Repository Example (All Types of Parameters)**

```java
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    // Positional
    @Query("select c from Campaign c where c.name=?1 and c.description=?2")
    List<Campaign> findWithNameAndDescriptionPositionalBind(String name, String description);

    // Named
    @Query("select c from Campaign c where c.name=:name and c.description=:description")
    List<Campaign> findWithNameAndDescriptionNamedBind(
            @Param("name") String name,
            @Param("description") String description);

    // IN clause
    @Query("from Campaign c where c.code in ?1")
    List<Campaign> findWithCodeIn(Collection<String> codes);

    // LIKE contains
    @Query("from Campaign c where c.description like %:keyword%")
    List<Campaign> findWithDescriptionIsLike(@Param("keyword") String keyword);

    // LIKE prefix + suffix
    @Query("select c from Campaign c where c.description like CONCAT(:prefix, '%', :suffix)")
    List<Campaign> findWithDescriptionWithPrefixAndSuffix(
            @Param("prefix") String prefix,
            @Param("suffix") String suffix);

    // Native query
    @Query(value = "select * from campaign c where length(c.description) < :length", nativeQuery = true)
    List<Campaign> findWithDescriptionIsShorterThan(@Param("length") int len);

    // UNSAFE example (requires sanitization)
    @Query("select c from Campaign c where c.description like :prefix%")
    List<Campaign> findWithDescriptionWithPrefix(@Param("prefix") String prefix);
}
```

---
