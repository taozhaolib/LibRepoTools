<%-- 
    Document   : plosDataUpload
    Created on : Jan 25, 2016, 12:32:47 AM
    Author     : Tao Zhao
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html>
<html>
<head>
<title>Uploading the file for SAGE publications</title>

</head>
<body>
    <%@ include file="mainPageIncludes.jsp" %>     
    
    <div class="container">
        <div class="jumbotron">
            <div class="panel panel-primary">
                <div class="panel-heading"><h4>Process the SAGE Publications:</h4></div>
                <div class="panel-body">
                    <h3>Select a file to upload:</h3>
                        
                        <form action="/webserv/sage/upload" method="post"
                                                enctype="multipart/form-data">
                        <input type="file" name="file" />
                        <br />
                        <!-- <button type="button" class="btn btn-primary btn-md">Upload File</button> -->
                        <input type="submit" class="btn btn-info btn-primary" value="Upload File">
                        </form>
                    <BR>
                    <BR>
                    <div class="download-link-div">
                        <h4>Download the original uploaded file and the loading files:</h4>
                        <a href="${oldFile}" class="btn btn-info btn-sm" role="button">Uploaded File</a>
                        &nbsp;&nbsp;
                        <a href="${loadingFile}" class="btn btn-info btn-sm" role="button">Loading Files</a>
                        <BR><BR>
                    </div>
                </div>
            </div>
            
        </div>
    </div>
</body>
</html>
