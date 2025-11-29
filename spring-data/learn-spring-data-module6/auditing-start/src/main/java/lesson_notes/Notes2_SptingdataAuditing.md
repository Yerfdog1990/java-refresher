
## # **Spring Data Auditing**

---

# **1. Introduction to Auditing**

In the context of databases and ORM, **auditing** refers to the process of tracking and recording metadata about changes made to persistent entities. These metadata typically include:

* **Who created the entity**
* **Who last modified the entity**
* **When the entity was created**
* **When the entity was last modified**
* **What type of operation occurred** (insert, update, delete — depending on auditing approach)

This allows you to answer questions such as:

* *When was this record created?*
* *Who updated this record?*
* *Has this entity changed recently?*
* *Who made the last change?*

Auditing provides two major benefits:

### ✔ **Security Insight**

It allows you to detect improper or unauthorized usage patterns.

### ✔ **Operational Visibility**

It makes general system operations observable — important for debugging, analytics, compliance, and system governance.

Spring Data JPA provides **built-in auditing functionality** that automates the population of audit fields, making implementation extremely simple compared to manual auditing.

---

# **2. Enabling Spring Data JPA Auditing**

To use Spring Data JPA’s auditing features, you must explicitly enable them.

## **2.1. Configuration**

Create a config class and annotate it with:

```java
@Configuration
@EnableJpaAuditing
public class AppConfig {
}
```

### What this does internally:

* Registers `AuditingEntityListener`
* Activates annotation-based auditing processing
* Allows Spring Data to populate @CreatedDate, @LastModifiedDate, etc.

---

# **3. Capturing Creation and Update Timestamps**

Spring Data provides annotation-based metadata for capturing timestamps:

* `@CreatedDate`
* `@LastModifiedDate`

These values are automatically set by the framework.

### **3.1. Basic Example**

```java
@Entity
public class Campaign {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}
```

You **do not** manually set these fields — Spring does it during `persist` and `update`.

---

# **4. Using an Embeddable Auditing Class (Recommended Practice)**

Most applications want auditing for *multiple* entities.
Instead of duplicating fields, we move them to a reusable embeddable class.

### **4.1. Create `AuditingData`**

```java
@Embeddable
public class AuditingData {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public LocalDateTime getCreatedDate() { return createdDate; }
    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }

    @Override
    public String toString() {
        return "[createdDate=" + createdDate +
               ", lastModifiedDate=" + lastModifiedDate + "]";
    }
}
```

### **4.2. Use It in an Entity**

```java
@Entity
public class Campaign {

    @Embedded
    private AuditingData auditingData = new AuditingData();

    // other fields...
}
```

### Behavior

Auditing fields remain columns of the **Campaign** table, but are cleanly grouped as a composite embeddable.

---

# **5. AuditingEntityListener**

Spring Data leverages **JPA Entity Listeners** to hook into lifecycle events.

To attach the Spring Data auditing listener to your entity:

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Campaign {
}
```

This ensures timestamps (and later, user information) are set automatically during:

* `@PrePersist` (entity insert)
* `@PreUpdate` (entity update)

---

# **6. Auditing in Action (Timestamps Only)**

## **6.1. Creating a New Campaign**

```java
public void run(ApplicationArguments args) {
    Campaign campaign = new Campaign("C4", "Audited Campaign", "This campaign has auditable fields");
    campaign = campaignRepository.save(campaign);

    LOG.info("New Campaign Auditing Data: {}", campaign.getAuditingData());
}
```

### **Output Example**

```
New Campaign Auditing Data:
[createdDate=2021-08-28T23:07:51.803503,
 lastModifiedDate=2021-08-28T23:07:51.803503]
```

Because this is a new entity, **createdDate == lastModifiedDate**.

---

# **7. Updating an Entity (Audit Timestamp Change)**

```java
Thread.sleep(2000);
campaign.setName("Updated Campaign");
campaign = campaignRepository.save(campaign);

LOG.info("Updated Campaign Auditing Data: {}", campaign.getAuditingData());
```

### Example Output

```
Updated Campaign Auditing Data:
[createdDate=2021-08-28T23:07:51.803503,
 lastModifiedDate=2021-08-28T23:07:53.866631]
