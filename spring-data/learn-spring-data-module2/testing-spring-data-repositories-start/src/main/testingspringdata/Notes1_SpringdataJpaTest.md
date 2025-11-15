
---

# **📘 Lesson Notes: Testing Spring Data Repositories**

Testing Spring Data repositories is an essential part of ensuring the reliability of your application’s data-access layer. Unlike simple Java classes, repositories interact directly with a real or embedded database—and this changes how we must test them.

This lesson covers:

1. **Unit Test vs Integration Test**
2. **Using @DataJpaTest**
3. **Injecting TestEntityManager**
4. **Writing Repository Integration Tests**
5. **Examples of CRUD tests**
6. **Testing query methods**
7. **Customizing transaction behavior**
8. **Using TestEntityManager vs Repository**
9. **Testing with real DB vs embedded DB**

---

# ------------------------------------------------------------

# **1. Unit Test or Integration Test?**

Testing Spring Data repositories using **unit tests** is typically **not recommended**.

### ❌ Why NOT Unit Tests?

To unit-test a repository you would need to mock:

* `EntityManager`
* `JpaRepository` internals
* Hibernate behavior
* SQL generation

These mocks do **not** represent real repository behavior. The purpose of repository logic is **database interaction**, and you lose that in mocked tests.

### ✅ Why Integration Tests?

Repository tests should:

* Run actual SQL queries against a real DB (or embedded DB)
* Verify `SELECT`, `INSERT`, `UPDATE`, `DELETE` queries
* Ensure entity mappings are correct
* Ensure constraints (unique, not-null, relationships) work as expected

➡️ **Conclusion:**
**Repository tests MUST be integration tests.**

---

# ------------------------------------------------------------

# **2. Auto-Configured Data JPA Tests (@DataJpaTest)**

Spring Boot provides the `@DataJpaTest` annotation which configures everything required to test repositories.

```java
@DataJpaTest
class CampaignRepositoryIntegrationTest {
}
```

### ✔ What @DataJpaTest configures:

* Loads only **JPA components** (lightweight context)
* Scans `@Entity` classes
* Configures **Spring Data JPA repositories**
* Auto-configures an **embedded database** (H2/HSQL/Derby)
* Enables **SQL logging** (Hibernate shows SQL in test console)
* Each test is wrapped in a **transaction**, which **rolls back** after test

### ✔ What it does NOT configure:

* No controllers
* No services
* No security components
* No custom application beans

This makes it **fast and focused**.

---

## **Disabling transactions**

By default, each test runs inside a rollback transaction.

If you want tests to commit (rare), add:

```java
@Transactional(propagation = Propagation.NOT_SUPPORTED)
```

---

# ------------------------------------------------------------

# **3. Injecting TestEntityManager**

`TestEntityManager` is a special test-friendly wrapper around the regular `EntityManager`.

### ✔ Why use TestEntityManager?

* Allows inserting test data without using repository methods
* Helps isolate repository method behavior
* Supports:

    * `persist()`
    * `find()`
    * `flush()`
    * `clear()`

### Example Integration Test Setup:

```java
@DataJpaTest
class CampaignRepositoryIntegrationTest {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private TestEntityManager entityManager;
}
```

---

# ------------------------------------------------------------

# **4. Creating a Repository Test Class**

A typical repository test:

```java
@DataJpaTest
class CampaignRepositoryIntegrationTest {

    @Autowired
    CampaignRepository campaignRepository;

    @Autowired
    TestEntityManager entityManager;

    // test methods...
}
```

---

# ------------------------------------------------------------

# **5. Testing Repository Methods (CRUD)**

Below are examples of typical repository test cases.

---

## **5.1. Testing INSERT (`save()` creates new entity)**

```java
@Test
void givenNewCampaign_whenSave_thenSuccess() {
    Campaign newCampaign = new Campaign(
        "CTEST-1",
        "Test Campaign 1",
        "Description for campaign CTEST-1"
    );

    Campaign saved = campaignRepository.save(newCampaign);

    Campaign found = entityManager.find(Campaign.class, saved.getId());

    assertThat(found).isEqualTo(newCampaign);
}
```

### ✔ What is being verified?

* Entity persists successfully
* ID is generated
* Data is stored correctly

---

## **5.2. Testing UPDATE (`save()` on an existing entity)**

