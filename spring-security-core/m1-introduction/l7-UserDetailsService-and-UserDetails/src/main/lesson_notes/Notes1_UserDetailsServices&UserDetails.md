
---

# **Spring Security — UserDetailsService and UserDetails with Example**

**Last Updated:** 23 Jul, 2025

Spring Security is a framework that allows a programmer to use JEE components to set security limitations on Spring-framework-based Web applications. In a nutshell, it’s a library that can be utilized and customized to suit the demands of the programmer.

UserDetails and UserDetailsService are two major concepts to learn in Spring Security. This lesson will cover these concepts with proper examples.

---

## **1. UserDetailsService and UserDetails**

In Spring Security, the **UserDetailsService** interface is a core component used for **loading user-specific data**. It is responsible for retrieving user information from a backend data source, such as a database or an external service, and returning an instance of the **UserDetails** interface.

* **UserDetailsService interface** has a single method:

```java
UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
```

* Returns a **UserDetails object**, representing the authenticated user in Spring Security.

**UserDetails** contains:

* Username
* Password
* Authorities (roles)
* Additional attributes

We will demonstrate using:

1. **InMemoryUserDetailsManager** – an implementation of UserDetailsService that stores user details in memory. Useful for testing and development.
2. **User class** – a convenient builder class for creating UserDetails objects with attributes like username, password, and roles.

---

## **2. Creating Users in Memory**

You can create users using **InMemoryUserDetailsManager** and **User class**.

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();

    // Method 1 - Creating username, password, and role
    ArrayList<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
    SimpleGrantedAuthority role1 = new SimpleGrantedAuthority("USER");
    SimpleGrantedAuthority role2 = new SimpleGrantedAuthority("ADMIN");
    roles.add(role1);
    roles.add(role2);

    User anshulUser = new User("anshul", "123", roles);

    // Method 2 - Using builder
    // UserDetails anshulUser = User.withUsername("Anshul").password("123").roles("USER", "ADMIN").build();

    userDetailsManager.createUser(anshulUser);
    auth.userDetailsService(userDetailsManager);
}
```

---

## **3. UserDetailsService**

* Used by **DaoAuthenticationProvider** to retrieve username, password, and other attributes for authentication.
* Spring Security provides: **in-memory**, **JDBC**, and **caching** implementations.
* Custom authentication can be defined by exposing a **custom UserDetailsService** as a bean:

```java
@Bean
CustomUserDetailsService customUserDetailsService() {
    return new CustomUserDetailsService();
}
```

* This is used only if the AuthenticationManagerBuilder has not been populated and no AuthenticationProviderBean is defined.

---

## **4. DaoAuthenticationProvider**

* Implementation of AuthenticationProvider that uses **UserDetailsService** and **PasswordEncoder** to authenticate a username and password.

---

![img.png](img.png)

---

**Flow:**

1. The authentication Filter from the Reading the Username & Password section passes a UsernamePasswordAuthenticationToken to the AuthenticationManager, which is implemented by ProviderManager.
2. The ProviderManager is configured to use an AuthenticationProvider of type DaoAuthenticationProvider.
3. DaoAuthenticationProvider looks up the UserDetails from the UserDetailsService.
4. DaoAuthenticationProvider uses the PasswordEncoder to validate the password on the UserDetails returned in the previous step.
5. When authentication is successful, the Authentication that is returned is of type UsernamePasswordAuthenticationToken and has a principal that is the UserDetails returned by the configured UserDetailsService and a set of authorities containing at least FACTOR_PASSWORD. Ultimately, the returned UsernamePasswordAuthenticationToken is set on the SecurityContextHolder by the authentication Filter.

---

## **5. In-Memory Authentication Example**

* `InMemoryUserDetailsManager` implements UserDetailsService to provide support for username/password authentication stored in memory.

```java
@Bean
public UserDetailsService users() {
    UserDetails user = User.builder()
        .username("user")
        .password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
        .roles("USER")
        .build();

    UserDetails admin = User.builder()
        .username("admin")
        .password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
        .roles("USER", "ADMIN")
        .build();

    return new InMemoryUserDetailsManager(user, admin);
}
```

* For simple demos, `User.withDefaultPasswordEncoder()` can be used to store passwords in memory (not recommended for production).

---

## **6. XML Configuration Example**

```xml
<user-service>
    <user name="user" password="{noop}password" authorities="ROLE_USER" />
    <user name="admin" password="{noop}password" authorities="ROLE_USER,ROLE_ADMIN" />
