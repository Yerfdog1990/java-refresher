
---

# Grouping Jakarta (Javax) Validation Constraints

## 1. Introduction

In the **Java Bean Validation Basics** lesson, we explored how to validate Java beans using built-in constraints such as `@NotNull`, `@NotBlank`, `@Size`, and `@Future`. These constraints work well for simple scenarios where **all validations apply at the same time**.

However, real-world applications are rarely that simple.

Very often, **different operations require different validations** on the *same object*. For example:

* Creating a resource vs updating it
* Multi-step forms
* Partial updates (PATCH-like behavior)
* Nested resource updates

To handle these scenarios cleanly and declaratively, **Jakarta Bean Validation provides Validation Groups**.

This lesson explains:

* What validation groups are
* Why they are needed
* How to define and use them
* How Spring integrates with validation groups
* How to apply them in real REST API scenarios

---

## 2. Use Case: Why Do We Need Validation Groups?

Consider a **two-step user registration process**.

### Step 1: Basic Information

The user provides:

* First name
* Last name
* Email
* Phone number
* Captcha

At this point:

* Address-related fields should **not** be validated
* Only basic fields matter

### Step 2: Advanced Information

The user now provides:

* Street
* House number
* ZIP code
* City
* Country
* Captcha (again)

At this point:

* Basic fields are already validated
* Only advanced fields (and captcha) should be checked

👉 **Problem:**
If we validate the entire bean at once, constraints for step 2 will fail during step 1.

👉 **Solution:**
Use **validation groups** to activate only the constraints relevant to the current step.

---

## 3. Grouping Validation Constraints

Every Jakarta validation constraint has an attribute called `groups`.

```java
Class<?>[] groups() default {};
```

This allows us to:

* Assign constraints to one or more **validation groups**
* Choose which group(s) to validate at runtime

If no group is specified, the constraint belongs to the **Default** group.

---

## 3.1 Declaring Constraint Groups

Validation groups are defined as **marker interfaces** (interfaces with no methods).

### BasicInfo Group

```java
public interface BasicInfo {
}
```

### AdvanceInfo Group

```java
public interface AdvanceInfo {
}
```

These interfaces serve **only as identifiers**.

---

## 3.2 Using Constraint Groups in a Bean

Now we assign constraints to groups in the `RegistrationForm` bean:

```java
public class RegistrationForm {

    @NotBlank(groups = BasicInfo.class)
    private String firstName;

    @NotBlank(groups = BasicInfo.class)
    private String lastName;

    @Email(groups = BasicInfo.class)
    private String email;

    @NotBlank(groups = BasicInfo.class)
    private String phone;

    @NotBlank(groups = { BasicInfo.class, AdvanceInfo.class })
    private String captcha;

    @NotBlank(groups = AdvanceInfo.class)
    private String street;

    @NotBlank(groups = AdvanceInfo.class)
    private String houseNumber;

    @NotBlank(groups = AdvanceInfo.class)
    private String zipCode;

    @NotBlank(groups = AdvanceInfo.class)
    private String city;

    @NotBlank(groups = AdvanceInfo.class)
    private String contry;
}
```

### Key Observations

* Fields are selectively validated depending on the group
* `captcha` belongs to **both groups**
* Constraints without `groups` belong to `Default`

---

## 3.3 Testing Constraints with a Single Group

### Validating Only Basic Information

```java
Set<ConstraintViolation<RegistrationForm>> violations =
    validator.validate(form, BasicInfo.class);
```

Only constraints belonging to `BasicInfo` are checked.

#### Example Test Case

```java
@Test
public void whenBasicInfoIsNotComplete_thenShouldGiveConstraintViolationsOnlyForBasicInfo() {
    RegistrationForm form = buildRegistrationFormWithBasicInfo();
    form.setFirstName("");

    Set<ConstraintViolation<RegistrationForm>> violations =
        validator.validate(form, BasicInfo.class);

    assertThat(violations.size()).isEqualTo(1);
    violations.forEach(action -> {
        assertThat(action.getPropertyPath().toString()).isEqualTo("firstName");
    });
}
```

