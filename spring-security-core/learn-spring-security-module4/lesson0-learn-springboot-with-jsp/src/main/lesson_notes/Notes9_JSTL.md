
---

# JSP JSTL (JavaServer Pages Standard Tag Library)

## 1. Introduction to JSTL

**JSP Standard Tag Library (JSTL)** is a **standard library of ready-made tags** provided by JSP.
These tags are designed to **eliminate the use of JSP scriptlet code (`<% %>`)** from JSP pages.

> ❗ Writing Java code directly inside JSP pages using scriptlets is **not recommended** in modern web development because:

* It makes code difficult to read
* It mixes business logic with presentation
* It violates MVC (Model–View–Controller) principles

JSTL solves this problem by providing **predefined tags** that perform common tasks such as:

* Conditional statements
* Looping
* Output display
* URL handling
* Database access
* XML processing
* String manipulation

---

## 2. Advantages of JSTL

* Removes Java scriptlets from JSP pages
* Makes JSP pages cleaner and easier to read
* Improves maintainability
* Encourages MVC architecture
* Uses Expression Language (EL) instead of Java code
* Standard and portable across servers

---

## 3. JSTL Tag Libraries (Groups)

JSTL is divided into **five major tag libraries**:

---

### 3.1 JSTL Core Library

**Purpose:**
Provides basic control flow and utility tags such as:

* `if`
* `forEach`
* `choose`
* `out`
* `import`
* `set`
* `url`
* `catch`

**Taglib Directive:**

```jsp
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
```

---

### 3.2 JSTL Formatting Library

**Purpose:**
Used for formatting:

* Dates
* Numbers
* Currency
* Time
* Internationalization (i18n)

**Taglib Directive:**

```jsp
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
```

---

### 3.3 JSTL SQL Library

**Purpose:**
Provides tags to interact with relational databases:

* Insert
* Update
* Delete
* Select

⚠️ **Note:** Not recommended for enterprise applications.
Used mainly for learning and small demos.

**Taglib Directive:**

```jsp
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
```

---

### 3.4 JSTL XML Library

**Purpose:**
Used for XML processing such as:

* Parsing XML
* Transformation
* Flow control on XML documents

**Taglib Directive:**

```jsp
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
```

---

### 3.5 JSTL Functions Library

**Purpose:**
Provides functions for string manipulation:

* Length
* Substring
* Contains
* ToUpperCase
* ToLowerCase

**Taglib Directive:**

```jsp
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
```

---

## 4. JSTL Core Library – Important Tags

The **JSTL Core Library** is the most commonly used JSTL library.
It helps remove basic scripting logic such as loops and conditions from JSP pages.

---

## 4.1 JSTL `<c:if>` Tag

### Purpose

* Used for **conditional execution**
* Executes body content **only if condition is true**
* Replaces Java `if` statement

### Syntax

```jsp
<c:if test="condition">
   body
</c:if>
```

### Example

```jsp
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Tag Example</title>
</head>
<body>

<c:if test="${param.name == 'studytonight'}">
  <p>Welcome to ${param.name}</p>
</c:if>

</body>
</html>
```

### Explanation

* `${param.name}` retrieves request parameter
* If name equals `"studytonight"`, the message is displayed
* No Java code is written inside JSP

---

## 4.2 JSTL `<c:out>` Tag

### Purpose

* Displays output safely
* Prevents NullPointerException
* Replaces `out.print()`

### Syntax

```jsp
<c:out value="expression" default="value"/>
```

### Example

```jsp
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Tag Example</title>
</head>
<body>

<c:out value="${param.name}" default="StudyTonight" />

</body>
</html>
```

### Explanation

* If `param.name` is null, default value is printed
* Prevents runtime errors
* Cleaner than scriptlets

---

## 4.3 JSTL `<c:forEach>` Tag

### Purpose

* Iteration over collections
* Similar to enhanced `for` loop in Java

### Syntax

```jsp
<c:forEach var="item" items="collection">
   body
</c:forEach>
```

### Example

```jsp
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Tag Example</title>
</head>
<body>

<c:forEach var="message" items="${errorMsgs}">
  <li>${message}</li>
</c:forEach>

</body>
</html>
```

### Explanation

* `errorMsgs` is a collection
* Each element stored in variable `message`
* Loop executes once per element

---

## 4.4 JSTL `<c:choose>`, `<c:when>`, `<c:otherwise>`

### Purpose

* Implements **if-else-if-else** logic
* Only one `<c:when>` executes

### Example

```jsp
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Tag Example</title>
</head>
<body>

<c:forEach var="tutorial" items="${MyTutorialMap}" varStatus="status">

<c:choose>
  <c:when test="${status.count % 2 == 0}">
    <p>Divisible by 2 : ${tutorial.key}</p>
  </c:when>

  <c:when test="${status.count % 5 == 0}">
    <p>Divisible by 5 : ${tutorial.key}</p>
  </c:when>

  <c:otherwise>
    <p>Neither divisible by 2 nor 5 : ${tutorial.key}</p>
  </c:otherwise>
</c:choose>

</c:forEach>

</body>
</html>
```

### Explanation

* Conditions checked in order
* First true condition executes
* Others are skipped

---

## 4.5 JSTL `<c:import>` Tag

### Purpose

* Includes content from a URL at runtime
* Can load external resources

### Example

```jsp
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Tag Example</title>
</head>
<body>

<c:import url="http://www.example.com/hello.html">
  <c:param name="showproducts" value="true"/>
</c:import>

</body>
</html>
```

### Explanation

* Loads content dynamically
* Parameters can be passed
* Similar to dynamic include

---

## 4.6 JSTL `<c:url>` Tag

### Purpose

* Generates URLs
* Supports URL rewriting

### Example

```jsp
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Tag Example</title>
</head>
<body>

<a href='<c:url value="/home.jsp"/>'>Go Home</a>

</body>
</html>
```

### Explanation

* Automatically handles session tracking
* Recommended over hardcoded URLs

---

## 4.7 JSTL `<c:set>` Tag

### Purpose

* Sets variables
* Updates JavaBean properties

### Example

```jsp
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Tag Example</title>
</head>
<body>

<c:set target="${student}" property="name" value="${param.name}" />

</body>
</html>
```

### Explanation

* Stores user input into JavaBean
* Replaces setter method calls

---

## 4.8 JSTL `<c:catch>` Tag

### Purpose

* Handles exceptions
* Prevents page crash
* Does not forward to error page

### Example

```jsp
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Tag Example</title>
</head>
<body>

<c:catch>
<%
  int a = 0;
  int b = 10;
  int c = b / a;
%>
</c:catch>

</body>
</html>
```

### Explanation

* Exception is caught silently
* Page continues execution
* Useful for safe execution

---

## 5. Conclusion

* JSTL removes Java scriptlets from JSP
* Encourages clean and readable code
* Essential for modern JSP development
* Core library is the most frequently used
* JSTL works best with Expression Language (EL)

---

