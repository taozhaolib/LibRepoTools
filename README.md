# LibRepoTools
LibRepoTools provides a UI to handle data packaging (e.g. SAF package), date transformation and data importing from local files and remote sites such as PLOS ONE and SAGE Open into digital repositories such as DSpace and (soon) Fedora and Islandora. 

##Installation:

1. Install Java 8 (required), Tomcat 7.0.69 (do NOT use very old version Tomcat 7), Maven 3.
2. Install and run Redis 3.0 or higher with default settings.
3. Git clone this repository and run: mvn install.
4. At Tomcat home directory: /config/Catalina/localhost, add the file webserv.xml
5. Start Tomcat and go to: http://localhost:8080/webserv/home to get the home page.
6. Sample webserv.xml file:
```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <Context antiJARLocking="true" docBase="{LibRepoTools parent directory}/LibRepoTools/webserv/target/webserv-1.0-SNAPSHOT" path="/webserv"/>
  ```


##Technology stack:
Java 8(required), Maven 3, Tomcat 7, Spring Core, Spring Data, Spring Session, Spring MVC, Redis, JSch.

##Demo web site:
https://libtools-demo.repository.ou.edu/webserv/home

##Configuration:
1. The config directory under the LibRepoTools installation directory contains all the configuration information. 
2. Will push out the UI configuration feature soon.

##Please give us your suggestions, comments, and ideas to help us improve this software.

##Contact information: 
tao.zhao@ou.edu
