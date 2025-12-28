
---

# Lesson Notes: Simple Registration Flow

---

## 1. Introduction

In this lesson, we implement a **simple registration flow** as the first step toward supporting **real users** in a Spring Security–based application.

The goal is not to build a production-ready solution yet, but to:

* Create a working end-to-end registration process
* Persist users in the database
* Prepare the foundation for authentication using real users in the next lesson

By the end of this lesson, users will be able to:

* Access a registration page
* Submit registration details
* Be stored in the database
* Be redirected to the login page after registration

However, they **will not yet be able to log in**, which is intentional and addressed in the next lesson.

---

## 2. Introducing the Service Layer

### 2.1 Why a Service Layer?

In the previous module, controllers interacted **directly with repositories**.
In this module, we introduce a **simple service layer** to sit between:

* Controller layer
* Repository layer

At this stage:

* Only one service is introduced: **User Service**
* Interfaces and implementations are used: `IUserService` and `UserService`
* Controllers may still use repositories directly when convenient

This gradual approach allows flexibility now, while preparing for stricter layering in later modules.

---

## 3. Simple User Registration

### 3.1 Registration Page Entry Point

The first visible change is adding a **Sign Up link** on the login page.
This link directs unauthenticated users to the registration page.

---

### (Diagram Space – Login Page Navigation)

```
Login Page
   |
   └── Sign Up → Registration Page
```

---

## 4. Registration Controller

### 4.1 Displaying the Registration Form

A new controller is created to handle the registration flow.

```java
@Controller
class RegistrationController {

    @Autowired 
    private IUserService userService;

    @RequestMapping(value = "signup")
    public ModelAndView registrationForm() {
        return new ModelAndView("registrationPage", "user", new User());
    }
}
```

Key points:

* The controller maps `/signup` to the registration page
* A new `User` object backs the form
* Thymeleaf uses this object for data binding

---

## 5. Registration Page (Frontend)

The registration page is created under the templates directory.

Initially:

* The page is a basic Thymeleaf template
* A form is added to drive the registration process

### 5.1 Form Basics

The form:

* Uses HTTP `POST`
* Submits to `/user/register`
* Is backed by a `User` object

The first field introduced is **email**, which is critical because:

* Email is used as the **username** during authentication

---

### 5.2 Completing the Form

The registration form is expanded to include:

* Email
* Password
* Password confirmation

Password confirmation is required because:

* Passwords are sensitive fields
* Users can easily mistype them

These three fields are sufficient for a **basic registration flow**.

---

### 5.3 Submit Action

A simple **Register** button completes the form and submits the data.

---

### (Diagram Space – Registration Form)

```
[ Email ]
[ Password ]
[ Confirm Password ]
[ Register ]
```

---

## 6. Handling Registration Submission

### 6.1 Registration Logic

The registration logic is implemented in the same controller:

```java
@RequestMapping(value = "user/register")
public ModelAndView registerUser(@Valid User user, BindingResult result) {

    if (result.hasErrors()) {
        return new ModelAndView("registrationPage", "user", user);
    }

    try {
        userService.registerNewUser(user);
    } catch (EmailExistsException e) {
        result.addError(new FieldError("user", "email", e.getMessage()));
        return new ModelAndView("registrationPage", "user", user);
    }

    return new ModelAndView("redirect:/login");
}
```

### 6.2 Key Steps Explained

1. **Validation**

    * The `@Valid` annotation triggers validation
    * `BindingResult` captures validation errors

2. **Error Handling**

    * If validation fails, the user is returned to the registration page
    * If the email already exists, an error is added to the model

3. **User Registration**

    * The user is registered via the service layer
    * Business logic is encapsulated in `IUserService`

4. **Redirect**

    * On success, the user is redirected to the login page

---

## 7. Security Configuration Update

For the registration flow to work, unauthenticated users must be allowed to access:

* The registration page (`/signup`)
* The registration action (`/user/register`)

This is achieved by adding custom rules in the Spring Security configuration.

Without this step:

* Spring Security would block access to the registration flow

---

## 8. Running the Application

At runtime:

* The login page now displays a **Sign Up** link
* Clicking the link opens the registration page
* The user enters email, password, and confirmation
* On submission:

    * The user is saved
    * The browser is redirected to the login page

This confirms the **registration process works end to end**.

---

## 9. Limitation of the Current Implementation

Although registration succeeds:

* Logging in with the newly registered user **fails**
* Spring Security still uses in-memory authentication
* The database users are not yet used for authentication

This limitation is intentional and sets the stage for the next lesson.

---

## 10. Transition to Real User Authentication

To authenticate registered users, Spring Security must:

* Retrieve users from the database
* Use a `UserDetailsService`

---

## 11. Defining a UserDetailsService

### 11.1 Purpose

`UserDetailsService`:

* Is used by `DaoAuthenticationProvider`
* Retrieves user data during authentication
* Loads username, password, and authorities

---

### 11.2 User Model

```java
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;
}
```

---

### 11.3 User Repository

```java
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
```

---

### 11.4 Custom UserDetailsService

```java
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return new MyUserPrincipal(user);
    }
}
```

---

### 11.5 UserDetails Implementation

```java
public class MyUserPrincipal implements UserDetails {
    private User user;

    public MyUserPrincipal(User user) {
        this.user = user;
    }
}
```

---

## 12. Wiring UserDetailsService into Security

### 12.1 Annotation-Based Configuration

Because the service is annotated with `@Service`, Spring:

* Detects it automatically
* Registers it as a bean

It can also be explicitly wired using:

```java
auth.userDetailsService(userDetailsService);
```

---

### 12.2 XML Configuration (Alternative)

```xml
<bean id="myUserDetailsService" 
  class="org.baeldung.security.MyUserDetailsService"/>

<security:authentication-manager>
    <security:authentication-provider user-service-ref="myUserDetailsService">
        <security:password-encoder ref="passwordEncoder"/>
    </security:authentication-provider>
</security:authentication-manager>
```

---

## 13. Other Database-Backed Authentication Options

Spring Security also provides:

* JDBC authentication via `jdbcAuthentication`
* `JdbcUserDetailsManager`, which implements `UserDetailsService`

This approach:

* Is easier to implement
* Works well with Spring Boot auto-configured DataSources
* Offers less flexibility than a custom service

---

## 14. Upgrade Notes (Spring Boot 3)

With Spring Boot 3:

* Java EE has transitioned to **Jakarta EE**
* Packages such as `javax.*` have moved to `jakarta.*`
* Annotations like `@Valid` now come from `jakarta.validation`

---

## 15. Summary

In this lesson, we:

* Introduced a service layer
* Built a simple registration page
* Implemented user registration logic
* Allowed public access to registration endpoints
* Successfully persisted users
* Identified why authentication still fails
* Prepared the system for real-user authentication

This completes the **Simple Registration Flow** and sets the foundation for enabling login with registered users in the next lesson.

---

