
---

# **Lesson Notes: Spring Controller and Web Annotations**

---

## **1. Lesson Overview**

This lesson introduces the **Spring Controller** in the Spring MVC framework and explores the **core web annotations** used to handle HTTP requests and responses.
By the end of this lesson, learners will understand how to build both MVC-style and RESTful controllers using the appropriate Spring annotations.

---

## **2. The Front Controller in Spring MVC**

Spring MVC follows the **Model–View–Controller** architectural pattern. At its core is the **DispatcherServlet**, which acts as the **Front Controller**.

### **Main Responsibilities**

* Intercepts all incoming HTTP requests.
* Converts request data into internal Java objects.
* Sends data to the **Model** for processing.
* Retrieves processed data and forwards it to the **View** for rendering.

### **Simplified Flow Diagram**

```
Client → DispatcherServlet → Controller → Service/Model → ViewResolver → View
```

The `DispatcherServlet` delegates requests to controllers and coordinates responses either as rendered views (MVC) or raw data (REST).

---

## **3. Maven Dependencies**

To use Spring MVC or REST controllers in a Spring Boot application, add the following dependencies:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>3.0.2</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

The **spring-boot-starter-web** includes Spring MVC, and **Thymeleaf** provides template rendering for MVC views.

---

## **4. Web Configuration Example**

Basic configuration for Spring Boot can include enabling the default servlet and defining application beans:

```java
@Bean
public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> enableDefaultServlet() {
    return factory -> factory.setRegisterDefaultServlet(true);
}

@Bean
public Greeting greeting() {
    Greeting greeting = new Greeting();
    greeting.setMessage("Hello World !!");
    return greeting;
}

@Bean
public ObjectMapper objectMapper() {
    return new ObjectMapper();
}
```

When a controller returns a view such as `"welcome"`, Spring’s **View Resolver** automatically looks for `welcome.html` under the **templates** directory.

---

## **5. The MVC Controller**

An **MVC controller** handles web requests and returns a `ModelAndView` object for view rendering.

```java
@Controller
@RequestMapping(value = "/test")
public class TestController {

    @GetMapping
    public ModelAndView getTestData() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("welcome");
        mv.getModel().put("data", "Welcome home man");
        return mv;
    }
}
```

### **Explanation**

* The controller is mapped to `/test`.
* The `@GetMapping` annotation handles GET requests.
* The returned `ModelAndView` combines both the **model** (data) and the **view name** (`welcome`).
* The View Resolver locates `welcome.html` in the **templates** folder.

---

## **6. The REST Controller**

REST controllers return raw data (JSON or XML) rather than rendered views.

```java
@RestController
public class StudentController {

    @GetMapping(value = "/student/{studentId}")
    public Student getStudentData(@PathVariable Integer studentId) {
        Student student = new Student();
        student.setName("Peter");
        student.setId(studentId);
        return student;
    }
}
```

**Example Request:**
`GET http://localhost:8080/student/1`

**Response (JSON):**

```json
{
  "id": 1,
  "name": "Peter"
}
```

### **Key Difference**

The `@RestController` eliminates the need for view resolution and automatically serializes Java objects into the HTTP response body.

---

## **7. Understanding Spring Web Annotations**

Spring provides a range of annotations under `org.springframework.web.bind.annotation` that make it easy to map HTTP requests and responses in controller classes.

Below are the most common ones.

---

### **7.1. @RequestMapping**

* Marks request handler methods or entire controller classes.
* Used to map URLs and HTTP methods to specific handler methods.
* Supports attributes like `path`, `method`, `params`, `headers`, `consumes`, and `produces`.

**Example:**

```java
@Controller
class VehicleController {

    @RequestMapping(value = "/vehicles/home", method = RequestMethod.GET)
    String home() {
        return "home";
    }
}
```

**Class-level Mapping Example:**

```java
@Controller
@RequestMapping(value = "/vehicles", method = RequestMethod.GET)
class VehicleController {

    @RequestMapping("/home")
    String home() {
        return "home";
    }
}
```

**Simplified Variants:**

