
---

# **Controlling JSON Inclusion/Exclusion**

## **1. Overview**

By default, Jackson serializes every non-transient field it sees in a class. However, in some scenarios, certain fields may not be needed. Including them could:

* Waste bandwidth by sending unnecessary nulls
* Leak internal information
* Increase payload size with less important data

In this lesson, we explore annotations that help reduce or tailor JSON output.

---

## **2. Controlling JSON Output Using `@JsonInclude` Annotation**

Jackson provides the `@JsonInclude` annotation to control which fields are included during serialization. The annotation can be applied at:

* Class level
* Field level

### **Available Options**

* `NON_NULL` – skip when value is null
* `NON_EMPTY` – skip when value is null, empty string, empty collection/array, or empty optional
* `NON_DEFAULT` – skip when value equals Java default (e.g., 0 for primitives)

---

## **2.1. `@JsonInclude` at Field Level**

The `@JsonInclude` annotation decides at runtime whether a property is written, based on its value.

### **Example**

```java
public class Campaign {
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String code;

    // ...
}
```

### **Explanation**

* If `code == null`, Jackson **skips** this field during serialization

---

### **Unit Test**

```java
@Test
void whenUsingJsonIncludeAtFieldLevel_thenFieldOmitted() throws JsonProcessingException {
    Campaign campaign = new Campaign(null, "My Campaign", "Description of campaign 01");
    String json = objectMapper.writeValueAsString(campaign);

    assertFalse(json.contains("code"));
}
```

### **Key Point**

* The `code` field is **not included** in JSON when null
* The annotation **does not affect deserialization**

---

## **2.2. `@JsonInclude` at Class Level**

We can apply the annotation at the class level to affect all fields.

### **Example**

```java
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Campaign {

    // ...
}
```

### **Explanation**

* Excludes:

    * null values
    * empty strings
    * empty collections

---

### **Unit Test**

```java
@Test
void whenUsingJsonIncludeAtClassLevelWithEmptyOption_thenOmitEmptyField() throws JsonProcessingException {
    Campaign campaign = new Campaign("C01", "", "Description of campaign 01");
    String json = objectMapper.writeValueAsString(campaign);

    assertFalse(json.contains("name"));
}
```

### **Result**

* The `name` field is omitted because it is empty

---

## **3. Ignoring Properties Using `@JsonIgnore` and `@JsonProperty`**

We can also explicitly include or exclude fields using:

* `@JsonIgnore`
* `@JsonProperty`

---

## **3.1. Using the `@JsonIgnore` Annotation**

The `@JsonIgnore` annotation excludes a field from:

* Serialization
* Deserialization

---

### **Example**

```java
public class Campaign {
    
    @JsonIgnore
    private boolean closed;
    
    // ...
}
```

---

### **Unit Test**

```java
@Test
void whenUsingJsonIgnore_thenOmitTheAnnotatedField() throws JsonProcessingException {
    Campaign campaign = new Campaign("C01", "My Campaign", "Description of campaign 01");
    campaign.setClosed(true);
    String json = objectMapper.writeValueAsString(campaign);

    assertFalse(json.contains("closed"));
}
```

---

### **Key Observations**

* Field is not serialized even if it has a value (`true`)
* During deserialization:

    * Field is ignored
    * Assigned default value (`false` for boolean)

---

### **Note**

* We could also use `@JsonIncludeType` to ignore multiple fields

---

## **3.2. Using the `@JsonProperty` Annotation**

Although commonly used to rename fields, `@JsonProperty` can also control inclusion/exclusion behavior.

### **Access Options**

* `READ_ONLY` → serialized only
* `WRITE_ONLY` → deserialized only
* `READ_WRITE` → both serialization and deserialization
* `AUTO` → default behavior

---

### **Example (WRITE_ONLY)**

```java
public class Campaign {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String description;
    // ...
}
```

---

### **Explanation**

* `description` is:

    * **Deserialized**
    * **Not serialized**

---

### **Unit Test**

```java
@Test
void whenUsingJsonPropertyWithReadOption_thenOmitEmptyField() throws JsonProcessingException {
    Campaign campaign = new Campaign("C01", "My Campaign", "Description of campaign 01");
    String json = objectMapper.writeValueAsString(campaign);

    assertFalse(json.contains("description"));
}
```

