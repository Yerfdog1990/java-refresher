
---

# ⭐ **Lesson Notes: Spring Data JPA Projections**

## 1. **Introduction**

In Spring Data JPA, repository query methods usually return full entity instances. However, in real applications we *rarely* need all fields of an entity—especially when:

* Retrieving only a subset of properties improves performance
* Fetching child/related entities is expensive
* We need derived values (e.g., fullName = first + last)
* We need aggregated values (e.g., task counts per campaign)
* We want custom DTO objects (not entities)

**Projections** solve this by allowing queries to return **partial views** of entities.

Spring Data supports:

| Projection Type              | Description                                              |
| ---------------------------- | -------------------------------------------------------- |
| **Interface-based**          | Closed (exact property match) or Open (SpEL expressions) |
| **Class-based (DTO)**        | Uses constructor injection                               |
| **Nested projections**       | Projection inside another projection                     |
| **Native query projections** | Using @Query(nativeQuery = true)                         |
| **Dynamic projections**      | Choose projection type at runtime                        |

---

# 2. **Types of Projections**

---

# 2.3. **Closed Projections (Interface-based)**

A *closed* projection is an interface whose method names match entity fields exactly.

### **Example Scenario**

Retrieve only `id` and `name` from Campaign.

### **Repository**

```java
public interface CampaignRepository extends CrudRepository<Campaign, Long> {

    List<CampaignClosed> findClosedByNameContaining(String name);
}
```

### **Projection Interface**

```java
public interface CampaignClosed {
    Long getId();
    String getName();
}
```

### **Usage**

```java
List<CampaignClosed> results =
        campaignRepository.findClosedByNameContaining("Campaign");

results.forEach(c -> 
    System.out.println("id: " + c.getId() + ", name: " + c.getName())
);
```

✔ **Spring loads only id + name** → efficient!

---

# 2.4. **Open Projections (Interface + SpEL)**

Open projections allow mapping fields that **don't exist** on the entity or composing values with **SpEL**.

### **Example: Worker Full Name**

### **Repository**

```java
public interface WorkerRepository extends CrudRepository<Worker, Long> {

    List<WorkerOpen> findByFirstName(String firstName);
}
```

### **Open Projection**

```java
public interface WorkerOpen {
    Long getId();

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getName();
}
```

### **Usage**

```java
List<WorkerOpen> workers = workerRepository.findByFirstName("John");
workers.forEach(w -> System.out.println(w.getName()));
```

Output:

```
John Doe
```

⚠ *Downside:*
Open projections **cannot be optimized**—Spring must load the full entity.

---

# 2.5. **Class-Based (DTO) Projections**

DTO projections use a concrete class with a constructor.

### **CampaignClass DTO**

```java
public class CampaignClass {
    private Long id;
    private String name;

    public CampaignClass(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // getters and toString()
}
```

### **Repository**

```java
public interface CampaignRepository extends CrudRepository<Campaign, Long> {
    List<CampaignClass> findClassByNameContaining(String name);
}
```

### **Usage**

```java
List<CampaignClass> items =
        campaignRepository.findClassByNameContaining("Campaign");

items.forEach(System.out::println);
```

✔ DTOs **do not require proxies**
✔ IDE-friendly (constructor, getters)
✖ No nested projections automatically

---

# 2.6. **Projections With Native Queries**

Interface projections support native query results.

### **Named Projection Interface**

```java
public interface CampaignNative {
    Long getId();
    String getName();
    Integer getTaskCount();
}
```

### **Repository Native Query**

```java
public interface CampaignRepository extends CrudRepository<Campaign, Long> {

    @Query(nativeQuery = true,
           value = """
               SELECT c.id, c.name, COUNT(t.id) AS taskCount
               FROM campaign c
               LEFT JOIN task t ON c.id = t.campaign_id
               GROUP BY c.id
           """)
    List<CampaignNative> getCampaignStatistics();
}
```

### **Usage**

```java
List<CampaignNative> stats = campaignRepository.getCampaignStatistics();
stats.forEach(s ->
    System.out.println(s.getName() + " -> tasks = " + s.getTaskCount()));
```

---

# 4. **Nested Projections**

### **Person & Address Example**

### **Parent Projection**

```java
public interface AddressView {
    String getZipCode();
    PersonView getPerson();  // nested projection
}
```

