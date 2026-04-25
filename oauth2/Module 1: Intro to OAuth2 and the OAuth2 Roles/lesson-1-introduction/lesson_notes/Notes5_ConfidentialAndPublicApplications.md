# Confidential and Public Applications 
### Based on OAuth 2.0 Specification & Auth0 Documentation

---

## Table of Contents

1. [Overview — What is the Difference?](#1-overview--what-is-the-difference)
2. [Key Classifications](#2-key-classifications)
3. [Authentication Methods](#3-authentication-methods)
4. [Confidential Applications](#4-confidential-applications)
5. [Public Applications](#5-public-applications)
6. [Side-by-Side Comparison](#6-side-by-side-comparison)
7. [ID Token Signing](#7-id-token-signing)
8. [How to Check: Confidential or Public?](#8-how-to-check-confidential-or-public)
9. [Decision Flow — Which Type is Your App?](#9-decision-flow--which-type-is-your-app)
10. [Quick Reference](#10-quick-reference)

---

## 1. Overview — What is the Difference?

According to the **OAuth 2.0 specification**, all applications fall into one of two categories:

```
┌─────────────────────────────────────────────────────────────────────┐
│                  THE CORE QUESTION                                  │
│                                                                     │
│   Can your application securely store credentials                   │
│   (client ID + secret) without exposing them?                       │
│                                                                     │
│         YES  ──►  CONFIDENTIAL application                          │
│         NO   ──►  PUBLIC application                                │
└─────────────────────────────────────────────────────────────────────┘
```

The classification affects:
- Which **authentication methods** the app can use
- Which **grant types** are available to the app
- How **ID tokens** issued to the app must be signed
- Whether a **client secret** can safely be assigned

---

## 2. Key Classifications

There are **two independent classification axes** for applications. It is important not to conflate them:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    TWO INDEPENDENT CLASSIFICATIONS                      │
├──────────────────────────────┬──────────────────────────────────────────┤
│  CONFIDENTIAL vs PUBLIC      │  FIRST-PARTY vs THIRD-PARTY              │
├──────────────────────────────┼──────────────────────────────────────────┤
│  Describes the application's │  Describes the trust relationship:       │
│  authentication capability:  │  who owns and operates the application.  │
│                              │                                          │
│  Can it securely hold a      │  First-party: owned by the same org as   │
│  secret?                     │  the authorization server.               │
│                              │                                          │
│                              │  Third-party: owned by an external       │
│                              │  developer or partner.                   │
└──────────────────────────────┴──────────────────────────────────────────┘

These are INDEPENDENT — a first-party app can be public,
and a third-party app can be confidential.
```

---

## 3. Authentication Methods

Auth0 determines whether an application is confidential or public based on its **Authentication Method** — the mechanism the app uses to authenticate against the token endpoint.

The Auth0 Dashboard (Credentials tab) shows the following options:

```
┌──────────────────────────────────────────────────────────────────────────┐
│                      AUTHENTICATION METHODS                              │
├──────────────────────────┬───────────────┬───────────────────────────────┤
│  Method                  │  App Type     │  Description                  │
├──────────────────────────┼───────────────┼───────────────────────────────┤
│  None                    │  PUBLIC       │  No client secret. App cannot │
│                          │               │  authenticate via credentials.│
├──────────────────────────┼───────────────┼───────────────────────────────┤
│  Client Secret (Post)    │  CONFIDENTIAL │  Secret sent in POST request  │
│                          │               │  body parameters.             │
├──────────────────────────┼───────────────┼───────────────────────────────┤
│  Client Secret (Basic)   │  CONFIDENTIAL │  Secret sent via HTTP Basic   │
│                          │               │  authentication header.       │
├──────────────────────────┼───────────────┼───────────────────────────────┤
│  Private Key JWT         │  CONFIDENTIAL │  Asymmetric authentication    │
│                          │               │  using a private/public key   │
│                          │               │  pair. No shared secret.      │
├──────────────────────────┼───────────────┼───────────────────────────────┤
│  mTLS (CA-signed)        │  CONFIDENTIAL │  Mutual TLS using a           │
│                          │               │  certificate authority.       │
├──────────────────────────┼───────────────┼───────────────────────────────┤
│  mTLS (self-signed)      │  CONFIDENTIAL │  Mutual TLS using a           │
│                          │               │  self-signed certificate.     │
└──────────────────────────┴───────────────┴───────────────────────────────┘
```

> As seen in the Auth0 Dashboard: when **Private Key JWT** is selected (shown with a blue border), the app is classified as **confidential**. A "Credential required" warning appears until a credential is assigned.

---

## 4. Confidential Applications

### Definition

A **confidential application** is one that can hold credentials — a client secret or private key — in a **secure backend environment** inaccessible to end users or attackers.

```
┌──────────────────────────────────────────────────────────────────────┐
│                    CONFIDENTIAL APPLICATION                          │
│                                                                      │
│   ┌─────────────────────────┐        ┌──────────────────────────┐    │
│   │   Secure Backend Server │        │   Authorization Server   │    │
│   │                         │        │                          │    │
│   │  client_id     = "xyz"  │──────► │  Validates:              │    │
│   │  client_secret = "abc"  │        │  - client_id             │    │
│   │                         │◄────── │  - client_secret         │    │
│   │  (SECRET IS SAFE HERE)  │        │                          │    │
│   └─────────────────────────┘        └──────────────────────────┘    │
│                                                                      │
│   Secret never exposed to the browser, user, or device.              │
└──────────────────────────────────────────────────────────────────────┘
```

### Examples of Confidential Applications

| Application Type | Example | Grant Type Used |
|---|---|---|
| **Web application** | Server-rendered app (Node.js, Django, Rails) | Authorization Code Flow |
| **Machine-to-machine** | Backend service, API daemon, microservice | Client Credentials Flow |
| **Web app with password** | Legacy first-party app with trusted users | Resource Owner Password Flow |

### Grant Types Available to Confidential Applications

- **Authorization Code Flow** — standard web app login with user authorization
- **Client Credentials Flow** — machine-to-machine, no user involved
- **Resource Owner Password Flow** — direct username/password exchange (high-trust only)
- **Resource Owner Password Flow with Realm Support** — same as above, with realm-based routing

### Confidential App Flow — Authorization Code

```
User         Browser          Backend Server          Auth Server        Resource Server
  |              |                   |                     |                    |
  |── Login ────►|                   |                     |                    |
  |              |── Auth Request ──►|                     |                    |
  |              |                   |── Redirect User ───►|                    |
  |              |◄──────────────────|                     |                    |
  |              |────── Login ──────────────────────────► |                    |
  |              |                   |                     |                    |
  |              |◄─── Auth Code ────────────────────────  |                    |
  |              |── Code ──────────►|                     |                    |
  |              |                   |── POST /token ──────►                    |
  |              |                   |   client_id         |                    |
  |              |                   |   client_secret ────►                    |
  |              |                   |   code              |                    |
  |              |                   |◄── Access Token ────|                    |
  |              |                   |                     |                    |
  |              |                   |────── API Call (Access Token) ──────────►|
  |              |                   |◄────── Protected Resource ────────────── |
  |              |◄── Response ──────|                     |                    |
```

> The client secret **never touches the browser** — it lives only on the backend server.

### Confidential App Flow — Client Credentials (M2M)

```
Backend Service / Daemon                    Authorization Server
         |                                          |
         |── POST /token                            |
         |   grant_type=client_credentials          |
         |   client_id=xxx                          |
         |   client_secret=xxx ─────────────────────►
         |                                          |
         |                     Validates credentials|
         |                                          |
         |◄── { access_token } ──────────────────── |
         |                                          |
         |── API call with access token ────────────────────────► Resource Server
```

---

## 5. Public Applications

### Definition

A **public application** is one that **cannot** securely store credentials. The application runs in an environment controlled by the user — a browser, a mobile device, or a desktop — where secrets can be extracted or observed.

```
┌──────────────────────────────────────────────────────────────────────┐
│                      PUBLIC APPLICATION                              │
│                                                                      │
│   ┌──────────────────────────────────┐                               │
│   │   User's Device / Browser        │                               │
│   │                                  │                               │
│   │   client_id     = "xyz" ✅       │                               │
│   │   client_secret = ???  ❌        │ ← Cannot be stored safely     │
│   │                                  │   Anyone can inspect the app  │
│   │   (JS source, app binary,        │   and extract secrets.        │
│   │    network traffic all exposed)  │                               │
│   └──────────────────────────────────┘                               │
│                                                                      │
│   Solution: Use PKCE instead of a client secret.                     │
└──────────────────────────────────────────────────────────────────────┘
```

### Examples of Public Applications

| Application Type | Example | Grant Type Used |
|---|---|---|
| **Single-Page Application (SPA)** | React, Vue, Angular app running in browser | Authorization Code Flow + PKCE |
| **Native mobile app** | iOS or Android app | Authorization Code Flow + PKCE |
| **Desktop native app** | Electron app, CLI tool | Authorization Code Flow + PKCE |
| **Legacy browser app** | JavaScript-only app (older pattern) | Implicit Flow |

### Grant Types Available to Public Applications

- **Authorization Code Flow + PKCE** — the recommended approach for all public clients
- **Implicit Flow** — legacy pattern for browser-based apps (now discouraged)

> ⚠️ Public applications **cannot** use grant types that require sending a `client_secret`, because they have no way to store one safely.

### Public App Flow — Authorization Code + PKCE

```
User         Browser / Native App               Auth Server         Resource Server
  |                   |                               |                    |
  |── Open app ──────►|                               |                    |
  |                   |                               |                    |
  |                   | Generate:                     |                    |
  |                   |  code_verifier (random)       |                    |
  |                   |  code_challenge = SHA256(v)   |                    |
  |                   |                               |                    |
  |                   |── GET /authorize              |                    |
  |                   |   client_id=xxx               |                    |
  |                   |   code_challenge=...          |                    |
  |                   |   code_challenge_method=S256 ►|                    |
  |                   |                               |                    |
  |◄─ Login prompt ─────────────────────────────────  |                    |
  |── Approve ────────────────────────────────────►   |                    |
  |                   |                               |                    |
  |                   |◄── Auth Code ───────────────  |                    |
  |                   |                               |                    |
  |                   |── POST /token                 |                    |
  |                   |   code=xxx                    |                    |
  |                   |   code_verifier=xxx           |                    |
  |                   |   (NO client_secret) ────────►|                    |
  |                   |                               |                    |
  |                   |    Verifies:                  |                    |
  |                   |    SHA256(verifier)==challenge|                    |
  |                   |                               |                    |
  |                   |◄── Access Token ────────────  |                    |
  |                   |                               |                    |
  |                   |── API call ───────────────────────────────────────►|
  |                   |◄── Protected Resource ──────────────────────────── |
```

---

## 6. Side-by-Side Comparison

```
┌─────────────────────────────┬──────────────────────────────┬──────────────────────────────┐
│  Feature                    │  Confidential Application    │  Public Application          │
├─────────────────────────────┼──────────────────────────────┼──────────────────────────────┤
│  Can store secrets?         │  ✅ Yes                      │  ❌ No                       │
├─────────────────────────────┼──────────────────────────────┼──────────────────────────────┤
│  Runs where?                │  Secure backend server       │  User's browser or device    │
├─────────────────────────────┼──────────────────────────────┼──────────────────────────────┤
│  Uses client_secret?        │  ✅ Yes                      │  ❌ No                       │
├─────────────────────────────┼──────────────────────────────┼──────────────────────────────┤
│  Auth method                │  Basic, Post, Private Key    │  None (PKCE instead)         │
│                             │  JWT, mTLS                   │                              │
├─────────────────────────────┼──────────────────────────────┼──────────────────────────────┤
│  Auth Code Flow?            │  ✅ Yes (with secret)        │  ✅ Yes (with PKCE)          │
├─────────────────────────────┼──────────────────────────────┼──────────────────────────────┤
│  Client Credentials Flow?   │  ✅ Yes                      │  ❌ No                       │
├─────────────────────────────┼──────────────────────────────┼──────────────────────────────┤
│  Implicit Flow?             │  Possible but discouraged    │  Legacy only (discouraged)   │
├─────────────────────────────┼──────────────────────────────┼──────────────────────────────┤
│  ID token signing           │  HS256 or RS256              │  RS256 only                  │
├─────────────────────────────┼──────────────────────────────┼──────────────────────────────┤
│  Examples                   │  Web app, M2M service        │  SPA, native/mobile app      │
├─────────────────────────────┼──────────────────────────────┼──────────────────────────────┤
│  Dashboard Credentials tab? │  ✅ Available                │  ❌ Not available            │
└─────────────────────────────┴──────────────────────────────┴──────────────────────────────┘
```

---

## 7. ID Token Signing

Because ID tokens may contain sensitive user identity information, how they are signed depends on the application type:

### Confidential Applications — Two Options

```
┌──────────────────────────────────────────────────────────────┐
│             ID TOKEN SIGNING — CONFIDENTIAL APP              │
├───────────────────────────┬──────────────────────────────────┤
│  HS256 (Symmetric)        │  RS256 (Asymmetric)              │
├───────────────────────────┼──────────────────────────────────┤
│  Signed using the app's   │  Signed using a private key      │
│  own client_secret        │  held by the auth server         │
│                           │                                  │
│  Both the auth server     │  Verified using the auth         │
│  and app share the same   │  server's public key (JWKS)      │
│  secret to sign/verify    │                                  │
│                           │                                  │
│  ✅ Simpler setup         │  ✅ More secure (no shared       │
│  ⚠️ Secret must be kept   │     secret between parties)      │
│     safe on both sides    │                                  │
└───────────────────────────┴──────────────────────────────────┘
```

### Public Applications — One Option Only

```
┌──────────────────────────────────────────────────────────────┐
│               ID TOKEN SIGNING — PUBLIC APP                  │
├──────────────────────────────────────────────────────────────┤
│  RS256 (Asymmetric) ONLY                                     │
│                                                              │
│  Auth Server signs with:   Private Key  (kept secret)        │
│  App verifies with:        Public Key   (from JWKS endpoint) │
│                                                              │
│  Reason: Public apps cannot hold a shared secret (HS256      │
│  would require the app to store the client_secret, which     │
│  it cannot safely do).                                       │
└──────────────────────────────────────────────────────────────┘
```

### Signing Method Summary

| App Type | HS256 (Symmetric) | RS256 (Asymmetric) |
|---|---|---|
| Confidential | ✅ Allowed | ✅ Allowed |
| Public | ❌ Not allowed | ✅ Required |

---

## 8. How to Check: Confidential or Public?

Using the **Auth0 Dashboard**, follow these steps:

```
Step 1: Go to Applications > Applications
        Select the application name
            │
            ▼
Step 2: Is the "Credentials" tab visible?
            │
     ┌──────┴──────┐
     │             │
    NO            YES
     │             │
     ▼             ▼
  PUBLIC        Step 3: Open "Credentials" tab
  APPLICATION          Check "Authentication Method"
                              │
              ┌───────────────┼────────────────────┐
              │               │                    │
           "None"     "Client Secret      "Private Key JWT"
              │          Post/Basic"       or mTLS
              ▼               ▼                    ▼
           PUBLIC        CONFIDENTIAL         CONFIDENTIAL
```

### Authentication Method → App Type Mapping

| Auth Method | Application Type | Notes |
|---|---|---|
| **None** | Public | No secret. PKCE must be used. |
| **Client Secret (Post)** | Confidential | Secret in POST body |
| **Client Secret (Basic)** | Confidential | Secret in HTTP Basic header |
| **Private Key JWT** | Confidential | Asymmetric — no shared secret |
| **mTLS (CA-signed)** | Confidential | Certificate-based mutual TLS |
| **mTLS (self-signed)** | Confidential | Self-signed cert mutual TLS |
| **Unspecified** | Confidential | Backend server, treated as confidential |

> ⚠️ Note: A **Credential required** warning appears in the Auth0 dashboard (as shown in the screenshot) when a confidential authentication method like Private Key JWT is selected but no credential has been assigned yet.

---

## 9. Decision Flow — Which Type is Your App?

```
START: What kind of application are you building?
                    │
      ┌─────────────┴──────────────┐
      │                            │
  Runs on a                    Runs in a browser
  backend server?               or on a user's device?
      │                            │
      ▼                            ▼
Does it need to         Is it a Single-Page App (SPA)
act on its OWN          or Native / Mobile App?
behalf (no user)?           │
      │               ┌─────┴──────┐
   ┌──┴──┐           SPA         Native /
   YES   NO           │           Mobile
   │     │            │             │
   ▼     ▼            ▼             ▼
  M2M   Web      PUBLIC app    PUBLIC app
  App   App      Auth Code +   Auth Code +
   │     │       PKCE           PKCE
   ▼     ▼
CLIENT  AUTH
CREDS   CODE
FLOW    FLOW
   │     │
   └──┬──┘
      ▼
CONFIDENTIAL
APPLICATION
(needs client_secret
 or Private Key JWT)
```

---

## 10. Quick Reference

### Application Type by Example

| Example App | Type | Auth Method | Grant Type |
|---|---|---|---|
| React SPA | Public | None | Auth Code + PKCE |
| iOS native app | Public | None | Auth Code + PKCE |
| Angular SPA (legacy) | Public | None | Implicit Flow |
| Node.js web app | Confidential | Client Secret Basic | Auth Code Flow |
| Django web app | Confidential | Client Secret Post | Auth Code Flow |
| Microservice / daemon | Confidential | Private Key JWT | Client Credentials |
| CLI tool | Public | None | Device Code / Auth Code + PKCE |

### Key Rules to Remember

```
┌────────────────────────────────────────────────────────────────┐
│                        KEY RULES                               │
├────────────────────────────────────────────────────────────────┤
│  1. Public apps CANNOT use grant types requiring a secret.     │
│  2. Public apps MUST use RS256 for ID token signing.           │
│  3. Confidential apps CAN use HS256 or RS256.                  │
│  4. PKCE does NOT make a public app confidential.              │
│  5. Confidential/public is INDEPENDENT of first/third-party.   │
│  6. Auth method "None" = Public. Any other = Confidential.     │
│  7. Public app secrets in binaries CAN be extracted.           │
└────────────────────────────────────────────────────────────────┘
```

---
