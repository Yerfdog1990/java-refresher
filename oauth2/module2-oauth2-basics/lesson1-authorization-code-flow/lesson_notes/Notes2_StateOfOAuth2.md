Here are the detailed notes.

---

## The State of OAuth2 in Spring Security

### 1. Goal

To understand the current state of OAuth2 support across the Spring ecosystem — which projects provide it, what the architectural split looks like, which stack to use, and where to find the authoritative feature parity reference.

---

### 2. Background: A Major Re-Write

In early 2018, the Spring Security core team announced a significant new direction: a full **re-write** of the OAuth2 support in Spring Security.

Re-writes are extremely rare in the Spring ecosystem, which signals just how serious this investment was. The goals were to modernize the implementation and align it with the evolving OAuth specification — a multi-year process that continues today, tracked through Spring Security 5.x and beyond.

---

### 3. The Three Pillars of Spring OAuth2 Support

The effort was split into three major functional areas:

**Client Support** — the application acting as an OAuth2 client, requesting tokens and accessing protected resources. Released in Spring Security 5.0 and extended in 5.1.

**Resource Server Support** — protecting your own API endpoints using Access Tokens. Released in Spring Security 5.1 and expanded in 5.2.

**Authorization Server Support** — issuing tokens to clients. This was split out into a **separate project**, the Spring Authorization Server, built on top of the Spring Security framework. It is considered production-ready as of its 0.2.0 release.

---

### 4. Two Stacks: Old vs. New

There are currently two OAuth stacks in the Spring ecosystem:

| | New stack | Legacy stack |
|---|---|---|
| Project | Spring Security (5.x+) + Spring Authorization Server | Spring Security OAuth (2.x) |
| Status | Active development, focus of core team | Maintenance mode |
| Updates | New features + security fixes | Security fixes and minor updates only |

The legacy stack (`spring-security-oauth`) is **still fully supported** and will remain so for some time. However, the core team's attention has fully shifted to the new stack.

---

### 5. Which Stack Should You Use?

The guidance is pragmatic: **there is not yet full feature parity** between the two stacks.

- If the OAuth2 functionality you need is **already supported in the new GA releases** of Spring Security → use the new stack.
- If it is **not yet supported** in the new stack → fall back to the legacy stack for now.

This decision is made much easier by a community-maintained resource:

**The OAuth2 Feature Matrix** — a document that maps every major OAuth2 feature to the project(s) that support it across both stacks. It is updated continuously as new features ship.

---

### 6. The Feature Matrix — Summary

Here is an overview of where key features stand across the ecosystem. 

| **Feature**               | **Spring Security 5.4+** | **Spring Security OAuth 2.5** |
| ------------------------- | ------------------------ | ----------------------------- |
| **Authorization Grants**  |                          |                               |
| Authorization Code        | ✓                        | ✓                             |
| Implicit                  | —                        | ✓                             |
| Resource Owner Password   | —                        | ✓                             |
| Client Credentials        | ✓                        | ✓                             |
| Refresh Token             | ✓                        | ✓                             |
| **Client Authentication** |                          |                               |
| HTTP Basic                | ✓                        | ✓                             |
| HTTP POST                 | ✓                        | ✓                             |
| **HTTP Client Support**   |                          |                               |
| RestTemplate              | ✓ (via manager)          | ✓                             |
| WebClient                 | ✓                        | —                             |
| **User Authentication**   |                          |                               |
| OAuth 2.0 Login (SSO)     | ✓                        | ✓                             |
| UserInfo endpoint         | ✓                        | ✓                             |
| **Token Storage**         |                          |                               |
| In-memory                 | ✓                        | —                             |
| JDBC                      | —                        | ✓                             |

### 7. Notable Observations from the Matrix

**On the Client side**, the new Spring Security stack brings reactive support (`WebClient`) that the legacy stack lacks, while the legacy stack still has JDBC-backed token storage that the new stack hasn't fully replicated yet.

**On the Resource Server side**, feature parity between the two stacks is strong — both handle JWT and opaque tokens with a full range of verification mechanisms.

**On the Authorization Server side**, the new Spring Authorization Server deliberately omits the Implicit and ROPC grant types — consistent with OAuth 2.1's deprecation of both. The legacy stack still supports them for backward compatibility, but they should not be used in new systems.

---

### 8. Key Takeaways

The Spring ecosystem is in a transitional period. The old stack works and is maintained, but all forward momentum is in the new stack. The practical approach for any new Spring project is:

1. Start with the new stack (`spring-security` for Client and Resource Server, `spring-authorization-server` for the Authorization Server).
2. Check the OAuth2 Feature Matrix if you hit a gap.
3. Use the legacy stack (`spring-security-oauth`) only as a bridge for features not yet in the new stack.
4. Expect the gap to close over time — the core team is actively shipping.