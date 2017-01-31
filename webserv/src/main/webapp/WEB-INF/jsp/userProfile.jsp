<%-- 
    Document   : userProfile
    Created on : Jan 31, 2017, 3:18:19 PM
    Author     : Tao Zhao
--%>

<%@ include file="header.jsp" %>  
<%@include file="navTab.jsp" %>

    <div class="container dspace-server-info server-info-container">

        <div class="jumbotron">
            <div class="panel panel-primary server-config-panel">            
                <div class="panel-heading">
                    <span style="font-size: 20px; font-weight: 800;">Reset your password:</span>
                </div>
                <div class="panel-body">
                    <center>
                        <form role="form" action="/webserv/user/newPass" method="post" enctype="multipart/form-data">     
                            <div class="form-group">
                                <label class="control-label col-lg-2 col-sm-4 text-left" for="oldPass">Please type in your old password: </label>
                                <div class="col-lg-6 col-sm-8">
                                  <input type="password" class="form-control" name="oldPass" placeholder="">
                                </div>
                            </div>
                            <br><br><br>
                            <div class="form-group">
                                <label class="control-label col-lg-2 col-sm-4 text-left" for="newPass">Please type in your new password: </label>
                                <div class="col-lg-6 col-sm-8">
                                  <input type="password" class="form-control" name="newPass" placeholder="">
                                </div>
                            </div>
                            <br><br><br>
                            <div class="form-group">
                                <label class="control-label col-lg-2 col-sm-4 text-left" for="newPassConfirm">Please re-type your new password: </label>
                                <div class="col-lg-6 col-sm-8">
                                  <input type="password" class="form-control" name="newPassConfirm" placeholder="">
                                </div>
                            </div>
                            <br><br><br>
                                <input type="submit" class="btn btn-form" value="Submit">&nbsp;&nbsp;&nbsp;&nbsp;
                                <input type="button"  class="btn btn-form" onclick="location.href='/webserv/home'" value="Back to home page">
                            <br>
                        </form>
                    </center>
                </div>
            </div>
        </div>
    </div>
    </body>
</html>