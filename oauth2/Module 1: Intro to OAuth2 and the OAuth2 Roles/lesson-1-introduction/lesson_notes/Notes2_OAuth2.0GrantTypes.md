# OAuth 2.0 Grant Types

---

## Table of Contents

1. [What is a Grant Type?](#1-what-is-a-grant-type)
2. [Grant Types Overview](#2-grant-types-overview)
3. [Authorization Code Grant](#3-authorization-code-grant)
4. [PKCE Extension](#4-pkce-extension-rfc-7636)
5. [Client Credentials Grant](#5-client-credentials-grant)
6. [Device Authorization Grant](#6-device-authorization-grant-device-code)
7. [Refresh Token Grant](#7-refresh-token-grant)
8. [Comparison Table](#8-comparison-table)
9. [Quick Reference — Request Parameters](#9-quick-reference--request-parameters)

---

## 1. What is a Grant Type?

In OAuth 2.0, a **grant type** refers to the method an application uses to obtain an access token. Each grant type is designed for a specific use case and client profile.

> **Key idea:** The grant type tells the authorization server *how* the client is proving it has the right to receive an access token.

OAuth 2.0 defines several built-in grant types and also provides a framework for defining custom ones via extensions.

---

## 2. Grant Types Overview

```
┌──────────────────────────────────────────────────────────────────────┐
│                    OAUTH 2.0 GRANT TYPES                             │
├─────────────────────────────┬────────────────────────────────────────┤
│  Authorization Code         │ Web & mobile apps with a user present  │
├─────────────────────────────┼────────────────────────────────────────┤
│  Authorization Code + PKCE  │ Public clients (no client secret)      │
├─────────────────────────────┼────────────────────────────────────────┤
│  Client Credentials         │ Machine-to-machine, no user involved   │
├─────────────────────────────┼────────────────────────────────────────┤
│  Device Code                │ Browserless / input-constrained devices│
├─────────────────────────────┼────────────────────────────────────────┤
│  Refresh Token              │ Obtaining new tokens after expiry      │
└─────────────────────────────┴────────────────────────────────────────┘
```

---

## 3. Authorization Code Grant

### What it is

The **Authorization Code** grant is the most common OAuth 2.0 grant type. It is used by both web apps and native apps to obtain an access token after a user authorizes the application. It is suitable for both **confidential clients** (with a client secret) and **public clients** (using PKCE).

The defining characteristic of this flow is an **intermediary authorization code** — the client never receives the access token through the browser, only through a secure back-channel request. This prevents token interception.

### When to use it

- Web applications (server-side rendered)
- Native/mobile applications (with PKCE)
- Any scenario where a **user is present** and must grant permission

### Flow Diagram

```
User/Browser                   App (Client)              Auth Server
     |                              |                          |
     |  1. User clicks "Login"      |                          |
     |----------------------------->|                          |
     |                              |                          |
     |  2. App builds auth URL      |                          |
     |     and redirects browser    |                          |
     |<-----------------------------|                          |
     |                              |                          |
     |  3. Browser visits auth URL  |                          |
     |-------------------------------------------------------->|
     |                              |                          |
     |  4. Auth server shows        |                          |
     |     permission prompt        |                          |
     |<--------------------------------------------------------|
     |                              |                          |
     |  5. User approves            |                          |
     |-------------------------------------------------------->|
     |                              |                          |
     |  6. Auth server redirects    |                          |
     |     back with ?code=...      |                          |
     |<--------------------------------------------------------|
     |                              |                          |
     |  7. Browser follows redirect |                          |
     |     delivering the code      |                          |
     |----------------------------->|                          |
     |                              |  8. POST /token          |
     |                              |     (code + secret)      |
     |                              |------------------------->|
     |                              |                          |
     |                              |  9. Access Token (JSON)  |
     |                              |<-------------------------|
```

### Step-by-Step Breakdown

#### Step 1 — Build the Authorization URL

The app redirects the user's browser to the authorization server with the following parameters:

```
https://authorization-server.com/auth
  ?response_type=code
  &client_id=29352915982374239857
  &redirect_uri=https%3A%2F%2Fexample-app.com%2Fcallback
  &scope=create+delete
  &state=xcoiv98y2kd22vusuye3kch
```

| Parameter | Required? | Description |
|---|---|---|
| `response_type=code` | **Required** | Signals Authorization Code flow |
| `client_id` | **Required** | Public identifier for the app |
| `redirect_uri` | **Required** | Where the server sends the user after approval |
| `scope` | Optional | Space-separated permissions being requested |
| `state` | **Recommended** | Random string for CSRF protection |

#### Step 2 — User Approves the Request

The authorization server displays a consent screen. The user reviews and approves (or denies) the requested permissions.

#### Step 3 — Receive the Authorization Code

After approval, the server redirects the browser back to the `redirect_uri` with a `code` and the original `state`:

```
https://example-app.com/redirect
  ?code=g0ZGZmNjVmOWIjNTk2NTk4ZTYyZGI3
  &state=xcoiv98y2kd22vusuye3kch
```

> ⚠️ **The app must verify the `state` value matches** what it originally sent. This prevents CSRF attacks.

The authorization code is **short-lived** — typically 1 to 10 minutes.

#### Step 4 — Exchange the Code for an Access Token

The app makes a **back-channel POST request** to the token endpoint:

```
POST /oauth/token HTTP/1.1
Host: authorization-server.com

grant_type=authorization_code
&code=g0ZGZmNjVmOWIjNTk2NTk4ZTYyZGI3
&redirect_uri=https://example-app.com/redirect
&code_verifier=Th7UHJdLswIYQxwSg29DbK1a_d9o41uNMTRmuH0PM8zyoMAQ
&client_id=xxxxxxxxxx
&client_secret=xxxxxxxxxx
```

| Parameter | Required? | Description |
|---|---|---|
| `grant_type` | **Required** | Must be `"authorization_code"` |
| `code` | **Required** | The code received in the redirect |
| `redirect_uri` | Conditional | Required if included in the initial request; must match exactly |
| `client_id` | Conditional | Required if not authenticating via HTTP Basic Auth |
| `client_secret` | Conditional | Required for confidential clients |
| `code_verifier` | Required for PKCE | The plaintext secret used to generate the `code_challenge` |

#### Step 5 — Receive the Access Token

If everything is valid, the server returns an access token:

```json
HTTP/1.1 200 OK
Content-Type: application/json
Cache-Control: no-store
Pragma: no-cache

{
  "access_token": "MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3",
  "token_type": "bearer",
  "expires_in": 3600,
  "refresh_token": "IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk",
  "scope": "create delete"
}
```

### Server-Side Verification Checklist

When receiving the token request, the authorization server must verify:

- [ ] All required parameters are present
- [ ] Client is authenticated (if credentials were issued)
- [ ] Authorization code is valid and has not expired
- [ ] Authorization code was issued to this specific client
- [ ] `redirect_uri` in token request matches the one used to generate the code
- [ ] For PKCE: SHA256 hash of `code_verifier` matches the stored `code_challenge`

### Security: Preventing Replay Attacks

- Authorization codes must be **single-use**
- If a code is used more than once → treat as an attack
- The server should **revoke all access tokens** previously issued from that compromised code
- For self-encoded codes: cache the code for its lifetime to detect reuse

---

## 4. PKCE Extension (RFC 7636)

### What it is

**PKCE** (Proof Key for Code Exchange, pronounced "pixy") is an extension to the Authorization Code flow that prevents **CSRF attacks** and **authorization code injection attacks**.

### Important Clarifications

> ⚠️ **PKCE is NOT a replacement for client authentication.** It is an additional security layer.
> ⚠️ **PKCE does NOT make a public client into a confidential client.**
> ✅ **Confidential clients using a client secret should ALSO use PKCE.**

### When to use it

- Mobile and native apps (originally designed for these)
- Browser-based single-page apps (SPAs)
- **All OAuth clients** — recommended universally as best practice

### How PKCE Works

```
App (Client)                                      Auth Server
     |                                                  |
     |  1. Generate random secret: code_verifier        |
     |     e.g. "abc123xyz..."                          |
     |                                                  |
     |  2. Hash it: code_challenge = SHA256(verifier)   |
     |                                                  |
     |  3. Send code_challenge in auth request          |
     |------------------------------------------------->|
     |     (server stores code_challenge)               |
     |                                                  |
     |  4. Receive auth code in redirect                |
     |<-------------------------------------------------|
     |                                                  |
     |  5. Send code_verifier in token request          |
     |------------------------------------------------->|
     |                                                  |
     |  6. Server hashes verifier, compares to          |
     |     stored code_challenge. Match = valid.        |
     |                                                  |
     |  7. Access token issued                          |
     |<-------------------------------------------------|
```

### PKCE Parameters

| Parameter | Where Used | Description |
|---|---|---|
| `code_verifier` | Token request | The original random secret (plaintext) |
| `code_challenge` | Authorization request | SHA256 hash of the `code_verifier`, Base64URL-encoded |
| `code_challenge_method` | Authorization request | Must be `"S256"` (SHA256) |

### Why It Protects Against Code Injection

If an attacker intercepts the authorization code, they cannot exchange it for a token because they don't know the `code_verifier` — only the legitimate client that generated it does.

---

## 5. Client Credentials Grant

### What it is

The **Client Credentials** grant is used when an application accesses resources on its **own behalf**, with no user involved. This is the standard grant type for **machine-to-machine (M2M)** communication.

### When to use it

- Backend services or daemons
- Microservices communicating with each other
- Scripts or jobs accessing an API with their own identity
- Any scenario where **there is no user** to authorize

### Flow Diagram

```
Client Application                         Authorization Server
        |                                          |
        |  POST /token                             |
        |  grant_type=client_credentials           |
        |  client_id=xxx                           |
        |  client_secret=xxx                       |
        |----------------------------------------->|
        |                                          |
        |  Validates client credentials            |
        |                                          |
        |  200 OK { access_token: "..." }          |
        |<-----------------------------------------|
        |                                          |
        |  Use access token to call Resource Server|
        |----------------------------------------->|
```

### Token Request

```
POST /token HTTP/1.1
Host: authorization-server.com

grant_type=client_credentials
&client_id=xxxxxxxxxx
&client_secret=xxxxxxxxxx
```

| Parameter | Required? | Description |
|---|---|---|
| `grant_type` | **Required** | Must be `"client_credentials"` |
| `scope` | Optional | Requested access scope (not widely supported) |
| `client_id` + `client_secret` | **Required** | Client authentication (via params or HTTP Basic Auth) |

### Key Characteristics

- **No user authorization step** — the client authenticates itself directly
- **No refresh token** is typically issued (client can simply request a new token)
- **Only confidential clients** should use this flow (they can keep the secret safe)
- Scope support varies by authorization server

---

## 6. Device Authorization Grant (Device Code)

### What it is

The **Device Authorization Grant** (formerly Device Flow) allows devices with **no browser or limited input capability** to obtain an access token. The user completes the authorization on a separate device (phone or computer).

### When to use it

- Smart TV apps (e.g., Apple TV, Roku)
- CLI tools (e.g., GitHub CLI)
- Hardware devices (e.g., video encoders streaming to YouTube)
- IoT devices
- Any device that **cannot open a browser** or has limited keyboard input

### Flow Diagram

```
Device (Client)           Auth Server           User's Phone/Computer
      |                        |                         |
      |  POST /device_code     |                         |
      |----------------------->|                         |
      |                        |                         |
      |  { device_code,        |                         |
      |    user_code,          |                         |
      |    verification_uri }  |                         |
      |<-----------------------|                         |
      |                        |                         |
      |  Display to user:      |                         |
      |  "Go to example.com/activate                     |
      |   and enter: ABCD-1234"|                         |
      |                        |                         |
      |  [Poll every interval] |                         |
      |  POST /token           |   User visits URL &     |
      |  device_code=...       |   enters user_code      |
      |----------------------->|<------------------------|
      |                        |                         |
      |  (while pending)       |  User approves          |
      |  error:                |------------------------>|
      |  authorization_pending |                         |
      |<-----------------------|                         |
      |                        |                         |
      |  (after approval)      |                         |
      |  200 OK                |                         |
      |  { access_token: ... } |                         |
      |<-----------------------|                         |
```

### Phase 1 — Device Requests a Code

The device POSTs to the device authorization endpoint:

```
POST /device_code HTTP/1.1
Host: authorization-server.com

client_id=a17c21ed
&scope=create
```

The server responds with:

```json
{
  "device_code": "NGU5OWFiNjQ5YmQwNGY3YTdmZTEyNzQ3YzQ1YSA",
  "user_code": "ABCD-1234",
  "verification_uri": "https://example.com/activate",
  "expires_in": 1800,
  "interval": 5
}
```

| Field | Description |
|---|---|
| `device_code` | Used by the device to poll for the access token |
| `user_code` | Short, human-readable code the user enters on their device |
| `verification_uri` | URL the user visits to enter the code |
| `expires_in` | Seconds until the device code expires |
| `interval` | Minimum seconds the device must wait between polling attempts |

### Phase 2 — Device Polls for the Token

```
POST /token HTTP/1.1
Host: authorization-server.com
Content-type: application/x-www-form-urlencoded

grant_type=urn:ietf:params:oauth:grant-type:device_code
&client_id=a17c21ed
&device_code=NGU5OWFiNjQ5YmQwNGY3YTdmZTEyNzQ3YzQ1YSA
```

> The `grant_type` value for the Device Code flow is the full URI:
> `urn:ietf:params:oauth:grant-type:device_code`

### Polling Responses

| HTTP Status | Error | Meaning |
|---|---|---|
| `400` | `authorization_pending` | User has not yet approved or denied — keep polling |
| `400` | `slow_down` | Device is polling too fast — increase interval |
| `400` | `access_denied` | User denied the request — stop polling |
| `400` | `expired_token` | Device code expired — start over with a new device code |
| `200` | *(none)* | User approved — access token is in the response |

### Successful Response

```json
HTTP/1.1 200 OK
Content-Type: application/json
Cache-Control: no-store

{
  "access_token": "AYjcyMzY3ZDhiNmJkNTY",
  "refresh_token": "RjY2NjM5NzA2OWJjuE7c",
  "token_type": "Bearer",
  "expires": 3600,
  "scope": "create"
}
```

---

## 7. Refresh Token Grant

### What it is

The **Refresh Token** grant allows a client to obtain a new access token after the current one expires, **without requiring the user to log in again**. It uses the refresh token issued alongside the original access token.

### When to use it

- After an access token expires and the user's session should be maintained
- To silently renew tokens in long-running sessions
- Any client that received a refresh token during the original token issuance

### Flow Diagram

```
Client Application                         Authorization Server
        |                                          |
        |  [Access token expires]                  |
        |                                          |
        |  POST /token                             |
        |  grant_type=refresh_token                |
        |  refresh_token=xxxxxxxxxxx               |
        |  client_id=xxx                           |
        |  client_secret=xxx (if applicable)       |
        |----------------------------------------->|
        |                                          |
        |  Validates refresh token                 |
        |  Verifies client identity                |
        |                                          |
        |  200 OK { new access_token, ... }        |
        |<-----------------------------------------|
```

### Token Request

```
POST /oauth/token HTTP/1.1
Host: authorization-server.com

grant_type=refresh_token
&refresh_token=xxxxxxxxxxx
&client_id=xxxxxxxxxx
&client_secret=xxxxxxxxxx
```

| Parameter | Required? | Description |
|---|---|---|
| `grant_type` | **Required** | Must be `"refresh_token"` |
| `refresh_token` | **Required** | The refresh token previously issued to the client |
| `scope` | Optional | Must not exceed original granted scope; defaults to original scope if omitted |
| `client_id` + `client_secret` | Conditional | Required if the client was issued a secret |

### Server-Side Verification

Before issuing a new token, the server must verify:

- [ ] All required parameters are present
- [ ] Client is authenticated (if applicable)
- [ ] Refresh token is valid and has not expired
- [ ] Refresh token was issued to the requesting client

### Response

The response is identical to a standard access token response. Optionally, a **new refresh token** may be included:

```json
{
  "access_token": "newAccessTokenHere",
  "token_type": "bearer",
  "expires_in": 3600,
  "refresh_token": "newRefreshTokenHere"
}
```

> If no new refresh token is included in the response, the client should assume the **existing refresh token is still valid**.

### Refresh Token Behavior Notes

- Refresh tokens are typically only used with **confidential clients**, but the Authorization Code flow without a client secret may also produce a refresh token
- If the client has no secret, no client authentication is present in the refresh request
- Scope can be narrowed on refresh (requesting less access), but **never expanded**

---

## 8. Comparison Table

| Feature | Auth Code | Auth Code + PKCE | Client Credentials | Device Code | Refresh Token |
|---|---|---|---|---|---|
| **User involved?** | ✅ Yes | ✅ Yes | ❌ No | ✅ Yes | ❌ No |
| **Client type** | Confidential | Public or Confidential | Confidential only | Public or Confidential | Either |
| **Client secret required?** | ✅ Yes | ❌ No (PKCE replaces it for public clients) | ✅ Yes | ❌ No | Conditional |
| **Refresh token issued?** | ✅ Yes | ✅ Yes | ❌ Usually not | ✅ Yes | N/A |
| **Browser required?** | ✅ Yes | ✅ Yes | ❌ No | ❌ No (uses second device) | ❌ No |
| **Primary use case** | Web/native apps | Mobile/SPA | M2M / server | TV, IoT, CLI | Token renewal |
| **`grant_type` value** | `authorization_code` | `authorization_code` | `client_credentials` | `urn:ietf:...device_code` | `refresh_token` |

---

## 9. Quick Reference — Request Parameters

### Authorization Code — Auth Request

| Parameter | Required? |
|---|---|
| `response_type=code` | ✅ Required |
| `client_id` | ✅ Required |
| `redirect_uri` | Conditional |
| `scope` | Optional |
| `state` | ✅ Strongly recommended |
| `code_challenge` | Required for PKCE |
| `code_challenge_method` | Required for PKCE |

### Authorization Code — Token Request

| Parameter | Required? |
|---|---|
| `grant_type=authorization_code` | ✅ Required |
| `code` | ✅ Required |
| `redirect_uri` | Conditional |
| `client_id` | Conditional |
| `client_secret` | Conditional (confidential clients) |
| `code_verifier` | Required for PKCE |

### Client Credentials — Token Request

| Parameter | Required? |
|---|---|
| `grant_type=client_credentials` | ✅ Required |
| `client_id` + `client_secret` | ✅ Required |
| `scope` | Optional |

### Device Code — Device Authorization Request

| Parameter | Required? |
|---|---|
| `client_id` | ✅ Required |
| `scope` | Optional |

### Device Code — Token Poll Request

| Parameter | Required? |
|---|---|
| `grant_type=urn:ietf:params:oauth:grant-type:device_code` | ✅ Required |
| `client_id` | ✅ Required |
| `device_code` | ✅ Required |

### Refresh Token — Token Request

| Parameter | Required? |
|---|---|
| `grant_type=refresh_token` | ✅ Required |
| `refresh_token` | ✅ Required |
| `scope` | Optional |
| `client_id` + `client_secret` | Conditional |

---

