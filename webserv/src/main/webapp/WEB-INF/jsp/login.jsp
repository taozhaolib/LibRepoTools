<%-- 
    Document   : register
    Created on : Feb 29, 2016, 12:13:43 AM
    Author     : Tao Zhao
--%>

    <%@ include file="header.jsp" %>   
        
        <div class="container">
            <div class="jumbotron">
                <div class="panel panel-primary">
                    <div class="panel-heading"><h4>Please Login/Register to use ShareOK Data Services:</h4></div>
                    <div class="panel-body">
                        <center>
                            <form role="form" action="/webserv/register" method="post" enctype="multipart/form-data">
                                <div class="form-group">
                                    <label class="control-label col-sm-2" for="email">Email:</label>
                                    <div class="col-sm-10">
                                      <input type="email" class="form-control" id="email" placeholder="Enter email">
                                    </div>
                                </div>
                                <br/><br/><br/>
                                <div class="form-group">
                                    <label class="control-label col-sm-2" for="pwd">Password:</label>
                                    <div class="col-sm-10">          
                                        <input type="password" class="form-control" id="password" placeholder="Enter password">
                                    </div>
                                </div>
                                <br/><br/>
                                <div class="form-group">
                                    <label class="control-label col-sm-2" for="nickname">Nick name (optional): </label>
                                    <div class="col-sm-10">
                                       <input type="text" class="form-control" id="nickname">
                                    </div>
                                </div>
                                <br/><br/><br/>
                                    <button type="submit" class="btn btn-form">Submit</button>&nbsp;&nbsp;&nbsp;&nbsp;
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
