<%-- 
    Document   : serverSelectDropdown
    Created on : May 21, 2016, 6:54:15 PM
    Author     : Tao Zhao
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script type="text/javascript">
    $(document).ready(function(){
        var repoTypeList = '<%= request.getAttribute("repoTypeList") %>';        
        var data = $.parseJSON(repoTypeList);
        $.each(data, function(key, value){ 
            if(key !== '0'){
                var li = "<li role=\"presentation\">\n\
                            <a id=\"repoType-" + key + "\" role=\"menuitem\" tabindex=\"-1\" class=\"repoType-link\" href=\"javascript:void(0);\" >" + 
                                "<span class=\".text-capitalize \" ><strong>" + value + "</strong></span>" +
                            "</a>\n\
                          </li>";
                $("#repoTypeList-ul").append(li);
                var repoTypeDropdownDiv = $("#repoTypeDropdownDiv");
                switch(key){
                    case "1":
                        repoTypeDropdownDiv.after("<div id=\"repoTypeInfoDiv-1\" class=\"col-lg-12 col-md-12 col-sm-12 repoTypeInfoDivCls\"><br>" +
                                                "<fieldset style=\"border:2px solid #5bc0de;  padding-top: 1.5em; padding-bottom 1.5em;\">" +
                                                "<div><label class=\"control-label col-lg-2 col-sm-4 text-left\" for=\"drupalPath\">Dspace Directory:</label></div>" +
                                                "<div class=\"col-lg-4 col-sm-8\">" +
                                                      "<input type=\"text\" class=\"form-control\" name=\"dspacePath\" placeholder=\"\">" +
                                                "</div>" +
                                                "<label class=\"control-label col-lg-2 col-sm-4 text-left\" for=\"dspaceUploadPath\">Upload Directory:</label>" +
                                                "<div class=\"col-lg-4 col-sm-8\">" +          
                                                    "<input type=\"text\" class=\"form-control\" name=\"dspaceUploadPath\">" +
                                                "</div><br><br>" +
                                                "<br></fieldset><br></div>");
                        break;
                    case "2":
                        repoTypeDropdownDiv.after("<div id=\"repoTypeInfoDiv-2\" class=\"col-lg-12 col-md-12 col-sm-12 repoTypeInfoDivCls\"><br>" +
                                                "<fieldset style=\"border:2px solid #5bc0de;  padding-top: 1.5em; padding-bottom 1.5em;\">" +
                                                "<div><label class=\"control-label col-lg-2 col-sm-4 text-left\" for=\"drupalPath\">Drupal Directory:</label></div>" +
                                                "<div class=\"col-lg-4 col-sm-8\">" +
                                                      "<input type=\"text\" class=\"form-control\" name=\"drupalPath\" placeholder=\"\">" +
                                                "</div>" +
                                                "<label class=\"control-label col-lg-2 col-sm-4 text-left\" for=\"islandoraUploadPath\">Upload Directory:</label>" +
                                                "<div class=\"col-lg-4 col-sm-8\">" +          
                                                    "<input type=\"text\" class=\"form-control\" name=\"islandoraUploadPath\">" +
                                                "</div><br><br><br>" +
                                                "<label class=\"control-label col-lg-2 col-sm-4 text-left\" for=\"tempFilePath\">Temporary File Directory:</label>" +
                                                "<div class=\"col-lg-4 col-sm-8\">" +
                                                    "<input type=\"text\" class=\"form-control\" name=\"tempFilePath\">" +
                                                "</div><br><br><br></fieldset></div>");
                        break;
                    case "3":
                        break;
                    case "4":
                        break;
                    default:
                        break;
                }
            }
        });
        
        $(".repoType-link").click(function(){
            var id = $(this).attr("id").split("repoType-")[1];      
            $("input[name='repoTypeId']").val(id);
            $(".repoTypeInfoDivCls").hide();
            
            var repoTypeInfoDiv = $("#repoTypeInfoDiv-"+id);
            if(repoTypeInfoDiv && repoTypeInfoDiv.length === 1){
                repoTypeInfoDiv.show();
            }
            
            var repoTypeInput = $("#repoType");
            if(repoTypeInput){
                repoTypeInput.val(id);
            }
        });
    });

</script>           
    
   <div id="repoTypeDropdownDiv" class="col-lg-12 col-md-12 col-sm-12 btn-group">
        <button type="button" data-toggle="dropdown" class="btn btn-info dropdown-toggle">
            &nbsp;&nbsp;&nbsp;&nbsp;Select a Repository Type&nbsp;&nbsp;&nbsp;&nbsp; 
            <span class="caret"></span>
        </button>
        <ul class="dropdown-menu" id="repoTypeList-ul">

        </ul>
       <br>
       <input type="hidden" value="-1" id="repoTypeId" name="repoTypeId">
    </div>
<br><br>
  
