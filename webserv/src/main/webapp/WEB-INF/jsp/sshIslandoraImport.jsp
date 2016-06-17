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
            <div class="panel-heading"><h4>${jobType2} the package into Islandora repository:</h4></div>
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
                                <label class="control-label col-lg-3 col-sm-3 text-left" for="recipeFileUri">Remote online recipe file:</label>
                                <div class="col-lg-4 col-sm-8">
                                    <input type="text" class="form-control" name="recipeFileUri" />
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${jobType == 'import-uploaded'}">
                        </c:if>
                            <br/><br/><br/><br>
                    </c:if>
         
                        <div class="form-group">
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="host">Islandora Host:</label>
                            <div class="col-lg-6 col-sm-8">
                              <input type="text" class="form-control" name="host" placeholder="Domain or IP address">
                            </div>
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="port">Port Number:</label>
                            <div class="col-lg-2 col-sm-8">
                              <input type="text" class="form-control" name="port" placeholder="22">
                            </div>
                        </div>
                        <br/><br/><br/>
                        <div class="form-group">
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="userName">Server User Name:</label>
                            <div class="col-lg-4 col-sm-8">
                              <input type="text" class="form-control" name="userName" placeholder="">
                            </div>
                            <label class="control-label col-lg-2 col-sm-2 text-left" for="password">Password:</label>
                            <div class="col-lg-4 col-sm-6">          
                                <input type="password" class="form-control" name="password" placeholder="Enter password">
                            </div>
                        </div>
                        <br/><br/><br/>
                        <div class="form-group">
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="rsaKey">RSA Key File:</label>
                            <div class="col-lg-7 col-sm-8">
                              <input type="text" class="form-control" name="rsaKey" placeholder="">
                            </div>                            
                            <input type="button"  class="btn btn-click col-lg-3" value="Click to Set up Proxy">
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
                            <br><br><br>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="drupalDirectory">Islandora Drupal Directory:</label>
                            <div class="col-lg-4 col-sm-8">
                              <input type="text" class="form-control" name="drupalDirectory" placeholder="">
                            </div>
                            <div class="form-group">
                                <label class="control-label col-lg-2 col-sm-4 text-left" for="uploadDst">Islandora Upload Directory:</label>
                                <div class="col-lg-4 col-sm-8">
                                  <input type="text" class="form-control" name="uploadDst" placeholder="">
                                </div>
                            </div>
                        </div>
                        <br/><br/><br/><br>
                        <div class="form-group">                            
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="parentPid">Parent Collection PID:</label>
                            <div class="col-lg-4 col-sm-8">
                              <input type="text" class="form-control" name="parentPid" placeholder="">
                            </div>
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="tmpPath">Directory for Temporary Files</label>
                            <div class="col-lg-4 col-sm-8">
                              <input type="text" class="form-control" name="tmpPath" placeholder="">
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

