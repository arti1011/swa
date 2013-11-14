@ECHO OFF
mvn -o -DskipTests package jboss-as:deploy
pause
