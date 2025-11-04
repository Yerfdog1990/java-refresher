
---

## **Lesson Notes: Spring HTTP Message Converters**

### **1. Introduction**

In a Spring MVC application, when a client sends a request or receives a response, data often needs to be **converted** between Java objects (POJOs) and various formats such as **JSON**, **XML**, or **plain text**.

Spring simplifies this process through a powerful mechanism called **HTTP Message Converters**, which handle **serialization** (Java → JSON/XML) and **deserialization** (JSON/XML → Java) automatically.

---

### **2. Learning Goals**

By the end of this lesson, you should be able to:

* Understand what Spring HTTP Message Converters are.
* Describe how Spring serializes and deserializes data in requests and responses.
* Identify the default converters provided by Spring.
* Customize or extend message converters in your applications.
* Use `@RequestBody` and `@ResponseBody` effectively.

---

### **3. What Are HTTP Message Converters?**

**Definition:**
An **`HttpMessageConverter`** in Spring is an interface responsible for **converting HTTP requests and responses** to and from Java objects based on the content type (MIME type).

When a request comes in, the converter **reads** the data (e.g., JSON) and transforms it into a Java object.
When returning a response, the converter **writes** a Java object into the appropriate format (e.g., JSON or XML).

---

### **4. The `HttpMessageConverter` Interface**

Below is the core interface that defines how converters work internally:

```java
public interface HttpMessageConverter<T> {
    boolean canRead(Class<?> clazz, @Nullable MediaType mediaType);
    boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType);

    T read(Class<? extends T> clazz, HttpInputMessage inputMessage)
        throws IOException, HttpMessageNotReadableException;

    void write(T t, @Nullable MediaType contentType, HttpOutputMessage outputMessage)
        throws IOException, HttpMessageNotWritableException;
}
```

* **`read()`**: Converts the request body into a Java object.
* **`write()`**: Converts a Java object into a response body.
* **`canRead()` / `canWrite()`**: Used to verify if the converter can handle a specific class and media type.

---

### **5. Default Message Converters in Spring**

Spring provides a rich set of **pre-enabled message converters** that handle common content types:

| **Converter**                                                    | **Description**                                   |
| ---------------------------------------------------------------- | ------------------------------------------------- |
| `ByteArrayHttpMessageConverter`                                  | Converts byte arrays                              |
| `StringHttpMessageConverter`                                     | Converts plain text                               |
| `ResourceHttpMessageConverter`                                   | Converts file resources                           |
| `FormHttpMessageConverter`                                       | Converts form data                                |
| `MappingJackson2HttpMessageConverter`                            | Converts JSON (via Jackson library)               |
| `Jaxb2RootElementHttpMessageConverter`                           | Converts XML (if JAXB is present)                 |
| `MappingJackson2XmlHttpMessageConverter`                         | Converts XML using Jackson                        |
| `AtomFeedHttpMessageConverter`, `RssChannelHttpMessageConverter` | Converts Atom/RSS feeds (if Rome library present) |

---

### **6. Example: JSON Request and Response**

Consider a simple REST API for managing `Project` entities.

#### **Controller Example**

```java
@RestController
@RequestMapping("/projects")
public class ProjectController {

    @PostMapping
    public Project createProject(@RequestBody Project project) {
        // Automatically converted from JSON → Project (deserialization)
        project.setId(1L);
        return project; // Converted from Project → JSON (serialization)
    }
}
```

#### **Model Class**

```java
public class Project {
    private Long id;
    private String name;

    // Getters and setters
}
```

#### **Client Request**

```bash
POST /projects
Content-Type: application/json

{
  "name": "Spring Boot Demo"
}
```

#### **Response**

```json
{
  "id": 1,
  "name": "Spring Boot Demo"
}
```

**Explanation:**

* `@RequestBody` automatically uses a JSON converter to **deserialize** the request.
* `@ResponseBody` (or `@RestController`) uses the same converter to **serialize** the response.

---

### **7. Content Negotiation**

Spring decides **which converter to use** based on HTTP headers:

