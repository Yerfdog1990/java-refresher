
---

# Creating ACL Schema and Inserting Data for Spring Security ACL

---

## 1. Why Spring Security ACL Needs a Database Schema

Spring Security ACL (Access Control List) provides **fine-grained authorization** at the **domain object level**.

Unlike role-based security (e.g. `ROLE_USER`, `ROLE_ADMIN`), ACL answers questions such as:

* *Can Alice delete **this specific Student record**?*
* *Can Carol read Student #3 but not Student #4?*
* *Can permissions be inherited from a parent object?*

To do this, Spring Security ACL stores permissions in **relational tables**.

---

## 2. User Schema (Authentication & Role-Based Authorization)

Before ACL can work, users must be authenticated and assigned **authorities (roles)**.

Spring Security’s `JdbcDaoImpl` expects **two tables**:

---

### 2.1 `users` Table

Stores authentication information.

```sql
create table users (
    username varchar_ignorecase(50) not null primary key,
    password varchar_ignorecase(500) not null,
    enabled boolean not null
);
```

**Purpose**

* Used during login
* Loaded by `UserDetailsService`

**Key Columns**

* `username` → principal identifier
* `password` → encrypted password (BCrypt recommended)
* `enabled` → account status

---

### 2.2 `authorities` Table

Stores user roles.

```sql
create table authorities (
    username varchar_ignorecase(50) not null,
    authority varchar_ignorecase(50) not null,
    constraint fk_authorities_users
        foreign key(username) references users(username)
);

create unique index ix_auth_username
    on authorities (username, authority);
```

**Purpose**

* Supports role-based checks like `hasRole('ADMIN')`
* Roles are later reused inside ACL as **SIDs**

---

### 2.3 Example User & Role Mapping

| User  | Role       |
| ----- | ---------- |
| Alice | ROLE_USER  |
| Carol | ROLE_USER  |
| Alex  | ROLE_USER  |
| Bob   | ROLE_ADMIN |

At this point, **no object-level security exists yet**.

---

## 3. ACL Schema Overview

Spring Security ACL uses **four core tables**:

| Table                 | Responsibility                       |
| --------------------- | ------------------------------------ |
| `acl_sid`             | Who is requesting access             |
| `acl_class`           | What type of object is protected     |
| `acl_object_identity` | Which object instance is protected   |
| `acl_entry`           | What permission is granted or denied |

---

## 4. ACL Schema (PostgreSQL)

### 4.1 `acl_sid` – Security Identities

```sql
create table acl_sid (
    id bigserial not null primary key,
    principal boolean not null,
    sid varchar(100) not null,
    constraint unique_uk_1 unique(sid, principal)
);
```

**What it stores**

* Users **or** roles
* Examples:

    * `principal = true` → `Alice`
    * `principal = false` → `ROLE_ADMIN`

**Why roles appear here**

* ACL can grant permissions to **roles**, not just users

---

### 4.2 `acl_class` – Domain Object Types

```sql
create table acl_class (
    id bigserial not null primary key,
    class varchar(100) not null,
    constraint unique_uk_2 unique(class)
);
```

**What it stores**

* Fully qualified Java class names
* Example:

  ```
  springsecurity.lesson2datastructureofacl.persistence.entity.Student
  ```

This links ACL records to **entity classes**, not database tables.

---

### 4.3 `acl_object_identity` – Protected Objects

```sql
create table acl_object_identity (
    id bigserial primary key,
    object_id_class bigint not null,
    object_id_identity varchar(36) not null,
    parent_object bigint,
    owner_sid bigint,
    entries_inheriting boolean not null,
    constraint unique_uk_3 unique(object_id_class, object_id_identity),
    constraint foreign_fk_1 foreign key(parent_object)
        references acl_object_identity(id),
    constraint foreign_fk_2 foreign key(object_id_class)
        references acl_class(id),
    constraint foreign_fk_3 foreign key(owner_sid)
        references acl_sid(id)
);
```

**Key Concepts**

