
---

# Handling Date and Time Values

## 1. Overview

Handling date and time values is a common requirement in real-world JSON APIs. Depending on the scenario, we might need to represent:

* A **date** (e.g., `LocalDate`)
* A **time**
* A **date-time combination** (e.g., `LocalDateTime`)
* A **date-time with time zone** (e.g., `ZonedDateTime`)

The **Java Time API** provides dedicated classes for each of these representations. However, this introduces complexity when configuring how to **serialize (Java → JSON)** and **deserialize (JSON → Java)** them properly.

Additionally, different systems may expect different formats:

* ISO-8601 strings (e.g., `"2025-03-01"`)
* Numeric timestamps
* Custom patterns
* Specific time zones or locales

To handle all these variations, **Jackson provides configuration options** that control how date and time values are processed.

### Required Modules

To start working with date and time values:

* Import:

  ```
  handling-date-and-time-values-start
  ```

* Full implementation:

  ```
  handling-date-and-time-values-end
  ```

---

## 2. Handling the Java Time API

Jackson supports legacy date classes like:

* `java.util.Date`
* `java.util.Calendar`

However, for modern applications using Java 8+, we use:

* `LocalDate`
* `LocalDateTime`
* `ZonedDateTime`

To support these, we must include the **JSR-310 module**.

### Maven Dependency

```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>${jackson.version}</version>
</dependency>
```

### Registering the Module

```java
final ObjectMapper defaultObjectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
```

This ensures Jackson can properly handle Java Time API classes.

---

## 2.1 Default Serialization Behavior

By default, Jackson serializes Java Time types as **arrays of numbers**.

### Example: Serialize LocalDate

```java
@Test
void whenUsingDefaultMapper_thenSerializeAsArray() throws JsonProcessingException {
    Task task = new Task("T1", "Task 1", "The Task 1", LocalDate.of(2045, 3, 1), TaskStatus.TO_DO, null);
    String json = defaultObjectMapper.writeValueAsString(task);
    
    assertTrue(json.contains("[2045,3,1]"));
}
```

### Example: Deserialize LocalDate

```java
@Test
void whenUsingDefaultMapper_thenDeserializeFromArray() throws JsonProcessingException {
    String json = """
        {
          "code": "T1",
          "name": "Task 1",
          "description": "Task 1",
          "dueDate": [2045,3,1],
          "status": "TO_DO"
        }""";
    Task task = defaultObjectMapper.readValue(json, Task.class);
    assertEquals(task.getDueDate(), LocalDate.of(2045, 3, 1));
}
```

---

### LocalDateTime as Array

```java
@Test
void whenUsingDefaultMapper_thenSerializeDateTimeAsArray() throws JsonProcessingException {
    LocalDateTime dateTime = LocalDateTime.of(2045, 6, 28, 10, 10, 50, 1234);
    String dateTimeJson = defaultObjectMapper.writeValueAsString(dateTime);
    assertEquals("[2045,6,28,10,10,50,1234]", dateTimeJson);
    
    LocalDateTime dateTimeWithZeros = LocalDateTime.of(2045, 6, 28, 10, 0, 0, 0);
    String dateTimeWithZerosJson = defaultObjectMapper.writeValueAsString(dateTimeWithZeros);
    assertEquals("[2045,6,28,10,0]", dateTimeWithZerosJson);
}
```

### Important Note

* Arrays include:

    * year, month, day, hour, minute, second, nanoseconds
* **Trailing zeros are omitted**, so array length varies → may cause parsing issues

---

### Deserialize LocalDateTime

```java
@Test
void whenUsingDefaultMapper_thenDeserializeFromArray() throws JsonProcessingException {
    LocalDateTime dateTime = defaultObjectMapper.readValue("[2045,6,28,10,10]", LocalDateTime.class);
    assertEquals(LocalDateTime.of(2045, 6, 28, 10, 10), dateTime);
}
```

---

## 2.2 Configuring ISO-8601 Date Serialization

The **ISO-8601 format** (e.g., `"2045-03-01"`) is:

* Human-readable
* Standard across APIs
* Easier to parse

### Enable ISO Format

Disable timestamp serialization:

```java
ObjectMapper isoObjectMapper = new ObjectMapper().registerModule(new JavaTimeModule()) 
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
```

---

### Example: ISO Serialization

```java
@Test
void whenUsingIsoObjectMapper_thenSerializeAsIsoFormat() throws JsonProcessingException {
    LocalDate date = LocalDate.of(2045, 3, 1);
    LocalDateTime dateTime = LocalDateTime.of(2045, 6, 28, 10, 10, 0);
    LocalDateTime preciseDateTime = LocalDateTime.of(2045, 6, 28, 10, 10, 50, 1234);
    String dateStr = isoObjectMapper.writeValueAsString(date);
    String dateTimeStr = isoObjectMapper.writeValueAsString(dateTime);
    String preciseDateTimeStr = isoObjectMapper.writeValueAsString(preciseDateTime);
    assertEquals("\"2045-03-01\"", dateStr);
    assertEquals("\"2045-06-28T10:10:00\"", dateTimeStr);
    assertEquals("\"2045-06-28T10:10:50.000001234\"", preciseDateTimeStr);
}
```

### Key Observations

* `LocalDate` → `"2045-03-01"`
* `LocalDateTime` → `"2045-06-28T10:10:00"`
* Fractional seconds only appear if non-zero

### Benefits

* More readable
* Standardized
* Less error-prone

---

## 3. Handling Time Zones

`LocalDateTime` has **no time zone**. For real-world use, we often need:

* `ZonedDateTime`
* `OffsetDateTime`

---

## 3.1 ObjectMapper Time Zone Configuration

