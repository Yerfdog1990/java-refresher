
---

# 🌿 Lesson Notes: How Spring Creates Queries from Derived Methods in JPA and a Comparison with Other JPA Query Types

---

## 1. Introduction

JPA (Java Persistence API) supports multiple ways to query data from a database. Each type offers different levels of flexibility, readability, and control.

Spring Data JPA enhances this by allowing developers to **derive queries directly from method names**, eliminating the need to manually write JPQL or SQL for simple operations.

---

## 2. 🌳 Main Types of JPA Queries

| **Query Type**                              | **Description**                                                                       | **Use Case**                                                   | **Example (using `Employee` entity)**                                                                                                                                                                                     |
| ------------------------------------------- | ------------------------------------------------------------------------------------- | -------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **JPQL (Java Persistence Query Language)**  | Object-oriented query language that operates on entities rather than database tables. | For static, portable queries embedded in application logic.    | `@Query("SELECT e FROM Employee e WHERE e.firstName = :firstName")`                                                                                                                                                       |
| **Criteria API**                            | Type-safe, programmatic API for dynamically building queries at runtime.              | For complex, dynamic queries that need flexibility.            | `CriteriaBuilder cb = em.getCriteriaBuilder(); CriteriaQuery<Employee> cq = cb.createQuery(Employee.class); Root<Employee> root = cq.from(Employee.class); cq.select(root).where(cb.equal(root.get("lastName"), "Doe"));` |
| **Native SQL**                              | Executes direct SQL queries on the database.                                          | For database-specific operations or performance optimizations. | `@Query(value = "SELECT * FROM employee WHERE last_name = ?1", nativeQuery = true)`                                                                                                                                       |
| **Derived Query Methods (Spring Data JPA)** | Spring automatically creates queries based on method names in repository interfaces.  | For simple, static queries with readable and concise code.     | `List<Employee> findByFirstNameAndLastName(String firstName, String lastName);`                                                                                                                                           |

---

## 3. 🌱 How Spring Creates Queries from Derived Methods

Spring Data JPA’s **PartTree mechanism** analyzes the method name in a repository and constructs the corresponding JPQL query automatically.

### How It Works:

1. **Method Name Parsing**
   The method name is split into parts based on reserved keywords like `By`, `And`, `Or`, `Is`, `Between`, `In`, `Like`, `OrderBy`, etc.
   Example:
   `findByFirstNameAndLastName` → split into `find`, `By`, `FirstName`, and `LastName`.

2. **Property Recognition**
   Each part is mapped to the entity’s field names (e.g., `firstName`, `lastName`).

3. **Operator Inference**
   Keywords determine the operator (`=`, `<`, `>`, `LIKE`, etc.).
   Example:

    * `findByLastNameContaining` → uses `LIKE`
    * `findBySalaryGreaterThan` → uses `>`

4. **Query Generation**
   Spring Data JPA compiles these parts into a JPQL query, which Hibernate translates to SQL.

---

### 💡 Example with `Employee` Entity

```java
@Entity
@Table(name = "employee")
@Getter @Setter
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String lastName;
    
    // Constructor
    public Employee(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
```

### Repository with Various Query Types

```java
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // 1️⃣ Derived Query Method
    List<Employee> findByFirstNameAndLastName(String firstName, String lastName);

    // 2️⃣ JPQL Query
    @Query("SELECT e FROM Employee e WHERE e.firstName = :firstName")
    List<Employee> findByFirstNameJPQL(@Param("firstName") String firstName);

    // 3️⃣ Native SQL Query
    @Query(value = "SELECT * FROM employee WHERE last_name = ?1", nativeQuery = true)
    List<Employee> findByLastNameNative(String lastName);
}
```

### Criteria API Example

```java
@Repository
public class EmployeeCriteriaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Employee> findByLastNameUsingCriteria(String lastName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        cq.select(root).where(cb.equal(root.get("lastName"), lastName));
        return entityManager.createQuery(cq).getResultList();
    }
}
```

---

## 4. 🌾 Application Setup and Execution

