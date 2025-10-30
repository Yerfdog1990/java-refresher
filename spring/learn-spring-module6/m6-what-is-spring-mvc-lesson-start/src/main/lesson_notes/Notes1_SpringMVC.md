
---

# **Lesson Notes: Spring MVC**

---

## **1. Overview**

This lesson provides a step-by-step guide to setting up and understanding **Spring MVC (Model–View–Controller)** using both **Java-based** and **XML-based configurations**.
It also introduces **Spring Boot**, a modern extension of the Spring framework designed to simplify configuration and deployment.

The lesson concludes with a simple **Spring Boot MVC web application** demonstrating controllers, views, persistence, security, and testing.

---

## **2. What Is Spring MVC?**

**Spring MVC** is a core module of the Spring Framework that implements the **Model–View–Controller design pattern**. It separates application logic into three main components:

* **Model:** Represents the data and business logic.
* **View:** Defines the user interface.
* **Controller:** Handles user requests and coordinates between the model and the view.

Spring implements the MVC pattern using the **Front Controller** approach through a component called **`DispatcherServlet`**, which routes incoming web requests to the appropriate controller.

In Spring MVC:

* The **DispatcherServlet** is the main controller.
* The **Model** represents the application data.
* The **View** is usually implemented using JSP, Thymeleaf, or other templating technologies.

---

## **3. Spring MVC Using Java Configuration**

Spring MVC can be configured entirely through **Java-based configuration** without using XML files.

### **Basic Configuration**

```java
@EnableWebMvc
@Configuration
public class WebConfig {
    // basic MVC setup
}
```

The `@EnableWebMvc` annotation activates key MVC features such as:

* Controller mappings
* Type conversion
* Validation
* Message conversion
* Exception handling

### **Customized Configuration**

To customize the MVC configuration, implement the `WebMvcConfigurer` interface:

```java
@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

   @Override
   public void addViewControllers(ViewControllerRegistry registry) {
      registry.addViewController("/").setViewName("index");
   }

   @Bean
   public ViewResolver viewResolver() {
      InternalResourceViewResolver bean = new InternalResourceViewResolver();
      bean.setViewClass(JstlView.class);
      bean.setPrefix("/WEB-INF/view/");
      bean.setSuffix(".jsp");
      return bean;
   }
}
```

This configuration:

* Registers a **ViewResolver** that maps view names to JSP files under `/WEB-INF/view/`.
* Maps the root URL (`"/"`) directly to the `index.jsp` view without a controller.

### **Component Scanning**

If your controllers are in a package, you can use component scanning:

```java
@EnableWebMvc
@Configuration
@ComponentScan(basePackages = { "com.example.web.controller" })
public class WebConfig implements WebMvcConfigurer {
    // additional configurations
}
```

### **Web Application Initializer**

A Java-based Spring MVC project also requires an initializer to bootstrap the web context:

```java
public class MainWebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(final ServletContext sc) throws ServletException {

        AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
        root.scan("com.example");
        sc.addListener(new ContextLoaderListener(root));

        ServletRegistration.Dynamic appServlet =
          sc.addServlet("mvc", new DispatcherServlet(new GenericWebApplicationContext()));
        appServlet.setLoadOnStartup(1);
        appServlet.addMapping("/");
    }
}
```

> For versions before Spring 5, use `WebMvcConfigurerAdapter` instead of the interface.

---

## **4. Spring MVC Using XML Configuration**

Spring MVC can also be configured using **XML files** instead of Java classes.

```xml
<context:component-scan base-package="com.example.web.controller" />
<mvc:annotation-driven />    

<bean id="viewResolver" 
      class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="prefix" value="/WEB-INF/view/" />
    <property name="suffix" value=".jsp" />
</bean>

<mvc:view-controller path="/" view-name="index" />
```

The XML approach:

* Scans for controller components.
* Enables annotation-based MVC handling.
* Defines a view resolver for JSP files.

