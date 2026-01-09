<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Account Activated</title>
    <link rel="stylesheet" href="<c:url value='/css/styles.css'/>" />
</head>
<body>
<div class="container auth-container">
    <h1>Account Activated</h1>
    <div class="alert alert-success">
        Account activated successfully! You can now login into your account.
    </div>
    <div class="actions">
        <a class="btn primary" href="<c:url value='/login'/>">Login</a>
    </div>
</div>
</body>
</html>