* `object_id_identity` → domain object ID (`student.id`)
* `owner_sid` → owner of the object
* `parent_object` → enables ACL inheritance
* `entries_inheriting = true` → child objects inherit permissions

**Example**

> Student #3 is protected, owned by ROLE_ADMIN, and inherits permissions.

---

### 4.4 `acl_entry` – Permissions

```sql
create table acl_entry (
    id bigserial primary key,
    acl_object_identity bigint not null,
    ace_order int not null,
    sid bigint not null,
    mask integer not null,
    granting boolean not null,
    audit_success boolean not null,
    audit_failure boolean not null,
    constraint unique_uk_4 unique(acl_object_identity, ace_order),
    constraint foreign_fk_4 foreign key(acl_object_identity)
        references acl_object_identity(id),
    constraint foreign_fk_5 foreign key(sid)
        references acl_sid(id)
);
```

**This is the heart of ACL.**

Each row means:

> “SID X **is allowed/denied** permission Y on object Z”

---

## 5. Permission Masks (Bitwise)

| Permission | Mask |
| ---------- | ---- |
| READ       | 1    |
| WRITE      | 2    |
| CREATE     | 4    |
| DELETE     | 8    |
| ADMIN      | 16   |

Permissions can be combined using bitwise OR.

---

## 6. Explaining Your Inserted Data (Conceptually)

### 6.1 Why `acl_sid` Has Only Roles

```sql
INSERT INTO acl_sid (id, principal, sid)
VALUES
    (5, false, 'ROLE_ADMIN'),
    (6, false, 'ROLE_USER');
```

* Permissions are granted **at role level**
* All admins share the same permissions
* Individual users are handled by role membership

---

### 6.2 Why All Students Are Owned by ROLE_ADMIN

```sql
INSERT INTO acl_object_identity (...)
VALUES
    (1, 1, '1', NULL, 5, true),
    (2, 1, '2', NULL, 5, true),
    (3, 1, '3', NULL, 5, true),
    (4, 1, '4', NULL, 5, true);
```

**Meaning**

* All Student entities are owned by ROLE_ADMIN
* Admins can manage all students
* Users inherit permissions via entries

---

### 6.3 How Alice, Carol, Alex, Bob Gain Access

| User  | Role       | How ACL Applies                       |
| ----- | ---------- | ------------------------------------- |
| Alice | ROLE_USER  | Reads via ROLE_USER SID               |
| Carol | ROLE_USER  | Reads via ROLE_USER SID               |
| Alex  | ROLE_USER  | Read allowed, write explicitly denied |
| Bob   | ROLE_ADMIN | Full access via ROLE_ADMIN SID        |

ACL **does not store users directly** — it evaluates:

```
User → Roles → acl_sid → acl_entry
```

---

## 7. ACL Inheritance Example

If later you add:

```sql
parent_object = 1
entries_inheriting = true
```

Then:

* Child object automatically inherits permissions
* No need to duplicate ACL entries

---

## 8. PostgreSQL Identity Queries (Important)

Spring Security needs to retrieve generated IDs:

```sql
select currval(pg_get_serial_sequence('acl_class', 'id'));
select currval(pg_get_serial_sequence('acl_sid', 'id'));
```

These are configured in `JdbcMutableAclService`.

---

## 9. Summary Flow Diagram (Conceptual)

```
User logs in
   ↓
UserDetailsService loads roles
   ↓
PermissionEvaluator calls ACL
   ↓
acl_sid → acl_entry → acl_object_identity
   ↓
Access GRANTED or DENIED
```

---

## 10. Key Takeaways

* **User schema** handles authentication and roles
* **ACL schema** handles object-level authorization
* Roles act as **shared SIDs**
* Permissions are **bit masks**
* Inheritance reduces duplication
* Ownership enables `OWNER` permissions

---

Below are **continuation lesson notes**, building *directly* on your existing Spring Security + ACL understanding.
The focus is **conceptual clarity + practical wiring**, not just code fragments.

---

