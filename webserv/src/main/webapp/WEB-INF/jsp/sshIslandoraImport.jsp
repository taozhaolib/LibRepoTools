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
<c:set var="jobType2" value="${cg:upTextFirstLetter(string1)}" />
<c:if test="${empty jobType2}">
    <c:set var="jobType2" value="Import" />
</c:if>

<div class="container">
    
    <div class="jumbotron">
        <div class="panel panel-primary">
            <div class="panel-heading">
                <span style="font-size: 16px; font-weight: 800;">${jobType2} the simple archive format into Islandora repository:</span>
                <span class="pull-right"><%@include file="serverSelectDropdown.jsp" %></span>
            </div>
            <div class="panel-body">
                <center>
                    
                    <form role="form" action="/webserv/ssh/islandora/book/import/job/${jobType}" method="post" enctype="multipart/form-data">
                    <c:if test="${not empty jobType}">                        
                        <c:if test="${jobType == 'ssh-import' || jobType == 'upload'}">
                            <div class="form-group">
                                <label class="control-label col-lg-2 col-sm-4 text-left" for="recipeLocal">Upload the recipe file:</label>
                                <div class="col-lg-3 col-sm-8">
                                    <input type="file" class="file-input" name="recipeLocal" />
                                </div>
                                <!--
                                <label class="control-label col-lg-3 col-sm-3 text-left" for="recipeFileUri">Remote online recipe file:</label>
                                <div class="col-lg-4 col-sm-8">
                                    <input type="text" class="form-control" name="recipeFileUri" />
                                </div>
                                -->
                            </div>
                        </c:if>
                        <c:if test="${jobType == 'import-uploaded'}">
                        </c:if>
                            <br/><br/><br/>
                    </c:if>
                        <div class="form-group">
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="serverName">Islandora server name:</label>
                            <div class="col-lg-6 col-sm-8">
                              <input type="text" class="form-control" name="serverName">
                            </div>
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="serverId">Islandora server ID:</label>
                            <div class="col-lg-2 col-sm-8">
                              <input type="text" class="form-control" name="serverId">
                            </div>
                        </div>
                        <br><br>                        
                        <div class="form-group">                            
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="parentPid">Parent Collection PID:</label>
                            <div class="col-lg-4 col-sm-8">
                              <input type="text" class="form-control" name="parentPid" placeholder="">
                            </div>                            
                        </div>
                        <br/><br/><br/>
                            <input type="submit" class="btn btn-form loading-btn" value="${jobType2}">&nbsp;&nbsp;&nbsp;&nbsp;
                            <input type="button"  class="btn btn-form" onclick="location.href='/webserv/home'" value="Back to home page"><BR>
                            <%@ include file="spining.jsp" %> 
                        <br>
                        <input type="hidden" value="${uploadFile}" name="uploadFile">
                        
                     </form>
                    
                </center>
            </div>
          </div>
    </div>
</div>

