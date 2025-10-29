
---

# **Lesson Notes: NamedParameterJdbcTemplate in Spring Framework**

---

## **1. Introduction**

The **NamedParameterJdbcTemplate** is an advanced class in the **Spring JDBC framework** that builds upon the functionality of the standard **JdbcTemplate**.
Its primary purpose is to make SQL queries **more readable and maintainable** by allowing developers to use **named parameters** (e.g., `:id`, `:name`) instead of traditional **positional placeholders** (`?`).

NamedParameterJdbcTemplate simplifies complex queries by:

* Reducing confusion about parameter order.
* Increasing code clarity.
* Supporting parameter binding using maps or `SqlParameterSource` objects.

It internally uses **JdbcTemplate** for executing queries and updates but enhances it with a more expressive parameter syntax.

---

## **2. Why NamedParameterJdbcTemplate?**

In traditional `JdbcTemplate`, parameters are set in order using `?` placeholders, which can become confusing when many parameters are involved. For example:

```java
String sql = "SELECT * FROM project WHERE id = ? AND status = ? AND category = ?";
jdbcTemplate.query(sql, new Object[]{id, status, category}, new ProjectRowMapper());
```

This approach can easily lead to mistakes if parameter order changes.

With **NamedParameterJdbcTemplate**, we can rewrite the same query using **named parameters**:

```java
String sql = "SELECT * FROM project WHERE id = :id AND status = :status AND category = :category";
Map<String, Object> params = Map.of(
    "id", id,
    "status", status,
    "category", category
);
namedParameterJdbcTemplate.query(sql, params, new ProjectRowMapper());
```

This not only improves readability but also reduces potential parameter mismatches.

---

## **3. Setting Up NamedParameterJdbcTemplate**

### **3.1. Adding Dependencies**

Add Spring JDBC and a database dependency in your `pom.xml` file:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>

<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
```

---

### **3.2. Configuration**

When using Spring Boot, the framework automatically configures `JdbcTemplate` and `NamedParameterJdbcTemplate` beans.
You can inject it directly in your repository class:

```java
@Repository
public class ProjectRepositoryImpl {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public ProjectRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
}
```

If you are using manual configuration (non-Boot setup), define the bean explicitly:

```java
@Configuration
public class AppConfig {

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
```

---

## **4. Core Operations**

NamedParameterJdbcTemplate supports all operations of JdbcTemplate — such as **query**, **update**, **batchUpdate**, and **execute** — but uses **named parameters** instead of positional ones.

---

### **4.1. Querying Data**

```java
String sql = "SELECT * FROM project WHERE id = :id";

Map<String, Object> params = Map.of("id", 1L);

Project project = namedParameterJdbcTemplate.queryForObject(
    sql,
    params,
    new ProjectRowMapper()
);
```

**Explanation:**

* `:id` is a **named parameter** bound to the value `1L`.
* A **RowMapper** converts each record into a `Project` object.
* The method `queryForObject()` returns a single object.

---

### **4.2. Inserting Data**

```java
String sql = "INSERT INTO project (name, date_created) VALUES (:name, :dateCreated)";

Map<String, Object> params = Map.of(
    "name", "AI Research Project",
    "dateCreated", LocalDate.now()
);

namedParameterJdbcTemplate.update(sql, params);
```

This operation:

* Uses named placeholders `:name` and `:dateCreated`.
* Automatically binds the values from the `params` map.
* Executes the SQL insert statement safely.

---

### **4.3. Updating Data**

```java
String sql = "UPDATE project SET status = :status WHERE id = :id";

Map<String, Object> params = Map.of(
    "status", "Completed",
    "id", 3
);

int rowsAffected = namedParameterJdbcTemplate.update(sql, params);
System.out.println(rowsAffected + " record(s) updated.");
```

This approach clearly identifies which parameters correspond to which columns — making updates readable and maintainable.

---

### **4.4. Deleting Data**

```java
String sql = "DELETE FROM project WHERE id = :id";
Map<String, Object> params = Map.of("id", 5);

namedParameterJdbcTemplate.update(sql, params);
```

The use of named parameters ensures clarity and prevents errors when working with large or dynamic queries.

---

## **5. Using SqlParameterSource**

In addition to `Map<String, Object>`, Spring provides the **SqlParameterSource** interface for parameter binding.
Two key implementations are commonly used:

| Implementation                     | Description                                                  |
| ---------------------------------- | ------------------------------------------------------------ |
| **MapSqlParameterSource**          | Manually maps parameters to values.                          |
| **BeanPropertySqlParameterSource** | Automatically maps Java bean properties to named parameters. |

---

### **5.1. MapSqlParameterSource Example**

```java
String sql = "SELECT * FROM project WHERE category = :category AND status = :status";

SqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("category", "Education")
        .addValue("status", "Active");

List<Project> projects = namedParameterJdbcTemplate.query(sql, parameters, new ProjectRowMapper());
```

---

### **5.2. BeanPropertySqlParameterSource Example**

`BeanPropertySqlParameterSource` automatically maps bean fields to SQL parameters with matching names.

```java
Project project = new Project();
project.setName("Healthcare App");
project.setDateCreated(LocalDate.now());

String sql = "INSERT INTO project (name, date_created) VALUES (:name, :dateCreated)";
SqlParameterSource parameters = new BeanPropertySqlParameterSource(project);

namedParameterJdbcTemplate.update(sql, parameters);
```

This eliminates the need to manually create a map for parameters, as long as field names match the named parameters in the SQL query.

---

## **6. Batch Operations**

NamedParameterJdbcTemplate supports efficient batch updates for inserting or updating multiple rows.

```java
String sql = "INSERT INTO project (name, date_created) VALUES (:name, :dateCreated)";

List<Project> projects = List.of(
    new Project("AI Assistant", LocalDate.now()),
    new Project("Data Analytics", LocalDate.now())
);

SqlParameterSource[] batchParams = SqlParameterSourceUtils.createBatch(projects.toArray());

namedParameterJdbcTemplate.batchUpdate(sql, batchParams);
```

**Explanation:**

* Each `Project` object is converted to a parameter set using `SqlParameterSourceUtils.createBatch()`.
* All insert statements are executed in a single batch, improving performance.

---

## **7. Returning Values (queryForObject)**

NamedParameterJdbcTemplate provides the same retrieval methods as JdbcTemplate.

```java
String sql = "SELECT COUNT(*) FROM project WHERE status = :status";
Map<String, Object> params = Map.of("status", "Active");

int count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
System.out.println("Active projects: " + count);
```

---

## **8. Integration Example**

A full repository implementation using NamedParameterJdbcTemplate:

```java
@Repository
public class ProjectRepositoryImpl implements IProjectRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Optional<Project> findById(Long id) {
        String sql = "SELECT * FROM project WHERE id = :id";
        Map<String, Object> params = Map.of("id", id);