---

### **Result**

* Field exists in object
* But not included in JSON output

---

## **4. Class-Level Inclusion/Exclusion**

When handling multiple fields, field-level annotations become verbose. Jackson provides:

* `@JsonIgnoreProperties`
* `@JsonIncludeProperties`

---

## **4.1. The `@JsonIgnoreProperties` Annotation**

Used to ignore multiple fields at once.

---

### **Example**

```java
@JsonIgnoreProperties({"name", "closed"})
public class PrivateCampaign {
    // Same fields and constructors as Campaign class
}
```

---

### **Effect**

* `name` and `closed` are excluded from:

    * Serialization
    * Deserialization

---

### **Unit Test (Serialization)**

```java
@Test
void whenUsingJsonIgnorePropertiesAtClassLevel_thenOmitSpecifiedFields() throws JsonProcessingException {
    PrivateCampaign campaign = new PrivateCampaign("PC01", "My Private Campaign", 
      "Description of private campaign 01");
    String json = objectMapper.writeValueAsString(campaign);

    assertFalse(json.contains("name"));
    assertFalse(json.contains("closed"));
}
```

---

### **Unit Test (Deserialization)**

```java
@Test
void whenUsingJsonIgnorePropertiesAtClassLevel_thenOmitSpecifiedFieldsDuringSerializationAndReplaceWithDefault() throws JsonProcessingException {
    String json = """
        {"code": "PC01", "name": "My Private Campaign", "description": "Description of private campaign 01", "tasks": [], "closed": true}
        """;
    PrivateCampaign campaign = objectMapper.readValue(json, PrivateCampaign.class);

    assertNull(campaign.getName());
    assertFalse(campaign.isClosed());
}
```

---

### **Key Observations**

* Ignored fields:

    * Not serialized
    * Not deserialized
* Default values applied:

    * `name → null`
    * `closed → false`

---

## **4.2. The `@JsonIncludeProperties` Annotation**

Used to include **only specific fields**.

---

### **Example**

```java
@JsonIncludeProperties(value = { "name", "description"})
public class PublicCampaign {
    // Same fields and constructors as Campaign class 
}
```

---

### **Effect**

* Only `name` and `description` are included
* All other fields are ignored

---

### **Unit Test (Serialization)**

```java
@Test
void whenUsingJsonIncludePropertiesAtClassLevel_thenOmitNonSpecifiedFields() throws JsonProcessingException {
    PublicCampaign publicCampaign = 
      new PublicCampaign("PUC01", "My Public Campaign", "Description of public campaign 01");
    String json = objectMapper.writeValueAsString(publicCampaign);

    assertTrue(json.contains("name"));
    assertFalse(json.contains("code"));
}
```

---

### **Unit Test (Deserialization)**

```java
@Test
void whenUsingJsonIncludePropertiesAtClassLevel_thenOmitNonSpecifiedFieldsDuringDeserialization() throws Exception {
    String json = """
            {"code": "PUC01", "name": "My Public Campaign", "description": "Description of public campaign 01"}
            """;
    PublicCampaign publicCampaign = objectMapper.readValue(json, PublicCampaign.class);

    assertEquals("My Public Campaign", publicCampaign.getName());
    assertNotSame("PUC01", publicCampaign.getCode());
}
```

---

### **Key Observations**

* Only specified fields:

    * Serialized
    * Deserialized
* Other fields are ignored

---

### **Additional Note**

* We can use `@JsonAutoDetect` at the class level to control visibility of fields

---

# **Summary**

Jackson provides powerful tools to control JSON inclusion and exclusion:

### **Field-Level Control**

* `@JsonInclude` → conditional inclusion
* `@JsonIgnore` → completely ignore field
* `@JsonProperty(access = …)` → fine-grained control

### **Class-Level Control**

* `@JsonInclude` → apply rules globally
* `@JsonIgnoreProperties` → exclude multiple fields
* `@JsonIncludeProperties` → include only specific fields

These annotations help:

* Optimize payload size
* Protect sensitive data
* Match API contract requirements

---
