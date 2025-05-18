@echo off
setlocal enabledelayedexpansion

REM 設置UTF-8編碼
chcp 65001 > nul
echo 使用UTF-8編碼進行編譯...

REM 建立lib目錄的類路徑
set CLASSPATH=.
if exist "lib\jfreechart-1.5.3.jar" (
    set CLASSPATH=!CLASSPATH!;lib\jfreechart-1.5.3.jar
)
if exist "lib\jcommon-1.0.24.jar" (
    set CLASSPATH=!CLASSPATH!;lib\jcommon-1.0.24.jar
)

REM 刪除先前編譯的類文件，確保完全重新編譯
echo 清理之前的編譯文件...
del /q /s AccountProgram\*.class 2>nul
del /q /s *.class 2>nul

REM 檢查所有依賴文件是否存在
echo 檢查依賴文件...
set missing_files=0

if not exist "Account.java" (
    echo 找不到 Account.java 文件
    set /a missing_files+=1
)
if not exist "AccountList.java" (
    echo 找不到 AccountList.java 文件
    set /a missing_files+=1
)
if not exist "StreamHelper.java" (
    echo 找不到 StreamHelper.java 文件
    set /a missing_files+=1
)
if not exist "DateUtils.java" (
    echo 找不到 DateUtils.java 文件
    set /a missing_files+=1
)
if not exist "ChartHelper.java" (
    echo 找不到 ChartHelper.java 文件
    set /a missing_files+=1
)

if %missing_files% GTR 0 (
    echo 警告：有 %missing_files% 個文件缺失，編譯可能會失敗
    pause
)

REM 先編譯基礎類
echo 編譯基礎類...
javac -encoding UTF-8 -d . DateUtils.java
if %ERRORLEVEL% NEQ 0 (
    echo DateUtils編譯失敗！
    pause
    exit /b %ERRORLEVEL%
)

javac -encoding UTF-8 -d . Account.java
if %ERRORLEVEL% NEQ 0 (
    echo Account編譯失敗！
    pause
    exit /b %ERRORLEVEL%
)

javac -encoding UTF-8 -d . AccountList.java
if %ERRORLEVEL% NEQ 0 (
    echo AccountList編譯失敗！
    pause
    exit /b %ERRORLEVEL%
)

javac -encoding UTF-8 -d . StreamHelper.java
if %ERRORLEVEL% NEQ 0 (
    echo StreamHelper編譯失敗！
    pause
    exit /b %ERRORLEVEL%
)

javac -encoding UTF-8 -d . ChartHelper.java
if %ERRORLEVEL% NEQ 0 (
    echo ChartHelper編譯失敗！
    pause
    exit /b %ERRORLEVEL%
)

REM 編譯SimpleChartPanel
echo 編譯SimpleChartPanel...
javac -encoding UTF-8 -d . SimpleChartPanel.java
if %ERRORLEVEL% NEQ 0 (
    echo SimpleChartPanel編譯失敗！
    pause
    exit /b %ERRORLEVEL%
)

REM 最後編譯AccountGUI
echo 編譯AccountGUI...
javac -encoding UTF-8 -d . AccountGUI.java
if %ERRORLEVEL% NEQ 0 (
    echo AccountGUI編譯失敗！
    pause
    exit /b %ERRORLEVEL%
)

echo 全部編譯成功！
echo 現在您可以運行程序了。

pause 