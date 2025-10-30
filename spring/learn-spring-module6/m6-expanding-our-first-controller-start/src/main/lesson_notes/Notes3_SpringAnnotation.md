
---

# **Lesson Notes: Loading Initial Data with Spring Boot**

---

## **1. Overview**

Spring Boot simplifies the management of database setup and initialization. By default, it automatically scans entity classes in the project and creates the respective tables in the database schema.

However, developers often need finer control over **database initialization**, such as defining custom schemas or loading sample data during application startup.

Spring Boot provides multiple mechanisms for this purpose, including the use of:

* `schema.sql` – to define or modify database schema manually.
* `data.sql` – to populate tables with initial data.

These scripts can be used together or independently, depending on the application’s requirements.

---

## **2. The `data.sql` File**

When an application runs with **JPA** and entity classes, Spring Boot creates empty tables automatically. However, these tables remain unpopulated unless data is explicitly inserted.

### **Example: Country Entity**

```java
@Entity
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    // getters and setters
}
```

By default, Spring Boot creates a `country` table, but without any records. To insert initial records, a developer can create a **data.sql** file in the `src/main/resources` directory:

### **Example: data.sql**

```sql
INSERT INTO country (name) VALUES ('India');
INSERT INTO country (name) VALUES ('Brazil');
INSERT INTO country (name) VALUES ('USA');
INSERT INTO country (name) VALUES ('Italy');
```

When the application starts, Spring executes this script to populate the table automatically.

### **Defer Data Source Initialization**

By default, `data.sql` runs **before** Hibernate initializes the schema, which can lead to errors if tables do not yet exist. To ensure that the schema is created first, we defer data source initialization:

```properties
spring.jpa.defer-datasource-initialization=true
```

This ensures Hibernate creates tables before executing `data.sql`.

### **Enable Script-Based Initialization**

For script-based initialization (via `data.sql` or `schema.sql`), enable:

```properties
spring.sql.init.mode=always
```

> Note: For embedded databases such as **H2**, this property defaults to `always`.

---

## **3. The `schema.sql` File**

At times, developers prefer **custom schema creation** instead of relying on Hibernate’s automatic mechanism. This is achieved using the **schema.sql** file.

### **Example: schema.sql**

```sql
CREATE TABLE USERS (
  ID INT NOT NULL AUTO_INCREMENT,
  NAME VARCHAR(100) NOT NULL,
  STATUS INT,
  PRIMARY KEY (ID)
);
```

Spring Boot automatically detects and executes this file during startup, creating the `USERS` table even if it is not defined as an entity.

### **Disabling Hibernate DDL Generation**

When both Hibernate schema generation and SQL scripts are used together, conflicts can occur. To avoid this, disable Hibernate DDL generation:

```properties
spring.jpa.hibernate.ddl-auto=none
```

This ensures only the SQL scripts are executed to create the schema.

### **Using Both Hibernate and SQL Scripts Together**

If you wish to keep both Hibernate schema generation and SQL scripts:

```properties
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
```

This ensures:

1. Hibernate creates the schema.
2. `schema.sql` applies additional schema changes.
3. `data.sql` populates the tables with data.

---

## **4. Controlling Database Creation Using Hibernate**

Spring Boot provides the property **`spring.jpa.hibernate.ddl-auto`** to define how Hibernate handles schema creation and updates.

### **Common Property Values**

| Value         | Description                                                              |
| ------------- | ------------------------------------------------------------------------ |
| `create`      | Drops existing tables and creates new ones.                              |
| `update`      | Updates schema based on entity definitions without dropping tables.      |
| `create-drop` | Creates schema at startup and drops it at shutdown (useful for testing). |
| `validate`    | Validates schema; throws exception if tables/columns are missing.        |
| `none`        | Disables DDL generation completely.                                      |

By default, Spring Boot uses:

* `create-drop` for embedded databases.
* `none` for external databases.

### **Example Configuration**

```properties
spring.jpa.hibernate.ddl-auto=update
```

This updates the existing schema automatically without deleting tables or data.

---

## **5. Customizing Database Schema Creation**

Spring Boot’s **`spring.sql.init.mode`** property controls when script-based initialization should occur.

### **Possible Values**

| Value      | Description                                       |
| ---------- | ------------------------------------------------- |
| `always`   | Always initialize the database.                   |
| `embedded` | Initialize only for embedded databases (default). |
| `never`    | Never run initialization scripts.                 |

