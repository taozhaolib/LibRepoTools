<%-- any content can be specified here e.g.: --%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <title>ShareOK Data Processing Center</title>
    
    <!-- Bootstrap include -->
    <!-- Latest compiled and minified CSS -->
    <link href="<c:url value="/resources/css/bootstrap.min.css" />" rel="stylesheet">
    <!-- jQuery library -->    
    <script src="<c:url value="/resources/js/jquery.min.js" />"></script>

    <!-- Latest compiled JavaScript -->
    <script src="<c:url value="/resources/js/bootstrap.min.js" />"></script>
    
    <script src="<c:url value="/resources/js/loadingBtn.js" />"></script>
    
    <script src="<c:url value="/resources/js/vue.min.js" />"></script>
    
    <script src="<c:url value="/resources/js/utils.js" />"></script>
    
    <link href="<c:url value="/resources/css/main.css" />" rel="stylesheet">
    
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
</head>
<body>
    
    <div class="container">
        <div class="page-header">
            <span class="heading-text">Welcome to LibRepoTools</span><%@ include file="userInfo.jsp" %>      
        </div> 
    </div>
    