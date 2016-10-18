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
                var li = "<li role=\"presentation\"><a id=\"server-" + value + "\" role=\"menuitem\" tabindex=\"-1\" class=\"server-link\" href=\"javascript:void(0);\" >" + key + "</a></li>";
                $("#server-ui").append(li);
            });
            
            var serverObjList = '<%= request.getAttribute("serverObjList") %>';
            var objData = $.parseJSON(serverObjList);
            var serverParser = {};
            serverParser["id"] = "ok";
            $.each(objData, function(key, value){
                var serverObj = new Object();
                $.each(value, function(property, propertyVal){
                    if(property === "serverId"){
                        serverParser[String(propertyVal)] = serverObj;
                        serverParser[String(propertyVal)][property] = propertyVal;
                    }
                    else{
                        serverObj[property] = propertyVal;
                    }
                });
                
            });
            $(".server-link").click(function(){
                var id = $(this).attr("id").split("server-")[1];
                for(var key in serverParser[id]){
                    if(serverParser[id].hasOwnProperty(key)){
                        //alert("key = "+key+" value ="+serverParser[id][key]);
                        switch(key){
                            case "serverId":
                                $("input[name='serverId']").val(serverParser[id][key]);
                                break;
                            case "host":
                                $("input[name='host']").val(serverParser[id][key]);
                                break;
                            case "port":
                                $("input[name='port']").val(serverParser[id][key]);
                                break;
                            case "serverName":
                                $("input[name='serverName']").val(serverParser[id][key]);
                                break;
                            case "userName":
                                $("input[name='userName']").val(serverParser[id][key]);
                                break;
                            case "rsaKey":
                                $("input[name='rsaKey']").val(serverParser[id][key]);
                                break;
                            case "proxyHost":
                                $("input[name='proxyHost']").val(serverParser[id][key]);
                                break;
                            case "proxyPort":
                                $("input[name='proxyPort']").val(serverParser[id][key]);
                                break;
                            case "proxyUserName":
                                $("input[name='proxyUserName']").val(serverParser[id][key]);
                                break;
                            case "proxyPassword":
                                $("input[name='proxyPassword']").val(serverParser[id][key]);
                                break;
                            case "password":
                                $("input[name='password']").val(serverParser[id][key]);
                                break;
                            case "passPhrase":
                                $("input[name='passPhrase']").val(serverParser[id][key]);
                                break;
                            case "address":
                                $("input[name='address']").val(serverParser[id][key]);
                                break;
                            default:
                                break;
                        }
                    }
                }
                //alert(serverParser[id].serverId);
            });
        });

    </script>
</c:if>            
    
    <div class="dropdown col-lg-9 col-sm-9">
      <button class="btn btn-primary dropdown-toggle" id="server-list" type="button" data-toggle="dropdown">Select a server from the list &nbsp;
      <span class="caret"></span></button>
      <ul id="server-ui" class="dropdown-menu" role="menu" aria-labelledby="server-list">
 
      </ul>
    </div>
<br><br><br>