Applications using XML need a **web.xml** file for bootstrapping.

---

## **5. Controller and Views**

### **Controller Example**

```java
@Controller
public class SampleController {
    @GetMapping("/sample")
    public String showForm() {
        return "sample";
    }
}
```

### **View Example (sample.jsp)**

```html
<html>
   <head></head>
   <body>
      <h1>This is the body of the sample view</h1>	
   </body>
</html>
```

JSP files are stored in `/WEB-INF` so they cannot be directly accessed through a URL.

---

## **6. Spring MVC with Boot**

### **6.1 Spring Boot Overview**

**Spring Boot** simplifies the development of Spring applications by providing:

* Auto-configuration
* Embedded servers
* Starter dependencies
* Minimal XML setup

### **6.2 Spring Boot Starter**

Define the parent in your `pom.xml`:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
</parent>
```

This parent manages dependency versions automatically.

### **6.3 Spring Boot Entry Point**

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

`@SpringBootApplication` combines:

* `@Configuration`
* `@EnableAutoConfiguration`
* `@ComponentScan`

---

## **7. Simple MVC View Using Thymeleaf**

Add Thymeleaf support:

```xml
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-thymeleaf</artifactId> 
</dependency>
```

**application.properties**

```properties
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.application.name=Spring MVC Demo
```

**Controller**

```java
@Controller
public class SimpleController {
    @Value("${spring.application.name}")
    String appName;

    @RequestMapping("/")
    public String homePage(Model model) {
        model.addAttribute("appName", appName);
        return "home";
    }
}
```

**home.html**

```html
<html>
<head><title>Home</title></head>
<body>
    <h1>Hello!</h1>
    <p>Welcome to <span th:text="${appName}">Spring MVC App</span></p>
</body>
</html>
```

---

## **8. Adding Security**

Include Spring Security:

```xml
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-security</artifactId> 
</dependency>
```

**Security Configuration**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .authorizeHttpRequests(requests -> requests.anyRequest().permitAll())
          .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
```

---

## **9. Persistence with JPA and H2**

**Entity**

```java
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private String author;
}
```

**Repository**

```java
public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findByTitle(String title);
}
```

**Configuration**

```java
@EnableJpaRepositories("com.example.repo") 
@EntityScan("com.example.model")
@SpringBootApplication 
public class Application {}
```

**Database Properties**

```properties
spring.datasource.url=jdbc:h2:mem:bookapp
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

---

## **10. Controller for CRUD Operations**

```java
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public Iterable<Book> findAll() {
        return bookRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    @PutMapping("/{id}")
    public Book updateBook(@RequestBody Book book, @PathVariable Long id) {
        bookRepository.findById(id)
            .orElseThrow(BookNotFoundException::new);
        return bookRepository.save(book);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookRepository.deleteById(id);
    }
}
```

---

## **11. Error Handling**

```java
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ BookNotFoundException.class })
    protected ResponseEntity<Object> handleNotFound(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "Book not found", 
          new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
```

**Custom Exception**

```java
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException() {
        super("Book not found");
    }
}
```

---

## **12. Testing the Application**

**Spring Context Test**

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SpringContextTest {
    @Test
    public void contextLoads() { }
}
```

**REST API Tests with REST Assured**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookApiTest {

    @LocalServerPort
    private int port;

    @Test
    public void whenGetAllBooks_thenStatusOK() {
        Response response = RestAssured.get("http://localhost:" + port + "/api/books");
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }
}
```

---

## **13. Conclusion**

Spring MVC provides a structured way to build web applications based on the **MVC pattern**.
With **Spring Boot**, this process is even simpler, allowing developers to create full-stack web applications with minimal setup, automatic configuration, and embedded servers.

Developers can extend this foundation with advanced modules for **security**, **data persistence**, **error handling**, and **testing**, enabling the creation of scalable, maintainable enterprise applications.

---
