<%-- 
    Document   : remoteDspace
    Created on : Apr 18, 2016, 9:31:42 AM
    Author     : Tao Zhao
--%>
<!-- This block of code requires a value of publisher -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ include file="header.jsp" %>  
<%@include file="navTab.jsp" %>

<div class="container dspace-server-info server-info-container">
    
    <div class="jumbotron">
        <div class="panel panel-primary server-config-panel">            
            <div class="panel-heading">
                <span style="font-size: 20px; font-weight: 800;">Configure the repository server:</span>
                <span class="btn btn-primary pull-right new-server-btn">Create a new server</span>
                <span class="pull-right"><%@include file="serverSelectDropdown.jsp" %></span>
            </div>
            <div class="panel-body">
                <center>
                    <form role="form" action="/webserv/server/update" method="post" enctype="multipart/form-data">                    
                    <div class="form-group">
                        <label class="control-label col-lg-2 col-sm-4 text-left" for="serverName">Server Name: </label>
                        <div class="col-lg-6 col-sm-8">
                          <input type="text" class="form-control" name="serverName" placeholder="">
                        </div>
                        <label class="control-label col-lg-2 col-sm-4 text-left" for="port">Port Number:</label>
                        <div class="col-lg-2 col-sm-8">
                          <input type="text" class="form-control" name="port" placeholder="22">
                        </div>
                    </div>
                        <br><br><br>
                    <div class="form-group">
                        <label class="control-label col-lg-2 col-sm-4 text-left" for="host">Host:</label>
                        <div class="col-lg-4 col-sm-8">
                          <input type="text" class="form-control" name="host" placeholder="Domain or IP address">
                        </div>
                        <label class="control-label col-lg-2 col-sm-4 text-left" for="userName">Server User Name:</label>
                        <div class="col-lg-4 col-sm-8">
                          <input type="text" class="form-control" name="userName" placeholder="">
                        </div>
                    </div>
                    <br/><br/><br/>
                    <div class="form-group">
                        <label class="control-label col-lg-2 col-sm-4 text-left" for="rsaKey">RSA Key File:</label>
                        <div class="col-lg-4 col-sm-8">
                          <input type="text" class="form-control" name="rsaKey" placeholder="">
                        </div>   
                        <label class="control-label col-lg-2 col-sm-2 text-left" for="password">User Password:</label>
                        <div class="col-lg-4 col-sm-6">          
                            <input type="password" class="form-control" name="password" placeholder="Enter password">
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label class="control-label col-lg-2 col-sm-4 text-left" for="address">Web address:</label>
                        <div class="col-lg-4 col-sm-8">
                          <input type="text" class="form-control" name="address" placeholder="">
                        </div>   
                        
                    </div>
                    <br/><br/>
                    <div class="col-lg-6 col-sm-12">
                        <input type="button"  class="btn btn-info btn-click col-lg-6 col-sm-6" value="Click to Set up Proxy">
                    </div>
                    <br/><br/>    
                    <div class="form-group click-show-slow">
                        <label class="control-label col-lg-2 col-sm-4 text-left" for="proxyHost">Proxy Host:</label>
                        <div class="col-lg-6 col-sm-8">
                          <input type="text" class="form-control" name="proxyHost" placeholder="Domain or IP address">
                        </div>
                        <label class="control-label col-lg-2 col-sm-4 text-left" for="proxyPort">Proxy Port Number:</label>
                        <div class="col-lg-2 col-sm-8">
                          <input type="text" class="form-control" name="proxyPort" placeholder="22">
                        </div>
                        <br><br><br>
                    </div>

                    <div class="form-group click-show-slow">
                        <label class="control-label col-lg-2 col-sm-4 text-left" for="proxyUserName">Proxy Server User Name:</label>
                        <div class="col-lg-4 col-sm-8">
                          <input type="text" class="form-control" name="proxyUserName" placeholder="">
                        </div>
                        <label class="control-label col-lg-2 col-sm-4 text-left" for="proxyPassword">Proxy Password:</label>
                        <div class="col-lg-4 col-sm-8">          
                            <input type="password" class="form-control" name="proxyPassword" placeholder="Enter password">
                        </div>
                        <br><br><br>
                        <label class="control-label col-lg-2 col-sm-4 text-left" for="passPhrase">Passphrase:</label>
                        <div class="col-lg-4 col-sm-8">          
                            <input type="password" class="form-control" name="passPhrase" placeholder="Enter password">
                        </div>                        
                        <br><br>                     
                    </div>
                    <br>
                      
                    <%@include file="repoTypeDropdown.jsp" %>
                    
                    <div>
                        <br>
                        <input type="submit" class="btn btn-form loading-btn" value="Save">&nbsp;&nbsp;&nbsp;&nbsp;
                        <input type="button"  class="btn btn-form" onclick="location.href='/webserv/home'" value="Back to home page"><BR>
                        <%@ include file="spining.jsp" %> 
                        <br>
                        <input type="hidden" value="-1" name="serverId">
                        <input type="hidden" value="-1" name="repoType" id="repoType">
                    </div>

                 </form>
                    <c:if test="${not empty message}">
                        <h4><span class="label label-pill label-success">${message}</span></h4>
                    </c:if>
                </center>
            </div>
          </div>
    </div>
</div>