```java
@Test
void givenCampaignCreated_whenUpdate_thenSuccess() {
    Campaign campaign = new Campaign("CTEST-1", "Old Name", "Description");
    entityManager.persist(campaign);

    campaign.setName("New Campaign Name");
    campaignRepository.save(campaign);

    Campaign found = entityManager.find(Campaign.class, campaign.getId());

    assertThat(found.getName()).isEqualTo("New Campaign Name");
}
```

### ✔ What is being verified?

* Updates propagate to the DB
* Dirty checking and merge operations work

---

## **5.3. Testing SELECT (`findById`)**

```java
@Test
void givenCampaignCreated_whenFindById_thenSuccess() {
    Campaign campaign = new Campaign("CTEST-1", "Test", "Desc");
    entityManager.persist(campaign);

    Optional<Campaign> retrieved = campaignRepository.findById(campaign.getId());

    assertThat(retrieved).contains(campaign);
}
```

### ✔ What is being verified?

* Entity can be retrieved via repository

---

## **5.4. Testing Custom Query (`findByNameContaining`)**

```java
@Test
void givenCampaignCreated_whenFindByNameContaining_thenSuccess() {
    Campaign c1 = new Campaign("CTEST-1", "Test Campaign 1", "Desc");
    Campaign c2 = new Campaign("CTEST-2", "Another Test Campaign", "Desc");

    entityManager.persist(c1);
    entityManager.persist(c2);

    Iterable<Campaign> results = campaignRepository.findByNameContaining("Test");

    assertThat(results).contains(c1, c2);
}
```

---

## **5.5. Testing DELETE (`delete`)**

```java
@Test
void givenCampaignCreated_whenDelete_thenSuccess() {
    Campaign campaign = new Campaign("CTEST-1", "Test Campaign", "Desc");
    entityManager.persist(campaign);

    campaignRepository.delete(campaign);

    Campaign found = entityManager.find(Campaign.class, campaign.getId());

    assertThat(found).isNull();
}
```

### ✔ What is being verified?

* Entity is removed from the DB

---

# ------------------------------------------------------------

# **6. Using a Real Database Instead of Embedded**

By default, `@DataJpaTest` uses H2 in-memory.

To use your real database:

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class CampaignRepositoryIntegrationTest {
}
```

---

# ------------------------------------------------------------

# **7. When Should You Use TestEntityManager?**

| Use Case                             | Use Repository?            | Use TestEntityManager? |
| ------------------------------------ |----------------------------| ---------------------- |
| Insert test data                     | ❌ No (pollutes test scope) | ✅ Yes                  |
| Verify DB state                      | ❌ No                       | ✅ Yes                  |
| Running the actual method under test | ✅ Yes                      | ❌ No                   |
| Read-only repository                 | Limited                    | Useful                 |
| Testing repository logic only        | Yes                        | Yes                    |

---

# ------------------------------------------------------------

# **8. Full Example Project Structure for Repository Tests**

```
src/
 └── test/
     └── java/
         └── com/example/demo/repository/
             ├── CampaignRepositoryIntegrationTest.java
             └── ...
     └── resources/
         ├── application-test.properties
         └── data.sql        (optional)
```

`application-test.properties` example:

```
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
```

---

# ------------------------------------------------------------

# **9. Summary Table**

| Topic             | Explanation                                |
| ----------------- | ------------------------------------------ |
| Unit Test?        | ❌ Not useful for repositories              |
| Integration Test? | ✅ Required                                 |
| @DataJpaTest      | Auto-configures JPA layer only             |
| Embedded DB       | Enabled by default                         |
| Transactions      | Each test rolls back                       |
| TestEntityManager | Used for inserting/verifying data          |
| save() behavior   | Insert or update                           |
| Query tests       | Validate repository method logic           |
| Real DB option    | @AutoConfigureTestDatabase(replace = NONE) |

---

# ------------------------------------------------------------

Here you go — **all items delivered in clean, professional, text-based formats**, without image generation so that I can provide everything in one response.

---

# ✅ **1. Flowchart: “How to Test Spring Data Repositories”**

```
                           ┌────────────────────────┐
                           │  Start Testing Repo    │
                           └─────────────┬──────────┘
                                         │
                                         ▼
                      ┌────────────────────────────────────┐
                      │ Do you need to test DB behaviour?  │
                      └─────────────┬──────────────────────┘
                                    │ Yes
                                    ▼
                   ┌────────────────────────────────────────────┐
                   │ Use Integration Test with @DataJpaTest     │
                   └───────────────────────────┬────────────────┘
                                               │
                                               ▼
                        ┌──────────────────────────────────────┐
                        │ Do you need test data in the DB?     │
                        └─────────────┬────────────────────────┘
                                      │ Yes
                                      ▼
                   ┌────────────────────────────────────────────┐
                   │ Insert test data using TestEntityManager   │
                   │ (avoid using repository for setup)         │
                   └───────────────────────────┬────────────────┘
                                               │
                                               ▼
                         ┌─────────────────────────────────-───┐
                         │ Execute repository method under     │
                         │ test (SELECT/UPDATE/DELETE/etc.)    │
                         └─────────────────────┬───────────────┘
                                               │
                                               ▼
                         ┌────────────────────────────────-────┐
                         │ Verify results using:               │
                         │  - assertions on returned entities  │
                         │  - entityManager.find(…) checks     │
                         └─────────────────────┬───────────────┘
                                               │
                                               ▼
                        ┌──────────────────────────────────────┐
                        │ Transaction rolls back automatically │
                        │ after test                           │
                        └─────────────────────┬───────────-────┘
                                               │
                                               ▼
                                  ┌────────────────────┐
                                  │      End Test      │
                                  └────────────────────┘
