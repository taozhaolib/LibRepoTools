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
<title>Uploading the file for PLOS publications</title>

    
    
</head>
<body>
    <%@ include file="header.jsp" %>    
    <div class="container">
        <div class="jumbotron">
            <div class="panel panel-primary">
                <div class="panel-heading"><h4>Process the PLOS Publications:</h4></div>
                <div class="panel-body">
                    <h3>Select a file to upload:</h3>
                        
                        <form action="plosWeb/upload" method="post"
                                                enctype="multipart/form-data">
                        <input type="file" name="file" />
                        <br />
                        <button type="button" class="btn btn-primary btn-md">Upload File</button>
                        </form>
                </div>
              </div>
            
        </div>
    </div>
</body>
</html>
