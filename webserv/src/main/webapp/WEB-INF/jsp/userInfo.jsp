<%-- 
    Document   : userInfo
    Created on : Mar 11, 2016, 4:58:44 PM
    Author     : Tao Zhao
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
   String welcome = "";
   String logText = "";
   String logTextHref = "";
   String registerText = "";
   String registerTextHref = "";
   if(null != session){
       String email = (String)session.getAttribute("email");
       if(null != email && !email.equals("")){
           String userName = (String)session.getAttribute("userName");
           String userId = String.valueOf(session.getAttribute("userId"));
           welcome = "Welcome, "+userName;
           logText = "Log out";
           logTextHref = "/webserv/logout";
       }
       else{
            welcome = "";
            logText = "Log in";
            logTextHref = "/webserv/login";
            registerText = "Register";
            registerTextHref = "/webserv/newUser";
       }
   }
%>

<div id="userIndoDiv" class="userInfo">
    <% out.print(welcome); %>
    <a id="logLink" class="logLinkClass" href="<% out.print(logTextHref); %>" > &nbsp; <% out.print(logText); %></a>
    <% if(logText.equals("Log in")) { %>
        &nbsp;&nbsp;&nbsp;<span>or</span>&nbsp;&nbsp;&nbsp;
        <a id="registerLink" class="logLinkClass" href="/webserv/newUser" >Register</a>
    <% } else {%>
    <c:set var="loggedin" value="${userId}"/>
    <c:set var="userRole" value="${userRole}"/>
    <% } %>
</div>
