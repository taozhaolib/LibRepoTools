# LibRepoTools
LibRepoTools provides a UI to handle data package (e.g. SAF pagkage), date transformation and importing into digital repositories like DSpace.

Installation:

1. Install Java 8, tomcat 7, maven 3.
2. Install and run redis 3.0 or higher with default settings.
3. Git clone this repository and run mvn install.
4. At tomcat home directory/config/Catalina/localhost, add file webserv.xml:
  <?xml version="1.0" encoding="UTF-8"?>
    <Context antiJARLocking="true" 
    docBase="{LibRepoTools parent directory}/LibRepoTools/shareokdata/webserv/target/webserv-1.0-SNAPSHOT" 
    path="/webserv"
  />
5. Start tomcat and go to http://localhost:8080/webserv/home to get the home page.

Contact tao.zhao@ou.edu should you have any questions or problems.
