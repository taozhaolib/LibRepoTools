<%-- 
    Document   : sshIslandoraImport
    Created on : Jun 14, 2016, 2:57:00 PM
    Author     : Tao Zhao
--%>
<%@ include file="header.jsp" %>   
<%@ include file="navTab.jsp" %>  

<!-- This block of code requires a value of publisher -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/jsp/tld/textProcess" prefix="cg" %>

<c:set var="string1" value="${jobType}"/>
<c:set var="string2" value="${repoType}"/>
<c:set var="jobType2" value="${cg:upTextFirstLetter(string1)}" />
<c:set var="repoType2" value="${cg:upTextFirstLetter(string2)}" />
<c:set var="localFile" value='<%= request.getParameter("file") %>' />
<c:set var="localFolder" value='<%= request.getParameter("folder") %>' />
<c:set var="localSafDir" value='<%= request.getParameter("safDir") %>' />

<div class="container">
    
    <div class="jumbotron">
        <div class="panel panel-primary">
            <div class="panel-heading">
                <span style="font-size: 16px; font-weight: 800;">${jobType2} into ${repoType2} repository:</span>                
            </div>
            <div class="panel-body">
                <center>
                    
                    <form role="form" action="/webserv/rest/${repoType}/${jobType}" method="post" enctype="multipart/form-data">
                    <c:if test="${not empty jobType}">                        
                        <c:if test="${jobType == 'rest-import' && repoType == 'dspace'}">
                            <div class="form-group">
                                <label class="control-label col-lg-2 col-sm-4 text-left" for="recipeLocal">Upload the zip file of SAF package:</label>
                                <div class="col-lg-3 col-sm-8">
                                    <input type="file" class="file-input" name="localFile" />
                                </div>
                                <label class="control-label col-lg-3 col-sm-3 text-left" for="recipeFileUri">Remote online zip file of SAF package:</label>
                                <div class="col-lg-4 col-sm-8">
                                    <input type="text" class="form-control" name="remoteFileUri" value="${localFile}" />
                                </div>
                            </div>
                            <c:set var="restImport" value="Import into Server"/>
                            <c:set var="userNameInput" value="dspaceUserName"/>
                            <c:set var="userPasswordInput" value="dspaceUserPw"/>
                        </c:if>
                            <br/><br/><br/><br>
                    </c:if>
                       
                        <div class="form-group">
                            <div class="container-fluid" style="padding-left: 0px">
                                <span class="pull-left"><%@include file="serverSelectDropdown.jsp" %></span>
                            </div>
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="serverName">${repoType2} server name:</label>
                            <div class="col-lg-6 col-sm-8">
                              <input type="text" class="form-control" name="serverName">
                            </div>
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="serverId">${repoType2} server ID:</label>
                            <div class="col-lg-2 col-sm-8">
                              <input type="number" class="form-control" name="serverId">
                            </div>
                        </div>
                        <br><br>
                        <!--
                        <div class="form-group">
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="rsaKey">${repoType2} User Name:</label>
                            <div class="col-lg-4 col-sm-8">
                              <input type="text" class="form-control" name="${userNameInput}" placeholder="">
                            </div>   
                            <label class="control-label col-lg-2 col-sm-2 text-left" for="password">${repoType2} User Password:</label>
                            <div class="col-lg-4 col-sm-6">          
                                <input type="password" class="form-control" name="${userPasswordInput}" placeholder="Enter password">
                            </div>                
                        </div>
                        <br/><br/>
                        -->
                        <div class="form-group">                            
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="collectionId">Collection ID:</label>
                            <div class="col-lg-4 col-sm-8">
                              <input type="text" class="form-control" name="collectionId" placeholder="">
                            </div>
                        </div>
                        <br/><br/><br/>
                            <input type="submit" class="btn btn-form loading-btn" value="${restImport}">&nbsp;&nbsp;&nbsp;&nbsp;
                            <input type="button"  class="btn btn-form" onclick="location.href='/webserv/home'" value="Back to home page"><BR>
                            <%@ include file="spining.jsp" %> 
                        <br>
                        <input type="hidden" value="${uploadFile}" name="uploadFile">
                        <input type="hidden" value="${localFile}" name="localFile">
                        <input type="hidden" value="${localFolder}" name="localFolder">
                        <input type="hidden" value="${localSafDir}" name="localSafDir">
                     </form>
                    
                </center>
            </div>
          </div>
    </div>
</div>

