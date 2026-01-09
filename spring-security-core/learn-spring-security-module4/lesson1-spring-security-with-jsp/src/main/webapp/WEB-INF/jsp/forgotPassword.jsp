<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Forgot Password</title>
    <link rel="stylesheet" href="<c:url value='/css/styles.css'/>" />
</head>
<body>
<div class="container auth-container">
    <h1>Forgot Password</h1>
    <c:if test="${not empty message}">
        <div class="alert alert-success">${message}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">${errorMessage}</div>
    </c:if>

    <c:url var="resetPasswordUrl" value="/student/resetPassword"/>
    <form action="${resetPasswordUrl}" method="post">
        <sec:csrfInput />
        <div class="form-group">
            <label for="email">Email:</label>
            <input id="email" type="email" name="email" required />
        </div>
        <div class="actions">
            <input type="submit" class="btn primary" value="Reset Password" />
            <a class="btn" href="<c:url value='/login'/>">Back to Login</a>
        </div>
    </form>
</div>
</body>
</html>
