# Introduction to JsonPath

## 1. Overview

One of the major advantages of XML is the availability of standardized tools for processing it. Among these tools is **XPath**, which is defined as a **W3C standard** and allows developers to navigate through XML documents and extract specific elements.

For **JSON**, a similar querying mechanism called **JsonPath** has emerged. JsonPath allows developers to **navigate, filter, and extract data from JSON documents** in a structured and expressive way, similar to how XPath works for XML.

This tutorial introduces **Jayway JsonPath**, which is a **Java implementation of the JsonPath specification**. Jayway JsonPath provides developers with a powerful API for parsing JSON data and retrieving specific elements using path expressions.

The tutorial covers the following key aspects:

* Setup and dependency configuration
* JsonPath syntax
* Common APIs used to access JSON data
* Predicates and filters
* Configuration options
* Service Provider Interfaces (SPIs)
* Practical use cases for working with JSON returned from web services

---

# 2. Setup

To start using **JsonPath in a Java project**, we need to include the JsonPath dependency in the **Maven `pom.xml`** file.

### Maven Dependency

```xml
<dependency>
    <groupId>com.jayway.jsonpath</groupId>
    <artifactId>json-path</artifactId>
    <version>2.9.0</version>
</dependency>
```

This dependency provides the **Jayway JsonPath library**, which includes:

* JSON parsing capabilities
* Path-based querying of JSON structures
* Filtering and predicates
* Configuration customization

Once added, JsonPath can be used directly in Java code to query JSON strings, files, URLs, or streams.

---

# 3. Syntax

To demonstrate JsonPath syntax and APIs, we will use the following JSON structure:

```json
{
    "tool": 
    {
        "jsonpath": 
        {
            "creator": 
            {
                "name": "Jayway Inc.",
                "location": 
                [
                    "Malmo",
                    "San Francisco",
                    "Helsingborg"
                ]
            }
        }
    },

    "book": 
    [
        {
            "title": "Beginning JSON",
            "price": 49.99
        },

        {
            "title": "JSON at Work",
            "price": 29.99
        }
    ]
}
```

This structure contains:

* A **tool object**
* A **jsonpath object**
* A **creator object**
* A **location array**
* A **book array** with multiple book objects

JsonPath allows us to traverse such structures using path expressions.

---

# 3.1 Notation

JsonPath uses special notations to represent **nodes and relationships between nodes** in a JSON structure.

There are **two notation styles**:

1. **Dot notation**
2. **Bracket notation**

Both notations refer to the same node but use different syntaxes.

### Example Node

We want to access:

> The third element in the `location` array under
> `tool → jsonpath → creator`.

---

### Dot Notation

```java
$.tool.jsonpath.creator.location[2]
```

Explanation:

| Symbol        | Meaning                    |
| ------------- | -------------------------- |
| `$`           | Root object                |
| `tool`        | Child node                 |
| `jsonpath`    | Child node                 |
| `creator`     | Child node                 |
| `location[2]` | Third element in the array |

Note: JsonPath uses **zero-based indexing**, so `[2]` refers to the **third element**.

---

### Bracket Notation

```java
$['tool']['jsonpath']['creator']['location'][2]
```

This notation is useful when:

* Property names contain spaces
* Special characters exist
* Dynamic access is needed

---

### Root Symbol

The **dollar sign (`$`)** represents the **root member object** of the JSON document.

---

# 3.2 Operators

JsonPath provides several operators that help navigate and query JSON structures.

## 1. Root Node Operator `$`

The `$` symbol represents the **root of the JSON structure**.

Example:

```java
$.tool
```

Accesses the `tool` object from the root.

---

## 2. Current Node Operator `@`

The `@` symbol represents the **current node being processed**.

It is typically used inside **predicates or filters**.

Example:

```java
book[?(@.price == 49.99)]
```

Explanation:

* `@.price` refers to the price field of the current object
* This expression returns books whose price equals **49.99**

---

## 3. Wildcard Operator `*`

The wildcard operator selects **all elements within a scope**.

Example:

```java
book[*]
```

This expression returns **all nodes inside the book array**.

---

# 3.3 Functions and Filters

JsonPath also provides **built-in functions** that can be used at the end of path expressions to compute values.

### Supported Functions

| Function   | Description        |
| ---------- | ------------------ |
| `min()`    | Minimum value      |
| `max()`    | Maximum value      |
| `avg()`    | Average value      |
| `stddev()` | Standard deviation |
| `length()` | Number of elements |

---

### Filters

Filters allow developers to **restrict results based on conditions**.

Filters are **boolean expressions** that determine which nodes should be returned.

Common filter operators include:

| Operator | Meaning                     |
| -------- | --------------------------- |
| `==`     | Equality                    |
| `=~`     | Regular expression matching |
| `in`     | Inclusion check             |
| `empty`  | Check if value is empty     |

Filters are commonly used inside **predicates**.

Example:

```java
book[?(@.price == 49.99)]
```

This returns books whose price equals **49.99**.

For a full list of operators, filters, and functions, developers can refer to the **JsonPath GitHub project documentation**.

---

# 4. Operations

