<%-- 
    Document   : remoteDspace
    Created on : Apr 18, 2016, 9:31:42 AM
    Author     : Tao Zhao
--%>
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
            <div class="panel-heading"><h4>${jobType2} the simple archive format into DSpace repository:</h4></div>
            <div class="panel-body">
                <center>
                    
                    <c:if test="${not empty jobType}">
                        <form role="form" action="/webserv/ssh/dspace/saf/job/${jobType}" method="post" enctype="multipart/form-data">
                        <c:if test="${jobType == 'import' || jobType == 'upload'}">
                            <div class="form-group">
                                <label class="control-label col-lg-2 col-sm-4 text-left" for="saf">Upload the simple archive format:</label>
                                <div class="col-lg-3 col-sm-8">
                                    <input type="file" class="file-input" name="saf" />
                                </div>
                                <label class="control-label col-lg-3 col-sm-3 text-left" for="saf-online">Remote online simple archive format package (ZIP file):</label>
                                <div class="col-lg-4 col-sm-8">
                                    <input type="text" class="form-control" name="saf-online" />
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${jobType == 'import-uploaded'}">
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="saf-online">Path to the Uploaded Package on the Server:</label>
                                <div class="col-lg-4 col-sm-8">
                                    <input type="text" class="file-input" name="saf-online" />
                                </div>
                            <div class="form-group">
                                <label class="control-label col-lg-2 col-sm-4 text-left" for="old-jobId">ID of the Uploading Job:</label>
                                <div class="col-lg-4 col-sm-8">
                                  <input type="text" class="form-control" name="old-jobId">
                                </div>
                            </div>
                        </c:if>
                            <br/><br/><br/><br>
                    </c:if>
                    <c:if test="${empty jobType}">
                        <form role="form" action="/webserv/ssh/dspace/journal/${publisher}/import" method="post" enctype="multipart/form-data">
                    </c:if>
         
                        <div class="form-group">
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="host">Dspace Host:</label>
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
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="dspaceDirectory">DSpace Installation Directory:</label>
                            <div class="col-lg-4 col-sm-8">
                              <input type="text" class="form-control" name="dspaceDirectory" placeholder="">
                            </div>
                            <div class="form-group">
                                <label class="control-label col-lg-2 col-sm-4 text-left" for="uploadDst">DSpace Upload Directory:</label>
                                <div class="col-lg-4 col-sm-8">
                                  <input type="text" class="form-control" name="uploadDst" placeholder="">
                                </div>
                            </div>
                        </div>
                        <br/><br/><br/><br>
                        <div class="form-group">
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="dspaceUser">DSpace User Name:</label>
                            <div class="col-lg-4 col-sm-8">
                              <input type="text" class="form-control" name="dspaceUser" placeholder="">
                            </div>
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="collectionId">Collection Id:</label>
                            <div class="col-lg-4 col-sm-8">
                              <input type="text" class="form-control" name="collectionId" placeholder="">
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