# OWNER Permission, Automatic ACL Creation, and Execution Flow

---

## 1. OWNER Permission Logic in Spring Security ACL

### 1.1 What “OWNER” Means in ACL

In Spring Security ACL, **OWNER is not just another mask** like READ or WRITE.

Conceptually:

> **OWNER = the SID stored in `acl_object_identity.owner_sid`**

So when you write:

```java
@PreAuthorize("hasPermission(#student, 'OWNER')")
```

Spring Security checks:

```
Is the authenticated principal the same as owner_sid?
```

Not:

```
Is there an acl_entry with mask OWNER?
```

---

### 1.2 How OWNER Is Evaluated Internally

Evaluation order inside ACL:

1. Check if authenticated user **is owner**
2. If yes → access is granted **immediately**
3. If not → check ACL entries (`acl_entry`)
4. Apply inheritance if enabled
5. Final decision

This makes OWNER a **short-circuit permission**

---

### 1.3 Custom Permission Names (OWNER)

Spring ACL works with **integer masks**, but your application works with **semantic names**.

| Permission Name | Mask                   |
| --------------- | ---------------------- |
| READ            | 1                      |
| WRITE           | 2                      |
| DELETE          | 8                      |
| ADMIN           | 16                     |
| OWNER           | *special* (not a mask) |

Your `PermissionEvaluator` typically maps strings:

```java
switch (permission) {
    case "READ"   -> BasePermission.READ;
    case "WRITE"  -> BasePermission.WRITE;
    case "DELETE" -> BasePermission.DELETE;
    case "ADMIN"  -> BasePermission.ADMINISTRATION;
    case "OWNER"  -> BasePermission.ADMINISTRATION; // trigger owner check
}
```

The **actual owner comparison** happens in the ACL engine, not in your code.

---

## 2. Automatic ACL Creation on Save

### 2.1 Why Automatic ACL Creation Is Required

Without automatic creation:

* Entity saved
* No `acl_object_identity`
* No permissions exist
* `hasPermission()` always fails

So **every protected object must have an ACL entry at creation time**.

---

### 2.2 When Should ACL Be Created?

**Best practice**:

* Create ACL **after the entity is persisted**
* Because the database ID is required

Common trigger points:

* Service layer
* Event listener
* Transactional boundary

---

### 2.3 Typical Automatic ACL Creation Flow

When saving a `Student` (or `Possession`):

1. Entity is persisted → ID generated
2. ACL identity is created
3. Owner is set
4. Default permissions are assigned
5. ACL is stored

---

### 2.4 What Gets Created Automatically

For a newly saved object:

| Table                 | Entry                                |
| --------------------- | ------------------------------------ |
| `acl_class`           | Created once per entity type         |
| `acl_sid`             | Created if owner SID does not exist  |
| `acl_object_identity` | Created for this entity              |
| `acl_entry`           | Owner permissions + role permissions |

---

### 2.5 Typical Default ACL Strategy

For a new domain object:

| SID        | Permission          |
| ---------- | ------------------- |
| OWNER      | Full control        |
| ROLE_ADMIN | READ, WRITE, DELETE |
| ROLE_USER  | READ                |

This matches your earlier SQL inserts.

---

### 2.6 Conceptual Pseudocode (Save + ACL)

```java
@Transactional
public Student save(Student student) {

    Student saved = repository.save(student);

    ObjectIdentity oid =
        new ObjectIdentityImpl(Student.class, saved.getId());

    MutableAcl acl = aclService.createAcl(oid);

    Sid owner = new PrincipalSid(SecurityContextHolder
            .getContext().getAuthentication());

    acl.setOwner(owner);

    acl.insertAce(0, BasePermission.READ, new GrantedAuthoritySid("ROLE_USER"), true);
    acl.insertAce(1, BasePermission.WRITE, new GrantedAuthoritySid("ROLE_ADMIN"), true);
    acl.insertAce(2, BasePermission.DELETE, new GrantedAuthoritySid("ROLE_ADMIN"), true);

    aclService.updateAcl(acl);

    return saved;
}
```

