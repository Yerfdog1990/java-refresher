
---

# Remember-Me Authentication with Persistence 

---

## 1. Overview

Remember-Me authentication allows users to stay logged in across browser restarts without re-entering credentials.

In **Spring Security 7.0**, this is implemented using:

**`PersistentTokenBasedRememberMeServices`**

This approach stores **random tokens in a database**, not credentials in cookies, making it suitable for production systems.

---

### 📊 Diagram 1: Where Remember-Me Fits in Spring Security

```
┌──────────┐
│  Browser │
└────┬─────┘
     │ HTTP Request
     ▼
┌──────────────────────────┐
│ Spring Security Filter   │
│ Chain                    │
├──────────────────────────┤
│ UsernamePasswordFilter   │
│ RememberMeFilter         │
│ SecurityContextFilter    │
└──────────┬───────────────┘
           ▼
┌──────────────────────────┐
│ Application Controllers  │
└──────────────────────────┘
```

---

## 2. Why Persistent Remember-Me Is More Secure

Spring Security supports two Remember-Me strategies:

| Strategy         | Storage     | Security |
| ---------------- | ----------- | -------- |
| Hash-based       | Cookie only | Lower    |
| Persistent token | DB + Cookie | Higher   |

### Security Improvements

* No password-derived cookie values
* Tokens revocable from database
* Automatic token theft detection

---

### 📊 Diagram 2: Cookie-Based vs Persistent Token Approach

```
COOKIE-BASED (Hash)
────────────────────────
Cookie = hash(username + password + key)

PERSISTENT TOKEN
────────────────────────
Cookie = random(series, token)
DB     = series → token → username
```

---

## 3. Database Schema (Required)

Spring Security expects a table named:

```
persistent_logins
```

```sql
CREATE TABLE persistent_logins (
    username VARCHAR(64) NOT NULL,
    series VARCHAR(64) PRIMARY KEY,
    token VARCHAR(64) NOT NULL,
    last_used TIMESTAMP NOT NULL
);
```

---

### 📊 Diagram 3: Persistent Token Storage Model

```
┌─────────────────────────┐
│ persistent_logins table │
├───────────┬─────────────┤
│ series    │ PRIMARY KEY │
│ token     │ random      │
│ username  │ user id     │
│ last_used │ timestamp   │
└───────────┴─────────────┘
```

---

## 4. How the Persistent Token Mechanism Works

1. User logs in with Remember-Me enabled
2. Spring Security generates a **series + token**
3. Token is stored in DB
4. Cookie sent to browser
5. Browser restarts
6. Cookie validated against DB
7. Token rotated
8. User authenticated

---

### 📊 Diagram 4: Persistent Remember-Me Authentication Flow

```
[ Login Request ]
        │
        ▼
[ Generate Series + Token ]
        │
        ▼
[ Store Token in Database ]
        │
        ▼
[ Send Cookie to Browser ]
        │
   (Browser Restart)
        │
        ▼
[ Cookie Read ]
        │
        ▼
[ Validate Against DB ]
        │
        ▼
[ Rotate Token ]
        │
        ▼
[ Authentication Success ]
```

---

## 5. Java Configuration (Spring Security 7.0)

### 5.1 PersistentTokenRepository Bean

```java
@Bean
public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
    JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
    repo.setDataSource(dataSource);
    return repo;
}
```

---

### 📊 Diagram 5: Bean Wiring for Persistent Remember-Me

```
┌───────────────┐
│ DataSource    │
└───────┬───────┘
        ▼
┌────────────────────────────┐
│ PersistentTokenRepository  │
│ (JdbcTokenRepositoryImpl)  │
└───────────┬────────────────┘
            ▼
┌────────────────────────────┐
│ RememberMeServices         │
│ (PersistentTokenBased...)  │
└───────────┬────────────────┘
            ▼
┌────────────────────────────┐
│ RememberMeAuthentication   │
│ Filter                     │
└────────────────────────────┘
```

---

### 5.2 Security Filter Chain Configuration

```java
.rememberMe(remember -> remember
    .tokenRepository(tokenRepository)
    .tokenValiditySeconds(1209600)
    .key("uniqueAndSecret")
)
```

---

### 📊 Diagram 6: Security Filter Chain with Remember-Me

```
HTTP Request
     │
     ▼
[ SecurityContextFilter ]
     │
     ▼
[ RememberMeAuthenticationFilter ]
     │
     ▼
[ AuthenticationManager ]
     │
     ▼
[ UserDetailsService ]
```

---

## 6. Frontend Requirements

Login form must include:

```html
<input type="checkbox" name="remember-me">
```

---

### 📊 Diagram 7: Login Request with Remember-Me

```
Browser Form
────────────
username=alice
password=******
remember-me=on
```

---

## 7. Core Interfaces

### RememberMeServices

```java
Authentication autoLogin(...)
void loginFail(...)
void loginSuccess(...)
```

---

### 📊 Diagram 8: RememberMeServices Method Triggers

```
Login Success ─────► loginSuccess()
Login Failure ─────► loginFail()
No Session   ──────► autoLogin()
```

---

## 8. Implementations Comparison

| Implementation                         | Storage     | Use Case   |
| -------------------------------------- | ----------- | ---------- |
| TokenBasedRememberMeServices           | Cookie      | Simple     |
| PersistentTokenBasedRememberMeServices | DB + Cookie | Production |

---

### 📊 Diagram 9: Implementation Selection

```
RememberMeServices
       │
       ├── TokenBased (cookie only)
       │
       └── PersistentTokenBased
           ├── JdbcTokenRepositoryImpl
           └── InMemoryTokenRepositoryImpl
```

---

## 9. Key Differences in Spring Security 7.0

* Lambda-based DSL
* No `WebSecurityConfigurerAdapter`
* Mandatory password encoding
* Token theft detection

---

### 📊 Diagram 10: Token Theft Detection

```
Cookie Series Found
        │
        ▼
Token Matches DB? ── NO ──► Invalidate Session
        │
       YES
        │
        ▼
Rotate Token + Authenticate
```

---

## 10. Summary

Persistent Remember-Me in Spring Security 7.0:
* ✔ Database-backed tokens
* ✔ Secure cookie contents
* ✔ Automatic revocation
* ✔ Token theft detection
* ✔ Production-ready

---
