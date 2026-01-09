<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Reset Password</title>
    <link rel="stylesheet" href="<c:url value='/css/styles.css'/>" />
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/pwstrength-bootstrap/3.1.1/pwstrength-bootstrap.min.js"></script>
</head>
<body>
<div class="container auth-container">
    <h1>Reset Password</h1>
    <br />

    <c:url var="savePasswordUrl" value="/user/savePassword"/>
    <form action="${savePasswordUrl}" method="post" class="form-horizontal">
        <sec:csrfInput />
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>
        <div class="form-group">
            <label class="control-label col-xs-2" for="question">Security Question:</label>
            <div class="col-xs-10">
                <select id="question" name="questionId" class="form-control" required>
                    <option value="">Select a security question</option>
                    <c:forEach items="${questions}" var="question">
                        <option value="${question.id}">${question.text}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-xs-2" for="answer">Answer</label>
            <div class="col-xs-10">
                <input id="answer" type="text" name="answer" class="form-control" required/>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-xs-2" for="password">New Password</label>
            <div class="col-xs-10">
                <input id="password" type="password" name="password" class="form-control" required/>
                <div class="pwstrength_viewport_progress"></div>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-xs-2" for="passwordConfirmation">Confirm Password</label>
            <div class="col-xs-10">
                <input id="passwordConfirmation" type="password" name="passwordConfirmation" class="form-control" required/>
            </div>
        </div>
        <div class="form-group">
            <div class="col-xs-offset-2 col-xs-10">
                <button type="submit" class="btn btn-primary">Reset Password</button>
                <a href="<c:url value='/login'/>" class="btn btn-default">Cancel</a>
            </div>
        </div>
        <input type="hidden" name="token" value="<c:out value='${token}'/>" />
    </form>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        options = {
            common: {
                minChar: 8
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
