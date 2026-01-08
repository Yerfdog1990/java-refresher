
---

# JSP Standard Action Tags (Action Elements)

---

## 1. Introduction to JSP Standard Action Tags

JSP **Standard Action Tags** (also called **Action Elements**) are predefined XML-style tags provided by the **JSP specification**. These tags are used inside JSP pages to perform common tasks **without writing Java code directly**.

### Why Action Tags Exist

In early JSP development, developers often used **scriptlets** like this:

```jsp
<%
    String name = request.getParameter("username");
%>
```

Although this works, it is now considered **bad practice** because:

* It mixes Java code with HTML
* It makes JSP pages hard to read and maintain
* It violates separation of concerns

👉 **Modern JSP encourages minimizing or eliminating scriptlets**.
👉 JSP Standard Action Tags help achieve this goal.

---

## 2. General Characteristics of Action Tags

* All standard action tags start with the prefix **`jsp:`**
* They are processed **at request time**
* They look like XML tags
* They make JSP pages cleaner and easier to maintain

Example syntax:

```jsp
<jsp:tagName attribute="value" />
```

---

## 3. List of JSP Standard Action Tags with Mini Projects

| JSP Action Tag        | Description + Action Tag Example                                                                                                                                            | Scriptlet Equivalent                                                                                              | Mini Project (Real-World Use Case)                                                                                                                                          |
| --------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------- |-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **`jsp:forward`**     | **Purpose:** Forwards request to another JSP on the server side. <br><br> **Example:** <br>`jsp <jsp:forward page="dashboard.jsp" /> `                                      | `jsp <% RequestDispatcher rd = request.getRequestDispatcher("dashboard.jsp"); rd.forward(request, response); %> ` | **Mini Project: Login Redirect System** <br>• User submits login form <br>• If credentials are correct → forward to `dashboard.jsp` <br>• If wrong → forward to `error.jsp` |
| **`jsp:useBean`**     | **Purpose:** Creates or retrieves a JavaBean object. <br><br> **Example:** <br>`jsp <jsp:useBean id="user" class="model.User" scope="session"/> `                           | `jsp <% User user = new User(); session.setAttribute("user", user); %> `                                          | **Mini Project: User Profile Storage** <br>• Store user name, email, age in a JavaBean <br>• Maintain data across pages using session scope                                 |
| **`jsp:setProperty`** | **Purpose:** Sets values into a JavaBean property. <br><br> **Example:** <br>`jsp <jsp:setProperty name="user" property="username" value="Alice"/> `                        | `jsp <% user.setUsername("Alice"); %> `                                                                           | **Mini Project: Registration Form** <br>• User fills registration form <br>• Form values are stored inside JavaBean properties                                              |
| **`jsp:getProperty`** | **Purpose:** Retrieves values from a JavaBean. <br><br> **Example:** <br>`jsp <jsp:getProperty name="user" property="username"/> `                                          | `jsp <% out.print(user.getUsername()); %> `                                                                       | **Mini Project: Profile Display Page** <br>• Display user details saved during registration <br>• No Java code inside HTML                                                  |
| **`jsp:include`**     | **Purpose:** Includes output of another JSP dynamically. <br><br> **Example:** <br>`jsp <jsp:include page="header.jsp"/> `                                                  | `jsp <% RequestDispatcher rd = request.getRequestDispatcher("header.jsp"); rd.include(request,response); %> `     | **Mini Project: Website Layout System** <br>• One `header.jsp` and `footer.jsp` <br>• Included in every page dynamically                                                    |
| **`jsp:param`**       | **Purpose:** Passes parameters during forward/include. <br><br> **Example:** <br>`jsp <jsp:forward page="result.jsp"> <jsp:param name="score" value="85"/> </jsp:forward> ` | `jsp <% request.setAttribute("score","85"); %> `                                                                  | **Mini Project: Online Exam Result Page** <br>• Pass marks from exam page <br>• Result page displays pass/fail                                                              |
| **`jsp:plugin`**      | **Purpose:** Loads Java applets (legacy). <br><br> **Example:** <br>`jsp <jsp:plugin type="applet" code="ClockApplet.class"/> `                                             | ❌ No clean scriptlet equivalent                                                                                  | **Mini Project: Java Applet Clock** <br>• Embed a digital clock applet in a JSP page <br>• Mostly theoretical today                                                         |
| **`jsp:fallback`**    | **Purpose:** Shows message if plugin fails. <br><br> **Example:** <br>`jsp <jsp:fallback>Applet not supported</jsp:fallback> `                                              | ❌ No scriptlet equivalent                                                                                        | **Mini Project: Browser Compatibility Message** <br>• Show user-friendly message when plugin fails                                                                          |
| **`jsp:element`**     | **Purpose:** Creates XML/HTML dynamically. <br><br> **Example:** <br>`jsp <jsp:element name="h2"> Welcome User </jsp:element> `                                             | `jsp <% out.print("<h2>Welcome User</h2>"); %> `                                                                  | **Mini Project: Dynamic Heading Generator** <br>• Generate headings based on user role (Admin/User)                                                                         |
| **`jsp:attribute`**   | **Purpose:** Creates dynamic attributes. <br><br> **Example:** <br>`jsp <jsp:attribute name="style">color:blue</jsp:attribute> `                                            | `jsp <% out.print("style='color:blue'"); %> `                                                                     | **Mini Project: Dynamic Styling Page** <br>• Change text color based on user selection                                                                                      |
| **`jsp:body`**        | **Purpose:** Defines content inside tags. <br><br> **Example:** <br>`jsp <jsp:body>Content here</jsp:body> `                                                                | ❌ No equivalent                                                                                                  | **Mini Project: Custom Tag Layout** <br>• Define reusable content blocks in JSP                                                                                             |
| **`jsp:text`**        | **Purpose:** Outputs static text safely. <br><br> **Example:** <br>`jsp <jsp:text>Hello World</jsp:text> `                                                                  | `jsp <% out.print("Hello World"); %> `                                                                            | **Mini Project: Static Template Page** <br>• Display copyright text <br>• Prevent accidental parsing of special characters                                                  |

