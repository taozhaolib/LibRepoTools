<%-- 
    Document   : userJobHistory
    Created on : May 11, 2016, 8:49:26 AM
    Author     : Tao Zhao
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ include file="header.jsp" %>  
<%@include file="navTab.jsp" %>

<c:if test="${empty jobList}">
    <div class="container">
        <div class="jumbotron">
            <div class="panel panel-primary">
                <div class="panel-heading"><h4>User ${nickName} - ${email} has no jobs to display</h4></div>
            </div>
        </div>
    </div>
</c:if>

<c:if test="${not empty jobList}">
    <script type="text/javascript">
        $(document).ready(function(){
            var jobList = '<%= request.getAttribute("jobList") %>';        
            var data = $.parseJSON(jobList);//alert(data);
            $.each(data, function(){
                var trObj = new Object();

                $.each(this, function(key, value) {
                    if(key === 'jobId'){
                        trObj.jobId = value;
                    }
                    else if(key === 'repoType'){
                        trObj.repoType = value;
                    }
                    else if(key === 'startTime'){
                        trObj.startTime = value;
                    }
                    else if(key === 'endTime'){
                        trObj.endTime = value;
                    }
                    else if(key === 'jobType'){
                        trObj.jobType = value;
                    }
                    else if(key === 'userId'){
                        trObj.userId = value;
                    }
                    else if(key === 'status'){
                        trObj.status = value;
                    }
                });

                var trText = "<tr>";
                trText += "<td>" + trObj.jobId + "</td>";
                trText += "<td>" + trObj.repoType + "</td>";
                trText += "<td>" + trObj.jobType + "</td>";
                trText += "<td>" + trObj.userId + "</td>";
                trText += "<td>" + trObj.status + "</td>";
                trText += "<td>" + trObj.startTime + "</td>";
                trText += "<td>" + trObj.endTime + "</td>";
                trText += "</tr>";

                $('#jobHistoryTable tbody').append(trText);
            });
        });
        
    </script>
    <div class="container">
        <div class="jumbotron">
            <div class="panel panel-primary">
                <div class="panel-heading"><h4>The job history of user ${nickName} - ${email}</h4></div>
                    <div class="container">
                        <table id="jobHistoryTable" class="table table-striped">
                            <thead>
                              <tr>
                                <th>Job ID</th>
                                <th>Repository</th>
                                <th>Job Type</th>
                                <th>User ID</th>
                                <th>Job Status</th>
                                <th>Start Time</th>
                                <th>End Time</th>
                              </tr>
                            </thead>
                            <tbody>
                             
                            </tbody>
                          </table>
                    </div>
            </div>
        </div>
    </div>
</c:if>