</user-service>
```

---

## **7. JDBC Authentication**

* `JdbcUserDetailsManager` extends `JdbcDaoImpl` to provide support for username/password authentication retrieved via JDBC.

### **Default Schema:**

```sql
create table users(
    username varchar_ignorecase(50) not null primary key,
    password varchar_ignorecase(500) not null,
    enabled boolean not null
);

create table authorities (
    username varchar_ignorecase(50) not null,
    authority varchar_ignorecase(50) not null,
    constraint fk_authorities_users foreign key(username) references users(username)
);
```

* Groups schema example (optional):

```sql
create table groups (
    id bigint generated by default as identity(start with 0) primary key,
    group_name varchar_ignorecase(50) not null
);
```

* **Embedded DataSource Example:**

```java
@Bean
DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
        .setType(H2)
        .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
        .build();
}
```

* Creating users with `JdbcUserDetailsManager`:

```java
UserDetails user = User.builder()
    .username("user")
    .password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
    .roles("USER")
    .build();

UserDetails admin = User.builder()
    .username("admin")
    .password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
    .roles("USER","ADMIN")
    .build();

JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
users.createUser(user);
users.createUser(admin);
```

---

## **8. Caching UserDetails**

* Reduces repeated computation when loading user data.
* Use **CachingUserDetailsService** or `@Cacheable` annotation.

```java
@Bean
public CachingUserDetailsService cachingUserDetailsService(UserCache userCache) {
    UserDetailsService delegate = ...;
    CachingUserDetailsService service = new CachingUserDetailsService(delegate);
    service.setUserCache(userCache);
    return service;
}
```

* **Using @Cacheable:**

```java
@Service
public class MyCustomUserDetailsImplementation implements UserDetailsService {

    @Override
    @Cacheable
    public UserDetails loadUserByUsername(String username) {
        // logic to get user details
        return userDetails;
    }
}
```

* Disable credential erasure to keep passwords available for validation:

```java
@Autowired
public void configure(AuthenticationManagerBuilder builder) {
    builder.eraseCredentials(false);
}
```

---

### **Summary Table**

| Concept                    | Purpose                                                  | Notes                                           |
| -------------------------- | -------------------------------------------------------- | ----------------------------------------------- |
| UserDetailsService         | Load user-specific data                                  | Can be custom, in-memory, JDBC, or cached       |
| UserDetails                | Represents authenticated user                            | Holds username, password, roles, account status |
| InMemoryUserDetailsManager | In-memory user store                                     | Good for testing, dev only                      |
| JdbcUserDetailsManager     | JDBC-based user store                                    | Default schema provided, supports groups        |
| DaoAuthenticationProvider  | Authenticates username/password using UserDetailsService | Works with PasswordEncoder                      |
| CachingUserDetailsService  | Caches user details                                      | Avoid repeated DB queries                       |

---

Perfect! Let’s upgrade your example to **Spring Boot 3 / Spring Security 6 (Spring 6)** and use **Thymeleaf** instead of JSP for the login and home pages. I’ll provide a full example demonstrating `UserDetailsService` and `UserDetails`.

---

# **Spring Boot 3 + Spring Security 6 Example Using Thymeleaf**

### **1. `pom.xml` Dependencies**

```xml
<dependencies>
    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Boot Starter Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- Spring Boot Starter Thymeleaf -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>

    <!-- Optional: Lombok for brevity -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---

### **2. Spring Boot Application Class**

```java
package com.gfg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringSecurityThymeleafApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityThymeleafApplication.class, args);
    }
}
```

---

### **3. Security Configuration (`SecurityConfig.java`)**

```java
package com.gfg.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        // Create UserDetails objects
        UserDetails user = User.withUsername("user")
                .password("123")
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password("123")
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Not for production
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/custom-login")
                .loginProcessingUrl("/process-login")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .permitAll()
            );

        return http.build();
    }
}
```

---

### **4. Controllers**

**`HomeController.java`**

```java
package com.gfg.controller;

import java.security.Principal;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homePage(Principal principal, Authentication auth, Model model) {
        model.addAttribute("username", principal.getName());
        Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
        model.addAttribute("roles", roles);
        return "home";
    }
}
```

**`LoginController.java`**

```java
package com.gfg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/custom-login")
    public String loginPage() {
        return "login";
    }
}
```

---

### **5. Thymeleaf Views**

