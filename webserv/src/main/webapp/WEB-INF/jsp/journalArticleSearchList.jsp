<%-- 
    Document   : journalArticleSearchList
    Created on : Feb 14, 2017, 12:45:18 AM
    Author     : Tao Zhao
--%>

<%@ include file="header.jsp" %>   
<%@ include file="navTab.jsp" %>

<c:set var="string1" value="${publisher}"/>
<c:set var="publisherUpCase" value="${fn:toUpperCase(string1)}" />

<div class="container">
        <div class="jumbotron">
            <div class="panel panel-primary">
            <div class="panel-heading"><h4>Search articles published at ${publisherUpCase} between date range:</h4></div>
                
                <div class="panel-body">
                     
                      <!-- Form code begins -->
                    <form action="/webserv/journal/search/${publisher}/date" method="post" accept-charset="utf-8">
                        <div>
                            <script>
                                var startDate = '<%= request.getAttribute("startDate") %>';
                                var endDate = '<%= request.getAttribute("endDate") %>';
                                startDate = isEmpty(startDate) || startDate === 'null' ? '' : String(startDate);
                                endDate = isEmpty(endDate) || endDate === 'null' ? '' : String(endDate);
                            </script>
                            
                            <%@include file= "dateRangePicker.jsp" %>
                            <%@include file= "institutionsSelect.jsp" %>
                            
                            <br><br><br><br>
                            <div id="buttonsDiv" style="margin-left: 10px;">
                                <input type="submit" class="btn btn-form loading-btn" value="Search">&nbsp;&nbsp;&nbsp;&nbsp;                                  
                                <c:if test="${not empty articles}">                                                                       
                                    <input type="button"  class="btn btn-form loading-btn" name="btnSaveArticlesSearch" value="Download Search Results">&nbsp;&nbsp;&nbsp;&nbsp;
                                    <a id="download_hidden_link" href="#" target="_blank" style="display:none;">download</a>
                                    <script>
                                        $(document).ready(function(){
                                            var dois = "";
                                            var articles = <%= request.getAttribute("articles") %>;
                                            var articlesStr = '<%= request.getAttribute("articles") %>';
                                            var articleData = "articlesStr=" + articlesStr + "&&startDate=" + startDate + "&&endDate=" + endDate;
                                            $.each(articles, function(index, value){
                                                dois += value.doi.trim() + ";";
                                            });
                                            $("input[name='btnSearchArticlesSaf']").click(function(){
                                                $(".spining-class").show();
                                                $.ajax({
                                                    method: "POST",
                                                    url: "/webserv/dspace/journal/${publisher}/saf",
                                                    data: { articleData },
                                                    beforeSend: function(xhr){
                                                        $(".loading-btn").attr("disabled", true);
                                                    },
                                                    success: function(data) {  
                                                        var dataArr = jQuery.parseJSON(data);
                                                        var paths = new Array(dataArr.length);
                                                        $.each(dataArr, function(index, value){
                                                            paths[index] = value.split("/uploads/")[1];
                                                        });
                                                        getDownloadLinksVue(paths);                                                     
                                                        $(".spining-class").hide();
                                                        $(".loading-btn").attr("disabled", false);
                                                    }
                                                })
                                            });
                                            $("input[name='btnSaveArticlesSearch']").click(function(){
                                                $(".spining-class").show();
                                                $.ajax({
                                                    async:  false,
                                                    method: "POST",
                                                    url: "/webserv/download/dspace/journal/${publisher}/search",
                                                    data: { articleData },
                                                    beforeSend: function(xhr){
                                                        $(".loading-btn").attr("disabled", true);
                                                    },
                                                    success: function(data) {  
//                                                        alert(data); 
                                                        var hiddenLink = $("#download_hidden_link");
                                                        hiddenLink.attr("href", data + "/");
//                                                        hiddenLink.show();
                                                        document.getElementById('download_hidden_link').click();
                                                        $(".spining-class").hide();
                                                        $(".loading-btn").attr("disabled", false);
                                                    }
                                                })
                                            });
                                        });
                                    </script>
                                    <input type="button"  class="btn btn-form loading-btn" name="btnSearchArticlesSaf" value="Generate SAF Package">&nbsp;&nbsp;&nbsp;&nbsp;
                                </c:if>
                                <input type="button"  class="btn btn-form" onclick="location.href='/webserv/home'" value="Back to home page"><BR>                                
                                <%@ include file="spining.jsp" %> 
                            </div>
                        <br>
                        </div>
                            
                        <%@include file="downloadLinks.jsp" %>
                        
                    </form>
                    
                    <%@include file= "journalArticleList.jsp" %>
                    
                </div>                        
            </div>
        </div>
</div>

</body>
</html>