```

Spring updated only the `lastModifiedDate`.

---

# **8. Auditing the User Who Changed the Entity**

Spring Data also supports:

* `@CreatedBy`
* `@LastModifiedBy`

But to populate these automatically, Spring needs to know **who** the current user is.

That requires an implementation of **AuditorAware<T>**.

---

## **8.1. Extend the Embeddable Class**

```java
@Embeddable
public class AuditingData {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    public LocalDateTime getCreatedDate() { return createdDate; }
    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }
    public String getCreatedBy() { return createdBy; }
    public String getLastModifiedBy() { return lastModifiedBy; }

    @Override
    public String toString() {
        return "[createdDate=" + createdDate +
               ", lastModifiedDate=" + lastModifiedDate +
               ", createdBy=" + createdBy +
               ", lastModifiedBy=" + lastModifiedBy + "]";
    }
}
```

---

# **9. Implementing AuditorAware**

Spring Data will call your `AuditorAware` implementation **whenever an entity is saved**.

### **Example: Simple Static User**

```java
@Component
public class AuditAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("System-User");
    }
}
```

In a real app, you would replace this with Spring Security authentication details.

---

# **10. Logging the Results**

After enabling this, creating a campaign might produce:

```
New Campaign Auditing Data:
[createdDate=2021-08-28T23:17:30.709864,
 lastModifiedDate=2021-08-28T23:17:30.709864,
 createdBy=System-User,
 lastModifiedBy=System-User]
```

---

# **11. ReactiveAuditorAware (Spring WebFlux / Reactive MongoDB)**

In reactive applications, auditing must be non-blocking.
Spring provides a *reactive* equivalent of `AuditorAware`:

```java
class SpringSecurityReactiveAuditorAware implements ReactiveAuditorAware<User> {

    @Override
    public Mono<User> getCurrentAuditor() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(User.class::cast);
    }
}
```

This works exactly like `AuditorAware`, except:

* It returns a **Mono<T>** instead of `Optional<T>`
* It leverages **reactive security context**

---

# **12. Interface-Based Auditing Metadata (Alternative to Annotations)**

Instead of using annotations like `@CreatedDate`, you can implement:

```java
public interface Auditable<U, ID extends Serializable, T extends TemporalAccessor> {
    void setCreatedBy(U createdBy);
    void setCreatedDate(T createdDate);
    void setLastModifiedBy(U lastModifiedBy);
    void setLastModifiedDate(T lastModifiedDate);
    // getters...
}
```

Or extend the helper class:

```java
public abstract class AbstractAuditable<U, ID extends Serializable> 
        implements Auditable<U, ID, Instant> {
}
```

This approach tightly couples your entities to Spring Data, and is less common today.

---

# **13. General JPA XML Configuration (Legacy / Optional)**

If using XML-based config:

```xml
<persistence-unit-metadata>
  <persistence-unit-defaults>
    <entity-listeners>
      <entity-listener class="org.springframework.data.jpa.domain.support.AuditingEntityListener" />
    </entity-listeners>
  </persistence-unit-defaults>
</persistence-unit-metadata>
```

And enable with:

```xml
<jpa:auditing auditor-aware-ref="yourAuditorAwareBean" />
```

---

# **14. Using Auditing in Spring Data Repositories**

All auditing functionality applies to any entity saved through Spring Data repository methods:

* `save()`
* `saveAll()`
* `delete()`
* `flush()`
* or any custom query annotated with `@Modifying`

As long as the Entity has:

* `@EntityListeners(AuditingEntityListener.class)`
* Auditing annotations
* Auditing is enabled

---
Here we go — **Part 2 of the full detailed lesson notes on Spring Data Auditing**.
This part covers:

* Standard JPA lifecycle auditing
* Hibernate Envers (deep versioning)
* How it compares with Spring Data JPA auditing
* Examples of audit tables, querying history, and configuration

---

# **15. Auditing With Pure JPA (Without Spring Data)**

Spring Data JPA builds on top of standard JPA, but **pure JPA** itself provides a minimalistic auditing mechanism using *entity lifecycle callback annotations*:

* `@PrePersist`
* `@PreUpdate`
* `@PreRemove`

These callbacks fire before JPA executes:

* INSERT
* UPDATE
* DELETE

### Example:

```java
@Entity
public class Bar {

