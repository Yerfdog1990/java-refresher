# JSON Web Token (JWT)

> **Reference:** RFC 7519 · Internet Standards Track · IETF · May 2015  
> **Authors:** Michael B. Jones (Microsoft), John Bradley (Ping Identity), Nat Sakimura (Nomura Research Institute)  
> **Pronunciation:** "jot"

---

## 1. What is a JWT?

A JWT is a **compact, URL-safe** means of representing claims to be transferred between two parties. The claims are encoded as a JSON object used as either:

- The **payload** of a JSON Web Signature (JWS) structure, or
- The **plaintext** of a JSON Web Encryption (JWE) structure

This enables claims to be digitally signed, integrity-protected with a Message Authentication Code (MAC), and/or encrypted.

JWTs are always represented using the **JWS Compact Serialization** or the **JWE Compact Serialization**.

---

## 2. Core Terminology

| Term | Definition |
|------|-----------|
| **JWT** | A string representing a set of claims as a JSON object, encoded in a JWS or JWE. |
| **JWT Claims Set** | The JSON object containing all the claims conveyed by the JWT. |
| **Claim** | A piece of information asserted about a subject — a name/value pair. |
| **Claim Name** | The name portion of a claim; always a string. |
| **Claim Value** | The value portion of a claim; can be any JSON value. |
| **Nested JWT** | A JWT used as the payload/plaintext of an enclosing JWS or JWE, enabling nested signing and/or encryption. |
| **Unsecured JWT** | A JWT whose claims are not integrity-protected or encrypted. |
| **NumericDate** | Seconds since `1970-01-01T00:00:00Z UTC` (ignoring leap seconds). |
| **StringOrURI** | A JSON string; any value containing `:` MUST be a valid URI (RFC 3986). |

---

## 3. JWT Structure

A JWT is a sequence of **URL-safe parts separated by period (`.`) characters**. Each part contains a Base64url-encoded value.

```
BASE64URL(JOSE Header) . BASE64URL(JWT Claims Set) . BASE64URL(Signature)
```

### Example (JWS — HMAC SHA-256)

**JOSE Header:**
```json
{"typ":"JWT", "alg":"HS256"}
```

**JWT Claims Set:**
```json
{
  "iss": "joe",
  "exp": 1300819380,
  "http://example.com/is_root": true
}
```

**Complete JWT (line breaks for display only):**
```
eyJ0eXAiOiJKV1QiLA0KICJhbGciOiJIUzI1NiJ9
.
eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFt
cGxlLmNvbS9pc19yb290Ijp0cnVlfQ
.
dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk
```

---

## 4. JWT Claims

The JWT Claims Set is a JSON object whose members are the claims of the JWT. **Claim Names within a JWT Claims Set MUST be unique.**

There are three classes of Claim Names:

### 4.1 Registered Claim Names

These are standardised, interoperable claims registered with IANA. All are **optional** unless required by the application.

| Claim | Name | Description |
|-------|------|-------------|
| `iss` | Issuer | Identifies the principal that issued the JWT. Case-sensitive string (StringOrURI). |
| `sub` | Subject | Identifies the subject of the JWT. Must be locally or globally unique. Case-sensitive string (StringOrURI). |
| `aud` | Audience | Identifies the intended recipients. If present and the processor cannot identify itself with a value in `aud`, the JWT **MUST be rejected**. Can be an array or single string. |
| `exp` | Expiration Time | The time on/after which the JWT MUST NOT be accepted. Current time MUST be before this value. A NumericDate. |
| `nbf` | Not Before | The time before which the JWT MUST NOT be accepted. Current time MUST be on or after this value. A NumericDate. |
| `iat` | Issued At | The time the JWT was issued. Used to determine token age. A NumericDate. |
| `jti` | JWT ID | A unique identifier for the JWT; used to prevent replay. Case-sensitive string. |

> **Clock Skew:** For `exp` and `nbf`, implementations MAY allow a small leeway (usually no more than a few minutes) to account for clock skew between parties.

### 4.2 Public Claim Names

Defined at will by JWT users. To prevent collisions, new names should either be:
- Registered in the IANA "JSON Web Token Claims" registry, or
- A **Collision-Resistant Name** (e.g. using a domain name or UUID namespace)

### 4.3 Private Claim Names

Custom claims agreed upon between a producer and consumer of a JWT. Not registered or publicly defined. **Subject to collision — use with caution.**

---

## 5. JOSE Header

The JOSE (JSON Object Signing and Encryption) Header describes the cryptographic operations applied to the JWT.

