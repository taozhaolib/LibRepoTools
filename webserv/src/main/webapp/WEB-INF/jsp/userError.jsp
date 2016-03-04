<%-- 
    Document   : error
    Created on : Feb 27, 2016, 2:47:33 AM
    Author     : Tao Zhao
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>User Information Error</title>
    </head>
    <body>
        <h1>Your user information does not match the records!</h1>
        <h2>Error message: <%= request.getParameter("errorMessage") %> </h2>
    </body>
</html>
