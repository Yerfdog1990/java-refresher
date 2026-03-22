
---

# **Handling Unknown Properties**

## **1. Overview**

When working with external APIs or evolving systems, JSON data often changes over time, with new fields appearing in responses. If not handled properly, these differences can:

* Cause deserialization to fail
* Interrupt the data flow

In this lesson, we explore how to make applications more resilient by using Jackson mechanisms for handling unknown properties.

---

## **2. The Problem in Practice: Failing on an Unknown Property**

Consider a scenario:

* A brand runs multiple campaigns
* A new field `budget` is added to JSON
* Our API does **not** support this field

By default, Jackson throws an exception when encountering unknown properties.

---

### **Example: Default Behavior**

```java
@Test
void givenUnknownProperty_whenUsingDefaultMapper_thenFail() {
    ObjectMapper mapper = new ObjectMapper();
    String json = """
            {
              "code": "C2",
              "name": "Campaign 2",
              "description": "The description of Campaign 2",
              "budget": 100
            }
            """;
    assertThrows(UnrecognizedPropertyException.class, () -> mapper.readValue(json, Campaign.class));
}
```

---

### **Result**

* Deserialization fails
* Exception thrown: `UnrecognizedPropertyException`

This confirms Jackson’s default behavior when unknown fields are present.

---

## **3. Handling Unknown Properties Using the ObjectMapper**

The quickest way to handle unknown properties is by configuring the `ObjectMapper`.

---

### **Configuration**

```java
mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
```

---

### **Example**

```java
@Test
void givenMapperConfiguredToIgnoreUnknown_thenDeserializationSucceeds() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    String json = """
            {
              "code": "C2",
              "name": "Campaign 2",
              "description": "The description of Campaign 2",
              "budget": 500
            }
            """;

    Campaign campaign = mapper.readValue(json, Campaign.class);
    assertEquals("C2", campaign.getCode());
}
```

---

### **Behavior**

* Unknown fields (like `budget`) are ignored
* Deserialization succeeds

---

### **Important Note**

* This configuration applies **globally** to the `ObjectMapper`
* All deserialization using this mapper will ignore unknown properties

---

## **4. Class-Level Control with `@JsonIgnoreProperties`**

Global configuration is not always ideal.

### **Problem**

* Ignoring unknown fields globally may affect other classes (e.g., `Task`)
* Some classes may require strict validation

---

### **Solution**

Use:

```java
@JsonIgnoreProperties(ignoreUnknown = true)
```

This applies only to a specific class.

---

### **Example Class**

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class CampaignWithIgnoreUnknown extends Campaign{
}
```

---

### **Test Case**

```java
@Test
void givenJsonIgnorePropertiesConfiguredToIgnoreUnknown_thenDeserializationSucceeds() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    String json = """
            {
              "code": "C2",
              "name": "Campaign 2",
              "description": "The description of Campaign 2",
              "budget": 500
            }
            """;

    CampaignWithIgnoreUnknown campaign =
        mapper.readValue(json, CampaignWithIgnoreUnknown.class);

    assertEquals("C2", campaign.getCode());
}
```

---

### **Result**

* Unknown property `budget` is ignored
* Deserialization succeeds

---

### **Key Advantage**

* Applies only to **one class**
* Provides **fine-grained control**

---

### **Additional Capability**

* `@JsonIgnoreProperties` can also:

    * List specific properties to ignore
    * Apply to both serialization and deserialization

---

## **5. Capturing Unknown Fields with `@JsonAnySetter`**

Ignoring unknown properties is not always enough.

### **Why Capture Unknown Fields?**

We may want to:

* Store them for auditing
* Log them
* Forward original payload
* Support gradual schema evolution

---

### **Solution: `@JsonAnySetter`**

This annotation:

* Captures all unknown properties
* Passes them to a method
* Allows storing them in a `Map`

---

### **Example Class**

```java
public class CampaignWithSetUnknownProperties extends Campaign {

    private Map<String, Object> unknownProperties = new HashMap<>();

    @JsonAnySetter
    void addUnknownProperties(String key, Object value) {
        unknownProperties.put(key, value);
    }

    public Map<String, Object> getUnknownProperties() {
        return unknownProperties;
    }
}
```

---

### **Explanation**

* `unknownProperties` stores extra fields
* Method receives:

    * `String key` → property name
    * `Object value` → property value

---

### **Test Case**

```java
@Test
void givenJsonAnySetterConfiguredToRecordUnknown_thenDeserializationSucceeds() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    String json = """
            {
              "code": "C2",
              "name": "Campaign 2",
              "description": "The description of Campaign 2",
              "budget": 500
            }
            """;

    CampaignWithSetUnknownProperties campaign =
        mapper.readValue(json, CampaignWithSetUnknownProperties.class);

    assertTrue(campaign.getUnknownProperties().containsKey("budget"));
}
```

---

### **Result**

* `budget` is stored in the `unknownProperties` map

---

### **Important Notes**

* Method must accept:

    * `String key`
    * `Object value`
* Jackson can perform type conversion if needed
* Alternative approach:

    * Annotate the `Map<String, Object>` directly with `@JsonAnySetter`
    * Jackson will populate it automatically

---

### **Use Cases**

This approach is useful for:

* Preserving original payload
* Logging unknown fields
* Forwarding data to other services
* Gradual migration to new JSON schema

---

# **Summary**

Jackson provides three main strategies for handling unknown properties:

### **1. Default Behavior**

* Fails with `UnrecognizedPropertyException`

---

### **2. Global Handling (ObjectMapper)**

```java
mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
```

* Ignores unknown fields globally

---

### **3. Class-Level Handling**

```java
@JsonIgnoreProperties(ignoreUnknown = true)
```

* Ignores unknown fields for a specific class

---

### **4. Capturing Unknown Fields**

```java
@JsonAnySetter
```

* Stores unknown fields in a map
* Preserves full JSON data

---