Before discussing operations, remember that this section uses the **JSON structure introduced earlier**.

JsonPath operations mainly involve **reading and filtering JSON data**.

---

# 4.1 Access to Documents

JsonPath provides **static APIs** to read JSON documents.

### Basic Read API

```java
<T> T JsonPath.read(String jsonString, String jsonPath, Predicate... filters);
```

This method:

* Takes a JSON string
* Evaluates the JsonPath expression
* Returns the extracted result

---

### Fluent API

A more flexible approach is using the **fluent parsing API**:

```java
<T> T JsonPath.parse(String jsonString).read(String jsonPath, Predicate... filters);
```

This allows chaining operations.

---

### Supported JSON Sources

JsonPath can read JSON data from multiple sources:

* String
* Object
* InputStream
* URL
* File

---

### Example Paths

```java
String jsonpathCreatorNamePath = "$['tool']['jsonpath']['creator']['name']";
String jsonpathCreatorLocationPath = "$['tool']['jsonpath']['creator']['location'][*]";
```

---

### Parsing JSON

```java
DocumentContext jsonContext = JsonPath.parse(jsonDataSourceString);
```

The `DocumentContext` object represents a **parsed JSON document**.

---

### Reading Data

```java
String jsonpathCreatorName = jsonContext.read(jsonpathCreatorNamePath);

List<String> jsonpathCreatorLocation =
    jsonContext.read(jsonpathCreatorLocationPath);
```

Results:

* First call returns **"Jayway Inc."**
* Second call returns **a list of locations**

---

### Assertions

```java
assertEquals("Jayway Inc.", jsonpathCreatorName);

assertThat(jsonpathCreatorLocation.toString(), containsString("Malmo"));
assertThat(jsonpathCreatorLocation.toString(), containsString("San Francisco"));
assertThat(jsonpathCreatorLocation.toString(), containsString("Helsingborg"));
```

These assertions confirm that the JsonPath queries return expected results.

---

# 4.2 Predicates

Predicates are conditions used to **filter JSON nodes**.

Consider the following JSON example:

```json
{
    "book":[
        {
            "title":"Beginning JSON",
            "author":"Ben Smith",
            "price":49.99
        },
        {
            "title":"JSON at Work",
            "author":"Tom Marrs",
            "price":29.99
        },
        {
            "title":"Learn JSON in a DAY",
            "author":"Acodemy",
            "price":8.99
        },
        {
            "title":"JSON: Questions and Answers",
            "author":"George Duckett",
            "price":6.00
        }
    ],

    "price range":{
        "cheap":10.00,
        "medium":20.00
    }
}
```

Predicates help determine **true/false conditions** used in filters.

---

## Example 1: Using Filter with Criteria

```java
Filter expensiveFilter =
    Filter.filter(Criteria.where("price").gt(20.00));

List<Map<String, Object>> expensive =
    JsonPath.parse(jsonDataSourceString)
    .read("$['book'][?]", expensiveFilter);
```

This filter selects books with price **greater than 20**.

---

## Example 2: Custom Predicate

```java
Predicate expensivePredicate = new Predicate() {
    public boolean apply(PredicateContext context) {
        String value = context.item(Map.class)
                              .get("price").toString();
        return Float.valueOf(value) > 20.00;
    }
};
```

Usage:

```java
List<Map<String, Object>> expensive =
    JsonPath.parse(jsonDataSourceString)
    .read("$['book'][?]", expensivePredicate);
```

---

## Example 3: Inline Predicate

Inline predicates can be written directly inside the JsonPath expression.

```java
List<Map<String, Object>> expensive =
    JsonPath.parse(jsonDataSourceString)
    .read("$['book'][?(@['price'] > $['price range']['medium'])]");
```

---

### Assertion Helper Method

```java
private void predicateUsageAssertionHelper(List<?> predicate) {
    assertThat(predicate.toString(), containsString("Beginning JSON"));
    assertThat(predicate.toString(), containsString("JSON at Work"));
    assertThat(predicate.toString(),
        not(containsString("Learn JSON in a DAY")));
    assertThat(predicate.toString(),
        not(containsString("JSON: Questions and Answers")));
}
```

This helper confirms that only the expected books are returned.

---

# 5. Configuration

Jayway JsonPath allows developers to customize behavior using **configuration options and SPIs**.

---

# 5.1 Options

JsonPath provides several configuration options.

| Option                      | Description                             |
| --------------------------- | --------------------------------------- |
| `AS_PATH_LIST`              | Returns paths instead of values         |
| `DEFAULT_PATH_LEAF_TO_NULL` | Returns null for missing leaves         |
| `ALWAYS_RETURN_LIST`        | Always returns list                     |
| `SUPPRESS_EXCEPTIONS`       | Prevents exceptions during evaluation   |
| `REQUIRE_PROPERTIES`        | Requires properties in indefinite paths |

---

### Creating Configuration

```java
Configuration configuration =
    Configuration.builder()
    .options(Option.<OPTION>)
    .build();
```

---

### Adding Option to Existing Configuration

```java
Configuration newConfiguration =
    configuration.addOptions(Option.<OPTION>);
```

---

