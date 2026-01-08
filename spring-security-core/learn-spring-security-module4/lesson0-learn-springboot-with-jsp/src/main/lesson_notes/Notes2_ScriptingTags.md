
---

# JSP Scripting Tags (JSP Scripting Elements)

---

## Introduction to JSP Scripting Elements

JSP Scripting Elements are special tags used to write **Java code inside a JSP page**.
These elements are written inside **JSP tags**, and the Java code within them is processed by the **JSP engine during the translation phase** (when JSP is converted into a Servlet).

Any content written **outside JSP scripting tags** is treated as **HTML or plain text** and is sent directly to the browser.

---

## Basic Syntax of JSP Scripting Element

```jsp
<% Java code %>
```

---

## Simple Example

```jsp
<html>
    <head>
        <title>My First JSP Page</title>
    </head>

    <%
        int count = 0;
    %>

    <body>
        Page Count is <% out.println(++count); %>
    </body>
</html>
```

### Explanation

* Java code is written inside `<% %>` tags.
* The variable `count` is initialized and incremented.
* The output is printed using the implicit `out` object.

📌 **Experiment Tip**
If you remove the `<% %>` scriptlet tags and run the page:

* Everything will be displayed exactly as written.
* This happens because, without scriptlet tags, the JSP engine treats all content as **plain HTML**.

---

## Types of JSP Scripting Elements

There are **five types** of JSP scripting elements:

| Scripting Element | Syntax Example       |
| ----------------- | -------------------- |
| Comment           | `<%-- comment --%>`  |
| Directive         | `<%@ directive %>`   |
| Declaration       | `<%! declaration %>` |
| Scriptlet         | `<% scriptlet %>`    |
| Expression        | `<%= expression %>`  |

---

## 1. JSP Comment

### Description

JSP Comments are used to add **explanatory notes** in a JSP page for developers.

### Key Characteristics

* Visible only in JSP source code
* Removed during JSP translation
* Not included in generated servlet
* Not visible in browser output

### Syntax

```jsp
<%-- JSP comment --%>
```

---

### Example

```jsp
<html>
    <head>
        <title>My First JSP Page</title>
    </head>

    <%
        int count = 0;
    %>

    <body>
        <%-- Code to display page count --%>
        Page Count is <% out.println(++count); %>
    </body>
</html>
```

📌 **Note**
Writing comments is a **good programming practice**. It improves readability and makes maintenance easier.

---

## 2. JSP Scriptlet Tag

### Description

The Scriptlet Tag allows you to write **Java statements inside a JSP page**.
All code inside a scriptlet is placed inside the **`_jspService()` method** of the generated servlet.

### Syntax

```jsp
<% Java code %>
```

---

### Example: Page Visit Counter

```jsp
<html>
    <head>
        <title>My First JSP Page</title>
    </head>

    <%
        int count = 0;
    %>

    <body>
        Page Count is <% out.println(++count); %>
    </body>
</html>
```

### Explanation

* Scriptlet code is compiled as Java.
* The `count` variable is initialized and incremented.
* Scriptlets are executed **for every request**.

📌 JSP allows you to:

* Perform calculations
* Process user input
* Interact with databases
  directly inside HTML using scriptlet tags.

---

### Example: Reading User Input Using Scriptlet

#### index.html

```html
<form method="POST" action="welcome.jsp">
    Name <input type="text" name="user">
    <input type="submit" value="Submit">
</form>
```

* Sends a POST request to `welcome.jsp`
* Sends the parameter `user`

---

#### welcome.jsp

```jsp
<html>
    <head>
        <title>Welcome Page</title>
    </head>

    <%
        String user = request.getParameter("user");
    %>

    <body>
        Hello, <% out.println(user); %>
    </body>
</html>
```

### Explanation

* JSP is translated into a servlet.
* `_jspService()` receives `HttpServletRequest` and `HttpServletResponse`.
* Form data is accessed using `request.getParameter()`.

---

## Mixing Scriptlet Tags with HTML

JSP allows Java logic and HTML to be mixed to create **dynamic web pages**.

---

### Example: Dynamic Table Using Scriptlet

