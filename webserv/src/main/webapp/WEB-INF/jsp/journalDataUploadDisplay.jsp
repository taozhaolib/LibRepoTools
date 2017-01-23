<%-- 
    Document   : plosDataUpload
    Created on : Jan 25, 2016, 12:32:47 AM
    Author     : Tao Zhao
--%>

    <%@ include file="header.jsp" %>   
    <%@ include file="navTab.jsp" %>     
    
    <c:set var="uploadStr1" value="Upload a CSV or Excel file to generate the SAF package:"/>
    <c:set var="uploadStr2" value="Upload the zip file to generate the SAF package:"/>
    
    <c:if test="${not empty safPackages}">
        <script type="text/javascript">
            $(document).ready(function(){
                var safPackages = '<%= request.getAttribute("safPackages") %>';        
                var data = $.parseJSON(safPackages);
                var safListDiv = $("#safListDiv");
                $.each(data, function(key, value){    
                    var fileName = "";
                    var safDir = "";
                    var folder = "";
                    var fileInfo = value.split("/");
                    if(fileInfo.length > 0){
                        var length = fileInfo.length;
                        if(fileInfo[length-1] === ""){
                            length--;
                        }
                        fileName = fileInfo[length-1];
                        safDir = length>1 ? fileInfo[length-2] : "";
                        folder = length>2 ? fileInfo[length-3] : "";
                    }
                    else{
                        fileName = value;
                    }
//                    alert(fileName);
                    var item = "<span style=\"margin-left:20px; font-s=ize: 1.2em; font-weight: bold; \">"+fileName+"</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+"<a href=\""+value+"\" class=\"btn btn-info btn-sm\" role=\"button\">Download</a>&nbsp;"+
                                "&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"\/webserv\/rest\/dspace\/saf\/page\/rest-import?file="+fileName+"&safDir="
                                +safDir+"&folder="+folder+"\" class=\"btn btn-info btn-sm\" role=\"button\" target=\"_blank\">Import into Server</a><br><br>";
                    safListDiv.append(item);
                });
            });

        </script>
    </c:if>
    
    <div class="container">
        <div class="jumbotron">
            <div class="panel panel-primary">
                <div class="panel-heading"><h4>List of the Generated SAF packages</h4></div>
                <div class="panel-body">                    
                    <br>
                    <div class="download-link-div">                        
                        <div id="safListDiv">
                           
                        </div>     
                        <BR><BR>
                    </div>
                    <BR>
                </div>
            </div>
            
        </div>
    </div>
</body>
</html>
