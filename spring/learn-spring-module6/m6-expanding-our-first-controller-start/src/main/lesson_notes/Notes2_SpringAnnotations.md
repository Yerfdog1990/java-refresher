
---

# **Lesson Notes: Spring Web Annotations**

---

## **1. Overview**

In this lesson, we explore key **Spring Web annotations** from the `org.springframework.web.bind.annotation` package. These annotations are primarily used to define and handle web requests within Spring MVC and Spring Boot applications.

They allow developers to:

* Map HTTP requests to specific controller methods.
* Bind request parameters, path variables, and headers.
* Control request and response handling.
* Manage cross-origin communication.

---

## **2. `@RequestMapping`**

The `@RequestMapping` annotation identifies request handler methods inside a `@Controller` class. It defines how a particular HTTP request should be routed to a specific method.

### **Key Attributes**

* **path / value / name:** URL pattern the method handles.
* **method:** Supported HTTP methods (GET, POST, PUT, etc.).
* **params:** Filters requests based on HTTP parameters.
* **headers:** Filters requests based on HTTP headers.
* **consumes:** Defines media types accepted in the request body.
* **produces:** Defines media types returned in the response body.

### **Example 1: Basic Mapping**

```java
@Controller
class VehicleController {

    @RequestMapping(value = "/vehicles/home", method = RequestMethod.GET)
    String home() {
        return "home";
    }
}
```

### **Example 2: Class-Level Mapping**

You can apply `@RequestMapping` at the class level to set a base path or default settings for all methods:

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

### **Shortcuts**

Since Spring 4.3, the following are predefined variants of `@RequestMapping`:

* `@GetMapping`
* `@PostMapping`
* `@PutMapping`
* `@DeleteMapping`
* `@PatchMapping`

Each automatically sets the corresponding HTTP method.

---

## **3. `@RequestBody`**

`@RequestBody` binds the body of an HTTP request to a Java object automatically. It is commonly used when sending JSON or XML data to the server.

```java
@PostMapping("/save")
void saveVehicle(@RequestBody Vehicle vehicle) {
    // logic to save the vehicle
}
```

Spring automatically deserializes the request body based on the content type (e.g., `application/json`).

---

## **4. `@PathVariable`**

`@PathVariable` binds method parameters to URI template variables.

### **Example 1: Named Variable**

```java
@RequestMapping("/{id}")
Vehicle getVehicle(@PathVariable("id") long id) {
    // retrieve vehicle by id
}
```

### **Example 2: Implicit Naming**

If the parameter name matches the path variable, the name can be omitted:

```java
@RequestMapping("/{id}")
Vehicle getVehicle(@PathVariable long id) {
    // retrieve vehicle by id
}
```

### **Optional Path Variable**

```java
@RequestMapping("/{id}")
Vehicle getVehicle(@PathVariable(required = false) long id) {
    // handle optional path variable
}
```

---

## **5. `@RequestParam`**

`@RequestParam` extracts query parameters from the request URL.

```java
@RequestMapping
Vehicle getVehicleByParam(@RequestParam("id") long id) {
    // logic to retrieve vehicle
}
```

### **Default Value**

You can set a default value and make the parameter optional:

```java
@RequestMapping("/buy")
Car buyCar(@RequestParam(defaultValue = "5") int seatCount) {
    // logic to buy car
}
```

### **Related Annotations**

* `@CookieValue` — Access cookies.
* `@RequestHeader` — Access HTTP headers.

All have similar configuration options as `@RequestParam`.

---

## **6. Response Handling Annotations**

### **6.1. `@ResponseBody`**

Marks a method’s return value as the actual HTTP response body. Commonly used in REST APIs.

```java
@ResponseBody
@RequestMapping("/hello")
String hello() {
    return "Hello World!";
}
```

If used at the class level, it applies to all methods in that controller.

---

### **6.2. `@ExceptionHandler`**

Defines a custom exception handler for specific exceptions thrown in controller methods.

```java
@ExceptionHandler(IllegalArgumentException.class)
void onIllegalArgumentException(IllegalArgumentException exception) {
    // handle the exception
}
```

---

### **6.3. `@ResponseStatus`**

Specifies the HTTP status code returned by a handler or exception method.

```java
@ExceptionHandler(IllegalArgumentException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
void onIllegalArgumentException(IllegalArgumentException exception) {
    // return HTTP 400 with reason
}
```

---

## **7. Other Web Annotations**

### **7.1. `@Controller`**

Marks a class as a Spring MVC controller. It is typically used to return view names in a web application.

```java
@Controller
class VehicleController {
    // controller logic
}
```

---

### **7.2. `@RestController`**

Combines `@Controller` and `@ResponseBody`. Used in REST APIs to send data (not views) directly as the HTTP response.

```java
@RestController
class VehicleRestController {
    // RESTful controller logic
}
```

Equivalent to:

```java
@Controller
@ResponseBody
class VehicleRestController {
    // ...
}
```

---

### **7.3. `@ModelAttribute`**

Used to bind form data or existing model attributes to a method parameter.

```java
@PostMapping("/assemble")
void assembleVehicle(@ModelAttribute("vehicle") Vehicle vehicleInModel) {
    // ...
}
```

If the parameter name matches the model key, you can omit it:

```java
@PostMapping("/assemble")
void assembleVehicle(@ModelAttribute Vehicle vehicle) {
    // ...
}
```

#### **Used on Methods**

When applied to a method, Spring adds the return value to the model automatically:

```java
@ModelAttribute("vehicle")
Vehicle getVehicle() {
    return new Vehicle();
}
```

If no key is specified, Spring uses the method name by default:

```java
@ModelAttribute
Vehicle vehicle() {
    return new Vehicle();
}
```

All `@ModelAttribute` methods are executed before controller handler methods.

---

### **7.4. `@CrossOrigin`**

Enables **Cross-Origin Resource Sharing (CORS)**, allowing requests from different domains.

```java
@CrossOrigin
@RequestMapping("/hello")
String hello() {
    return "Hello World!";
}
```

If placed at the class level, it applies to all request handler methods in that controller.

---

## **Summary**

Spring Web annotations simplify the development of web applications by providing clear and declarative ways to handle requests, responses, parameters, and exceptions. Understanding these annotations helps you build clean, maintainable, and RESTful web controllers efficiently.

---

