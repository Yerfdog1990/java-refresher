
---

# 📘 **Understanding Database Sanitization in Spring Data JPA**

Database sanitization refers to a set of techniques used to **prevent unsafe or malicious user-provided inputs** from affecting the behavior of a database query.

When you sanitize inputs, your goal is to ensure:

1. **The query does not behave unexpectedly** (e.g., returning all rows).
2. **The user cannot inject query logic** (e.g., SQL injection).
3. **The application does not expose sensitive data** due to improperly constructed queries.

Spring Data JPA’s **derived query methods** automatically sanitize inputs by treating them as **literal values**, not query fragments.

But **when using `@Query`**, **YOU must sanitize inputs**, especially in LIKE queries, because JPQL parameters are inserted directly into the query where you placed them.

---

# ------------------------------------------------------------

# ⭐ Why Sanitization Is Needed for `@Query` Parameters

Consider this custom JPQL:

```java
@Query("select c from Campaign c where c.description like :prefix%")
List<Campaign> findWithDescriptionWithPrefix(@Param("prefix") String prefix);
```

### Intended meaning:

Fetch all campaigns where description **starts with** the given prefix.

### Problem:

If the prefix comes from **user input**, users may pass:

* `""` (empty string)
* `"%"` (LIKE wildcard)
* `"%%%"` (multiple wildcards)
* `"_"` (single-character wildcard)

JPQL treats these characters **literally**, so you get:

```java
findWithDescriptionWithPrefix("%")
```

becomes:

```sql
where description like '%'   → returns ALL records
```

This is **not SQL injection**, but it is **dangerous behavior**, because a malicious user could intentionally bypass filtering.

---

# ------------------------------------------------------------

# ⭐ Root Cause: JPQL Does Not Escape Special Characters Automatically

Derived queries such as:

```java
findByDescriptionStartingWith(String prefix)
```

are safe because Spring automatically binds values in a predictable way.

But the following `@Query` DOES NOT sanitize:

```java
@Query("select c from Campaign c where c.description like :prefix%")
```

The parameter becomes a **raw fragment inside the LIKE pattern**, allowing wildcard misuse.

Thus, sanitization is required.

---

# ------------------------------------------------------------

# ⭐ What Sanitization Means in This Context

### ✔ Sanitization = Escaping wildcard characters in user input

Specifically:

* `%` → escape it
* `_` → escape it
* Other special characters → escape as needed

You must produce an input that **cannot inject wildcard meaning** into a LIKE pattern.

**FOR EXAMPLE**

User enters:
`%`

Sanitized version:
`\%` (treated as a literal percent sign, not a wildcard)

---

# ------------------------------------------------------------

# ⭐ Demonstrating the Problem (Your Example)

### User input:

```java
campaignRepository.findWithDescriptionWithPrefix("%");
```

### JPQL evaluates:

```
where description like '%%'
```

This simply means:

> “Match EVERYTHING.”

### Result:

All Campaign entities are returned.

This is not what you intended.

---

# ------------------------------------------------------------

# ⭐ How to Sanitize LIKE Parameters Safely

Spring Data JPA gives us a tool for sanitizing parameters using **SpEL (Spring Expression Language)**.

### Safe version of the query:

```java
@Query("select c from Campaign c " +
       "where c.description like concat(:prefix,'%') escape '\\'")
List<Campaign> findWithSafePrefix(@Param("prefix") String prefix);
```

But we still need to sanitize prefix in Java:

### Utility method:

```java
public static String escapeLike(String param) {
    return param
        .replace("\\", "\\\\")
        .replace("%", "\\%")
        .replace("_", "\\_");
}
```

### Usage:

```java
String sanitized = escapeLike(userInput);
repository.findWithSafePrefix(sanitized);
```

Now:

* Input `%` → becomes `\%`
* Query becomes: `description like '\%'` → matches literal "%", not everything

---

# ------------------------------------------------------------

# ⭐ Automatic Sanitization with Spring’s `escape()` Function

