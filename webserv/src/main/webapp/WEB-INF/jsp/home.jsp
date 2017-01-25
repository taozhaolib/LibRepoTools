<%-- 
    Document   : home
    Created on : Jan 28, 2016, 11:03:04 AM
    Author     : Tao Zhao
--%>

    <%@ include file="header.jsp" %>   
    <%@ include file="navTab.jsp" %>  
    
    <div class="container">
        <div class="jumbotron">
            <div class="panel panel-primary">
                <div class="panel-heading"><h4>Click the links to use LibRepoTools:</h4></div>
                <div class="panel-body">
                    <center>

                        <div >
                            
                                <a href="/webserv/dspace/journal/ouhistory" class="list-group-item list-group-item-action home-page-list">Generate SAF Package for DSpace Import</a>
                                <a href="/webserv/rest/dspace/saf/page/rest-import" class="list-group-item list-group-item-action home-page-list">Import into DSpace with Existing SAF Package</a>
                                <a href="/webserv/ssh/islandora/book/import/page/ssh-import" class="list-group-item list-group-item-action home-page-list">Import Bagit into Islandora</a>
                                <a href="/webserv/s3/islandora/bag/import/page" class="list-group-item list-group-item-action home-page-list">Import Bagit at AWS S3 Buckets into Islandora</a>
                                <a href="/webserv/user/20/jobHistory" class="list-group-item list-group-item-action home-page-list">Check Job History</a>
                            
                            
                            <!-- image source: http://imcreator.com/free creative commons license 
                                <div class="well" style="width: 50%">Data import and export</div>
                                <div class="well" style="width: 50%">Data inter-platform migration</div>
                                <div class="well" style="width: 50%">Data customization</div>
                                <div class="well" style="width: 50%">Data publication</div>
                                <img src="/webserv/resources/images/home.jpg" class="img-responsive image-fluid" alt="" > 
                            -->
                            
                        </div>
                    </center>
                </div>
              </div>
            
        </div>
    </div>
</body>
</html>
