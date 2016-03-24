<%-- 
    Document   : userInfo
    Created on : Mar 11, 2016, 4:58:44 PM
    Author     : Tao Zhao
--%>

<%
   String welcome = "";
   String logText = "";
   String logTextHref = "";
   if(null != session){
       String email = (String)session.getAttribute("email");
       if(null != email && !email.equals("")){
           String userName = (String)session.getAttribute("userName");
           welcome = "Welcome, "+userName;
           logText = "Log out";
           logTextHref = "/webserv/logout";
       }
       else{
           welcome = "Welcome";
           logText = "Log in";
           logTextHref = "/webserv/login";
       }
   }
%>

<div id="userIndoDiv" class="userInfo">
    <% out.print(welcome); %>
    <a id="logLink" class="logLinkClass" href="<% out.print(logTextHref); %>" > &nbsp; <% out.print(logText); %></a>
    <% if(logText.equals("/webserv/login")) { %>
        <a id="registerLink" class="logLinkClass" href="/webserv/register" >New User Register</a>
    <% } %>
</div>