👉 **Important:**

Constraints from `AdvanceInfo` are completely ignored.

---

### Validating Only Advanced Information

```java
Set<ConstraintViolation<RegistrationForm>> violations =
    validator.validate(form, AdvanceInfo.class);
```

Only address-related constraints are evaluated.

---

## 3.4 Constraints Belonging to Multiple Groups

Some fields are shared across steps.

Example: `captcha`

```java
@NotBlank(groups = { BasicInfo.class, AdvanceInfo.class })
private String captcha;
```

### Result:

* Validated in **step 1**
* Validated again in **step 2**

This allows **reuse without duplication**.

---

## 4. Ordering Validation with `@GroupSequence`

By default:

* Validation groups are evaluated **independently**
* All constraints from all specified groups are checked

Sometimes, this is not desirable.

### Example Requirement

* Validate `BasicInfo` **first**
* Only if it passes, validate `AdvanceInfo`

---

## 4.1 GroupSequence on the Entity

```java
@GroupSequence({ BasicInfo.class, AdvanceInfo.class })
public class RegistrationForm {
    @NotBlank(groups = BasicInfo.class)
    private String firstName;

    @NotBlank(groups = AdvanceInfo.class)
    private String street;
}
```

### Behavior

* If `BasicInfo` fails → `AdvanceInfo` is skipped
* Fail-fast validation

---

## 4.2 GroupSequence on an Interface (Reusable)

```java
@GroupSequence({ BasicInfo.class, AdvanceInfo.class })
public interface CompleteInfo {
}
```

This approach is:

* Cleaner
* Reusable across multiple entities

---

## 4.3 Testing GroupSequence

```java
Set<ConstraintViolation<RegistrationForm>> violations =
    validator.validate(form, CompleteInfo.class);
```

### Outcome

* If `BasicInfo` is invalid → only its errors appear
* `AdvanceInfo` is validated **only if** `BasicInfo` succeeds

---

## 5. Applying Validation Groups in a REST API (Real Project Context)

### Default Validation in DTOs

Example from `TaskDto`:

```java
@Future(message = "dueDate must be in the future")
LocalDate dueDate;
```

This works for **creation**, but causes issues for **updates**.

---

## 5.1 Problem: Updating a Past-Due Task

Business case:

* Task is already past due
* User wants to update name/description
* Validation fails because `@Future` is always enforced

👉 **This is where validation groups shine.**

---

## 5.2 Defining an Update Validation Group

```java
public interface TaskUpdateValidationData {
}
```

---

## 5.3 Assigning Groups in TaskDto

```java
@NotBlank(groups = { TaskUpdateValidationData.class, Default.class })
String name;

@Future
LocalDate dueDate;
```

### Key Result

* `dueDate` validated **only on creation**
* Ignored during update

---

## 5.4 Triggering Groups in Spring Controllers

### Creation (Default group)

```java
@PostMapping
public TaskDto create(@RequestBody @Valid TaskDto task) {
}
```

### Update (Custom group)

```java
@PutMapping("/{id}")
public TaskDto update(
  @PathVariable Long id,
  @RequestBody @Validated(TaskUpdateValidationData.class) TaskDto task) {
}
```

👉 `@Validated` (Spring) is required for group-based validation

👉 `@Valid` triggers **only Default group**

---

## 6. Group Validation for Nested Objects

### Updating Task Status

```java
@NotNull(groups = { TaskUpdateStatusValidationData.class })
TaskStatus status;
```

Controller:

```java
@PutMapping("/{id}/status")
public TaskDto updateStatus(
  @RequestBody @Validated(TaskUpdateStatusValidationData.class) TaskDto task) {
}
```

---

## 6.1 Nested Validation with @Valid

For assignee updates:

```java
@Valid
WorkerDto assignee;
```

Inside `WorkerDto`:

```java
@NotNull(groups = { TaskUpdateAssigneeValidationData.class })
Long id;
```

