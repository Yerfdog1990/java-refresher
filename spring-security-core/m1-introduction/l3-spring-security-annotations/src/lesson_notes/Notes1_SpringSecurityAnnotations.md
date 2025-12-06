
---

# 🌟 **Lesson Notes: Spring Security Annotations**

**Last Updated: 23 Jul 2025**

Spring Security offers a rich set of annotations that enable **declarative**, **method-level**, and **role-based** security inside Spring Boot applications. These annotations control **who** can access application resources and **how** they interact with them.

They are essential for securing:

* Controllers (REST)
* Service Layer
* Repository Layer
* Domain Model Methods

---

# ✅ **1. @Secured**

### 🔹 Purpose

* Restrict method access to **specific roles**.
* **Does NOT support SpEL** (Spring Expression Language).
* Simple, fast, straightforward.

### ✔ Enable

```java
@EnableGlobalMethodSecurity(securedEnabled = true)
```

### ✔ Example

```java
@Secured({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
public void createUser(User user) {
    // logic
}
```

---

# ✅ **2. @PreAuthorize**

### 🔹 Purpose

* Evaluate **SpEL expressions BEFORE** method execution.
* Allows **complex logic**, combining roles, conditions, ownership checks.

### ✔ Enable

```java
@EnableGlobalMethodSecurity(prePostEnabled = true)
```

### ✔ Example

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteAdminUser(Long userId) {
    // logic
}
```

### More advanced:

```java
@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
public User getProfile(Long id) { ... }
```

---

# ✅ **3. @PostAuthorize**

### 🔹 Purpose

* Runs **AFTER** method execution.
* Useful when access depends on **returned object**.

### ✔ Example

```java
@PostAuthorize("returnObject.owner == authentication.name")
public Course getCourse(Long id) {
    return courseService.find(id);
}
```

Another example:

```java
@PostAuthorize("hasRole('ADMIN') and hasPermission(returnObject, 'read:courses')")
public List<Course> findAllCourses() { ... }
```

---

# ✅ **4. @PreFilter**

### 🔹 Purpose

* Filters **input collections BEFORE** method runs.
* Uses SpEL to include/exclude items.

### ✔ Example

```java
@PreFilter("filterObject.owner == authentication.name")
public void deleteCourses(List<Course> courses) {
    // only allowed items will remain
}
```

---

# ✅ **5. @PostFilter**

### 🔹 Purpose

* Filters **output collections AFTER** method execution.

### ✔ Example

```java
@PostFilter("filterObject.owner == authentication.name")
public List<Class> findAllClasses() {
    return classService.findAll();
}
```

---

# ✅ **6. @RolesAllowed** (JSR-250)

### 🔹 Purpose

* Similar to **@Secured**, but part of **JSR-250**.
* Supported across multiple Java frameworks.

### ✔ Enable

```java
@EnableGlobalMethodSecurity(jsr250Enabled = true)
```

### ✔ Example

```java
@RolesAllowed("ROLE_ADMIN")
public void deleteCourse(Long courseId) { ... }
```

---

# ✅ **7. @AuthenticationPrincipal**

### 🔹 Purpose

Injects the **currently authenticated user** into a method.

### ✔ Example

```java
@GetMapping("/username")
public String getUsername(@AuthenticationPrincipal User user) {
    return user.getUsername();
}
```

or lighter:

```java
@GetMapping("/username")
public String getUsername(@AuthenticationPrincipal String username) {
    return username;
}
```

---

# ✅ **8. @RoleHierarchy**

### 🔹 Purpose

Defines **role inheritance**.
Example:
`ADMIN > TEACHER > STUDENT`

### ✔ Example

```java
@Configuration
public class RoleHierarchyConfig {

    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(
            "ROLE_SUPER_ADMIN > ROLE_ADMIN \n" +
            "ROLE_ADMIN > ROLE_USER"
        );
        return hierarchy;
    }
}
```

---

# 📌 **Choosing the Right Annotation (Quick Guide)**

| Operation  | Best Annotation                                  | Example                               |
| ---------- | ------------------------------------------------ | ------------------------------------- |
| **Create** | `@Secured`, `@PreAuthorize`                      | `@Secured("ROLE_ADMIN")`              |
| **Read**   | `@PreAuthorize`, `@PostAuthorize`, `@PostFilter` | `@PreAuthorize("hasRole('STUDENT')")` |
| **Update** | `@PreAuthorize`, `@Secured`                      | `@PreAuthorize("hasRole('TEACHER')")` |
| **Delete** | `@Secured("ROLE_ADMIN")`                         | delete-only for admins                |

---

# ⭐ Best Practice Example: Updated RegistrationController

Below is a **fully secured**, **production-ready** controller using proper Spring Security annotations.

```java
@RestController
@RequestMapping("/api")
@Slf4j
public class RegistrationController {

    // CREATE — Admin Only
    @Secured("ROLE_ADMIN")
    @PostMapping("/registerStudent")
    public String registerStudent() {
        return "Student registered successfully!";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/registerTeacher")
    public String registerTeacher() {
        return "Teacher registered successfully!";
    }

    // UPDATE GRADES — Teacher OR Admin
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping("/enterGrade")
    public String enterGrade() {
        return "Grade entered successfully!";
    }

    // READ — Teacher & Admin
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @GetMapping("/getStudents")
    public String getStudents() {
        return "Students fetched successfully!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getTeachers")
    public String getTeachers() {
        return "Teachers fetched successfully!";
    }

    // BASIC READ — All authenticated users
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getClasses")
    public String getClasses() {
        return "Classes fetched successfully!";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getSubjects")
    public String getSubjects() {
        return "Subjects fetched successfully!";
    }

    // UPDATE — Admin only
    @RolesAllowed("ROLE_ADMIN")
    @PutMapping("/updateStudent")
    public String updateStudent() {
        return "Student updated successfully!";
    }

    @RolesAllowed("ROLE_ADMIN")
    @PutMapping("/updateTeacher")
    public String updateTeacher() {
        return "Teacher updated successfully!";
    }

    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/updateGrade")
    public String updateGrade() {
        return "Grade updated successfully!";
    }

    // DELETE — Admin only
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/deleteStudent")
    public String deleteStudent() {
        return "Student deleted successfully!";
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/deleteTeacher")
    public String deleteTeacher() {
        return "Teacher deleted successfully!";
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/deleteClass")
    public String deleteClass() {
        return "Class deleted successfully!";
    }
}
```

---

# 👍 **Conclusion**

Spring Security annotations provide:

* **Powerful, declarative authorization**
* **Fine-grained method-level security**
* **Role-based + expression-based access control**
* **Cleaner code compared to manual checks**
* **Strong integration with Spring Boot**

Using these annotations ensures:

* Maintainability
* Clear intent
* Strong protection against unauthorized access

---

