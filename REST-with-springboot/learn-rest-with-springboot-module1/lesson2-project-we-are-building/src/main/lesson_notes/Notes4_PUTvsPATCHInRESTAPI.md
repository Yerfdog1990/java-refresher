
---

# **HTTP PUT vs HTTP PATCH in a REST API**

---

## **1. Overview**

When building RESTful APIs, updating resources is a very common operation. HTTP provides **two different verbs** for updating resources:

* **PUT**
* **PATCH**

Although both are used to modify existing resources, **they have different semantics and use cases**.

In this lesson, we will:

* Understand the semantic differences between PUT and PATCH
* Learn when to use each method
* Implement PUT and PATCH endpoints using Spring
* Test PUT and PATCH requests
* Discuss edge cases such as handling `null` values
* Compare PUT and PATCH side by side

---

## **2. When to Use PUT vs PATCH**

### **Simple Rule of Thumb**

* **PUT** → Replace the **entire resource**
* **PATCH** → Update **part of the resource**

### **Explanation**

If a client wants to **completely replace** a resource, they should use **PUT**.

If a client only wants to **modify specific fields**, sending the entire resource can be:

* Wasteful
* Error-prone
* Inefficient

In such cases, **PATCH** is a better fit.

---

### **Idempotence Consideration**

Another important distinction:

* **PUT is idempotent**

    * Repeating the same request produces the same result
* **PATCH is not guaranteed to be idempotent**

    * It *can* be idempotent, but it depends on implementation

This characteristic can influence which method to choose depending on the operation’s behavior.

---

## **3. Implementing PUT and PATCH Logic (Spring Example)**

### **Resource Model**

Let’s assume we are managing a resource with many fields:

```java
public class HeavyResource {
    private Integer id;
    private String name;
    private String address;
    // other fields
}
```

---

### **3.1 Full Update Using PUT**

A **PUT request** expects the **complete representation** of the resource.

```java
@PutMapping("/heavyresource/{id}")
public ResponseEntity<?> saveResource(
        @RequestBody HeavyResource heavyResource,
        @PathVariable("id") String id) {

    heavyResourceRepository.save(heavyResource, id);
    return ResponseEntity.ok("resource saved");
}
```

📌 Characteristics:

* Replaces the existing resource
* Missing fields may be overwritten or lost
* Client must send **all fields**, even unchanged ones

---

### **3.2 Partial Update Using PATCH (DTO Approach)**

Suppose the `address` field changes frequently.
We don’t want to send the entire `HeavyResource` every time.

We define a **partial DTO**:

```java
public class HeavyResourceAddressOnly {
    private Integer id;
    private String address;
}
```

Now implement PATCH:

```java
@PatchMapping("/heavyresource/{id}")
public ResponseEntity<?> partialUpdateAddress(
        @RequestBody HeavyResourceAddressOnly partialUpdate,
        @PathVariable("id") String id) {

    heavyResourceRepository.save(partialUpdate, id);
    return ResponseEntity.ok("resource address updated");
}
```

📌 Benefits:

* Smaller payload
* Clear intent
* Better performance

---

### **3.3 Generic PATCH Using a Map**

If many partial updates exist, creating a DTO for each can be cumbersome.

Instead, we can accept a generic `Map`:

```java
@RequestMapping(
    value = "/heavyresource/{id}",
    method = RequestMethod.PATCH,
    consumes = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<?> partialUpdateGeneric(
        @RequestBody Map<String, Object> updates,
        @PathVariable("id") String id) {

    heavyResourceRepository.save(updates, id);
    return ResponseEntity.ok("resource updated");
}
```

⚠ Trade-off:

* More flexible
* Less type safety
* Harder validation

---

## **4. Testing PUT and PATCH**

### **4.1 Testing PUT**

```java
mockMvc.perform(put("/heavyresource/1")
    .contentType(MediaType.APPLICATION_JSON_VALUE)
    .content(objectMapper.writeValueAsString(
        new HeavyResource(1, "Tom", "Jackson", 12, "heaven street")
    ))
).andExpect(status().isOk());
```

---

### **4.2 Testing PATCH with DTO**

```java
mockMvc.perform(patch("/heavyresource/1")
    .contentType(MediaType.APPLICATION_JSON_VALUE)
    .content(objectMapper.writeValueAsString(
        new HeavyResourceAddressOnly(1, "5th avenue")
    ))
).andExpect(status().isOk());
```

---

### **4.3 Testing Generic PATCH**

```java
HashMap<String, Object> updates = new HashMap<>();
updates.put("address", "5th avenue");

mockMvc.perform(patch("/heavyresource/1")
    .contentType(MediaType.APPLICATION_JSON_VALUE)
    .content(objectMapper.writeValueAsString(updates))
).andExpect(status().isOk());
```

---

## **5. Handling PATCH Requests with `null` Values**

Consider the following PATCH request:

```json
{
  "id": 1,
  "address": null
}
```

Two valid strategies exist:

1. **Set the field to `null`**
2. **Ignore the field and treat it as “no change”**

📌 **Important:**
Choose **one strategy** and apply it consistently across all PATCH endpoints.

Inconsistent handling leads to unpredictable APIs.

---

## **6. PUT vs PATCH — Side-by-Side Comparison**

| Feature           | PUT                                  | PATCH                                   |
| ----------------- | ------------------------------------ | --------------------------------------- |
| Purpose           | Replace entire resource              | Partial modification                    |
| Data Sent         | Full resource                        | Only changed fields                     |
| Idempotence       | Yes                                  | Not guaranteed                          |
| Performance       | Less efficient for large objects     | More efficient                          |
| Typical Use       | Full updates, replacements           | Incremental updates                     |
| Resource Creation | Can create if resource doesn’t exist | Usually fails if resource doesn’t exist |

---

## **7. PUT Request Example (Node.js / Express)**

### **PUT replaces entire resource**

```js
app.put('/users/:id', (req, res) => {
    const userId = req.params.id;
    const updatedUser = req.body;
    res.json({ message: `User with ID ${userId} updated`, updatedUser });
});
```

📌 Client must send **all fields**:

```json
{
  "id": 123,
  "name": "Anjali",
  "email": "123@example.com",
  "age": 20
}
```

---

## **8. PATCH Request Example (Node.js / Express)**

### **PATCH updates specific fields**

```js
app.patch('/users/:id', (req, res) => {
    const userId = req.params.id;
    const partialUpdate = req.body;
    res.json({ message: `User with ID ${userId} updated`, partialUpdate });
});
```

📌 Only changed fields are sent:

```json
{
  "age": 40
}
```

---

## **9. Which One Should You Use?**

### **Use PUT When**

* You want to **replace the entire resource**
* You are okay overwriting missing fields
* The client has the full resource state
* The operation should be idempotent

### **Use PATCH When**

* You want to update **specific fields**
* You want to reduce payload size
* Updates are incremental
* Performance and bandwidth matter

---

## **10. Conclusion**

* **PUT** and **PATCH** both update resources, but with different intentions
* **PUT replaces**, **PATCH modifies**
* Correct usage improves:

    * API clarity
    * Performance
    * Maintainability
* A well-designed REST API uses **both**, each where it fits best

🎯 **Design APIs that clearly communicate intent — the HTTP verb matters.**

---

