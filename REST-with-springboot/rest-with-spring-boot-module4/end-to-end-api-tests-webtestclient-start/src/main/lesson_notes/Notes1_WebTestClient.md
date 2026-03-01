# Spring WebTestClient

## 1. Overview

**WebTestClient** is an HTTP client designed specifically for **testing server applications**.

It:

* Wraps Spring’s WebClient
* Uses WebClient internally to perform requests
* Exposes a **testing facade** for verifying responses

WebTestClient can be used to:

* Perform **end-to-end HTTP tests**
* Test **Spring MVC** and **Spring WebFlux** applications
* Test applications **without a running server** via mock request and response objects

It supports both:

* Real HTTP communication (live server)
* Mock-based testing (no server required)

---

# Setup

To set up a WebTestClient you must **choose a server setup to bind to**.

Available setup options:

1. Bind to Controller
2. Bind to ApplicationContext
3. Bind to Router Function
4. Bind to Server

Each option determines how requests are handled.

---

# 1. Bind to Controller

This setup allows you to:

* Test specific controller(s)
* Use mock request and response objects
* Avoid starting a server

---

## WebFlux – Bind to Controller

For WebFlux applications:

* Loads infrastructure equivalent to WebFlux Java config
* Registers the given controller(s)
* Creates a WebHandler chain to handle requests

```java
WebTestClient client =
        WebTestClient.bindToController(new TestController()).build();
```

---

## Spring MVC – Bind to Controller

For Spring MVC:

* Delegates to StandaloneMockMvcBuilder
* Loads infrastructure equivalent to WebMvc Java config
* Registers the given controller(s)
* Creates an instance of MockMvc to handle requests

```java
WebTestClient client =
        MockMvcWebTestClient.bindToController(new TestController()).build();
```

---

# 2. Bind to ApplicationContext

This setup allows you to:

* Load full Spring configuration
* Include Spring MVC or WebFlux infrastructure
* Include controller declarations
* Handle requests via mock request/response objects
* Avoid running a real server

---

## WebFlux – Bind to ApplicationContext

Here:

* The Spring ApplicationContext is passed to WebHttpHandlerBuilder
* A WebHandler chain is created

```java
@SpringJUnitConfig(WebConfig.class)
class MyTests {

    WebTestClient client;

    @BeforeEach
    void setUp(ApplicationContext context) {
        client = WebTestClient.bindToApplicationContext(context).build();
    }
}
```

Steps involved:

* Specify the configuration to load
* Inject the configuration
* Create the WebTestClient

---

## Spring MVC – Bind to ApplicationContext

Here:

* ApplicationContext is passed to MockMvcBuilders.webAppContextSetup
* A MockMvc instance is created to handle requests

```java
@ExtendWith(SpringExtension.class)
@WebAppConfiguration("classpath:META-INF/web-resources")
@ContextHierarchy({
    @ContextConfiguration(classes = RootConfig.class),
    @ContextConfiguration(classes = WebConfig.class)
})
class MyTests {

    @Autowired
    WebApplicationContext wac;

    WebTestClient client;

    @BeforeEach
    void setUp() {
        client = MockMvcWebTestClient.bindToApplicationContext(this.wac).build();
    }
}
```

Steps:

* Specify configuration to load
* Inject configuration
* Create WebTestClient

---

# 3. Bind to Router Function

This setup allows you to:

* Test functional endpoints
* Use mock request and response objects
* Avoid running a server

---

## WebFlux – Functional Endpoints

Delegates to `RouterFunctions.toWebHandler`:

```java
RouterFunction<?> route = ...
client = WebTestClient.bindToRouterFunction(route).build();
```

---

## Spring MVC

Currently, there are **no options** to test WebMvc functional endpoints using this approach.

---

# 4. Bind to Server (Live Server)

This setup:

* Connects to a running server
* Performs full end-to-end HTTP tests

```java
client = WebTestClient.bindToServer()
        .baseUrl("http://localhost:8080")
        .build();
```

Use this when you want real HTTP communication.

---

# Client Configuration

Besides server setup, you can configure:

* Base URL
* Default headers
* Client filters
* Other client options

Options available:

* Directly after `bindToServer()`
* Or via `configureClient()` for other setups

Example:

```java
client = WebTestClient.bindToController(new TestController())
        .configureClient()
        .baseUrl("/test")
        .apiVersionInserter(ApiVersionInserter.fromHeader("API-Version").build())
        .build();
```

Important:

* `configureClient()` transitions from server configuration to client configuration.

---

# Writing Tests

WebClient and WebTestClient share the **same API up to `exchange()`**.

After `exchange()`:

WebTestClient provides two verification approaches:

1. Built-in Assertions (chained expectations)
2. AssertJ Integration (assertThat style)

