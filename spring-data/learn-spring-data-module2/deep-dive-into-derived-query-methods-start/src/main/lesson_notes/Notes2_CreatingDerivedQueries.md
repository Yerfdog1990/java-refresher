
---

# 🌿 Lesson Notes: How Spring Creates Queries from Derived Methods

---

## **1. Overview**

JPA supports several types of queries to interact with a database.
Each serves a different purpose and offers varying levels of flexibility, complexity, and portability.

### **Main Types of JPA Queries**
Here’s your **updated table** with a **third column** added — showing an **example of each query type** in Spring Data JPA:

---

### 🌳 Types of JPA Queries

| **Query Type**                              | **Description**                                                                       | **Use Case**                                                   | **Example**                                                                                                                                                                                                                |
| ------------------------------------------- | ------------------------------------------------------------------------------------- | -------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **JPQL (Java Persistence Query Language)**  | Object-oriented query language that operates on entities rather than database tables. | For static, portable queries embedded in application logic.    | `@Query("SELECT e FROM Employee e WHERE e.department = :dept")`                                                                                                                                                            |
| **Criteria API**                            | Type-safe, programmatic API for dynamically building queries at runtime.              | For complex, dynamic queries that need flexibility.            | `CriteriaBuilder cb = em.getCriteriaBuilder(); CriteriaQuery<Employee> cq = cb.createQuery(Employee.class); Root<Employee> root = cq.from(Employee.class); cq.select(root).where(cb.equal(root.get("department"), "HR"));` |
| **Native SQL**                              | Executes direct SQL queries on the database.                                          | For database-specific operations or performance optimizations. | `@Query(value = "SELECT * FROM employees WHERE salary > ?", nativeQuery = true)`                                                                                                                                           |
| **Derived Query Methods (Spring Data JPA)** | Spring automatically creates queries based on method names in repository interfaces.  | For simple, static queries with readable and concise code.     | `List<Employee> findByDepartmentAndStatus(String dept, String status);`                                                                                                                                                    |

---

## **2. How Spring Data JPA Derives Queries**

Spring Data JPA introduces a powerful feature called **derived query methods**.
Instead of manually writing JPQL or SQL statements, you define method names following a **specific naming convention**, and Spring automatically generates the underlying query.

This mechanism is built upon an internal Spring component known as the **PartTree**.

---

## **3. The “PartTree” Query Generation Mechanism**

Spring Data JPA uses the `PartTree` class to **parse** and **analyze** repository method names.
It then **constructs JPQL queries** from the identified method parts.

Let’s explore how this process works step by step.

---

### **Step 1: Method Name Parsing**

Spring examines the method name and breaks it into parts using reserved keywords such as:

* **By, And, Or**
* **Is, Equals, Like, Between, In, Not, Null, True, False**
* **OrderBy, Count, Exists, Delete**

For example:

```java
List<Employee> findByFirstNameAndLastName(String firstName, String lastName);
```

Spring splits this into:

* `find` → introducer
* `ByFirstNameAndLastName` → criteria parts

---

### **Step 2: Property Recognition**

Next, Spring matches the parsed property names (like `firstName` and `lastName`)
with the **entity’s field names**.

If a field is invalid or misspelled (e.g., `findByFirtName`),
Spring Boot will **fail to start** and throw a clear error, helping you catch mistakes early.

---

### **Step 3: Operator Inference**

Spring then interprets keywords to determine the **query operators**:

| Keyword                                            | JPQL Operator            |
| -------------------------------------------------- | ------------------------ |
| `And`                                              | `AND`                    |
| `Or`                                               | `OR`                     |
| `Like`, `Containing`, `StartingWith`, `EndingWith` | `LIKE`                   |
| `GreaterThan`, `LessThan`, `Between`               | Comparison operators     |
| `IsNull`, `NotNull`                                | `IS NULL`, `IS NOT NULL` |
| `OrderBy`                                          | `ORDER BY`               |
| `ExistsBy`                                         | Boolean existence check  |

This step defines the **logical and comparison structure** of the query.

---

### **Step 4: Query Generation**

Once Spring knows the entities, properties, and operators,
it automatically **constructs a JPQL query** (or a native SQL query if configured).

This JPQL is then converted to SQL and executed by the underlying JPA provider (e.g., Hibernate).

---

## **4. Example: Step-by-Step Query Derivation**

