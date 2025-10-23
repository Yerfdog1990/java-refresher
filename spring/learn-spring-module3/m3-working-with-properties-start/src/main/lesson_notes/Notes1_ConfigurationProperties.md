
---

# 🌿 **Lesson 1: Configuration Using Properties**

## 1️⃣ Lesson Goals

In the previous lessons, we learned how to create beans and wire them together.
Now, we’ll explore **how to configure our Spring Boot application using property files** — a powerful way to make applications flexible, maintainable, and environment-independent.

By the end of this lesson, you will be able to:

✅ Understand how Spring Boot uses externalized configuration
✅ Define and read key-value pairs from `.properties` files
✅ Inject property values using `@Value` and `@ConfigurationProperties`
✅ Load properties from multiple sources (e.g., files, environment, command line)
✅ Use the `Environment` interface to access configuration dynamically

---

## 2️⃣ What Are Properties in Spring?

Spring applications are **configurable through externalized properties** — meaning we can adjust an application’s behavior **without recompiling the code**.

Property files are simple **key-value pairs**:

```properties
propertyName=propertyValue
```

Spring Boot automatically detects and loads properties from:

```
src/main/resources/application.properties
```

You can also use YAML (`.yml`) files, environment variables, or command-line arguments for configuration.

---

## 3️⃣ Example: Defining Application Properties

Let’s define a simple example to understand how properties work in a Spring Boot application.

We’ll configure project-related information in a file named:

**📁 application.properties**

```properties
project.prefix=PRO
project.suffix=123
```

---

## 4️⃣ Injecting Property Values Using `@Value`

We can inject property values directly into class fields using the `@Value` annotation.

```java
@Repository
public class ProjectRepositoryImpl implements IProjectRepository {

    @Value("${project.prefix}")
    private String prefix;

    @Value("${project.suffix}")
    private Integer suffix;

    public void save(Project project) {
        updateInternalId(project);
    }

    private void updateInternalId(Project project) {
        System.out.println("Prepending Prefix: " + prefix);
        System.out.println("Appending Suffix: " + suffix);

        project.setInternalId(prefix + "-" + project.getId() + "-" + suffix);
        System.out.println("Generated internal id: " + project.getInternalId());
    }
}
```

When the Spring container initializes this bean, it will **automatically inject** the values from the `application.properties` file into the `prefix` and `suffix` fields.

---

### ✅ Running the Application

```java
@SpringBootApplication
public class LsApp {

    @Autowired
    private IProjectService projectService;

    @PostConstruct
    public void init() {
        Project project = new Project("My First Project", LocalDate.now());
        projectService.save(project);
    }

    public static void main(String[] args) {
        SpringApplication.run(LsApp.class, args);
    }
}
```

### 🧾 Output

```
Prepending Prefix: PRO
Appending Suffix: 123
Generated internal id: PRO-1-123
```

This confirms that our properties were successfully injected into the application.

---

## 5️⃣ Loading Properties with `@PropertySource` (Pure Spring Example)

In **non-Spring Boot** (pure Spring) applications, we must manually specify where the properties file is located.

```java
@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {
}
```

The `@PropertySource` annotation tells Spring to load key-value pairs from the specified file into the environment.

You can also load **multiple property files**:

```java
@Configuration
@PropertySources({
    @PropertySource("classpath:foo.properties"),
    @PropertySource("classpath:bar.properties")
})
public class MultiPropertyConfig {
}
```

If there are duplicate keys, **the last file loaded takes precedence**.

---

## 6️⃣ Accessing Properties Using the `Environment` Interface

The `Environment` interface provides programmatic access to all property sources.
It’s useful when you want to retrieve properties dynamically or when you’re unsure which profile is active.

```java
@Configuration
public class AppConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);

    @Autowired
    private Environment environment;

    @PostConstruct
    private void postConstruct() {
        String suffix = environment.getProperty("project.suffix");
        LOG.info("Loaded property from Environment: project.suffix = {}", suffix);
    }
}
```

Spring searches across all registered **PropertySource** objects (e.g., files, system variables, etc.) to resolve the requested property.

---

## 7️⃣ Using `@ConfigurationProperties` for Grouped Properties

When you have **multiple related properties**, it’s better to group them together using the `@ConfigurationProperties` annotation.
This approach avoids clutter and keeps configuration structured.

### Example

**application.properties**

```properties
database.url=jdbc:mysql://localhost:3306/demo
database.username=admin
database.password=secret
```

**Java Class**

```java
@Component
@ConfigurationProperties(prefix = "database")
public class DatabaseConfig {
    private String url;
    private String username;
    private String password;

    // Getters and Setters
}
```

Now, any class can autowire this configuration:

```java
@Service
public class DatabaseService {

    @Autowired
    private DatabaseConfig dbConfig;

    public void connect() {
        System.out.println("Connecting to " + dbConfig.getUrl());
    }
}
```

---

## 8️⃣ Environment-Specific Property Files

Spring Boot allows environment-based configuration using profile-specific files.

For example:

```
application.properties
application-dev.properties
application-prod.properties
```

If you activate the “dev” profile:

```bash
java -jar app.jar --spring.profiles.active=dev
```

Spring Boot loads both `application.properties` and `application-dev.properties`, with the latter overriding duplicate keys.

---

## 9️⃣ Overriding Properties

Spring Boot automatically resolves properties in the following **priority order** (highest → lowest):

1. Command line arguments
2. `application-{profile}.properties`
3. `application.properties`
4. System environment variables
5. Default values in code

Example:

```bash
java -jar app.jar --project.suffix=999
```

This overrides the value in the `application.properties` file.

---

## 🔟 Randomized Properties

Spring Boot includes a special **RandomValuePropertySource** for generating random numbers or UUIDs.

**application.properties**

```properties
random.number=${random.int}
random.uuid=${random.uuid}
```

These can be injected as:

```java
@Value("${random.number}")
private int randomNumber;

@Value("${random.uuid}")
private String randomId;
```

---

## 1️⃣1️⃣ Testing Property Injection

We can create test-specific properties using `@TestPropertySource`:

```java
@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"foo=bar"})
public class PropertyInjectionTest {

    @Value("${foo}")
    private String foo;

    @Test
    public void whenPropertyInjected_thenSuccess() {
        assertThat(foo).isEqualTo("bar");
    }
}
```

---

## 1️⃣2️⃣ Summary Table

| Feature                    | Description                 | Example                                       |
| -------------------------- | --------------------------- | --------------------------------------------- |
| `@Value`                   | Inject single property      | `@Value("${project.prefix}")`                 |
| `@PropertySource`          | Load custom file            | `@PropertySource("classpath:foo.properties")` |
| `Environment`              | Programmatically access     | `env.getProperty("key")`                      |
| `@ConfigurationProperties` | Bind multiple properties    | `prefix = "database"`                         |
| Profile files              | Environment-specific        | `application-dev.properties`                  |
| Random values              | Dynamic property generation | `${random.uuid}`                              |

---

## 1️⃣3️⃣ Key Takeaways

- 🔹 Spring Boot **auto-detects `application.properties`** and injects values automatically.
- 🔹 Use `@Value` for simple injection; `@ConfigurationProperties` for grouped configuration.
- 🔹 The `Environment` interface gives you programmatic access to all properties.
- 🔹 Profile-specific and externalized configurations make your app flexible and environment-independent.
- 🔹 Always externalize sensitive credentials (e.g., passwords) outside source code.

---
