
---

# 🧭 **Lesson Notes: Jakarta Persistence API (JPA)**

### Module: Java Persistence and ORM

### Level: Intermediate

---

## **1. Overview**

The **Jakarta Persistence API (JPA)** is a **specification** that defines a standard way for Java applications to interact with relational databases through **Object-Relational Mapping (ORM)**.

Instead of writing SQL queries manually, developers use **Java objects (entities)** to interact with data. JPA automatically handles the mapping between Java classes and database tables.

JPA can be used in both **Jakarta EE** and **Java SE** applications.

> ⚙️ JPA itself is only a *specification*. You still need a **persistence provider** (implementation) to make it work.

Common implementations include:

* **EclipseLink** (Reference implementation)
* **Hibernate**
* **Apache OpenJPA**

---

## **2. How JPA Works**

JPA acts as a middle layer between the **application** and the **database**, managing entity lifecycle, transactions, and queries.

📘 **JPA Architecture Diagram**

```
Application
     ↓
EntityManager (manages entities & transactions)
     ↓
Persistence Unit (configured in persistence.xml)
     ↓
JPA Provider (EclipseLink / Hibernate)
     ↓
Database
```

### Components:

| Component            | Description                                                               |
| -------------------- | ------------------------------------------------------------------------- |
| **Entity**           | Java class mapped to a database table                                     |
| **EntityManager**    | Interface that manages entities and performs CRUD operations              |
| **Persistence Unit** | Configuration for database connection and entities (in `persistence.xml`) |
| **JPA Provider**     | The actual implementation (e.g., EclipseLink, Hibernate)                  |

---

## **3. Entities**

An **entity** is a lightweight, persistent domain object that represents a table in the database. Each entity instance corresponds to a row in that table.

### Example: Simple Entity

```java
package de.vogella.jpa.simple.model;

import javax.persistence.*;

@Entity
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String summary;
    private String description;

    // Getters and setters
    public Long getId() { return id; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Todo [id=" + id + ", summary=" + summary + ", description=" + description + "]";
    }
}
```

---

## **4. Persistence Unit**

The **persistence unit** defines database connection settings and entity mappings. It is configured in `META-INF/persistence.xml`.

### Example: persistence.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/persistence
    http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
    version="2.0">

    <persistence-unit name="todos" transaction-type="RESOURCE_LOCAL">
        <class>de.vogella.jpa.simple.model.Todo</class>
        <properties>
            <property name="javax.persistence.jdbc.driver" value="org.apache.derby.jdbc.EmbeddedDriver" />
            <property name="javax.persistence.jdbc.url" value="jdbc:derby:/home/vogella/databases/simpleDb;create=true" />
            <property name="javax.persistence.jdbc.user" value="test" />
            <property name="javax.persistence.jdbc.password" value="test" />
            <property name="eclipselink.ddl-generation" value="create-tables" />
        </properties>
    </persistence-unit>
</persistence>
```

---

## **5. The EntityManager**

The **EntityManager** is the central JPA interface responsible for managing the lifecycle of entities and performing **CRUD operations**.

### Main Methods:

| Method                                          | Purpose                                                |
| ----------------------------------------------- | ------------------------------------------------------ |
| `persist(Object entity)`                        | Save (INSERT) a new entity                             |
| `find(Class<T> entityClass, Object primaryKey)` | Retrieve (SELECT) an entity by ID                      |
| `merge(Object entity)`                          | Update an existing entity                              |
| `remove(Object entity)`                         | Delete an entity                                       |
| `createQuery(String qlString)`                  | Execute JPQL (Java Persistence Query Language) queries |

---

## **6. CRUD Operations in Detail**

### 🟩 **CREATE (persist)**

```java
em.getTransaction().begin();
Todo todo = new Todo();
todo.setSummary("Learn JPA");
todo.setDescription("Study the CRUD operations using EntityManager");
em.persist(todo);
em.getTransaction().commit();
```

✅ Steps:

1. Begin transaction
2. Create new entity instance
3. Call `persist()` to save it
4. Commit to execute SQL `INSERT`

---

### 🟦 **READ (find)**

```java
Todo foundTodo = em.find(Todo.class, 1L);
System.out.println("Found: " + foundTodo);
```

✅ Steps:

1. Call `find()` with entity class and ID
2. JPA translates to `SELECT` SQL
3. Returns entity or `null`

---

### 🟨 **UPDATE (merge)**

```java
em.getTransaction().begin();
Todo existingTodo = em.find(Todo.class, 1L);
existingTodo.setDescription("Updated JPA tutorial");
em.merge(existingTodo);
em.getTransaction().commit();
```

✅ Steps:

1. Retrieve entity
2. Modify fields
3. Call `merge()` to apply changes
4. Commit transaction (`UPDATE` SQL)

---

### 🟥 **DELETE (remove)**

```java
em.getTransaction().begin();
Todo todoToDelete = em.find(Todo.class, 1L);
em.remove(todoToDelete);
em.getTransaction().commit();
```

✅ Steps:

1. Find entity
2. Call `remove()`
3. Commit to execute `DELETE`

---

### 🟧 **QUERY (createQuery)**

```java
Query query = em.createQuery("SELECT t FROM Todo t WHERE t.summary LIKE :keyword");
query.setParameter("keyword", "%JPA%");
List<Todo> results = query.getResultList();