You can prepare requests with:

* JSON
* Form data
* Multipart data
* Any content supported by WebClient

---

# Built-in Assertions

## Status and Headers

```java
client.get().uri("/persons/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON);
```

---

## expectAll – Soft Assertions

To assert all expectations even if one fails:

```java
client.get().uri("/persons/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(
            spec -> spec.expectStatus().isOk(),
            spec -> spec.expectHeader().contentType(MediaType.APPLICATION_JSON)
        );
```

Similar to:

* AssertJ soft assertions
* JUnit Jupiter assertAll()

---

# Decoding the Response Body

Options:

* `expectBody(Class<T>)` → Decode to single object
* `expectBodyList(Class<T>)` → Decode to List<T>
* `expectBody()` → Decode to byte[] or empty body

Example:

```java
client.get().uri("/persons")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Person.class)
        .hasSize(3)
        .contains(person);
```

---

## Custom Assertions

```java
client.get().uri("/persons/1")
        .exchange()
        .expectStatus().isOk()
        .expectBody(Person.class)
        .consumeWith(result -> {
            // custom assertions (e.g. AssertJ)
        });
```

---

## Returning Result

```java
EntityExchangeResult<Person> result =
        client.get().uri("/persons/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Person.class)
                .returnResult();
```

For generics, use overloaded methods with `ParameterizedTypeReference`.

---

# No Content Responses

If no content is expected:

```java
client.post().uri("/persons")
        .body(personMono, Person.class)
        .exchange()
        .expectStatus().isCreated()
        .expectBody().isEmpty();
```

To ignore response content:

```java
client.get().uri("/persons/123")
        .exchange()
        .expectStatus().isNotFound()
        .expectBody(Void.class);
```

---

# JSON Content Assertions

## Full JSON Verification

```java
client.get().uri("/persons/1")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .json("{\"name\":\"Jane\"}");
```

Uses JSONAssert.

---

## JSONPath Assertions

```java
client.get().uri("/persons")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$[0].name").isEqualTo("Jane")
        .jsonPath("$[1].name").isEqualTo("Jason");
```

---

# Streaming Responses

For infinite streams such as:

* text/event-stream
* application/x-ndjson

Start by verifying status and headers:

```java
FluxExchangeResult<MyEvent> result =
        client.get().uri("/events")
                .accept(TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(MyEvent.class);
```

Then consume with StepVerifier (from reactor-test):

```java
Flux<Event> eventFlux = result.getResponseBody();

StepVerifier.create(eventFlux)
        .expectNext(person)
        .expectNextCount(4)
        .consumeNextWith(p -> ...)
        .thenCancel()
        .verify();
```

---

# AssertJ Integration

WebTestClientResponse is the main entry point.

It:

* Is an AssertProvider
* Wraps the ResponseSpec
* Enables assertThat() style assertions

Example:

```java
ResponseSpec spec = client.get().uri("/persons").exchange();

WebTestClientResponse response = WebTestClientResponse.from(spec);

assertThat(response).hasStatusOk();
assertThat(response)
        .hasContentTypeCompatibleWith(MediaType.TEXT_PLAIN);
```

---

## Using Built-in Workflow Then AssertJ

```java
ExchangeResult result =
        client.get().uri("/persons")
                .exchange()
                .returnResult();

WebTestClientResponse response =
        WebTestClientResponse.from(result);

assertThat(response).hasStatusOk();
assertThat(response)
        .hasContentTypeCompatibleWith(MediaType.TEXT_PLAIN);
```

---

# MockMvc Assertions (Spring MVC Only)

WebTestClient as an HTTP client can verify only:

* Status
* Headers
* Body

When using a Spring MVC MockMvc server setup, you can also assert **server-side state**.

---

## Obtain ExchangeResult

For response with body:

```java
EntityExchangeResult<Person> result =
        client.get().uri("/persons/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Person.class)
                .returnResult();
```

For response without body:

```java
EntityExchangeResult<Void> result =
        client.get().uri("/path")
                .exchange()
                .expectBody().isEmpty();
```

---

## Switch to MockMvc Assertions

```java
MockMvcWebTestClient.resultActionsFor(result)
        .andExpect(model().attribute("integer", 3))
        .andExpect(model().attribute("string", "a string value"));
```

This enables:

* Assertions on model attributes
* Deeper server-side verification

---

# Summary

WebTestClient:

* Wraps WebClient
* Provides a testing-focused API
* Supports both WebFlux and Spring MVC
* Works with or without a running server
* Supports controller, context, router, and live server setups
* Provides built-in assertions
* Integrates with AssertJ
* Supports streaming verification
* Allows MockMvc server-side assertions in MVC setups

It is the unified HTTP testing tool for modern Spring applications.

---