Let’s consider an `Employee` entity with the fields `firstName` and `lastName`:

```java
@Entity
public class Employee {
    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String lastName;
}
```

Now, we define several repository methods in the `EmployeeRepository`:

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByFirstNameAndLastName(String firstName, String lastName);
    List<Employee> findByLastNameContainingOrderByFirstNameAsc(String lastName);
    long countByFirstName(String firstName);
    boolean existsByLastName(String lastName);
}
```

---

### **Example 1: Simple Equality**

```java
List<Employee> findByFirstNameAndLastName(String firstName, String lastName);
```

🧠 **Derived Query Logic:**

* `findBy` → query subject (returns entity)
* `FirstNameAndLastName` → criteria fields joined by AND

**Generated JPQL:**

```jpql
SELECT e FROM Employee e WHERE e.firstName = ?1 AND e.lastName = ?2
```

---

### **Example 2: Pattern Matching and Sorting**

```java
List<Employee> findByLastNameContainingOrderByFirstNameAsc(String lastName);
```

🧠 **Derived Query Logic:**

* `Containing` → translates to `LIKE %...%`
* `OrderByFirstNameAsc` → sorting by `firstName` ascending

**Generated JPQL:**

```jpql
SELECT e FROM Employee e WHERE e.lastName LIKE ?1 ORDER BY e.firstName ASC
```

---

### **Example 3: Counting Records**

```java
long countByFirstName(String firstName);
```

🧠 **Derived Query Logic:**

* `countBy` → tells Spring to return a count instead of entities
* `FirstName` → condition field

**Generated JPQL:**

```jpql
SELECT COUNT(e) FROM Employee e WHERE e.firstName = ?1
```

---

### **Example 4: Existence Check**

```java
boolean existsByLastName(String lastName);
```

🧠 **Derived Query Logic:**

* `existsBy` → Boolean result
* `LastName` → condition field

**Generated JPQL:**

```jpql
SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END
FROM Employee e WHERE e.lastName = ?1
```

---

## **5. Reserved Keywords in Derived Queries**

Spring Data JPA defines several **reserved prefixes** and **operators**.

| Category                     | Examples                                                         | Description                                                 |
| ---------------------------- | ---------------------------------------------------------------- | ----------------------------------------------------------- |
| **Query Subjects**           | `findBy`, `readBy`, `getBy`, `countBy`, `existsBy`, `deleteBy`   | Define what type of result or operation is being performed. |
| **Logical Operators**        | `And`, `Or`                                                      | Combine multiple conditions.                                |
| **Comparison Operators**     | `GreaterThan`, `LessThan`, `Between`, `IsNot`, `Before`, `After` | Used for numeric, date, or comparable fields.               |
| **Null / Boolean Operators** | `IsTrue`, `IsFalse`, `IsNull`, `IsNotNull`                       | Check for boolean or null states.                           |
| **String Operators**         | `Like`, `Containing`, `StartingWith`, `EndingWith`               | Pattern-matching conditions.                                |
| **Ordering**                 | `OrderBy...Asc`, `OrderBy...Desc`                                | Sort query results.                                         |

If you use an **invalid property name** or keyword,
Spring Boot will report a **startup error**, indicating that it could not resolve the method into a valid query.

---

## **6. Benefits of Derived Query Methods**

- ✅ **No boilerplate JPQL or SQL:** Queries are auto-generated.
- ✅ **Readable and expressive:** Method names clearly describe intent.
- ✅ **Compile-time safety:** Invalid method names fail at startup.
- ✅ **Easy integration:** Works seamlessly with pagination (`Pageable`) and sorting (`Sort`).
- ✅ **Flexible:** Can be combined with `@Query` for more complex scenarios.

---

## **7. Summary**

| Step                       | Description                                                    |
| -------------------------- | -------------------------------------------------------------- |
| **1. Parse Method Name**   | Spring splits the method into logical parts using keywords.    |
| **2. Identify Properties** | Matches property names with entity fields.                     |
| **3. Infer Operators**     | Determines logical and comparison operators based on keywords. |
| **4. Generate JPQL Query** | Builds a JPQL query dynamically and executes it at runtime.    |

This process — known as **“PartTree Query Derivation”** — is what powers Spring Data JPA’s ability to **create queries from method names automatically**.

---

### 🌱 **In Short:**

> Spring Data JPA turns repository method names into executable JPQL queries
> — all without writing a single query string!

---