* **`Content-Type`** — Indicates the request body format (used for deserialization).
* **`Accept`** — Indicates what format the client expects in the response (used for serialization).

#### **Example**

```bash
curl -H "Content-Type: application/json" \
     -H "Accept: application/xml" \
     -d '{"id":1,"name":"Test"}' \
     http://localhost:8080/projects
```

In this case, the request body is **JSON**, but the response should be **XML** — Spring will use:

* `MappingJackson2HttpMessageConverter` → for JSON input.
* `MappingJackson2XmlHttpMessageConverter` → for XML output.

---

### **8. Configuring Custom Message Converters**

Developers can **customize or extend** the default converters by implementing the `WebMvcConfigurer` interface.

#### **Example: Adding a Custom XML Converter**

```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();
        XStreamMarshaller marshaller = new XStreamMarshaller();
        xmlConverter.setMarshaller(marshaller);
        xmlConverter.setUnmarshaller(marshaller);

        converters.add(xmlConverter);
        converters.add(new MappingJackson2HttpMessageConverter()); // JSON support
    }
}
```

**Explanation:**

* Adds custom XML marshaller using **XStream**.
* Retains JSON support with `MappingJackson2HttpMessageConverter`.

---

### **9. Spring Boot Auto-Configuration**

In **Spring Boot**, you don’t need to manually configure converters.
If libraries like **Jackson** or **JAXB** are on the classpath, Boot automatically registers their respective converters.

To add or modify converters, simply declare them as beans:

```java
@Bean
public HttpMessageConverter<Object> createXmlHttpMessageConverter() {
    return new MarshallingHttpMessageConverter(new XStreamMarshaller());
}
```

Spring Boot will detect and include it automatically.

---

### **10. Using RestTemplate with Message Converters**

HTTP Message Converters also work on the **client-side** with `RestTemplate`.

#### **Example: Consuming JSON**

```java
RestTemplate restTemplate = new RestTemplate();
restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

Foo foo = restTemplate.getForObject("http://localhost:8080/foos/1", Foo.class);
System.out.println(foo.getName());
```

#### **Example: Consuming XML**

```java
RestTemplate restTemplate = new RestTemplate();
restTemplate.getMessageConverters().add(new MarshallingHttpMessageConverter(new XStreamMarshaller()));

HttpHeaders headers = new HttpHeaders();
headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));

HttpEntity<String> entity = new HttpEntity<>(headers);
ResponseEntity<Foo> response = restTemplate.exchange(
    "http://localhost:8080/foos/1", HttpMethod.GET, entity, Foo.class);
```

---

### **11. Debugging Converter Usage**

When debugging a Spring MVC request, note that message converters are invoked inside:

* `RequestResponseBodyMethodProcessor.readWithMessageConverters()` — for request deserialization.
* `RequestResponseBodyMethodProcessor.writeWithMessageConverters()` — for response serialization.

If Spring cannot find a suitable converter for a given **Content-Type** or **Accept** header, it throws an:

> `HttpMediaTypeNotSupportedException`

---

### **12. Summary**

| **Aspect**             | **Explanation**                                      |
| ---------------------- | ---------------------------------------------------- |
| **Purpose**            | Convert Java objects to/from HTTP messages           |
| **Direction**          | Serialization (write) and Deserialization (read)     |
| **Selection**          | Based on `Content-Type` and `Accept` headers         |
| **Default Converters** | JSON, XML, text, byte array, form data               |
| **Customization**      | Via `WebMvcConfigurer` or custom `@Bean` definitions |
| **Client Use**         | Works seamlessly with `RestTemplate`                 |

---

### **13. Conclusion**

Spring’s **HTTP Message Converters** abstract away the complexity of converting data between client and server formats.
They enable **seamless integration** between RESTful APIs and clients, supporting multiple data formats and customizable serialization mechanisms.

By understanding and customizing message converters, developers can ensure efficient, flexible, and format-independent communication in Spring applications.

---
## **HttpMessageConverter flow**

