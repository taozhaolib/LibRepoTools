# LibRepoTools
LibRepoTools provides a UI to handle data package (e.g. SAF pagkage), date transformation and importing into digital repositories like DSpace. 

Installation:

1. Install Java 8, tomcat 7.0.69 <do NOT use very old version>, maven 3.
2. Install and run redis 3.0 or higher with default settings.
3. Git clone this repository and run mvn install.
4. At tomcat home directory/config/Catalina/localhost, add the file webserv.xml
5. Start tomcat and go to http://localhost:8080/webserv/home to get the home page.
6. Sample webserv.xml file:
```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <Context antiJARLocking="true" docBase="{LibRepoTools parent directory}/LibRepoTools/shareokdata/webserv/target/webserv-1.0-SNAPSHOT" path="/webserv"/>
  ```


Technology stack:
Java 8, Maven 3, tomcat 7, Spring Core, Spring Data, Spring session, Spring MVC, Redis, JSch.

Contact tao.zhao@ou.edu should you have any questions or problems.