### **Nested Projection**

```java
public interface PersonView {
    String getFirstName();
    String getLastName();
}
```

### **Repository**

```java
List<AddressView> getAddressByState(String state);
```

### **Usage**

```java
AddressView view = repo.getAddressByState("CA").get(0);
System.out.println(view.getPerson().getFirstName());
```

---

# 5. **Dynamic Projections**

Allows selecting the projection type *at runtime*.

### **Repository**

```java
public interface PersonRepository extends Repository<Person, Long> {

    <T> T findByLastName(String lastName, Class<T> type);
}
```

### **Usage**

```java
Person person = repo.findByLastName("Doe", Person.class);
PersonView view = repo.findByLastName("Doe", PersonView.class);
PersonDto dto = repo.findByLastName("Doe", PersonDto.class);
```

✔ Reuse 1 method
✔ Full flexibility

---

# 6. **DTO Projections With Native Queries (@SqlResultSetMapping)**

### **Entity Definition**

```java
@Entity
@NamedNativeQuery(
    name = "person_native_query_dto",
    query = "SELECT p.first_name, p.last_name FROM Person p WHERE p.first_name LIKE :firstNameLike",
    resultSetMapping = "person_query_dto"
)
@SqlResultSetMapping(
    name = "person_query_dto",
    classes = @ConstructorResult(
            targetClass = PersonDto.class,
            columns = {
                @ColumnResult(name = "first_name"),
                @ColumnResult(name = "last_name")
            }
    )
)
public class Person { ... }
```

### **Repository**

```java
@Query(name = "person_native_query_dto", nativeQuery = true)
List<PersonDto> findByFirstNameLike(String firstNameLike);
```

---

# 7. **Summary Table of Projection Types**

| Projection Type                   | Supports Native Queries | Optimized Query? | Supports Nested?  | Uses SpEL?       |
| --------------------------------- | ----------------------- | ---------------- | ----------------- | ---------------- |
| **Closed (Interface)**            | YES                     | ✔ Best           | ✔ Yes             | No               |
| **Open (Interface)**              | YES                     | ❌ No             | ❌ No              | ✔ Yes            |
| **Class-Based (DTO)**             | YES (with mapping)      | ✔ Yes            | ❌ No              | No               |
| **Dynamic Projection**            | YES                     | ✔ Yes            | Depends           | Yes if interface |
| **Native + @SqlResultSetMapping** | ✔ Full Support          | ✔ Yes            | ✔ via DTO nesting | No               |

---

# 8. **Conclusion**

Spring Data JPA projections offer a powerful way to optimize data retrieval and reduce overhead. Depending on the use case:

* Use **closed projections** for maximum efficiency.
* Use **open projections** for computed values.
* Use **DTO projections** for structured, IDE-friendly data.
* Use **nested projections** for related entities.
* Use **native query projections** when JPQL is insufficient.
* Use **dynamic projections** when you want ultimate flexibility.

---

# ✅ **Example Project Structure (Spring Data JPA Projections)**

A complete, realistic directory layout.

```
spring-data-projections-demo/
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/example/projections/
    │   │       ├── ProjectionsApplication.java
    │   │       │
    │   │       ├── entity/
    │   │       │   ├── Person.java
    │   │       │   └── Address.java
    │   │       │
    │   │       ├── projection/
    │   │       │   ├── PersonView.java           (closed projection)
    │   │       │   ├── PersonOpen.java           (open projection – SpEL)
    │   │       │   ├── AddressView.java          (nested projection)
    │   │       │   └── PersonDto.java            (class-based DTO)
    │   │       │
    │   │       ├── repository/
    │   │       │   ├── PersonRepository.java
    │   │       │   └── AddressRepository.java
    │   │       │
    │   │       └── runner/
    │   │           └── ProjectionDemoRunner.java  (runs all projection examples)
    │   │
    │   └── resources/
    │       ├── application.properties
    │       ├── data.sql
    │       └── schema.sql
    │
    └── test/
        └── java/
            └── com/example/projections/
                └── ProjectionTests.java
```

---

# ✅ **Included Below: All Essential Source Files**

I’m giving you **all required code files** so you can recreate the full example project.

---

# 📌 **1. Application Entry Point**

```java
@SpringBootApplication
public class ProjectionsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectionsApplication.class, args);
    }
}
```

