<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login Page</title>
    <link rel="stylesheet" href="<c:url value='/css/styles.css'/>" />
</head>
<body>
<div class="container auth-container">
    <h1>Login page</h1>

    <c:if test="${not empty param.error}">
        <div class="alert alert-danger">Invalid username and password.</div>
    </c:if>
    <c:if test="${not empty param.logout}">
        <div class="alert alert-success">You have been logged out.</div>
    </c:if>
    <c:if test="${not empty message}">
        <div class="alert alert-success">${message}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">${errorMessage}</div>
    </c:if>

    <c:url var="loginUrl" value="/doLogin"/>
    <form action="${loginUrl}" method="post">
        <sec:csrfInput />
        <div class="form-group">
            <label for="username">Email:</label>
            <input id="username" type="text" name="username" />
        </div>
        <div class="form-group">
            <label for="password">Password:</label>
            <input id="password" type="password" name="password" />
        </div>
        <div class="form-group">
            <label class="control-label col-xs-2" for="remember">Remember me?</label>
            <input id="remember" type="checkbox" name="remember-me" value="true" />
        </div>
        <div class="actions">
            <input type="submit" class="btn primary" value="Sign In" />
            <a class="btn" href="<c:url value='/signup'/>">Sign Up</a>
            <br>
            <a href="<c:url value='/forgotPassword'/>">Reset Password</a>
        </div>
    </form>
</div>
</body>
</html>
