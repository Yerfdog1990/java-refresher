<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Registration Page</title>
    <link rel="stylesheet" href="<c:url value='/css/styles.css'/>"/>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/pwstrength-bootstrap/3.1.1/pwstrength-bootstrap.min.js"></script>
</head>
<body>
<div class="container auth-container">
    <h1>Registration page</h1>
    <div class="alert alert-info">An activation link will be sent to your email after registration.</div>
    
    <c:choose>
        <c:when test="${student.id == null}">
            <c:url var="actionUrl" value="/student/register"/>
        </c:when>
        <c:otherwise>
            <c:url var="actionUrl" value="/users/${student.id}/edit"/>
        </c:otherwise>
    </c:choose>

    <form:form id="studentForm" action="${actionUrl}" modelAttribute="student" method="post">
        <form:errors path="" element="div" cssClass="alert alert-danger" />

        <div class="form-group">
            <label for="username">Username</label>
            <form:input id="username" path="username" />
            <form:errors path="username" cssClass="field-error" element="div" />
        </div>
        <div class="form-group">
            <label for="email">Email</label>
            <form:input id="email" type="email" path="email" />
            <form:errors path="email" cssClass="field-error" element="div" />
        </div>
        <div class="form-group">
            <label for="password">Password</label>
            <form:password id="password" path="password" />
            <div class="pwstrength_viewport_progress"></div>
            <form:errors path="password" cssClass="field-error" element="div" />
        </div>
        <div class="form-group">
            <label for="passwordConfirmation">Password Confirmation</label>
            <form:password id="passwordConfirmation" path="passwordConfirmation" />
            <form:errors path="passwordConfirmation" cssClass="field-error" element="div" />
        </div>
        <div class="form-group">
            <label class="control-label col-xs-2" for="question">Security Question:</label>
            <div class="col-xs-10">
                <select id="question" name="questionId">
                    <c:forEach items="${questions}" var="question">
                        <option value="${question.id}">${question.text}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-xs-2" for="answer">Answer</label>
            <div class="col-xs-10">
                <input id="answer" type="text" name="answer"/>
            </div>
        </div>
        <div class="actions">
            <input type="submit" class="btn primary" value="${student.id == null ? 'Register' : 'Save Changes'}"/>
            <c:choose>
                <c:when test="${student.id == null}">
                    <a class="btn" href="<c:url value='/login'/>">Back to Login</a>
                </c:when>
                <c:otherwise>
                    <a class="btn" href="<c:url value='/users'/>">Back to Users</a>
                </c:otherwise>
            </c:choose>
        </div>
    </form:form>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        options = {
            common: {
                minChar: 8,
                usernameField: "#username"
            },
            ui: {
                showVerdictsInsideProgressBar: true,
                showErrors: true,
                errorMessages: {
                    wordLength: 'Your password is too short',
                },
                viewports: {
                    progress: ".pwstrength_viewport_progress"
                }
            }
        };
        $('#password').pwstrength(options);
    });
</script>
</body>
</html>