When working with non-embedded databases such as **MySQL** or **PostgreSQL**, you must explicitly set:

```properties
spring.sql.init.mode=always
```

> For Spring Boot versions earlier than 2.5.0, use `spring.datasource.initialization-mode` instead.

---

## **6. Using the `@Sql` Annotation**

Spring provides the **`@Sql` annotation** to execute SQL scripts during testing. It allows developers to load or clean up test data declaratively in integration tests.

### **Attributes of `@Sql`**

* **config** – Provides configuration for script parsing and execution.
* **executionPhase** – Defines when the SQL script should run.
* **statements** – Allows inline SQL statements.
* **scripts** – Specifies script file paths.

---

### **6.1. Using `@Sql` at Class Level**

To execute SQL scripts before running any tests in a class:

```java
@Sql({"/employees_schema.sql", "/import_employees.sql"})
public class SpringBootInitialLoadIntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void testLoadDataForTestClass() {
        assertEquals(3, employeeRepository.findAll().size());
    }
}
```

By default, scripts execute **before each test method** (`BEFORE_TEST_METHOD` phase).

To specify a class-level execution phase (from Spring 6.1 / Boot 3.2.0):

```java
@Sql(scripts = {"/employees_schema.sql", "/import_employees.sql"}, executionPhase = BEFORE_TEST_CLASS)
public class SpringBootInitialLoadIntegrationTest {
    // ...
}
```

To run scripts after all tests:

```java
@Sql(scripts = {"/delete_employees_data.sql"}, executionPhase = AFTER_TEST_CLASS)
public class SpringBootInitialLoadIntegrationTest {
    // ...
}
```

---

### **6.2. Using `@Sql` at Method Level**

For executing additional scripts for specific test methods:

```java
@Test
@Sql({"/import_senior_employees.sql"})
public void testLoadDataForTestCase() {
    assertEquals(5, employeeRepository.findAll().size());
}
```

You can specify when the script should run:

```java
@Test
@Sql(scripts = {"/import_senior_employees.sql"}, executionPhase = BEFORE_TEST_METHOD)
public void testLoadDataForTestCase() {
    assertEquals(5, employeeRepository.findAll().size());
}
```

The `AFTER_TEST_METHOD` phase is used to run scripts after a test—for example, to clean up data.

By default, **method-level** `@Sql` overrides class-level `@Sql` definitions.
To merge both, use `@SqlMergeMode`.

---

## **7. The `@SqlConfig` Annotation**

The `@SqlConfig` annotation customizes how SQL scripts are parsed and executed.
It can be applied globally at the class level or individually with `@Sql`.

### **Example**

```java
@Test
@Sql(
  scripts = {"/import_senior_employees.sql"}, 
  config = @SqlConfig(encoding = "utf-8", transactionMode = TransactionMode.ISOLATED)
)
public void testLoadDataForTestCase() {
    assertEquals(5, employeeRepository.findAll().size());
}
```

### **Common Attributes**

* **encoding** – Script file encoding (default: platform).
* **transactionMode** – Transaction handling mode.
* **commentPrefix** – Character(s) marking SQL comments.
* **separator** – Statement separator (default: “;”).
* **errorMode** – Defines error handling behavior.
* **dataSource** – Data source bean name for execution.

---

## **8. The `@SqlGroup` Annotation**

Before Java 8, repeating annotations were not allowed. To declare multiple `@Sql` annotations, Spring provides the **`@SqlGroup`** container annotation.

### **Example**

```java
@SqlGroup({
  @Sql(scripts = "/employees_schema.sql",
       config = @SqlConfig(transactionMode = TransactionMode.ISOLATED)),
  @Sql("/import_employees.sql")
})
public class SpringBootSqlGroupAnnotationIntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void testLoadDataForTestCase() {
        assertEquals(3, employeeRepository.findAll().size());
    }
}
```

---

## **9. Conclusion**

In this lesson, we examined several approaches for **loading and managing initial data** in Spring Boot:

* Using `schema.sql` to define custom database schemas.
* Using `data.sql` to insert seed data.
* Controlling Hibernate’s DDL behavior through `spring.jpa.hibernate.ddl-auto`.
* Customizing script execution using properties like `spring.sql.init.mode`.
* Employing annotations such as `@Sql`, `@SqlConfig`, and `@SqlGroup` for test database initialization.

These methods are ideal for simple initialization and testing.
For more complex production environments, **database migration tools like Liquibase or Flyway** are recommended for managing versioned schema changes effectively.

---

