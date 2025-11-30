
---

# ⭐ Lesson Notes: Basic Security Java Configuration 

## 1. Introduction

Spring Security is the de-facto standard for securing Java applications. In this lesson, you’ll learn:

1. How to enable Spring Security in a Spring Boot project
2. How to configure authentication using Boot properties
3. How to replace property-based config with Java Security Config
4. Modern Spring Security practices **without** `WebSecurityConfigurerAdapter` (deprecated)
5. How to configure Security both **with** and **without** Spring Boot
6. Important upgrade and migration notes (Boot 1 → Boot 2 → Boot 3)

---

# 2. Adding Spring Security Dependency

## 2.1. Using Spring Boot

To enable Spring Security, simply add the Boot starter:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

As soon as you add this dependency:

* Security becomes active automatically
* Boot generates a default user and password (unless overridden)
* All endpoints become secured using form login (Boot 2.x+) or basic auth (Boot 1.x)

---

# 3. Configuring Security with Boot Properties

Before defining any explicit Java config, you can control security using `application.properties`.

### Boot 1.x (Old, for reference):

```properties
security.user.name=user
security.user.password=pass

security.basic.authorize-mode=authenticated
security.basic.path=/**
```

### Boot 2.x+ and Boot 3.x (Updated Properties)

Spring Boot 2 introduced breaking changes:

| Boot 1.x                      | Boot 2.x / Boot 3.x equivalent |
| ----------------------------- | ------------------------------ |
| security.user.name            | spring.security.user.name      |
| security.user.password        | spring.security.user.password  |
| security.basic.authorize-mode | ❌ Removed                      |
| security.basic.path           | ❌ Removed                      |

Modern properties example:

```properties
spring.security.user.name=user
spring.security.user.password=pass
```

After setting these, restart your app and access any endpoint:

* Boot automatically shows a **form login page** (Spring Security 5+)
* Before Spring Boot 2, default was **HTTP Basic login**

---

# 4. Switching to Java-Based Security Configuration

Configuring security using properties is convenient, but limited.
Next, we remove the Boot properties and define a real security config class.

---

# 5. Legacy Security Config (Before Spring Security 5.7)

Historically, Spring apps extended `WebSecurityConfigurerAdapter`:

```java
@EnableWebSecurity
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser("user").password("pass").roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
              .anyRequest().authenticated()
              .and()
              .formLogin();
    }
}
```

### Notes:

* `@EnableWebSecurity` was optional in Boot, but useful for readability.
* Spring-generated login form was used automatically.
* `configureGlobal` allowed customizing authentication sources.

---

# 6. Deprecation of WebSecurityConfigurerAdapter (Spring Security 5.7+)

From Spring Security **5.7** and Boot **2.7+**, the framework moved to a **component-based configuration**.

`WebSecurityConfigurerAdapter` is deprecated.

### New recommended structure:

### ✔ Define a `SecurityFilterChain` bean

### ✔ Define authentication using `UserDetailsService` / `AuthenticationManager` beans

### ✔ Define `PasswordEncoder` explicitly

---

# 7. Modern Security Configuration (Spring Boot 2.7+ / Boot 3)

This is the **correct and up-to-date approach**:

---

## 7.1 Define PasswordEncoder (Mandatory)

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Spring now **requires** encrypted passwords.

---

## 7.2 Define In-Memory Authentication Using UserDetails

```java
@Bean
public UserDetailsService userDetailsService(PasswordEncoder encoder) {

    UserDetails user = User.builder()
        .username("user")
        .password(encoder.encode("pass"))
        .roles("USER")
        .build();

    return new InMemoryUserDetailsManager(user);
}
```

---

## 7.3 Define Authorization & HTTP Security Using SecurityFilterChain

```java
@Configuration
@EnableWebSecurity   // Boot 3: @Configuration is NOT included by this annotation
public class LssSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .formLogin(withDefaults());

        return http.build();
    }
}
```

### Explanation:

* `authorizeHttpRequests` is the modern DSL
* `formLogin(withDefaults())` uses auto-generated Spring login page
* `http.build()` finalizes the filter chain

---

# 8. Optional: WebSecurityCustomizer (Replaces web.ignoring())

Deprecated approach (old):

```java
@Override
public void configure(WebSecurity web) {
    web.ignoring().antMatchers("/assets/**");
}
```

Modern approach:

```java
@Bean
public WebSecurityCustomizer customizer() {
    return web -> web.ignoring().requestMatchers("/assets/**");
}
```

⚠ **Warning:** Prefer `permitAll()` instead—ignoring bypasses filters entirely.

---

# 9. Full Modern Example (Boot 3 / Spring Security 6)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.builder()
            .username("user")
            .password(encoder.encode("pass"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults());

        return http.build();
    }
}
```

This is the correct configuration for **Spring Boot 3 (2024+)**.

---

# 10. Spring Security Without Spring Boot

If not using Boot, include:

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
</dependency>
```

Then configure security using the same Java-based config described above.

Boot is not required to use modern SecurityFilterChain patterns.

---

# 11. Summary of Migration Notes (Important)

### ✔ Boot 1 → Boot 2

* Default login changes **Basic → Form Login**
* Properties renamed to `spring.security.*`
* `security.basic.*` removed

### ✔ Security 5.7+

* `WebSecurityConfigurerAdapter` deprecated
* Use `SecurityFilterChain`
* Use `UserDetailsService` / `AuthenticationManager` beans

### ✔ Boot 3 / Security 6

* `@EnableWebSecurity` no longer includes `@Configuration`
* Must explicitly write:

```java
@Configuration
@EnableWebSecurity
```

* Java 17+ required
* No more `antMatchers` → use `requestMatchers`

---

# 12. What You Learned in This Lesson

1. How to activate Spring Security using Boot starter
2. How to configure simple login using properties
3. How to write a basic security config in Java
4. How to migrate from old to new Security configuration
5. How to define authentication providers and password encoders
6. How Boot and non-Boot projects differ in setup
7. How Spring auto-generates forms for login
8. How security evolved from Boot 1 → Boot 2 → Boot 3

---

