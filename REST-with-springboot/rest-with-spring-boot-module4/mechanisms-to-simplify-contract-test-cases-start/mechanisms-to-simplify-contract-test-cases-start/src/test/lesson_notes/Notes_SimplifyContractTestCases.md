
---

# Mechanisms to Simplify Contract Test Cases

## 1. Goal

The main goal of this lesson is to **simplify contract tests** by extracting repeated logic and creating **generic helper utilities** that can test **any API endpoint** more efficiently.

When writing contract tests, developers often repeat the same patterns:

* Creating HTTP requests
* Setting headers
* Sending JSON bodies
* Checking HTTP response status
* Validating response fields

To reduce this repetition, we introduce **common reusable mechanisms** that abstract these operations.

The solution involves:

* Extracting **common testing procedures**
* Creating **generic helper classes**
* Separating **test logic from reusable utilities**

This results in:

* **Cleaner test cases**
* **Less boilerplate code**
* **Better maintainability**

---

# 2. Project Structure

## 2.1 Modularized Structure

The project is **modularized** to separate:

1. **Application and test logic**
2. **Common reusable utilities**

This allows shared functionality to be reused across different contract tests.

The structure separates:

* **Main test logic**
* **Commons functionality**

The **commons module** contains reusable classes for simplifying contract tests.

---

## Commons Classes Introduced

The following classes are provided as part of the solution:

### 1. `SimpleContractBodyContentSpec`

A **facade class** for:

```
WebTestClient.BodyContentSpec
```

Purpose:

* Provide **simplified contract-based assertions**
* Reduce complexity when validating response bodies.

---

### 2. `SimpleContractWebTestClient`

A **facade wrapper** around:

```
WebTestClient
```

Purpose:

* Simplify HTTP request execution
* Standardize common operations like **create requests**

---

### 3. `SimpleRequestBodyBuilder`

A **utility class** used to:

* Construct **JSON request bodies**
* Modify JSON fields dynamically for tests.

---

These classes will be implemented step by step to gradually simplify contract tests.

---

# 2.2 Simplifying the WebTestClient

Consider the following **contract test**:

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ContractTaskApiIntegrationTest {

    @Autowired
    WebTestClient webClient;

    @Value("classpath:task.json")
    Resource resource;

    @Test
    void whenCreateNewTask_thenSuccess() throws Exception {
        String taskJson = generateTaskJson();

        webClient.post()
            .uri("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(taskJson)
            .exchange()
            .expectStatus()
            .isCreated();
    }

    // …

    private String generateTaskJson() throws Exception {
        Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
        return FileCopyUtils.copyToString(reader);
    }
}
```

### What this test does

It performs a **POST request** to create a new task and verifies that:

```
HTTP status = 201 CREATED
```

However, the test contains **boilerplate logic**:

* Constructing request
* Setting content type
* Sending JSON body
* Verifying status

This logic will appear **in many tests**, so it should be extracted.

---

# Creating a Simplified WebTestClient

We create a helper class:

```
SimpleContractWebTestClient
```

Implementation:

```java
@Lazy
@Component
public class SimpleContractWebTestClient {

    WebTestClient webClient;

    public SimpleContractWebTestClient(WebTestClient webClient) {
        super();
        this.webClient = webClient;
    }

    public void create(String url, String jsonBody) {
        webClient.post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonBody)
            .exchange()
            .expectStatus()
            .isCreated();
    }
}
```

---

## Why use `@Lazy`?

The class needs to:

* Inject the **framework-provided WebTestClient**
* Ensure it is **initialized before injection**

`@Lazy` solves the **bean initialization order problem**.

---

## What the `create()` method does

The method performs:

1. **POST request**
2. Sets **JSON content type**
3. Sends **request body**
4. Executes the request
5. Verifies **201 CREATED**

This is the **standard behavior for "create" operations**, so it removes repeated code in tests.

---

# Using the Simplified Client

Create a new package:

```
com.baeldung.rwsb.contract.simplified
```

Add a test class:

```
ContractTaskSimplifiedApiIntegrationTest
```

Implementation:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContractTaskSimplifiedApiIntegrationTest {

    @Autowired
    SimpleContractWebTestClient webClient;

    @Value("classpath:task.json")
    Resource resource;

    @Test
    void whenCreateNewTask_thenSuccess() throws Exception {
        webClient.create("/tasks", generateTaskJson());
    }

    private String generateTaskJson() throws Exception {
        Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
        return FileCopyUtils.copyToString(reader);
    }
}
```

The test becomes **much simpler**.

---

# 2.3 Asserting the Response Body

In contract tests, the method:

```
expectBody()
```

returns:

```
WebTestClient.BodyContentSpec
```

This object is used to:

* Validate **JSON responses**
* Apply **jsonPath assertions**

Instead of using the raw API, we introduce a **facade wrapper**:

```
SimpleContractBodyContentSpec
```

---

# Implementing SimpleContractBodyContentSpec

```java
public class SimpleContractBodyContentSpec {

    private WebTestClient.BodyContentSpec contentBodySpec;

    public SimpleContractBodyContentSpec(WebTestClient.BodyContentSpec contentBodySpec) {
        super();
        this.contentBodySpec = contentBodySpec;
    }

    public SimpleContractBodyContentSpec containsFields(String... fields) {
        Stream.of(fields)
            .forEach(field -> contentBodySpec.jsonPath("$.%s".formatted(field))
                .isNotEmpty());
        return this;
    }

    @SafeVarargs
    public final SimpleContractBodyContentSpec fieldsMatch(Map.Entry<String, Matcher<?>>... fields) {
        Stream.of(fields)
            .forEach(field -> contentBodySpec.jsonPath("$.%s".formatted(field.getKey()))
                .value(field.getValue()));
        return this;
    }

}
```

