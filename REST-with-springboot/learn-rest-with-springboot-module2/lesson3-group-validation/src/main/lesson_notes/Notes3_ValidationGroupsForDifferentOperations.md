
---

# Validation Groups for Different API Operations

## 1. Introduction

In real-world REST APIs, **not all operations require the same validation rules**.

While basic input validation ensures data correctness, **different API operations (Create, Update, Partial Update)** often have **different business rules**. Applying the same validation constraints everywhere can make an API **too strict**, blocking valid use cases.

To solve this, **Jakarta Bean Validation provides *validation groups***, and **Spring enables them through the `@Validated` annotation**.

In this lesson, we’ll explore:

* Why validation groups are needed
* How they solve real API problems
* How to apply them to **CRUD operations**
* How to handle **nested DTO validation**
* How to design **flexible yet safe APIs**

---

## 2. Our Project’s Current Input Validations

Let’s start by reviewing the current validation setup in our project.

### 2.1 TaskDto – Baseline Validation

```java
public record TaskDto(
    Long id,

    String uuid,

    @NotBlank(message = "name can't be blank")
    String name,

    @Size(min = 10, max = 50,
      message = "description must be between 10 and 50 characters long")
    String description,

    @Future(message = "dueDate must be in the future")
    LocalDate dueDate,

    TaskStatus status,

    @NotNull(message = "campaignId can't be null")
    Long campaignId,

    WorkerDto assignee,

    @Min(value = 1, message = "estimatedHours can't be less than 1")
    @Max(value = 40, message = "estimatedHours can't exceed 40")
    Integer estimatedHours
) {
    // …
}
```

These constraints ensure that:

* Required fields are present
* Text fields meet length requirements
* Dates are logically valid
* Numeric values fall within acceptable ranges

---

### 2.2 How Validation Is Triggered Today

In the `TaskController`, validation is triggered using `@Valid`:

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public TaskDto create(@RequestBody @Valid TaskDto newTask) {
    // …
}

@PutMapping("/{id}")
public TaskDto update(
  @PathVariable Long id,
  @RequestBody @Valid TaskDto updatedTask) {
    // …
}
```

📌 **Important observation**
`@Valid`:

* Always triggers **all constraints**
* Has **no awareness of the operation type**
* Applies the same rules for *Create* and *Update*

---

## 3. The Problem: One Size Does Not Fit All

### 3.1 The Past-Due Task Scenario

Imagine this situation:

* A Task was created with a valid `dueDate`
* Time passes, and the task becomes **past due**
* We now want to update only:

    * `name`
    * `description`

🚫 **Problem:**
The update fails because of this constraint:

```java
@Future(message = "dueDate must be in the future")
LocalDate dueDate;
```

Even though:

* The past due date is acceptable for an **update**
* We are not modifying the `dueDate`

---

### 3.2 Key Insight

> **Different REST operations require different validation rules.**

This is exactly what **validation groups** are designed for.

---

## 4. Validation Groups and the `@Validated` Annotation

### 4.1 What Are Validation Groups?

Validation groups allow us to:

* Categorize constraints
* Activate only a **subset of validations**
* Tailor validation logic per API operation

Instead of “validate everything”, we say:

> *Validate only what matters for this operation.*

---

### 4.2 Defining a Validation Group

Validation groups are defined as **marker interfaces**:

```java
public interface TaskUpdateValidationData {
}
```

They contain no methods and exist purely to label constraints.

---

### 4.3 Applying Groups in TaskDto

```java
public record TaskDto(
    Long id,

    String uuid,

    @NotBlank(groups = { TaskUpdateValidationData.class, Default.class },
      message = "name can't be blank")
    String name,

    @Size(groups = { TaskUpdateValidationData.class, Default.class },
      min = 10, max = 50,
      message = "description must be between 10 and 50 characters long")
    String description,

    @Future
    LocalDate dueDate,

    @NotNull(groups = { TaskUpdateValidationData.class },
      message = "status can't be null")
    TaskStatus status,

    @NotNull(groups = { TaskUpdateValidationData.class, Default.class },
      message = "campaignId can't be null")
    Long campaignId,

    WorkerDto assignee,

    @Min(groups = { TaskUpdateValidationData.class, Default.class }, value = 1)
    @Max(groups = { TaskUpdateValidationData.class, Default.class }, value = 40)
    Integer estimatedHours
) {
    public interface TaskUpdateValidationData {
    }
}
```

---

### 4.4 What’s Happening Here?

✔ `Default` group → used by `@Valid` (creation)

✔ `TaskUpdateValidationData` → used only for updates

Key decisions:

* `dueDate` has **no group**, so it belongs only to `Default`
* `status` is required **only on update**
* Common fields are validated for both operations

---

## 5. Activating Groups in the Controller

To trigger group validation, we must use **Spring’s `@Validated` annotation**:

```java
@PutMapping("/{id}")
public TaskDto update(
  @PathVariable Long id,
  @RequestBody @Validated(TaskUpdateValidationData.class)
  TaskDto updatedTask) {
    // …
}
```

📌 **Why not `@Valid`?**
Because `@Valid`:

* Cannot accept validation groups
* Always validates the `Default` group only

---

## 6. Group Validation for Specialized Update Endpoints

Our API also exposes **partial update endpoints**:

```java
@PutMapping("/{id}/status")
public TaskDto updateStatus(
  @PathVariable Long id,
  @RequestBody TaskDto taskWithStatus) {
    // …
}

