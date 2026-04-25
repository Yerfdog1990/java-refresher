# Proof Key for Code Exchange (PKCE) — Detailed Notes
### RFC 7636 — September 2015

---

## Table of Contents

1. [What is PKCE?](#1-what-is-pkce)
2. [The Problem — Authorization Code Interception Attack](#2-the-problem--authorization-code-interception-attack)
3. [How PKCE Solves It — Core Concept](#3-how-pkce-solves-it--core-concept)
4. [Key Terminology](#4-key-terminology)
5. [PKCE Protocol — Step by Step](#5-pkce-protocol--step-by-step)
    - [Step 1: Create the Code Verifier](#step-1-create-the-code-verifier)
    - [Step 2: Create the Code Challenge](#step-2-create-the-code-challenge)
    - [Step 3: Send the Code Challenge with the Authorization Request](#step-3-send-the-code-challenge-with-the-authorization-request)
    - [Step 4: Server Returns the Authorization Code](#step-4-server-returns-the-authorization-code)
    - [Step 5: Send the Code Verifier to the Token Endpoint](#step-5-send-the-code-verifier-to-the-token-endpoint)
    - [Step 6: Server Verifies and Returns the Token](#step-6-server-verifies-and-returns-the-token)
6. [Full Flow Diagram — PKCE vs Standard Auth Code](#6-full-flow-diagram--pkce-vs-standard-auth-code)
7. [Code Challenge Methods — S256 vs plain](#7-code-challenge-methods--s256-vs-plain)
8. [Worked Example (from RFC 7636)](#8-worked-example-from-rfc-7636)
9. [Security Considerations](#9-security-considerations)
10. [PKCE Parameters Quick Reference](#10-pkce-parameters-quick-reference)
11. [Common Misconceptions](#11-common-misconceptions)

---

## 1. What is PKCE?

**PKCE** (Proof Key for Code Exchange, pronounced **"pixy"**) is an extension to the OAuth 2.0 Authorization Code Grant, defined in **RFC 7636**. It was created to protect public clients — apps that cannot securely store a client secret — against the **authorization code interception attack**.

### The Big Picture

```
┌────────────────────────────────────────────────────────────────────┐
│                        PKCE IN ONE LINE                            │
│                                                                    │
│  "Prove you are the same client that started this auth request,    │
│   by revealing the secret you hashed at the beginning."            │
└────────────────────────────────────────────────────────────────────┘
```

### Why It Exists

In standard OAuth 2.0, the authorization code is returned via a redirect URI. In native and mobile apps, this redirect uses a **custom URI scheme** (e.g., `myapp://callback`). Any app on the device can register the same custom URI scheme, meaning a malicious app can intercept the authorization code — and then use it to get an access token.

PKCE defeats this by requiring the client to prove it holds a **one-time secret** that only it generated and that the attacker cannot know.

---

## 2. The Problem — Authorization Code Interception Attack

### Attack Scenario (Native / Mobile App)

```
+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+
|           End Device (e.g., Smartphone)         |
|                                                 |
|  +------------------+    +-------------------+  |
|  | Legitimate       |    | Malicious App     |  |
|  | OAuth 2.0 App    |    | (registered same  |  |
|  |                  |    |  URI scheme!)     |  |
|  +------------------+    +-------------------+  |
|          |                        ^             |
|          |  (1) Auth Request      |             |
|          |  via browser/OS        |  (4) Auth   |
|          v                        |  Code sent  |
|  +--------------------------------+             |
|  |    Operating System / Browser  |  to BOTH    |
|  |                                |  apps!      |
|  +--------------------------------+             |
|          |              ^                       |
|          | (2) Auth     | (3) Auth Code         |
|          |  Request     |  returned             |
|          v              |                       |
+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+
                          |
              +-----------+------------+
              |    Authorization       |
              |       Server          |
              +-----------+------------+
                          |
                          | (5) Malicious app sends
                          |     intercepted code
                          v
                    Access Token ← Attacker wins!
```

### How the Attack Works — Step by Step

| Step | What Happens |
|---|---|
| **(1)** | Legitimate app initiates OAuth Authorization Request via OS/browser |
| **(2)** | Browser sends the authorization request to the auth server over TLS (secure) |
| **(3)** | Auth server returns the authorization **code** back to the registered redirect URI |
| **(4)** | The OS delivers the code to **all apps** registered for that custom URI scheme — including the malicious one |
| **(5)** | Malicious app uses the intercepted code to call the token endpoint |
| **(6)** | Auth server issues an access token **to the attacker** |

### Pre-Conditions for the Attack to Succeed

All of the following must be true:

1. A malicious app is installed on the device and registers the **same custom URI scheme** as the legitimate app
2. The OAuth 2.0 **authorization code grant** is being used
3. The attacker knows the `client_id` (and `client_secret` if any — both are extractable from app binaries)
4. The authorization code is intercepted before the legitimate app can use it

---

## 3. How PKCE Solves It — Core Concept

PKCE introduces a **dynamically created, one-time cryptographic secret** called the `code_verifier`. Before starting the auth flow, the client:

1. Generates a random `code_verifier`
2. Hashes it to produce a `code_challenge`
3. Sends the `code_challenge` (not the secret) in the authorization request
4. Later, sends the original `code_verifier` in the token request

The server verifies that hashing the `code_verifier` produces the same `code_challenge` it stored. Since the `code_verifier` travels over TLS and was never exposed, an interceptor who only got the authorization code cannot redeem it.

```
┌──────────────────────────────────────────────────────────────────┐
│                     WHY PKCE WORKS                               │
│                                                                  │
│  Attacker intercepts: authorization code ✅                      │
│  Attacker also needs: code_verifier ❌ (never exposed)           │
│                                                                  │
│  Without code_verifier → token endpoint rejects the request.     │
│  The intercepted authorization code is useless.                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 4. Key Terminology

| Term | Definition |
|---|---|
| **PKCE** | Proof Key for Code Exchange — the name of this RFC 7636 extension |
| **code_verifier** | A high-entropy cryptographic random string generated fresh for each authorization request. This is the secret. |
| **code_challenge** | A derived value produced by transforming the `code_verifier` (either by hashing it or using it directly). Sent in the auth request. |
| **code_challenge_method** | The transformation method used. Either `S256` (SHA-256 hash) or `plain` (no transformation). |
| **S256** | The recommended method: `BASE64URL-ENCODE(SHA256(ASCII(code_verifier)))` |
| **plain** | Fallback method: `code_challenge = code_verifier`. Only for constrained environments. |
| **Base64url Encoding** | URL-safe Base64 encoding with trailing `=` characters omitted |
| **MITM** | Man-in-the-middle attack |
| **Entropy** | The degree of randomness in a generated value. High entropy = hard to guess. |

---

## 5. PKCE Protocol — Step by Step

### Step 1: Create the Code Verifier

The client generates a fresh, cryptographically random string for every single authorization request.

```
code_verifier = high-entropy cryptographic random string

Allowed characters: A-Z / a-z / 0-9 / "-" / "." / "_" / "~"
Minimum length: 43 characters
Maximum length: 128 characters
Recommended: 32 random octets → base64url-encoded → 43 characters
Minimum entropy: 256 bits
```

**Generation process:**

```
Random Number Generator
        |
        | produces 32 random bytes (256 bits of entropy)
        v
[byte array: 32 octets]
        |
        | base64url-encode (no padding)
        v
code_verifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"
                 (43–128 character URL-safe string)
```

> ⚠️ A new `code_verifier` **must be created for every authorization request**. Never reuse.

---

### Step 2: Create the Code Challenge

The client transforms the `code_verifier` into a `code_challenge`. The **S256 method is mandatory** unless S256 cannot be supported.

#### Method: S256 (Required / Recommended)

```
code_challenge = BASE64URL-ENCODE( SHA256( ASCII(code_verifier) ) )
```

```
code_verifier  = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"
       |
       | Step 1: SHA-256 hash
       v
[19, 211, 30, 150, 26, 26, 216, 236, 47, ...]  ← raw bytes
       |
       | Step 2: base64url-encode
       v
code_challenge = "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM"
```

#### Method: plain (Fallback Only)

```
code_challenge = code_verifier
```

> ❌ Do **not** use `plain` in new implementations. It provides no protection against an attacker who can observe the authorization request.

---

### Step 3: Send the Code Challenge with the Authorization Request

The client sends the `code_challenge` and `code_challenge_method` as additional parameters in the standard authorization request:

```
GET /authorize
  ?response_type=code
  &client_id=s6BhdRkqt3
  &redirect_uri=https%3A%2F%2Fclient.example.com%2Fcb
  &scope=openid%20profile
  &state=abc123
  &code_challenge=E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM
  &code_challenge_method=S256
```

| Parameter | Required? | Description |
|---|---|---|
| `code_challenge` | **Required** | The transformed value derived from `code_verifier` |
| `code_challenge_method` | Optional (default: `plain`) | The transformation method: `S256` or `plain` |

The authorization server **stores** the `code_challenge` and `code_challenge_method` associated with the issued authorization code.

> The `code_challenge` must **never** be sent back to the client in a recoverable form.

---

### Step 4: Server Returns the Authorization Code

The authorization server responds as in a standard authorization code flow, redirecting the user-agent back to the client with the authorization code:

```
HTTP/1.1 302 Found
Location: https://client.example.com/cb
  ?code=SplxlOBeZQQYbYS6WxSbIA
  &state=abc123
```

The server **records** the `code_challenge` and `code_challenge_method` linked to this code for later verification.

**Error cases:**

| Situation | Error Returned |
|---|---|
| PKCE required but `code_challenge` missing | `invalid_request` |
| Requested `code_challenge_method` not supported | `invalid_request` |

---

### Step 5: Send the Code Verifier to the Token Endpoint

The client now sends the original (un-hashed) `code_verifier` alongside the authorization code to the token endpoint:

```
POST /token HTTP/1.1
Host: authorization-server.com
Content-Type: application/x-www-form-urlencoded

grant_type=authorization_code
&code=SplxlOBeZQQYbYS6WxSbIA
&redirect_uri=https://client.example.com/cb
&client_id=s6BhdRkqt3
&code_verifier=dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk
```

| Parameter | Required? | Description |
|---|---|---|
| `grant_type` | **Required** | Must be `"authorization_code"` |
| `code` | **Required** | Authorization code from step 4 |
| `redirect_uri` | Conditional | Must match if included in the original request |
| `client_id` | Conditional | Required if no other client authentication |
| `code_verifier` | **Required** | The original plaintext secret from step 1 |

---

### Step 6: Server Verifies and Returns the Token

The server re-applies the transformation to the received `code_verifier` and compares it with the stored `code_challenge`:

```
For S256:
  Server computes: BASE64URL-ENCODE(SHA256(ASCII(code_verifier)))
  Server compares: computed value == stored code_challenge

For plain:
  Server compares: code_verifier == stored code_challenge
```

```
If EQUAL    → ✅ Issue access token (normal flow continues)
If NOT EQUAL → ❌ Return error: "invalid_grant"
```

---

## 6. Full Flow Diagram — PKCE vs Standard Auth Code

### Standard Authorization Code (without PKCE)

```
Client                        Auth Server              Resource Server
  |                                |                         |
  |-- GET /authorize?              |                         |
  |   response_type=code           |                         |
  |   client_id=...        ------->|                         |
  |                                |                         |
  |<-- 302 ?code=ABCDEF  ----------|                         |
  |                                |                         |
  |-- POST /token                  |                         |
  |   code=ABCDEF          ------->|                         |
  |   client_secret=...            |                         |
  |                                |                         |
  |<-- { access_token } -----------|                         |
  |                                |                         |
  |-- GET /resource (+ token) ---->|------------------------>|
```

> ⚠️ If an attacker intercepts `ABCDEF` and also has `client_id` + `client_secret` (often extractable from app binaries), they can get a token.

---

### Authorization Code + PKCE

```
Client                               Auth Server           Resource Server
  |                                       |                      |
  | [STEP 1 & 2] Generate locally:        |                      |
  |   code_verifier = random secret       |                      |
  |   code_challenge = SHA256(verifier)   |                      |
  |                                       |                      |
  | [STEP 3] GET /authorize               |                      |
  |   ?response_type=code                 |                      |
  |   &code_challenge=E9Mel...            |                      |
  |   &code_challenge_method=S256 ------->|                      |
  |                                       |                      |
  |                        [Server stores |                      |
  |                         code_challenge|                      |
  |                         with code]    |                      |
  |                                       |                      |
  | [STEP 4] <-- 302 ?code=ABCDEF --------|                      |
  |                                       |                      |
  | [STEP 5] POST /token                  |                      |
  |   code=ABCDEF                         |                      |
  |   code_verifier=dBjft... ------------>|                      |
  |                                       |                      |
  |              [STEP 6] Server computes:|                      |
  |       SHA256(dBjft...) == E9Mel... ✅ |                      |
  |                                       |                      |
  | <-- { access_token } -----------------|                      |
  |                                       |                      |
  |-- GET /resource (+ token) ----------->|--------------------->|
```

---

### What Happens When an Attacker Intercepts the Code

```
Attacker intercepts: code = ABCDEF  ✅ (got the code)

Attacker tries to redeem it:
  POST /token
    code=ABCDEF
    client_id=s6BhdRkqt3
    ??? code_verifier = ???  ← Attacker does NOT have this

Auth Server:
  Needs to verify: SHA256(???) == E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM
  Cannot compute match without the original code_verifier.

Result: ❌ Error: invalid_grant — Access DENIED
```

The stolen authorization code is **completely useless** without the `code_verifier`.

---

## 7. Code Challenge Methods — S256 vs plain

```
┌──────────────────────────────────────────────────────────────────────┐
│               CODE CHALLENGE METHODS COMPARED                        │
├───────────────────────┬──────────────────┬───────────────────────────┤
│  Property             │  S256            │  plain                    │
├───────────────────────┼──────────────────┼───────────────────────────┤
│  Formula              │  BASE64URL(      │  code_challenge =         │
│                       │  SHA256(verifier)│  code_verifier            │
│                       │  )               │                           │
├───────────────────────┼──────────────────┼───────────────────────────┤
│  Protects vs.         │  Eavesdropping   │  Does NOT protect vs.     │
│  eavesdropper?        │  ✅ Yes          │  eavesdropping ❌         │
├───────────────────────┼──────────────────┼───────────────────────────┤
│  Mandatory on server? │  ✅ Yes (MTI)    │  Optional                 │
├───────────────────────┼──────────────────┼───────────────────────────┤
│  Recommended?         │  ✅ Always       │  ❌ Only legacy/compat.   │
├───────────────────────┼──────────────────┼───────────────────────────┤
│  Can downgrade from   │  ❌ MUST NOT     │  N/A                      │
│  S256 to plain?       │  downgrade       │                           │
└───────────────────────┴──────────────────┴───────────────────────────┘
```

### Why plain is Dangerous

With `plain`, the `code_challenge` **equals** the `code_verifier`. If an attacker can observe the authorization request (e.g., via HTTP logs or OS-level interception), they immediately know the `code_verifier` too — defeating the entire purpose of PKCE.

### Why You Must Not Downgrade from S256 to plain

If a server supports PKCE, it is **required** to support `S256`. Therefore, if `S256` fails, it is either a server bug or a **downgrade attack by a MITM**. Clients must not silently fall back to `plain` — they must treat the error as a potential attack.

---

## 8. Worked Example (from RFC 7636)

### Generating the code_verifier

```
Random 32-octet sequence (decimal values):
[116, 24, 223, 180, 151, 153, 224, 37, 79, 250, 96, 125, 216, 173,
 187, 186, 22, 212, 37, 77, 105, 214, 191, 240, 91, 88, 5, 88, 83,
 132, 141, 121]

After base64url-encoding:

  code_verifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"
```

### Hashing to produce code_challenge

```
SHA256(ASCII("dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"))
→ raw bytes:
  [19, 211, 30, 150, 26, 26, 216, 236, 47, 22, 177, 12, 76, 152, 46,
   8, 118, 168, 120, 173, 109, 241, 68, 86, 110, 225, 137, 74, 203,
   112, 249, 195]

After base64url-encoding:

  code_challenge = "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM"
```

### Authorization Request includes

```
code_challenge=E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM
&code_challenge_method=S256
```

### Token Request includes

```
code_verifier=dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk
```

### Server Verification

```
BASE64URL-ENCODE(SHA256(ASCII("dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk")))
     = "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM"
     == stored code_challenge ✅

→ Token is issued.
```

---

## 9. Security Considerations

### Entropy of the code_verifier

- Must be **cryptographically random** — not pseudo-random or time-based
- Minimum **256 bits of entropy** (32 random bytes base64url-encoded = 43 characters)
- Must be **impractical to guess** — brute-force attacks must be infeasible
- Generated fresh for **every authorization request** — never reused

```
Recommended generation:
  32 random bytes  →  base64url-encode  →  43-character code_verifier

Entropy: 32 × 8 = 256 bits
Attack resistance: 2^256 combinations to brute force
```

### Why No Salting is Needed

Unlike passwords, the `code_verifier` already contains 256 bits of entropy. Adding a publicly known salt to it before hashing (as is done with passwords) would **not** increase the difficulty of a brute-force attack. The verifier itself is the source of randomness — salting adds no value here.

### Protection Against Eavesdroppers

```
SCENARIO: Attacker observes the authorization request

  With S256:
    Attacker sees: code_challenge = "E9Mel..."  (a hash)
    Attacker cannot reverse SHA256 to find code_verifier ✅
    Attack fails.

  With plain:
    Attacker sees: code_challenge = "dBjftJeZ..."
    code_challenge == code_verifier  ← Attacker now has the secret ❌
    Attack succeeds.
```

### Downgrade Attack Prevention

```
Legitimate Client:  sends code_challenge_method=S256
MITM Attacker:      tries to intercept and change it to "plain"

Server:  S256 is Mandatory-To-Implement (MTI)
         Any server supporting PKCE MUST support S256
         If S256 fails on a PKCE server → indicates server fault
                                          OR active MITM attack

Client MUST NOT silently downgrade to "plain" — treat as an attack.
```

### TLS Requirement

All PKCE flows must transmit the `code_verifier` and `code_challenge` over **TLS**. The `code_verifier` sent to the token endpoint is the most sensitive value — if intercepted in transit, an attacker could redeem it. TLS ensures this cannot happen.

---

## 10. PKCE Parameters Quick Reference

| Parameter | Used In | Required? | Description |
|---|---|---|---|
| `code_verifier` | Token request | **Required** | The original random secret (43–128 chars). Sent to the token endpoint only. |
| `code_challenge` | Authorization request | **Required** | SHA256 hash (or plain) of `code_verifier`, base64url-encoded. |
| `code_challenge_method` | Authorization request | Optional (default: `plain`) | `S256` (recommended) or `plain` (fallback only) |

### PKCE Parameter Format Rules

| Parameter | Allowed Characters | Min Length | Max Length |
|---|---|---|---|
| `code_verifier` | `[A-Z][a-z][0-9]-._~` | 43 | 128 |
| `code_challenge` | `[A-Z][a-z][0-9]-._~` | 43 | 128 |
| `code_challenge_method` | N/A | N/A | N/A (registered value) |

### Base64url Encoding Rules

- Uses URL-safe character set (replaces `+` → `-`, `/` → `_`)
- Trailing `=` padding characters are **omitted**
- No line breaks or whitespace

---

## 11. Common Misconceptions

```
┌────────────────────────────────────────────────────────────────────┐
│                      PKCE MISCONCEPTIONS                           │
├────────────────────────────────┬───────────────────────────────────┤
│  ❌ Misconception              │  ✅ Reality                       │
├────────────────────────────────┼───────────────────────────────────┤
│  PKCE replaces the client      │  No. PKCE is an additional        │
│  secret                        │  security layer. Confidential     │
│                                │  clients should use BOTH.         │
├────────────────────────────────┼───────────────────────────────────┤
│  PKCE makes a public client    │  No. A public client remains      │
│  into a confidential client    │  public. PKCE only prevents code  │
│                                │  interception, not impersonation. │
├────────────────────────────────┼───────────────────────────────────┤
│  PKCE is only for mobile apps  │  No. PKCE is recommended for ALL  │
│                                │  OAuth clients — web, mobile,     │
│                                │  SPA, and native apps alike.      │
├────────────────────────────────┼───────────────────────────────────┤
│  The plain method is fine for  │  No. plain should only be used    │
│  new implementations           │  in legacy/constrained systems    │
│                                │  that cannot support S256.        │
├────────────────────────────────┼───────────────────────────────────┤
│  Servers not supporting PKCE   │  They will simply ignore the      │
│  will reject PKCE requests     │  unknown parameters and fall back │
│                                │  to standard OAuth 2.0.           │
├────────────────────────────────┼───────────────────────────────────┤
│  PKCE prevents all OAuth       │  No. PKCE specifically prevents   │
│  attacks                       │  authorization code interception  │
│                                │  and code injection. Other        │
│                                │  mitigations are still needed.    │
└────────────────────────────────┴───────────────────────────────────┘
```

---

## Summary — PKCE in 6 Steps

```
CLIENT SIDE (secret never leaves the client)
┌──────────────────────────────────────────────────────────┐
│ 1. Generate random code_verifier (32 bytes, 256-bit)     │
│ 2. Hash it: code_challenge = BASE64URL(SHA256(verifier)) │
└───────────────────────────┬──────────────────────────────┘
                            │ Send code_challenge
                            ▼
AUTH SERVER STORES code_challenge + issues auth code
                            │ Returns auth code
                            ▼
CLIENT SENDS code_verifier to token endpoint
                            │
AUTH SERVER VERIFIES:       │
  SHA256(verifier) == stored code_challenge?
       YES ──► Issue access token ✅
       NO  ──► Reject with invalid_grant ❌
```

---

*Notes based on RFC 7636 — "Proof Key for Code Exchange by OAuth Public Clients" by Sakimura et al., September 2015.*