---

# 4. `<jsp:forward>` Action Tag

## 4.1 What is `<jsp:forward>`?

The `<jsp:forward>` tag forwards the **current request** to another resource (JSP, servlet, or HTML page).

* The browser URL **does not change**
* The same request and response objects are used

### Syntax

```jsp
<jsp:forward page="relativeURL" />
```

---

## 4.2 Example

### login.jsp

```jsp
<%
    String user = request.getParameter("username");
%>

<jsp:forward page="welcome.jsp" />
```

### welcome.jsp

```jsp
<h2>Welcome to the application</h2>
```

---

## 4.3 Explanation

* Control is transferred internally to `welcome.jsp`
* User never sees the forwarding
* Useful for navigation control

---

## 4.4 Real-World Use

* Login success/failure routing
* Page flow control
* MVC controller logic

---

# 5. `<jsp:useBean>` Action Tag

---

## 5.1 What is `<jsp:useBean>`?

The `<jsp:useBean>` tag is used to **create or locate a JavaBean object**.

A **JavaBean** is a simple Java class that:

* Has a no-argument constructor
* Has private properties
* Uses getters and setters

---

## 5.2 Syntax

```jsp
<jsp:useBean id="beanId" class="package.ClassName" scope="scope" />
```

---

## 5.3 JavaBean Example

### Student.java

```java
public class Student {
    private String name;

    public Student() {}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

---

## 5.4 JSP Example

```jsp
<jsp:useBean id="student" class="Student" scope="session" />
```

---

## 5.5 Explanation

* If `student` exists → reuse it
* Otherwise → create a new one
* Scope controls lifespan

---

## 5.6 Real-World Use

* User profile data
* Form backing objects
* MVC model objects

---

# 6. `<jsp:getProperty>` Action Tag

---

## 6.1 What is `<jsp:getProperty>`?

Used to **retrieve a property value** from a JavaBean.

---

## 6.2 Syntax

```jsp
<jsp:getProperty name="beanId" property="propertyName" />
```

---

## 6.3 Example

```jsp
<jsp:useBean id="student" class="Student" />
<jsp:getProperty name="student" property="name" />
```

---

## 6.4 Explanation

* Calls `getName()` internally
* Displays value in the output

---

## 6.5 Real-World Use

* Displaying user details
* Showing form data
* Outputting database results

---

# 7. `<jsp:setProperty>` Action Tag

---

## 7.1 What is `<jsp:setProperty>`?

Used to **set a value** into a JavaBean property.

---

## 7.2 Syntax

```jsp
<jsp:setProperty name="beanId" property="propertyName" value="value" />
```

---

## 7.3 Example

```jsp
<jsp:useBean id="student" class="Student" />
<jsp:setProperty name="student" property="name" value="John" />
```

---

## 7.4 Automatic Form Mapping

```jsp
<jsp:setProperty name="student" property="*" />
```

✔ Automatically maps request parameters to bean properties.

---

## 7.5 Real-World Use

* Form processing
* Data binding
* MVC applications

---

# 8. `<jsp:include>` Action Tag

---

## 8.1 What is `<jsp:include>`?

Includes the **runtime output** of another resource into the current page.

---

## 8.2 Syntax

```jsp
<jsp:include page="header.jsp" />
```

---

## 8.3 Example

```jsp
<jsp:include page="header.jsp" />
<h2>Main Content</h2>
<jsp:include page="footer.jsp" />
```

---

## 8.4 Explanation

* Included page is executed every time
* Useful for reusable components

---

## 8.5 Real-World Use

* Headers and footers
* Navigation menus
* Layout templates

---

# 9. `<jsp:param>` Action Tag

---

## 9.1 What is `<jsp:param>`?

Adds parameters to a request during include or forward.

---

## 9.2 Example

```jsp
<jsp:forward page="welcome.jsp">
    <jsp:param name="user" value="Godfrey" />
