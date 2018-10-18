# Log Management Service

## Package

mvn -Dmaven.test.skip clean install

## PostgreSQL
pgAdmin 4

Tools -> Restore

Import Use log_db.backup

## Tomcat
cp target/logManagementService##1.0.0-SNAPSHOT.war <TOMCAT_HOME>/webapps

cp target/classes/log4j2.xml <TOMCAT_HOME>/lib

cp target/classes/logManagementService.properties <TOMCAT_HOME>/lib

catalina start

## Swagger
[Swagger UI](http://localhost:8080/logManagementService/lms/v1/swagger-ui.html)