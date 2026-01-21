
---

# Configuring Spring Security ACL (Domain Object Security)

## 1. Why Domain Object Security (ACL) Exists

Traditional Spring Security answers only two questions:

* **Who** is the user? → `Authentication`
* **Where** are they accessing? → URL or method

However, **real applications also need to answer**:

* **What exact domain object instance** is being accessed?

### Example Problem

In a pet clinic system:

* Staff → access **all** customer records
* Customers → access **only their own** records
* Customers may grant access to **specific other users**

This cannot be solved cleanly with:

* URL security alone
* Role-based authorization alone

➡️ **Access Control Lists (ACLs)** solve this by securing **individual domain object instances**.

---

## 2. What Spring Security ACL Provides

Spring Security ACL gives you:

1. **Efficient ACL retrieval** (with caching)
2. **Authorization before method execution**
3. **Authorization after method execution**
4. **Ownership, inheritance, and fine-grained permissions**

All ACL functionality is provided by:

```
spring-security-acl-*.jar
```

---

## 3. Core ACL Concepts (Mental Model)

Every secured domain object has:

```
ObjectIdentity
   ↓
ACL
   ↓
Access Control Entries (ACEs)
```

An authorization decision considers:

```
(Authentication) + (Method Invocation) + (Domain Object)
```

---

## 4. ACL Database Schema (Conceptual View)

Spring Security ACL uses **four tables**:

| Table                 | Purpose                            |
| --------------------- | ---------------------------------- |
| `acl_sid`             | Identifies users and roles         |
| `acl_class`           | Identifies domain object types     |
| `acl_object_identity` | Identifies domain object instances |
| `acl_entry`           | Stores permissions                 |

These tables allow **billions of objects** with **only 32 permission bits**.

---

## 5. Key ACL Interfaces (Conceptual Understanding)

### 5.1 `Acl`

* Represents **one domain object instance**
* Contains:

  * Owner
  * Parent ACL (inheritance)
  * List of ACEs
* Stored in `acl_object_identity`

---

### 5.2 `AccessControlEntry (ACE)`

* One permission assignment
* Tuple of:

  * `Sid` (who)
  * `Permission` (what)
  * Granting / Denying
* Stored in `acl_entry`

---

### 5.3 `Permission`

* Represents a **bit mask**
* Default permissions:

| Permission | Bit |
| ---------- | --- |
| READ       | 1   |
| WRITE      | 2   |
| CREATE     | 4   |
| DELETE     | 8   |
| ADMIN      | 16  |

Custom permissions (e.g. OWNER) are supported.

---

### 5.4 `Sid` (Security Identity)

Represents **who** receives a permission:

* `PrincipalSid` → user
* `GrantedAuthoritySid` → role

Stored in `acl_sid`.

---

### 5.5 `ObjectIdentity`

* Represents **what object** is secured
* Default implementation:

  ```java
  ObjectIdentityImpl(Class, Serializable id)
  ```

Stored via `acl_class` + `acl_object_identity`.

---

## 6. ACL Service Layer Interfaces

### 6.1 `AclService`

* Read-only interface
* Retrieves ACLs for an object
* Used during authorization

```java
Acl readAclById(ObjectIdentity oid)
```

---

### 6.2 `MutableAclService`

* Extends `AclService`
* Allows:

  * Creating ACLs
  * Modifying ACLs
  * Deleting ACLs

```java
MutableAcl createAcl(ObjectIdentity oid)
void updateAcl(MutableAcl acl)
```

---

## 7. Why We Need Multiple ACL Beans

Spring Security ACL is **highly modular**.

Each bean has **one responsibility**:

```
PermissionEvaluator
    ↓
AclService
    ↓
LookupStrategy
    ↓
Jdbc Access + Cache
```

---

## 8. ACL Configuration Beans (Explained One by One)

---

### 8.1 `MethodSecurityExpressionHandler`