---

## 6.2 Controlling Nested Validation with @ConvertGroup

When creating a task:

* Assignee should be ignored
* No validation errors should occur

```java
@Valid
@ConvertGroup(from = Default.class, to = WorkerOnTaskCreateValidationData.class)
WorkerDto assignee;
```

This **prevents unintended validation**.

---

## 7. Applying the Pattern Across All DTOs

### WorkerDto

```java
public interface WorkerUpdateValidationData {
}
```

No constraints → no validation during update.

---

### CampaignDto

```java
@NotBlank(groups = { CampaignUpdateValidationData.class, Default.class })
String name;
```

Controller:

```java
@PutMapping("/{id}")
public CampaignDto update(
  @RequestBody @Validated(CampaignUpdateValidationData.class) CampaignDto campaign) {
}
```

---

## 8. Conclusion

### What We Learned

* Validation groups allow **context-specific validation**
* The same DTO can support:

    * Create
    * Update
    * Partial updates
    * Nested updates
* Spring integrates seamlessly using `@Validated`
* GroupSequence enables **ordered validation**
* `@ConvertGroup` gives fine-grained control over nested validation

### Why This Matters

Validation groups:

* Prevent over-validation
* Improve API usability
* Reflect real business rules
* Scale cleanly in complex systems

👉 **This is professional-grade validation design**, exactly what’s expected in real-world RESTful applications.

---

# Mini-Project Lab: CRUD REST API with Validation Groups

## Project Title

**Task Management API – Validation Groups in Action**

---

## 1. Learning Objectives

By the end of this lab, learners will be able to:

* Build a CRUD REST API using Spring Boot
* Apply **Jakarta Bean Validation** to DTOs
* Use **validation groups** for different CRUD operations
* Understand the difference between `@Valid` and `@Validated`
* Handle real-world validation scenarios such as:

    * Different rules for CREATE vs UPDATE
    * Partial updates
    * Nested object validation

---

## 2. Project Scenario

We are building a **Task Management API**.

A `Task` represents a piece of work in a campaign/project.

### Business Rules

| Operation     | Validation Rules                                       |
| ------------- | ------------------------------------------------------ |
| CREATE Task   | name, description, dueDate, campaignId required        |
| UPDATE Task   | name & description required, **dueDate NOT validated** |
| UPDATE Status | only status must be provided                           |
| DELETE Task   | no validation required                                 |
| READ Tasks    | no validation required                                 |

This makes it a **perfect use case for validation groups**.

---

## 3. Domain Model Overview

### Entity (simplified)

```java
public class Task {
    private Long id;
    private String name;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
    private Long campaignId;
}
```

---

## 4. Validation Groups Definition

Create marker interfaces to represent validation contexts.

```java
public interface OnCreate {
}

public interface OnUpdate {
}

public interface OnStatusUpdate {
}
```

---

## 5. Task DTO with Validation Groups

This is the **core of the lab**.

```java
import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;

public record TaskDto(

    Long id,

    @NotBlank(
        groups = { OnCreate.class, OnUpdate.class },
        message = "Task name cannot be blank"
    )
    String name,

    @Size(
        min = 10,
        max = 100,
        groups = { OnCreate.class, OnUpdate.class },
        message = "Description must be between 10 and 100 characters"
    )
    String description,

    @Future(
        groups = { OnCreate.class },
        message = "Due date must be in the future"
    )
    LocalDate dueDate,

    @NotNull(
        groups = { OnStatusUpdate.class },
        message = "Status cannot be null"
    )
    TaskStatus status,

    @NotNull(
        groups = { OnCreate.class },
        message = "Campaign ID is required"
    )
    Long campaignId
) {}
```

---

## 6. Why This Design Works

| Field       | Create  | Update   |  Status Update  |
| ----------- | ------  |--------- |---------------- |
| name        | ✅      | ✅       | ❌              |
| description | ✅      | ✅       | ❌              |
| dueDate     | ✅      | ❌       | ❌              |
| status      | ❌      | ❌       | ✅              |
| campaignId  | ✅      | ❌       | ❌              |

