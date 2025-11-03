
---

# **Lesson Notes: The @PathVariable Annotation**

## **1. Overview**

In Spring MVC, the `@PathVariable` annotation is used to extract values from the **URI path** and bind them directly to method parameters in a controller.
It allows developers to create **dynamic URLs**, making endpoints more flexible and expressive.

For example, instead of hardcoding multiple endpoints like:
`/projects/1`, `/projects/2`, `/projects/3`,
we can create one endpoint `/projects/{id}` that dynamically maps to different project IDs.

---

## **2. Definition**

The `@PathVariable` annotation binds a method parameter to a **URI template variable** defined in the request mapping.

### **Syntax**

```java
@GetMapping("/{variableName}")
public ResponseEntity<?> methodName(@PathVariable DataType variableName) {
    // business logic
}
```

---

## **3. Basic Example**

Consider a `ProjectController` that retrieves a project by its ID.

```java
@RestController
@RequestMapping("/projects")
public class ProjectController {

    @GetMapping("/{id}")
    public ProjectDto findOne(@PathVariable Long id) {
        Project entity = projectService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return convertToDto(entity);
    }
}
```

### **Explanation**

* The `@GetMapping("/{id}")` defines a dynamic path variable `{id}`.
* `@PathVariable Long id` binds that variable to the method parameter `id`.
* If a user sends a GET request to `http://localhost:8080/projects/101`,
  the value `101` will automatically be assigned to the variable `id`.

---

## **4. Explicit Variable Naming**

If the variable name in the URL and the parameter name are **different**,
we can explicitly specify the path variable name in the annotation.

```java
@GetMapping("/{projectId}")
public ProjectDto findOne(@PathVariable("projectId") Long id) {
    // ...
}
```

If the path variable name **matches** the parameter name,
the explicit naming is optional.

---

## **5. Multiple @PathVariable Parameters**

You can map multiple URI segments to method parameters.

```java
@GetMapping("/departments/{deptId}/projects/{projectId}")
public ProjectDto getProject(
        @PathVariable Long deptId,
        @PathVariable Long projectId) {
    // Logic using deptId and projectId
}
```

**Example URL:**

```
GET /departments/5/projects/22
```

Result:

* `deptId = 5`
* `projectId = 22`

---

## **6. @PathVariable with Regular Expressions**

Spring allows the use of **regex patterns** inside URI templates for validation and pattern matching.

```java
@GetMapping("/{category}-{subcategoryId:\\d\\d}/{id}")
public ProjectDto findOne(
        @PathVariable Long id,
        @PathVariable String category,
        @PathVariable Integer subcategoryId) {
    // ...
}
```

**Example Request:**

```
GET /projects/tech-12/45
```

**Result:**

* `category = "tech"`
* `subcategoryId = 12`
* `id = 45`

This ensures that the `subcategoryId` must be a two-digit number (`\\d\\d`).

---

## **7. Optional Path Variables**

If a path variable is not mandatory, you can make it **optional**:

```java
@GetMapping("/{id}")
public ProjectDto findOne(@PathVariable(required = false) Long id) {
    // If no ID is provided, id will be null
    return new ProjectDto();
}
```

However, this is rarely used, as path variables are typically **mandatory**.
Optional parameters are better handled using `@RequestParam`.

---

## **8. Combining @PathVariable with @RequestMapping**

You can define URI templates at both the **class** and **method** level:

```java
@RestController
@RequestMapping("/owners/{ownerId}")
public class OwnerController {

    @GetMapping("/pets/{petId}")
    public Pet findPet(@PathVariable Long ownerId, @PathVariable Long petId) {
        // ...
    }
}
```

**Example URL:**

```
GET /owners/7/pets/42
```

This retrieves the pet with ID `42` that belongs to the owner `7`.

---

## **9. Type Conversion**

Spring automatically converts simple path variable types —
like `int`, `long`, `boolean`, `String`, or even `enum` —
using the **Type Conversion System**.

If conversion fails, Spring throws a `TypeMismatchException`.

---

## **10. Practical Example: Nested Resource Access**

Let’s see a complete RESTful example for hierarchical data access.

```java
@RestController
@RequestMapping("/api")
public class EmployeeController {

    @GetMapping("/departments/{deptId}/employees/{empId}")
    public EmployeeDto getEmployeeDetails(
            @PathVariable("deptId") Long departmentId,
            @PathVariable("empId") Long employeeId) {
        return employeeService.getEmployee(departmentId, employeeId);
    }
}
```

**Example Request**

```
GET /api/departments/3/employees/12
```

**Output**

```json
{
  "departmentId": 3,
  "employeeId": 12,
  "name": "Alice Johnson",
  "role": "Software Engineer"
}
```

---

## **11. Diagram: @PathVariable Flow in Spring MVC**

Below is a conceptual diagram of how `@PathVariable` works.

```
 ┌────────────────────────────┐
 │        HTTP Client         │
 │ (GET /projects/101)        │
 └────────────┬───────────────┘
              │
              ▼
 ┌────────────────────────────┐
 │     DispatcherServlet      │
 │ Intercepts the Request     │
 └────────────┬───────────────┘
              │
              ▼
 ┌────────────────────────────┐
 │    Handler Mapping         │
 │ Finds Controller & Method  │
 │ /projects/{id} → findOne() │
 └────────────┬───────────────┘
              │
              ▼
 ┌────────────────────────────┐
 │     Controller Method      │
 │  @GetMapping("/{id}")      │
 │  @PathVariable Long id=101 │
 └────────────┬───────────────┘
              │
              ▼
 ┌────────────────────────────┐
 │       Service Layer        │
 │    projectService.findById │
 └────────────┬───────────────┘
              │
              ▼
 ┌────────────────────────────┐
 │       Response Body        │
 │ JSON → { "id": 101, ... }  │
 └────────────────────────────┘
```

---

## **12. Key Points Summary**

| Concept                  | Description                                       |
| ------------------------ | ------------------------------------------------- |
| **Annotation**           | `@PathVariable`                                   |
| **Purpose**              | Binds method parameters to URI template variables |
| **Binding Type**         | By name or explicit declaration                   |
| **Supports Regex**       | Yes                                               |
| **Optional Parameters**  | Yes (`required = false`)                          |
| **Automatic Conversion** | Yes (for primitive and simple types)              |
| **Common Use Case**      | RESTful endpoints with dynamic resource IDs       |

---

## **13. Comparison: @PathVariable vs. @RequestParam**

| Feature              | @PathVariable                | @RequestParam                     |
| -------------------- | ---------------------------- | --------------------------------- |
| **Source**           | URI Path                     | Query Parameter                   |
| **Example URL**      | `/projects/5`                | `/projects?id=5`                  |
| **Annotation Usage** | `@PathVariable Long id`      | `@RequestParam Long id`           |
| **Common Use**       | RESTful resource identifiers | Search filters or optional params |

---

## **14. Conclusion**

The `@PathVariable` annotation plays a central role in **RESTful URL design**.
It helps create **clean, readable, and parameterized URIs**, enabling dynamic routing and clear resource identification.
By leveraging regex, optional parameters, and automatic type conversion, developers can build powerful, flexible, and type-safe endpoints.

---
