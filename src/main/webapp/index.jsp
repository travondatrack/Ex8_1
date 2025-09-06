<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Root Index</title>
  </head>
  <body>
    <h1>Welcome to the Application</h1>
    <p>If you can see this page, the application is running correctly.</p>
    <p>Context Path: <%= request.getContextPath() %></p>
    <p>Try accessing:</p>
    <ul>
      <li><a href="index.html">Main Form (index.html)</a></li>
      <li><a href="test.jsp">Test Page (test.jsp)</a></li>
      <li><a href="emailList">Email List Servlet (direct)</a></li>
    </ul>
  </body>
</html>
