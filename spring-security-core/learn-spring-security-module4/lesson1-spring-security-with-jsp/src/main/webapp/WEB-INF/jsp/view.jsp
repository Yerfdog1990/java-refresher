<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>View Student</title>
    <link rel="stylesheet" href="<c:url value='/css/styles.css'/>" />
</head>
<body>
<div class="container">
    <div style="text-align: right;">
        <c:url var="logoutUrl" value="/logout"/>
        <form action="${logoutUrl}" method="post" style="display: inline-block;">
            <sec:csrfInput />
            <button type="submit" class="btn danger">Logout</button>
        </form>
    </div>
    <c:if test="${not empty message}">
        <div class="alert alert-success">${message}</div>
        <div class="actions">
            <a class="btn" href="<c:url value='/users'/>">View registered students</a>
        </div>
    </c:if>

    <h1>Student Details</h1>
    <div class="detail">
        <p><strong>Name:</strong> <span><c:out value="${student.username}"/></span></p>
        <p><strong>Email:</strong> <span><c:out value="${student.email}"/></span></p>
        <p><strong>Date Created:</strong>
            <span>
                <c:choose>
                    <c:when test="${not empty student.created}">
                        <fmt:formatDate value="${student.created.time}" pattern="yyyy-MM-dd HH:mm" />
                    </c:when>
                    <c:otherwise>-</c:otherwise>
                </c:choose>
            </span>
        </p>
    </div>

    <div class="actions">
        <a class="btn primary" href="<c:url value='/users/${student.id}/edit'/>">Modify</a>
        <c:url var="deleteUrl" value="/users/${student.id}/delete"/>
        <form action="${deleteUrl}" method="post" style="display: inline-block;">
            <sec:csrfInput />
            <button type="submit" class="btn danger" onclick="return confirm('Are you sure you want to delete this user?')">Delete</button>
        </form>
        <a class="btn" href="<c:url value='/home'/>">Back to Home</a>
    </div>
</div>
</body>
</html>