</jsp:forward>
```

---

## 9.3 Explanation

* Adds `user=Godfrey` to request
* Accessible using `request.getParameter()`

---

## 9.4 Real-World Use

* Passing data between pages
* Navigation parameters

---

# 10. `<jsp:plugin>` and `<jsp:fallback>`

---

## 10.1 What is `<jsp:plugin>`?

Used to load **Java applets** (legacy technology).

---

## 10.2 Example

```jsp
<jsp:plugin type="applet" code="MyApplet.class">
    <jsp:fallback>
        Applet not supported
    </jsp:fallback>
</jsp:plugin>
```

---

## 10.3 Explanation

* Generates browser-specific tags
* Rarely used today

---

# 11. XML-Related Tags

---

## `<jsp:element>`

Creates XML elements dynamically.

```jsp
<jsp:element name="student">
    <jsp:body>John</jsp:body>
</jsp:element>
```

---

## `<jsp:attribute>`

Defines attributes dynamically.

---

## `<jsp:body>`

Defines tag body content.

---

## `<jsp:text>`

Outputs template text exactly as written.

```jsp
<jsp:text>
    This is static template text
</jsp:text>
```

---

# 12. Advantages of JSP Action Tags

* Reduce Java code in JSP
* Improve readability
* Encourage MVC design
* Easier maintenance

---

# 13. Summary 

| Tag             | Main Purpose         |
| --------------- | -------------------- |
| jsp:forward     | Page navigation      |
| jsp:useBean     | Create model objects |
| jsp:getProperty | Read bean data       |
| jsp:setProperty | Write bean data      |
| jsp:include     | Reuse page parts     |
| jsp:param       | Pass parameters      |
| jsp:text        | Static template text |

---

# MINI PROJECT 1: Page Navigation Using `<jsp:forward>` and `<jsp:param>`

---

## 🎯 Project Goal

Forward a user to another JSP page and pass data **without writing Java code**.

---

## 📂 Files Used

* `index.jsp`
* `welcome.jsp`

---

### index.jsp

```jsp
<h2>Home Page</h2>

<jsp:forward page="welcome.jsp">
    <jsp:param name="username" value="Godfrey" />
</jsp:forward>
```

---

### welcome.jsp

```jsp
<h2>Welcome Page</h2>

Username received successfully.
```

---

## 🧠 Explanation (Beginner Level)

* `<jsp:forward>` moves control to another page
* `<jsp:param>` sends data along with the request
* No browser URL change occurs
* No Java code is written

---

## 🌍 Real-World Use

* Login success routing
* Dashboard navigation
* MVC controller flow

---

# MINI PROJECT 2: JavaBean Creation Using `<jsp:useBean>`

---

## 🎯 Project Goal

Create and reuse a JavaBean object using action tags.

---

## 📂 Files Used

* `student.jsp`
* `Student.java`

---

### Student.java (JavaBean)

```java
public class Student {
    private String name;

