# Spring WebClient

---

# 1. Overview

In this tutorial, we examine **WebClient**, a **reactive web client introduced in Spring 5**.

We also examine **WebTestClient**, a WebClient designed specifically for use in tests.

WebClient represents the modern way of performing HTTP requests in Spring’s reactive ecosystem.

---

# 2. What Is the WebClient?

Simply put, **WebClient** is:

> An interface representing the main entry point for performing web requests.

It was created as part of the **Spring Web Reactive module** and is intended to replace the classic RestTemplate in reactive scenarios.

### Key Characteristics

* Reactive
* Non-blocking
* Works over HTTP/1.1
* Part of `spring-webflux`
* Supports both synchronous and asynchronous operations

Although it belongs to the reactive stack, it also works in **Servlet Stack applications**.

This is possible by **blocking the operation** to obtain the result. However:

* Blocking is not recommended in a Reactive Stack.

### Implementation

WebClient has a single implementation:

* `DefaultWebClient`

This is the class used internally when building a client instance.

---

# 3. Dependencies

Since we are using Spring Boot, we only need:

```
spring-boot-starter-webflux
```

This provides full Spring Framework Reactive Web support.

---

## 3.1 Maven Configuration

Add the following to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
    <version>3.5.7</version>
</dependency>
```

---

## 3.2 Gradle Configuration

Add the following to `build.gradle`:

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
}
```

---

# 4. Working with the WebClient

To work properly with WebClient, we need to understand how to:

1. Create an instance
2. Make a request
3. Handle the response

---

# 4.1 Creating a WebClient Instance

There are **three options**.

---

## Option 1: Default Settings

```java
WebClient client = WebClient.create();
```

Creates a client with default configuration.

---

## Option 2: With Base URI

```java
WebClient client = WebClient.create("http://localhost:8080");
```

All relative URIs will use this base URL.

---

## Option 3: Using Builder (Full Customization)

```java
WebClient client = WebClient.builder()
  .baseUrl("http://localhost:8080")
  .defaultCookie("cookieKey", "cookieValue")
  .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
  .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8080"))
  .build();
```

This is the most advanced and flexible option.

You can configure:

* Base URL
* Default cookies
* Default headers
* Default URI variables

---

# 4.2 Creating a WebClient Instance with Timeouts

The default HTTP timeout is 30 seconds. Often this is too slow.

To customize timeouts:

1. Create a custom HttpClient
2. Configure WebClient to use it

### Available Timeout Options

* Connection timeout → `ChannelOption.CONNECT_TIMEOUT_MILLIS`
* Read timeout → `ReadTimeoutHandler`
* Write timeout → `WriteTimeoutHandler`
* Response timeout → `responseTimeout`

### Example

```java
HttpClient httpClient = HttpClient.create()
  .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
  .responseTimeout(Duration.ofMillis(5000))
  .doOnConnected(conn ->
    conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
        .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

WebClient client = WebClient.builder()
  .clientConnector(new ReactorClientHttpConnector(httpClient))
  .build();
```

Important distinction:

Calling `timeout()` on the request:

* Is a signal timeout
* Applies to Mono/Flux publisher
* Is NOT an HTTP connection/read/write timeout

---

# 4.3 Preparing a Request – Define the Method

First, specify the HTTP method.

### Generic Method

```java
UriSpec<RequestBodySpec> uriSpec = client.method(HttpMethod.POST);
```

### Shortcut Methods

```java
UriSpec<RequestBodySpec> uriSpec = client.post();
```

Other shortcuts:

* get()
* delete()

Important:

Do not reuse request spec variables across different requests. They are references, and reusing them modifies prior definitions.

---

# 4.4 Preparing a Request – Define the URL

You must specify the URI.

### As String

```java
RequestBodySpec bodySpec = uriSpec.uri("/resource");
```

### Using UriBuilder

```java
RequestBodySpec bodySpec = uriSpec.uri(
  uriBuilder -> uriBuilder.pathSegment("/resource").build());
```

### As URI Instance

```java
RequestBodySpec bodySpec = uriSpec.uri(URI.create("/resource"));
```

If a base URL is defined, providing a full URI overrides it.

---

# 4.5 Preparing a Request – Define the Body

You can set:

* Body
* Content type
* Content length
* Cookies
* Headers

---

## Using bodyValue()

```java
RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue("data");
```

---

## Using Publisher (Mono / Flux)

```java
RequestHeadersSpec<?> headersSpec = bodySpec.body(
  Mono.just(new Foo("name")), Foo.class);
```

---

## Using BodyInserters

### From Simple Value

```java
RequestHeadersSpec<?> headersSpec = bodySpec.body(
  BodyInserters.fromValue("data"));
```

### From Publisher

```java
RequestHeadersSpec headersSpec = bodySpec.body(
  BodyInserters.fromPublisher(Mono.just("data")),
  String.class);
```

