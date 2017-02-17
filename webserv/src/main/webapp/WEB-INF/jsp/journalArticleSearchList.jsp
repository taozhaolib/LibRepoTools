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
            <div class="panel-heading"><h4>Search articles published at ${publisherUpCase} between data range:</h4></div>
                
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
                            <div style="margin-left: 10px;">
                                <input type="submit" class="btn btn-form loading-btn" value="Search">&nbsp;&nbsp;&nbsp;&nbsp;
                                <input type="button"  class="btn btn-form" onclick="location.href='/webserv/home'" value="Back to home page"><BR>                                
                                <%@ include file="spining.jsp" %> 
                            </div>
                        <br>
                        </div>
                    </form>
                    
                    <%@include file= "journalArticleList.jsp" %>
                    
                </div>                        
            </div>
        </div>
</div>

</body>
</html>