        List<Project> results = namedParameterJdbcTemplate.query(sql, params, new ProjectRowMapper());
        return results.stream().findAny();
    }

    @Override
    public Project save(Project project) {
        String sql = "INSERT INTO project (name, date_created) VALUES (:name, :dateCreated)";
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(project);
        namedParameterJdbcTemplate.update(sql, parameters);
        return project;
    }
}
```

This version is cleaner, type-safe, and easily maintainable.

---

## **9. Comparison: JdbcTemplate vs NamedParameterJdbcTemplate**

| Feature                    | JdbcTemplate                   | NamedParameterJdbcTemplate             |
| -------------------------- | ------------------------------ | -------------------------------------- |
| **Parameter Type**         | Positional (`?`)               | Named (`:name`)                        |
| **Readability**            | Harder with many params        | Clear and descriptive                  |
| **Parameter Binding**      | Manual order required          | Uses `Map` or `SqlParameterSource`     |
| **Batch Support**          | Yes                            | Yes                                    |
| **Automatic Bean Mapping** | No                             | Yes (`BeanPropertySqlParameterSource`) |
| **Best For**               | Simple SQL with few parameters | Complex SQL with many parameters       |

---

## **10. Advantages**

* **Improved readability:** Named parameters make SQL more self-explanatory.
* **Reduced parameter order errors:** No need to remember or count placeholders.
* **Supports object-based parameter binding:** Directly map bean fields to SQL.
* **Batch operations supported:** Enhances performance for bulk data processing.
* **Full compatibility:** Can be used wherever JdbcTemplate is used.

---

## **11. Summary**

The **NamedParameterJdbcTemplate** provides a more elegant and safer way to interact with relational databases using **named parameters** instead of **positional placeholders**.
It enhances code readability, reduces maintenance complexity, and supports advanced features such as **batch updates** and **automatic bean mapping**.

In summary:

* It extends `JdbcTemplate` functionality.
* Uses `Map`, `MapSqlParameterSource`, or `BeanPropertySqlParameterSource` for parameter binding.
* Retains all core operations such as `query`, `update`, `batchUpdate`, and `queryForObject`.

**NamedParameterJdbcTemplate = JdbcTemplate + Readability + Type Safety.**

---
