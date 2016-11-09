<%-- 
    Document   : sshDspaceSafImport
    Created on : Apr 19, 2016, 2:32:54 PM
    Author     : Tao Zhao
--%>

<%@ include file="header.jsp" %>   
<%@ include file="navTab.jsp" %>  

<%@ taglib uri="/WEB-INF/jsp/tld/textProcess" prefix="cg" %>

<c:set var="action" value="${action}"/>
<c:set var="action2" value="${cg:upTextFirstLetter(action)}" />
<c:if test="${empty action2}">
    <c:set var="action2" value="Import" />
</c:if>

<c:set var="repository" value="${repository}"/>
<c:set var="repository2" value="${cg:upTextFirstLetter(repository)}" />
<c:if test="${empty repository2}">
    <c:set var="repository2" value="" />
</c:if>

<div class="container">
    
    <div class="jumbotron">
        <div class="panel panel-primary">
            <div class="panel-heading">
                <span style="font-size: 16px; font-weight: 800;">${action2} the S3 Bag into ${repository2} Repository:</span>
                <span class="pull-right"><%@include file="serverSelectDropdown.jsp" %></span>
                
            </div>
            <div class="panel-body">
                <center>
                    
                    <form role="form" action="/webserv/s3/${repository}/bag/${action}" method="post" enctype="multipart/form-data">
                        <br>
                        <div class="form-group">
                            <label class="control-label col-lg-2 col-md-4 col-sm-4 text-left" for="serverName">${repository2} server name:</label>
                            <div class="col-lg-6 col-md-8 col-sm-8">
                              <input type="text" class="form-control" name="serverName">
                            </div>
                            <label class="control-label col-lg-2 col-md-4 col-sm-4 text-left" for="serverId">${repository2} server ID:</label>
                            <div class="col-lg-2 col-md-8 col-sm-8">
                              <input type="text" class="form-control" name="serverId">
                            </div>
                            <br><br><br>                                                   
                            <label class="control-label col-lg-2 col-sm-4 text-left" for="parentPid">Parent Collection PID:</label>
                            <div class="col-lg-4 col-sm-8">
                              <input type="text" class="form-control" name="parentPid" placeholder="">
                            </div>                            
                        </div>
                        <br><br>
                        <div class="form-group">
                            <label class="control-label col-lg-2 col-md-4 col-sm-4 text-left" for="bucketName">S3 Bucket Name:</label>
                            <div class="col-lg-4 col-md-8 col-sm-8">
                                <input type="text" class="form-control" name="bucketName" />
                            </div>
                            <label class="control-label col-lg-2 col-md-4 col-sm-4 text-left" for="bagName">Bag Name:</label>
                            <div class="col-lg-4 col-md-8 col-sm-8">
                                <input type="text" class="form-control" name="bagName" />
                            </div>
                        </div>
                        
                        <br/><br/><br/>
                            <input type="submit" class="btn btn-form loading-btn" value="submit">&nbsp;&nbsp;&nbsp;&nbsp;
                            <input type="button"  class="btn btn-form" onclick="location.href='/webserv/home'" value="Back to home page"><BR>
                            <%@ include file="spining.jsp" %> 
                        <br>
                        
                    </form>
                </center>
            </div>
        </div>
    </div>
</div>
 
</body>
</html>
