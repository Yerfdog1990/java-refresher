
---

# Lesson: Why Hashing Isn’t Enough – Using Salts

## 1. Lesson Objectives

By the end of this lesson, students should be able to:

* Explain **why hashing alone is insufficient** for password security
* Understand **what a salt is and why it matters**
* Identify **common salt implementation mistakes**
* Describe how **BCrypt and modern Spring Security encoders handle salts**
* Trace the **authentication flow with salted hashes**
* Apply **best practices** when storing and verifying passwords

---

## 2. Why Hashing Alone Is Not Enough

Hashing converts a password into a fixed-length value:

```
password123 → hash(password123)
```

This is better than plaintext, but **still vulnerable**.

### 2.1 Core Problems with Hashing Only

#### Problem 1: Identical Passwords → Identical Hashes

```
User A: password123 → 482c811da5d5b4bc6d497ffa98491e38
User B: password123 → 482c811da5d5b4bc6d497ffa98491e38
```

👉 An attacker instantly knows users share the same password.

---

#### Problem 2: Rainbow Table Attacks

A **rainbow table** is a precomputed database:

```
password → hash(password)
```

If your database leaks, attackers can:

1. Look up hashes
2. Instantly recover weak passwords

No brute force needed.

---

## 3. Introducing Salts

### 3.1 What Is a Salt?

A **salt** is a **random, unique string** added to a password **before hashing**.

```
hash(password + salt)
```

### 3.2 Why Salts Work

Salts add:

* **Entropy**
* **Uniqueness**
* **Resistance to precomputation attacks**

---

## 4. How Salting Changes Everything

### 4.1 Without a Salt (Vulnerable)

```
password123 → hashA
password123 → hashA   ❌ same result
```

---

### 4.2 With a Salt (Secure)

```
User A:
password123 + salt1 → hash1

User B:
password123 + salt2 → hash2
```

Even though the passwords are identical:

```
hash1 ≠ hash2
```

---

### 4.3 Visual Comparison Diagram

```
WITHOUT SALT                    WITH SALT
-------------                   ------------------
password123                     password123
     |                               |
     v                               v
  HASH()                       +------------+
     |                         |   SALT     |
     v                         +------------+
  same hash                          |
                                   v
                              HASH(password + salt)
                                   |
                              unique hash
```

---

## 5. Where Is the Salt Stored?

**Important concept:**
👉 **Salts do NOT need to be secret**

They are stored **alongside the hash**.

---

## 6. BCrypt Hash Structure (Critical Exam Topic)

Example BCrypt hash:

```
$2a$10$ZLhnHxdpHETcxmtEStgpI./Ri1mksgJ9iDP36FmfMdYyVg9g0b2dq
```

### Breakdown:

```
$2a$10$ZLhnHxdpHETcxmtEStgpI.$Ri1mksgJ9iDP36FmfMdYyVg9g0b2dq
 |   |          |                     |
 |   |          |                     └── Actual hash
 |   |          └── 22-char random salt
 |   └── Cost factor (2^10 rounds)
 └── BCrypt version
```

---

## 7. Automatically Handled by BCrypt

Modern libraries like **BCrypt** do everything for you.

### 7.1 Hashing a Password

```java
String hashed = BCrypt.hashpw("myPassword123", BCrypt.gensalt());
```

Behind the scenes:

1. Generates a cryptographically secure random salt
2. Combines password + salt
3. Applies slow hashing
4. Stores everything in one string

---

### 7.2 Verifying a Password

```java
boolean matches = BCrypt.checkpw("myPassword123", hashed);
```

Verification flow:

```
Input Password
      |
      v
Extract salt from stored hash
      |
      v
Re-hash input password + extracted salt
      |
      v
Compare hashes → true / false
```

---

## 8. Authentication Flow with Salts (Spring Security)

```
[ Login Form ]
      |
      v
[ Username + Password ]
      |
      v
[ Load User from DB ]
      |
      v
[ Extract salt from stored BCrypt hash ]
      |
      v
[ Hash input password + salt ]
      |
      v
[ Compare hashes ]
      |
      v
[ Authentication Success / Failure ]
```

---

## 9. Common Salt Mistakes (Very Important)

### ❌ Mistake 1: System-Wide Salt

```
hash(password + GLOBAL_SALT)
```

Problems:

* Identical passwords still produce identical hashes
* Rainbow tables still feasible

---

### ❌ Mistake 2: Using Username as Salt

```
hash(password + username)
```

Why it fails:

* Usernames are predictable
* Low entropy
* Easy to precompute tables

---

### ❌ Mistake 3: Reusing Salts After Password Change

Salts must be:

* **Unique per credential**
* **Regenerated when password changes**

---

### ❌ Mistake 4: Non-Secure Random Generators

Using:

```java
new Random()
```

Instead of:

```java
SecureRandom
```

👉 Predictable salts = broken security

---

### ❌ Mistake 5: Rolling Your Own Crypto

* Chaining hash functions
* Custom algorithms
* “Security through obscurity”

❌ All proven unsafe

---

## 10. Correct Salt Requirements (Summary)

A proper salt must be:

| Requirement  | Explanation              |
| ------------ | ------------------------ |
| Unique       | Per credential           |
| Random       | High entropy             |
| Fixed length | Predictable storage      |
| Secure RNG   | Cryptographically strong |
| Public       | Stored with hash         |

---

## 11. Spring Security: Old vs Modern Approach

### 11.1 Old (Deprecated)

* `Md5PasswordEncoder`
* `StandardPasswordEncoder`
* Required `SaltSource`
* Error-prone

---

### 11.2 Modern (Recommended)

* `BCryptPasswordEncoder`
* Automatic salt generation
* Automatic storage
* Automatic verification

---

## 12. Spring Boot Recommended Configuration

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Used in:

* User registration
* Password change
* Authentication provider

---

## 13. Why BCrypt Is Preferred

| Feature             | BCrypt |
| ------------------- | ------ |
| Built-in salt       | ✅      |
| Slow by design      | ✅      |
| Adaptive cost       | ✅      |
| Secure defaults     | ✅      |
| Spring Boot default | ✅      |

---

## 14. Key Takeaways

* **Hashing alone is not enough**
* Salts prevent rainbow table and mass cracking attacks
* Salts must be **random, unique, per credential**
* Salts do **not need to be secret**
* BCrypt handles salting correctly and safely
* **Never design your own password scheme**

---

## 15. Final Mental Model

```
PASSWORD SECURITY = HASHING + SALT + SLOW ALGORITHM
```

Or simply:

```
Use BCrypt. Don’t be clever.
```

---

