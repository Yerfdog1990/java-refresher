
---

# **Lesson Notes: Hibernate**

## **1. Introduction to Hibernate**

Hibernate is a powerful **Object-Relational Mapping (ORM)** framework and a popular **implementation of the Java Persistence API (JPA)** standard. It acts as a layer on top of **JDBC (Java Database Connectivity)** and provides a database-independent persistence solution.

Hibernate automatically maps Java objects (entities) to database tables and manages the SQL required to perform persistence operations such as **inserting, updating, deleting, and querying** records.

### **Key Features**

* Implements the **JPA specification**.
* Provides **automatic SQL generation** and **transaction management**.
* Supports **database independence** through **dialects**.
* Includes **caching**, **lazy loading**, and **relationship management**.
* Can integrate seamlessly with **Spring Boot** and other Java frameworks.

---

## **2. Setting Up Hibernate**

### **2.1 Required Dependencies**

To start using Hibernate, include the `hibernate-core` dependency in your Maven project:

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.2.12.Final</version>
</dependency>
```

For Spring Boot projects, Hibernate is included through the **Spring Data JPA starter**:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

---

### **2.2 Database Configuration (persistence.xml)**

Hibernate connects to a database using configuration details specified in `META-INF/persistence.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" version="2.1">
    <persistence-unit name="my-persistence-unit">
        <description>Hibernate Example</description>
        <properties>
            <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQL94Dialect"/>
            <property name="javax.persistence.jdbc.driver" value="org.postgresql.Driver"/>
            <property name="javax.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/recipes"/>
            <property name="javax.persistence.jdbc.user" value="postgres"/>
            <property name="javax.persistence.jdbc.password" value="postgres"/>
        </properties>
    </persistence-unit>
</persistence>
```

**Important properties:**

* `hibernate.dialect` – specifies database type (e.g., PostgreSQL, MySQL).
* `javax.persistence.jdbc.*` – defines connection parameters.

---

## **3. Bootstrapping Hibernate**

Hibernate can be bootstrapped in two main ways:

### **3.1 Using JPA Bootstrapping API**

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");
EntityManager em = emf.createEntityManager();
em.getTransaction().begin();
```

After performing operations, always close the EntityManager:

```java
em.getTransaction().commit();
em.close();
```

---

### **3.2 Using Hibernate’s Native Bootstrapping API**

You can also configure Hibernate natively using `hibernate.cfg.xml`:

```xml
<hibernate-configuration>
    <session-factory>
        <property name="dialect">org.hibernate.dialect.PostgreSQLDialect</property>
        <property name="connection.driver_class">org.postgresql.Driver</property>
        <property name="connection.url">jdbc:postgresql://localhost:5432/recipes</property>
        <property name="connection.username">postgres</property>
        <property name="connection.password">postgres</property>
        <property name="hbm2ddl.auto">create</property>
    </session-factory>
</hibernate-configuration>
```

**Java Example:**

```java
ServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
        .configure().build();

SessionFactory sessionFactory = new MetadataSources(standardRegistry)
        .addAnnotatedClass(Author.class)
        .buildMetadata()
        .buildSessionFactory();

Session session = sessionFactory.openSession();
```

---

## **4. Defining Entities in Hibernate**

Entities represent database tables. Each entity must:

* Be annotated with `@Entity`
* Have a primary key annotated with `@Id`
* Contain a no-argument constructor
* Follow standard JavaBean conventions (getters/setters)

### **Basic Entity Example**

```java
@Entity
public class Book {

    @Id
    private Long id;

    private String title;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
```

---

### **Customizing the Mapping**

You can specify table and column details using `@Table` and `@Column`:

```java
@Entity
@Table(name = "books")
public class Book {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;
}
```

---

### **Primary Key Generation**

```java
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
@Column(name = "id", updatable = false, nullable = false)
private Long id;
```

---

### **Enumerations and Temporal Fields**

**Enum Example:**

```java
@Enumerated(EnumType.STRING)
private BookStatus status;
```

**Date Example:**

```java
@Temporal(TemporalType.DATE)
private Date publishedOn;
```

---

## **5. Modeling Relationships**

Hibernate supports multiple relationship types, which mirror database relationships.

| Annotation    | Description                             |
| ------------- | --------------------------------------- |
| `@OneToOne`   | One entity relates to exactly one other |
| `@OneToMany`  | One entity relates to many others       |
| `@ManyToOne`  | Many entities relate to one entity      |
| `@ManyToMany` | Many entities relate to many others     |

### **@OneToOne Example**

```java
@Entity
public class AuthorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bio;

    @OneToOne(mappedBy = "profile")
    private Author author;
}
```

### **@OneToMany and @ManyToOne Example**

```java
@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Book> books = new ArrayList<>();
}

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;
}
```

### **@ManyToMany Example**

```java
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany
    @JoinTable(name = "student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> courses = new ArrayList<>();
}

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

---

## **6. CRUD Operations Using EntityManager**

The `EntityManager` handles all **Create, Read, Update, Delete (CRUD)** operations.

### **Persist – Save a New Entity**

```java
Author a = new Author();
a.setFirstName("Thorben");
a.setLastName("Janssen");

em.persist(a);
```

### **Find – Retrieve by ID**

```java
Author a = em.find(Author.class, 1L);
```

### **Merge – Update an Entity**

```java
Author a = em.find(Author.class, 1L);
a.setFirstName("Thorben Updated");
em.merge(a);
```

### **Remove – Delete an Entity**

```java
Author a = em.find(Author.class, 1L);
em.remove(a);
```

### **createQuery – Run JPQL Queries**

```java
List<Book> books = em.createQuery("SELECT b FROM Book b WHERE b.title LIKE :t", Book.class)
                     .setParameter("t", "%Java%")
                     .getResultList();
```

---

## **7. Bootstrapping Hibernate with Spring Boot**

Spring Boot simplifies Hibernate setup through **auto-configuration**.

**Dependencies:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

**Configuration (application.properties):**

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/recipes
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

**Inject EntityManager:**

```java
@Autowired
private EntityManager em;
```

---

## **8. Summary**

* **Hibernate** is a high-level ORM framework implementing **JPA**.
* It maps Java objects to relational tables, eliminating manual SQL coding.
* Uses **EntityManager** for CRUD operations.
* Supports relationship mappings and inheritance structures.
* Can be configured via **JPA (persistence.xml)**, **Hibernate (hibernate.cfg.xml)**, or **Spring Boot auto-configuration**.

**In short:** Hibernate abstracts database complexity, letting developers focus on business logic while managing persistence efficiently.

---