    @Column(name = "operation")
    private String operation;

    @Column(name = "timestamp")
    private long timestamp;

    @PrePersist
    public void onPrePersist() {
        audit("INSERT");
    }

    @PreUpdate
    public void onPreUpdate() {
        audit("UPDATE");
    }

    @PreRemove
    public void onPreRemove() {
        audit("DELETE");
    }

    private void audit(String op) {
        this.operation = op;
        this.timestamp = System.currentTimeMillis();
    }
}
```

### ✔ Advantages of Pure JPA Auditing

* Zero dependencies
* Works in any JPA provider

### ✘ Limitations of Pure JPA Auditing

* You can *only modify non-relationship*, simple fields
* You cannot query audit history
* You cannot track "who" made the change
* No versioning
* Delete events (`@PreRemove`) do not keep deleted rows (they disappear)

---

# **16. Centralizing JPA Callbacks With EntityListeners**

Instead of repeating callbacks in every entity:

### Listener:

```java
public class AuditListener {

    @PrePersist
    @PreUpdate
    @PreRemove
    private void beforeOperation(Object entity) {
        // set metadata — limited to simple fields only
    }
}
```

### Attach listener to entity:

```java
@Entity
@EntityListeners(AuditListener.class)
public class Bar { }
```

This is cleaner, but all pure JPA limitations still apply.

---

# **17. Hibernate Envers — Enterprise-Level Auditing + Version History**

While JPA’s callbacks are limited, **Hibernate Envers** is a dedicated module that provides:

### ✔ Full entity versioning

### ✔ History tables

### ✔ Tracking Insert/Update/Delete

### ✔ Querying historic revisions

### ✔ Comparing versions

### ✔ Works with JPA entities transparently

Hibernate Envers is widely used for systems that require audit trails for compliance:

* Finance
* Healthcare
* Government
* Corporate governance

---

# **18. Enabling Hibernate Envers**

### Maven:

```xml
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-envers</artifactId>
    <version>6.4.4.Final</version>
</dependency>
```

---

# **19. Annotating Entities for Envers**

### To audit the entire entity:

```java
@Entity
@Audited
public class Bar { }
```

### To exclude a field:

```java
@NotAudited
private Set<Foo> fooSet;
```

You may choose:

* audit only the entity
* audit only specific fields

---

# **20. Envers Creates Audit Tables Automatically**

If Hibernate DDL auto-generation is enabled (`create`, `update`):

Tables created:

```
BAR
BAR_AUD
REVINFO
```

### BAR_AUD contains:

| Column     | Meaning                      |
| ---------- | ---------------------------- |
| BAR fields | copy of normal columns       |
| REV        | FK to revision metadata      |
| REVTYPE    | 0=INSERT, 1=UPDATE, 2=DELETE |

### REVINFO contains:

* REV (revision number)
* REVTSTMP (timestamp)

---

# **21. Configuring Envers Properties**

Example: Change audit table suffix

```java
hibernateProperties.setProperty(
  "org.hibernate.envers.audit_table_suffix", "_AUDIT_LOG"
);
```

---

# **22. Querying Audit History**

Envers provides `AuditReader`:

```java
AuditReader reader = AuditReaderFactory.get(entityManager);
```

### Get entity at a given revision:

```java
Bar bar = reader.find(Bar.class, barId, revision);
```

### Get all revisions:

```java
AuditQuery query = reader.createQuery()
    .forRevisionsOfEntity(Bar.class, true, true);

