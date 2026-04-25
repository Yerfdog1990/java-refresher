# OAuth 2.0 Authorization Framework
### Based on RFC 6749 — October 2012

---

## Table of Contents

1. [Introduction & Problem Statement](#1-introduction--problem-statement)
2. [Key Keyword Definitions](#2-key-keyword-definitions)
3. [Roles](#3-roles)
4. [Protocol Flow (Abstract)](#4-protocol-flow-abstract)
5. [Authorization Grants](#5-authorization-grants)
6. [Access Tokens & Refresh Tokens](#6-access-tokens--refresh-tokens)
7. [Client Types & Registration](#7-client-types--registration)
8. [Protocol Endpoints](#8-protocol-endpoints)
9. [Grant Type Flows (Detailed)](#9-grant-type-flows-detailed)
    - [Authorization Code Grant](#91-authorization-code-grant)
    - [Implicit Grant](#92-implicit-grant)
    - [Resource Owner Password Credentials Grant](#93-resource-owner-password-credentials-grant)
    - [Client Credentials Grant](#94-client-credentials-grant)
10. [Token Responses & Error Codes](#10-token-responses--error-codes)
11. [Refreshing an Access Token](#11-refreshing-an-access-token)
12. [Security Considerations](#12-security-considerations)
13. [Quick Reference Cheat Sheet](#13-quick-reference-cheat-sheet)

---

## 1. Introduction & Problem Statement

In the **traditional client-server model**, a third-party app gains access to protected resources by using the resource owner's credentials directly. This creates serious problems:

| Problem | Description |
|---|---|
| Credential storage | Third-party apps must store the owner's password in plain text |
| Overly broad access | No way to limit what the third party can access |
| No selective revocation | Revoking one app requires changing the password for all |
| Cascade compromise | If one app is breached, the owner's password and all data are exposed |

**OAuth 2.0 solves this** by introducing an **authorization layer** that separates the client's identity from the resource owner's identity. Instead of sharing credentials, the client receives an **access token** — a scoped, time-limited credential.

> **Example:** A user (resource owner) grants a printing service (client) access to photos stored at a photo-sharing site (resource server), without ever sharing their password. The authorization server issues a delegation-specific access token to the printing service.

---

## 2. Key Keyword Definitions

### Core Roles

| Term | Definition |
|---|---|
| **Resource Owner** | The entity that can grant access to a protected resource. When a person, referred to as an *end-user*. |
| **Resource Server** | The server hosting the protected resources. Accepts and responds to protected resource requests using access tokens. |
| **Client** | The application making protected resource requests on behalf of the resource owner, with its authorization. |
| **Authorization Server** | Issues access tokens to the client after authenticating the resource owner and obtaining authorization. |

### Token & Credential Terms

| Term | Definition |
|---|---|
| **Access Token** | A string credential representing the authorization granted to the client. Has a specific scope, lifetime, and other access attributes. Usually opaque to the client. |
| **Refresh Token** | A credential used to obtain new access tokens when the current one expires, without requiring the resource owner to re-authenticate. |
| **Authorization Code** | A short-lived, single-use intermediary credential issued by the authorization server and exchanged by the client for an access token. |
| **Authorization Grant** | A credential representing the resource owner's authorization, used by the client to obtain an access token. |
| **Client Credentials** | The client's own authentication credentials (e.g., `client_id` + `client_secret`). |
| **Scope** | A space-delimited list of case-sensitive strings defining the access range being requested or granted. |

### Endpoint Terms

| Term | Definition |
|---|---|
| **Authorization Endpoint** | The server endpoint where the client sends the resource owner to obtain an authorization grant. |
| **Token Endpoint** | The server endpoint where the client exchanges an authorization grant or refresh token for an access token. |
| **Redirection Endpoint (Redirect URI)** | The client's endpoint where the authorization server sends the resource owner's user-agent back after authorization. |

### Protocol Terms

| Term | Definition |
|---|---|
| **Bearer Token** | A type of access token where possession alone is sufficient to access the resource (no additional proof of identity needed). |
| **TLS** | Transport Layer Security — required for all sensitive OAuth communications. |
| **CSRF** | Cross-Site Request Forgery — an attack OAuth mitigates via the `state` parameter. |
| **User-Agent** | Typically a web browser acting on behalf of the resource owner. |
| **Opaque** | A token value that the client cannot inspect or interpret; it is only meaningful to the authorization/resource server. |

---

## 3. Roles

```
┌─────────────────────────────────────────────────────────────────────┐
│                         OAUTH 2.0 ROLES                             │
├───────────────────┬─────────────────────────────────────────────────┤
│  Resource Owner   │  Controls access to protected resources         │
│                   │  (usually the end-user)                         │
├───────────────────┼─────────────────────────────────────────────────┤
│  Resource Server  │  Hosts the protected resources                  │
│                   │  Validates access tokens                        │
├───────────────────┼─────────────────────────────────────────────────┤
│  Client           │  Requests access on behalf of the resource owner│
│                   │  (web app, mobile app, desktop app, etc.)       │
├───────────────────┼─────────────────────────────────────────────────┤
│  Authorization    │  Authenticates the resource owner               │
│  Server           │  Issues access tokens to the client             │
└───────────────────┴─────────────────────────────────────────────────┘
```

> **Note:** The Authorization Server and Resource Server may be the same entity, or a single Authorization Server may serve multiple Resource Servers.

---

## 4. Protocol Flow (Abstract)

```
+--------+                               +---------------+
|        |--(A)- Authorization Request ->|   Resource    |
|        |                               |     Owner     |
|        |<-(B)-- Authorization Grant ---|               |
|        |                               +---------------+
|        |
|        |                               +---------------+
|        |--(C)-- Authorization Grant -->| Authorization |
| Client |                               |     Server    |
|        |<-(D)----- Access Token -------|               |
|        |                               +---------------+
|        |
|        |                               +---------------+
|        |--(E)----- Access Token ------>|   Resource    |
|        |                               |    Server     |
|        |<-(F)--- Protected Resource ---|               |
+--------+                               +---------------+
```

### Step-by-Step Explanation

| Step | Actor | Action |
|---|---|---|
| **(A)** | Client → Resource Owner | Client requests authorization (directly or via the authorization server) |
| **(B)** | Resource Owner → Client | Resource owner grants authorization (one of four grant types) |
| **(C)** | Client → Authorization Server | Client presents the authorization grant to obtain a token |
| **(D)** | Authorization Server → Client | Server validates grant and issues an access token |
| **(E)** | Client → Resource Server | Client presents the access token to access protected resources |
| **(F)** | Resource Server → Client | Server validates token and serves the resource |

---

## 5. Authorization Grants

An **authorization grant** is a credential representing the resource owner's authorization. OAuth 2.0 defines **four standard grant types**:

```
┌──────────────────────────────────────────────────────────────────────────┐
│                     AUTHORIZATION GRANT TYPES                            │
├──────────────────────────┬───────────────────────────────────────────────┤
│ Authorization Code       │Most secure. Uses an intermediary code.        │
│                          │Best for confidential (server-side) clients.   │
├──────────────────────────┼───────────────────────────────────────────────┤
│ Implicit                 │Simplified flow for browser-based JS clients.  │
│                          │Access token issued directly. No refresh token.│
├──────────────────────────┼───────────────────────────────────────────────┤
│ Resource Owner Password  │Client directly uses owner's username+password.│
│ Credentials              │Only for highly trusted clients. High risk.    │
├──────────────────────────┼───────────────────────────────────────────────┤
│ Client Credentials       │Client acts on its own behalf (no user).       │
│                          │Machine-to-machine use cases.                  │
└──────────────────────────┴───────────────────────────────────────────────┘
```

### When to Use Which Grant Type

| Grant Type | Use Case | Refresh Token? |
|---|---|---|
| Authorization Code | Server-side web apps, native apps | ✅ Yes |
| Implicit | Legacy browser JS apps (now discouraged) | ❌ No |
| Resource Owner Password Credentials | Legacy/migration only, high-trust clients | ✅ Yes |
| Client Credentials | Server-to-server, daemons, microservices | ❌ Usually not |

---

## 6. Access Tokens & Refresh Tokens

### Access Token

- A **string** representing the authorization issued to the client
- Usually **opaque** to the client (only the resource server interprets it)
- Represents a **specific scope, lifetime, and access attributes**
- Can be self-contained (e.g., JWT) or a reference to server-side data
- Must be transmitted **only over TLS**

### Refresh Token

- Used to obtain a **new access token** without re-involving the resource owner
- **Never sent to the resource server** — only used with the authorization server
- Issuance is **optional** — at the authorization server's discretion
- Should be treated as highly sensitive and stored securely

### Token Lifecycle Diagram

```
Client                    Auth Server              Resource Server
  |                            |                         |
  |---(Authorization Grant)--->|                         |
  |<--(Access Token +          |                         |
  |    Refresh Token)----------|                         |
  |                            |                         |
  |---(Access Token)-------------------------->          |
  |<--(Protected Resource)-------------------->          |
  |                            |                         |
  |   [Access Token Expires]   |                         |
  |                            |                         |
  |---(Access Token)-------------------------->          |
  |<--(Invalid Token Error)-------------------->         |
  |                            |                         |
  |---(Refresh Token)--------->|                         |
  |<--(New Access Token)-------|                         |
  |                            |                         |
```

---

## 7. Client Types & Registration

### Client Types

```
┌─────────────────────────────────────────────────────────────┐
│                      CLIENT TYPES                           │
├─────────────────────┬───────────────────────────────────────┤
│  CONFIDENTIAL       │  Can keep client credentials secret   │
│                     │  e.g., server-side web applications   │
├─────────────────────┼───────────────────────────────────────┤
│  PUBLIC             │  Cannot keep credentials secret       │
│                     │  e.g., mobile apps, browser JS apps   │
└─────────────────────┴───────────────────────────────────────┘
```

### Client Profiles

| Profile | Type | Description |
|---|---|---|
| **Web Application** | Confidential | Runs on web server; credentials never exposed to end-user |
| **User-Agent-Based** | Public | Code runs in browser (JavaScript); credentials visible to user |
| **Native Application** | Public | Installed on user's device; credentials can be extracted |

### Registration Requirements

When registering, the client developer **must**:
1. Specify the **client type** (confidential or public)
2. Provide **redirection URIs**
3. Include any other information required by the authorization server (name, logo, legal terms, etc.)

The client is issued a **`client_id`** — a unique, non-secret identifier — and optionally a **`client_secret`**.

---

## 8. Protocol Endpoints

### Authorization Server Endpoints

```
┌───────────────────────────────────────────────────────────┐
│             AUTHORIZATION SERVER ENDPOINTS                │
├───────────────────────┬───────────────────────────────────┤
│  Authorization        │ Resource owner grants/denies      │
│  Endpoint             │ access. Requires TLS.             │
│                       │ Supports GET (and optionally POST)│
├───────────────────────┼───────────────────────────────────┤
│  Token Endpoint       │ Client exchanges grant for token. │
│                       │ equires TLS. POST only.           │
└───────────────────────┴───────────────────────────────────┘
```

### Client Endpoint

```
┌───────────────────────────────────────────────────────────┐
│                    CLIENT ENDPOINT                        │
├───────────────────────┬───────────────────────────────────┤
│  Redirection          │ Auth server returns authorization │
│  Endpoint             │ responses here via user-agent.    │
│  (Redirect URI)       │ Must be pre-registered.           │
└───────────────────────┴───────────────────────────────────┘
```

### Scope Parameter

- Expressed as **space-delimited, case-sensitive strings**
- Example: `scope=read write delete`
- Authorization server may grant less scope than requested
- If issued scope differs from requested, server must include `scope` in response

---

## 9. Grant Type Flows (Detailed)

### 9.1 Authorization Code Grant

**Best for:** Confidential (server-side) clients. Most secure grant type.

```
+----------+
| Resource |
|   Owner  |
+----------+
     ^
     | (B) User authenticates & approves
+----|-----+          Client ID           +---------------+
|  User-  -+----(A)-- & Redirect URI ---->|               |
|  Agent  -+----(C)-- Auth Code ---------<| Authorization |
+-|----|---+                              |     Server    |
  |    |                                  +---------------+
 (A)  (C)
  |    |
  v    v
+---------+
|         |>---(D)-- Auth Code + Redirect URI -->  Auth Server
| Client  |<---(E)-- Access Token (+ Refresh) ---  Auth Server
+---------+
```

#### Step-by-Step

| Step | Description |
|---|---|
| **(A)** | Client redirects user-agent to authorization endpoint with `client_id`, `redirect_uri`, `scope`, `state`, `response_type=code` |
| **(B)** | Authorization server authenticates the resource owner and asks for approval |
| **(C)** | Server redirects user-agent back to client's `redirect_uri` with `code` and `state` |
| **(D)** | Client sends `code` + `redirect_uri` + credentials to the token endpoint |
| **(E)** | Server validates everything and returns `access_token` (+ optional `refresh_token`) |

#### Key Authorization Request Parameters

| Parameter | Required? | Description |
|---|---|---|
| `response_type` | **Required** | Must be `"code"` |
| `client_id` | **Required** | The registered client identifier |
| `redirect_uri` | Optional | Where to send the user-agent after authorization |
| `scope` | Optional | The requested access scope |
| `state` | Recommended | Opaque value to prevent CSRF |

#### Authorization Code Rules
- Must **expire shortly** after issuance (max 10 minutes recommended)
- Must be **single-use** — if reused, server must deny and revoke all issued tokens
- Bound to the `client_id` and `redirect_uri`

---

### 9.2 Implicit Grant

**Best for:** Browser-based JavaScript applications. **Now largely discouraged** in favor of Authorization Code + PKCE.

```
+----------+
| Resource |
|   Owner  |
+----------+
     ^
     | (B)
+----|-----+       Client ID          +---------------+
|  User-  -+---(A)-- & Redirect URI ->|               |
|  Agent  -|---(B)-- User Auth ------>| Authorization |
|          |<--(C)-- Redirect URI ----|     Server    |
|          |      (access_token       +---------------+
|          |       in URI fragment)
|          |                          +---------------+
|          |---(D)-- Redirect URI --->|  Web-Hosted   |
|          |<--(E)-- Script ----------|    Client     |
|     (F)  |         (extracts token) +---------------+
|          |
+-|--------+
  | (G) Access Token passed to client
  v
+---------+
|  Client |
+---------+
```

#### Key Differences from Authorization Code

| Feature | Authorization Code | Implicit |
|---|---|---|
| Client authenticated? | ✅ Yes | ❌ No |
| Refresh token issued? | ✅ Yes | ❌ No |
| Token in URL? | ❌ No (in body) | ✅ Yes (in fragment) |
| Round trips | More (2-step) | Fewer (1-step) |
| Security | Higher | Lower |

#### Authorization Request Parameters

| Parameter | Required? | Description |
|---|---|---|
| `response_type` | **Required** | Must be `"token"` |
| `client_id` | **Required** | The registered client identifier |
| `redirect_uri` | Optional | Redirect destination |
| `scope` | Optional | Requested scope |
| `state` | Recommended | CSRF protection value |

---

### 9.3 Resource Owner Password Credentials Grant

**Best for:** Legacy/migration scenarios only. Client must be **highly trusted** (e.g., the device OS or a first-party app).

```
+----------+
| Resource |
|   Owner  |
+----------+
     |
    (A) Resource Owner provides username + password to client
     |
     v
+---------+                                  +----------------+
|         |>--(B)-- username + password ----->|               |
| Client  |                                   | Authorization |
|         |<--(C)-- Access Token (+ Refresh)--|     Server    |
+---------+                                  +----------------+
```

#### Rules
- Client **must discard credentials** once the access token is obtained
- Authorization server must protect against **brute force attacks** (rate limiting, alerts)
- **Minimize use** — only when no other grant type is available

#### Token Request Parameters

| Parameter | Required? | Value |
|---|---|---|
| `grant_type` | **Required** | `"password"` |
| `username` | **Required** | Resource owner's username |
| `password` | **Required** | Resource owner's password |
| `scope` | Optional | Requested scope |

---

### 9.4 Client Credentials Grant

**Best for:** Machine-to-machine authentication where no user is involved. Client acts on its own behalf.

```
+---------+                                  +---------------+
|         |>--(A)-- Client Authentication -->|               |
| Client  |                                  | Authorization |
|         |<--(B)-- Access Token ------------|     Server    |
+---------+                                  +---------------+
```

#### Rules
- **Only for confidential clients**
- Refresh token should **not** be included in the response
- No resource owner involvement at all

#### Token Request Parameters

| Parameter | Required? | Value |
|---|---|---|
| `grant_type` | **Required** | `"client_credentials"` |
| `scope` | Optional | Requested scope |

---

## 10. Token Responses & Error Codes

### Successful Token Response

```json
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Cache-Control: no-store
Pragma: no-cache

{
  "access_token": "2YotnFZFEjr1zCsicMWpAA",
  "token_type": "Bearer",
  "expires_in": 3600,
  "refresh_token": "tGzv3JOkF0XG5Qx2TlKWIA",
  "scope": "read write"
}
```

### Response Fields

| Field | Required? | Description |
|---|---|---|
| `access_token` | **Required** | The issued access token |
| `token_type` | **Required** | Type of token (e.g., `"Bearer"`) |
| `expires_in` | Recommended | Lifetime in seconds (e.g., `3600` = 1 hour) |
| `refresh_token` | Optional | Token used to get new access tokens |
| `scope` | Conditional | Required if different from requested scope |

### Authorization Error Codes (4.1.2.1 / 4.2.2.1)

| Error Code | Description |
|---|---|
| `invalid_request` | Missing or malformed request parameter |
| `unauthorized_client` | Client not authorized to use this grant type |
| `access_denied` | Resource owner or server denied the request |
| `unsupported_response_type` | Server doesn't support this response type |
| `invalid_scope` | Requested scope is invalid or unknown |
| `server_error` | Unexpected server condition |
| `temporarily_unavailable` | Server temporarily overloaded or in maintenance |

### Token Endpoint Error Codes (5.2)

| Error Code | Description |
|---|---|
| `invalid_request` | Malformed or duplicate parameters |
| `invalid_client` | Client authentication failed |
| `invalid_grant` | Auth code/credentials invalid, expired, or revoked |
| `unauthorized_client` | Client not authorized for this grant type |
| `unsupported_grant_type` | Grant type not supported by the server |
| `invalid_scope` | Scope invalid, unknown, or exceeds granted scope |

---

## 11. Refreshing an Access Token

```
Client                         Authorization Server
  |                                     |
  |---(G)-- Refresh Token ------------->|
  |         (+ client authentication)   |
  |                                     |
  |<--(H)-- New Access Token -----------|
  |         (+ Optional New Refresh     |
  |            Token)                   |
```

### Refresh Token Request Parameters

| Parameter | Required? | Value |
|---|---|---|
| `grant_type` | **Required** | `"refresh_token"` |
| `refresh_token` | **Required** | The refresh token issued to the client |
| `scope` | Optional | Must not exceed originally granted scope |

### Refresh Token Rotation
- The server **may issue a new refresh token** with each refresh response
- The old refresh token must be **discarded** immediately
- If a compromised token is used by both attacker and legitimate client, the invalidated token signals a breach

---

## 12. Security Considerations

### Key Security Rules Summary

```
┌──────────────────────────────────────────────────────────────────────┐
│                     SECURITY REQUIREMENTS                            │
├─────────────────────────────────┬────────────────────────────────────┤
│  All token transmission         │  Must use TLS                      │
│  Credential guessing prevention │  Entropy ≥ 2^-128 (ideally 2^-160) │
│  CSRF protection                │  Use `state` parameter             │
│  Authorization code reuse       │  Deny + revoke all derived tokens  │
│  Auth code lifetime             │  Max 10 minutes (recommended)      │
│  Cache-Control on token resp.   │  Must be `no-store`                │
│  Pragma on token response       │  Must be `no-cache`                │
└─────────────────────────────────┴────────────────────────────────────┘
```

### Threat Summary

| Threat | Mitigation |
|---|---|
| **CSRF** | Use `state` parameter with non-guessable, session-bound value |
| **Phishing** | Require TLS; educate users; verify URI authenticity |
| **Clickjacking** | Use `X-Frame-Options: deny` or `sameorigin` header |
| **Token theft (implicit)** | Avoid implicit flow; use auth code + PKCE instead |
| **Redirect URI manipulation** | Register full redirect URIs; validate on every request |
| **Code injection** | Sanitize all inputs, especially `state` and `redirect_uri` |
| **Open redirectors** | Validate all redirect URI values against registered list |
| **Refresh token abuse** | Implement refresh token rotation; bind tokens to client |
| **Client impersonation** | Authenticate clients whenever possible; require URI registration |
| **Credentials-guessing** | Rate limit; use high-entropy tokens |
| **Password grant risk** | Minimize use; prefer other grant types; discard credentials immediately |

### TLS Requirements

| Endpoint | TLS Required? |
|---|---|
| Authorization Endpoint | ✅ Required |
| Token Endpoint | ✅ Required |
| Redirection Endpoint (with `code` or `token`) | ✅ Strongly recommended |
| Any endpoint with end-user interaction | ✅ Required |

---

## 13. Quick Reference Cheat Sheet

### Grant Type Decision Tree

```
Is there a user (resource owner) involved?
├── NO  ──► Use Client Credentials Grant
└── YES
    │
    Is the client server-side (can keep secrets)?
    ├── YES ──► Use Authorization Code Grant  ✅ (Most recommended)
    └── NO
        │
        Is it a legacy system that can't use auth code?
        ├── YES ──► Resource Owner Password Credentials (last resort)
        └── NO  ──► Use Authorization Code + PKCE (for public clients)
```

### Parameter Quick Reference

| Parameter | Used In | Description |
|---|---|---|
| `response_type` | Auth request | `"code"` or `"token"` |
| `client_id` | Auth + Token requests | Client's registered identifier |
| `client_secret` | Token request | Client's secret (confidential clients) |
| `redirect_uri` | Auth + Token requests | Where to redirect after auth |
| `scope` | Auth + Token requests/responses | Access scope |
| `state` | Auth request + response | CSRF protection value |
| `code` | Auth response + Token request | Authorization code |
| `grant_type` | Token request | Grant type identifier |
| `access_token` | Token response | The issued token |
| `token_type` | Token response | Type of token (e.g., `Bearer`) |
| `expires_in` | Token response | Seconds until token expires |
| `refresh_token` | Token request + response | Used to refresh access tokens |
| `error` | Error responses | Error code string |
| `error_description` | Error responses | Human-readable error description |
| `error_uri` | Error responses | URI with error details |
| `username` / `password` | Password grant request | Resource owner credentials |

### RFC 6749 vs OAuth 1.0

| Feature | OAuth 1.0 | OAuth 2.0 |
|---|---|---|
| Signature required | ✅ Yes (complex) | ❌ No (uses TLS) |
| Bearer tokens | ❌ No | ✅ Yes |
| Multiple grant types | ❌ No | ✅ Yes (4+) |
| Backward compatible | — | ❌ Not with 1.0 |
| Extensibility | Limited | ✅ Rich framework |

---

*Notes based on RFC 6749 — "The OAuth 2.0 Authorization Framework" by Dick Hardt (ed.), October 2012.*

---