**`login.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Login Page</title>
</head>
<body>
    <h1>Custom Login</h1>
    <form th:action="@{/process-login}" method="post">
        <div>
            <label>Username:</label>
            <input type="text" name="username" />
        </div>
        <div>
            <label>Password:</label>
            <input type="password" name="password" />
        </div>
        <div>
            <button type="submit">Login</button>
        </div>
    </form>
</body>
</html>
```

**`home.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Home Page</title>
</head>
<body>
    <h1>Welcome, <span th:text="${username}"></span>!</h1>
    <p>Your Roles: <span th:text="${roles}"></span></p>

    <form th:action="@{/logout}" method="post">
        <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
        <button type="submit">Logout</button>
    </form>
</body>
</html>
```

---

### ✅ **6. How It Works**

1. Run the Spring Boot application.
2. Visit: `http://localhost:8080/custom-login`.
3. Login credentials:

    * Username: `user` / Password: `123`
    * Username: `admin` / Password: `123`
4. Redirects to `/` (home page) and displays:

    * Current username
    * Roles
5. Logout works with CSRF protection.

---

Perfect! Let’s upgrade the previous **Spring Boot + Spring Security 6 + Thymeleaf** example to **use JDBC authentication** with a database. I’ll provide a complete example using **H2 in-memory database** for simplicity.

---

# **Spring Boot 3 + Spring Security 6 + JDBC Authentication + Thymeleaf**

---

### **1. `pom.xml` Dependencies**

```xml
<dependencies>
    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Boot Starter Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- Spring Boot Starter Thymeleaf -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>

    <!-- H2 Database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

---

### **2. `application.properties`**

```properties
# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.platform=h2

# Enable H2 console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Hibernate DDL auto
spring.jpa.hibernate.ddl-auto=none

# Thymeleaf
spring.thymeleaf.cache=false
```

---

### **3. Initialize Database Schema**

Create a schema and data file under `src/main/resources`:

**`schema.sql`**

```sql
CREATE TABLE users (
    username VARCHAR(50) NOT NULL PRIMARY KEY,
    password VARCHAR(500) NOT NULL,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE authorities (
    username VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    CONSTRAINT fk_users FOREIGN KEY(username) REFERENCES users(username)
);

CREATE UNIQUE INDEX ix_auth_username ON authorities(username, authority);
```

**`data.sql`**

```sql
INSERT INTO users(username, password, enabled) VALUES
('user', '{noop}123', true),
('admin', '{noop}123', true);

INSERT INTO authorities(username, authority) VALUES
('user', 'ROLE_USER'),
('admin', 'ROLE_USER'),
('admin', 'ROLE_ADMIN');
```

> Here `{noop}` tells Spring Security the password is not encoded. For production, use BCrypt.

---

### **4. Security Configuration (`SecurityConfig.java`)**

```java
package com.gfg.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public JdbcUserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        // Uses default queries for users and authorities table
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // For demo only
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/custom-login")
                .loginProcessingUrl("/process-login")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .permitAll()
            );
        return http.build();
    }
}
```

---

### **5. Controllers**

**`HomeController.java`**

```java
package com.gfg.controller;

import java.security.Principal;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homePage(Principal principal, Authentication auth, Model model) {
        model.addAttribute("username", principal.getName());
        Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
        model.addAttribute("roles", roles);
        return "home";
    }
}
```

**`LoginController.java`**

```java
package com.gfg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/custom-login")
    public String loginPage() {
        return "login";
    }
}
```

---

### **6. Thymeleaf Views**

**`login.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Login Page</title>
</head>
<body>
    <h1>Custom Login</h1>
    <form th:action="@{/process-login}" method="post">
        <div>
            <label>Username:</label>
            <input type="text" name="username" />
        </div>
        <div>
            <label>Password:</label>
            <input type="password" name="password" />
        </div>
        <div>
            <button type="submit">Login</button>
        </div>
    </form>
</body>
</html>
```

**`home.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Home Page</title>
</head>
<body>
    <h1>Welcome, <span th:text="${username}"></span>!</h1>
    <p>Your Roles: <span th:text="${roles}"></span></p>

    <form th:action="@{/logout}" method="post">
        <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
        <button type="submit">Logout</button>
    </form>
</body>
</html>
```

---

### ✅ **7. How It Works**

1. Run the application: `http://localhost:8080/custom-login`.
2. Login with credentials:

    * Username: `user` / Password: `123`
    * Username: `admin` / Password: `123`
3. Redirects to `/` (home page) with current username and roles.
4. Logout is fully functional with CSRF protection.

---


