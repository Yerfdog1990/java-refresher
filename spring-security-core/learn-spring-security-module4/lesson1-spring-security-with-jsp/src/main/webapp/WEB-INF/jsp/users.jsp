<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Registered Students</title>
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
    <h1>Registered Students</h1>

    <c:if test="${not empty param.logout}">
        <div class="alert alert-success">You have been logged out.</div>
    </c:if>

    <div class="actions">
        <a class="btn primary" href="<c:url value='/signup'/>">Register New Student</a>
        <a class="btn" href="<c:url value='/home'/>">Back to Home</a>
    </div>

    <table>
        <thead>
        <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Date Created</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty students}">
                <tr>
                    <td colspan="4">No students registered yet.</td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:forEach items="${students}" var="s">
                    <tr>
                        <td><c:out value="${s.username}"/></td>
                        <td><c:out value="${s.email}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty s.created}">
                                    <fmt:formatDate value="${s.created.time}" pattern="MMM d, yyyy HH:mm:ss" />
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td class="actions">
                            <a class="btn" href="<c:url value='/users/${s.id}'/>">View</a>
                            <a class="btn" href="<c:url value='/users/${s.id}/edit'/>">Edit</a>
                            <c:url var="deleteUrl" value="/users/${s.id}/delete"/>
                            <form action="${deleteUrl}" method="post" style="display: inline-block;">
                                <sec:csrfInput />
                                <button type="submit" class="btn danger" onclick="return confirm('Are you sure you want to delete this user?')">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
</div>
</body>
</html>