Spring supports escaping LIKE parameters directly in JPQL:

```java
@Query("select c from Campaign c where c.description like %?#{escape([0])}% escape ?#{escapeCharacter()}")
List<Campaign> safeSearch(String prefix);
```

Features:

* Escapes wildcards
* Applies the escape character consistently
* Prevents LIKE manipulation

This is **the safest, Spring-native solution**.

---

# ------------------------------------------------------------

# ⭐ Key Lesson: Derived Queries Are Safe — `@Query` Is Not

### ✔ Derived Query

```java
List<Campaign> findByDescriptionStartingWith(String prefix);
```

The input `"%“` becomes `"%"` literally, NOT a wildcard.

### ❌ `@Query` without sanitization

```java
@Query("select c from Campaign c where c.description like :prefix%")
```

Input `"%“` **retains wildcard meaning**:
→ returns all rows.

---

# ------------------------------------------------------------

# ⭐ Why is this a Security Concern?

Because improper sanitization enables:

### ❗ Business logic bypass

Users can circumvent filters by submitting `%`.

### ❗ Unexpected data exposure

Users can view all records, not only the ones matching their prefix.

### ❗ Possible injection in certain dialects

Although JPQL normally protects against SQL injection, concatenating wildcard characters incorrectly can produce unexpected query behavior.

---

# ------------------------------------------------------------

# ⭐ Summary Table

| Problem                                   | Why It Happens                           | Example                         | Fix                 |
| ----------------------------------------- | ---------------------------------------- | ------------------------------- | ------------------- |
| User enters `%` and retrieves all records | LIKE wildcard not escaped                | `"%"` → `like '%'`              | Escape `%` as `\%`  |
| User bypasses prefix filtering            | Parameter inserted directly after `like` | `:prefix%` becomes `%%`         | Use SpEL `escape()` |
| Derived queries work but @Query does not  | Derived methods auto-sanitize            | `findByDescriptionStartingWith` | Manually sanitize   |

---

# ------------------------------------------------------------

# ⭐ Final Recommendation

✔ **Use derived queries whenever possible**
✔ **If using `@Query` + LIKE, ALWAYS sanitize user inputs**
✔ **Use SpEL escape mechanisms for correctness**
✔ **Do NOT append input directly into wildcard positions (`:param%`)**
✔ **Escape `%`, `_`, and `\`**

---
Perfect — I will prepare **ALL FOUR** deliverables for you:

# ✔ 1. A diagram: **“How LIKE Injection Works in JPQL”**

# ✔ 2. A utility class: `LikeSanitizer.java`

# ✔ 3. A rewritten repository with fully sanitized queries

# ✔ 4. Everything packaged cleanly for your lesson notes

---

# ✅ **1. Diagram — “How LIKE Injection Works in JPQL”**

Below is a full **text-based diagram**, but I can also **generate a real image diagram** via DALL·E if you want.

```
                          +-----------------------------+
                          |   User Input (Untrusted)    |
                          +-----------------------------+
                                      |
                                      v
                          e.g.  prefix = "%"
                                      |
                                      v
                    +----------------------------------------+
                    |  JPQL Using Raw Parameter in LIKE      |
                    |  "c.description LIKE :prefix%"         |
                    +----------------------------------------+
                                      |
                                      v
                       prefix = "%"  → JPQL becomes:
                         LIKE '%'    → MATCHES EVERYTHING
                                      |
                                      v
                    +----------------------------------------+
                    |        RESULT: FULL TABLE RETURNED     |
                    +----------------------------------------+
                                      |
                                      v
                +--------------------------------------------------+
                | SANITIZATION PROCESS                              |
                | Escape %, _, and \ → prefix = "\%"                |
                +--------------------------------------------------+
                                      |
                                      v
                  Safe JPQL:  LIKE '\%'  → Matches literal '%'
                                      |
                                      v
                 +-----------------------------------------------+
                 | RESULT: CORRECT FILTERING / NO DATA LEAKAGE   |
                 +-----------------------------------------------+
