<%-- any content can be specified here e.g.: --%>
<%@ page pageEncoding="UTF-8" %>

    <spring:url value="/resources/js/jquery.1.12.min.js" var="jqueryJs" />
    <spring:url value="/resources/js/test.js" var="mainJs" />

    <script src="${jqueryJs}"></script>
    <script src="${mainJs}"></script>
    
    <!-- Bootstrap include -->
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">

    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>

    <!-- Latest compiled JavaScript -->
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
    
    
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    
    <div class="container">
      <div class="page-header">
        <h1>ShareOK Data Processing Center</h1>      
      </div>     
        <ul class="nav nav-tabs">
            <li class="active"><a href="#">Home</a></li>
            <li><a href="#">PLOS Publications</a></li>
            <li><a href="#">SAGE Publications</a></li>
            <li><a href="#">DSpace Data Service</a></li>
            <li><a href="#">Islandora Data Service</a></li>
            <li><a href="#">Fedora Data Service</a></li>
          </ul>
    </div>
