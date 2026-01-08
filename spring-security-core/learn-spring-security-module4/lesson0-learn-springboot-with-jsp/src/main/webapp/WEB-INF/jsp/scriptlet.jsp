<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Scripting</title>
</head>
<%
    int counter = 0;
%>
<%
    String user = request.getParameter("user");
%>
<body>
    <h1>Scripting Example</h1>
    <p>This page demonstrates scripting in JSP.</p>
Page count is <% out.println(++counter); %>
    <form method="POST" action="/welcome">
        <label for="user">Enter your name:</label>
        <input type="text" name="user">
        <input type="submit" value="Submit">
    </form>
</body>
</html>