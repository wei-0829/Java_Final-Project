@echo off
setlocal enabledelayedexpansion

REM 設置UTF-8編碼
chcp 65001 > nul
echo 修復圖表按鈕功能...

REM 創建備份
if not exist "AccountGUI.java.bak" (
    copy AccountGUI.java AccountGUI.java.bak
    echo 已創建備份：AccountGUI.java.bak
)

REM 修改ChartButtonListener類
echo 正在更新ChartButtonListener類...
powershell -Command "(Get-Content AccountGUI.java) -replace 'SimpleChartPanel.showChartDialog\\(frame\\);', 'try { Class<?> chartPanelClass = Class.forName(\"SimpleChartPanel\"); java.lang.reflect.Method showChartDialogMethod = chartPanelClass.getMethod(\"showChartDialog\", JFrame.class); showChartDialogMethod.invoke(null, frame); } catch (Exception e) { System.err.println(\"圖表功能錯誤: \" + e.getMessage()); e.printStackTrace(); JOptionPane.showMessageDialog(frame, \"圖表功能出現錯誤。請確保lib目錄中有JFreeChart的jar檔案。\\n錯誤: \" + e.getMessage(), \"圖表錯誤\", JOptionPane.ERROR_MESSAGE); }' | Set-Content AccountGUI.java"

echo 修復完成！
echo.
echo 您現在可以使用以下命令編譯和運行程序：
echo compile_with_packages.bat
echo run_accounting.bat
echo.
echo 如需測試圖表功能，可使用以下命令：
echo compile_test_chart.bat
echo run_chart_test.bat

pause 