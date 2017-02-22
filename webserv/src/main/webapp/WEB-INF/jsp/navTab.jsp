<%-- 
    Document   : navTab
    Created on : Jan 28, 2016, 10:51:01 AM
    Author     : Tao Zhao
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="main-tab" class="container">    
    <ul class="nav nav-tabs">
        <li class="active dropdown"><a href="/webserv/home">Home</a></li>
        <li><a class="dropdown-toggle" data-toggle="dropdown" href="#">DSpace Data Service
            <span class="caret"></span></a>
            <ul class="dropdown-menu">
                <li class="mainMenuDrop" id="plosDs"><a href="/webserv/dspace/journal/plos">Import PLOS Publications</a></li>
                <!-- <li class="mainMenuDrop" id="plosDs"><a href="/webserv/dspace/journal/plos/doi">Import PLOS Publications by DOI</a></li> -->
                <li class="mainMenuDrop" id="plosDs"><a href="/webserv/journal/search/plos/date">Search PLOS Publications by Dates</a></li>
                <li class="mainMenuDrop" id="sageDs"><a href="/webserv/dspace/journal/sage">Import SAGE Publications</a></li>
                <li class="mainMenuDrop" id="sageDs"><a href="/webserv/dspace/journal/ouhistory">Generate SAF Package</a></li>
                <!-- 
                <li class="mainMenuDrop" id="dsSafImport"><a href="/webserv/ssh/dspace/saf/page/ssh-upload">Upload Simple Archive Format Package</a></li>
                <li class="mainMenuDrop" id="dsSafImport"><a href="/webserv/ssh/dspace/saf/page/ssh-import">Import Simple Archive Format Package</a></li>
                <li class="mainMenuDrop" id="dsSafImport"><a href="/webserv/ssh/dspace/saf/page/ssh-importloaded">Import Uploaded Simple Archive Format Package</a></li>
                -->
                <li class="mainMenuDrop" id="dsApiSafImport"><a href="/webserv/rest/dspace/saf/page/rest-import">DSpace REST API: Import Simple Archive Format Package</a></li>
            </ul>
        </li>
        <li><a class="dropdown-toggle" data-toggle="dropdown" href="#">Fedora Data Service
           <span class="caret"></span></a>
           <ul class="dropdown-menu">
             <li class="mainMenuDrop" id="dataFd"><a href="#">Fedora Data Manipulation</a></li>
           </ul>
        </li>
        <li><a class="dropdown-toggle" data-toggle="dropdown" href="#">Islandora Data Service
           <span class="caret"></span></a>
           <ul class="dropdown-menu">
             <li class="mainMenuDrop" id="dataIs"><a href="/webserv/ssh/islandora/book/import/page/ssh-import">Islandora Book Import</a></li>
             <li class="mainMenuDrop" id="dataIs"><a href="/webserv/s3/islandora/bag/import/page">Islandora S3 Bag Import</a></li>
           </ul>
        </li>
        <c:if test="${not empty loggedin }">
            <li><a class="dropdown-toggle" data-toggle="dropdown" href="#">Your Account
                <span class="caret"></span></a>
                <ul class="dropdown-menu">
                  <li class="mainMenuDrop" id="userJobHistory"><a href="/webserv/user/${loggedin}/jobHistory">Job History</a></li>
                  <li class="mainMenuDrop" id="userJobHistory"><a href="/webserv/server/config">Server Configuration</a></li>
                  <li class="mainMenuDrop" id="userProfile"><a href="/webserv/userProfile">Change Profile</a></li>
                </ul>
             </li>
        </c:if>
      </ul>
</div>
