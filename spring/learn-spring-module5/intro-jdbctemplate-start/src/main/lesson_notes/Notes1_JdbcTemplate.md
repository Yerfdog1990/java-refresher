
---

# **Lesson Notes: JdbcTemplate in Spring Framework**

---

## **1. Introduction**

The **Spring JDBC module** provides a simplified and consistent approach to interact with relational databases using Java Database Connectivity (JDBC).
One of the most powerful tools in this module is the **JdbcTemplate** class, which streamlines database access by eliminating repetitive boilerplate code such as opening connections, preparing statements, handling exceptions, and closing resources.

Unlike **Object Relational Mapping (ORM)** tools such as **Hibernate** or **Spring Data JPA**, which abstract SQL almost completely, **JdbcTemplate** provides **direct JDBC access** while still managing the tedious aspects of database interaction.

In this lesson, we will:

* Understand how JdbcTemplate differs from ORM frameworks.
* Explore its setup, basic operations, and callback mechanisms.
* Demonstrate its core operations with practical examples.

---

## **2. ORM vs. Direct JDBC Access**

Spring supports two major approaches to persistence:

| Approach                            | Description                                                                                                            |
| ----------------------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| **ORM (Object Relational Mapping)** | Defines entities and repositories to map database tables to Java objects. Hides SQL complexity (e.g., Hibernate, JPA). |
| **Direct JDBC Access**              | Offers full control over SQL statements and data mapping. Requires defining how data maps to Java objects manually.    |

With ORM, the developer interacts with entities and repositories abstractly, while the framework handles the SQL generation and object mapping.
With **JdbcTemplate**, the developer explicitly defines the SQL statements and the logic for converting query results into objects — allowing for **finer control** and **better performance transparency**.

Despite this lower-level approach, Spring still handles all tedious JDBC concerns, such as:

* Opening and closing database connections.
* Managing transactions.
* Handling SQL exceptions through meaningful translations.

---

## **3. What Is JdbcTemplate?**

The **JdbcTemplate** is the core class in Spring’s JDBC framework.
It provides a set of methods for executing SQL statements, mapping result sets, and performing database updates — all while automatically managing resource handling and error translation.

The design pattern behind JdbcTemplate is based on the **Template Method** pattern.
This means that it defines a fixed workflow (e.g., open connection → execute query → close connection) while delegating customizable logic (like mapping results) to user-provided callback functions.

All other Spring JDBC utilities such as `NamedParameterJdbcTemplate`, `SimpleJdbcInsert`, and `SimpleJdbcCall` are built on top of JdbcTemplate.

---

## **4. Setting Up JdbcTemplate**

### **4.1. Adding Dependencies**

To use JdbcTemplate in a Spring Boot project, include the following dependencies in your `pom.xml`:

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

The first dependency includes Spring’s JDBC functionality, while H2 provides an **in-memory database** for demonstration purposes.

---

### **4.2. Database Configuration**

In `application.properties`, configure the data source:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
```

Spring Boot automatically creates a `JdbcTemplate` bean based on this configuration.

---

## **5. JdbcTemplate Basic Operations**

Let’s explore the three most common operations:

### **5.1. Executing SQL Statements**

The simplest method is `execute()`, used for **DDL statements** such as creating tables.

```java
@SpringBootApplication
public class LsApp implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        jdbcTemplate.execute(
            "CREATE TABLE project (" +
            "id SERIAL, " +
            "name VARCHAR(255), " +
            "date_created DATE)"
        );
    }
}
```

Here, `JdbcTemplate`:

* Acquires a connection from the `DataSource`.
* Executes the SQL command.
* Closes the connection automatically.

---

### **5.2. Querying Data (SELECT Statements)**

Let’s adapt our repository to use JdbcTemplate for retrieving records.

```java
@Repository
public class ProjectRepositoryImpl implements IProjectRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Project> findById(Long id) {
        List<Project> projects = jdbcTemplate.query(
            "SELECT id, name, date_created FROM project WHERE id = ?",
            (resultSet, rowNum) -> new Project(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getDate("date_created").toLocalDate()
            ),
            id
        );
        return projects.stream().findAny();
    }
}
```

**Explanation:**

1. The SQL statement includes a **placeholder (?)** for parameters to prevent SQL injection.
2. A **RowMapper** callback (here implemented via a lambda) maps each row of the `ResultSet` into a `Project` object.
3. `query()` returns a list, which may be empty — making it safer than `queryForObject()` for optional results.

---

### **5.3. Inserting Data (INSERT Statements)**

The `update()` method is used for `INSERT`, `UPDATE`, or `DELETE` operations.

```java
public Project save(Project project) {
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
        PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO project (name, date_created) VALUES (?, ?)",
            new String[] { "id" }
        );
        statement.setString(1, project.getName());
        statement.setDate(2, Date.valueOf(project.getDateCreated()));
        return statement;
    }, keyHolder);

    Long generatedId = keyHolder.getKey().longValue();
    return this.findById(generatedId).get();
}
```

**Explanation:**

* The **PreparedStatementCreator** callback defines how the SQL command and its parameters are set up.
* The **KeyHolder** retrieves the auto-generated ID (primary key).
* The saved project is then retrieved and returned.

---

## **6. Other JdbcTemplate Variants**

| Variant                        | Description                                                                                                                  |
| ------------------------------ | ---------------------------------------------------------------------------------------------------------------------------- |
| **NamedParameterJdbcTemplate** | Allows the use of named parameters (e.g., `:id`) instead of `?`. Improves readability when dealing with multiple parameters. |
| **SimpleJdbcInsert**           | Simplifies insert operations by using table metadata.                                                                        |
| **SimpleJdbcCall**             | Used to call stored procedures with minimal configuration.                                                                   |
| **RDBMS Object Classes**       | Provide reusable, object-oriented representations of queries.                                                                |

---

### **6.1. NamedParameterJdbcTemplate Example**

```java
SqlParameterSource params = new MapSqlParameterSource().addValue("id", 1);
String sql = "SELECT name FROM project WHERE id = :id";

