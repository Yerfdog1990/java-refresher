# A Custom Media Type for a Spring REST API

---

## 1. Overview

Custom media types in Spring REST allow you to control exactly what representation format each endpoint produces and consumes. A primary use case is **API versioning** — instead of versioning the URI, you version the media type, keeping the URL stable while allowing representations to evolve independently.

The client signals which version it wants via the `Accept` request header. Spring routes to the correct handler method based on the `produces` parameter in `@RequestMapping`.

---

## 2. API — Version 1

### Custom media type format

Custom vendor media types follow the convention:

```
application/vnd.{name}.api.v{version}+json
```

For example: `application/vnd.baeldung.api.v1+json`

### The Resource (V1)

A simple resource with a single field — `itemId`:

```java
public class BaeldungItem {
    private String itemId;

    // standard getters and setters
}
```

### The Endpoint (V1)

The `produces` parameter on `@RequestMapping` declares which media type this method handles. Spring will route requests with a matching `Accept` header to this method:

```java
@RequestMapping(
  method = RequestMethod.GET,
  value = "/public/api/items/{id}",
  produces = "application/vnd.baeldung.api.v1+json"
)
@ResponseBody
public BaeldungItem getItem(@PathVariable("id") String id) {
    return new BaeldungItem("itemId1");
}
```

### Integration Test (V1)

The test sets `Accept: application/vnd.baeldung.api.v1+json` and expects a `200 OK` with JSON content:

```java
@Test
public void givenServiceEndpoint_whenGetRequestFirstAPIVersion_then200() {
    given()
      .accept("application/vnd.baeldung.api.v1+json")
    .when()
      .get(URL_PREFIX + "/public/api/items/1")
    .then()
      .contentType(ContentType.JSON).and().statusCode(200);
}
```

---

## 3. API — Version 2 (Breaking Change)

### Why a new version is needed

The V1 resource exposed a raw `itemId`. The new requirement is to hide the raw ID and expose a `name` instead — giving more flexibility. This is a **breaking change** because existing clients depend on the `itemId` field.

### The Resource (V2)

A new resource class with `itemName` replaces `itemId`:

```java
public class BaeldungItemV2 {
    private String itemName;

    // standard getters and setters
}
```

### The Endpoint (V2)

A new method is added to the same controller, at the **same URI**, but with a different `produces` value:

```java
@RequestMapping(
  method = RequestMethod.GET,
  value = "/public/api/items/{id}",
  produces = "application/vnd.baeldung.api.v2+json"
)
@ResponseBody
public BaeldungItemV2 getItemSecondAPIVersion(@PathVariable("id") String id) {
    return new BaeldungItemV2("itemName");
}
```

### How Spring routes between versions

Spring uses the `Accept` header sent by the client to determine which method to invoke:

| Client `Accept` header | Spring invokes | Response contains |
|---|---|---|
| `application/vnd.baeldung.api.v1+json` | `getItem()` | `{ "itemId": "itemId1" }` |
| `application/vnd.baeldung.api.v2+json` | `getItemSecondAPIVersion()` | `{ "itemName": "itemName" }` |

The URI `/public/api/items/{id}` **never changes** — only the media type in the `Accept` header changes.

### Integration Test (V2)

Identical structure to the V1 test, with the updated `Accept` header:

```java
@Test
public void givenServiceEndpoint_whenGetRequestSecondAPIVersion_then200() {
    given()
      .accept("application/vnd.baeldung.api.v2+json")
    .when()
      .get(URL_PREFIX + "/public/api/items/2")
    .then()
      .contentType(ContentType.JSON).and().statusCode(200);
}
```

---

## 4. Applying a Custom Media Type at Class Level

`@RequestMapping` works at the class level too, allowing a default `produces` (and `consumes`) to be set for all methods in the controller. Individual methods can still override this if needed:

```java
@RestController
@RequestMapping(
  value = "/",
  produces = "application/vnd.baeldung.api.v1+json"
)
public class CustomMediaTypeController {
    // all methods inherit produces = "application/vnd.baeldung.api.v1+json"
    // unless individually overridden
}
```

### Parameters available in `@RequestMapping`

| Parameter | Purpose |
|---|---|
| `value` | The URI path this controller/method handles |
| `produces` | The media type(s) this controller/method can return |
| `consumes` | The media type(s) this controller/method accepts in the request body |

---

## 5. Key Concepts Summary

### Custom media type naming convention
```
application/vnd.{vendor}.api.v{n}+json
```
- `vnd` — vendor prefix (standard MIME convention)
- `{vendor}` — your application or company name
- `v{n}` — version number
- `+json` — the underlying format (JSON)

### How versioning via media type works in Spring

1. Client sets `Accept` header to the desired media type version
2. Spring matches the `Accept` header against the `produces` value of each `@RequestMapping` method
3. The matching method is invoked and returns the corresponding resource representation
4. The response includes the `Content-Type` header confirming which version was served

### Advantages of this approach over URI versioning

- The **URI space remains stable** — no `/v1/`, `/v2/` path proliferation
- Individual resources can be versioned independently — no need to fork the entire API
- Aligns with REST principles — the media type, not the URL, is the contract
- Old and new versions coexist in the same controller cleanly

---

## 6. Full Version Comparison

| | V1 | V2 |
|---|---|---|
| Media type | `application/vnd.baeldung.api.v1+json` | `application/vnd.baeldung.api.v2+json` |
| URI | `/public/api/items/{id}` | `/public/api/items/{id}` (unchanged) |
| Resource class | `BaeldungItem` | `BaeldungItemV2` |
| Field exposed | `itemId` | `itemName` |
| Breaking change? | — | Yes — `itemId` removed, `itemName` added |

---