<%-- 
    Document   : error
    Created on : Feb 27, 2016, 2:47:33 AM
    Author     : Tao Zhao
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
    
    <%@ include file="header.jsp" %>   
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>User Information Error</title>
    </head>
    
    <%
        String errorMessage = (String)request.getAttribute("errorMessage");
        errorMessage = (null == errorMessage || "".equals(errorMessage)) ? "" : errorMessage; 
     %>

    <body>
        <div class="container">
        <div class="jumbotron">
        <h4>Cannot handle your request for server information!</h4>
        <h5>Error message: <%= errorMessage %> </h5>
        <br>
        <input type="button"  class="btn btn-form" onclick="location.href='/webserv/home'" value="Back to Home">
        </div>
        </div>
    </body>
</html>
