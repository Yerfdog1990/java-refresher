
---

# Password Storage in Spring Security

---

## 1. Password Storage Fundamentals

### PasswordEncoder Interface

Spring Security’s `PasswordEncoder` interface is used to perform a **one-way transformation** of a password to let the password be stored securely. Given `PasswordEncoder` is a one-way transformation, it is **not useful** when the password transformation needs to be two-way (such as storing credentials used to authenticate to a database).

Typically, `PasswordEncoder` is used for storing a password that needs to be **compared to a user-provided password at the time of authentication**.

---

## 2. Password Storage History

### 2.1 Plaintext Passwords

Throughout the years, the standard mechanism for storing passwords has evolved. In the beginning, passwords were stored in **plaintext**. The passwords were assumed to be safe because the data store the passwords were saved in required credentials to access it.

However, malicious users were able to find ways to get large “data dumps” of usernames and passwords by using attacks such as **SQL Injection**. As more and more user credentials became public, security experts realized that we needed to do more to protect users' passwords.

---

### 2.2 One-Way Hashing (SHA-256)

Developers were then encouraged to store passwords after running them through a **one way hash**, such as SHA-256. When a user tried to authenticate, the hashed password would be compared to the hash of the password that they typed.

This meant that the system only needed to store the **one-way hash** of the password. If a breach occurred, only the one-way hashes of the passwords were exposed. Since the hashes were one-way and it was computationally difficult to guess the passwords given the hash, it would not be worth the effort to figure out each password in the system.

---

### 2.3 Rainbow Table Attacks

To defeat this new system, malicious users decided to create lookup tables known as **Rainbow Tables**. Rather than doing the work of guessing each password every time, they computed the password once and stored it in a lookup table.

---

### 2.4 Salting Passwords

To mitigate the effectiveness of Rainbow Tables, developers were encouraged to use **salted passwords**.

Instead of using just the password as input to the hash function, **random bytes (known as salt)** would be generated for every user’s password. The salt and the user’s password would be run through the hash function to produce a unique hash.

The salt would be stored alongside the user’s password in **clear text**. Then when a user tried to authenticate, the hashed password would be compared to the hash of the stored salt and the password that they typed.

The unique salt meant that Rainbow Tables were no longer effective because the hash was different for every salt and password combination.

---

### 2.5 Why Cryptographic Hashes Are No Longer Enough

In modern times, we realize that cryptographic hashes (like SHA-256) are no longer secure. The reason is that with modern hardware we can perform **billions of hash calculations a second**. This means that we can crack each password individually with ease.

---

### 2.6 Adaptive One-Way Functions (Modern Best Practice)

Developers are now encouraged to leverage **adaptive one-way functions** to store a password.

Validation of passwords with adaptive one-way functions are intentionally **resource-intensive** (they intentionally use a lot of CPU, memory, or other resources). An adaptive one-way function allows configuring a **“work factor”** that can grow as hardware gets better.

We recommend that the “work factor” be tuned to take **about one second** to verify a password on your system. This trade off is to make it difficult for attackers to crack the password, but not so costly that it puts excessive burden on your own system or irritates users.

Spring Security has attempted to provide a good starting point for the “work factor”, but we encourage users to customize the “work factor” for their own system, since the performance varies drastically from system to system.

Examples of adaptive one-way functions that should be used include:

* bcrypt
* PBKDF2
* scrypt
* argon2

---

### 2.7 Long-Term vs Short-Term Credentials

Because adaptive one-way functions are intentionally resource intensive, validating a username and password for every request can significantly degrade the performance of an application.

There is nothing Spring Security (or any other library) can do to speed up the validation of the password, since security is gained by making the validation resource intensive.

Users are encouraged to exchange the **long term credentials** (that is, username and password) for a **short term credential** (such as a session, an OAuth Token, and so on). The short term credential can be validated quickly without any loss in security.

---

## 3. DelegatingPasswordEncoder

### 3.1 Motivation

Prior to Spring Security 5.0, the default `PasswordEncoder` was `NoOpPasswordEncoder`, which required plain-text passwords.

