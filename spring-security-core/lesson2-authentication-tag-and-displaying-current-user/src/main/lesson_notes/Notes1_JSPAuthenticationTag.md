
---

# **Authentication Tag and Displaying the Current User**

**Spring Security with JSP (Spring Boot)**

---

## 1. Lesson Overview

In this lesson, we learn how to **display and use authentication information** on a JSP page using **Spring Security JSP tags** in a **Spring Boot application**.

By the end of this lesson, you will be able to:

* Display the **currently logged-in user**
* Display **roles (authorities)** assigned to the user
* Access the **Authentication object** directly in JSP
* Store authentication data in JSP variables
* Use authentication data in JSP logic (`<c:if>`)
* Understand how authentication data flows from request to JSP
* Build a **mini-project: User Profile Page**

### Technologies Used

* Spring Boot
* Spring MVC
* Spring Security
* JSP
* JSTL
* In-memory authentication

### Test Credentials Used Throughout the Lesson

| Username | Password |
| -------- | -------- |
| `user`   | `pass`   |

---

## 2. Prerequisites (Already Completed)

Before starting this lesson, the following are assumed to be **already configured**:

* Spring Boot project created
* Spring MVC enabled
* Spring Security dependency added
* Login form working
* In-memory authentication configured
* `profile.jsp` page already created
* `layout.jsp` already exists

> Since page setup was covered earlier, this lesson focuses **only on authentication tags and usage**.

---

## 3. Spring Boot Dependencies (JSP + Security)

### pom.xml

```xml
<dependencies>

    <!-- Spring MVC + Embedded Tomcat -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- Spring Security JSP Tag Library -->
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-taglibs</artifactId>
    </dependency>

    <!-- JSP Support -->
    <dependency>
        <groupId>org.apache.tomcat.embed</groupId>
        <artifactId>tomcat-embed-jasper</artifactId>
    </dependency>

    <!-- JSTL -->
    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>jstl</artifactId>
    </dependency>

</dependencies>
```

Spring Boot automatically manages dependency versions.

Why Each Dependency Is Needed (Simple Explanation)

| Dependency                     | Why it is required                                   |
| ------------------------------ | ---------------------------------------------------- |
| `spring-boot-starter-web`      | Enables Spring MVC and embedded Tomcat               |
| `spring-boot-starter-security` | Authentication, authorization, filter chain          |
| `spring-security-taglibs`      | Enables `<sec:authentication>` and `<sec:authorize>` |
| `tomcat-embed-jasper`          | Allows JSP files to be rendered                      |
| `jstl`                         | Required for `<c:if>` and EL expressions             |

---

## 4. JSP Configuration in Spring Boot

### application.properties

```properties
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp
```

---

## 5. JSP Folder Structure

Spring Boot requires JSP files to be placed under:

```
src/main/webapp/
 └── WEB-INF/
     └── jsp/
         ├── login.jsp
         ├── profile.jsp
         └── layout.jsp
```

> JSP files must **not** be placed under `resources/templates`.

---

## 6. Declaring JSP Tag Libraries

Every JSP that uses Spring Security tags must declare the tag libraries:

```jsp
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
```

### Why This Is Required

* JSP does not recognize Spring Security tags by default
* This enables:

    * `<sec:authentication>`
    * `<sec:authorize>`
    * `<sec:csrfInput>`

---

## 7. The Authentication Tag – Core Concept

Spring Security stores login information inside the **SecurityContext**.
The `<sec:authentication>` tag allows JSP pages to **read authentication data**.

### Display the Current Username

```jsp
<sec:authentication property="principal.username" />
```

### Output

```
user
```

### Concept Explanation

| Term           | Meaning                          |
| -------------- | -------------------------------- |
| Authentication | Represents the logged-in session |
| Principal      | The user object                  |
| Username       | Login identifier                 |

---

## 8. Displaying User Authorities (Roles)

Every authenticated user has **authorities** (roles).

### JSP Code

```jsp
<sec:authentication property="principal.authorities" />
```

### Output

```
[ROLE_USER]
```

Spring Security automatically assigns roles based on configuration.

---

