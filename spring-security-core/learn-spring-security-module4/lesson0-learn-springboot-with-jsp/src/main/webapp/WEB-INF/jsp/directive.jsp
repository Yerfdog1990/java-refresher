<%@ page language="java" contentType="text/html; ISO-8859-1" pageEncoding="UTF-8" %>
<%@ page import="java.util.Date"%>
<%
if(request.getParameter("redirect") != null){
    response.sendRedirect("expression");
    return;
}
%>
<html>
    <head>
        <title>Directive Page</title>
    </head>
    <body>
        <h1>Welcome to the Directive Page</h1>
        <p>Current Date: <%= new Date() %></p>
    <p>Go to the expression tag page</p>
    <a href="?redirect=true">Expression tag</a>
    </body>
</html>