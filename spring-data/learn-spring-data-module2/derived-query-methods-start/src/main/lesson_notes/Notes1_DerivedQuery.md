
## **Derived Query Methods in Spring Data JPA**

---

### **1. Introduction**

Derived queries are one of the most **powerful and convenient features** of Spring Data JPA.
They allow developers to **define database queries by simply declaring method names** that follow a specific naming convention — without writing any JPQL or SQL manually.

This drastically reduces boilerplate code in the data access layer and makes repositories easy to read and maintain.

Example:

```java
Optional<Campaign> findByCodeEquals(String code);
```

✅ **Explanation:**
Spring Data JPA automatically interprets this method name, derives the appropriate query, and executes it.
At runtime, this translates into a SQL query such as:

```sql
SELECT * FROM campaign WHERE code = ?
```

When invoked:

```java
Optional<Campaign> campaign1 = campaignRepository.findByCodeEquals("C1");
LOG.info("Campaign with code C1: {}", campaign1);
```

To see the query in the console, enable SQL logging in your `application.properties`:

```properties
spring.jpa.show-sql=true
```

---

### **2. How Derived Query Methods Work**

Spring Data JPA parses the **method name** of repository interfaces to build the corresponding query automatically.
Each derived query method typically has two parts:

```
find...By<Property><Condition>
```

Example:

```java
List<User> findByNameEquals(String name);
```

* The **subject** (`findBy`) defines the *intent* (i.e., a SELECT query).
* The **predicate** (`NameEquals`) defines the *criteria* used for filtering.

---

### **3. Structure of Derived Query Methods**

#### **3.1 Subject Keywords**

The subject (introductory part) determines the type of query.
Common subject keywords include:

| Keyword                                    | Description                              |
| ------------------------------------------ | ---------------------------------------- |
| `find…By`, `read…By`, `get…By`, `query…By` | Used to retrieve data (SELECT).          |
| `count…By`                                 | Returns the count of matching entities.  |
| `exists…By`                                | Checks for existence; returns a boolean. |
| `delete…By`, `remove…By`                   | Used to delete matching records.         |
| `distinct`                                 | Returns only unique results.             |
| `first<number>` or `top<number>`           | Limits results to the first *n* matches. |

**Example:**

```java
int countByName(String name);
```

This translates to:

```sql
SELECT COUNT(*) FROM campaign WHERE name = ?
```

---

#### **3.2 Predicate Keywords**

The **predicate** part defines the filtering condition, and it must reference valid entity field names.
Common condition keywords include:

| Keyword                                    | SQL Equivalent           | Example                                  |
| ------------------------------------------ | ------------------------ | ---------------------------------------- |
| `Is`, `Equals`                             | `=`                      | `findByCodeEquals(String code)`          |
| `IsNot`, `Not`                             | `<>`                     | `findByNameIsNot(String name)`           |
| `LessThan`, `LessThanEqual`                | `<`, `<=`                | `findByAgeLessThan(int age)`             |
| `GreaterThan`, `GreaterThanEqual`          | `>`, `>=`                | `findByAgeGreaterThan(int age)`          |
| `Between`                                  | `BETWEEN ? AND ?`        | `findByAgeBetween(int start, int end)`   |
| `Containing`, `StartingWith`, `EndingWith` | `LIKE`                   | `findByNameContaining(String part)`      |
| `IsNull`, `IsNotNull`                      | `IS NULL`, `IS NOT NULL` | `findByEmailIsNull()`                    |
| `True`, `False`                            | `= true`, `= false`      | `findByActiveTrue()`                     |
| `In`, `NotIn`                              | `IN`, `NOT IN`           | `findByIdIn(List<Long> ids)`             |
| `Before`, `After`                          | Date comparisons         | `findByCreatedDateAfter(LocalDate date)` |

---

### **4. Example: CampaignRepository**

```java
@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    // Equality condition
    Optional<Campaign> findByCodeEquals(String code);

    // Count projection
    int countByName(String name);

    // Distinct and descriptive example
    int countDistinctByName(String name);

    // Existence check
    boolean existsByCode(String code);

    // Pattern-based matching
    List<Campaign> findByNameContaining(String text);

    // Delete query
    void deleteByCode(String code);
}
```

---

### **5. Enabling Query Logging**

To inspect the SQL generated from derived queries, add this line in `application.properties`:

```properties
spring.jpa.show-sql=true
```

This helps you verify that Spring correctly interprets your derived query methods.

---

### **6. Combining Conditions**

You can combine multiple conditions using **And** / **Or** operators.

Example:

```java
List<User> findByNameAndAge(String name, Integer age);
List<User> findByNameOrAge(String name, Integer age);
```

✅ Spring respects the logical precedence:

* `AND` is evaluated **before** `OR`, just like in SQL and Java.

---

### **7. Sorting Results**

You can include sorting in method names using **OrderBy**:

