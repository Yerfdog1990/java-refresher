<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Handle the redirect first
    if (request.getParameter("redirect") != null) {
        response.sendRedirect("declaration");
        return;
    }

    // Get username from request parameter
    String username = request.getParameter("username");
%>
<html>
<head>
    <title>Home Page</title>
</head>
<body>
<h1>Welcome to the Home Page</h1>

<%-- Display welcome message if username exists --%>
<% if (username != null && !username.trim().isEmpty()) { %>
<div style="color: blue; font-size: 1.2em; margin: 20px 0;">
    <%
        out.print("Welcome, " + username + "!");
    %>
</div>
<% } %>

<p>Click the button to see the response redirect in action:</p>
<a href="?redirect=true">Go to Declaration tag page</a>

<div style="margin-top: 20px; padding: 10px; border: 1px solid #ccc; width: 300px;">
    <h3>Enter your name:</h3>
    <form method="get" action="">
        <label for="username">
            <input type="text" name="username" placeholder="Your name">
        </label>

        <label for="submit">
            <input type="submit" value="Submit">
        </label>
    </form>
</div>
<%
    String result = ""; // Initialize result as empty string

    // Check if form was submitted
    if (request.getParameter("numerator") != null && request.getParameter("denominator") != null) {
        try {
            int numerator = Integer.parseInt(request.getParameter("numerator"));
            int denominator = Integer.parseInt(request.getParameter("denominator"));
            double divisionResult = (double) numerator / denominator;
            result = String.format("%.2f", divisionResult); // Format to 2 decimal places
        } catch (NumberFormatException e) {
            result = "Error: Please enter valid numbers";
        } catch (ArithmeticException e) {
            result = "Error: Cannot divide by zero!";
        }
    }
%>

<div style="margin-top: 20px; padding: 10px; border: 1px solid #ccc; width: 300px;">
    <form method="post" action="">
        <label for="numerator">
            <input type="number" name="numerator" placeholder="Enter the numerator" required>
        </label>
        <br>
        <label for="denominator">
            <input type="number" name="denominator" placeholder="Enter the denominator" required>
        </label>
        <br><br>
        <input type="submit" value="Calculate">
        <br><br>
        <label>Result: <%= result %></label>
    </form>
</div>
</body>
</html>