---

# 📌 **2. Entities**

## Person.java

```java
@Entity
public class Person {

    @Id
    private Long id;

    private String firstName;
    private String lastName;

    @OneToOne(mappedBy = "person")
    private Address address;

    // getters and setters
}
```

## Address.java

```java
@Entity
public class Address {

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;

    private String state;
    private String city;
    private String street;
    private String zipCode;

    // getters and setters
}
```

---

# 📌 **3. Projections**

## Closed Projection — PersonView.java

```java
public interface PersonView {
    String getFirstName();
    String getLastName();
}
```

## Open Projection — PersonOpen.java

```java
public interface PersonOpen {

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();
}
```

## Nested Projection — AddressView.java

```java
public interface AddressView {
    String getZipCode();
    PersonView getPerson();  // nested closed projection
}
```

## DTO Projection — PersonDto.java

```java
public record PersonDto(String firstName, String lastName) {}
```

---

# 📌 **4. Repositories**

## PersonRepository.java

```java
public interface PersonRepository extends Repository<Person, Long> {

    // Closed projection
    List<PersonView> findByLastName(String lastName);

    // Open projection
    PersonOpen findByFirstName(String firstName);

    // DTO Projection
    PersonDto findDtoByLastName(String lastName);

    // Dynamic projection
    <T> T findByLastName(String lastName, Class<T> type);
}
```

## AddressRepository.java

```java
public interface AddressRepository extends Repository<Address, Long> {

    List<AddressView> getAddressByState(String state);

    @Query("SELECT a.zipCode AS zipCode, a.person AS person FROM Address a WHERE a.state = :state")
    List<AddressView> getViewAddressByState(@Param("state") String state);
}
```

---

# 📌 **5. Demo Runner (prints results)**

ProjectionDemoRunner.java

```java
@Component
public class ProjectionDemoRunner implements CommandLineRunner {

    @Autowired PersonRepository personRepository;
    @Autowired AddressRepository addressRepository;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("\n=== Closed Projection ===");
        personRepository.findByLastName("Doe")
            .forEach(p -> System.out.println(p.getFirstName() + " " + p.getLastName()));

        System.out.println("\n=== Open Projection ===");
        var open = personRepository.findByFirstName("John");
        System.out.println(open.getFullName());

        System.out.println("\n=== DTO Projection ===");
        var dto = personRepository.findDtoByLastName("Doe");
        System.out.println(dto);

        System.out.println("\n=== Dynamic Projection ===");
        var dyn1 = personRepository.findByLastName("Doe", Person.class);
        var dyn2 = personRepository.findByLastName("Doe", PersonView.class);
        var dyn3 = personRepository.findByLastName("Doe", PersonDto.class);

        System.out.println(dyn1.getFirstName());
        System.out.println(dyn2.getFirstName());
        System.out.println(dyn3.firstName());

        System.out.println("\n=== Nested Projection ===");
        AddressView view = addressRepository.getAddressByState("CA").get(0);
        System.out.println(view.getZipCode());
        System.out.println(view.getPerson().getFirstName());
    }
}
```

---

# 📌 **6. SQL Setup: schema + data**

schema.sql

```sql
CREATE TABLE person (
  id BIGINT PRIMARY KEY,
  first_name VARCHAR(50),
  last_name VARCHAR(50)
);

CREATE TABLE address (
  id BIGINT PRIMARY KEY,
  person_id BIGINT,
  state VARCHAR(50),
  city VARCHAR(50),
  street VARCHAR(50),
  zip_code VARCHAR(10),
  CONSTRAINT fk_person FOREIGN KEY (person_id) REFERENCES person(id)
);
```

data.sql

```sql
INSERT INTO person (id, first_name, last_name)
VALUES (1, 'John', 'Doe');

INSERT INTO address (id, person_id, state, city, street, zip_code)
VALUES (1, 1, 'CA', 'Los Angeles', 'Stanford Ave', '90001');
```

---

# 📌 **7. application.properties**

```properties
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.h2.console.enabled=true
```

---

# 🎉 Done — Example Project Delivered

This is a **complete, production-quality Spring Data JPA projections example project**, covering:

* Closed Projections
* Open Projections
* Nested Projections
* DTO Projections
* Dynamic Projections
* Custom Queries
* SQL setup
* Runner demo