# 5.2 SPIs (Service Provider Interfaces)

Advanced users can modify JsonPath behavior using **three SPIs**.

### 1. JsonProvider SPI

Controls how JsonPath **parses and handles JSON documents**.

---

### 2. MappingProvider SPI

Allows customization of **object mapping between JSON nodes and Java types**.

---

### 3. CacheProvider SPI

Controls how **JsonPath expressions are cached**, improving performance.

---

# 6. Example Use Cases

Now we examine practical scenarios involving **JSON returned from a web service**.

Assume a movie service returns this JSON:

```json
[
    {
        "id":1,
        "title":"Casino Royale",
        "director":"Martin Campbell",
        "starring":["Daniel Craig","Eva Green"],
        "desc":"Twenty-first James Bond movie",
        "release date":1163466000000,
        "box office":594275385
    }
]
```

Where:

* **release date** is milliseconds since the **Epoch**
* **box office** is revenue in **US dollars**

The JSON is stored in a variable:

```java
String jsonString
```

---

# 6.1 Getting Object Data Given IDs

Suppose a client requests the movie with **id = 2**.

```java
Object dataObject =
    JsonPath.parse(jsonString)
    .read("$[?(@.id == 2)]");

String dataString = dataObject.toString();
```

Assertions:

```java
assertThat(dataString, containsString("2"));
assertThat(dataString, containsString("Quantum of Solace"));
assertThat(dataString,
    containsString("Twenty-second James Bond movie"));
```

---

# 6.2 Getting Movie Title Given Starring

Find the movie starring **Eva Green**.

```java
List<Map<String, Object>> dataList =
    JsonPath.parse(jsonString)
    .read("$[?('Eva Green' in @['starring'])]");

String title =
    (String) dataList.get(0).get("title");
```

Assertion:

```java
assertEquals("Casino Royale", title);
```

---

# 6.3 Calculation of Total Revenue

Use the `length()` function to count movies.

```java
DocumentContext context = JsonPath.parse(jsonString);

int length = context.read("$.length()");
long revenue = 0;

for (int i = 0; i < length; i++) {
    revenue += context.read("$[" + i + "]['box office']", Long.class);
}
```

Assertion:

```java
assertEquals(
594275385L + 591692078L + 1110526981L + 879376275L,
revenue
);
```

---

# 6.4 Highest Revenue Movie

First extract revenues:

```java
DocumentContext context = JsonPath.parse(jsonString);

List<Object> revenueList =
    context.read("$[*]['box office']");

Integer[] revenueArray =
    revenueList.toArray(new Integer[0]);

Arrays.sort(revenueArray);
```

Determine highest revenue:

```java
int highestRevenue =
    revenueArray[revenueArray.length - 1];
```

Use **AS_PATH_LIST option**:

```java
Configuration pathConfiguration =
Configuration.builder()
.options(Option.AS_PATH_LIST)
.build();

List<String> pathList =
JsonPath.using(pathConfiguration)
.parse(jsonString)
.read("$[?(@['box office'] == " + highestRevenue + ")]");
```

Retrieve title:

```java
Map<String, String> dataRecord =
    context.read(pathList.get(0));

String title = dataRecord.get("title");
```

Assertion:

```java
assertEquals("Skyfall", title);
```

---

# 6.5 Latest Movie of a Director

Find the latest movie directed by **Sam Mendes**.

First retrieve movies:

```java
DocumentContext context = JsonPath.parse(jsonString);

List<Map<String, Object>> samMendesMovies =
context.read("$[?(@.director == 'Sam Mendes')]");
```

Alternative approach using Filter:

```java
Filter directorSamMendesFilter =
Filter.filter(
Criteria.where("director")
.contains("Sam Mendes"));

List<Map<String, Object>> samMendesMovies =
JsonPath.parse(jsonString)
.read("$[?]", directorSamMendesFilter);
```

Extract release dates:

```java
List<Object> dateList = new ArrayList<>();

for (Map<String, Object> item : samMendesMovies) {
    Object date = item.get("release date");
    dateList.add(date);
}

Long[] dateArray = dateList.toArray(new Long[0]);
Arrays.sort(dateArray);
```

Find latest date:

```java
long latestTime =
    dateArray[dateArray.length - 1];
```

Retrieve movie title:

```java
List<Map<String, Object>> finalDataList =
context.read("$[?(@['director'] == 'Sam Mendes' && @['release date'] == "
+ latestTime + ")]");

String title =
(String) finalDataList.get(0).get("title");
```

Assertion:

```java
assertEquals("Spectre", title);
```

---

# 7. Conclusion

This tutorial introduced the **fundamental features of Jayway JsonPath**, a powerful Java library used to **traverse and parse JSON documents**.

Key takeaways include:

* JsonPath is similar to **XPath for JSON**
* It allows **structured querying of JSON data**
* Supports **path expressions, filters, predicates, and functions**
* Provides flexible **configuration options**
* Can process JSON from **multiple sources**
* Useful in **web service data extraction and testing**

Although JsonPath has some limitations, such as the **lack of operators for navigating parent or sibling nodes**, it remains a **highly useful tool** for many scenarios involving JSON processing.

---

