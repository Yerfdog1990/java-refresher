# Roy Fielding on Versioning, Hypermedia & REST

*InfoQ interview by Mike Amundsen · December 2014*

---

## About Roy Fielding

- Senior Principal Scientist at Adobe
- PhD student at UC Irvine — coined the term **REST** (Representational State Transfer), originally called the "HTTP Object Model"
- Founding member of the Apache HTTP Server project
- Co-author of HTTP and URI Templates RFC specifications
- Editor of the W3C Do Not Track standards effort

---

## Core Argument: DON'T Version Your API

### What "versioning" means
Fielding defines API versioning as putting client-visible version numbers inside interface names (e.g. `/v1/users`, `/api/v2/orders`).

### Two failure modes of versioning

**a) The version changes** → all prior clients must be restarted, redeployed, or abandoned because they cannot adapt to the new system.

**b) The version never changes** → it becomes permanent dead weight, making every API call less efficient.

> *"Versioning interface names only manages change for the API owner's sake. That is a myopic view of interface design: one where the owner's desire for control ignores the customer's need for continuity."*

### When you do need a breaking change
Use a **new hostname** — a new brand. That is not "v2", it is a new website. Websites don't carry version numbers; neither should a RESTful API.

> *"A RESTful API (done right) is just a website for clients with a limited vocabulary."*

---

## Hypermedia as a REST Constraint (Not Optional)

### HATEOAS is mandatory
"Hypermedia as the engine of application state" is a **hard constraint** of REST — not an option, not an ideal. Without it, clients have their controls baked in at deployment and cannot adapt to change. Hypermedia enables controls to be supplied on demand, learned at runtime rather than hardcoded.

> *"You either do it or you aren't doing REST. You can't have evolvability if clients have their controls baked into their design at deployment. Controls have to be learned on the fly. That's what hypermedia enables."*

### Code-on-demand complements hypermedia
Hypermedia handles application control evolution. For adapting how clients *understand* representations (media types, expected processing), **code-on-demand** is the complementary mechanism. Both are needed for full evolvability.

---

## Evolvability: The Real Goal

### Don't build for "RESTful" — build for evolvability
REST is a means to an end. The real goal is a system that does not need to be shut down or redeployed to adapt to change. REST induces properties known to benefit multi-organisation systems — chief among them evolvability.

> *"Don't build an API to be RESTful — build it to have the properties you want."*

### "Scale of decades" — taken literally
Fielding designed REST to solve a specific problem: improving HTTP without breaking the Web. He needed a system capable of withstanding decades of change by people across the world, evolving in independent and orthogonal directions, without ever requiring a shutdown. HTTP has achieved this for 30+ years.

### Working backwards from decades
Developers often think short-term ("works next week", "fix it in the next release"). Fielding suggests a different framing:

| Time horizon | Question to ask |
|---|---|
| Decades | How long could this system realistically be in use? |
| Years | When will you no longer know your users? |
| Months | When do you lose control over client deployment? |

### Why in-house patterns don't apply to the Web
Techniques developers learn managing in-house software — where they control deployment of both clients and servers — simply don't transfer to network-based software crossing organisational boundaries. This is precisely the problem REST is designed to solve.

---

## Lessons from HTTP & HTML

### HTTP doesn't version interface names
HTTP method names and URIs have no version numbers. Versioning in HTTP is **informative rather than contractual** — it signals what "language" is being spoken, not a breaking boundary at the endpoint level.

### The world changed *around* HTTP
- The `Host` header wasn't needed in 1992 (no one needed multiple domains per IP until the Web made internet presence a business imperative)
- Persistent connections only mattered once Mosaic added embedded images
- Absolute time caching made more sense when humans — not caches — looked at expiration fields

None of these were mistakes; the world simply evolved around a well-designed system.

### The key lesson
Define how a protocol can be expected to change, and what recipients should do when they receive a change they don't yet understand. HTTP survived because:
- New syntax was required to be **ignorable**
- Semantic changes were **version-gated at the protocol level**, not at the endpoint name level

---

## Key Concepts at a Glance

| Concept | Fielding's position |
|---|---|
| Version IDs in endpoint names | Never — they are breaking by design |
| HATEOAS | Hard constraint, not optional |
| Breaking API changes | Use a new hostname (new brand) |
| Code-on-demand | Necessary for media type evolution |
| REST | A means to evolvability, not a label to claim |
| Engineering timescale | Decades, not sprints |
| In-house vs. web-scale patterns | Fundamentally different problems |

---

## Bottom Line

> *"Heh, I didn't say DON'T change over time — just don't use deliberately breaking names in an API."*

REST remains Fielding's advice for building Web applications that work well over time and generate more addressable resources. But the goal is **evolvability** — REST is simply a well-proven path to get there.

---