List<Bar> history = query.getResultList();
```

### Order by revision:

```java
query.addOrder(AuditEntity.revisionNumber().desc());
```

---

# **23. Summary: Pure JPA vs Hibernate Envers vs Spring Data JPA Auditing**

| Feature               | Pure JPA Callbacks | Hibernate Envers | Spring Data JPA Auditing         |
| --------------------- | ------------------ | ---------------- | -------------------------------- |
| Tracks timestamps     | ✔                  | ✔                | ✔                                |
| Tracks user           | ✘                  | ✔ (custom)       | ✔ (built-in)                     |
| Tracks DELETE         | ✘                  | ✔                | ✘ (deleted entity gone)          |
| Maintains history     | ✘                  | ✔                | ✘                                |
| Version comparison    | ✘                  | ✔                | ✘                                |
| Query historic data   | ✘                  | ✔                | ✘                                |
| Ease of use           | Medium             | Medium           | Very easy                        |
| Suitable for auditing | Partial            | Full enterprise  | Full but without version history |

**Spring Data JPA auditing is NOT a versioning system.**
For full historic recovery, use **Envers**.

---

# **24. How Spring Data JPA Auditing Works Internally**

Spring Data uses:

* `AuditingEntityListener`
* AOP interception
* Metadata annotations (`@CreatedDate`, etc.)
* A registered `AuditorAware<T>` implementation
* A `DateTimeProvider` (optional override)
* Entity lifecycle callbacks

Workflow:

1. EntityManager triggers persist/update
2. JPA calls AuditingEntityListener
3. Spring injects timestamps + current user

---

# **25. DateTimeProvider Customization**

To override default clock:

```java
@Bean
public DateTimeProvider dateTimeProvider() {
    return () -> Optional.of(OffsetDateTime.now(ZoneOffset.UTC));
}
```

Then in @EnableJpaAuditing:

```java
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
```

---

# **26. Using Auditing With Embedded Metadata**

Spring supports embedding audit fields (recommended):

```java
class Customer {
    private AuditMetadata auditingMetadata;
}
```

Embedded class:

```java
class AuditMetadata {
    @CreatedBy
    private User user;

    @CreatedDate
    private Instant createdDate;
}
```

Works seamlessly.

---

# **27. Spring Security Integration (Typical Setup)**

### Define AuditorAware using SecurityContext:

```java
class SpringSecurityAuditorAware implements AuditorAware<User> {

  @Override
  public Optional<User> getCurrentAuditor() {
    return Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getPrincipal)
            .map(User.class::cast);
  }
}
```

In config:

```java
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
class PersistenceConfig {

  @Bean
  AuditorAware<User> auditorProvider() {
    return new SpringSecurityAuditorAware();
  }
}
```

This automatically populates:

* `@CreatedBy`
* `@LastModifiedBy`

with the authenticated user.

---

# **28. Reactive Auditing (WebFlux + Reactive MongoDB/R2DBC)**

Reactive apps need a non-blocking auditor provider:

```java
class SpringSecurityReactiveAuditorAware implements ReactiveAuditorAware<User> {

  @Override
  public Mono<User> getCurrentAuditor() {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .filter(Authentication::isAuthenticated)
        .map(Authentication::getPrincipal)
        .map(User.class::cast);
  }
}
```

Assign in config:

```java
@EnableReactiveMongoAuditing(auditorAwareRef = "reactiveAuditor")
```

or:

```java
@EnableR2dbcAuditing(auditorAwareRef = "reactiveAuditor")
```

---

# **29. Summary of How Auditing Fits in JPA Ecosystem**

| Aspect                  | Pure JPA           | Hibernate Envers          | Spring Data JPA Auditing |
| ----------------------- | ------------------ | ------------------------- | ------------------------ |
| Purpose                 | Minimal events     | Full versioning           | Simple audit metadata    |
| Code style              | Callback methods   | @Audited + history tables | Annotations on fields    |
| Complexity              | Low                | Medium/High               | Very low                 |
| Use cases               | Basic timestamping | Legal compliance          | Web/business apps        |
| Tracks deleted entities | No                 | Yes                       | No                       |
| Stores history tables   | No                 | Yes                       | No                       |

---

Below is **Part 3 — the Final Section of the Complete Spring Data Auditing Lesson Notes**.
You can copy–paste directly into your Markdown documentation.

---

# **30. COMPLETE CRUD EXAMPLES WITH SPRING DATA JPA AUDITING**

This section demonstrates **full CRUD operations** using:

* Spring Data repositories
* Auditing-enabled entities
* AuditorAware
* Automatic timestamps and user tracking

We continue using our **Campaign** and **AuditingData** examples.

---

# **30.1. Audited Entity Example**

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String name;
    private String description;

    @Embedded
    private AuditingData auditingData = new AuditingData();

    // constructors, getters, setters
}
```

