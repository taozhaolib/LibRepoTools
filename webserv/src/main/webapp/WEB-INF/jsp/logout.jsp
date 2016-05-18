<%-- 
    Document   : logout
    Created on : Mar 1, 2016, 1:04:26 AM
    Author     : Tao Zhao
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>You are logged out</title>
    </head>
    <body>
        <%@ include file="header.jsp" %>
        
        <div class="container">
            <div class="jumbotron">
                <div class="panel panel-primary">
                    <div class="panel-heading"><h4>Please Login/Register to use ShareOK Data Services:</h4></div>
                    <div class="panel-body">
                        <center>
                            <h3>Your time has ended. Please log back in to use ShaoreOK Data service:</h3>
                            <a href="/webserv/login">log in</a>
                        </center>
                    </div>
                  </div>

            </div>
        </div>
        
    </body>
</html>
