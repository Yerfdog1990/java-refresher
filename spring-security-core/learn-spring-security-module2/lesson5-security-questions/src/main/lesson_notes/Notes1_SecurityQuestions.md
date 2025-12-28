
---

# Lesson Notes: Security Questions

---

## 1. Introduction to Security Questions

Security questions are a commonly used technique to **add an extra layer of protection** around sensitive operations in an application, such as **resetting a password**.

The core idea is simple:

* A user selects a predefined question
* The user provides an answer known only to them
* The system later verifies this answer before allowing sensitive actions

Security questions are an example of **Knowledge-Based Authentication (KBA)**.

---

## 2. Defining Security Questions in the System

### 2.1 Why Persist Security Questions?

There are two main ways to define security questions:

* Hard-code them in the application
* Persist them in the database

Persisting security questions is preferable because:

* They can be modified at runtime
* New questions can be added without changing code
* They are easier to manage and extend

---

### 2.2 Security Question Definition Entity

The **SecurityQuestionDefinition** entity represents the **question itself**, independent of any user.

```java
import javax.persistence.Id;
import javax.persistence.Entity;

@Entity
public class SecurityQuestionDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotEmpty
    private String text;
}
```

This entity contains:

* A unique identifier
* The text of the security question

---

### 2.3 Repository for Security Question Definitions

```java
public interface SecurityQuestionDefinitionRepository
        extends JpaRepository<SecurityQuestionDefinition, Long> {
}
```

This repository provides basic CRUD operations for managing security questions.

---

### 2.4 Populating Security Questions Using `data.sql`

Spring Boot allows initializing database data using a `data.sql` file.

```sql
insert into security_question_definition (id, text)
values (1, 'What is the last name of the teacher who gave you your first failing grade?');

insert into security_question_definition (id, text)
values (2, 'What is the first name of the person you first kissed?');

insert into security_question_definition (id, text)
values (3, 'What is the name of the place your wedding reception was held?');

insert into security_question_definition (id, text)
values (4, 'When you were young, what did you want to be when you grew up?');

insert into security_question_definition (id, text)
values (5, 'Where were you New Year''s 2000?');

insert into security_question_definition (id, text)
values (6, 'Who was your childhood hero?');
```

These predefined questions are loaded automatically when the application starts.

---

### (Diagram Space – Security Question Definition Model)

```
SecurityQuestionDefinition
--------------------------
id
text
```

---

## 3. The Security Question of the User

Once questions are defined, we need to associate **one question and answer with each user**.

---

### 3.1 User-Specific Security Question Entity

```java
import javax.persistence.Id;
import javax.persistence.Entity;

@Entity
public class SecurityQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id", unique = true)
    private User user;

    @OneToOne(targetEntity = SecurityQuestionDefinition.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "securityQuestionDefinition_id")
    private SecurityQuestionDefinition questionDefinition;

    private String answer;
}
```

This entity stores:

* The user
* The selected security question
* The user’s answer

---

### (Diagram Space – User Security Question Relationship)

```
User
  |
  | 1:1
  |
SecurityQuestion
  |
  | 1:1
  |
SecurityQuestionDefinition
```

---

### 3.2 Security Question Repository

```java
public interface SecurityQuestionRepository
        extends JpaRepository<SecurityQuestion, Long> {
}
```

---

### 3.3 Custom Query for Answer Verification

```java
SecurityQuestion findByQuestionDefinitionIdAndUserIdAndAnswer(
    Long questionDefinitionId,
    Long userId,
    String answer
);
```

This method is later used to verify:

* The selected question
* The user
* The provided answer

---

## 4. Front-End Integration (Registration Page)

During registration, users must:

* Select a security question
* Provide an answer

---

### 4.1 Registration Page HTML