    public Student() {}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

---

### student.jsp

```jsp
<jsp:useBean id="student" class="Student" scope="session" />

Student bean created successfully.
```

---

## 🧠 Explanation

* `<jsp:useBean>` creates the object automatically
* `scope="session"` keeps it available across pages
* Bean is reused if it already exists

---

## 🌍 Real-World Use

* User profile storage
* Shopping cart object
* Model objects in MVC

---

# MINI PROJECT 3: Store and Display Data Using `<jsp:setProperty>` and `<jsp:getProperty>`

---

## 🎯 Project Goal

Set values into a JavaBean and display them **without Java code**.

---

### studentDetails.jsp

```jsp
<jsp:useBean id="student" class="Student" scope="session" />

<jsp:setProperty name="student" property="name" value="Alice" />

<h2>Student Name:</h2>
<jsp:getProperty name="student" property="name" />
```

---

## 🧠 Explanation

* `<jsp:setProperty>` calls `setName("Alice")`
* `<jsp:getProperty>` calls `getName()`
* Everything happens behind the scenes

---

## 🌍 Real-World Use

* Form handling
* Profile display pages
* Data binding

---

# MINI PROJECT 4: Automatic Form Handling Using `<jsp:setProperty property="*">`

---

## 🎯 Project Goal

Automatically map form data to a JavaBean.

---

## 📂 Files Used

* `form.jsp`
* `process.jsp`

---

### form.jsp

```jsp
<form action="process.jsp">
    Name: <input type="text" name="name"><br>
    <input type="submit" value="Submit">
</form>
```

---

### process.jsp

```jsp
<jsp:useBean id="student" class="Student" />

<jsp:setProperty name="student" property="*" />

<h2>Submitted Name:</h2>
<jsp:getProperty name="student" property="name" />
```

---

## 🧠 Explanation

* `property="*"` matches form fields with bean setters
* No request handling code required
* Clean and beginner-friendly

---

## 🌍 Real-World Use

* Registration forms
* Contact forms
* Survey processing

---

# MINI PROJECT 5: Page Layout Using `<jsp:include>`

---

## 🎯 Project Goal

Reuse common page components like header and footer.

---

## 📂 Files Used

* `main.jsp`
* `header.jsp`
* `footer.jsp`

---

### header.jsp

```jsp
<h2>My Website</h2>
<hr>
```

---

### footer.jsp

```jsp
<hr>
<p>© 2026 My Website</p>
```

---

### main.jsp

```jsp
<jsp:include page="header.jsp" />

<p>Welcome to the main content area.</p>

<jsp:include page="footer.jsp" />
```

---

## 🧠 Explanation

* Included files run at runtime
* Makes maintenance easy
* Reduces duplication

---

## 🌍 Real-World Use

* Website layouts
* Navigation menus
* Dashboards

---

# MINI PROJECT 6: Passing Parameters Using `<jsp:include>` + `<jsp:param>`

---

## 🎯 Project Goal

Send data while including another JSP.

---

### main.jsp

```jsp
<jsp:include page="message.jsp">
    <jsp:param name="msg" value="Hello from Main Page" />
</jsp:include>
```

---

### message.jsp

```jsp
<h3>Message received successfully.</h3>
```

---

## 🧠 Explanation

* `<jsp:param>` adds request parameters
* Included page can access them
* Useful for dynamic includes

---

## 🌍 Real-World Use

* Notifications
* Alerts
* Modular UI components

---

# MINI PROJECT 7: Static Content Using `<jsp:text>`

---

## 🎯 Project Goal

Write fixed template text safely inside JSP.

---

### textDemo.jsp

```jsp
<jsp:text>
Welcome to the JSP Action Tag Tutorial.
This content is static and safe.
</jsp:text>
```

---

## 🧠 Explanation

* Content is printed exactly as written
* Useful in XML-based JSP documents

---

## 🌍 Real-World Use

* Templates
* Static documentation
* Legal notices

---

# MINI PROJECT 8: XML Output Using `<jsp:element>` and `<jsp:body>`

---

## 🎯 Project Goal

Generate XML content dynamically.

---

### xml.jsp

```jsp
<jsp:element name="student">
    <jsp:body>
        John
    </jsp:body>
</jsp:element>
```

---

## 🧠 Explanation

* Creates `<student>John</student>`
* Useful in XML-based applications

---

## 🌍 Real-World Use

* Web services
* XML feeds
* Data interchange

---

# FINAL SUMMARY TABLE

| Mini Project         | Tags Used                        |
| -------------------- | -------------------------------- |
| Page navigation      | jsp:forward, jsp:param           |
| Bean creation        | jsp:useBean                      |
| Data storage/display | jsp:setProperty, jsp:getProperty |
| Form handling        | jsp:setProperty (*)              |
| Page layout          | jsp:include                      |
| Parameter passing    | jsp:include, jsp:param           |
| Static text          | jsp:text                         |
| XML generation       | jsp:element, jsp:body            |

---

## ✅ What This Teaches a Beginner

* JSP can work **without Java code**
* Action tags improve readability
* MVC-friendly approach
* Industry-recommended practices

---

