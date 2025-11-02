
---

# **Lesson Notes: Basic Form Template in Spring Boot**

### **1. Introduction**

In this lesson, we build upon our knowledge of displaying project data and extend the functionality of our Spring Boot application to handle **form submission** for creating new records.
We will use **Spring MVC** for handling HTTP requests and **Thymeleaf** for rendering the HTML templates.

Creating a “Basic Form Template” is the foundation for implementing **Create (C)** operations in the CRUD cycle.

---

### **2. Purpose of a Form Template**

A **form template** in a Spring Boot web application enables user input that can be submitted to the server for processing.
Through **Thymeleaf’s binding expressions**, form fields are directly mapped to an object (DTO or Entity) in the controller.

---

## **3. Creating a Basic Form Template**

### **3.1 Creating the Template File**

We first create a file `new-project.html` inside the directory:

```
src/main/resources/templates/
```

#### **Code Example: new-project.html**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Add Project</title>
</head>
<body>
    <h2 title="Add a new project">New Project</h2>
</body>
</html>
```

This template currently defines only a title header and serves as the base structure.

---

### **3.2 Controller Mapping for the Form Page**

We now define a new endpoint in our **ProjectController** class to serve this HTML page.

#### **Code Example: ProjectController.java**

```java
@GetMapping("/new")
public String newProject(Model model) {
    return "new-project";
}
```

This method handles **GET requests** to `/projects/new` and returns the name of the view `new-project.html`.

---

### **3.3 Adding a Navigation Link**

We add a link in our existing `projects.html` page that directs users to the **New Project** form.

#### **Code Example: projects.html**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Projects</title>
</head>
<body>
    <table>
        <!-- Existing table data -->
    </table>
    
    <p>
        <a th:href="@{/projects/new}">New Project</a>
    </p>
</body>
</html>
```

Now users can click **“New Project”** to open the form.

---

## **4. Defining the HTML Form**

### **4.1 Adding a Form in Thymeleaf**

We modify `new-project.html` to include a **form** with the following features:

* **th:action** – defines the POST request URL.
* **th:object** – binds the form to a specific model attribute.
* **th:field** – binds each field to a property in that model object.

#### **Code Example: new-project.html**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Add Project</title>
</head>
<body>
    <h2 title="Add a new project">New Project</h2>

    <form th:action="@{/projects}" th:object="${project}" method="post">
        <label for="name">Name</label>
        <input type="text" th:field="*{name}" id="name">
        <input type="submit" value="Save Project">
    </form>
</body>
</html>
```

---

## **5. Handling Errors: Binding the Model**

### **5.1 Common Error**

If we run the application and open `/projects/new`, we may see the following error:

> **Neither BindingResult nor plain target object for bean name 'project' available as request attribute**

This happens because Thymeleaf expects an object named `project` (bound to `th:object`), but we haven’t added it to the model in our controller.

---

### **5.2 Fixing the Error**

We bind a `ProjectDto` instance to the model in the controller.

#### **Code Example: ProjectController.java**

```java
@GetMapping("/new")
public String newProject(Model model) {
    model.addAttribute("project", new ProjectDto());
    return "new-project";
}
```

Now the form will render correctly, as the model attribute `project` is available to the view.

---

## **6. Submitting the Form (POST Request)**

Once the form is displayed and filled, submitting it should trigger a **POST** operation handled by the controller.

#### **Code Example: ProjectController.java**

```java
@PostMapping
public String addProject(ProjectDto project) {
    projectService.save(convertToEntity(project));
    return "redirect:/projects";
}
```

This method:

* Receives form data bound to the `ProjectDto` object.
* Converts the DTO to an entity.
* Saves the project.
* Redirects to `/projects` to show the updated list.

---

## **7. Testing the Flow**

### **Steps:**

1. Run the Spring Boot application.
2. Visit `http://localhost:8080/projects/`.
3. Click **“New Project”**.
4. Fill in the project name and submit.
5. Observe the redirect and the updated project list.

---

## **8. Using Messages from a Properties File**

To avoid hardcoding text, we can externalize strings using **`messages.properties`**.

#### **Step 1: Create the file**

`src/main/resources/messages.properties`

```
new.project.title=Add a new Project
```

#### **Step 2: Use in Template**

```html
<h2 th:text="#{new.project.title}">New Project</h2>
```

Now the header text is dynamically loaded from the message bundle.

---

## **9. Conceptual Flow Diagram**

Below is the logical flow of request and data binding for the Basic Form Template.

**Diagram: Basic Form Submission in Spring Boot**

```
[Client (Browser)]
        │
        ▼
 [GET /projects/new]
        │
        ▼
 [DispatcherServlet]
        │
        ▼
 [ProjectController.newProject()]
   └── model.addAttribute("project", new ProjectDto())
        │
        ▼
 [Thymeleaf Template: new-project.html]
        │
        ▼
 [User fills and submits form → POST /projects]
        │
        ▼
 [DispatcherServlet]
        │
        ▼
 [ProjectController.addProject()]
   └── projectService.save()
        │
        ▼
 [Redirect: /projects]
```

---

## **10. Summary**

| **Aspect**               | **Description**                                                                 |
| ------------------------ | ------------------------------------------------------------------------------- |
| **View Template**        | `new-project.html` defines the form structure.                                  |
| **Data Binding**         | Managed through `th:object` and `th:field`.                                     |
| **Controller**           | Provides `@GetMapping` for form display and `@PostMapping` for form submission. |
| **Error Handling**       | Fixed by adding `model.addAttribute()` for the bound object.                    |
| **Internationalization** | Achieved via `messages.properties` and `th:text="#{}"`.                         |

---

### ✅ **Outcome**

After completing this lesson, learners should be able to:

* Design and implement a basic form template using **Thymeleaf**.
* Bind form data to a **DTO** or **Entity**.
* Handle both **GET** and **POST** mappings in **Spring MVC**.
* Integrate message properties for dynamic content.

---

