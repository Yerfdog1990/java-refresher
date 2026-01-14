
---

# Hashing Passwords in Spring Security

---

## Learning Objectives

By the end of this lesson, learners will be able to:

* Explain **why password hashing is required**
* Understand the **authentication flow involving password encoders**
* Distinguish between **deprecated and modern password encoders**
* Correctly **configure password hashing** during registration and authentication
* Explain **why MD5 and SHA-256 are no longer secure**
* Implement **BCrypt-based password hashing** in Spring Security

---

## 1. Why Hash Passwords?

Storing passwords in **plain text** is one of the most critical security vulnerabilities in an application.

If an attacker gains access to the database:

* Plain-text passwords are immediately exposed
* Users often reuse passwords across systems
* The damage extends beyond your application

### Security Goal

> **Never store passwords in a reversible format.**

This is achieved using **one-way hashing**.

---

## 2. Password Hashing Flow (Conceptual Diagram)

```
User Password (plain text)
        │
        ▼
PasswordEncoder.encode()
        │
        ▼
Hashed Password
        │
        ▼
Stored in Database
```

At login time:

```
User enters password
        │
        ▼
PasswordEncoder.matches()
        │
        ▼
Compare hashes (NOT passwords)
```

---

## 2.1 First – A Deprecated Password Encoder (MD5)

### Purpose of This Step

This section demonstrates:

* How hashing works technically
* Why **deprecated encoders exist**
* Why they should **not** be used in modern systems

---

### Step 1: Define the PasswordEncoder Bean

```java
@Bean
public PasswordEncoder encoder() {
    return new Md5PasswordEncoder();
}
```

* `Md5PasswordEncoder` uses the **MD5 hashing algorithm**
* It is **deprecated** and insecure
* Used here **only for demonstration**

---

### Step 2: Wire the Encoder into Authentication

```java
auth.userDetailsService(userDetailsService)
    .passwordEncoder(encoder());
```

This ensures that:

* Authentication compares **hashed passwords**
* Spring Security no longer expects plain text

---

### Step 3: Encode the Password When Creating a User

```java
user.setPassword(encoder().encode("pass", null));
```

Important:

* Password hashing must happen **before saving**
* The database should **never** see the plain password

---

### Step 4: Verify the Database

After running the application and checking the database:

```
Before hashing:
password = pass

After hashing:
password = 1a1dc91c907325c69271ddf0c944bc72
```

The password is:

* No longer readable
* No longer stored as plain text

---

### Why MD5 Is Deprecated

| Issue    | Explanation                   |
| -------- | ----------------------------- |
| Too fast | Billions of hashes per second |
| No salt  | Vulnerable to rainbow tables  |
| Broken   | Collisions are trivial        |

**Conclusion:**
MD5 is **not secure** and should never be used in production.

---

## 2.2 The New Password Encoder (StandardPasswordEncoder)

### Switching to a Non-Deprecated Encoder

```java
@Bean
public PasswordEncoder encoder() {
    return new StandardPasswordEncoder();
}
```

* Uses **SHA-256**
* Introduced as a replacement for MD5
* Still considered **weak by modern standards**

---

### Inject the Encoder into the User Service

```java
@Autowired
private PasswordEncoder encoder;
```

---

### Encode Password on User Registration

```java
public User registerNewUser(final User user)
        throws EmailExistsException {

    user.setPassword(encoder.encode(user.getPassword()));
    return userRepository.save(user);
}
```

---

### Encode Password on Password Change

```java
public void changeUserPassword(final User user,
                               final String password) {
    user.setPassword(encoder.encode(password));
    userRepository.save(user);
}
```

---

### Password Hashing Lifecycle Diagram

```
User Registration / Password Change
            │
            ▼
PasswordEncoder.encode()
            │
            ▼
Hashed Password
            │
            ▼
Database
```

---

## 2.3 Upgrade Notes (Spring Boot 2+)

Starting with **Spring Boot 2**:

* `Md5PasswordEncoder` → **removed**
* `StandardPasswordEncoder` → **deprecated**
* Explicit password encoding → **mandatory**

### Why?

| Reason             | Explanation           |
| ------------------ | --------------------- |
| SHA-256 too fast   | GPU cracking          |
| No adaptive cost   | Cannot slow attackers |
| No memory hardness | Vulnerable to ASICs   |

---

## Modern Recommendation

> **Use BCryptPasswordEncoder**

---

## 3. BCryptPasswordEncoder (Recommended)

### Define the BCrypt Encoder Bean

```java
@Bean
public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
}
```

---

### Why BCrypt Is Secure

| Feature             | Benefit                 |
| ------------------- | ----------------------- |
| Adaptive cost       | Slows brute force       |
| Random salt         | Prevents rainbow tables |
| Salt stored in hash | No extra storage        |
| Widely supported    | Industry standard       |

---

### BCrypt Hash Structure

Example hash:

```
$2a$10$ZLhnHxdpHETcxmtEStgpI./Ri1mksgJ9iDP36FmfMdYyVg9g0b2dq
```

Breakdown:

| Part                     | Meaning                |
| ------------------------ | ---------------------- |
| `2a`                     | BCrypt version         |
| `10`                     | Strength (cost factor) |
| `ZLhnHxdpHETcxmtEStgpI.` | Salt                   |
| Remaining                | Hashed password        |

**Important:**
BCrypt hashes are always **60 characters long**.

---

### Common Mistake

❌ Database column too short
✅ Use at least `VARCHAR(60)`

---

## 4. Encode Password on Registration (Production Flow)

```java
@Autowired
private PasswordEncoder passwordEncoder;

@Override
public User registerNewUserAccount(UserDto accountDto)
        throws EmailExistsException {

    if (emailExist(accountDto.getEmail())) {
        throw new EmailExistsException(
          "There is an account with that email address:"
          + accountDto.getEmail());
    }

    User user = new User();
    user.setFirstName(accountDto.getFirstName());
    user.setLastName(accountDto.getLastName());

    user.setPassword(
        passwordEncoder.encode(accountDto.getPassword())
    );

    user.setEmail(accountDto.getEmail());
    user.setRole(new Role(1, user));

    return repository.save(user);
}
```

---

## 5. Encode Password on Authentication

### Authentication Flow Diagram

```
Login Request
     │
     ▼
DaoAuthenticationProvider
     │
     ▼
PasswordEncoder.matches()
     │
     ▼
Authentication Success / Failure
```

---

### Configure DaoAuthenticationProvider

```java
@Autowired
private UserDetailsService userDetailsService;

@Bean
public DaoAuthenticationProvider authProvider() {
    DaoAuthenticationProvider authProvider =
            new DaoAuthenticationProvider();

    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(encoder());

    return authProvider;
}
```

---

### Java-Based Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecSecurityConfig {

    @Bean
    public AuthenticationManager authManager(
            HttpSecurity http) throws Exception {

        return http
            .getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(authProvider())
            .build();
    }
}
```

---

## 6. Summary Diagram – Complete Password Hashing Lifecycle

```
User Password
     │
     ▼
BCryptPasswordEncoder.encode()
     │
     ▼
Hashed Password (salt + cost)
     │
     ▼
Database
     │
     ▼
Login Attempt
     │
     ▼
PasswordEncoder.matches()
```

---

## Key Takeaways

* **Never store plain-text passwords**
* **Hash once, compare many times**
* **MD5 and SHA-256 are obsolete**
* **BCrypt is the current baseline**
* **Always encode passwords at the service layer**
* **Authentication compares hashes, not passwords**

---

