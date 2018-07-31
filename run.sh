#!/bin/bash
rm apache-tomcat-9.0.10/webapps/Scrabble -f
cp build/prod/Scrabble.war apache-tomcat-9.0.10/webapps -f
chmod -R 777 apache-tomcat-9.0.10/bin/catalina.sh
apache-tomcat-9.0.10/bin/catalina.sh run