By default, Jackson uses JVM time zone (often UTC).

We can explicitly set it:

```java
final ObjectMapper isoObjectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    .setTimeZone(TimeZone.getTimeZone("America/New_York"));
```

---

## 3.2 ZonedDateTime Serialization

```java
@Test
void givenZonedDateTime_whenSerializing_thenJSONFormattedUsingContextTimezone() 
  throws JsonProcessingException {
    ZonedDateTime zonedDateTime 
      = ZonedDateTime.of(2045, 6, 28, 10, 10, 10, 0, ZoneId.of("Europe/Berlin"));

    String defaultMapperJson = defaultObjectMapper.writeValueAsString(zonedDateTime);
    String isoMapperJson = isoObjectMapper.writeValueAsString(zonedDateTime);

    assertEquals("2382250210.000000000", defaultMapperJson);
    assertEquals("\"2045-06-28T04:10:10-04:00\"", isoMapperJson); // Adjusts to context time zone
}
```

### Behavior

* Default mapper → **epoch timestamp**
* ISO mapper → adjusts to configured timezone

---

### Preserve Original Time Zone

```java
final ObjectMapper isoObjectMapperNotAdjustingZone 
  = new ObjectMapper().registerModule(new JavaTimeModule())
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    .disable(SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE)
    .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
    .setTimeZone(TimeZone.getTimeZone("America/New_York"));
```

---

### Example

```java
@Test
void givenZonedDateTime_whenSerializingNowAdjustingTZ_thenJSONFormattedUsingDateTimeTz() 
  throws JsonProcessingException {
    ZonedDateTime zonedDateTime 
      = ZonedDateTime.of(2045, 6, 28, 10, 10, 10, 0, ZoneId.of("Europe/Berlin"));
    ZonedDateTime sameZonedDateTimeinUTC = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);

    String isoMapperJson = isoObjectMapperNotAdjustingZone.writeValueAsString(zonedDateTime);
    String isoMapperJsonUTC = isoObjectMapperNotAdjustingZone.writeValueAsString(sameZonedDateTimeinUTC);

    assertEquals("\"2045-06-28T10:10:10+02:00\"", isoMapperJson);
    assertEquals("\"2045-06-28T08:10:10Z\"", isoMapperJsonUTC);
}
```

### Key Insight

* `+02:00` → offset
* `Z` → UTC

---

## 3.3 ZonedDateTime Deserialization

ISO-8601 supports:

* Offsets (`+02:00`, `Z`)
* ❌ Not full zones (`Europe/Berlin`)

---

### Example

```java
@Test
void givenIsoZonedTimeFields_whenUsingIsoMapperNotAdjusting_thenDeserializeWithDateTimeZone() 
  throws JsonProcessingException {
    String isoTimezoneJson = "\"2045-06-28T10:10:10+02:00\"";

    ZonedDateTime zonedDateTimeFromIso 
      = isoObjectMapperNotAdjustingZone.readValue(isoTimezoneJson, ZonedDateTime.class);

    ZonedDateTime expectedZonedDateTimeWithOffset 
      = ZonedDateTime.of(2045, 6, 28, 10, 10, 10, 0, ZoneOffset.of("+02:00"));

    // Passes to input time offset now
    assertEquals(ZoneOffset.of("+02:00"), zonedDateTimeFromIso.getZone());
    assertNotEquals(ZoneId.of("Europe/Berlin"), zonedDateTimeFromIso.getZone());
    assertEquals(expectedZonedDateTimeWithOffset, zonedDateTimeFromIso);
}
```

### Key Point

* Offset is preserved
* Original ZoneId is lost

---

## 4. Handling Other Java Time API and Legacy Classes

Jackson also supports:

* `LocalTime`
* `OffsetDateTime`
* `Instant`
* `Duration`
* `Period`
* `java.util.Date`

Examples are available in the End module for:

* Default serialization
* ISO-8601 serialization

---

## 5. Field-Level Formatting With @JsonFormat

Sometimes we need **custom formats per field**.

### Example Class

```java
class DateWrapper {
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX'['VV']'")
    private ZonedDateTime zonedDateTime;

    // ...
}
```

---

### Example Usage

```java
@Test
void whenUsingJsonFormatAnnotation_thenDeserializeInSpecificFormat() throws JsonProcessingException {
    String json = """
        {
            "date":"01-03-2045",
            "zonedDateTime":"2045-06-28T10:10:10+02:00[Europe/Berlin]"
        }
        """;
    DateWrapper dateWrapper = isoObjectMapperNotAdjustingZone.readValue(json, DateWrapper.class);

    assertEquals(LocalDate.of(2045, 3, 1), dateWrapper.getDate());
    assertNotEquals(ZoneOffset.of("+02:00"), dateWrapper.getZonedDateTime().getZone());
    assertEquals(ZoneId.of("Europe/Berlin"), dateWrapper.getZonedDateTime().getZone());
    assertEquals(ZonedDateTime.of(2045, 6, 28, 10, 10, 10, 0, ZoneId.of("Europe/Berlin")), dateWrapper.getZonedDateTime());
}
```

---

### Key Insight

* Custom pattern allows:

    * Full **ZoneId restoration**
    * Example: `[Europe/Berlin]`

* Without it → only offset is preserved

---

## Final Summary

* Jackson supports Java Time API via `jackson-datatype-jsr310`
* Default format = **numeric arrays**
* Recommended format = **ISO-8601 strings**
* Time zones require careful configuration:

    * Context-based adjustment (default)
    * Preserve original zone (disable features)
* ISO-8601:

    * Supports offsets
    * Does NOT store full zone IDs
* Use `@JsonFormat` for **field-level customization**

---