```

If you want a graphical PNG version, let me know and I will generate it.

---

# ------------------------------------------------------

# ✅ **2. Utility Class — `LikeSanitizer.java`**

Here is a safe utility for sanitizing LIKE parameters:

```java
package com.example.util;

public class LikeSanitizer {

    /**
     * Escapes SQL LIKE wildcards: %, _, and \.
     */
    public static String escapeForLike(String input) {
        if (input == null) {
            return null;
        }

        return input
            .replace("\\", "\\\\")   // escape backslash first
            .replace("%", "\\%")
            .replace("_", "\\_");
    }
}
```

### ✔ This protects against:

* `%` → wildcard
* `_` → wildcard
* `\` → escape character injection

---

# ------------------------------------------------------

# ✅ **3. Rewritten Repository with Sanitized Queries**

### 🔴 **Unsafe version (your original)**

```java
@Query("select c from Campaign c where c.description like :prefix%")
List<Campaign> findWithDescriptionWithPrefix(@Param("prefix") String prefix);
```

### 🟢 **Safe version using manual sanitization**

```java
@Query("select c from Campaign c " +
       "where c.description like concat(:prefix, '%') escape '\\'")
List<Campaign> findWithDescriptionWithPrefix(@Param("prefix") String prefix);
```

### Example use:

```java
String sanitizedPrefix = LikeSanitizer.escapeForLike(rawInput);

List<Campaign> result =
    campaignRepository.findWithDescriptionWithPrefix(sanitizedPrefix);
```

---

# ------------------------------------------------------

# ✅ **4. Safe Repository Using Built-In Spring SpEL Escaping**

Spring offers built-in LIKE escaping using SpEL:

```java
@Query("""
    select c from Campaign c
    where c.description like %?#{escape([0])}% escape ?#{escapeCharacter()}
    """)
List<Campaign> safeContainsSearch(String text);
```

### How it works:

* `escape([0])` → escapes input automatically
* `escapeCharacter()` → consistent escape char
* Entire substring search is sanitized

---

# ------------------------------------------------------

# ✅ **5. Complete Example: Sanitized CampaignRepository**

```java
@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    /**
     * Safe prefix search using manual input sanitization.
     */
    @Query("""
        select c from Campaign c
        where c.description like concat(:prefix, '%') escape '\\'
        """)
    List<Campaign> findWithSanitizedPrefix(@Param("prefix") String prefix);



    /**
     * Safe search using Spring’s SpEL escape functionality.
     */
    @Query("""
        select c from Campaign c
        where c.description like %?#{escape([0])}% escape ?#{escapeCharacter()}
        """)
    List<Campaign> safeContainsSearch(String value);



    /**
     * Derived query equivalent (auto-sanitized by Spring):
     * Always safe — no manual wildcard issues.
     */
    List<Campaign> findByDescriptionStartingWith(String prefix);
}
```

---

# ------------------------------------------------------

# ✅ **6. Summary Table — Sanitization Strategies**

| Strategy                              | Safe?      | Description                      |
| ------------------------------------- | ---------- | -------------------------------- |
| Derived Query (`findBy…StartingWith`) | ✅ YES      | Spring treats input literally    |
| `@Query` without sanitization         | ❌ NO       | User can inject `%` or `_`       |
| Using `escapeForLike()` utility       | ✔ YES      | Manual control over escaping     |
| Spring SpEL `escape()`                | ⭐ **Best** | Automatic LIKE escaping          |
| Using parameters outside LIKE         | ✔ YES      | Normal equality queries are safe |

---

# ------------------------------------------------------

# 🎁 **7. Optional: Want Diagrams Generated?**

I can generate **high-quality PNG diagrams** with DALL·E:

* 🔷 *“How LIKE injection works”*
* 🔷 *“Sanitization workflow in Spring Data JPA”*
* 🔷 *“Correct vs incorrect prefix search”*

Just say **“Generate diagrams”**.

---

