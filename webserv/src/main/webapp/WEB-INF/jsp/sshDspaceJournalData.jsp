<%-- 
    Document   : remoteDspace
    Created on : Apr 18, 2016, 9:31:42 AM
    Author     : Tao Zhao
--%>
<!-- This block of code requires a value of publisher -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="container">
    
    <div class="jumbotron">
        <div class="panel panel-primary">
            <div class="panel-heading"><h4>Importing the simple archive format into DSpace repository:</h4></div>
            <div class="panel-body">
                <center>
                    
                    <c:if test="${not empty view}">
                        <form role="form" action="/webserv/ssh/dspace/saf/import" method="post" enctype="multipart/form-data">
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="saf">Upload the simple archive format:</label>
                            <div class="col-sm-10">
                                <input type="file" class="file-input" name="saf" />
                            </div>
                        </div><br/><br/><br/>
                    </c:if>
                    <c:if test="${empty view}">
                        <form role="form" action="/webserv/ssh/dspace/journal/${publisher}/import" method="post" enctype="multipart/form-data">
                    </c:if>
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="host">Dspace Host:</label>
                            <div class="col-sm-10">
                              <input type="text" class="form-control" name="host" placeholder="Domain or IP address">
                            </div>
                        </div>
                        <br/><br/><br/>
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="port">Port Number:</label>
                            <div class="col-sm-10">
                              <input type="text" class="form-control" name="port" placeholder="22">
                            </div>
                        </div>
                        <br/><br/><br/>
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="userName">Server User Name:</label>
                            <div class="col-sm-10">
                              <input type="text" class="form-control" name="userName" placeholder="">
                            </div>
                        </div>
                        <br/><br/><br/>
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="password">Password:</label>
                            <div class="col-sm-10">          
                                <input type="password" class="form-control" name="password" placeholder="Enter password">
                            </div>
                        </div>
                        <br/><br/>
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="dspaceUser">DSpace User Name:</label>
                            <div class="col-sm-10">
                              <input type="text" class="form-control" name="dspaceUser" placeholder="">
                            </div>
                        </div>
                        <br/><br/><br/>
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="dspaceDirectory">DSpace Installation Directory:</label>
                            <div class="col-sm-10">
                              <input type="text" class="form-control" name="dspaceDirectory" placeholder="">
                            </div>
                        </div>
                        <br/><br/><br/>
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="uploadDst">DSpace Upload Directory:</label>
                            <div class="col-sm-10">
                              <input type="text" class="form-control" name="uploadDst" placeholder="">
                            </div>
                        </div>
                        <br/><br/><br/>
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="collectionId">Collection Id:</label>
                            <div class="col-sm-10">
                              <input type="text" class="form-control" name="collectionId" placeholder="">
                            </div>
                        </div>
                        <br/><br/><br/>
                            <input type="submit" class="btn btn-form" value="Import">&nbsp;&nbsp;&nbsp;&nbsp;
                            <input type="button"  class="btn btn-form" onclick="location.href='/webserv/home'" value="Back to home page">
                        <br>
                        <input type="hidden" value="${uploadFile}" name="uploadFile">
                     </form>
                     <c:if test="${not empty importTime && empty failedTime}">
                        <div class="panel-heading"><h4>Import into the collection: ${collection} at the server ${host} was conducted at ${importTime}:</h4></div>
                        <div class="panel-body">
                            <a href="/webserv/ssh/dspace/download/${host}/${importTime}" class="btn btn-info btn-sm" role="button">Download</a>
                        </div>
                    </c:if>
                    <c:if test="${empty importTime && not empty failedTime}">
                        <div class="panel-heading"><h4>Import into the collection: ${collection} at the server ${host} was not completed at ${importTime}:</h4></div>
                        <div class="panel-body">
                            <a href="/webserv/ssh/dspace/download/${host}/${failedTime}" class="btn btn-info btn-sm" role="button">Download</a>
                        </div>   
                    </c:if>    
                </center>
            </div>
          </div>
    </div>
</div>
