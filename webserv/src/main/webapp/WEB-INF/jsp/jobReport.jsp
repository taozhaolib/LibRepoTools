<%-- 
    Document   : jobReport
    Created on : May 11, 2016, 12:39:31 AM
    Author     : Tao Zhao
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ include file="header.jsp" %>  
<%@include file="navTab.jsp" %>

<div class="container">
        <div class="jumbotron">
            <div class="panel panel-primary">
                <div class="panel-heading"><h4>Job ${jobId} Report</h4></div>
                <div class="panel-body">
                    <c:if test="${fn:startsWith(jobType, 'ssh-import')}">
                        <h5>Job Description: Import into the ${repoType} repository @${host}: collection is ${collection}</h5>
                    </c:if>
                    <c:if test="${fn:startsWith(jobType, 'ssh-upload')}">
                        <h5>Job Description: Upload the data package into the ${repoType} repository @${host}</h5>
                    </c:if>
                    <c:if test="${fn:startsWith(jobType, 'rest-import')}">
                        <h5>Job Description: Import into the ${repoType} repository @${host}, through its REST API. 
                            <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The collection is ${collection}</h5>
                    </c:if>
                    <h5>Job Status: ${status}</h5>
                    <h5>Job Started at : ${startTime}</h5>
                    <h5>Job Ended at : ${endTime}</h5>       
                    <br>
                    <c:if test="${not empty isFinished && isFinished == 'true'}">
                        <a href="/webserv/download/report/${jobType}/${jobId}" class="btn btn-info btn-sm" role="button">Download Job Report</a>
                    </c:if>                        
                    <input type="button"  class="btn btn-form" onclick="location.href='/webserv/home'" value="Back to home page"><BR>
                </div>
            </div>
        </div>
</div>
