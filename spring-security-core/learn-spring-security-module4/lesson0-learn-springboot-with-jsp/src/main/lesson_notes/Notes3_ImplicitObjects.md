
---
# Implicit Objects

---
## 1. What Are Implicit Objects?

When a JSP page runs, it is internally converted into a Servlet by the web container (Tomcat, GlassFish, etc.).

In Servlets:

    * The programmer must create objects manually

    * The code becomes long and complex

In JSP:

    * Some important objects are automatically created

    * These are called implicit objects

👉 Implicit means you do not create them yourself.

## 1.1. Why Implicit Objects Are Important

Implicit objects help JSP developers to:

* Read user input easily

* Send output to the browser

* Track users using sessions

* Share data across pages

* Handle errors gracefully

Without implicit objects, JSP would be as complex as Servlets.

---
# 2. `request` Implicit Object

## 2.1 What is the `request` Object?

The `request` implicit object represents an instance of `HttpServletRequest`. It is used to:

* Retrieve form data
* Read request parameters
* Get server information

---

## 1.2 Servlet Version

```java
import javax.servlet.http.*;

public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        response.getWriter().println("Welcome " + username);
    }
}
```

---

## 2.3 HTML Version

```html
<form action="welcome.jsp">
    <input type="text" name="username">
    <input type="submit" value="Submit">
</form>
```

---

## 2.4 JSP Version

```jsp
<%
    String name = request.getParameter("username");
    out.print("Welcome " + name);
%>
```

---

## 2.5 Explanation

* `request` is automatically available in JSP
* No object creation is required
* Commonly used for form handling

---

# 3. `response` Implicit Object

## 3.1 What is the `response` Object?

Represents `HttpServletResponse`. It is used to:

* Send output to the client
* Redirect users
* Set headers and cookies

---

## 3.2 Servlet Version

```java
protected void doGet(HttpServletRequest request,
                     HttpServletResponse response)
        throws IOException {

    response.sendRedirect("home.html");
}
```

---

## 3.3 JSP Version

```jsp
<%
    response.sendRedirect("home.html");
%>
```

---

## 3.4 Explanation

* `response` is used for navigation control
* Redirects cause a new HTTP request

---

# 4. `out` Implicit Object

## 4.1 What is the `out` Object?

Represents `JspWriter`. It is used to send output to the browser.

---

## 4.2 Servlet Version

```java
PrintWriter out = response.getWriter();
out.println("Hello from Servlet");
```

---

## 4.3 JSP Version

```jsp
<%
    out.println("Hello from JSP");
%>
```

---

## 4.4 Explanation

* `out` replaces `PrintWriter` in JSP
* Used for displaying dynamic content

---

# 5. `session` Implicit Object

## 5.1 What is the `session` Object?

Represents `HttpSession`. It is used to:

* Store user-specific data
* Maintain login state

---

## 5.2 Servlet Version

```java
HttpSession session = request.getSession();
session.setAttribute("user", "Godfrey");
```

---

## 5.3 JSP Version

```jsp
<%
    session.setAttribute("user", "Godfrey");
    out.print("Session created");
%>
```

---

## 5.4 Reading Session Data (JSP)

```jsp
<%
    String user = (String) session.getAttribute("user");
    out.print("Welcome " + user);
%>
```

---

## 5.5 Explanation

* Sessions persist across multiple requests
* Common in authentication systems

---

# 6. `application` Implicit Object

## 6.1 What is the `application` Object?

Represents `ServletContext`. Used to share data across the entire application.

---

## 6.2 Servlet Version

```java
ServletContext context = getServletContext();
context.setAttribute("siteName", "JSP Tutorial");
```

---

## 6.3 JSP Version

```jsp
<%
    application.setAttribute("siteName", "JSP Tutorial");
%>
```

---

## 6.4 Reading Application Data (JSP)

```jsp
<%
    out.print(application.getAttribute("siteName"));
%>
```

---

## 6.5 Explanation

* Data is shared by all users
* Exists until server shutdown

---

# 7. `config` Implicit Object

## 7.1 What is the `config` Object?

Represents `ServletConfig`. Used to access initialization parameters.

---

## 7.2 Servlet Version

```java
ServletConfig config = getServletConfig();
String driver = config.getInitParameter("driver");
```

---

## 7.3 JSP Version

```jsp
<%
    String driver = config.getInitParameter("driver");
    out.print(driver);
%>
```

---

## 7.4 Explanation

* Configuration is defined in `web.xml`
* Used for database settings

---

# 8. `pageContext` Implicit Object

## 8.1 What is the `pageContext` Object?

Represents `PageContext`. It manages attributes in different scopes.

---

## 8.2 JSP Version

```jsp
<%
    pageContext.setAttribute("course", "JSP");
    out.print(pageContext.getAttribute("course"));
%>
```

---

## 8.3 Scope Example

```jsp
<%
    pageContext.setAttribute("x", 10, PageContext.REQUEST_SCOPE);
%>
```

---

## 8.4 Explanation

* Acts as a gateway to all other implicit objects
* Supports page, request, session, application scopes

---

# 9. `page` Implicit Object

## 9.1 What is the `page` Object?

Refers to the current JSP servlet instance. Similar to `this` in Java.

---

## 9.2 JSP Version

```jsp
<%
    out.print(page.toString());
%>
```

---

## 9.3 Explanation

* Rarely used in real projects
* Mainly for internal reference

---

# 10. `exception` Implicit Object

## 10.1 What is the `exception` Object?

Represents `Throwable`. Used only in **error pages**.

---

## 10.2 JSP Error Page Configuration

```jsp
<%@ page isErrorPage="true" %>
```

---

## 10.3 JSP Error Handling Example

```jsp
<%@ page isErrorPage="true" %>

<%
    out.print("Error occurred: " + exception.getMessage());
%>
```

---

## 10.4 Normal JSP Trigger Page

```jsp
<%@ page errorPage="error.jsp" %>

<%
    int x = 10 / 0;
%>
```

---

## 10.5 Explanation

* Automatically holds the thrown exception
* Used for centralized error handling

---

# Summary Table

| Implicit Object | Servlet Equivalent  |
| --------------- | ------------------- |
| request         | HttpServletRequest  |
| response        | HttpServletResponse |
| out             | PrintWriter         |
| session         | HttpSession         |
| application     | ServletContext      |
| config          | ServletConfig       |
| pageContext     | PageContext         |
| page            | this                |
| exception       | Throwable           |

---

