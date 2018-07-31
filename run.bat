del apache-tomcat-9.0.10\webapps\Scrabble /y
copy build\prod\Scrabble.war apache-tomcat-9.0.10\webapps /y
set CATALINA_HOME=apache-tomcat-9.0.10
apache-tomcat-9.0.10\bin\catalina.bat run