```jsp
<table border="1">
<%
    for (int i = 0; i < n; i++) {
%>
        <tr>
            <td>Number</td>
            <td><%= i + 1 %></td>
        </tr>
<%
    }
%>
</table>
```

### Explanation

* Java logic is inside scriptlet tags
* HTML remains outside
* Expression tag prints values

---

### Example: Conditional Display

```jsp
<%
    if (hello) {
%>
        <p>Hello, world</p>
<%
    } else {
%>
        <p>Goodbye, world</p>
<%
    }
%>
```

* Uses `if–else` condition
* Output depends on the value of `hello`
* Input can come from an HTML form

---

## 3. JSP Declaration Tag

### Description

The Declaration Tag is used to declare **variables and methods at the class level**.
These declarations appear **outside the `_jspService()` method** in the generated servlet.

### Syntax

```jsp
<%! declaration %>
```

---

### Example: Variable Declaration

```jsp
<html>
    <head>
        <title>My First JSP Page</title>
    </head>

    <%!
        int count = 0;
    %>

    <body>
        Page Count is: <% out.println(++count); %>
    </body>
</html>
```

---

### Equivalent Generated Servlet (Conceptual)

```java
public class hello_jsp extends HttpServlet {
    int count = 0;

    public void _jspService(HttpServletRequest request,
                            HttpServletResponse response) {
        out.print(++count);
    }
}
```

📌 **Key Difference**

* Declaration → outside `_jspService()`
* Scriptlet → inside `_jspService()`

---

### When to Use Declaration Instead of Scriptlet

* Use **Declaration Tag** to define:

    * Methods
    * Instance variables
* Scriptlet tags **cannot contain methods**, because Java does not allow methods inside methods.

---

### Example: Declaring a Method

```jsp
<html>
    <head>
        <title>My First JSP Page</title>
    </head>

    <%!
        int count = 0;

        int getCount() {
            return count;
        }
    %>

    <body>
        Page Count is: <% out.println(getCount()); %>
    </body>
</html>
```

---

### Generated Servlet (Conceptual)

```java
public class hello_jsp extends HttpServlet {
    int count = 0;

    int getCount() {
        return count;
    }

    public void _jspService(HttpServletRequest request,
                            HttpServletResponse response) {
        out.print(getCount());
    }
}
```

---

## 4. JSP Directive Tag

### Description

Directive Tags provide **instructions to the Web Container** during JSP translation.

### Types of Directive Tags

| Directive | Description                |
| --------- | -------------------------- |
| `page`    | Sets page-level properties |
| `include` | Includes another file      |
| `taglib`  | Declares tag libraries     |

📌 Best practice: place the **page directive at the top** of the JSP file.

---

### Page Directive Syntax

```jsp
<%@ page attribute="value" %>
```

---

### Common Page Directive Attributes

* `import`
* `language`
* `extends`
* `session`
* `isThreadSafe`
* `isErrorPage`
* `errorPage`
* `contentType`
* `autoFlush`
* `buffer`

---

### Examples

**Import Classes**

```jsp
<%@ page import="java.util.Date, java.net.*" %>
```

**Session Control**

```jsp
<%@ page session="true" %>
```

**Content Type**

```jsp
<%@ page contentType="text/html" %>
```

---

## 5. JSP Expression Tag

### Description

The Expression Tag is used to **display Java expressions directly** in the browser output.

### Syntax

```jsp
<%= Java Expression %>
```

📌 JSP engine converts it into:

```java
out.print(expression);
```

---

### Example

```jsp
<html>
    <head>
        <title>My First JSP Page</title>
    </head>

    <%
        int count = 0;
    %>

    <body>
        Page Count is <%= ++count %>
    </body>
</html>
```

---

### Important Rule

❌ Incorrect:

```jsp
<%= (2 * 5); %>
```

✔ Correct:

```jsp
<%= (2 * 5) %>
```

---

## Summary

* JSP scripting tags allow Java inside HTML
* JSP is translated into a servlet
* Scriptlets → logic per request
* Declarations → variables and methods at class level
* Expressions → output values
* Directives → configure JSP behavior
* JSP comments → removed during translation

---