@PutMapping("/{id}/assignee")
public TaskDto updateAssignee(
  @PathVariable Long id,
  @RequestBody TaskDto taskWithAssignee) {
    // …
}
```

Each of these requires **very specific validation logic**.

---

## 7. Validation Group for Updating Task Status

### 7.1 Define a Group

```java
public interface TaskUpdateStatusValidationData {
}
```

### 7.2 Apply It

```java
@NotNull(
  groups = { TaskUpdateStatusValidationData.class, TaskUpdateValidationData.class },
  message = "status can't be null"
)
TaskStatus status;
```

### 7.3 Activate It

```java
@PutMapping("/{id}/status")
public TaskDto updateStatus(
  @PathVariable Long id,
  @RequestBody @Validated(TaskUpdateStatusValidationData.class)
  TaskDto taskWithStatus) {
    // …
}
```

✔ Only the `status` field is validated

✔ Other fields are ignored

---

## 8. Group Validation for Nested Objects (Assignee)

### 8.1 The Complexity

Valid requests include:

* Assigning a worker
* Reassigning a worker
* Unassigning (assignee = null)

---

### 8.2 Enabling Nested Validation

```java
@Valid
WorkerDto assignee;
```

This allows cascading validation into `WorkerDto`.

---

### 8.3 Worker Validation Group

```java
@NotNull(
  groups = { TaskUpdateValidationData.class, TaskUpdateAssigneeValidationData.class },
  message = "Worker id can't be null"
)
Long id;
```

### 8.4 Activate in Controller

```java
@PutMapping("/{id}/assignee")
public TaskDto updateAssignee(
  @PathVariable Long id,
  @RequestBody @Validated(TaskUpdateAssigneeValidationData.class)
  TaskDto taskWithAssignee) {
    // …
}
```

---

## 9. Group Conversion for Creation Scenarios

When creating a Task:

* Assignee is ignored
* Validation should **not cascade**

### 9.1 Convert Validation Group

```java
@Valid
@ConvertGroup(
  from = Default.class,
  to = WorkerOnTaskCreateValidationData.class
)
WorkerDto assignee;
```

If no constraints use this group:
✔ No validation is triggered

✔ Input is permissive

✔ Business rules remain clean

---

## 10. Applying Groups to Other DTOs

### 10.1 WorkerDto

```java
public record WorkerDto(
    // …
) {
    public interface WorkerUpdateValidationData {
    }
}
```

```java
@PutMapping("/{id}")
public WorkerDto update(
  @PathVariable Long id,
  @RequestBody @Validated(WorkerUpdateValidationData.class)
  WorkerDto updatedWorker) {
    // …
}
```

---

### 10.2 CampaignDto

```java
public record CampaignDto(
    Long id,

    @NotBlank(message = "code can't be null")
    String code,

    @NotBlank(groups = { CampaignUpdateValidationData.class, Default.class })
    String name,

    @Size(groups = { CampaignUpdateValidationData.class, Default.class },
      min = 10, max = 50)
    String description,

    Set<TaskDto> tasks
) {
    public interface CampaignUpdateValidationData {
    }
}
```

```java
@PutMapping("/{id}")
public CampaignDto update(
  @PathVariable Long id,
  @RequestBody @Validated(CampaignUpdateValidationData.class)
  CampaignDto updatedCampaign) {
    // …
}
```

---

## 11. Key Takeaways 🧠

* **Validation groups enable operation-specific validation**
* `@Valid` → Default group only
* `@Validated(Group.class)` → Targeted validation
* Groups are essential for:

    * Create vs Update
    * Partial updates
    * Nested object control
* Group conversion allows **flexible, permissive APIs**

---

## 12. Conclusion

Validation groups give us the power to:

* Avoid over-validating
* Support real-world workflows
* Keep APIs flexible yet safe
* Clearly express business rules at the API boundary

They are a **critical tool** for designing professional, scalable Spring REST APIs.

---