**Important**:

* ACL creation must be inside the same transaction
* The entity ID must already exist

---

## 3. Parent / Inherited ACLs (Quick Recap)

If the object has a parent:

```java
acl.setParent(parentAcl);
acl.setEntriesInheriting(true);
```

Then:

* Child object inherits permissions
* Only differences need new entries
* Reduces duplication

---

## 4. Full Execution Sequence Diagram

### (Method → ACL Decision)

### 4.1 High-Level Flow

```
Client
  ↓
Controller
  ↓
Service Method (@PreAuthorize)
  ↓
MethodSecurityInterceptor
  ↓
PermissionEvaluator
  ↓
AclPermissionEvaluator
  ↓
JdbcAclService
  ↓
Database (ACL tables)
```

---

### 4.2 Detailed Step-by-Step Sequence

```
User calls DELETE /students/3
        |
        v
@Controller
        |
        v
@Service.delete(student)
  @PreAuthorize("hasPermission(#student, 'DELETE')")
        |
        v
MethodSecurityInterceptor
        |
        v
ExpressionHandler
        |
        v
PermissionEvaluator.hasPermission(...)
        |
        v
AclPermissionEvaluator
        |
        |-- Load Authentication
        |-- Extract SIDs (USER + ROLES)
        |
        v
JdbcAclService.readAclById()
        |
        |-- acl_object_identity
        |-- acl_sid
        |-- acl_entry
        |
        v
ACL Permission Check
        |
        |-- Is OWNER? → YES → GRANT
        |-- Else check entries
        |-- Else check parent
        |
        v
AccessDecision
        |
        v
Method Executes OR AccessDeniedException
```

---

### 4.3 OWNER Check Position (Important)

```
ACL Permission Evaluation Order
--------------------------------
1. OWNER check
2. Local ACEs
3. Inherited ACEs
4. Deny by default
```

This explains why OWNER is **stronger than ADMIN**.

---

## 5. Common Pitfalls (Exam & Interview Gold)

### ❌ Forgetting ACL creation on save

→ `hasPermission()` always returns false

### ❌ Creating ACL before entity persistence

→ No object ID, ACL breaks

### ❌ Expecting OWNER to be a mask

→ OWNER is resolved via `owner_sid`

### ❌ Not enabling inheritance

→ Parent permissions ignored

---

## 6. Key Takeaways

* OWNER is **identity-based**, not mask-based
* ACLs must be created **automatically on save**
* Permissions are evaluated **before method execution**
* ACL evaluation follows a strict order
* Inheritance simplifies large object graphs

---

# 1. Full Runnable ACL Service Class

*(Automatic ACL creation + OWNER support)*

This is the **core service** that makes your ACL system actually work.

---

## 1.1 Purpose of the ACL Service

The ACL service is responsible for:

* Creating ACL entries **automatically** when an entity is saved
* Assigning:

    * OWNER
    * ROLE_ADMIN permissions
    * ROLE_USER read permissions
* Updating ACLs inside a transaction
* Ensuring `@PreAuthorize(hasPermission(...))` works

---

## 1.2 Runnable `AclManagementService`

```java
package springsecurity.lesson2datastructureofacl.security.acl;

import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AclManagementService {

    private final MutableAclService aclService;

    public AclManagementService(MutableAclService aclService) {
        this.aclService = aclService;
    }

    /**
     * Automatically create ACL when a domain object is saved
     */
    @Transactional
    public void createAclForObject(Class<?> clazz, Long objectId) {

        ObjectIdentity oid =
                new ObjectIdentityImpl(clazz, objectId);

        MutableAcl acl = aclService.createAcl(oid);

        // OWNER = authenticated principal
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
        Sid ownerSid = new PrincipalSid(auth);

        acl.setOwner(ownerSid);

        /*
         * ROLE-BASED PERMISSIONS
         */
        Sid adminRole = new GrantedAuthoritySid("ROLE_ADMIN");
        Sid userRole  = new GrantedAuthoritySid("ROLE_USER");

        int aceIndex = 0;

        // ROLE_ADMIN → full control
        acl.insertAce(aceIndex++, BasePermission.READ, adminRole, true);
        acl.insertAce(aceIndex++, BasePermission.WRITE, adminRole, true);
        acl.insertAce(aceIndex++, BasePermission.DELETE, adminRole, true);
        acl.insertAce(aceIndex++, BasePermission.ADMINISTRATION, adminRole, true);

        // ROLE_USER → read-only
        acl.insertAce(aceIndex++, BasePermission.READ, userRole, true);

        aclService.updateAcl(acl);
    }
}
```

