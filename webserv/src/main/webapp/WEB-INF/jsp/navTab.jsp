<%-- 
    Document   : navTab
    Created on : Jan 28, 2016, 10:51:01 AM
    Author     : Tao Zhao
--%>

<div id="main-tab" class="container">    
    <ul class="nav nav-tabs">
        <li class="active dropdown"><a href="/webserv/home">Home</a></li>
        <li><a class="dropdown-toggle" data-toggle="dropdown" href="#">DSpace Data Service
            <span class="caret"></span></a>
            <ul class="dropdown-menu">
                <li class="mainMenuDrop" id="plosDs"><a href="/webserv/dspace/journal/plos">PLOS Publications</a></li>
                <li class="mainMenuDrop" id="sageDs"><a href="/webserv/dspace/journal/sage">SAGE Publications</a></li>
                <li class="mainMenuDrop" id="dataDs"><a href="#">DSpace Data Manipulation</a></li>
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
              <li class="mainMenuDrop" id="dataIs"><a href="#">Islandora Data Manipulation</a></li>
            </ul>
         </li>
      </ul>
</div>
