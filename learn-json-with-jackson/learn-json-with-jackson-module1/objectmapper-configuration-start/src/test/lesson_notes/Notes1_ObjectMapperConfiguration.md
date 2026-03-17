
---

# **ObjectMapper Configuration**

## **1. Overview**

Jackson’s `ObjectMapper` class is the easiest and most commonly used way to parse and generate JSON in Java. While the default behavior is sufficient for basic use cases, real-world applications often require customization.

In practice, we may need to:

* Format JSON output to improve readability
* Handle unknown or extra fields during deserialization
* Change the naming strategy of properties
* Exclude null or default values
* Customize date formats

These adjustments are very common in RESTful APIs, microservices, and applications that exchange JSON data frequently.

---

## **2. ObjectMapper Basic Configurations**

The `ObjectMapper` provides several built-in configuration mechanisms that allow us to adapt JSON processing to our needs.

These include:

* Feature toggling (enable/disable behavior)
* Helper configuration methods
* Module registration
* Annotation-based customization

An important characteristic of `ObjectMapper` is that it is **thread-safe after configuration**. This means:

* We can safely reuse a single instance across multiple threads
* It is inefficient to create a new instance for every operation

This is especially important in web applications (e.g., Spring Boot), where a single shared instance is typically used.

---

## **3. Default Behavior**

Before applying any configuration, it’s important to understand how `ObjectMapper` behaves by default.

### **Example**

```java
@Test
void givenDefaultObjectMapperInstance_whenSerializingAnObject_thenReturnJson() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    Campaign campaign = new Campaign("A1", "JJ", "");
    String result = mapper.writeValueAsString(campaign);

    System.out.println(result);
}
```

### **Output**

```json
{"code":"A1","name":"JJ","description":"","closed":false}
```

### **Explanation**

* The JSON is produced as a **single line** (no formatting)
* Field names directly reflect the Java object properties
* Default values (e.g., `false`) are included

This represents Jackson’s **default, minimal configuration behavior**.

---

## **4. Feature Toggling**

Feature toggling is the primary mechanism for configuring `ObjectMapper`.

Jackson provides three main methods:

* `enable(Feature)` → activates a feature
* `disable(Feature)` → deactivates a feature
* `configure(Feature, boolean)` → dynamically enables or disables a feature

These methods allow us to modify behavior globally without using annotations.

---

## **4.1 enable()**

The `enable()` method is used to activate a specific feature.

### **Example: Pretty Printing JSON**

```java
@Test
void givenEnableFeatureToggle_thenBehaviorIsAdjusted() throws JsonProcessingException {
    ObjectMapper customMapper = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);

    String result = customMapper.writeValueAsString(new Campaign("A1", "JJ", ""));

    System.out.println(result);
    assertTrue(result.contains("\n"));
}
```

### **Output**

```json
{
  "code" : "A1",
  "name" : "JJ",
  "description" : "",
  "closed" : false
}
```

### **Explanation**

* The feature `SerializationFeature.INDENT_OUTPUT` enables formatting
* The JSON becomes easier to read with indentation and line breaks
* The actual data remains unchanged

---

## **4.2 disable()**

The `disable()` method is used to turn off a specific feature.

### **Example: Handling Empty Beans**

```java
@Test
void givenDisableFeatureToggle_thenBehaviorIsAdjusted() throws JsonProcessingException {
    class EmptyClass {
    }

    ObjectMapper defaultMapper = new ObjectMapper();
    assertThrows(InvalidDefinitionException.class,
        () -> defaultMapper.writeValueAsString(new EmptyClass()));

    ObjectMapper customMapper = new ObjectMapper()
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    String customOutput = customMapper.writeValueAsString(new EmptyClass());
    assertEquals("{}", customOutput);
}
```

### **Explanation**

* By default, Jackson throws an exception when serializing an empty class
* Disabling `FAIL_ON_EMPTY_BEANS` allows serialization
* The result is an empty JSON object: `{}`

---

## **4.3 configure()**

The `configure()` method allows dynamic control using a boolean flag.

### **Example: Ignoring Unknown Properties**

```java
@Test
void givenConfigureFeatureToggle_thenBehaviorIsAdjusted() throws JsonProcessingException {
    String json = """
        {"code":"X1","name":"Extra","description":"-", "extraField":"ignored"}
        """;

    ObjectMapper defaultMapper = new ObjectMapper();
    assertThrows(UnrecognizedPropertyException.class,
        () -> defaultMapper.readValue(json, Campaign.class));

    ObjectMapper customMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    Campaign campaignResult = customMapper.readValue(json, Campaign.class);

    assertEquals("X1", campaignResult.getCode());
}
```

### **Explanation**

* Default behavior: fails on unknown fields (`extraField`)
* Configured behavior: ignores unknown fields and continues processing

---

## **4.4 Feature Types**