---

## 1.3 How This Maps to Your Database

| Code                  | Table                 |
| --------------------- | --------------------- |
| `createAcl()`         | `acl_object_identity` |
| `setOwner()`          | `owner_sid`           |
| `insertAce()`         | `acl_entry`           |
| `GrantedAuthoritySid` | `acl_sid`             |
| `ObjectIdentityImpl`  | `acl_class`           |

This replaces **manual SQL ACL inserts** in real applications.

---

# 2. Unit Tests Proving OWNER Logic

Now we **prove** that OWNER works and is **stronger than role permissions**.

---

## 2.1 What We Are Testing

We want to prove:

1. OWNER can DELETE their object
2. ROLE_USER cannot DELETE unless OWNER
3. OWNER check happens **before ACE evaluation**

---

## 2.2 Test Configuration Notes

Assumptions:

* PostgreSQL or H2 with ACL schema loaded
* Method security enabled
* ACL beans configured

---

## 2.3 Test Service Method

```java
@Service
public class StudentService {

    @PreAuthorize("hasPermission(#studentId, 'springsecurity.lesson2datastructureofacl.persistence.entity.Student', 'DELETE')")
    public void deleteStudent(Long studentId) {
        // delete logic
    }
}
```

---

## 2.4 Unit Test Class

```java
package springsecurity.lesson2datastructureofacl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import springsecurity.lesson2datastructureofacl.service.StudentService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AclOwnerPermissionTest {

    @Autowired
    private StudentService studentService;

    /**
     * OWNER should be allowed even if no explicit DELETE ACE exists
     */
    @Test
    @WithMockUser(username = "Alice", roles = "USER")
    void ownerCanDeleteOwnStudent() {
        assertDoesNotThrow(() ->
                studentService.deleteStudent(1L)
        );
    }

    /**
     * ROLE_USER without ownership must be denied
     */
    @Test
    @WithMockUser(username = "Carol", roles = "USER")
    void nonOwnerUserCannotDelete() {
        assertThrows(AccessDeniedException.class, () ->
                studentService.deleteStudent(1L)
        );
    }

    /**
     * ROLE_ADMIN can delete via ACE
     */
    @Test
    @WithMockUser(username = "Bob", roles = "ADMIN")
    void adminCanDeleteViaAclEntry() {
        assertDoesNotThrow(() ->
                studentService.deleteStudent(1L)
        );
    }
}
```

---

## 2.5 What These Tests Prove

| Test              | Result    | Why             |
| ----------------- | --------- | --------------- |
| OWNER delete      | ✅ Allowed | owner_sid match |
| ROLE_USER delete  | ❌ Denied  | no DELETE ACE   |
| ROLE_ADMIN delete | ✅ Allowed | DELETE ACE      |

**Key Insight**

> OWNER bypasses ACE checks entirely.

---

# 3. ACL Caching and Performance Tuning

ACL can be **expensive** if not tuned properly.

---

## 3.1 Why ACL Needs Caching

Without caching:

* Every secured method call
* Triggers multiple SQL queries:

    * `acl_object_identity`
    * `acl_entry`
    * `acl_sid`
    * `acl_class`

This becomes a bottleneck.

---

## 3.2 ACL Cache Architecture

```
Method Call
   ↓
AclPermissionEvaluator
   ↓
AclCache (EhCache / ConcurrentMap)
   ↓
JdbcAclService (DB fallback)
```

