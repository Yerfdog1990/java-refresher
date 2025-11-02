
---

# **Lesson Notes: Form Validation in Spring Boot**

---

## ✅ **Learning Outcomes**

By the end of this lesson, learners should be able to:

* Apply **Bean Validation annotations** in DTOs or entities.
* Handle and display **validation errors** using **BindingResult**.
* Implement **Thymeleaf error rendering** for individual fields and global errors.
* Customize validation messages using properties files.
* Understand the **validation flow** between controller, DTO, and view.

---

## **1. Introduction**

After implementing a basic form to create new projects, the next essential step is **form validation**. Validation ensures that user-submitted data is **accurate**, **safe**, and **meaningful** before it reaches the persistence layer.

Form validation in Spring Boot is implemented using **Java Bean Validation (JSR 380)** annotations together with **Spring MVC** and **Thymeleaf** for user feedback.

---

## **2. Why Validation Matters**

There are two key reasons why validation is essential in any web application:

### **(a) Data Integrity**

Validation ensures the **sanity and correctness of the data** being persisted.
Since this is a **write operation**, unvalidated or empty fields (like a project name) could cause logical or database-level issues.

### **(b) User Experience (UX)**

Good validation also enhances **user experience**.
If input is invalid, users should:

* Clearly understand what went wrong.
* Receive specific feedback on how to correct the input.
* Be allowed to correct errors without losing their work.

---

## **3. Where to Add Validation**

Validation can be implemented in multiple layers:

* **Presentation layer** (client-side with JavaScript)
* **Controller layer** (Spring MVC)
* **Domain or DTO layer** (Bean Validation annotations)
* **Persistence layer** (Database constraints)

In this lesson, we’ll focus on **DTO-level validation**, which integrates neatly with Spring’s **@Valid** mechanism.

---

## **4. Validations on Entities or DTOs**

We begin by defining our validation constraints inside the `ProjectDto` class.

### **Code Example: ProjectDto.java**

```java
import jakarta.validation.constraints.NotBlank;

public class ProjectDto {

    @NotBlank
    private String name;

    // other fields, getters and setters
}
```

The `@NotBlank` annotation ensures that the **name field** is neither null nor empty nor only whitespace.

---

## **5. Enabling Validation in the Controller**

We now activate validation when the form is submitted.
This is done using the `@Valid` annotation on the DTO parameter in the controller’s POST method.

### **Code Example: ProjectController.java**

```java
@PostMapping
public String addProject(@Valid ProjectDto project) {
    projectService.save(convertToEntity(project));
    return "redirect:/projects";
}
```

Spring will automatically validate the `project` object based on the annotations present in `ProjectDto`.

If validation fails, Spring throws a `MethodArgumentNotValidException`, resulting in a 400 Bad Request — which is technically correct but not user-friendly.

---

## **6. Capturing Validation Errors for Better UX**

To improve the user experience, we capture validation errors and return them to the form view.

We achieve this by adding:

* A **BindingResult** parameter to capture validation results.
* A **@ModelAttribute("project")** annotation to bind the form object properly.

### **Code Example: Enhanced Controller**

```java
@PostMapping
public String addProject(
        @Valid @ModelAttribute("project") ProjectDto project,
        BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
        return "new-project"; // stay on form page
    }

    projectService.save(convertToEntity(project));
    return "redirect:/projects";
}
```

### **Explanation:**

* **@Valid** triggers the validation process.
* **BindingResult** contains validation errors (if any).
* If errors exist, we remain on the same page.
* Otherwise, the project is saved, and we redirect to `/projects`.

---

## **7. Displaying Errors in Thymeleaf**

To provide user feedback, we modify the form (`new-project.html`) to display error messages.

### **Code Example: new-project.html**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Add Project</title>
</head>
<body>
    <h2 th:text="#{new.project.title}">Add New Project</h2>

    <form th:action="@{/projects}" th:object="${project}" method="post">
        <label for="name">Project Name:</label>
        <input type="text" th:field="*{name}" id="name">

        <!-- Display validation errors for 'name' -->
        <p th:if="${#fields.hasErrors('name')}" 
           th:errors="*{name}" 
           style="color:red"></p>

        <input type="submit" value="Save Project">
    </form>
</body>
</html>
```

---

## **8. Running and Testing Validation**

1. Run the application.
2. Navigate to `http://localhost:8080/projects/new`.
3. Submit the form **without** entering a project name.
4. Observe the error message displayed below the field.

This time, the user remains on the same form and sees an informative error message.

---

## **9. Customizing Validation Messages**

To make validation messages more descriptive or localized, we use a **ValidationMessages.properties** file.

### **File Location:**

```
src/main/resources/ValidationMessages.properties
```

### **Example Content:**

```
javax.validation.constraints.NotBlank.message=Field should not be blank
```

Spring automatically loads this message bundle and overrides the default message of `@NotBlank`.

---

## **10. Optional: Displaying All Field Errors**

Thymeleaf allows flexible rendering of multiple field or global errors using iteration.

### **Example: Displaying All Errors**

```html
<ul>
    <li th:each="err : ${#fields.errors('*')}" th:text="${err}" />
</ul>
```

### **Example: Displaying Global Errors**

```html
<ul>
    <li th:each="err : ${#fields.errors('global')}" th:text="${err}" />
</ul>
```

These snippets are especially helpful for forms with multiple fields or cross-field validation logic.

---

## **11. Dependency Notes**

### **Spring Boot Starter Dependency**

When you include:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

It automatically brings in `spring-boot-starter-validation` (for versions < 2.3).

### **For Spring Boot ≥ 2.3**

You must add validation manually:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

This brings in:

* **jakarta.validation-api**
* **hibernate-validator**

---

## **12. Conceptual Flow Diagram**

**Form Validation Request Flow**

```
[User Submits Form]
        │
        ▼
 [DispatcherServlet]
        │
        ▼
 [Controller @PostMapping]
        │
     @Valid ProjectDto
        │
        ▼
 [Validation Layer (Bean Validation API)]
        │
   ├── Valid → Save & Redirect
   └── Invalid → BindingResult.hasErrors()
        │
        ▼
 [Return Form View: new-project.html]
        │
        ▼
 [Thymeleaf Renders Error Messages]
```

---

## **13. Summary Table**

| **Step** | **Action**                        | **Purpose**                      |
| -------- | --------------------------------- | -------------------------------- |
| **1**    | Add validation annotations in DTO | Enforce input rules              |
| **2**    | Use `@Valid` in Controller        | Trigger automatic validation     |
| **3**    | Add `BindingResult`               | Capture validation results       |
| **4**    | Return same view if errors        | Allow user correction            |
| **5**    | Display errors via `th:errors`    | Provide feedback                 |
| **6**    | Customize messages                | Improve clarity and localization |

---


Would you like me to include a **visual diagram (color-coded)** showing both the **validation and error rendering flow** (similar in style to the “Static Members” and “DispatcherServlet” diagrams)?
