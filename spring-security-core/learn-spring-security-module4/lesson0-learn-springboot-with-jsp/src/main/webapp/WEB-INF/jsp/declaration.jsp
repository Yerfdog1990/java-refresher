<%@ page contentType="text/html; charset=UTF-8" language="java" %>

<html>
<head>
    <title>My First JSP Page</title>
</head>
<%
if(request.getParameter("redirect") != null){
    response.sendRedirect("directive");
    return;
}
%>
<%!
    int count = 0;
    int getCount() {
        System.out.println( "In getCount() method" );
        return count;
    }
%>
<body>
Page Count is:
<% out.println(getCount()); %>
<p>Click the button to see the response redirect in action:</p>
<a href="?redirect=true">Go to the Directive tag page</a>
</body>
</html>
