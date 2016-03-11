<%-- 
    Document   : plosDataUpload
    Created on : Jan 25, 2016, 12:32:47 AM
    Author     : Tao Zhao
--%>

    <%@ include file="header.jsp" %>   
    <%@ include file="navTab.jsp" %>     
    
    <c:set var="string1" value="${publisher}"/>
    <c:set var="publisherUpCase" value="${fn:toUpperCase(string1)}" />
    
    <div class="container">
        <div class="jumbotron">
            <div class="panel panel-primary">
                <div class="panel-heading"><h4>Process the ${publisherUpCase} Publications:</h4></div>
                <div class="panel-body">
                    <h3>Select a file to upload:</h3>
                        
                        <form action="/webserv/dspace/journal/${publisher}/upload" method="post"
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
                    <BR>
                    <div class="download-link-div">
                         <h4>Sample dublin_core metadata file can be downloaded here:</h4>
                         <a href="${sampleDublinCore}" class="btn btn-info btn-sm" role="button">Sample Dublin Core File</a>
                    </div>
                    <BR>
                </div>
            </div>
            
        </div>
    </div>
</body>
</html>