```java
@Bean
static MethodSecurityExpressionHandler expressionHandler(
        AclPermissionEvaluator aclPermissionEvaluator) {

    DefaultMethodSecurityExpressionHandler handler =
            new DefaultMethodSecurityExpressionHandler();
    handler.setPermissionEvaluator(aclPermissionEvaluator);
    return handler;
}
```

### Why This Bean Exists

* Enables `hasPermission()` in:

  * `@PreAuthorize`
  * `@PostAuthorize`
  * `@PreFilter`
  * `@PostFilter`
* Without it → ACL is ignored

---

### 8.2 `AclPermissionEvaluator`

```java
@Bean
static AclPermissionEvaluator aclPermissionEvaluator(AclService aclService) {
    return new AclPermissionEvaluator(aclService);
}
```

### Responsibility

* Converts SpEL expressions like:

  ```java
  hasPermission(#id, 'Student', 'READ')
  ```

  into ACL lookups
* Delegates to `AclService`

---

### 8.3 `JdbcMutableAclService`

```java
@Bean
static JdbcMutableAclService aclService(
        DataSource dataSource,
        LookupStrategy lookupStrategy,
        AclCache aclCache) {

    return new JdbcMutableAclService(dataSource, lookupStrategy, aclCache);
}
```

### Responsibility

* Core ACL persistence engine
* Uses **JDBC**, not JPA
* Handles:

  * Creating ACLs
  * Updating ACEs
  * Cache eviction

---

### 8.4 `LookupStrategy`

```java
@Bean
static LookupStrategy lookupStrategy(
        DataSource dataSource,
        AclCache cache,
        AclAuthorizationStrategy authStrategy,
        PermissionGrantingStrategy permissionStrategy) {

    return new BasicLookupStrategy(
            dataSource,
            cache,
            authStrategy,
            permissionStrategy
    );
}
```

### Why This Exists

* Optimizes ACL retrieval
* Supports:

  * Batch loading
  * Inheritance resolution
  * Minimal SQL calls

---

### 8.5 `AclCache`

```java
@Bean
static AclCache aclCache(
        PermissionGrantingStrategy pgs,
        AclAuthorizationStrategy aas) {

    Cache cache = new ConcurrentMapCache("aclCache");
    return new SpringCacheBasedAclCache(cache, pgs, aas);
}
```

### Responsibility

* Prevents repeated DB hits
* Stores:

  * ACLs
  * ACEs
* Automatically invalidated on updates

---

### 8.6 `AclAuthorizationStrategy`

```java
@Bean
static AclAuthorizationStrategy aclAuthorizationStrategy() {
    return new AclAuthorizationStrategyImpl(
            new SimpleGrantedAuthority("ROLE_ADMIN")
    );
}
```

### Purpose

* Controls **who can modify ACLs**
* Typically:

  * `ROLE_ADMIN`

---

### 8.7 `PermissionGrantingStrategy`

```java
@Bean
static PermissionGrantingStrategy permissionGrantingStrategy() {
    return new DefaultPermissionGrantingStrategy(
            new ConsoleAuditLogger()
    );
}
```

### Responsibility

* Evaluates permissions
* Supports:

  * Granting
  * Denying
  * Auditing

---

## 9. Using ACL in Method Security

Once configured, ACL is used via:

```java
@PreAuthorize("hasPermission(#id, 'Student', 'READ')")
```

Or filtering collections:

```java
@PostFilter("hasPermission(filterObject, 'READ')")
Iterable<Student> findAll();
```

---

## 10. Execution Flow (End-to-End)

```
@PreAuthorize
   ↓
AclPermissionEvaluator
   ↓
AclService
   ↓
AclCache (hit?)
   ↓
LookupStrategy
   ↓
Database
   ↓
PermissionGrantingStrategy
```

---

## 11. Why ACL Is the “Right” Solution

Compared to alternatives:

| Approach               | Problem                       |
| ---------------------- | ----------------------------- |
| Business logic checks  | Tight coupling                |
| Authorities per object | Memory explosion              |
| Manual DAO access      | Double DB hits                |
| ACL                    | Scalable, reusable, optimized |

