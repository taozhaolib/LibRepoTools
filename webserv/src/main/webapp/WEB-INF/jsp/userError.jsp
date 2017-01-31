<%-- 
    Document   : error
    Created on : Feb 27, 2016, 2:47:33 AM
    Author     : Tao Zhao
--%>

    <%@ include file="header.jsp" %>   
    <%@ include file="navTab.jsp" %>  
    
    <%
        String errorMessage = (String)request.getAttribute("errorMessage");
        errorMessage = (null == errorMessage || "".equals(errorMessage)) ? "" : errorMessage; 
     %>

    <body>
        <div class="container">
        <div class="jumbotron">
        <h4>The user information is incorrect!</h4>
        <h5>Error message: <%= errorMessage %> </h5>
        <br>
        <input type="button"  class="btn btn-form" onclick="location.href='/webserv/home'" value="Back to Home">
        </div>
        </div>
    </body>
</html>
