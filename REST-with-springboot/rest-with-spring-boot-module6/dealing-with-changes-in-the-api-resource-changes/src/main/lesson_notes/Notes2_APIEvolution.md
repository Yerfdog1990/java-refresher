# API Evolution for REST/HTTP APIs

*Phil Sturgeon · May 2, 2018*

---

## What is API Evolution?

![img.png](img.png)

API evolution is the practice of maintaining the "I" in API — keeping request/response bodies, query parameters, and general functionality stable, only breaking them when absolutely necessary. The core philosophy is that **API developers bending over backwards to maintain a contract** is often more financially and logistically viable than dumping the workload onto a wide array of clients.

When change truly cannot be avoided, evolution calls for:
- Providing sensible warnings to affected clients
- Not disturbing unaffected clients
- Giving clients adequate time to migrate

> Evolution is not new — REST advocates have recommended it for decades. GraphQL and gRPC have recently brought it back into the spotlight.

---

## Strategies for Common Change Scenarios

### 1. Splitting a property (e.g. `name` → `first_name` + `last_name`)

Add the new properties alongside the old one. The serializer outputs all three, and the API accepts either format on write:

```ruby
class UserSerializer
  include FastJsonapi::ObjectSerializer

  attributes :name, :first_name, :last_name

  attribute :name do |object|
    "#{object.first_name} #{object.last_name}"
  end
end
```

- Old clients sending `name` → server converts it
- New clients sending `first_name` + `last_name` → picked up automatically
- Nothing breaks for existing integrations

---

### 2. Changing a value format (e.g. `price` in dollars → `price_micros`)

Don't replace the old property — **add a new one**. Renaming to something like `price_micros` (or `amount`) avoids breaking existing clients.

- Clients can send either `price` (server converts) or `price_micros`
- For unsupported currencies on the old property, return a clear error pointing to the new one
- This avoids the disaster of charging $1,000,000 for something that should cost $1

**Rule:** never silently change the meaning of a value — add a new property with the new semantics.

---

### 3. Removing old/deprecated properties

Options for communicating deprecation:

| Method | Notes |
|---|---|
| OpenAPI `deprecated: true` | Human-readable docs; clients can't detect it programmatically |
| JSON Schema `deprecated` keyword | Can pair with a smart SDK to raise warnings when a deprecated field is accessed at runtime or compile time |
| GraphQL type system | Best-in-class here — knows exactly which clients are requesting deprecated fields |

**Practical reality:** removing old properties is rarely urgent. New developers write new integrations against current docs, and a developer newsletter or changelog is usually enough.

---

### 4. Fundamental concept changes (e.g. `matches` → `riders`)

When a whole resource concept is wrong — not just a property — create a **new endpoint with a new name**.

**Example:** A carpooling app had `/matches` (one driver + one passenger). When multi-driver carpools were needed, the concept was fundamentally broken.

**Solution:**
- Create `/riders` with a `status` property (`pending`, `active`, `inactive`, `blocked`, etc.)
- `/riders` maps to the same underlying database — the API layer does the translation
- Old clients continue using `/matches`; new clients use `/riders`
- Internally, refactoring happens gradually and is validated by contract tests

**Handling edge cases during transition:** When old endpoint logic doesn't fit new data (e.g. a match with multiple drivers), apply simple rules — expose only the first match to older clients. If accepted, great; if rejected, show the next. Ingenuity bridges the gap.

> This is essentially the same as `/v1/matches` vs `/v2/matches`, but skips the entire quagmire of choosing between global, resource, or method versioning.

---

### 5. Deprecating whole endpoints — the `Sunset` header

Use the **`Sunset` HTTP response header** to signal when an endpoint will be removed:

```
Sunset: Sat, 31 Dec 2018 23:59:59 GMT
```

Can be combined with a `Link` header pointing to documentation, OpenAPI/JSON Schema definitions, or a blog post:

```
Link: <https://example.com/docs/new-endpoint>; rel="sunset"
```

