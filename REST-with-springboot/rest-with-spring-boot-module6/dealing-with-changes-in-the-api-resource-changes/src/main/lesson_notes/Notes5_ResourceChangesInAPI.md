# Dealing with Changes in the API – Resource Changes

---

## 1. Goal

Explore how to handle different types of changes to API resources while minimising the impact on clients consuming the service. The guiding principle throughout is **backward compatibility** — keeping existing clients working while the API evolves.

---

## 2. Types of API Changes

An API is in constant evolution, adapting to new requirements. The most common changes to resources and their representations are:

- Adding, removing, or renaming fields
- Data type changes
- Structure changes

Each type has different implications for whether the change is **breaking** or **non-breaking**, and different strategies to handle it safely.

---

## 3. Data Type Changes

### The Problem

Changing the type of a field in the domain model ripples through every layer of the application — DTOs, services, repositories, controllers, and data scripts. More importantly, it almost always **affects the final representation** seen by the client.

**Example:** Switching a `Long` ID to a `UUID` String.

In JSON, strings use quotes and numbers do not — so clients parsing a numeric ID will break when they receive a string. This is a **breaking change**.

### When a Type Change May Not Break Clients

If the change does not affect the final JSON representation, it is less likely to be breaking. However, even then, context matters:

| Change direction | Risk |
|---|---|
| Wider → narrower type (e.g. `Long` → `Integer`) | Breaking on **input** — client may send a value too large for the new type |
| Narrower → wider type (e.g. `Integer` → `Long`) | Breaking on **output** — client may not expect or correctly parse a large value |

If the former type was never conceptually correct for the field (e.g. a "day of month" field that should never exceed 31), the change is less likely to be a practical breaking change.

### Strategy: Handle Type Changes Gracefully

**For trivial type changes** (where a clear correspondence exists between old and new values):

1. Make the type change in the core layers (service, domain, persistence)
2. In the **presentation layer DTO**, add the new field and deprecate the old one
3. The DTO acts as an adapter — supporting both formats and translating between them

**For non-trivial type changes** (e.g. `Long` → `UUID`, where no direct mapping exists):

1. Add the new field all the way down to the persistence model
2. Data is temporarily "duplicated" — both old and new fields are stored and returned
3. Auto-generate the new field's values for existing entities where possible
4. Support accessing resources via either field during the transition period
5. Deprecate the old field once clients have had time to migrate

---

## 4. Adding, Removing, and Renaming Fields

### Adding a Field

| Scenario | Breaking? |
|---|---|
| New field is **mandatory** (required in input) | **Yes** — existing clients don't send it; requests fail |
| New field is **optional** | **No** — existing clients ignore it on output; not required on input |

**Example:** Adding a mandatory `estimatedHours` field to a `TaskDto` with `@NotNull` causes existing POST requests to fail with a validation error. Removing the `@NotNull` constraint makes the same change non-breaking — the request succeeds, and clients can add support for the new field at their own pace.

### Removing a Field

