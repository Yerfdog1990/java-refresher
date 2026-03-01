# End-To-End API Tests – WebTestClient

---

# 1. Goal

In this lesson, we learn:

* What API tests are
* Why they are essential in RESTful services
* How to implement **End-to-End API tests**
* The difference between **Live tests** and **Integration tests**
* How to use **Spring’s WebTestClient** to implement both

We also briefly review alternative libraries before focusing on WebTestClient.

---

# 2. What Are API Tests?

In a RESTful service, **API design is crucial** because it defines how systems communicate.

Therefore, we must validate that the API:

* Fulfills expected functionality
* Is reliable
* Performs correctly
* Enforces security expectations

A common and effective strategy is to use:

> **End-to-End (E2E) tests**

These tests:

* Require a running service
* Consume the API as an external client would
* Do not bypass or mock any layer
* Validate the full HTTP request/response cycle

They simulate the final behavior of the system as it would run in production.

---

# 2.2 API Testing Alternatives

Popular API testing libraries include:

* **REST Assured** – simple DSL for testing REST APIs
* **Spring TestRestTemplate** – synchronous HTTP client
* **Spring WebTestClient** – non-blocking, reactive HTTP client for testing

In this lesson, we use:

> **Spring WebTestClient**

Even though our project is based on a **Servlet synchronous stack**, Spring encourages using:

* WebClient
* WebTestClient

Because:

* They support blocking usage
* They work perfectly with Servlet-based applications
* They provide a modern and fluent testing API

---

# 2.3 Introducing WebTestClient

WebTestClient:

* Uses WebClient internally
* Provides convenience methods for different testing scenarios
* Supports multiple server binding strategies

---

## Required Dependencies

WebTestClient is already available via:

```xml
spring-boot-starter-test
```

Because it pulls in:

```xml
spring-test
```

However, we must also include:

```xml
spring-boot-starter-webflux
```

Only for testing support.

### pom.xml Configuration

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
    <scope>test</scope>
</dependency>
```

Important:

* WebFlux is added with `test` scope
* We do not use reactive logic in the main application
* It is required only for WebTestClient support

---

## Declaring WebTestClient

In:

```
com.baeldung.rwsb.endtoend.CampaignEndToEndApiTest
```

Start with:

```java
public class CampaignEndToEndApiTest {
    
    WebTestClient webClient;

}
```

Now let’s examine the testing strategies.

---

# 2.4 Live API Tests

## What Are Live Tests?

Live tests:

* Target a running instance
* Are independent of test lifecycle
* Require manual service startup
* Can point to local or production servers

---

## Step 1: Start the Application

Run:

* `RwsbApp` class from IDE
  OR
* `mvn spring-boot:run`

Application runs on:

```
http://localhost:8080
```

---

## Step 2: Create Test

```java
WebTestClient webClient = WebTestClient.bindToServer()
    .baseUrl("http://localhost:8080")
    .build();

@Test
void givenRunningService_whenGetSingleCampaign_thenExpectStatus() {
    webClient.get()
      .uri("/campaigns/3")
      .exchange()
      .expectStatus()
      .isOk();
}
```

---

## What Happens Here?

1. `bindToServer()` → connect to live server
2. `baseUrl()` → specify target URL
3. `.get()` → HTTP GET method
4. `.uri("/campaigns/3")` → endpoint
5. `.exchange()` → execute request
6. `.expectStatus().isOk()` → verify 200 OK

This validates:

* Endpoint exists
* Endpoint is reachable
* Endpoint responds correctly

---

## Limitations of Live Tests

* Require manual service startup
* Not ideal for CI/CD environments
* Depend on external instance

We need a better approach.

---

# 2.5 End-to-End Integration API Tests

Goal:

* Automatically start application
* Avoid manual setup
* Keep full end-to-end behavior

Spring Boot provides this support.

---

## Updated Test

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CampaignEndToEndApiTest {

    @LocalServerPort
    int port;

    @Test
    void givenRunningService_whenGetSingleCampaign_thenExpectStatus() {

        WebTestClient webClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .build();

        webClient.get()
            .uri("/campaigns/3")
            .exchange()
            .expectStatus()
            .isOk();
    }
}
```

---

## What’s Happening?

### @SpringBootTest

* Starts full Spring context
* Boots embedded server

### webEnvironment = RANDOM_PORT

* Starts server on random available port
* Prevents port conflicts

### @LocalServerPort

* Injects actual running port
* Used to build dynamic base URL

### Why WebClient Initialization Inside Test?

Because:

* The port is injected after context startup
* We must wait for Boot to assign the port

Now:

* No manual startup required
* Fully automated
* CI-friendly

---

## Integration vs Live Test

| Live Test             | Integration Test          |
| --------------------- | ------------------------- |
| Manual startup        | Automatic startup         |
| External instance     | Boot-managed instance     |
| Independent lifecycle | Managed by test lifecycle |

Even though we’re still testing a live instance:

It is created **ad hoc**, so it qualifies as an **Integration Test**.

---

## Performance Note

Integration tests:

* Take longer
* Must initialize full Spring context
* Start embedded server

This overhead is expected.

---

# 2.6 Spring Boot WebTestClient Autoconfiguration

Spring Boot provides an additional simplification.

When using:

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
```

Spring Boot:

* Automatically configures a WebTestClient
* Points it to the assigned random port
* Registers it in the context

So we can simply autowire it.

---

## Final Simplified Version

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CampaignEndToEndApiTest {

    @Autowired
    WebTestClient webClient;

    @Test
    void givenRunningService_whenGetSingleCampaign_thenExpectStatus() {

        webClient.get()
            .uri("/campaigns/3")
            .exchange()
            .expectStatus()
            .isOk();
    }
}
```

---

## What Changed?

We removed:

* Manual bindToServer()
* Manual baseUrl()
* @LocalServerPort
* Dynamic URL construction

Spring Boot handles:

* Server startup
* Random port assignment
* WebTestClient configuration

This is:

* Clean
* Minimal
* Production-grade
* CI-ready

---

# Summary of Approaches

### 1. Live API Test

* Manually run app
* Use bindToServer()
* Connect to fixed URL

### 2. Integration Test (Manual WebClient Setup)

* Use @SpringBootTest
* Use RANDOM_PORT
* Inject port
* Create WebTestClient manually

### 3. Integration Test (Autoconfigured WebTestClient)

* Use @SpringBootTest
* Autowire WebTestClient
* Simplest and recommended approach

---

# 2.7 Conclusion

In this lesson, we:

* Defined what API tests are
* Explained why End-to-End tests are critical
* Compared API testing alternatives
* Implemented Live API tests
* Implemented Integration End-to-End tests
* Leveraged Spring Boot WebTestClient autoconfiguration

We also analyzed:

* How Spring Boot manages the test lifecycle
* How RANDOM_PORT works
* How WebTestClient integrates with Spring context

WebTestClient provides:

* Fluent API
* Reactive and blocking support
* Clean integration with Spring Boot
* Powerful end-to-end validation capabilities

In future lessons, we can explore:

* Advanced response assertions
* Body validation
* JSONPath usage
* Header verification
* Error handling validation

WebTestClient is the modern, recommended approach for End-to-End API testing in Spring applications.

---