String name = namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
```

This approach improves clarity and avoids confusion when queries have many parameters.

---

### **6.2. SimpleJdbcInsert Example**

```java
SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource)
    .withTableName("project")
    .usingGeneratedKeyColumns("id");

Map<String, Object> params = Map.of(
    "name", "AI Research",
    "date_created", LocalDate.now()
);

Number newId = insert.executeAndReturnKey(params);
```

No explicit SQL is required — the class uses metadata to generate the insert statement automatically.

---

### **6.3. SimpleJdbcCall Example**

Used to execute stored procedures easily.

```java
SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
    .withProcedureName("READ_PROJECT");

SqlParameterSource in = new MapSqlParameterSource().addValue("in_id", 1);
Map<String, Object> out = call.execute(in);

System.out.println(out.get("NAME"));
```

---

## **7. Mapping Query Results to Java Objects**

The `RowMapper` interface is commonly used for mapping `ResultSet` rows into Java objects.

```java
public class ProjectRowMapper implements RowMapper<Project> {
    @Override
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
        Project project = new Project();
        project.setId(rs.getLong("id"));
        project.setName(rs.getString("name"));
        project.setDateCreated(rs.getDate("date_created").toLocalDate());
        return project;
    }
}
```

Then used as:

```java
Project project = jdbcTemplate.queryForObject(
    "SELECT * FROM project WHERE id = ?",
    new ProjectRowMapper(),
    id
);
```

---

## **8. Exception Translation**

Spring provides its own consistent **DataAccessException hierarchy**, translating vendor-specific SQL exceptions into general-purpose ones.
This allows the same code to work across different databases.

Example of a custom translator:

```java
public class CustomSQLErrorCodeTranslator extends SQLErrorCodeSQLExceptionTranslator {
    @Override
    protected DataAccessException customTranslate(String task, String sql, SQLException ex) {
        if (ex.getErrorCode() == 23505) {
            return new DuplicateKeyException("Duplicate record detected.", ex);
        }
        return null;
    }
}
```

Attach the custom translator:

```java
jdbcTemplate.setExceptionTranslator(new CustomSQLErrorCodeTranslator());
```

---

## **9. Batch Operations**

### **9.1. Using JdbcTemplate**

```java
jdbcTemplate.batchUpdate(
    "INSERT INTO project (name, date_created) VALUES (?, ?)",
    new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setString(1, projects.get(i).getName());
            ps.setDate(2, Date.valueOf(projects.get(i).getDateCreated()));
        }

        @Override
        public int getBatchSize() {
            return projects.size();
        }
    });
```

### **9.2. Using NamedParameterJdbcTemplate**

```java
SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(projects.toArray());
namedParameterJdbcTemplate.batchUpdate(
    "INSERT INTO project (name, date_created) VALUES (:name, :dateCreated)", batch);
```

---

## **10. Summary Table: Who Does What in Spring JDBC**

| Action                       | Spring | Developer |
| ---------------------------- | ------ | --------- |
| Define connection parameters | ✅      |           |
| Open and close connection    | ✅      |           |
| Handle transactions          | ✅      |           |
| Prepare and run SQL          |        | ✅         |
| Map query results            |        | ✅         |
| Handle exceptions            | ✅      |           |
| Process each record          |        | ✅         |

Spring handles the infrastructure; the developer focuses on SQL logic and object mapping.

---

## **11. Summary**

* **JdbcTemplate** simplifies traditional JDBC by handling repetitive tasks.
* It supports all CRUD operations through simple, flexible APIs.
* It uses **callbacks** like `RowMapper` and `PreparedStatementCreator` to inject logic dynamically.
* Variants like **NamedParameterJdbcTemplate**, **SimpleJdbcInsert**, and **SimpleJdbcCall** provide additional convenience.
* Spring’s **exception translation** makes error handling consistent across databases.

Overall, **JdbcTemplate bridges the gap** between low-level JDBC and high-level ORM tools, giving developers a balanced mix of **control, simplicity, and performance**.

---