- Primarily affects **output** (clients expecting the field won't find it)
- If the field was **required**, this is a breaking change
- **Strategy:** Keep the field in the model, remove any required constraint, and mark it `@Deprecated` before eventually removing it

```java
@Deprecated
String description,
```

### Renaming a Field

- Affects both **input** (client sends old name, server doesn't recognise it) and **output** (client expects old name, server sends new name)
- Always a **potentially breaking change**
- **Strategy:** Duplicate the field in the DTO — keep both old and new names. Mark old name as deprecated. On input, new name takes precedence. On output, both fields return the same value during the transition period.

### General Strategy for Field Changes

The common thread across addition, removal, and renaming:

1. **Never force clients to change immediately** — support both old and new approaches during a transition period
2. **Remove or avoid required constraints** on the affected field at first
3. **Mark the old approach as deprecated** so clients know to migrate
4. **Remove the old field** only after sufficient time has passed

---

## 5. Structure Changes

### What Counts as a Structure Change

Moving from a flat structure to a nested one, or reorganising how fields are grouped. Example: extracting an `email` field from a `WorkerDto` into a nested `UserDto`:

```java
public record UserDto(
    Long id,
    String email
) {}

public record NewWorkerDto(
    Long id,
    UserDto user,       // nested structure
    String firstName,
    String lastName
) {}
```

### Breaking or Not?

Depends on the complexity of the change:

- **Simple restructuring** (e.g. splitting `name` into `firstName` + `lastName` where fields are optional) → likely non-breaking using the same field-duplication strategies
- **Complex restructuring** (nested objects, new relationships) → likely **breaking**; supporting both structures in a single resource becomes impractical

### Strategy: New Resource + Content Negotiation

When the structural change is too complex to support in one resource:

1. **Create a new DTO class** for the new structure (e.g. `NewWorkerDto`)
2. **Keep the old DTO class** (e.g. `OldWorkerDto`) for backward compatibility
3. **Deprecate the old DTO** at class level — use both the Java annotation and the Swagger annotation for documentation:

```java
@Deprecated
@Schema(deprecated = true)
public record OldWorkerDto( … ) {}
```

> Note: `@Deprecated` alone is not enough for Swagger to mark the schema as deprecated in API docs — `@Schema(deprecated = true)` is also required.

4. **Duplicate the controller methods** — one for each DTO. Use **custom media types** (content negotiation) to route between them, keeping the same URL:

```java
// Old structure — default, backward compatible
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public OldWorkerDto create(@RequestBody @Valid OldWorkerDto newWorker) { … }

// New structure — opt-in via custom media type
@PostMapping(
  produces = "application/vnd.baeldung.new-worker+json",
  consumes = "application/vnd.baeldung.new-worker+json"
)
@ResponseStatus(HttpStatus.CREATED)
public NewWorkerDto createNewStructure(@RequestBody @Valid NewWorkerDto newWorker) { … }
```

### How it works in practice

- Clients sending requests **without** the custom `Content-Type`/`Accept` headers get the old structure — no changes needed on their side
- Clients wanting the new structure **opt in** by setting `Content-Type: application/vnd.baeldung.new-worker+json` and `Accept: application/vnd.baeldung.new-worker+json`
- API documentation shows both media types are supported and flags the default one as deprecated

> This is **not** API versioning — no new version of the whole API is deployed. It is also **not** resource versioning because there is no ongoing progression — just one representation being deprecated in favour of a better structure.

---

## 6. Deprecation Policy and Sunsetting

Deprecation signals that an API feature is no longer recommended and should be replaced. For it to be useful, deprecation must be communicated clearly and proactively.

### Key elements of a good deprecation policy

| Element | Description |
|---|---|
| **Deprecation date** | When the feature is officially deprecated |
| **Sunset date** | When the feature will be permanently removed |
| **Migration documentation** | Step-by-step guide to adopting the new approach |
| **Active notification** | Directly inform clients — documentation changes alone are easily missed |

Documentation updates are necessary but not sufficient. Clients should be **actively notified** of planned changes with enough lead time to adapt.

---

## 7. The Evolution Strategy (Versionless API)

The approaches throughout this lesson collectively form the **evolution strategy** — also called a versionless API approach.

### Core idea

Rather than releasing new major versions of the API, the interface evolves continuously:
- Maintaining functionality as much as possible
- Marking individual fields or structures as deprecated when needed
- Letting clients adapt incrementally, in step with the interface

### Key principles

- **Backward compatibility is the primary goal** — never silently break existing clients
- **Avoid required constraints on new fields** during transition
- **Support old and new simultaneously** for a defined transition period
- **Communicate proactively** — headers, documentation, SDKs, and direct notification

### When evolution works well

- APIs with many external clients where coordinated upgrades are impractical
- Incremental, ongoing changes rather than large-scale redesigns

### When versioning may be more appropriate

- Small number of clients (e.g. one iOS and one Android app) where coordinated upgrades are feasible
- Drastic, wholesale API redesigns every year or two

---

## 8. Summary: Change Types and Strategies

| Change type | Breaking? | Strategy |
|---|---|---|
| Data type (trivial mapping) | Maybe | Change core layers; adapter DTO supports both for transition |
| Data type (no mapping, e.g. Long → UUID) | Yes | Duplicate field all the way to persistence; auto-generate new values |
| Add optional field | No | Add field without required constraint; clients adopt at own pace |
| Add mandatory field | Yes | Make optional first; enforce required constraint only after clients adopt |
| Remove field | Yes (if required) | Keep field; remove required constraint; mark deprecated; remove later |
| Rename field | Yes | Duplicate field in DTO; deprecate old name; both returned on output |
| Simple structure change | Maybe | Field duplication strategies; keep optional during transition |
| Complex structure change | Yes | New DTO + old DTO; content negotiation routes between them at same URI |

---