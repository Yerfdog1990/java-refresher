
---

# 📘 **Step-By-Step Tutorial: Database Parameter Sanitization in Spring Data JPA**

---

# 🎯 **Learning Objectives**

By the end of this tutorial, students should be able to:

✅ Understand what SQL/JPQL LIKE injection is
✅ Explain why derived query methods do *not* need extra sanitization
✅ Explain why `@Query` methods *do*
✅ Implement parameter sanitization for LIKE queries
✅ Use Spring's built-in sanitizing (`escape()`)
✅ Write safe repository methods
✅ Test sanitization behavior

---

# ------------------------------------------------------------

# 🟦 **Step 1 — Explain the Problem: What Is LIKE Injection?**

Before writing any code, teach the student what the danger is.

### ❌ Unsafe behavior:

A user enters the search prefix:

```
%
```

The JPQL WHERE clause becomes:

```
WHERE description LIKE '%'
```

This matches **every row in the table**, leaking data.

### Why it happens:

* `%` and `_` are wildcards in SQL LIKE
* JPQL will **not** escape them automatically
* So user-controlled input can change the query logic

### ✔ Key Concept

**User input used inside a LIKE clause must be sanitized.**

---

# ------------------------------------------------------------

# 🟦 **Step 2 — Show the Unsafe Repository Method**

```java
@Query("select c from Campaign c where c.description like :prefix%")
List<Campaign> findWithDescriptionWithPrefix(@Param("prefix") String prefix);
```

### Problems:

❌ `%` appended directly
❌ No escaping
❌ A prefix of `%` or `_` breaks filtering
❌ Attackers can dump the entire table

---

# ------------------------------------------------------------

# 🟦 **Step 3 — Demonstrate the Unsafe Result**

Run:

```java
campaignRepository.findWithDescriptionWithPrefix("%");
```

Output:

```
find all Campaigns...
[id=1, ...]
[id=2, ...]
[id=3, ...]
```

Explain to the student that this is **a form of LIKE injection**.

---

# ------------------------------------------------------------

# 🟦 **Step 4 — Teach How to Sanitize LIKE Parameters**

Create a sanitizer class:

```java
public class LikeSanitizer {

    // Escape: %, _, and \
    public static String escapeForLike(String input) {
        if (input == null) {
            return null;
        }

        return input
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_");
    }
}
```

### Why escape these?

* `%` → wildcard
* `_` → wildcard
* `\` → escape marker
  → These can alter your query logic

---

# ------------------------------------------------------------

# 🟦 **Step 5 — Create a Safe JPQL Query**

```java
@Query("""
    select c from Campaign c
    where c.description like concat(:prefix, '%') escape '\\'
    """)
List<Campaign> findWithSanitizedPrefix(@Param("prefix") String prefix);
```

And use it:

```java
String safeInput = LikeSanitizer.escapeForLike(userInput);

return campaignRepository.findWithSanitizedPrefix(safeInput);
```

Now:

* Input of `%` becomes `\%`
* JPQL treats it as a **literal** percent sign
* NO unexpected matches

---

# ------------------------------------------------------------

# 🟦 **Step 6 — Teach the Better Alternative: Derived Queries**

Explain:

Derived query methods are **safe by default**.

Example:

```java
List<Campaign> findByDescriptionStartingWith(String prefix);
```

Derived queries **do not allow injection**, because:

* Spring binds values safely
* Spring does not concatenate the input into JPQL
* Parameters are treated as literals

This is the **best practice whenever possible**.

---

# ------------------------------------------------------------

# 🟦 **Step 7 — Teach the BEST Method: Spring SpEL LIKE Escaping**

Spring offers a built-in mechanism:

```java
@Query("""
    select c from Campaign c
    where c.description like %?#{escape([0])}% escape ?#{escapeCharacter()}
    """)
List<Campaign> safeContainsSearch(String value);
```

Advantages:

* Automatic escaping
* No need for manual sanitizer class
* Based on Spring Data’s `Escape` utility

This is the recommended method for complex queries.

---

# ------------------------------------------------------------

# 🟦 **Step 8 — Compare All Approaches (Teaching Table)**

| Method                                          | Safe?      | Notes                        |
| ----------------------------------------------- | ---------- | ---------------------------- |
| Derived Query (`findByDescriptionStartingWith`) | ⭐ **YES**  | Easiest and safest           |
| `@Query` with raw parameters                    | ❌ NO       | Vulnerable to LIKE injection |
| Manual escape via utility class                 | ✔ YES      | Works everywhere             |
| SpEL escape (`escape()`)                        | ⭐ **BEST** | Built-in and robust          |
| Binding parameters in ordinary `=` query        | ✔ YES      | No LIKE wildcards            |

---

# ------------------------------------------------------------

# 🟦 **Step 9 — Add Integration Tests (Teaching Moment)**

Write a test:

```java
@Test
@Transactional
public void whenPassingPercent_ThenSanitizedCorrectly() {

    String unsafe = "%";
    String sanitized = LikeSanitizer.escapeForLike(unsafe);

    List<Campaign> result =
            campaignRepository.findWithSanitizedPrefix(sanitized);

    assertThat(result).isEmpty(); // No more unintended matches!
}
```

### Teaching Notes:

* Show how the test confirms sanitization
* Reinforce the concept of predictable behavior

---

# ------------------------------------------------------------

# 🟦 **Step 10 — Class Discussion Questions**

1. Why are LIKE queries more dangerous than equality queries?
2. Why don’t derived queries need sanitization?
3. Why is concatenating user input into JPQL unsafe?
4. What is the difference between `%` and `_` in LIKE?
5. When should you prefer a derived query over a custom `@Query`?
6. Why does Spring require `escape '\\'` in JPQL?

Use these questions to drive understanding.

---

# ------------------------------------------------------------

# 🟦 **Step 11 — Assign a Coding Exercise**

Ask students to:

1. Write a repository method to search by description suffix.
2. Use `@Query` to implement it.
3. Add parameter sanitization.
4. Write a test that tries to inject `%`.
5. Verify sanitized behavior.

This reinforces skills learned.

---

# ------------------------------------------------------------

# 🟦 **Step 12 — Summary for Students**

* LIKE injection is real and common
* Derived queries are safe
* Custom JPQL requires sanitization
* Spring SpEL escape is the cleanest option
* Always escape `%`, `_`, and `\`
* Testing sanitization is essential

---