Embedded audit metadata:

```java
@Embeddable
public class AuditingData {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @Override
    public String toString() {
        return "[createdDate=" + createdDate +
               ", lastModifiedDate=" + lastModifiedDate +
               ", createdBy=" + createdBy +
               ", lastModifiedBy=" + lastModifiedBy + "]";
    }
}
```

---

# **30.2. AuditorAware Implementation**

This provides the current authenticated user:

```java
@Component
public class AuditAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // In real applications, integrate with Spring Security
        return Optional.of("System-User");
    }
}
```

---

# **30.3. Enable Auditing**

```java
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class AppConfig {}
```

---

# **30.4. CRUD Repository**

```java
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    @Transactional(readOnly = true)
    Iterable<Campaign> findByNameContaining(String keyword);
}
```

---

# **30.5. CRUD Operations (with auditing automatically applied)**

### **CREATE**

```java
Campaign c = new Campaign("C100", "New Campaign","Description");
Campaign saved = campaignRepository.save(c);

System.out.println(saved.getAuditingData());
// createdDate=..., createdBy=System-User
```

---

### **READ**

```java
Campaign existing = campaignRepository.findById(1L)
    .orElseThrow();
```

---

### **UPDATE**

```java
Campaign existing = repo.findById(1L).orElseThrow();
existing.setName("Renamed Campaign");
Campaign updated = repo.save(existing);

System.out.println(updated.getAuditingData());
// lastModifiedDate changed
// lastModifiedBy=System-User
```

---

### **DELETE**

```java
repo.deleteById(1L);

// Note: Spring Data auditing does NOT store deleted entity history.
```

For delete-history retaining, use **Hibernate Envers** (explained later).

---

# **31. PROGRAMMATIC vs DECLARATIVE TRANSACTION MANAGEMENT WITH AUDITING**

Now we show **equivalent CRUD operations inside transactions**, using:

* Declarative (`@Transactional`)
* Programmatic (`TransactionTemplate`)

---

# **31.1 Declarative Transactional CRUD**

```java
@Service
public class CampaignService {

    @Autowired
    private CampaignRepository repo;

    @Transactional
    public Campaign createCampaign(Campaign c) {
        return repo.save(c);
    }

    @Transactional
    public Campaign updateCampaign(Long id, String newName) {
        Campaign c = repo.findById(id).orElseThrow();
        c.setName(newName);
        return repo.save(c);
    }

    @Transactional
    public void deleteCampaign(Long id) {
        repo.deleteById(id);
    }
}
```

**Spring automatically:**

* Opens a transaction
* Applies auditing (timestamps + user)
* Commits or rolls back

---

# **31.2 Programmatic Transaction Management CRUD**

Useful only when you need fine control (rare today).

```java
@Service
public class ProgrammaticCampaignService {

    @Autowired
    private TransactionTemplate txTemplate;

    @Autowired
    private CampaignRepository repo;

    public Campaign createCampaign(Campaign c) {
        return txTemplate.execute(status -> repo.save(c));
    }

    public Campaign updateCampaign(Long id, String newName) {
        return txTemplate.execute(status -> {
            Campaign c = repo.findById(id).orElseThrow();
            c.setName(newName);
            return repo.save(c);
        });
    }

    public void deleteCampaign(Long id) {
        txTemplate.executeWithoutResult(s -> repo.deleteById(id));
    }
}
```

---

# **31.3 Declarative vs Programmatic — Comparison Table**

