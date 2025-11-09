
---

# **Lesson Notes: Project Setup – Persistence Project**

---

## **2. Lesson Notes**

### **Module Overview**

The relevant module you need to import when working with this lesson is:

> **`persistence-project`**

This lesson only requires a **single reference codebase**, so there is **no separate end version** of the project.

---

## **2.1. Setting up the Project**

Now that we have a high-level understanding of the **Spring Data JPA** module, let’s examine the project that will serve as our foundation for this course.

Our main focus is the **persistence layer**, particularly the **domain model** that represents the data structure of a task management system.

To make development easier, we’ll build the project as a **Spring Boot application**, which integrates seamlessly with **Spring Data JPA** and provides a simple way to configure and run our app.

---

### **Step 1: Create a New Spring Boot Project**

We’ll use the **Spring Initializr** available at
👉 [https://start.spring.io/](https://start.spring.io/)

**Project Settings:**

* **Project type:** Maven
* **Language:** Java
* **Group:** `com.baeldung`
* **Artifact:** `persistence-project`
* **Package name:** `com.baeldung.lsd`

**Dependencies to add:**

1. **Spring Data JPA** – provides the JPA specification and Hibernate as the default ORM implementation.
2. **Spring Web** – allows the application to start and stay running (even though we won’t focus on web features).

Once done, download the generated ZIP file, unzip it, and **import the project** into your IDE.

At this stage, the project includes only the **main class**:

```java
@SpringBootApplication
public class PersistenceProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(PersistenceProjectApplication.class, args);
    }
}
```

This class bootstraps the Spring Boot application.

---

## **2.2. Defining the Domain**

Our domain models a **task management system**, where:

* A **Campaign** has many **Tasks**
* A **Worker** can be assigned to multiple **Tasks**

Below is a simplified diagram of the relationships:

```
Campaign 1 --- * Task * --- 1 Worker
```

This means:

* Each **Campaign** can have multiple **Tasks**
* Each **Task** belongs to one **Campaign**
* Each **Task** can be assigned to one **Worker**

---

### **Creating the Task Entity**

We start by adding a `Task` class under the package:

```
com.baeldung.lsd.persistence.model
```

**Basic structure:**

```java
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Task() {}
}
```

The `@GeneratedValue` annotation automatically generates IDs when new entities are persisted.

---

### **Adding a UUID Field**

```java
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(unique = true, nullable = false, updatable = false)
    private String uuid = UUID.randomUUID().toString();
}
```

**Purpose of the UUID:**

* Acts as a **natural business key**, ensuring each `Task` is uniquely identifiable before it’s saved to the database.
* Unlike `id`, which is assigned after persistence, the `uuid` exists immediately upon object creation.

The `@NaturalId` annotation highlights that this is a **business identifier**, even though Spring Data JPA doesn’t use it directly.

---

### **Adding Descriptive Fields**

```java
@Entity
public class Task {
    //...

    private String name;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
}
```

The `TaskStatus` is an enumeration that defines the current state of the task.

---

### **Creating the TaskStatus Enum**

```java
public enum TaskStatus {
    TO_DO("To Do"),
    IN_PROGRESS("In Progress"),
    ON_HOLD("On Hold"),
    DONE("Done");

    private final String label;

    private TaskStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
```

---

### **Implementing equals(), hashCode(), and toString()**

To ensure entity equality is based on the `uuid` field:

```java
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Task other)) return false;
    return Objects.equals(getUuid(), other.getUuid());
}

@Override
public int hashCode() {
    return Objects.hash(getUuid());
}
```

And for debugging/logging purposes:

```java
@Override
public String toString() {
    return "Task [id=" + id + ", name=" + name + ", description=" + description +
           ", dueDate=" + dueDate + ", status=" + status +
           ", campaign=" + campaign + ", assignee=" + assignee + "]";
}
```

*Note:* It’s usually safer to omit associated entities in `toString()` to avoid circular references or performance issues.

---

### **Creating the Campaign Entity**

```java
@Entity
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Campaign() {}
}
```

Add business and descriptive fields:

```java
@Entity
public class Campaign {

    @NaturalId
    @Column(unique = true, nullable = false, updatable = false)
    private String code;

    private String name;
    private String description;
}
```

Define a **one-to-many relationship** with `Task`:

```java
@OneToMany(mappedBy = "campaign", orphanRemoval = true,
           fetch = FetchType.EAGER, cascade = CascadeType.ALL)
private Set<Task> tasks = new HashSet<>();
```

Implement `equals()` and `hashCode()` based on the `code` field.

```java
@Override
public int hashCode() {
    return Objects.hashCode(getCode());
}

@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Campaign other)) return false;
    return Objects.equals(getCode(), other.getCode());
}
```

---

### **Defining the Relationship in Task**

```java
@ManyToOne(optional = false)
private Campaign campaign;
```

Add getters, setters, and constructors.
Example constructor with a default status:

```java
public Task(String name, String description, LocalDate dueDate, Campaign campaign) {
    this(name, description, dueDate, campaign, TaskStatus.TO_DO);
}
```

---

### **Creating the Worker Entity**

```java
@Entity
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(unique = true, nullable = false, updatable = false)
    private String email;

    private String firstName;
    private String lastName;

    public Worker() {}
}
```

Add the **many-to-one** relationship in `Task`:

```java
@ManyToOne
private Worker assignee;
```

Each worker can be assigned multiple tasks, but each task has one worker.

---

### **Domain Summary Diagram**

```
Campaign 1 --- * Task * --- 1 Worker
```

* **Campaign–Task:** Bidirectional relationship (`@OneToMany` / `@ManyToOne`)
* **Task–Worker:** Unidirectional relationship (`@ManyToOne`)

This completes our **domain model**.

---

## **2.3. Database Setup**

We’ll use the **H2 in-memory database** for simplicity and to avoid external configuration.

### **Add H2 Dependency**

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
```

Spring Boot will automatically configure this as the default datasource.

---

### **Enable the H2 Console**

In `application.properties`:

```properties
spring.h2.console.enabled=true
```

Then configure the connection parameters:

```properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.open-in-view=false
```

**Notes:**

* `DB_CLOSE_ON_EXIT=FALSE` ensures the database stays alive while the app runs.
* `spring.jpa.open-in-view=false` disables the *Open Session In View* pattern, which is often discouraged in production.

---

## **2.4. Generating the Database Schema**

With **Spring Boot** and **Hibernate**, the schema is automatically generated from the entity classes.

The property controlling this is:

```properties
spring.jpa.hibernate.ddl-auto=create-drop
```

By default, this means:

* Tables are **created** on startup.
* Tables are **dropped** on shutdown.

Access the H2 console at
👉 [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

Use these credentials:

```
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: 
```

Once connected, you’ll see the generated tables for `Task`, `Campaign`, and `Worker`.

---

### **Adding Sample Data**

Create a `data.sql` file in `src/main/resources`:

```sql
INSERT INTO Campaign(id, code, name, description) VALUES (1, 'C1', 'Campaign 1', 'Description of Campaign 1');
INSERT INTO Worker(id, email, first_name, last_name) VALUES(1, 'john@test.com', 'John', 'Doe');
INSERT INTO Task(id, uuid, name, due_date, description, campaign_id, status) VALUES (1, uuid(), 'Task 1', '2025-01-12', 'Task 1 Description', 1, 0);
```

Ensure the data loads **after Hibernate initializes**:

```properties
spring.jpa.defer-datasource-initialization=true
```

Restart the app and verify that the data appears in the H2 console.

---

### **Logging Startup**

Modify the main class to implement `ApplicationRunner`:

```java
@SpringBootApplication
public class PersistenceProjectApplication implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceProjectApplication.class);

    @Override
    public void run(ApplicationArguments args) {
        LOG.info("Starting Spring Boot application...");
    }
}
```

This logs a message when the application starts.

---

## **2.5. Summary**

* We created a **Spring Boot project** named `persistence-project`.
* Defined **entities**: `Task`, `Campaign`, and `Worker` with clear relationships.
* Configured an **H2 in-memory database** with auto schema generation.
* Added **sample data** via `data.sql`.
* Verified database initialization through the **H2 web console**.
* Prepared the base for implementing **Spring Data JPA repositories** in the next lessons.

---

### ✅ **Key Takeaways**

* **Spring Initializr** quickly bootstraps new projects.
* **Spring Data JPA** brings in both **JPA** and **Hibernate**.
* Use **H2** for fast, zero-setup testing.
* Relationships are defined via **JPA annotations** (`@OneToMany`, `@ManyToOne`).
* Automatic schema generation simplifies development during early stages.

---