---

## 12. Key Takeaways

* ACL secures **domain object instances**
* Permissions are **bit masks**
* OWNER is evaluated before ACEs
* Caching is mandatory for performance
* ACL logic lives **outside business code**
* Spring Security ACL is **database-centric and scalable**

---

### Final Mental Model

```
User → SID
Object → ObjectIdentity
ACL → Owner + ACEs
Decision → OWNER → ACEs → Inheritance
```

---

# 1. Custom OWNER Permission (Correct Way)

## 1.1 Important Clarification (Very Common Exam Trap)

In Spring Security ACL:

> **OWNER is NOT a normal permission bit**

Ownership is stored **on the ACL itself**, not as an ACE.

That means:

* ❌ You do NOT store OWNER in `acl_entry`
* ✅ OWNER is evaluated by comparing `Authentication` with `acl.getOwner()`

However:

* You **can expose OWNER as a permission name** in `hasPermission(...)`
* This requires a **custom Permission + evaluator logic**

---

## 1.2 Define OWNER Permission Constant

Create a custom permission class.

```java
package com.example.security.acl.permission;

import org.springframework.security.acls.domain.BasePermission;

public class CustomPermission extends BasePermission {

    public static final CustomPermission OWNER =
            new CustomPermission(1 << 5, 'O'); // bit 32

    protected CustomPermission(int mask, char code) {
        super(mask, code);
    }
}
```

Notes:

* Spring’s default permissions use bits 1–16
* Using bit 32 avoids collisions
* The character `'O'` is optional but useful for debugging

---

## 1.3 Why We Still Need This Permission

Even though ownership is stored separately, **SpEL expressions expect a Permission object**.

So when you write:

```java
@PreAuthorize("hasPermission(#student, 'OWNER')")
```

Spring will:

1. Resolve `'OWNER'` → `Permission`
2. Delegate decision to `PermissionGrantingStrategy`

We’ll intercept this properly.

---

# 2. OWNER Evaluation Logic (Critical Section)

## 2.1 How Spring Checks Ownership Internally

Inside `AclImpl`:

```java
public Sid getOwner();
```

Ownership check happens **before ACE evaluation**.

### Logical Order:

```
1. Is authentication == owner?
   → ALLOW immediately
2. Else → evaluate ACEs
3. Else → check parent ACL (inheritance)
```

---

## 2.2 Custom PermissionGrantingStrategy

We extend the default strategy to support OWNER.

```java
package com.example.security.acl.permission;

import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.model.*;

import java.util.List;

public class OwnerAwarePermissionGrantingStrategy
        extends DefaultPermissionGrantingStrategy {

    public OwnerAwarePermissionGrantingStrategy(AuditLogger auditLogger) {
        super(auditLogger);
    }

    @Override
    public boolean isGranted(
            List<Permission> permissions,
            Authentication authentication,
            ObjectIdentity oid,
            List<Sid> sids,
            Acl acl) {

        // 1️⃣ OWNER short-circuit
        if (permissions.contains(CustomPermission.OWNER)) {
            Sid owner = acl.getOwner();
            for (Sid sid : sids) {
                if (sid.equals(owner)) {
                    return true;
                }
            }
            return false;
        }

        // 2️⃣ Delegate to default logic
        return super.isGranted(permissions, authentication, oid, sids, acl);
    }
}
```

This gives you:

* Clean OWNER semantics
* No ACE pollution
* Correct inheritance behavior

---

## 2.3 Register the Custom Strategy

```java
@Bean
PermissionGrantingStrategy permissionGrantingStrategy() {
    return new OwnerAwarePermissionGrantingStrategy(
            new ConsoleAuditLogger()
    );
}
```

Everything else stays unchanged.

---

# 3. Automatic OWNER Assignment on Save

When a domain object is created, we must:

1. Create ACL
2. Set owner
3. Optionally grant ADMIN to owner

### Example: Student Creation