Based on the Password History section, you might expect that the default `PasswordEncoder` would now be something like `BCryptPasswordEncoder`. However, this ignores three real world problems:

1. Many applications use old password encodings that cannot easily migrate.
2. The best practice for password storage will change again.
3. As a framework, Spring Security cannot make breaking changes frequently.

---

### 3.2 DelegatingPasswordEncoder Solution

Instead Spring Security introduces `DelegatingPasswordEncoder`, which solves all of the problems by:

* Ensuring that passwords are encoded by using the current password storage recommendations
* Allowing for validating passwords in modern and legacy formats
* Allowing for upgrading the encoding in the future

---

### 3.3 Creating a DelegatingPasswordEncoder

#### Default DelegatingPasswordEncoder

```java
PasswordEncoder passwordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder();
```

---

#### Custom DelegatingPasswordEncoder

```java
String idForEncode = "bcrypt";
Map<String, PasswordEncoder> encoders = new HashMap<>();
encoders.put(idForEncode, new BCryptPasswordEncoder());
encoders.put("noop", NoOpPasswordEncoder.getInstance());
encoders.put("pbkdf2", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_5());
encoders.put("pbkdf2@SpringSecurity_v5_8", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8());
encoders.put("scrypt", SCryptPasswordEncoder.defaultsForSpringSecurity_v4_1());
encoders.put("scrypt@SpringSecurity_v5_8", SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8());
encoders.put("argon2", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_2());
encoders.put("argon2@SpringSecurity_v5_8", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());
encoders.put("sha256", new StandardPasswordEncoder());

PasswordEncoder passwordEncoder =
        new DelegatingPasswordEncoder(idForEncode, encoders);
```

---

## 4. Password Storage Format

### DelegatingPasswordEncoder Storage Format

The general format for a password is:

```
{id}encodedPassword
```

* `id` is an identifier that is used to look up which `PasswordEncoder` should be used
* `encodedPassword` is the original encoded password
* The `id` must be at the beginning of the password, start with `{`, and end with `}`
* If the id cannot be found, the id is set to null

---

### Encoded Password Examples

```
{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG 
{noop}password 
{pbkdf2}5d923b44a6d129f3ddf3e3c8d29412723dcbde72445e8ef6bf3b508fbf17fa4ed4d6b99ca763d8dc 
{scrypt}$e0801$8bWJaSu2IKSn9Z9kM+TPXfOc/9bdYSrN1oD9qfVThWEwdRTnO7re7Ei+fUZRJ68k9lTyuTeUp4of4g24hHnazw==$OAOec05+bXxvuu/1qZ6NUR+xQYvYv7BeL1QxwRpY5Pc=  
{sha256}97cde38028ad898ebc02e690819fa220e88c62e0699403e94fff291cfffaf8410849f27605abcbc0
```

Each entry delegates to its corresponding `PasswordEncoder`.

---

### Security Note

Some users might be concerned that the storage format is provided for a potential hacker. This is not a concern because the storage of the password does not rely on the algorithm being a secret.

---

## 5. Password Encoding and Matching

### Encoding

The `idForEncode` determines which `PasswordEncoder` is used for encoding passwords. The encoded result is prefixed with `{id}`.

Example:

```
{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
```

---

### Matching

Matching is based upon the `{id}` and the mapping of the id to the `PasswordEncoder`.

By default, if the id is not mapped (including null), an `IllegalArgumentException` is thrown.

This behavior can be customized using:

```java
DelegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(...)
```

---

## 6. Getting Started Experience

### withDefaultPasswordEncoder

```java
UserDetails user = User.withDefaultPasswordEncoder()
        .username("user")
        .password("password")
        .roles("user")
        .build();
```

⚠️ This is **not secure for production**.

---

## 7. Encoding Passwords with Spring Boot CLI

```bash
spring encodepassword password
```

Output:

```
{bcrypt}$2a$10$X5wFBtLrL/kHcmrOGGTrGufsBX8CJ0WpQpF3pgeuxBB/H73BK1DW6
```

---

## 8. Troubleshooting

### Missing ID Error

```
java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"
```

Solutions include:

* Explicitly providing the correct `PasswordEncoder`
* Prefixing stored passwords with `{id}`
* Migrating legacy passwords

---

## 9. Built-in PasswordEncoders

Detailed coverage of:

* BCryptPasswordEncoder
* Argon2PasswordEncoder
* Pbkdf2PasswordEncoder
* SCryptPasswordEncoder

Each should be tuned to take about **1 second** to verify a password.

---

## 10. Password4j-Based Password Encoders (Spring Security 7.0)

Spring Security 7.0 introduces Password4j-based implementations:

* Argon2Password4jPasswordEncoder
* BcryptPassword4jPasswordEncoder
* ScryptPassword4jPasswordEncoder
* Pbkdf2Password4jPasswordEncoder
* BalloonHashingPassword4jPasswordEncoder

All implementations are **thread-safe** and support custom configurations.

---

## 11. Password Storage Configuration

Spring Security uses `DelegatingPasswordEncoder` by default.

Reverting to `NoOpPasswordEncoder` is **not secure** and should only be used for migration.

---

## 12. Change Password Configuration

Spring Security supports **`.well-known/change-password`** discovery for password managers.

```java
http.passwordManagement(Customizer.withDefaults());
```

Or with a custom endpoint:

```java
http.passwordManagement(management ->
        management.changePasswordPage("/update-password")
);
```

---

## 13. Compromised Password Checking

---

There are some scenarios where you need to check whether a password has been compromised, for example, if you are creating an application that deals with sensitive data, it is often needed that you perform some check on user’s passwords in order to assert its reliability. One of these checks can be if the password has been compromised, usually because it has been found in a data breach.

To facilitate that, Spring Security provides integration with the Have I Been Pwned API via the HaveIBeenPwnedRestApiPasswordChecker implementation of the CompromisedPasswordChecker interface.

You can either use the CompromisedPasswordChecker API by yourself or, if you are using the DaoAuthenticationProvider via Spring Security authentication mechanisms, you can provide a CompromisedPasswordChecker bean, and it will be automatically picked up by Spring Security configuration.

By doing that, when you try to authenticate via Form Login using a weak password, let’s say 123456, you will receive a 401 or be redirected to the /login?error page (depending on your user-agent). However, just a 401 or the redirect is not so useful in that case, it will cause some confusion because the user provided the right password and still was not allowed to log in. In such cases, you can handle the CompromisedPasswordException via the AuthenticationFailureHandler to perform your desired logic, like redirecting the user-agent to /reset-password, for example:

---

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	http
		.authorizeHttpRequests(authorize -> authorize
			.anyRequest().authenticated()
		)
		.formLogin((login) -> login
			.failureHandler(new CompromisedPasswordAuthenticationFailureHandler())
		);
	return http.build();
}

@Bean
public CompromisedPasswordChecker compromisedPasswordChecker() {
	return new HaveIBeenPwnedRestApiPasswordChecker();
}

static class CompromisedPasswordAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private final SimpleUrlAuthenticationFailureHandler defaultFailureHandler = new SimpleUrlAuthenticationFailureHandler(
			"/login?error");

	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		if (exception instanceof CompromisedPasswordException) {
			this.redirectStrategy.sendRedirect(request, response, "/reset-password");
			return;
		}
		this.defaultFailureHandler.onAuthenticationFailure(request, response, exception);
	}

}
```
---

## Hashing Algorithms: Defense Characteristics

| Algorithm | Type        | CPU Cost | Memory Cost | Resistant to GPU | Recommended |
| --------- | ----------- | -------- | ----------- | ---------------- |-------------|
| SHA-256   | Fast hash   | Low      | Low         | ❌ No            | ❌ No       |   
| BCrypt    | Adaptive    | Medium   | Low         | ⚠️ Partial       | ✅ Yes      | 
| PBKDF2    | Adaptive    | Medium   | Low         | ⚠️ Partial       | ✅ (FIPS)   |  
| SCrypt    | Memory-hard | Medium   | High        | ✅ Yes           | ✅ Yes      |   
| Argon2    | Memory-hard | High     | High        | ✅ Best          | ✅ Best     |   

---