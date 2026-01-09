<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>User Profile</h2>

<p>Username:
    <sec:authentication property="principal.username"/>
</p>

<p>Authorities:
    <sec:authentication property="authorities"/>
</p>

<sec:authentication
        property="principal.username"
        var="currentUserName"/>

<c:if test="${currentUserName == 'user'}">
    <div>Welcome, standard user!</div>
</c:if>
