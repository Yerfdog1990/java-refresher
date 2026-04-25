# Access Tokens vs Refresh Tokens 

---

## Table of Contents

1. [What is a Token?](#1-what-is-a-token)
2. [Token Types at a Glance](#2-token-types-at-a-glance)
3. [What is an Access Token?](#3-what-is-an-access-token)
4. [What is a Refresh Token?](#4-what-is-a-refresh-token)
5. [How Access Tokens Work — Flow](#5-how-access-tokens-work--flow)
6. [How Refresh Tokens Work — Flow](#6-how-refresh-tokens-work--flow)
7. [Full OAuth Token Lifecycle — Combined Flow](#7-full-oauth-token-lifecycle--combined-flow)
8. [Access Token vs Refresh Token — Comparison](#8-access-token-vs-refresh-token--comparison)
9. [Refresh Token Security](#9-refresh-token-security)
10. [When to Use Each Token](#10-when-to-use-each-token)
11. [Security Best Practices](#11-security-best-practices)
12. [Key Definitions](#12-key-definitions)

---

## 1. What is a Token?

A **token** is a piece of data that carries just enough information to facilitate the process of determining a user's identity or authorizing a user to perform an action. Tokens are artifacts that allow application systems to perform the **authorization** and **authentication** process.

Common identity frameworks that use token-based strategies:

| Framework | Purpose | Token Used |
|---|---|---|
| **OAuth 2.0** | Authorization | Access Token + Refresh Token |
| **OpenID Connect (OIDC)** | Authentication | ID Token |

> **Authorization vs Authentication:**
> - *Authentication* = verifying who the user is
> - *Authorization* = verifying what the user is allowed to do

The most common format for both access and refresh tokens is the **JSON Web Token (JWT)**.

---

## 2. Token Types at a Glance

```
┌───────────────────────────────────────────────────────────────────┐
│                     OAUTH 2.0 TOKEN TYPES                         │
├──────────────────┬────────────────────────────────────────────────┤
│  ID Token        │  Issued by OIDC. Contains user identity info   │
│                  │  (name, email, profile picture). Used by the   │
│                  │  client to build a user profile.               │
├──────────────────┼────────────────────────────────────────────────┤
│  Access Token    │  Issued by OAuth 2.0. Used by client apps to   │
│                  │  make authorized calls to an API/resource.     │
│                  │  Short-lived. Bearer token.                    │
├──────────────────┼────────────────────────────────────────────────┤
│  Refresh Token   │  Issued alongside access tokens. Used to       │
│                  │  obtain new access tokens silently after       │
│                  │  expiry. Longer-lived.                         │
└──────────────────┴────────────────────────────────────────────────┘
```

---

## 3. What is an Access Token?

An **access token** is a digital credential — typically a JWT — that a client application uses to make secure, authorized calls to a resource server on behalf of a user. Possession of the token alone is sufficient to access a resource (it is a **bearer token**).

### Key Characteristics

- Acts as a **key** granting access to protected resources
- Does **not** identify the user (that's the ID token's job) — it authorizes actions
- Usually **opaque** or formatted as a **JWT**
- Has a **very short lifespan** — typically 30–90 minutes
- Stored in **browser memory** or a **secure HTTP-only cookie**
- Transmitted over **HTTPS** with every API request
- **Non-revocable** beyond its set expiration (in most implementations)

### Example JWT Access Token (decoded)

```json
{
  "iss": "https://your-auth-server.com/",
  "sub": "auth0|123456",
  "aud": [
    "my-api-identifier",
    "https://your-auth-server.com/userinfo"
  ],
  "azp": "YOUR_CLIENT_ID",
  "exp": 1489179954,
  "iat": 1489143954,
  "scope": "openid profile email read:appointments"
}
```

| JWT Claim | Meaning |
|---|---|
| `iss` | Issuer — who created the token |
| `sub` | Subject — the user the token refers to |
| `aud` | Audience — who the token is intended for |
| `azp` | Authorized party — the client that requested it |
| `exp` | Expiration timestamp |
| `iat` | Issued-at timestamp |
| `scope` | The permissions granted |

> ⚠️ **Security note:** Access tokens are **bearer tokens** — anyone who holds one can use it. This is why keeping their lifespan short is critical. A stolen access token is valid until it expires.

---

## 4. What is a Refresh Token?

A **refresh token** is a long-lived credential issued alongside an access token. Its sole purpose is to obtain a **new access token** once the current one expires — without requiring the user to log in again.

### Key Characteristics

- Has a **much longer lifespan** than access tokens (typically days to 90 days)
- Stored **on the authorization server** (not in browser memory)
- **Never sent to the resource server** — only used with the authorization server
- **Revocable by design** — can be invalidated at any time
- Not all flows issue refresh tokens (e.g., the Implicit flow should not)
- There is **no such thing as a refresh token without an access token** — they work in tandem

### Refresh Token Lifespan Examples (Microsoft Identity Platform)

| Token | Default Lifespan |
|---|---|
| Access Token | 30–90 minutes (randomly assigned) |
| Refresh Token (most scenarios) | 90 days |
| Refresh Token (SPAs) | 24 hours |

---

## 5. How Access Tokens Work — Flow

Based on the diagram from Descope:

```
┌─────────────────────────────────────────────────────────────────┐
│                    HOW ACCESS TOKENS WORK                       │
└─────────────────────────────────────────────────────────────────┘

  User                  Client App            Auth Server       Resource Server
  (Resource Owner)
     |                      |                      |                  |
     |  1. Access service   |                      |                  |
     |--------------------->|                      |                  |
     |                      |                      |                  |
     |  2. Grant access     |                      |                  |
     |--------------------->|  3. Issue auth code  |                  |
     |                      |--------------------->|                  |
     |                      |                      |                  |
     |                      |  4. Return auth code |                  |
     |                      |     + receive        |                  |
     |                      |     access token     |                  |
     |                      |<---------------------|                  |
     |                      |                      |                  |
     |                      |  5. Use access token to access resource |
     |                      |---------------------------------------->|
     |                      |                      |                  |
     |                      |  Resource returned   |                  |
     |                      |<----------------------------------------|
```

### Step-by-Step

| Step | Actor | Action |
|---|---|---|
| **1** | User → Client App | User tries to access the service |
| **2** | User → Auth Server | User grants access permission |
| **3** | Client → Auth Server | Client requests authorization code |
| **4** | Auth Server → Client | Auth server validates and issues an access token |
| **5** | Client → Resource Server | Client presents the access token to access protected data |

---

## 6. How Refresh Tokens Work — Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                   HOW REFRESH TOKENS WORK                       │
└─────────────────────────────────────────────────────────────────┘

  Client App                             Authorization Server
      |                                           |
      |  [Initial login]                          |
      |  POST /token (grant + credentials)        |
      |------------------------------------------>|
      |                                           |
      |  200 OK                                   |
      |  { access_token, refresh_token }          |
      |<------------------------------------------|
      |                                           |
      |  [Use access_token for API calls]         |
      |  ...                                      |
      |  ...                                      |
      |                                           |
      |  [Access token EXPIRES]                   |
      |                                           |
      |  POST /token                              |
      |  grant_type=refresh_token                 |
      |  refresh_token=<token>                    |
      |------------------------------------------>|
      |                                           |
      |  Validates refresh token                  |
      |  Checks not expired / revoked             |
      |  Verifies client identity                 |
      |                                           |
      |  200 OK                                   |
      |  { new_access_token, new_refresh_token }  |
      |<------------------------------------------|
      |                                           |
      |  [Resume API calls with new access token] |
```

### Step-by-Step

| Step | Description |
|---|---|
| **1** | User logs in; auth server issues **both** an access token and a refresh token |
| **2** | Client uses the access token to make API calls |
| **3** | Access token **expires** (after 30–90 minutes) |
| **4** | Client sends the refresh token to the token endpoint |
| **5** | Auth server validates the refresh token |
| **6** | Auth server issues a **new access token** (and optionally a new refresh token) |
| **7** | Client resumes API calls — user never had to log in again |

---

## 7. Full OAuth Token Lifecycle — Combined Flow

```
User          Client App            Auth Server          Resource Server
  |                |                     |                      |
  |-- Login ------>|                     |                      |
  |                |-- Auth Request ---->|                      |
  |<-- Consent ----|<-- Consent Page ----|                      |
  |-- Approve ---->|                     |                      |
  |                |-- Auth Code ------->|                      |
  |                |<-- Access Token ----|                      |
  |                |   + Refresh Token   |                      |
  |                |                     |                      |
  |                |===[ Active Session ]===                    |
  |                |                     |                      |
  |                |-- Access Token ---->|-- Validate Token --->|
  |                |                     |                      |
  |                |<-- Protected Data --|<-- Resource ---------|
  |                |                     |                      |
  |                |  [Token expires]    |                      |
  |                |                     |                      |
  |                |-- Refresh Token --->|                      |
  |                |<-- New Access Token-|                      |
  |                |   (+ New Refresh)   |                      |
  |                |                     |                      |
  |                |-- New Access Token->|-- Validate Token --->|
  |                |<-- Protected Data --|<-- Resource ---------|
  |                |                     |                      |
  |                |  [Refresh expires]  |                      |
  |                |                     |                      |
  |<-- Re-login ---|  (Session ends,     |                      |
  |                |   user must log in) |                      |
```

---

## 8. Access Token vs Refresh Token — Comparison

| Feature | Access Token | Refresh Token |
|---|---|---|
| **Primary purpose** | Authorize access to resources | Obtain new access tokens silently |
| **Lifespan** | Short — 30 to 90 minutes | Long — hours to 90 days |
| **Who stores it** | Client (browser memory / HTTP-only cookie) | Authorization server |
| **Sent to** | Resource server (with every API call) | Only the authorization server |
| **Format** | Usually JWT | Opaque string or JWT |
| **Revocable?** | Usually not (expires naturally) | Yes — can be revoked at any time |
| **Independently useful?** | ✅ Yes | ❌ No — only useful alongside access tokens |
| **Required?** | ✅ Yes | ❌ Optional — some flows omit them |
| **Issued in Implicit Flow?** | ✅ Yes | ❌ No — spec says should not be issued |
| **User interaction needed?** | Yes (initial login) | No (silent refresh) |
| **Risk if stolen** | Attacker has access until expiry | Attacker can silently get new access tokens |
| **Exposure risk** | Higher (sent frequently) | Lower (sent rarely) |
| **Scope** | Access to specific resources/APIs | Only refreshing access tokens |

---

## 9. Refresh Token Security

### Why Refresh Tokens Are High-Value Targets

A long-lived refresh token is powerful — if stolen, an attacker can silently generate new access tokens for as long as the refresh token remains valid. This makes them attractive targets and demands careful security design.

### Refresh Token Rotation

**Refresh token rotation** means that every time a refresh token is used to get a new access token, the server **also issues a brand-new refresh token** and **invalidates the old one**.

```
BEFORE rotation (risky):

  Client ----[Refresh Token A]----> Auth Server
  Auth Server <---- New Access Token ----

  [Refresh Token A is still valid — forever]
  Attacker can also use Refresh Token A ❌

─────────────────────────────────────────────

AFTER rotation (safe):

  Client ----[Refresh Token A]----> Auth Server
  Auth Server <---- New Access Token
                    + New Refresh Token B ----

  [Refresh Token A is now INVALIDATED]
  Attacker can no longer use Token A ✅
```

Benefits of rotation:
- No long-lived static refresh token to steal
- Each token is valid only within one short access token lifetime
- Enables safer use of refresh tokens in SPAs

### Automatic Reuse Detection

This is a critical companion to rotation. The server tracks a **token family** — all refresh tokens descended from the original. If an already-used token is submitted again, the server treats it as a breach.

```
SCENARIO: Attacker steals Refresh Token 1

  Legitimate User         Auth Server          Malicious User
       |                       |                     |
       |-- Refresh Token 1 --->|                     |
       |<-- Access Token 2     |                     |
       |    Refresh Token 2 ---|                     |
       |                       |                     |
       |                       |<--- Refresh Token 1-|
       |                       |                     |
       |  ⚠️ REUSE DETECTED!   |                     |
       |  Token family         |                     |
       |  INVALIDATED          |                     |
       |                       |--Access Denied----->|
       |                       |                     |
       |-- Refresh Token 2 --->|                     |
       |<-- Access Denied -----|  (family revoked)   |
       |                       |                     |
       |  Must re-authenticate |                     |
```

> The server cannot determine whether the legitimate user or the attacker presented the old token first. So it **invalidates the entire token family** to be safe. Both parties must re-authenticate.

### Privacy Tools & Refresh Tokens

Browser privacy technologies like **Intelligent Tracking Prevention (ITP)** block session cookies, forcing re-authentication. Refresh token rotation is **not affected by ITP** because it does not rely on session cookies.

### Storing Refresh Tokens in Local Storage

Normally, storing tokens in `localStorage` is discouraged because XSS attacks can steal them. However, **with rotation enabled**, this risk is reduced:

- The refresh token is only valid for the lifespan of the current access token (short-lived)
- Even if stolen via XSS, the window of exploitation is very narrow
- This does **not** protect against persistent XSS attacks

> ✅ With rotation: local storage becomes acceptable
> ❌ Without rotation: avoid local storage for refresh tokens

---

## 10. When to Use Each Token

### Choosing the Right Flow by Client Type

| Client Type | Recommended Flow | Refresh Token? |
|---|---|---|
| Traditional server-side web app | Authorization Code Flow | ✅ Yes |
| Single-Page Application (SPA) | Authorization Code + PKCE | ✅ Yes (with rotation) |
| SPA not needing access token | Implicit Flow with Form Post | ❌ No |
| Client is the resource owner | Client Credentials Flow | ❌ Usually not |
| Highly trusted first-party app | Resource Owner Password Flow | ✅ Yes |

### When Access Tokens Alone Are Sufficient

- Short-lived sessions (e.g., a brief admin task)
- Internal tools where users are expected to re-authenticate frequently
- High-security environments that require strict session control

### When Refresh Tokens Are Recommended

- Mobile apps where users expect to stay logged in for days or weeks
- SPAs where frequent re-authentication would hurt UX
- B2B SaaS products with long user sessions
- Any scenario where users should remain logged in across browser tabs or page refreshes

> ⚠️ **Note:** Per the OAuth 2.0 spec, the Implicit Flow **should not** issue refresh tokens. This is one of many reasons the Implicit Flow is now largely discouraged.

---

## 11. Security Best Practices

| Practice | Description |
|---|---|
| **Limit lifespans** | Use the shortest possible lifespan for both token types. Balance security with UX. |
| **Minimize scopes** | Issue tokens with the least privilege necessary — don't over-grant permissions. |
| **Use HTTPS always** | All token transmission must happen over HTTPS to prevent interception. |
| **Store tokens securely** | Prefer HTTP-only cookies or secure server-side storage. Avoid `localStorage` unless rotation is in place. |
| **Use rotation** | Always use refresh token rotation, especially in SPAs and mobile apps. |
| **Implement reuse detection** | Treat reuse of an invalidated refresh token as a confirmed breach — revoke the token family immediately. |
| **Don't store sensitive data in tokens** | JWTs are only base64-encoded, not encrypted — anyone can decode them. |
| **Give tokens an expiration** | Never issue tokens without an expiry (`exp` claim). |
| **Revoke on logout** | Invalidate refresh tokens server-side when a user explicitly logs out. |

---

## 12. Key Definitions

| Term | Definition |
|---|---|
| **Token** | A piece of data used to prove identity or authorization in a system |
| **Bearer Token** | A token where possession alone grants access — no additional proof needed |
| **JWT (JSON Web Token)** | A compact, URL-safe format for representing claims between parties. Consists of header, payload, and signature. |
| **Access Token** | Short-lived credential used to access protected resources on behalf of a user |
| **Refresh Token** | Long-lived credential used to silently obtain new access tokens after expiry |
| **ID Token** | OIDC token containing user identity information (name, email, etc.) |
| **Token Family** | The chain of refresh tokens derived from an original refresh token; all invalidated on reuse detection |
| **Rotation** | Security strategy where a new refresh token is issued with every token refresh, and the old one is invalidated |
| **Reuse Detection** | Automatic detection and response when an already-used (invalidated) refresh token is submitted again |
| **Scope** | The set of permissions an access token grants (e.g., `read:profile`, `write:data`) |
| **ITP** | Intelligent Tracking Prevention — browser privacy tech that can block session cookies |
| **XSS** | Cross-Site Scripting — an attack where malicious scripts are injected into web pages |
| **PKCE** | Proof Key for Code Exchange — an extension to the auth code flow that prevents code injection attacks |
| **Silent Authentication** | A technique to renew a session without user interaction, using cookies or hidden iframes |

---