```html
<div class="form-group">
    <label class="control-label col-xs-2" for="question">Security Question:</label>
    <div class="col-xs-10">
        <select id="question" name="questionId">
            <option th:each="question : ${questions}"
                    th:value="${question.id}"
                    th:text="${question.text}">
                Question
            </option>
        </select>
    </div>
</div>

<div class="form-group">
    <label class="control-label col-xs-2" for="answer">Answer</label>
    <div class="col-xs-10">
        <input id="answer" type="text" name="answer"/>
    </div>
</div>
```

This displays:

* A dropdown of available questions
* A text field for the answer

---

## 5. Registration Logic Changes

### 5.1 Providing Security Questions to the View

```java
@RequestMapping(value = "signup")
public ModelAndView registrationForm() {

    Map<String, Object> model = new HashMap<>();
    model.put("user", new User());
    model.put("questions",
              securityQuestionDefinitionRepository.findAll());

    return new ModelAndView("registrationPage", model);
}
```

Here, the controller:

* Adds a user object
* Adds all available security questions to the model

---

### 5.2 Persisting the User’s Security Question

After registering the user, we also save the selected security question:

```java
SecurityQuestionDefinition questionDefinition =
        securityQuestionsDefRepo.findOne(questionId);

securityQuestionRepo.save(
        new SecurityQuestion(user, questionDefinition, answer)
);
```

Now, each registered user has:

* A selected security question
* A stored answer

---

## 6. Securing the Reset Password Operation

Resetting a password is a **highly sensitive operation**, so we protect it using security questions.

---

### 6.1 Reset Password Page (Front End)

```html
<div class="form-group">
    <label class="control-label col-xs-2" for="question">Security Question:</label>
    <div class="col-xs-10">
        <select id="question" name="questionId">
            <option th:each="question : ${questions}"
                    th:value="${question.id}"
                    th:text="${question.text}">
                Question
            </option>
        </select>
    </div>
</div>

<div class="form-group">
    <label class="control-label col-xs-2" for="answer">Answer</label>
    <div class="col-xs-10">
        <input id="answer" type="text" name="answer"/>
    </div>
</div>
```

---

### 6.2 Server-Side Answer Verification

```java
if (securityQuestionRepo
        .findByUserIdAndAnswer(user.getId(), answer) == null) {

    final Map<String, Object> model = new HashMap<>();
    model.put("errorMessage",
              "Answer to security question is incorrect");
    model.put("questions",
              securityQuestionsDefRepo.findAll());

    return new ModelAndView("resetPassword", model);
}
```

If:

* No matching record is found
* The answer is incorrect

Then:

* The password is **not changed**
* An error message is returned

---

### (Diagram Space – Reset Password Security Flow)

```
User Requests Password Reset
            ↓
Answer Security Question
            ↓
Server Verifies Answer
      ↓            ↓
 Incorrect        Correct
   Stop         Reset Password
```

---

## 7. Important Security Considerations

Security questions are a form of **Knowledge-Based Authentication (KBA)**.

### Limitations of Security Questions:

* Answers can be guessed
* Information may be found on social media
* Low assurance compared to modern methods

Because of these weaknesses:

* Security questions are **discouraged as a sole verification method**
* They should **not be relied upon alone**

---

## 8. Recommended Alternatives

Instead of using security questions alone, it is advised to use:

* Two-Factor Authentication (2FA)
* Multi-Factor Authentication (MFA)
* Or combine MFA with KBA for additional security

Security questions can still be useful:

* As an extra layer
* When used carefully
* With full awareness of their limitations

---

## 9. Upgrade Notes: Password Encoding in `data.sql`

If users are inserted using `data.sql`, passwords **must be encoded**.

```sql
insert into user (id, email, password, ...)
values (1, 'test@email.com', NNN, ...);
```

Here:

* `NNN` is the encoded password

---

### 9.1 Password Encoder Configuration

```java
@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth)
        throws Exception {

    auth.userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder());
}
```

---

### 9.2 BCrypt Password Encoder Bean

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

The same encoder must be used:

* When saving passwords
* When authenticating users

---