### Multipart Request

```java
LinkedMultiValueMap map = new LinkedMultiValueMap();
map.add("key1", "value1");
map.add("key2", "value2");

RequestHeadersSpec<?> headersSpec = bodySpec.body(
  BodyInserters.fromMultipartData(map));
```

---

### What Is BodyInserter?

A `BodyInserter`:

* Populates a `ReactiveHttpOutputMessage`
* Uses a given output message and context
* Is responsible for writing request body data

---

### What Is a Publisher?

A `Publisher`:

* A reactive component
* Emits potentially unbounded sequenced elements
* Is an interface

Most common implementations:

* Mono
* Flux

---

# 4.6 Preparing a Request – Define Headers

After defining the body, you can define:

* Headers
* Cookies
* Acceptable media types

Values are added to previously configured defaults.

There is built-in support for:

* If-None-Match
* If-Modified-Since
* Accept
* Accept-Charset

Example:

```java
ResponseSpec responseSpec = headersSpec.header(
    HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
  .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
  .acceptCharset(StandardCharsets.UTF_8)
  .ifNoneMatch("*")
  .ifModifiedSince(ZonedDateTime.now())
  .retrieve();
```

---

# 4.7 Getting a Response

There are two main approaches:

1. exchangeToMono / exchangeToFlux
2. retrieve

---

## exchangeToMono

Gives access to:

* ClientResponse
* Status
* Headers

```java
Mono<String> response = headersSpec.exchangeToMono(response -> {
  if (response.statusCode().equals(HttpStatus.OK)) {
      return response.bodyToMono(String.class);
  } else if (response.statusCode().is4xxClientError()) {
      return Mono.just("Error response");
  } else {
      return response.createException()
        .flatMap(Mono::error);
  }
});
```

This gives full control over status handling.

---

## retrieve()

Shortest path to get body:

```java
Mono<String> response = headersSpec.retrieve()
  .bodyToMono(String.class);
```

Important:

`bodyToMono()` throws a `WebClientException` if:

* 4xx status
* 5xx status

---

# 5. Working with WebTestClient

**WebTestClient** is the main entry point for testing WebFlux endpoints.

It:

* Has a very similar API to WebClient
* Delegates to an internal WebClient
* Focuses on providing a test context
* Has a single implementation: `DefaultWebTestClient`

It can bind to:

* Real server
* RouterFunction
* WebHandler
* ApplicationContext
* Controller

---

# 5.1 Binding to a Server

For full end-to-end integration tests:

```java
WebTestClient testClient = WebTestClient
  .bindToServer()
  .baseUrl("http://localhost:8080")
  .build();
```

---

# 5.2 Binding to a Router

```java
RouterFunction function = RouterFunctions.route(
  RequestPredicates.GET("/resource"),
  request -> ServerResponse.ok().build()
);

WebTestClient
  .bindToRouterFunction(function)
  .build().get().uri("/resource")
  .exchange()
  .expectStatus().isOk()
  .expectBody().isEmpty();
```

---

# 5.3 Binding to a Web Handler

```java
WebHandler handler = exchange -> Mono.empty();
WebTestClient.bindToWebHandler(handler).build();
```

---

# 5.4 Binding to ApplicationContext

Analyzes:

* Controller beans
* @EnableWebFlux configurations

```java
@Autowired
private ApplicationContext context;

WebTestClient testClient = WebTestClient
  .bindToApplicationContext(context)
  .build();
```

---

# 5.5 Binding to Controller

Shorter approach:

```java
@Autowired
private Controller controller;

WebTestClient testClient =
  WebTestClient.bindToController(controller).build();
```

---

# 5.6 Making a Request (WebTestClient)

After building, the API is similar to WebClient until `exchange()`.

After exchange:

You use:

* expectStatus
* expectHeader
* expectBody

Example:

```java
WebTestClient
  .bindToServer()
    .baseUrl("http://localhost:8080")
    .build()
    .post()
    .uri("/resource")
  .exchange()
    .expectStatus().isCreated()
    .expectHeader().valueEquals("Content-Type", "application/json")
    .expectBody().jsonPath("field").isEqualTo("value");
```

---

# 6. Conclusion

WebClient is:

* A reactive, non-blocking HTTP client
* Part of Spring WebFlux
* A replacement for RestTemplate in reactive scenarios
* Usable in both reactive and servlet-based applications

It provides:

* Flexible client configuration
* Custom timeout support
* Multiple request body strategies
* Advanced header configuration
* Full control via exchangeToMono
* Simpler retrieval via retrieve

WebTestClient extends WebClient concepts into testing, allowing:

* Real server integration tests
* Controller-level tests
* Functional endpoint tests
* ApplicationContext-based tests

Together, WebClient and WebTestClient form the modern client-side and testing foundation for reactive Spring applications.

---