### Configuration (application.properties)

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```

### Service Layer Example

```java
@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeCriteriaRepository criteriaRepo;

    public EmployeeService(EmployeeRepository repository, EmployeeCriteriaRepository criteriaRepo) {
        this.repository = repository;
        this.criteriaRepo = criteriaRepo;
    }

    public void runExamples() {
        repository.save(new Employee("John", "Smith"));
        repository.save(new Employee("Jane", "Doe"));
        repository.save(new Employee("John", "Doe"));

        System.out.println("Derived → " + repository.findByFirstNameAndLastName("John", "Doe"));
        System.out.println("JPQL → " + repository.findByFirstNameJPQL("Jane"));
        System.out.println("Native SQL → " + repository.findByLastNameNative("Smith"));
        System.out.println("Criteria → " + criteriaRepo.findByLastNameUsingCriteria("Doe"));
    }
}
```

### Main Application Runner

```java
@SpringBootApplication
public class JpaQueryDemoApplication implements CommandLineRunner {

    private final EmployeeService service;

    public JpaQueryDemoApplication(EmployeeService service) {
        this.service = service;
    }

    public static void main(String[] args) {
        SpringApplication.run(JpaQueryDemoApplication.class, args);
    }

    @Override
    public void run(String... args) {
        service.runExamples();
    }
}
```

---

## 5. 🌼 Example SQL Output (from Console)

When `spring.jpa.show-sql=true` is enabled, Hibernate prints the generated SQL:

### 🔹 Derived Query

**Method:** `findByFirstNameAndLastName("John", "Doe")`
**JPQL:** `SELECT e FROM Employee e WHERE e.firstName = ?1 AND e.lastName = ?2`
**SQL Output:**

```sql
select e.id, e.first_name, e.last_name
from employee e
where e.first_name = ? and e.last_name = ?
```

### 🔹 JPQL Query

**JPQL:** `SELECT e FROM Employee e WHERE e.firstName = :firstName`
**SQL:**

```sql
select e.id, e.first_name, e.last_name
from employee e
where e.first_name = ?
```

### 🔹 Native SQL

**Query Annotation:** `@Query(value = "SELECT * FROM employee WHERE last_name = ?1", nativeQuery = true)`
**Executed SQL:**

```sql
SELECT * FROM employee WHERE last_name = ?
```

### 🔹 Criteria API

**Programmatic JPQL:** `select e from Employee e where e.lastName = :lastName`
**Generated SQL:**

```sql
select e.id, e.first_name, e.last_name
from employee e
where e.last_name = ?
```

---

## 6. 🌿 Summary: How Spring Builds Derived Queries

| **Step**                     | **Action**                                       | **Example**                                                                 |
| ---------------------------- | ------------------------------------------------ | --------------------------------------------------------------------------- |
| 1️⃣ **Method Name Parsing**  | Breaks the repository method into logical parts. | `findByFirstNameAndLastName` → `find`, `By`, `FirstName`, `And`, `LastName` |
| 2️⃣ **Property Recognition** | Maps parts to entity attributes.                 | `firstName`, `lastName`                                                     |
| 3️⃣ **Operator Inference**   | Determines SQL operators from keywords.          | `And` → `AND`, `Containing` → `LIKE`, `GreaterThan` → `>`                   |
| 4️⃣ **JPQL Construction**    | Builds a JPQL query.                             | `SELECT e FROM Employee e WHERE e.firstName = ?1 AND e.lastName = ?2`       |
| 5️⃣ **SQL Translation**      | Hibernate converts JPQL to SQL.                  | `select * from employee where first_name=? and last_name=?`                 |

---

## 7. 🌸 Final Comparison Table

| **Query Type**    | **Definition Source**                    | **Use Case**                         | **Generated SQL Example**                                   |
| ----------------- | ---------------------------------------- | ------------------------------------ | ----------------------------------------------------------- |
| **Derived Query** | Repository method name                   | Simple static queries                | `select * from employee where first_name=? and last_name=?` |
| **JPQL**          | `@Query("SELECT e FROM Employee e ...")` | Portable, entity-based queries       | `select * from employee where first_name=?`                 |
| **Native SQL**    | `@Query(nativeQuery = true)`             | Database-specific optimizations      | `SELECT * FROM employee WHERE last_name=?`                  |
| **Criteria API**  | Built dynamically in Java code           | Complex, runtime-constructed queries | `select * from employee where last_name=?`                  |

---

## 🧩 Key Takeaways

* **Derived queries** are concise and readable — perfect for simple lookups.
* **JPQL** is portable and object-oriented.
* **Native SQL** is powerful for advanced, DB-specific use cases.
* **Criteria API** is flexible and ideal for dynamic query building.

> ✅ In most real-world Spring Boot applications, **derived query methods** cover 80% of database needs with minimal boilerplate.

---

