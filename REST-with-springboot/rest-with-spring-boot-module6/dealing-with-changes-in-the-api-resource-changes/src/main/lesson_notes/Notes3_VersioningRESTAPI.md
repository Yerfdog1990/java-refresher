# Versioning a REST API

---

## 1. The Problem

Evolving a REST API is a difficult problem with many available options. The core tension is this: APIs must change over time, but clients depend on them being stable. Choosing how to manage that change has significant consequences for URI design, caching, client workload, and long-term maintainability.

---

## 2. What Is in the Contract?

Before choosing a versioning strategy, you must first establish what the **contract** between the API and the client actually is.

### 2.1 Are URIs part of the contract?

No. Clients should **not** bookmark, hardcode, or generally rely on URI structures. Doing so means the client's interaction is driven by out-of-band information rather than by the service itself — which violates REST principles.

> *"A REST API should be entered with no prior knowledge beyond the initial URI (bookmark) and set of standardized media types that are appropriate for the intended audience… Failure here implies that out-of-band information is driving interaction instead of hypertext."*
> — Roy Fielding

The client should know only a **single URI** — the entry point. All other URIs should be **discovered** while consuming the API.

### 2.2 Are Media Types part of the contract?

Yes. The client must have prior knowledge of the media types used for resource representations in order to consume the API successfully. In fact, **the definition of the media types represents the entire contract**.

> *"A REST API should spend almost all of its descriptive effort in defining the media type(s) used for representing resources and driving application state."*

This is where standardisation matters most, and where versioning effort should be focused.

---

## 3. High-Level Versioning Options

Two main approaches exist:

| Approach | Mechanism |
|---|---|
| **URI Versioning** | Add version indicators to the URI space |
| **Media Type Versioning** | Version the representation of the resource via content negotiation |

### 3.1 URI Versioning

Version numbers are embedded directly in the URL:

```
http://host/v1/users
http://host/v1/privileges

http://host/v2/users
http://host/v2/privileges
```

Representations are considered **immutable** — when a breaking change is needed, an entirely new URI space is created.

### 3.2 Media Type Versioning

Version numbers are embedded in custom vendor MIME types. The URI stays stable; content negotiation selects the correct representation:

```http
GET /users/3 HTTP/1.1
Accept: application/vnd.myname.v1+json
```

```http
HTTP/1.1 200 OK
Content-Type: application/vnd.myname.v1+json

{
    "user": {
        "name": "John Smith"
    }
}
```

The client makes **no assumptions about response structure** beyond what is defined in the media type. This is why generic types like `application/json` are not ideal — they carry no semantic information and force clients to rely on additional hints to interpret the response.

---

## 4. Advantages and Disadvantages

### URI Versioning — Disadvantages

- **Large URI footprint** — any breaking change in any API creates a whole new tree of representations for the entire API
- **Severely inflexible** — impossible to evolve a single resource independently; it is an all-or-nothing approach
- **Slow upgrades** — moving clients from v1 to v2 becomes a major undertaking, leading to long sunset periods
- **Cache burden** — proxy caches must store multiple copies of each resource (one per version), reducing cache hit rates and putting extra load on the cache; some cache invalidation mechanisms stop working

### Media Type Versioning — Considerations

- Both the client and service must support the `Vary` HTTP header to indicate multiple versions being cached by proxies
- Client-side caching is slightly more complex — caching by URL is simpler than caching by media type
- Keeps the URI space stable and clean

### Goals for Either Approach (from API Evolution principles)

- Keep compatible changes out of names
- Avoid new major versions wherever possible
- Make changes backwards-compatible
- Think about forwards-compatibility from the start

---

## 5. Types of Changes to a REST API

### 5.1 Adding to a Resource Representation — Non-Breaking

Adding new fields to a response is **not a breaking change**, provided clients are implemented correctly (i.e. they ignore fields they don't understand — JSON handles this better than XML).

**Example:** Adding `amount` to a user response is safe for existing clients:

```json
{
    "user": {
        "name": "John Smith",
        "amount": "300"
    }
}
```

Media type documentation should be designed with **forward compatibility** in mind from the start.

### 5.2 Removing or Changing an Existing Representation — Breaking

Removing, renaming, or restructuring existing fields is a **breaking change** — clients already understand and depend on the old format.

**Solution:** Introduce a new vendor MIME media type version. The URI remains unchanged; clients request the new version via `Accept`:

```http
GET /users/3 HTTP/1.1
Accept: application/vnd.myname.v2+json
```

```http
HTTP/1.1 200 OK
Content-Type: application/vnd.myname.v2+json

{
    "user": {
        "firstname": "John",
        "lastname": "Smith",
        "amount": "300"
    }
}
```

Clients must update to request the new representation and understand the new semantics — but the **URI space remains stable**.

### 5.3 Major Semantic Changes — Most Disruptive

Changes in the **meaning** of resources, the relations between them, or what they map to in the backend. Options:

- Introduce a new media type
- Publish a new, sibling resource alongside the old one and use **linking** (HATEOAS) to point to it

**Key distinction from URI versioning:** the new resource is published independently — it does **not** fork the entire API at the root. Only the affected resource changes.

Because REST APIs should adhere to HATEOAS, most URIs should be discovered by clients rather than hardcoded. Changing a URI under these conditions is **not** an incompatible change — clients re-discover the new URI and continue to function.

---

## 6. Change Type Quick Reference

| Change type | Breaking? | Recommended approach |
|---|---|---|
| Adding a new field to a response | No | Just add it; clients ignore unknown fields |
| Removing or renaming a field | Yes | New vendor media type version |
| Restructuring a representation | Yes | New vendor media type version |
| Major semantic/resource change | Yes | New sibling resource + linking (HATEOAS) |
| URI change (HATEOAS-compliant API) | No | Clients re-discover the new URI |

---

## 7. Conclusion

The recommended approach is **media type versioning** — versioning the representations rather than the URIs. This keeps the URI space stable, avoids forking the entire API on every breaking change, and aligns with REST's constraint that URIs should be discovered rather than hardcoded.

URI versioning, while not technically un-RESTful, creates long-term maintenance burdens, inflexibility, and caching problems that compound over time.

The right mental model: **the media type is the contract**, not the URL.

---