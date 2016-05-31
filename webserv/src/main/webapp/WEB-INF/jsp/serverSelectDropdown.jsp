<%-- 
    Document   : serverSelectDropdown
    Created on : May 21, 2016, 6:54:15 PM
    Author     : Tao Zhao
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${not empty emptyServerList}">
    <div class="container">
        <div class="jumbotron">
            <div class="panel panel-primary">
                <div class="panel-heading"><h4>${emptyServerList}</h4></div>
            </div>
        </div>
    </div>
</c:if>

<c:if test="${not empty serverList}">
    <script type="text/javascript">
        $(document).ready(function(){
            var serverList = '<%= request.getAttribute("serverList") %>';        
            var data = $.parseJSON(serverList);
            $.each(data, function(key, value){                
                //alert("value = "+value + " and key = " + key);
                var li = "<li role=\"presentation\"><a id=\"server-" + value + "\" role=\"menuitem\" tabindex=\"-1\" class=\"server-link\" href=\"javascript:void(0);\" >" + key + "</a></li>";
                $("#server-ui").append(li);
            });
            
            var serverObjList = '<%= request.getAttribute("serverObjList") %>';
            var objData = $.parseJSON(serverObjList);alert(serverObjList);
            
            $(".server-link").click(function(){
                var id = $(this).attr("id").split("server-")[1];
                var serverList = '<%= request.getAttribute("'"+id+"'") %>';
            });
        });

    </script>
</c:if>            
<div class="server-config-dropdown">
    
    <div class="dropdown col-lr-9 col-sm-9">
      <button class="btn btn-info dropdown-toggle" id="menu1" type="button" data-toggle="dropdown">Select a server from the list:
      <span class="caret"></span></button>
      <ul id="server-ui" class="dropdown-menu" role="menu" aria-labelledby="menu1">
 
      </ul>
    </div>
</div>
<br><br><br>