* `@GetMapping` → handles GET requests
* `@PostMapping` → handles POST requests
* `@PutMapping` → handles PUT requests
* `@DeleteMapping` → handles DELETE requests
* `@PatchMapping` → handles PATCH requests

---

### **7.2. @RequestBody**

Maps the **body** of an HTTP request to a Java object.
Spring automatically deserializes the body based on the content type (JSON/XML).

```java
@PostMapping("/save")
void saveVehicle(@RequestBody Vehicle vehicle) {
    // vehicle object is automatically populated from request body
}
```

---

### **7.3. @PathVariable**

Binds a URI template variable to a method parameter.

```java
@RequestMapping("/{id}")
Vehicle getVehicle(@PathVariable("id") long id) {
    // fetch vehicle by ID
}
```

If the variable name matches, the name can be omitted:

```java
@RequestMapping("/{id}")
Vehicle getVehicle(@PathVariable long id) {
    // ...
}
```

Optional path variables:

```java
@RequestMapping("/{id}")
Vehicle getVehicle(@PathVariable(required = false) long id) {
    // ...
}
```

---

### **7.4. @RequestParam**

Used to extract query parameters from a request.

```java
@RequestMapping
Vehicle getVehicleByParam(@RequestParam("id") long id) {
    // ...
}
```

With default values:

```java
@RequestMapping("/buy")
Car buyCar(@RequestParam(defaultValue = "5") int seatCount) {
    // ...
}
```

---

### **7.5. @ResponseBody**

Indicates that the return value of a method is the response body itself.

```java
@ResponseBody
@RequestMapping("/hello")
String hello() {
    return "Hello World!";
}
```

When used at the **class level**, all handler methods in the class return data directly.

---

### **7.6. @ExceptionHandler**

Defines a custom method for handling specific exceptions.

```java
@ExceptionHandler(IllegalArgumentException.class)
void onIllegalArgumentException(IllegalArgumentException exception) {
    // handle exception
}
```

Can be combined with `@ResponseStatus` for setting custom HTTP status codes.

---

### **7.7. @ResponseStatus**

Sets a custom HTTP response status for a request handler or exception.

```java
@ExceptionHandler(IllegalArgumentException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
void onIllegalArgumentException(IllegalArgumentException exception) {
    // returns 400 BAD REQUEST
}
```

---

### **7.8. @Controller and @RestController**

* `@Controller` → Marks a class as a Spring MVC controller.
* `@RestController` → Combines `@Controller` + `@ResponseBody` to return data directly.

**Equivalent Declarations:**

```java
@Controller
@ResponseBody
class VehicleRestController {
    // ...
}
```

```java
@RestController
class VehicleRestController {
    // ...
}
```

---

### **7.9. @ModelAttribute**

Used for accessing or populating data in the **model** for an MVC controller.

```java
@PostMapping("/assemble")
void assembleVehicle(@ModelAttribute("vehicle") Vehicle vehicleInModel) {
    // ...
}
```

When applied to a method:

```java
@ModelAttribute("vehicle")
Vehicle getVehicle() {
    // method’s return value is added to the model
    return new Vehicle();
}
```

---

### **7.10. @CrossOrigin**

Enables **Cross-Origin Resource Sharing (CORS)** for a specific method or the entire controller.

```java
@CrossOrigin
@RequestMapping("/hello")
String hello() {
    return "Hello World!";
}
```

Can be fine-tuned using parameters such as `origins`, `methods`, and `maxAge`.

---

## **8. Summary**

* The **DispatcherServlet** serves as the Front Controller in Spring MVC.
* **@Controller** and **@RestController** are entry points for request handling.
* The `@RequestMapping` and its variants map HTTP requests to handler methods.
* **@RequestBody**, **@PathVariable**, and **@RequestParam** extract data from HTTP requests.
* **@ResponseBody** and **@ResponseStatus** define how responses are returned.
* **@ExceptionHandler** handles specific exceptions gracefully.
* **@CrossOrigin** allows cross-domain communication.

Together, these annotations make Spring MVC controllers powerful and declarative, minimizing boilerplate code and improving readability.

---


