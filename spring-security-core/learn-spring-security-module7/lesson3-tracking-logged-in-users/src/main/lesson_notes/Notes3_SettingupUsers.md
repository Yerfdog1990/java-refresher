
---

# 📘 Setting Up Users at Startup in Spring Security

---

## 1. Goal

In this lesson, we explore **two practical approaches** for setting up default users when a Spring Boot application starts:

1. **Using Spring Security’s `UserBuilder`**
2. **Initializing users from a property file**

These users:

* Are stored **in memory**
* Exist only **while the application is running**
* Are ideal for:

  * Development
  * Demos
  * Integration tests
  * Learning Spring Security

⚠️ **Important**
These approaches are **not recommended for production** because:

* Users are lost on restart
* Credentials may be exposed in code or config
* No audit or lifecycle management

---

## 2. Preparation (Conceptual)

### What We Are NOT Doing

* ❌ No database persistence
* ❌ No custom `User` entity
* ❌ No JPA repositories

### What We ARE Doing

* ✅ Using `InMemoryUserDetailsManager`
* ✅ Letting Spring Security authenticate users from memory
* ✅ Configuring everything at application startup

---

## 3. High-Level Authentication Flow

```
Application Startup
       |
       v
UserDetailsService Bean Created
       |
       v
Users Loaded into InMemoryUserDetailsManager
       |
       v
Authentication Request
       |
       v
AuthenticationManager
       |
       v
UserDetailsService (In-Memory)
       |
       v
Authentication Success / Failure
```

---

## 4. Approach 1: Using `UserBuilder` (Code-Based Users)

### 4.1 When to Use This Approach

* ✔ Very quick setup
* ✔ Fully type-safe
* ✔ Best for demos and tutorials
* ❌ Requires code changes to update users

---

### 4.2 Password Encoder (Required)

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

---

### 4.3 Defining Users with `UserBuilder`

```java
@Configuration
public class UsersFromUserBuilder {

    private final PasswordEncoder passwordEncoder;

    public UsersFromUserBuilder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean("fromUserBuilder")
    public UserDetailsService userDetailsService() {

        UserDetails user =
            User.builder()
                .username("user@email.com")
                .password(passwordEncoder.encode("pass"))
                .roles("USER")
                .build();

        UserDetails admin =
            User.builder()
                .username("admin@email.com")
                .password(passwordEncoder.encode("admin"))
                .roles("ADMIN")
                .build();

        UserDetails tester =
            User.builder()
                .username("tester@email.com")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user, admin, tester);
    }
}
```

---

### 4.4 Why Use a Bean Name?

You may already have another `UserDetailsService` (e.g., database-backed).

```java
@Bean("fromUserBuilder")
public UserDetailsService userDetailsService() { ... }
```

This allows **explicit selection** later using `@Qualifier`.

---

### 4.5 Wiring It into Spring Security (Modern Style)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(
        @Qualifier("fromUserBuilder") UserDetailsService userDetailsService
    ) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService)
            .formLogin(Customizer.withDefaults());

        return http.build();
    }
}
```

---

### 4.6 Demo Outcome

✔ Login works with:

* `user@email.com / pass`
* `admin@email.com / admin`

* ✔ No database required
* ✔ Users available immediately at startup

---

## 5. Approach 2: Creating Users from a Property File

### 5.1 When to Use This Approach

* ✔ No code changes for user updates
* ✔ Cleaner separation of concerns
* ✔ Ideal for shared test environments
* ❌ Passwords must be pre-encoded

---

## 5.2 Property File (`users.properties`)

```properties
# username=password,ROLE

user@email.com=$2a$10$.1W8V9YD8kxEu2sJV7caV.L2fXL6NSxtwemuSO/c7PfGTCyfr1yV2,ROLE_USER
admin@email.com=$2a$10$T9ZFcV0bMOpvFdFYYHIFx.PhH8ZE5GxUX3OEZ3xDGKuyghZNMT7X6,ROLE_ADMIN
propfile@email.com=$2a$10$.1W8V9YD8kxEu2sJV7caV.L2fXL6NSxtwemuSO/c7PfGTCyfr1yV2,ROLE_ADMIN
```

📌 Format:

```
username=password[,enabled|disabled],ROLE[,ROLE2]
```

---

## 5.3 Loading Users from Properties

```java
@Configuration
public class UsersFromPropertiesFile {

    @Bean("fromPropFile")
    public FactoryBean<? extends UserDetailsService> userDetailsService() {

        return UserDetailsManagerResourceFactoryBean
                .fromResourceLocation("classpath:users.properties");
    }
}
```

### Why This Works

* Spring reads the property file
* Builds `UserDetails` objects
* Stores them in `InMemoryUserDetailsManager`

---

## 5.4 Switching Authentication Source

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(
        @Qualifier("fromPropFile") UserDetailsService userDetailsService
    ) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService)
            .formLogin(Customizer.withDefaults());

        return http.build();
    }
}
```

* ✔ Now **only users from `users.properties` can log in**
* ✔ Code remains unchanged

---

## 6. Authentication Flow Comparison

### UserBuilder Flow

```
Startup
  |
  v
Java Config
  |
  v
UserBuilder -> UserDetails
  |
  v
InMemoryUserDetailsManager
```

### Properties File Flow

```
Startup
  |
  v
users.properties
  |
  v
UserDetailsManagerResourceFactoryBean
  |
  v
InMemoryUserDetailsManager
```

---

## 7. In-Memory Authentication Summary

| Feature        | In-Memory Users   |
| -------------- | ----------------- |
| Persistence    | ❌ No              |
| Restart Safe   | ❌ No              |
| Speed          | ✅ Very Fast       |
| Setup          | ✅ Simple          |
| Production Use | ❌ Not Recommended |

---

## 8. Password Encoding Notes

### Recommended (BCrypt)

```java
$2a$10$...
```

Generate using:

* Spring Boot CLI
* `htpasswd -B`
* Online bcrypt tools (for testing only)

---

### ⚠️ `User.withDefaultPasswordEncoder()`

```java
UserDetails user = User.withDefaultPasswordEncoder()
    .username("user")
    .password("password")
    .roles("USER")
    .build();
```

* ❌ Only for **getting started**
* ❌ Password visible in compiled bytecode

---

## 9. Conclusion

In this lesson, you learned **two safe and modern ways** to set up default users at application startup:

### ✅ UserBuilder

* Best for tutorials
* Fully code-driven
* Fast iteration

### ✅ Property File

* Better separation
* Easy to update users
* Cleaner for shared environments

Both approaches:

* Use `InMemoryUserDetailsManager`
* Work perfectly with **modern Spring Security**
* Should **never replace real persistence in production**

---