**Tooling:**
- Ruby on Rails: `rails-sunset`
- API Gateway: Tyk (upcoming support)
- Client middleware: `faraday-sunset` (Ruby), `guzzle-sunset` (PHP)

Clients add middleware to their HTTP stack that watches for `Sunset` headers and logs them — giving developers programmatic advance notice.

---

### 6. Changing validation rules

Two categories:

**Clearly breaking:**
- Lowering a maximum string length → clients send data they believe is valid, API rejects it

**Subtly breaking:**
- Increasing a maximum string length → one client version saves a 40-char value; an older client can't submit it back because it still enforces a 20-char limit

**Solution:** Move client-side validation logic to **server-defined JSON Schema**. Pair with a `Link` header so clients can fetch the schema at runtime. This communicates validation changes automatically without developer intervention on the client side.

---

### 7. Deprecating an authentication method (e.g. HTTP Basic Auth)

Steps:
1. Monitor usage and identify affected clients
2. Blog, post videos, communicate via developer channels
3. Add SDK-level deprecation warnings months ahead of cutoff
4. On removal date, return a structured error using **RFC 7807 (Problem Details for HTTP APIs)**:

```http
HTTP/1.1 403 Forbidden
Content-Type: application/problem+json

{
  "type": "https://example.org/docs/errors#http-basic-removed",
  "title": "Basic authentication is no longer supported",
  "detail": "HTTP Basic has been deprecated since January 1st 2018, and was removed May 1st 2018. Applications should switch to OAuth to resume service."
}
```

This approach also works for removing content types (e.g. dropping `application/xml` support).

---

## Toolbox Summary

| Tool / Standard | Purpose |
|---|---|
| Additive properties in serializer | Splitting or renaming fields without breaking old clients |
| New property name | Changing value semantics (e.g. currency units) |
| OpenAPI `deprecated: true` | Human-readable deprecation signal |
| JSON Schema `deprecated` keyword | Programmatic deprecation detectable by smart SDKs |
| `Sunset` header (RFC draft) | Signals endpoint removal date in HTTP response |
| `Link` header + `rel="sunset"` | Points client to documentation for replacement |
| RFC 7807 Problem Details | Structured error on removal of deprecated feature |
| Feature flags / declarative changes | Managing branching code paths during transition (Stripe's approach) |
| Contract / integration tests | Verifying external interface is unchanged during internal refactoring |

---

## When to Use Evolution vs. Global Versioning

| Scenario | Recommended approach |
|---|---|
| Many external client teams, frequent incremental changes | **API evolution** — saves aggregate engineering hours across all clients |
| Few clients (e.g. one iOS + one Android app), major overhauls every 1–2 years | **Global versioning** — simpler coordination at small scale |

**The economic argument for evolution:**

> If a change takes twice as long for the API team compared to a versioning approach, but saves 6–7 client teams from chasing a new version — the company wins on total engineering hours.

---

## Key Principles

- **Additive over destructive** — add new properties, don't remove old ones immediately
- **Communicate, don't surprise** — use headers, schemas, and SDKs to give clients advance notice
- **No lock-step deploys** — clients should be able to migrate on their own schedule
- **New concept = new endpoint name** — not a version number, a better domain name
- **Ingenuity bridges gaps** — transition-period edge cases can almost always be handled with a little creative logic

---

## Standards & Tools Referenced

- [RFC 7807 — Problem Details for HTTP APIs](https://tools.ietf.org/html/rfc7807)
- [Sunset header (IETF draft)](https://tools.ietf.org/html/draft-wilde-sunset-header)
- OpenAPI v3 — `deprecated: true` on properties
- JSON Schema — `deprecated` keyword (in progress at time of writing)
- `faraday-sunset` (Ruby), `guzzle-sunset` (PHP) — client-side Sunset header middleware
- `rails-sunset` — Rails server-side Sunset header support
- Tyk — open-source API gateway with Sunset support

---