## 9. Accessing the Authentication Object Directly

Instead of accessing `principal`, we can access the **Authentication object itself**.

### Example

```jsp
<sec:authentication property="name" />
```

### Explanation

* `name` maps to `getName()`
* The underlying object type is:

```
org.springframework.security.core.Authentication
```

---

## 10. How Property Resolution Works

Spring Security resolves properties using **getter methods**.

| JSP Property  | Java Method        |
| ------------- | ------------------ |
| `name`        | `getName()`        |
| `authorities` | `getAuthorities()` |
| `principal`   | `getPrincipal()`   |

### Equivalent Examples

```jsp
<sec:authentication property="authorities" />
```

```jsp
<sec:authentication property="principal.authorities" />
```

Both produce the same result.

---

## 11. Accessing Custom Principal Fields (Advanced)

If you implement a **custom UserDetails**, you can expose additional fields.

### Example

```jsp
<sec:authentication property="principal.organization" />
```

### Requirement

Your principal class must define:

```java
public String getOrganization()
```

This is commonly used for:

* Organization name
* Department
* Account ID

---

## 12. Storing Authentication Data in JSP Variables

Instead of rendering values directly, we can store them in variables.

### Example

```jsp
<sec:authentication 
    property="principal.username"
    var="currentUserName"
    scope="page"/>
```

### Benefits

* Reuse data
* Cleaner JSP
* Enables conditional logic

---

## 13. Using Authentication Data with JSTL Logic

Once stored, the variable can be used with JSTL.

```jsp
<c:if test="${currentUserName.startsWith('u')}">
    <div>User name starts with 'u'</div>
</c:if>
```

This is **standard JSTL**, not a Spring Security expression.

---

## 14. Authentication Request Flow (Diagram Placeholder)

```
[ Browser ]
     |
     v
[ Spring Security Filter Chain ]
     |
     v
[ AuthenticationManager ]
     |
     v
[ SecurityContextHolder ]
     |
     v
[ JSP View ]
     |
     v
<sec:authentication />
```

---

## 15. Mini-Project: User Profile Page

### Objective

Create a profile page that displays:

* Username
* Roles
* Conditional UI message

---

### Step 1: Security Configuration (Spring Boot 3)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .defaultSuccessUrl("/profile", true)
            )
            .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User
            .withUsername("user")
            .password("{noop}pass")
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(user);
    }
}
```

---

### Step 2: Controller Mapping

```java
@GetMapping("/profile")
public String profile() {
    return "profile";
}
```

---

### Step 3: profile.jsp

```jsp
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>User Profile</h2>

<p>Username:
   <sec:authentication property="principal.username"/>
</p>

<p>Authorities:
   <sec:authentication property="authorities"/>
</p>

<sec:authentication 
    property="principal.username"
    var="currentUserName"/>

<c:if test="${currentUserName == 'user'}">
    <div>Welcome, standard user!</div>
</c:if>
```

---

## 16. Testing the Mini-Project

| Action               | Expected Result |
| -------------------- | --------------- |
| Login with user/pass | Success         |
| Visit `/profile`     | Page loads      |
| Username shown       | `user`          |
| Role shown           | `ROLE_USER`     |
| Conditional message  | Displayed       |

---

## 17. Common Beginner Mistakes

| Mistake                       | Explanation                  |
| ----------------------------- | ---------------------------- |
| Missing taglib                | Security tags won’t work     |
| JSP filename case mismatch    | JSP is case-sensitive        |
| Assuming UI hiding = security | Backend rules still required |
| Accessing missing properties  | Getter must exist            |

---

## 18. Key Takeaways

* `<sec:authentication>` exposes login data to JSP
* JSP properties map directly to Java getters
* You can access:

    * Username
    * Roles
    * Authentication object
    * Custom fields
* Variables allow reuse and conditional logic
* Ideal for profile pages, dashboards, and personalized UI

---

## 19. What’s Next

Next lessons can cover:

* `<sec:authorize>` for role-based UI
* CSRF protection tags
* SecurityFilterChain filters explained
* JSP vs Thymeleaf security comparison
* Exam-oriented diagrams and summaries

---

