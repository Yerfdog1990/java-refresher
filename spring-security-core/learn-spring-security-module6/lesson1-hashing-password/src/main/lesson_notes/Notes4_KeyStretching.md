
---

# Lesson: Key Stretching

## 1. Lesson Objectives

By the end of this lesson, learners should be able to:

* Explain **why hashing + salting is still not enough**
* Define **key stretching** and its security goal
* Understand **brute-force and dictionary attack economics**
* Describe how **bcrypt and PBKDF2 implement key stretching**
* Tune key-stretching parameters responsibly
* Recognize **performance and DoS trade-offs**
* Apply **best practices in Spring Security**

---

## 2. Recap: Where We Are So Far

So far, we’ve learned:

1. **Hashing** hides plaintext passwords
2. **Salting** prevents rainbow tables and mass cracking

However…

> Even salted passwords can still be cracked via **brute-force or dictionary attacks**.

---

## 3. The Remaining Threat: Brute-Force Attacks

### 3.1 What Salts Do *Not* Prevent

Salts protect against:

* Rainbow tables
* Lookup tables
* Shared-password detection

Salts **do NOT** prevent:

* Brute-force attacks
* Dictionary attacks

---

### 3.2 Why Brute Force Is Still Practical

Modern attackers can use:

* **GPUs** (billions of hashes per second)
* **Cloud GPUs** (AWS, Azure, GCP)
* **Parallel hardware**

#### Example:

```
Single hash (fast algorithm):
→ millions or billions of guesses per second
```

Even salted hashes fall quickly if hashing is **fast**.

---

## 4. Introducing Key Stretching

### 4.1 What Is Key Stretching?

**Key stretching** deliberately makes password hashing **slow**.

> The goal is to make **each password guess expensive**.

---

### 4.2 Core Idea

```
Fast hash  → bad for passwords
Slow hash  → good for passwords
```

Key stretching ensures:

* Legitimate users barely notice
* Attackers pay a massive computational cost

---

## 5. Conceptual Model of Key Stretching

### 5.1 Without Key Stretching

```
password + salt
     |
     v
HASH()
     |
     v
stored hash
```

⏱️ Time: microseconds

---

### 5.2 With Key Stretching

```
password + salt
     |
     v
HASH()
     |
     v
HASH()
     |
     v
HASH()
     |
     v
... repeated thousands of times ...
     |
     v
final hash
```

⏱️ Time: ~200–500 ms

---

### 5.3 Attacker vs Defender Cost

```
LEGITIMATE USER:
1 password attempt → 500 ms → OK

ATTACKER:
1 billion attempts × 500 ms → impossible
```

---

## 6. Key Stretching Goals

| Goal         | Explanation                             |
| ------------ | --------------------------------------- |
| Slow hashing | Makes brute-force attacks impractical   |
| Predictable  | Same password always verifies correctly |
| Tunable      | Adjustable cost over time               |
| Standardized | Uses trusted algorithms                 |

🎯 **Target time:** ~500 ms per hash

---

## 7. Standard Key Stretching Algorithms

❌ **Do NOT invent your own**

✅ Use proven standards:

| Algorithm | Key Stretching | Memory Hard |
| --------- | -------------- | ----------- |
| bcrypt    | ✅              | ❌           |
| PBKDF2    | ✅              | ❌           |
| scrypt    | ✅              | ✅           |
| Argon2    | ✅              | ✅ (winner)  |

---

## 8. How Key Stretching Is Controlled

### 8.1 Iteration Count / Strength Factor

Key stretching algorithms accept a **cost parameter**:

* Higher value → slower hash
* Lower value → faster hash

---

### 8.2 bcrypt Strength Example

```java
BCryptPasswordEncoder encoder =
        new BCryptPasswordEncoder(12);
```

Meaning:

```
2^12 = 4096 internal rounds
```

---

### 8.3 PBKDF2 Example

```java
Pbkdf2PasswordEncoder encoder =
    new Pbkdf2PasswordEncoder(
        "secret",
        185000,
        256
    );
```

Where:

* 185,000 iterations
* 256-bit hash

---

## 9. Visual Flow: Authentication with Key Stretching

```
[ Login Request ]
       |
       v
[ Extract Salt from Stored Hash ]
       |
       v
[ Run Slow Hash Function ]
       |
       v
[ Same Cost as Original Hashing ]
       |
       v
[ Compare Hashes ]
```

---

## 10. Why 500 ms Is a Sweet Spot

### Too Fast ❌

* Attacker can try millions/sec
* Brute force feasible

### Too Slow ❌

* User experience suffers
* Login feels broken
* System overload risk

### Just Right ✅

* Attacks uneconomical
* User doesn’t notice

---

## 11. Risks of Key Stretching

### 11.1 Increased Resource Usage

Authentication becomes:

* CPU-intensive
* Memory-intensive (depending on algorithm)

---

### 11.2 Denial of Service (DoS) Risk

Attackers may:

* Flood login endpoints
* Force expensive hash calculations

---

## 12. Mitigating Key Stretching Risks

### 12.1 Lower—but Non-Zero—Cost

Even a **low iteration count** is:

```
Much better than no key stretching
```

---

### 12.2 Rate Limiting

* Limit login attempts per IP
* Lock accounts temporarily

---

### 12.3 CAPTCHA on Login

Especially effective when:

* Users log in infrequently
* “Remember Me” is enabled

---

### 12.4 Stress Testing

Always test:

* Max authentication throughput
* CPU usage under load

---

## 13. Key Stretching in Spring Security

### 13.1 Recommended Encoder

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
}
```

✔ Salted
✔ Key-stretched
✔ Adaptive
✔ Secure defaults

---

### 13.2 Why BCrypt Is Ideal

| Feature              | BCrypt |
| -------------------- | ------ |
| Built-in salting     | ✅      |
| Adjustable cost      | ✅      |
| Slow by design       | ✅      |
| Secure defaults      | ✅      |
| Spring Boot standard | ✅      |

---

## 14. Upgrade Notes (Spring Boot ≥ 2)

* MD5 → ❌ removed
* SHA-based encoders → ❌ deprecated
* `StandardPasswordEncoder` → ❌ deprecated
* **BCryptPasswordEncoder** → ✅ preferred

Strength range:

```
4 → 31
```

Even **low values** provide strong protection.

---

## 15. Key Stretching + Salting Together

### Combined Defense Diagram

```
password
   |
   v
+--------+
|  SALT  |
+--------+
   |
   v
SLOW HASH FUNCTION
   |
   v
KEY-STRETCHED HASH
```

---

## 16. Key Takeaways

* Salts stop *precomputation*
* Key stretching stops *brute force*
* Slow hashing protects passwords
* Use **standard algorithms only**
* bcrypt provides everything out of the box
* Tune cost carefully, test performance

---

## 17. Final Mental Model

```
PASSWORD SECURITY =
  SALT
+ SLOW HASH
+ KEY STRETCHING
```

Or simply:

```
Use BCrypt.
Tune it.
Test it.
```

---

