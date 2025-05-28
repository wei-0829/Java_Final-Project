@echo off
echo Compiling...
if not exist bin mkdir bin
javac -encoding UTF-8 -cp "lib/flatlaf-3.4.jar;lib/jfreechart-1.0.1.jar;lib/jcommon-1.0.0.jar;lib/jcalendar-1.4.jar" -d bin src/*.java
echo Running...
java -cp "lib/flatlaf-3.4.jar;lib/jfreechart-1.0.1.jar;lib/jcommon-1.0.0.jar;lib/jcalendar-1.4.jar;bin" AccountGUI
pause
