@echo off
setlocal enabledelayedexpansion

REM 設置UTF-8編碼
chcp 65001 > nul
echo 使用UTF-8編碼運行程序...

REM 建立lib目錄的類路徑
set CLASSPATH=.
if exist "lib\jfreechart-1.5.3.jar" (
    set CLASSPATH=!CLASSPATH!;lib\jfreechart-1.5.3.jar
)
if exist "lib\jcommon-1.0.24.jar" (
    set CLASSPATH=!CLASSPATH!;lib\jcommon-1.0.24.jar
)

REM 運行主程序
echo 正在啟動記帳程序...
java -cp %CLASSPATH% AccountProgram.AccountGUI
if %ERRORLEVEL% NEQ 0 (
    echo 運行失敗！
    pause
    exit /b %ERRORLEVEL%
)

pause 