If ACL is cached → **no DB hit**

---

## 3.3 Recommended Cache Bean

```java
@Bean
public AclCache aclCache() {
    return new EhCacheBasedAclCache(
            aclEhCacheFactoryBean().getObject(),
            permissionGrantingStrategy(),
            aclAuthorizationStrategy()
    );
}
```

Or simpler (in-memory):

```java
@Bean
public AclCache aclCache() {
    return new SpringCacheBasedAclCache(
            cacheManager().getCache("aclCache"),
            permissionGrantingStrategy(),
            aclAuthorizationStrategy()
    );
}
```

---

## 3.4 What Should Be Cached

| Item              | Cache |
| ----------------- | ----- |
| ACL entries       | ✅     |
| Object identities | ✅     |
| Permission checks | ❌     |
| Authentication    | ❌     |

---

## 3.5 Cache Invalidation Rules

You **must evict cache entries** when:

* ACL updated
* Permissions changed
* Ownership changed

Spring’s `JdbcMutableAclService` **automatically clears cache** on:

```java
updateAcl()
deleteAcl()
```

---

## 3.6 Performance Best Practices

### ✅ Use Role-Based ACLs

Avoid per-user ACEs unless required.

### ✅ Enable Inheritance

Reduces ACL rows dramatically.

### ❌ Avoid hasPermission() in loops

Each call = ACL lookup.

### ✅ Secure at Service Layer

Avoid ACL checks in controllers.

---

## 4. Mental Model Summary (Exam-Ready)

```
Authentication → Roles → SIDs
        ↓
ObjectIdentity → ACL
        ↓
OWNER?
  YES → GRANT
  NO  → ACEs
        ↓
Inherited?
        ↓
DENY
```

---

## 5. Final Takeaways

* OWNER is identity-based, not permission-based
* ACL must be created automatically on save
* Unit tests can **prove ACL correctness**
* Caching is mandatory for real systems
* ACL works best with inheritance + roles

---

Below is a **clean, end-to-end explanation + runnable code sketch** showing **Spring Security ACL with REST controllers + form login**, fully consistent with everything you’ve built so far (entities, ACL schema, OWNER logic, automatic ACL creation).

I’ll structure this as **lesson notes + implementation**, not just raw code, so you understand *why* each piece exists.

---

# 1. Big Picture: REST + Form Login + ACL

### What we want to achieve

1. **Form login** authenticates users (Students)
2. **REST controllers** expose secured endpoints
3. **ACL** enforces:

    * OWNER permissions
    * ROLE_ADMIN overrides
    * ROLE_USER read-only access
4. **ACL is created automatically** when a resource is saved

---

## High-Level Flow

```
Browser / REST Client
        ↓
Form Login (/login)
        ↓
Authentication (Student + Roles)
        ↓
REST Controller
        ↓
Service Layer (@PreAuthorize)
        ↓
AclPermissionEvaluator
        ↓
AclService → DB / Cache
```

---

# 2. Form Login Configuration (Spring Security 6)

### Why replace HTTP Basic?

* HTTP Basic → stateless, ugly, insecure for browsers
* Form Login → session-based, real-world apps

---

## 2.1 Security Configuration

```java
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/api/students", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
            );

        return http.build();
    }
}
```

---

## 2.2 Custom Login Controller

```java
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login"; // Thymeleaf template
    }
}
```

---

## 2.3 Login Page (Thymeleaf)

```html
<form th:action="@{/login}" method="post">
    <input type="text" name="username" placeholder="Username"/>
    <input type="password" name="password" placeholder="Password"/>
    <button type="submit">Login</button>
</form>
```

---

# 3. REST Controllers Secured with ACL

### Key Principle

> **Controllers are thin**
> **ACL lives at the service layer**

---

## 3.1 Student REST Controller