Jackson organizes features into several categories:

### **SerializationFeature**

Controls how Java objects are converted to JSON
Examples:

* `INDENT_OUTPUT`
* `WRITE_DATES_AS_TIMESTAMPS`

---

### **DeserializationFeature**

Controls how JSON is converted to Java objects
Examples:

* `FAIL_ON_UNKNOWN_PROPERTIES`
* `ACCEPT_SINGLE_VALUE_AS_ARRAY`

---

### **MapperFeature**

Controls core ObjectMapper behavior
Examples:

* `AUTO_DETECT_FIELDS`
* `DEFAULT_VIEW_INCLUSION`

---

### **JsonParser.Feature**

Low-level JSON parsing configuration
Example:

* `ALLOW_COMMENTS`

---

### **JsonGenerator.Feature**

Low-level JSON writing configuration
Example:

* `QUOTE_FIELD_NAMES`

---

## **5. Helper Configuration Methods**

Beyond feature toggling, Jackson provides helper methods for more advanced customization.

---

## **5.1 Controlling Property Inclusion**

By default, Jackson includes all fields, even if they are null.

### **Example: Excluding Null Values**

```java
@Test
void givenAnObjectWithNON_NULLFlagSet_whenSerializedTheObject_thenNullValuesExcluded() throws Exception {
    Campaign exampleCampaign = new Campaign("A1", "JJ", null);
    
    ObjectMapper defaultMapper = new ObjectMapper();
    String defaultOutput = defaultMapper.writeValueAsString(exampleCampaign);
    assertTrue(defaultOutput.contains("null"));
    
    ObjectMapper customMapper = new ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    String output = customMapper.writeValueAsString(exampleCampaign);

    System.out.println(output);
    assertFalse(output.contains("null"));
}
```

### **Explanation**

* Default mapper includes null values
* Custom mapper excludes them

---

### **Other Inclusion Options**

* `NON_EMPTY` → excludes null, empty strings, empty collections
* `NON_DEFAULT` → excludes default values

---

## **5.2 Other Helper Methods**

### **Property Naming Strategy**

```java
objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
```

* Converts `campaignName` → `campaign_name`

---

### **Visibility Control**

```java
objectMapper.setVisibility(...);
```

* Controls which fields/methods are visible to Jackson

---

### **Mix-ins**

```java
objectMapper.setMixIns(...);
```

* Adds annotations to third-party classes

---

### **Date Formatting**

```java
objectMapper.setDateFormat(...);
```

* Sets global date format

---

## **6. Modules and Custom Components**

Jackson allows extensibility through modules.

### **Registering Modules**

```java
objectMapper.registerModule(module);
objectMapper.registerModules(module1, module2);
objectMapper.findAndRegisterModules();
```

---

### **Advanced Customization**

* `setSerializerProvider()`
* `setSerializerFactory()`
* `setDeserializationContext()`

These are used in advanced scenarios.

---

### **Example Use Case**

* Registering `JavaTimeModule` for Java 8 date/time support

---

## **7. Annotation-Based Configuration**

Global configuration applies to the entire application, but sometimes we need fine-grained control.

Jackson provides annotations for this purpose.

---

## **Common Annotations**

* `@JsonInclude` → control inclusion rules
* `@JsonIgnoreProperties` → ignore unknown fields
* `@JsonNaming` → change naming strategy
* `@JsonProperty`, `@JsonIgnore`, `@JsonFormat`

---

## **Example Class**

```java
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignWithAnnotations {

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String description;

    // other fields
}
```

---

## **Annotation Precedence**

Jackson follows a **local-over-global principle**:

* Global configuration applies by default
* Annotations override it for specific classes/fields

---

### **Example**

```java
@Test
void givenGlobalAlways_thenClassNonNull_andFieldAlways_shouldRespectAnnotationPrecedence() throws Exception {
    ObjectMapper mapper = new ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.ALWAYS);

    CampaignWithAnnotations campaign =
        new CampaignWithAnnotations(null, "JJ", null);

    String json = mapper.writeValueAsString(campaign);

    System.out.println(json);

    assertTrue(json.contains("\"description\":null"));
    assertFalse(json.contains("\"code\":"));
}
```

---

## **Explanation**

* Global rule: include all fields
* Class-level rule: exclude nulls → removes `code`
* Field-level rule: include always → keeps `description`

👉 Annotations override global configuration

---

# **8. Conclusion**

The `ObjectMapper` configuration system is powerful and flexible, allowing developers to adapt JSON processing to real-world needs.

By combining:

* Feature toggles
* Helper methods
* Modules
* Annotations

we can:

* Control serialization and deserialization behavior
* Handle edge cases
* Maintain consistency across applications

In practice, most applications use:

* **Global configuration** for consistency
* **Annotations** for fine-grained control

This combination makes Jackson one of the most versatile JSON libraries in Java.

---

