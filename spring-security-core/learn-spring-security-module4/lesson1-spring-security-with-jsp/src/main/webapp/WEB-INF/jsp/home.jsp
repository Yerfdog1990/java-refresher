<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Welcome</title>
    <link rel="stylesheet" href="<c:url value='/css/styles.css'/>" />
</head>
<body>
<div class="container">
    <div style="text-align: right; margin-top: 10px;">
        <c:url var="logoutUrl" value="/logout"/>
        <form action="${logoutUrl}" method="post" style="display: inline-block;">
            <sec:csrfInput />
            <button type="submit" class="btn danger">Logout</button>
        </form>
    </div>
    <h1>Welcome to Student Registration</h1>
    <p>Please click the button below to proceed to register a new user.</p>
    <a class="btn" href="<c:url value='/signup'/>">Proceed to Register</a>
    <p> or </p>
    <a class="btn" href="<c:url value='/users'/>">View registered students</a>
</div>
</body>
</html>
