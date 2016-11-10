# LibRepoTools
LibRepoTools provides a UI to handle data packaging (e.g. SAF package), date migration and data importing from local files and remote storages, e.g. AWS S3 buckets, into digital repositories, e.g. DSpace and Islandora. 

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
1. The config module under the LibRepoTools installation directory contains all the configuration information. 

##Projects:
1. DSpace data processing through the REST API.
2. Data package, exporting from and importing into Islandora/Fedora repository.
3. Data package and transformation in AWS S3 buckets.

##Please give us your suggestions, comments, and ideas to help us improve this software.

##Contact information: 
tao.zhao@ou.edu