| Parameter | Description |
|-----------|-------------|
| `typ` | Declares the media type of the JWT. Recommended value: `"JWT"`. Should be uppercase for compatibility. OPTIONAL. |
| `cty` | Content Type — conveys structural information. MUST be `"JWT"` when nested signing/encryption is used. NOT RECOMMENDED otherwise. |
| `alg` | The algorithm used for signing or key encryption (e.g. `HS256`, `RS256`, `none`). |
| `enc` | The content encryption algorithm for JWE (e.g. `A128CBC-HS256`). |

### Replicating Claims as Header Parameters

In encrypted JWTs (JWE), it can be useful to have an **unencrypted copy** of certain claims in the header for pre-decryption processing. The `iss`, `sub`, and `aud` claims may be replicated as Header Parameters. The receiving application **SHOULD verify** their values are identical to those in the Claims Set.

> Only claims safe to transmit unencrypted should be replicated this way.

---

## 6. Unsecured JWTs

A JWT can be created without a signature or encryption when the content is secured by other means (e.g. a signature on a containing data structure).

An Unsecured JWT uses `"alg":"none"` in the JOSE Header and an **empty string** for the JWS Signature value.

**Example JOSE Header:**
```json
{"alg":"none"}
```

**Encoded:** `eyJhbGciOiJub25lIn0`

**Resulting JWT structure (note the trailing `.` with empty signature):**
```
eyJhbGciOiJub25lIn0
.
eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFt
cGxlLmNvbS9pc19yb290Ijp0cnVlfQ
.
```

---

## 7. Creating a JWT

Steps to create a JWT (order is not significant where there are no dependencies):

1. Create a **JWT Claims Set** containing the desired claims.
2. Let the **Message** be the UTF-8 octets of the JWT Claims Set.
3. Create a **JOSE Header** with the desired Header Parameters (conforming to JWS or JWE).
4. Depending on type:
    - **JWS:** Create a JWS using the Message as the JWS Payload.
    - **JWE:** Create a JWE using the Message as the plaintext.
5. For **nested** signing/encryption: let the Message be the JWS or JWE, return to Step 3, using `"cty":"JWT"` in the new JOSE Header.
6. Otherwise, the result is the final JWT.

---

## 8. Validating a JWT

If **any** step fails, the JWT **MUST be rejected**.

1. Verify the JWT contains at least one period (`.`) character.
2. Extract the **Encoded JOSE Header** — the portion before the first `.`.
3. Base64url decode the JOSE Header (no line breaks, whitespace, or extra characters allowed).
4. Verify the result is a valid **UTF-8 JSON object** (per RFC 7159).
5. Verify the JOSE Header contains only understood parameters, or those specified to be ignored if not understood.
6. Determine whether the JWT is a **JWS or JWE**.
7. Depending on type:
    - **JWS:** Validate per JWS spec; the Message is the base64url-decoded JWS Payload.
    - **JWE:** Validate per JWE spec; the Message is the resulting plaintext.
8. If the JOSE Header contains `"cty":"JWT"`, the Message is itself a JWT — return to Step 1 (nested JWT handling).
9. Otherwise, base64url decode the Message (no extra characters).
10. Verify the result is a valid **UTF-8 JSON object**; this is the JWT Claims Set.

> Even if validation succeeds, the JWT **SHOULD be rejected** if the algorithms used are not acceptable to the application.

---

## 9. String Comparison Rules

- All JSON string comparisons use the rules from RFC 7159 Section 8.3.
- Comparisons are **equality/inequality only** and are **case-sensitive**.
- Exception: `"typ"` and `"cty"` member values do not follow these case-sensitive comparison rules (media type names are case-insensitive by convention, though `"JWT"` uppercase is recommended).

---

## 10. Implementation Requirements

### Signing / MAC Algorithms (JSON Web Algorithms — JWA)

| Algorithm | Requirement |
|-----------|-------------|
| `HS256` (HMAC SHA-256) | **MUST** implement |
| `none` (Unsecured) | **MUST** implement |
| `RS256` (RSASSA-PKCS1-v1_5 + SHA-256) | RECOMMENDED |
| `ES256` (ECDSA P-256 + SHA-256) | RECOMMENDED |
| Others | OPTIONAL |

### Encryption (if provided)