```java
List<User> findByActiveTrueOrderByNameAsc();
List<User> findByAgeGreaterThanOrderByBirthDateDesc(Integer age);
```

Alternatively, you can pass a `Sort` object to any method:

```java
List<User> findByActiveTrue(Sort sort);
```

---

### **8. Example Application**

Let’s put this together in a simple application.

#### **Entity**

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Integer age;
    private ZonedDateTime birthDate;
    private Boolean active;

    // Getters and setters
}
```

#### **Repository**

```java
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findByName(String name);
    List<User> findByNameStartingWith(String prefix);
    List<User> findByAgeBetween(Integer start, Integer end);
    List<User> findByActiveTrueOrderByNameAsc();
}
```

#### **Main Runner**

```java
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) {

        // Sample data
        userRepository.save(new User("Alice", 30, ZonedDateTime.now().minusYears(30), true));
        userRepository.save(new User("Bob", 25, ZonedDateTime.now().minusYears(25), false));

        // Derived query usage
        List<User> users = userRepository.findByNameStartingWith("A");
        users.forEach(System.out::println);
    }
}
```

---

### **9. Key Takeaways**

| **Concept**                | **Description**                                                 |
| -------------------------- | --------------------------------------------------------------- |
| **Derived Queries**        | Built automatically by parsing method names.                    |
| **Subject Keywords**       | Indicate query type (`find`, `count`, `exists`, `delete`).      |
| **Predicate Keywords**     | Define filtering logic (`Equals`, `LessThan`, `Between`, etc.). |
| **Combination**            | Use `And` / `Or` to combine multiple conditions.                |
| **Sorting**                | Include `OrderBy` or use the `Sort` parameter.                  |
| **Distinct / Top / First** | Limit or refine results easily.                                 |

---

### **10. Conclusion**

Derived Query Methods provide a clean, expressive, and declarative way to create queries in Spring Data JPA — no SQL or JPQL strings needed.

They are ideal for **simple to moderately complex** queries.
For more advanced cases, you can use:

* `@Query` annotation,
* JPA Named Queries, or
* the Criteria API.

---
Perfect 👍 Here’s an additional section you can append to your **lesson notes on “Derived Query Methods”** — a concise **reference table** of the most commonly used Spring Data JPA keywords and their corresponding **JPQL/SQL equivalents**.

---

## **11. Derived Query Keywords Reference Table**

This table summarizes the keywords you can use in **Spring Data JPA derived query methods** and what each translates to in SQL/JPQL.

---

### **A. Subject (Introductory) Keywords**

| **Keyword**                                             | **Purpose / Meaning**    | **Example Method**                | **JPQL Equivalent / Behavior**                     |
| ------------------------------------------------------- | ------------------------ | --------------------------------- | -------------------------------------------------- |
| `find…By`, `read…By`, `get…By`, `query…By`, `search…By` | Retrieve entities        | `findByName(String name)`         | `SELECT e FROM Entity e WHERE e.name = ?`          |
| `count…By`                                              | Count matching records   | `countByStatus(String status)`    | `SELECT COUNT(e) FROM Entity e WHERE e.status = ?` |
| `exists…By`                                             | Check if entity exists   | `existsByEmail(String email)`     | Returns boolean (`true` if match exists)           |
| `delete…By`, `remove…By`                                | Delete matching entities | `deleteById(Long id)`             | `DELETE FROM Entity e WHERE e.id = ?`              |
| `distinct`                                              | Return unique results    | `findDistinctByName(String name)` | `SELECT DISTINCT e FROM Entity e WHERE e.name = ?` |
| `top`, `first`                                          | Limit number of results  | `findTop3ByOrderByAgeDesc()`      | `SELECT … LIMIT 3`                                 |

---

### **B. Predicate (Condition) Keywords**

| **Keyword**                       | **SQL/JPQL Translation** | **Example Method**                  | **Equivalent SQL Clause**    |
| --------------------------------- | ------------------------ | ----------------------------------- | ---------------------------- |
| `Is`, `Equals`                    | Equality check           | `findByCodeEquals(String code)`     | `WHERE code = ?`             |
| `IsNot`, `Not`                    | Inequality check         | `findByStatusNot(String status)`    | `WHERE status <> ?`          |
| `LessThan`, `LessThanEqual`       | Comparison (<, ≤)        | `findByAgeLessThan(30)`             | `WHERE age < 30`             |
| `GreaterThan`, `GreaterThanEqual` | Comparison (>, ≥)        | `findByPriceGreaterThanEqual(1000)` | `WHERE price >= 1000`        |
| `Between`                         | Range comparison         | `findByDateBetween(start, end)`     | `WHERE date BETWEEN ? AND ?` |
| `In`, `NotIn`                     | Match collection         | `findByIdIn(List<Long> ids)`        | `WHERE id IN (…)`            |
| `IsNull`, `IsNotNull`             | Null checks              | `findByEmailIsNull()`               | `WHERE email IS NULL`        |
| `True`, `False`                   | Boolean checks           | `findByActiveTrue()`                | `WHERE active = TRUE`        |
| `Before`, `After`                 | Date/time comparison     | `findByBirthDateAfter(date)`        | `WHERE birth_date > ?`       |
| `Like`                            | Pattern matching         | `findByNameLike(String pattern)`    | `WHERE name LIKE ?`          |
| `Containing`                      | Contains text (LIKE %…%) | `findByTitleContaining("Spring")`   | `WHERE title LIKE %Spring%`  |
| `StartingWith`                    | Prefix match             | `findByNameStartingWith("A")`       | `WHERE name LIKE A%`         |
| `EndingWith`                      | Suffix match             | `findByNameEndingWith("z")`         | `WHERE name LIKE %z`         |
| `OrderBy`                         | Sorting                  | `findByAgeOrderByNameDesc()`        | `ORDER BY name DESC`         |

---

### **C. Logical Operators**

| **Keyword** | **Description**             | **Example Method**              | **Resulting Clause**                |
| ----------- | --------------------------- | ------------------------------- | ----------------------------------- |
| `And`       | Combine conditions with AND | `findByNameAndAge("Alice", 30)` | `WHERE name = 'Alice' AND age = 30` |
| `Or`        | Combine conditions with OR  | `findByNameOrAge("Bob", 25)`    | `WHERE name = 'Bob' OR age = 25`    |

---

### **D. Modifiers**

| **Keyword**                 | **Usage**                            | **Effect / Behavior**                        |
| --------------------------- | ------------------------------------ | -------------------------------------------- |
| `IgnoreCase`                | Used with text comparisons           | Performs case-insensitive matching           |
| `AllIgnoreCase`             | Apply case-insensitive to all fields | Makes all string conditions case-insensitive |
| `Distinct`                  | Use unique results                   | Removes duplicates from results              |
| `OrderBy<Property>Asc/Desc` | Static sorting                       | Orders results ascending or descending       |

---

### **E. Special and Utility Keywords**

| **Keyword**     | **Purpose**                        | **Example Method**                  |
| --------------- | ---------------------------------- | ----------------------------------- |
| `ExistsBy…`     | Checks if any record matches       | `existsByUsername(String username)` |
| `CountBy…`      | Counts matching records            | `countByRole(String role)`          |
| `DeleteBy…`     | Deletes records matching condition | `deleteByRole(String role)`         |
| `Top<number>`   | Limits results                     | `findTop5ByOrderByIdDesc()`         |
| `First<number>` | Synonym for `Top`                  | `findFirst3ByOrderByNameAsc()`      |

---

### **F. Combined Example**

Let’s look at a few complex examples that combine multiple keywords and behaviors:

| **Method Signature**                                       | **Generated SQL/JPQL Equivalent**                                      |
| ---------------------------------------------------------- | ---------------------------------------------------------------------- |
| `findTop3DistinctByAgeGreaterThanOrderByNameDesc(int age)` | `SELECT DISTINCT * FROM user WHERE age > ? ORDER BY name DESC LIMIT 3` |
| `countByNameContainingIgnoreCase(String name)`             | `SELECT COUNT(*) FROM user WHERE LOWER(name) LIKE LOWER(%?%)`          |
| `findByBirthDateBeforeAndActiveTrue(ZonedDateTime date)`   | `SELECT * FROM user WHERE birth_date < ? AND active = TRUE`            |

---

### **12. Best Practices**

✅ **Use Derived Queries for:**

* Simple lookups (e.g., equality, comparisons, small combinations).
* Common CRUD-style filters (like “findByStatusTrue”).

⚠️ **Avoid Derived Queries for:**

* Complex logic requiring joins, subqueries, or projections.
* Very long method names — use `@Query` instead for clarity.

✅ **Combine Keywords Carefully:**
Use parentheses or break complex logic into multiple repository methods when readability suffers.

✅ **Enable SQL Logging for Debugging:**

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

---

### **13. Summary**

| **Category**              | **Purpose**                                                           |
| ------------------------- | --------------------------------------------------------------------- |
| **Derived Query Methods** | Define repository queries by method naming conventions.               |
| **Subject Keywords**      | Define query intent (`find`, `count`, `exists`, etc.).                |
| **Predicate Keywords**    | Define filtering criteria (`Equals`, `LessThan`, `Containing`, etc.). |
| **Modifiers**             | Add result constraints or case-insensitive behavior.                  |
| **Auto Configuration**    | JPA automatically translates methods into SQL queries at runtime.     |

---

Would you like me to add a **diagram** summarizing the *structure of a derived query method* (e.g., “`findTop3ByNameAndAgeGreaterThan`” split into subject + predicate parts)? It’s an excellent visual aid for lesson slides.
