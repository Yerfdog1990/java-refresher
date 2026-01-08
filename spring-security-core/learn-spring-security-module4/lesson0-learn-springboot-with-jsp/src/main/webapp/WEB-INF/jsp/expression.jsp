<%@ page import="java.util.Date" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<html>
<head>
    <title>Expression Tag Demo</title>
</head>
<body>
<h1>JSP Expression Tag Demo</h1>

<!-- Simple expression -->
<p>Current time: <%= new Date() %></p>

<!-- Expression with variables -->
<%
    int a = 10, b = 20;
    String name = "JSP";
%>
<p>Sum of <%= a %> and <%= b %> is: <%= a + b %></p>
<p>Welcome to <%= name %>!</p>

<!-- Expression with method call -->
<p>Lowercase name: <%= name.toLowerCase() %></p>

<!-- Expression with arithmetic -->
<p>Random number: <%= Math.random() %></p>
</body>
</html>