for (Todo t : results) {
    System.out.println(t);
}
```

✅ Steps:

1. Write JPQL query
2. Bind parameters
3. Execute query
4. Process results

---

### **CRUD Process Flow Diagram**

```
┌────────────────────────┐
│        Application      │
│ (Java Objects/Entities) │
└──────────┬──────────────┘
           │
           ▼
┌────────────────────────┐
│     EntityManager      │
│ ─ persist()  →  INSERT │
│ ─ find()     →  SELECT │
│ ─ merge()    →  UPDATE │
│ ─ remove()   →  DELETE │
│ ─ createQuery() → JPQL │
└──────────┬──────────────┘
           │
           ▼
┌────────────────────────┐
│     JPA Provider       │
│ (EclipseLink/Hibernate)│
└──────────┬──────────────┘
           │
           ▼
┌────────────────────────┐
│       Database         │
│ (CRUD operations occur)│
└────────────────────────┘
```

---

### **CRUD Summary Table**

| Operation | Method          | SQL Equivalent    | Description              |
| --------- | --------------- | ----------------- | ------------------------ |
| Create    | `persist()`     | `INSERT`          | Adds new record          |
| Read      | `find()`        | `SELECT`          | Retrieves record by ID   |
| Update    | `merge()`       | `UPDATE`          | Modifies existing record |
| Delete    | `remove()`      | `DELETE`          | Removes record           |
| Query     | `createQuery()` | `SELECT (custom)` | Runs JPQL queries        |

---

## **7. Relationship Mapping**

Entities often relate to each other — e.g., one user has one profile, one department has many employees, etc.
JPA provides annotations to define these relationships.

| Relationship Type | Description                | Example               |
| ----------------- | -------------------------- | --------------------- |
| `@OneToOne`       | One entity ↔ one entity    | User ↔ Profile        |
| `@OneToMany`      | One entity ↔ many entities | Department ↔ Employee |
| `@ManyToOne`      | Many entities ↔ one entity | Employee ↔ Department |
| `@ManyToMany`     | Many ↔ many                | Student ↔ Course      |

---

### **7.1. @OneToOne Relationship**

Example: `User ↔ Profile`

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private Profile profile;
}
```

```java
@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String bio;
}
```

Diagram:

```
User (id, username, profile_id)
      │
      ▼
Profile (id, full_name, bio)
```

---

### **7.2. @OneToMany and @ManyToOne Relationship**

Example: `Department ↔ Employee`

```java
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Employee> employees = new ArrayList<>();
}
```

```java
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String role;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
```

Diagram:

```
Department (id, name)
        │
        │ 1 ──── * 
        ▼
Employee (id, name, role, department_id)
```

---

### **7.3. @ManyToMany Relationship**

Example: `Student ↔ Course`

```java
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany
    @JoinTable(
        name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses = new ArrayList<>();
}
```

```java
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @ManyToMany(mappedBy = "courses")
    private List<Student> students = new ArrayList<>();
}
```

Diagram:

```
Student (id, name)
     * │
       │
       ▼
  student_course (student_id, course_id)
       │
       │ *
       ▼
Course (id, title)
```

---

### **Relationship Summary Table**

| Relationship                | Description | Example               | Mapping Table    |
| --------------------------- | ----------- | --------------------- | ---------------- |
| `@OneToOne`                 | One ↔ one   | User ↔ Profile        | No               |
| `@OneToMany` / `@ManyToOne` | One ↔ many  | Department ↔ Employee | No               |
| `@ManyToMany`               | Many ↔ many | Student ↔ Course      | Yes (Join table) |

---

### **Visual Summary**

```
@OnetoOne     @OneToMany/@ManyToOne     @ManyToMany
───────────   ───────────────────────    ────────────────
User          Department                 Student
 │              │                        │
 ▼              ▼                        ▼
Profile        Employee (many)           Course (many)
```

---

## **8. Project Structure Example**

A typical JPA project (using EclipseLink):

```
src/
 ├── de.vogella.jpa.eclipselink.main/
 │    └── Main.java
 ├── de.vogella.jpa.eclipselink.model/
 │    ├── Family.java
 │    ├── Job.java
 │    └── Person.java
 ├── META-INF/
 │    └── persistence.xml
test/
 └── de.vogella.jpa.eclipselink.main/
      └── JpaTest.java
```

---

## **9. Testing with JUnit**

```java
@Test
public void checkAvailablePeople() {
    EntityManager em = factory.createEntityManager();
    Query q = em.createQuery("SELECT m FROM Person m");
    assertTrue(q.getResultList().size() == 40);
    em.close();
}
```

---

## **10. Key Takeaways**

* ✅ **EntityManager** is the core JPA interface for CRUD operations
* ✅ **Transactions** are required for all modifications
* ✅ **JPQL** allows flexible, object-based queries
* ✅ **Relationships** define how entities connect through annotations
* ✅ **EclipseLink** and **Hibernate** are the most common JPA providers
* ✅ JPA promotes cleaner, object-oriented data access


