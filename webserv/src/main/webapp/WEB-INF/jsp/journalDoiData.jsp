<%-- 
    Document   : journalDoiUpload
    Created on : Jan 20, 2017, 10:19:45 AM
    Author     : Tao Zhao
--%>

<%@ include file="header.jsp" %>   
    <%@ include file="navTab.jsp" %>     
    
    <c:set var="string1" value="${publisher}"/>
    <c:set var="publisherUpCase" value="${fn:toUpperCase(string1)}" />
    
    <div class="container">
        <div class="jumbotron">
            <div class="panel panel-primary">
                <div class="panel-heading"><h4>Generate the SAF package for the ${publisherUpCase} Data by DOIs:</h4></div>
                <div class="panel-body">
                        
                    <form action="/webserv/dspace/safpackage/doi/generate" method="post" enctype="multipart/form-data">
                        <div class="form-group">
                            <h4 style="margin-left: 10px">Process Single DOI:</h4>
                            <label class="control-label col-lg-3 col-md-4 col-sm-4 text-left" for="doiInput">Type in a single DOI:</label>                            
                            <div class="col-lg-4 col-md-4 col-sm-4">
                                <input type="text" class="form-control" name="doiInput" />
                            </div>
                            <div class="col-lg-5 col-md-4 col-sm-4">
                                <input type="submit" name="singleDoi" class="btn btn-info btn-primary loading-btn" value="Generate">
                            </div>
                        </div>
                        <br><br>
                        
                        <br>
                        <div class="form-group">
                            <h4 style="margin-left: 10px">Process multiple DOIs:</h4>
                            <label class="control-label col-lg-4 col-md-6 col-sm-6 text-left" for="multiDoiInput">Paste the DOIs separated by semicolon: </label>                            
                            <div class="col-lg-4 col-md-6 col-sm-6">
                                <input type="text" class="form-control" name="multiDoiInput" />
                            </div>
                            <div class="col-lg-4">
                                <input type="submit" name="multiDoi" class="btn btn-info btn-primary loading-btn" value="Generate">
                            </div>
                            <br>                                
                            
                        </div>
                        <br><br>
                        <div class="form-group">
                            <h4 style="margin-left: 10px">Process multiple DOIs in a txt file:</h4>
                            <label class="control-label col-lg-4 col-md-7 col-sm-7 text-left" for="saf">Upload the txt file containging the DOIs:</label>                                                        
                            <div class="col-lg-4 col-md-5 col-sm-5" >
                                <input type="file" name="multiDoiUploadFile" />
                            </div>
                            <div class="col-lg-4">
                                <input type="submit" name="fileDoi" class="btn btn-info btn-primary loading-btn" value="Generate">
                            </div>
                        </div>
                    <br><br><br>
                    <div style="text-align:center;">                    
                    <%@ include file="spining.jsp" %>
                    </div>
                    </form>
                    <BR>
                    <BR>
                    
                </div>
            </div>
            
        </div>
    </div>

</body>
</html>