```java
@RestController
@RequestMapping("/api/students")
public class StudentRestController {

    private final StudentService studentService;

    public StudentRestController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<StudentResponseDto> findAll() {
        return studentService.findAll()
                .stream()
                .map(StudentMapper::toResponseDto)
                .toList();
    }

    @GetMapping("/{id}")
    public StudentResponseDto findById(@PathVariable Long id) {
        return studentService.findById(id)
                .map(StudentMapper::toResponseDto)
                .orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        studentService.deleteById(id);
    }
}
```

---

# 4. Service Layer with ACL Enforcement

This is where **ACL actually happens**.

---

## 4.1 StudentService (ACL-Secured)

```java
@Service
public class StudentService {

    private final IStudentRepository repository;

    public StudentService(IStudentRepository repository) {
        this.repository = repository;
    }

    @PreAuthorize("hasPermission(#id, 'springsecurity.lesson2datastructureofacl.persistence.entity.Student', 'READ')")
    public Optional<Student> findById(Long id) {
        return repository.findStudentById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Student> findAll() {
        return repository.findAll();
    }

    @PreAuthorize("hasPermission(#id, 'springsecurity.lesson2datastructureofacl.persistence.entity.Student', 'DELETE')")
    public void deleteById(Long id) {
        repository.deleteStudentById(id);
    }
}
```

---

## 4.2 Why ACL Is Here (Not Controller)

| Layer      | Responsibility            |
| ---------- | ------------------------- |
| Controller | HTTP, JSON                |
| Service    | Business rules + security |
| ACL        | Authorization             |
| Repository | Persistence               |

---

# 5. Automatic ACL Creation on Save

### This ensures:

* No manual SQL
* OWNER assigned correctly
* Inheritance works

---

## 5.1 Save Method with ACL Hook

```java
@Service
public class PossessionService {

    private final IPossessionRepository repository;
    private final AclManagementService aclService;

    public PossessionService(IPossessionRepository repository,
                             AclManagementService aclService) {
        this.repository = repository;
        this.aclService = aclService;
    }

    @Transactional
    public Possession create(Possession possession) {

        Possession saved = repository.save(possession);

        // 🔐 AUTO ACL CREATION
        aclService.createAclForObject(
                Possession.class,
                saved.getId()
        );

        return saved;
    }

    @PreAuthorize("hasPermission(#id, 'springsecurity.lesson2datastructureofacl.persistence.entity.Possession', 'READ')")
    public Possession findById(Long id) {
        return repository.findById(id).orElseThrow();
    }
}
```

---

# 6. OWNER Permission in REST Calls

### Example Scenario

| User        | Action             | Result      |
| ----------- | ------------------ | ----------- |
| Alice       | DELETE her Student | ✅ Allowed   |
| Carol       | DELETE Alice       | ❌ Forbidden |
| Bob (ADMIN) | DELETE anyone      | ✅ Allowed   |

---

## 6.1 Why OWNER Works Automatically

1. `Authentication` → `PrincipalSid`
2. ACL loaded
3. OWNER checked **before ACE**
4. Immediate GRANT

---

# 7. REST Error Behavior (Important!)

When ACL denies access:

```http
HTTP/1.1 403 Forbidden
```

Spring throws:

```java
AccessDeniedException
```

You can customize this with:

```java
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String denied() {
        return "Access Denied";
    }
}
```

---

# 8. Full Execution Sequence Diagram (REST + ACL)

```
POST /login
    ↓
UsernamePasswordAuthenticationFilter
    ↓
AuthenticationManager
    ↓
SecurityContext

GET /api/students/1
    ↓
StudentRestController
    ↓
StudentService.findById()
    ↓
@PreAuthorize
    ↓
AclPermissionEvaluator
    ↓
AclService
    ↓
AclCache
    ↓
DB (if cache miss)
    ↓
OWNER? → GRANT
```

---

# 9. Key Takeaways (Exam & Interview Ready)

* Form login handles authentication
* REST controllers stay thin
* ACL always lives in service layer
* OWNER overrides ACEs
* Automatic ACL creation is mandatory
* Caching is critical for performance

---