| Algorithm | Requirement |
|-----------|-------------|
| `RSA1_5` (RSAES-PKCS1-v1_5, 2048-bit) | **MUST** implement |
| `A128KW` / `A256KW` (AES Key Wrap) | **MUST** implement |
| `A128CBC-HS256` / `A256CBC-HS512` | **MUST** implement |
| `ECDH-ES+A128KW` / `ECDH-ES+A256KW` | RECOMMENDED |
| `A128GCM` / `A256GCM` | RECOMMENDED |
| Others | OPTIONAL |

- **Encrypted JWTs:** OPTIONAL
- **Nested JWTs:** OPTIONAL

---

## 11. Security Considerations

### 11.1 Trust Decisions

JWT contents **cannot be relied upon** in a trust decision unless they have been **cryptographically secured and bound** to the necessary context. The key(s) used to sign/encrypt must verifiably be under the control of the party identified as the issuer.

### 11.2 Signing and Encryption Order (Nested JWTs)

When both signing and encryption are required, the recommended order is:

1. **Sign** the message first.
2. **Encrypt** the signed result (encrypting the signature).

**Why?** This prevents attacks where the signature is stripped, leaving only an encrypted message. It also provides privacy for the signer. Additionally, signatures over encrypted text are not legally recognised in many jurisdictions.

---

## 12. Privacy Considerations

A JWT may contain **privacy-sensitive information**. Protective measures include:

- Using an **encrypted JWT** and authenticating the recipient.
- Only transmitting JWTs with unencrypted sensitive data over **TLS** (Transport Layer Security) with endpoint authentication.
- **Omitting privacy-sensitive information** from the JWT entirely — the simplest approach.

---

## 13. IANA Registrations

### Registered Claims (IANA "JSON Web Token Claims" Registry)

| Claim | Description | RFC Section |
|-------|-------------|-------------|
| `iss` | Issuer | §4.1.1 |
| `sub` | Subject | §4.1.2 |
| `aud` | Audience | §4.1.3 |
| `exp` | Expiration Time | §4.1.4 |
| `nbf` | Not Before | §4.1.5 |
| `iat` | Issued At | §4.1.6 |
| `jti` | JWT ID | §4.1.7 |

### URN

`urn:ietf:params:oauth:token-type:jwt` — registered for use when declaring JWT content types via URIs.

### Media Type

`application/jwt` — registered for use as the MIME type for JWT content.

---

## 14. Relationship to Other Standards

### JWT vs SAML Assertions

| | JWT | SAML 2.0 |
|--|-----|---------|
| Format | JSON | XML |
| Size | Compact (fits in HTTP headers/URIs) | Large |
| Complexity | Simple | High (XML Canonicalization etc.) |
| Signature | JWS (smaller, less flexible) | XML DSIG |
| Use case | Space-constrained environments | Rich expressivity, complex security options |

> JWTs are not a full replacement for SAML Assertions — they are a simpler token format suited to ease of implementation and compactness.

### JWT vs Simple Web Tokens (SWT)

| | JWT | SWT |
|--|-----|-----|
| Claim names | Strings | Strings |
| Claim values | Any JSON type | Strings only |
| Cryptographic protection | HS256, RS256, ES256, encryption | HMAC SHA-256 only |

---

## 15. Quick Reference — JWT Anatomy

```
Header           Payload          Signature
─────────────    ─────────────    ──────────────
Base64url(       Base64url(       Base64url(
  {               {                 HMAC/RSA/ECDSA(
    "alg":"HS256",  "iss":"joe",      header + "." + payload
    "typ":"JWT"     "sub":"1234",   )
  }               "exp":9999999,  )
)               "roles":["admin"]
                }
               )
```

---

## 16. Example: Encrypted JWT (JWE)

Algorithm: `RSA1_5` + `A128CBC-HS256`

**JOSE Header:**
```json
{"alg":"RSA1_5","enc":"A128CBC-HS256"}
```

The Claims Set is encrypted to the recipient. The result is a 5-part JWE Compact Serialization:

```
BASE64URL(Header) . BASE64URL(Encrypted Key) . BASE64URL(IV) . BASE64URL(Ciphertext) . BASE64URL(Auth Tag)
```

---

## 17. Example: Nested JWT

A JWT can serve as the payload of an outer JWE or JWS. The JOSE Header of the outer structure uses `"cty":"JWT"` to signal a Nested JWT.

**Outer JOSE Header:**
```json
{"alg":"RSA1_5","enc":"A128CBC-HS256","cty":"JWT"}
```

Recommended order: **sign first, then encrypt** (see §11.2).

---

*Source: RFC 7519 — JSON Web Token (JWT) — Jones, Bradley, Sakimura — May 2015*