```

---

# ✅ **2. Mind Map: Testing Spring Data Repositories**

```
                           Testing Spring Data Repositories
                                       /       |        \
                                      /        |         \
                                     ▼         ▼          ▼
                          Integration Tests   TestEntityManager   @DataJpaTest
                             /    |    \             |             /      |      \
                            /     |     \            |            /       |       \
                           ▼      ▼      ▼           ▼           ▼        ▼        ▼
                   SELECT Tests  INSERT  UPDATE   DB Setup   Auto-config  Rollback   Embedded DB
                     /  |  \                                   |            |            |
                    /   |   \                                  |            |            |
                   ▼    ▼    ▼                              Lightweight   Logs SQL     Entities scanned
         findById   Custom   findAll                     only loads JPA     queries
                     Queries                                 beans
                           \
                            ▼
                       DELETE Tests
```

---

# ✅ **3. Comparison: Repository Testing vs Service Testing**

| Aspect                | Repository Testing                                          | Service Testing                                   |
| --------------------- | ----------------------------------------------------------- | ------------------------------------------------- |
| Purpose               | Validate database interactions (SQL, JPA mappings, queries) | Validate business logic using repositories        |
| Type                  | Always **Integration Test**                                 | Usually **Unit Test**, sometimes Integration      |
| Uses DB?              | Yes (embedded or real)                                      | No (repositories mocked)                          |
| Typical Annotation    | `@DataJpaTest`                                              | `@SpringBootTest` or plain JUnit                  |
| Components Loaded     | Entities, Repositories, Hibernate                           | Services + Mocks                                  |
| TestEntityManager?    | Yes                                                         | No                                                |
| Inserts test data via | TestEntityManager                                           | Mock responses                                    |
| Slower/Faster         | Slower (DB interaction)                                     | Faster (no DB)                                    |
| Verifies              | SQL correctness, mappings, constraints                      | Business logic correctness                        |
| Example scenario      | “Does findByEmail() execute correct query?”                 | “Does registering a user send email + save data?” |

---

# ✅ **4. MockMvc + Repository Combined Test (Controller Integration Test)**

This is used when you want to test:

* Controller layer
* Service layer
* Repository layer
* End-to-end request → DB → response

### ✔ Use Case Example

Testing `/campaigns/{id}` returns correct data and hits the real DB.

---

## **4.1 Sample Structure**

```
Controller → Service → Repository → DB
```

---

## **4.2 Code Example: Full-Stack Integration Test**

### **Controller**

```java
@RestController
@RequestMapping("/campaigns")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @GetMapping("/{id}")
    public ResponseEntity<Campaign> getCampaign(@PathVariable Long id) {
        return campaignService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
```

### **Service**

```java
@Service
public class CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;

    public Optional<Campaign> findById(Long id) {
        return campaignRepository.findById(id);
    }
}
```

---

## **4.3 MockMvc + Repository Test**

```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CampaignControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void whenGetCampaign_thenReturnJson() throws Exception {
        Campaign campaign = new Campaign("CTEST-1", "Test Campaign", "Description");
        entityManager.persist(campaign);

        mockMvc.perform(get("/campaigns/" + campaign.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Campaign"))
                .andExpect(jsonPath("$.code").value("CTEST-1"));
    }
}
```

### ✔ What this test covers:

* Endpoint routing (`/campaigns/{id}`)
* Controller logic
* Service mapping
* Repository call
* Database persistence
* JSON serialization

This is a **true full-stack integration test**.

---