| Feature              | Declarative (`@Transactional`) | Programmatic (`TransactionTemplate`) |
| -------------------- | ------------------------------ | ------------------------------------ |
| Ease of use          | ✔ Very easy                    | ✘ Verbose                            |
| Readability          | ✔ Clean                        | ✘ Noisy                              |
| Cross-cutting        | ✔ Best for services            | ✘ Hard to maintain                   |
| Fine-grained control | Moderate                       | ✔ Very high                          |
| Auditing works?      | ✔ Yes                          | ✔ Yes                                |
| Typical use case     | 99% of applications            | Special cases only                   |

Declarative is the modern standard.

---

# **32. ADVANCED: XML CONFIGURATION FOR AUDITING**

If you use XML instead of Java:

```xml
<jpa:auditing auditor-aware-ref="auditorProvider"/>
```

You MUST also register entity listener:

```xml
<persistence-unit-metadata>
  <persistence-unit-defaults>
    <entity-listeners>
      <entity-listener class="org.springframework.data.jpa.domain.support.AuditingEntityListener"/>
    </entity-listeners>
  </persistence-unit-defaults>
</persistence-unit-metadata>
```

---

# **33. SPRING DATA AUDITING + SPRING SECURITY**

Typical real-world AuditorAware:

```java
public class SecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        return Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName);
    }
}
```

Automatically populates:

* createdBy = authenticated user
* lastModifiedBy = authenticated user

---

# **34. REACTIVE AUDITING (WebFlux + Reactive Repositories)**

Use **Mono<T>**, not Optional<T>:

```java
class ReactiveSecurityAuditor implements ReactiveAuditorAware<String> {

    @Override
    public Mono<String> getCurrentAuditor() {

        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName);
    }
}
```

Enable:

```java
@EnableR2dbcAuditing(auditorAwareRef = "reactiveAuditor")
```

Or for Mongo:

```java
@EnableReactiveMongoAuditing(auditorAwareRef="reactiveAuditor")
```

---

# **35. SPRING DATA JPA AUDITING vs HIBERNATE ENVERS**

| Feature                  | Spring Data Auditing        | Hibernate Envers            |
| ------------------------ | --------------------------- | --------------------------- |
| Purpose                  | Record metadata (who, when) | Full historical versioning  |
| Saves historical copies? | ✘ No                        | ✔ Yes                       |
| Tracks deletions?        | ✘ No                        | ✔ Yes                       |
| Rollback possible?       | ✘ No                        | ✔ Yes                       |
| View old versions?       | ✘ No                        | ✔ Yes                       |
| Compare versions?        | ✘ No                        | ✔ Yes                       |
| Best for                 | Software audit metadata     | Legal compliance audit logs |

---

# **36. When to Use Which Approach**

### ✔ Use **Spring Data JPA Auditing** when:

* You only need:

    * createdDate
    * lastModifiedDate
    * createdBy
    * modifiedBy
* Business auditing (not compliance)

### ✔ Use **Envers** when:

* You need full version history
* You must view deleted entities
* You need legal audit trails (financial, medical, gov)

### ✔ Use **Pure JPA Callbacks** when:

* Extremely simple environments
* No Spring

---

# **37. BEST PRACTICES FOR AUDITING**

### ✔ Move auditing fields to an @Embeddable

Keeps entities clean.

### ✔ Never manually set auditing fields

Let Spring handle everything.

### ✔ Prefer JavaConfig over XML

Cleaner and modern.

### ✔ Combine with Spring Security for real users

Critical for production apps.

### ✔ For full versioning, integrate Envers

Spring auditing ≠ versioning system.

---

# **38. FULL WORKING MINI-PROJECT STRUCTURE**

```
src/main/java
 └── com.example
      ├── config
      │     ├── AppConfig.java
      │     └── AuditAwareImpl.java
      ├── entity
      │     ├── Campaign.java
      │     └── AuditingData.java
      ├── repository
      │     └── CampaignRepository.java
      ├── service
      │     ├── CampaignService.java
      │     └── ProgrammaticCampaignService.java
      └── Application.java
```

All parts work together to deliver:

* Entity auditing
* User identification
* Timestamp tracking
* Transaction management
* CRUD operations

---



