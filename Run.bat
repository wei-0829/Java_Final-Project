@echo off
echo Compiling...
javac -cp "lib/*" -d bin src/*.java
echo Running...
java -cp "lib/*;bin" AccountGUI
pause