---

# How This Works

Instead of writing assertions individually:

```
jsonPath("$.id").isNotEmpty()
jsonPath("$.name").isNotEmpty()
jsonPath("$.campaignId").isNotEmpty()
```

We can simply pass fields as parameters.

---

## containsFields()

Checks that fields exist in response.

Example:

```
.containsFields("id", "name", "campaignId")
```

---

## fieldsMatch()

Validates field values using **Hamcrest matchers**.

Example:

```
fieldsMatch(
    Map.entry("name", CoreMatchers.equalTo("Test")),
    Map.entry("campaignId", Matchers.greaterThan(0))
)
```

---

## Fluent API Design

Both methods return:

```
SimpleContractBodyContentSpec
```

This allows chaining:

```
.containsFields(...).fieldsMatch(...)
```

---

# Updating the Client

Modify the `create()` method to return the facade.

```java
public SimpleContractBodyContentSpec create(String url, String jsonBody) {
    return new SimpleContractBodyContentSpec(webClient.post()
        .uri(url)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(jsonBody)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody());
}
```

---

# Example Test Using the Facade

```java
@Test
void whenCreateNewTask_thenSuccessWithExpectedFields() throws Exception {
    String taskJson = generateTaskJson();

    webClient.create("/tasks", taskJson)
        .containsFields("id", "name", "campaignId", "dueDate")
        .fieldsMatch(
            Map.entry("name", CoreMatchers.equalTo("Test - New Task 1")),
            Map.entry("campaignId", Matchers.greaterThan(0)),
            Map.entry("dueDate", equalTo("2050-12-30")));
}
```

Benefits:

* Simpler assertions
* Cleaner tests
* Dynamic validation

---

# 2.4 Defining the Request Body

Previously, all requests used the **same static JSON input**.

However, real tests require:

* Changing fields dynamically
* Customizing request inputs

To solve this, we introduce:

```
SimpleRequestBodyBuilder
```

---

# Implementing SimpleRequestBodyBuilder

```java
public class SimpleRequestBodyBuilder {

    private final ObjectNode jsonNodeTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    private SimpleRequestBodyBuilder(String jsonTemplate)
                    throws JsonMappingException, JsonProcessingException {
        super();
        this.jsonNodeTemplate = (ObjectNode) mapper.readTree(jsonTemplate);
    }

    public static SimpleRequestBodyBuilder fromResource(Resource inputJsonFile) throws IOException {
        String jsonTemplate = readResource(inputJsonFile);
        return new SimpleRequestBodyBuilder(jsonTemplate);
    }

    public SimpleRequestBodyBuilder with(String field, String value) throws IOException {
        JsonNode nodeValue = TextNode.valueOf(value);
        this.jsonNodeTemplate.set(field, nodeValue);
        return this;
    }

    private static String readResource(Resource resource) {
        try {
            Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String build() {
        return jsonNodeTemplate.toString();
    }
}
```

---

# How the Builder Works

### Step 1: Load JSON template

```
fromResource()
```

### Step 2: Modify fields

```
with(field, value)
```

### Step 3: Build JSON

```
build()
```

---

# Example Usage

Test the **validation constraint** for blank task name.

```java
@Test
void whenCreateNewTaskWithBlankName_thenBadRequest() throws Exception {
    String taskJson = fromResource(this.resource)
        .with("name", "")
        .build();

    webClient.create("/tasks", taskJson);
}
```

However, this fails because:

```
create() expects 201 CREATED
```

But the response should be:

```
400 BAD REQUEST
```

---

# 2.5 Flexible HTTP Method and Response Status

To support **different response statuses**, we implement a flexible request method.

```java
public SimpleContractBodyContentSpec requestWithResponseStatus(String url, HttpMethod method,
                                        String jsonBody, HttpStatus responseStatus) {
    return new SimpleContractBodyContentSpec(webClient.method(method)
        .uri(url)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(jsonBody)
        .exchange()
        .expectStatus()
        .isEqualTo(responseStatus)
        .expectBody());
}
```

---

# Updated Test

```java
@Test
void whenCreateNewTaskWithBlankName_thenBadRequest() throws Exception {
    String taskJson = fromResource(this.resource)
        .with("name", "")
        .build();

    webClient.requestWithResponseStatus("/tasks", HttpMethod.POST, taskJson, HttpStatus.BAD_REQUEST);
}
```

Now the test **passes correctly**.

---

# 2.6 Conclusion

This solution significantly simplifies **contract testing with WebTestClient**.

Key improvements:

### 1. Reduced Boilerplate

Common HTTP request logic extracted into:

```
SimpleContractWebTestClient
```

---

### 2. Simplified Assertions

Response validation simplified with:

```
SimpleContractBodyContentSpec
```

---

### 3. Flexible Request Inputs

Dynamic JSON request generation via:

```
SimpleRequestBodyBuilder
```

---

# Possible Future Improvements

The solution can be extended further to support additional scenarios:

### Additional API Operations

Support testing:

* GET
* PUT / UPDATE
* DELETE

---

### JSON Collection Assertions

Add support for validating:

```
JSON arrays
```

---

### Extract Response Objects

Allow converting response body to objects, for example:

```
Task task = responseBodyToObject()
```

This would allow using generated fields like:

```
task.id
```

in subsequent requests.

---

### Advanced Request Builder Features

Enhance request builder to support:

* Setting fields to **null**
* Setting **nested JSON objects**
* Generating **random values**
* Adding **JSON collections**

---

✔ **Final Result**

The mechanisms introduced:

* Improve **readability**
* Promote **reusability**
* Simplify **contract test implementation**

while still leveraging the full power of **Spring WebTestClient**.

---