✔ One DTO

✔ Multiple validation behaviors

✔ No duplication

✔ Clean REST design

---

## 7. Task Controller (CRUD Operations)

### 7.1 Create Task (POST)

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public TaskDto createTask(
    @RequestBody @Validated(OnCreate.class) TaskDto task) {
    return taskService.create(task);
}
```

📌 Uses `OnCreate` validation group

📌 Enforces strict validation

---

### 7.2 Read All Tasks (GET)

```java
@GetMapping
public List<TaskDto> getAllTasks() {
    return taskService.findAll();
}
```

📌 No validation needed

---

### 7.3 Read One Task (GET by ID)

```java
@GetMapping("/{id}")
public TaskDto getTask(@PathVariable Long id) {
    return taskService.findById(id);
}
```

---

### 7.4 Update Task (PUT)

```java
@PutMapping("/{id}")
public TaskDto updateTask(
    @PathVariable Long id,
    @RequestBody @Validated(OnUpdate.class) TaskDto task) {
    return taskService.update(id, task);
}
```

📌 `dueDate` is NOT validated

📌 Allows updating old tasks that are already overdue

---

### 7.5 Update Task Status (PUT – Partial Update)

```java
@PutMapping("/{id}/status")
public TaskDto updateStatus(
    @PathVariable Long id,
    @RequestBody @Validated(OnStatusUpdate.class) TaskDto task) {
    return taskService.updateStatus(id, task.status());
}
```

📌 Only `status` is validated

📌 Clean separation of concerns

---

### 7.6 Delete Task (DELETE)

```java
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void deleteTask(@PathVariable Long id) {
    taskService.delete(id);
}
```

📌 No validation required

---

## 8. Service Layer (Simplified)

```java
@Service
public class TaskService {

    private final Map<Long, TaskDto> db = new HashMap<>();
    private Long idCounter = 1L;

    public TaskDto create(TaskDto dto) {
        TaskDto saved = new TaskDto(
            idCounter++, 
            dto.name(),
            dto.description(),
            dto.dueDate(),
            TaskStatus.OPEN,
            dto.campaignId()
        );
        db.put(saved.id(), saved);
        return saved;
    }

    public List<TaskDto> findAll() {
        return new ArrayList<>(db.values());
    }

    public TaskDto findById(Long id) {
        return db.get(id);
    }

    public TaskDto update(Long id, TaskDto dto) {
        TaskDto existing = db.get(id);
        TaskDto updated = new TaskDto(
            id,
            dto.name(),
            dto.description(),
            existing.dueDate(), // unchanged
            existing.status(),
            existing.campaignId()
        );
        db.put(id, updated);
        return updated;
    }

    public TaskDto updateStatus(Long id, TaskStatus status) {
        TaskDto existing = db.get(id);
        TaskDto updated = new TaskDto(
            id,
            existing.name(),
            existing.description(),
            existing.dueDate(),
            status,
            existing.campaignId()
        );
        db.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        db.remove(id);
    }
}
```

---

## 9. Testing Scenarios (Postman / REST Client)

### ✅ Valid Create Request

* name ✔
* description ✔
* future dueDate ✔

### ❌ Invalid Create Request

* missing campaignId → **400 Bad Request**

---

### ✅ Update Past-Due Task

* dueDate in past ✔ (not validated)

---

### ❌ Invalid Status Update

* missing status → **400 Bad Request**

---

## 10. Student Exercises

1. Add a **PATCH** endpoint using a new validation group
2. Add a nested `UserDto assignee` with group-based validation
3. Implement `@GroupSequence` for ordered validation
4. Add global exception handling for validation errors
5. Convert this into a **real JPA-backed project**

---

## 11. Key Takeaways

* Validation groups allow **one DTO to serve many operations**
* `@Validated` is essential for group-based validation in Spring
* Groups map perfectly to REST semantics
* This approach scales cleanly in enterprise systems

---