```java
@Transactional
public Student save(Student student) {
    Student saved = repository.save(student);

    ObjectIdentity oid =
            new ObjectIdentityImpl(Student.class, saved.getId());

    MutableAcl acl = aclService.createAcl(oid);

    Authentication auth =
            SecurityContextHolder.getContext().getAuthentication();

    Sid ownerSid = new PrincipalSid(auth);
    acl.setOwner(ownerSid);

    acl.insertAce(
            0,
            BasePermission.ADMINISTRATION,
            ownerSid,
            true
    );

    aclService.updateAcl(acl);
    return saved;
}
```

Now:

* `hasPermission(student, 'OWNER')` works
* Ownership is stored in `acl_object_identity.owner_sid`

---

# 4. ACL Inheritance (Parent → Child)

## 4.1 What Inheritance Means

ACL inheritance allows:

> A child object to **reuse permissions from its parent**

This is **not JPA inheritance**, it is **security inheritance**.

---

## 4.2 Parent–Child Example

### Domain Model

```java
Course
 ├── owner: Teacher
 └── Students (many)
```

Rules:

* Course owner automatically controls students
* Students inherit Course ACL

---

## 4.3 Entities

### Parent Entity

```java
@Entity
public class Course {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
}
```

### Child Entity

```java
@Entity
public class Student {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    private Course course;
}
```

---

## 4.4 Creating Parent ACL

```java
ObjectIdentity courseOid =
        new ObjectIdentityImpl(Course.class, course.getId());

MutableAcl courseAcl = aclService.createAcl(courseOid);
courseAcl.setOwner(new PrincipalSid(auth));
aclService.updateAcl(courseAcl);
```

---

## 4.5 Creating Child ACL with Inheritance

```java
ObjectIdentity studentOid =
        new ObjectIdentityImpl(Student.class, student.getId());

MutableAcl studentAcl = aclService.createAcl(studentOid);

// 🔑 Set parent ACL
ObjectIdentity parentOid =
        new ObjectIdentityImpl(Course.class, student.getCourse().getId());

Acl parentAcl = aclService.readAclById(parentOid);
studentAcl.setParent(parentAcl);
studentAcl.setEntriesInheriting(true);

aclService.updateAcl(studentAcl);
```

Now:

* Student has **no ACEs**
* Permissions flow from Course

---

# 5. Method Security Using OWNER + Inheritance

```java
@PreAuthorize("hasPermission(#student, 'OWNER')")
public void updateStudent(Student student) {
}
```

### Who is allowed?

| User          | Result                 |
| ------------- |------------------------|
| Course owner  | ✅ Allowed             | 
| Student owner | ❌ (no ownership set)  | 
| Admin         | ✅ (via ADMIN ACE)     | 
| Other user    | ❌                     | 

---

# 6. Full Evaluation Order (Very Important)

```
hasPermission(student, OWNER)
        ↓
PermissionEvaluator
        ↓
AclService.readAclById(student)
        ↓
Is authentication == student.owner?
        ↓ NO
Check ACEs on student
        ↓ NONE
Check parent ACL (Course)
        ↓
Is authentication == course.owner?
        ↓ YES → GRANTED
```

This is **exactly why inheritance + OWNER is powerful**.

---

# 7. Execution Sequence Diagram (Textual)

```
Controller
   ↓
@PreAuthorize
   ↓
AclPermissionEvaluator
   ↓
AclService
   ↓
LookupStrategy
   ↓
Student ACL
   ↓
OWNER? → no
   ↓
Parent ACL (Course)
   ↓
OWNER? → yes
   ↓
Access GRANTED
```

---

# 8. Key Takeaways (Exam + Real Projects)

* OWNER is **not an ACE**
* OWNER is stored in `acl_object_identity`
* OWNER is evaluated **before ACEs**
* ACL inheritance is **recursive**
* Parent ACLs enable **clean authorization models**
* Custom PermissionGrantingStrategy is the **correct extension point**

---

