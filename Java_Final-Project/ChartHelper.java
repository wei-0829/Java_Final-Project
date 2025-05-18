package AccountProgram;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.axis.NumberAxis;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * 提供圖表生成和匯出功能的工具類
 */
public class ChartHelper {

    /**
     * 生成支出類別的圓餅圖
     * @param accountList 帳目列表
     * @param title 圖表標題
     * @return 圓餅圖面板
     */
    public static ChartPanel createExpensePieChart(AccountList accountList, String title) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        int breakfastTotal = 0, lunchTotal = 0, dinnerTotal = 0, othersTotal = 0;
        
        for (int i = 0; i < accountList.size(); i++) {
            Account acc = accountList.get(i);
            breakfastTotal += acc.getBreakfast();
            lunchTotal += acc.getLunch();
            dinnerTotal += acc.getDinner();
            othersTotal += acc.getOthers();
        }
        
        // 只添加非零值項目，並添加數值在標籤中
        if (breakfastTotal > 0) dataset.setValue("早餐 " + breakfastTotal + "元", breakfastTotal);
        if (lunchTotal > 0) dataset.setValue("午餐 " + lunchTotal + "元", lunchTotal);
        if (dinnerTotal > 0) dataset.setValue("晚餐 " + dinnerTotal + "元", dinnerTotal);
        if (othersTotal > 0) dataset.setValue("其他 " + othersTotal + "元", othersTotal);
        
        JFreeChart chart = ChartFactory.createPieChart(
                title,          // 圖表標題
                dataset,        // 資料集
                true,           // 是否顯示圖例
                true,           // 是否顯示提示
                false           // 是否產生URLs
        );
        
        // 設置中文字體
        setChartFont(chart);
        
        // 配置餅圖，顯示項目標籤和百分比
        org.jfree.chart.plot.PiePlot plot = (org.jfree.chart.plot.PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        plot.setLabelGap(0.02);
        plot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator(
                "{0} ({2})", new java.text.DecimalFormat("0"), new java.text.DecimalFormat("0.0%")));
                
        // 強制顯示標籤
        plot.setLabelLinksVisible(true);
        plot.setShadowGenerator(null); // 關閉陰影以提高清晰度
        
        // 設置標籤位置為外部
        plot.setLabelLinkStyle(org.jfree.chart.plot.PieLabelLinkStyle.QUAD_CURVE);
        plot.setLabelLinkMargin(0.1);
        plot.setSimpleLabels(false);
        plot.setLabelBackgroundPaint(new Color(255, 255, 255, 200)); // 半透明白色背景
        plot.setExplodePercent("早餐 " + breakfastTotal + "元", 0.05); // 稍微突出第一項
        
        return new ChartPanel(chart);
    }
    
    /**
     * 生成收入類別的圓餅圖
     * @param accountList 帳目列表
     * @param title 圖表標題
     * @return 圓餅圖面板
     */
    public static ChartPanel createIncomePieChart(AccountList accountList, String title) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        int salaryTotal = 0, investmentTotal = 0, bonusTotal = 0, otherIncomeTotal = 0;
        
        for (int i = 0; i < accountList.size(); i++) {
            Account acc = accountList.get(i);
            salaryTotal += acc.getSalary();
            investmentTotal += acc.getInvestment();
            bonusTotal += acc.getBonus();
            otherIncomeTotal += acc.getOtherIncome();
        }
        
        // 只添加非零值項目，並添加數值在標籤中
        if (salaryTotal > 0) dataset.setValue("薪資 " + salaryTotal + "元", salaryTotal);
        if (investmentTotal > 0) dataset.setValue("投資 " + investmentTotal + "元", investmentTotal);
        if (bonusTotal > 0) dataset.setValue("獎金 " + bonusTotal + "元", bonusTotal);
        if (otherIncomeTotal > 0) dataset.setValue("其他 " + otherIncomeTotal + "元", otherIncomeTotal);
        
        JFreeChart chart = ChartFactory.createPieChart(
                title,          // 圖表標題
                dataset,        // 資料集
                true,           // 是否顯示圖例
                true,           // 是否顯示提示
                false           // 是否產生URLs
        );
        
        // 設置中文字體
        setChartFont(chart);
        
        // 配置餅圖，顯示項目標籤和百分比
        org.jfree.chart.plot.PiePlot plot = (org.jfree.chart.plot.PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        plot.setLabelGap(0.02);
        plot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator(
                "{0} ({2})", new java.text.DecimalFormat("0"), new java.text.DecimalFormat("0.0%")));
                
        // 強制顯示標籤
        plot.setLabelLinksVisible(true);
        plot.setShadowGenerator(null); // 關閉陰影以提高清晰度
        
        // 設置標籤位置為外部
        plot.setLabelLinkStyle(org.jfree.chart.plot.PieLabelLinkStyle.QUAD_CURVE);
        plot.setLabelLinkMargin(0.1);
        plot.setSimpleLabels(false);
        plot.setLabelBackgroundPaint(new Color(255, 255, 255, 200)); // 半透明白色背景
        plot.setExplodePercent("薪資 " + salaryTotal + "元", 0.05); // 稍微突出第一項
        
        return new ChartPanel(chart);
    }
    
    /**
     * 生成每月總支出折線圖
     * @param accountList 帳目列表 
     * @param title 圖表標題
     * @return 折線圖面板
     */
    public static ChartPanel createMonthlyExpenseLineChart(AccountList accountList, String title) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // 將帳目按月份分組並計算每月總支出
        Map<String, Integer> monthlyTotals = new TreeMap<>(); // 使用TreeMap以確保月份排序
        
        // 記錄最早和最晚的月份，用於填充所有月份
        String earliestMonth = null;
        String latestMonth = null;
        
        // 收集數據
        for (int i = 0; i < accountList.size(); i++) {
            Account acc = accountList.get(i);
            String date = acc.getDate();
            
            // 確保日期長度足夠
            if (date == null || date.length() < 7) continue;
            
            // 從日期中提取年月 (yyyy/MM)
            String yearMonth = date.substring(0, 7);
            
            int total = acc.getExpenseTotal();
            
            // 更新每月總支出
            if (monthlyTotals.containsKey(yearMonth)) {
                monthlyTotals.put(yearMonth, monthlyTotals.get(yearMonth) + total);
            } else {
                monthlyTotals.put(yearMonth, total);
            }
            
            // 更新最早和最晚的月份
            if (earliestMonth == null || yearMonth.compareTo(earliestMonth) < 0) {
                earliestMonth = yearMonth;
            }
            if (latestMonth == null || yearMonth.compareTo(latestMonth) > 0) {
                latestMonth = yearMonth;
            }
        }
        
        // 如果有數據，確保所有月份都有值（包括零值）
        if (earliestMonth != null && latestMonth != null) {
            fillMissingMonths(monthlyTotals, earliestMonth, latestMonth);
        }
        
        // 將資料加入到dataset
        for (Map.Entry<String, Integer> entry : monthlyTotals.entrySet()) {
            dataset.addValue(entry.getValue(), "月支出", entry.getKey());
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
                title,                  // 圖表標題
                "月份",                  // X軸標籤
                "支出 (元)",             // Y軸標籤
                dataset,                // 資料集
                PlotOrientation.VERTICAL, // 圖表方向
                true,                   // 是否顯示圖例
                true,                   // 是否顯示提示
                false                   // 是否產生URLs
        );
        
        // 設置中文字體和顯示設定
        setChartFont(chart);
        
        // 加強折線圖的樣式
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(255, 255, 255));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        
        // 設置線條樣式 - 粗線條
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesPaint(0, new Color(0, 120, 0));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setDrawOutlines(true);
        renderer.setUseFillPaint(true);
        renderer.setSeriesFillPaint(0, Color.white);
        
        return new ChartPanel(chart);
    }
    
    /**
     * 生成每月總收入折線圖
     * @param accountList 帳目列表 
     * @param title 圖表標題
     * @return 折線圖面板
     */
    public static ChartPanel createMonthlyIncomeLineChart(AccountList accountList, String title) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // 將帳目按月份分組並計算每月總收入
        Map<String, Integer> monthlyTotals = new TreeMap<>(); // 使用TreeMap以確保月份排序
        
        // A記錄最早和最晚的月份，用於填充所有月份
        String earliestMonth = null;
        String latestMonth = null;
        
        // 收集數據
        for (int i = 0; i < accountList.size(); i++) {
            Account acc = accountList.get(i);
            String date = acc.getDate();
            
            // 確保日期長度足夠
            if (date == null || date.length() < 7) continue;
            
            // 從日期中提取年月 (yyyy/MM)
            String yearMonth = date.substring(0, 7);
            
            int total = acc.getIncomeTotal();
            
            // 更新每月總收入
            if (monthlyTotals.containsKey(yearMonth)) {
                monthlyTotals.put(yearMonth, monthlyTotals.get(yearMonth) + total);
            } else {
                monthlyTotals.put(yearMonth, total);
            }
            
            // 更新最早和最晚的月份
            if (earliestMonth == null || yearMonth.compareTo(earliestMonth) < 0) {
                earliestMonth = yearMonth;
            }
            if (latestMonth == null || yearMonth.compareTo(latestMonth) > 0) {
                latestMonth = yearMonth;
            }
        }
        
        // 如果有數據，確保所有月份都有值（包括零值）
        if (earliestMonth != null && latestMonth != null) {
            fillMissingMonths(monthlyTotals, earliestMonth, latestMonth);
        }
        
        // 將資料加入到dataset
        for (Map.Entry<String, Integer> entry : monthlyTotals.entrySet()) {
            dataset.addValue(entry.getValue(), "月收入", entry.getKey());
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
                title,                  // 圖表標題
                "月份",                  // X軸標籤
                "收入 (元)",             // Y軸標籤
                dataset,                // 資料集
                PlotOrientation.VERTICAL, // 圖表方向
                true,                   // 是否顯示圖例
                true,                   // 是否顯示提示
                false                   // 是否產生URLs
        );
        
        // 設置中文字體和顯示設定
        setChartFont(chart);
        
        // 加強折線圖的樣式
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(255, 255, 255));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        
        // 設置線條樣式 - 粗線條
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesPaint(0, new Color(0, 0, 200));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setDrawOutlines(true);
        renderer.setUseFillPaint(true);
        renderer.setSeriesFillPaint(0, Color.white);
        
        return new ChartPanel(chart);
    }
    
    /**
     * 生成收入-支出平衡趨勢圖
     * @param accountList 帳目列表
     * @param title 圖表標題
     * @return 折線圖面板
     */
    public static ChartPanel createIncomeExpenseBalanceChart(AccountList accountList, String title) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // 將帳目按月份分組並計算每月收支平衡
        Map<String, Integer> incomeByMonth = new TreeMap<>();
        Map<String, Integer> expenseByMonth = new TreeMap<>();
        Map<String, Integer> balanceByMonth = new TreeMap<>();
        
        // 記錄最早和最晚的月份，用於填充所有月份
        String earliestMonth = null;
        String latestMonth = null;
        
        // 收集數據
        for (int i = 0; i < accountList.size(); i++) {
            Account acc = accountList.get(i);
            String date = acc.getDate();
            
            // 確保日期長度足夠
            if (date == null || date.length() < 7) continue;
            
            // 從日期中提取年月 (yyyy/MM)
            String yearMonth = date.substring(0, 7);
            
            // 更新月度收入
            updateMonthlyMap(incomeByMonth, yearMonth, acc.getIncomeTotal());
            // 更新月度支出
            updateMonthlyMap(expenseByMonth, yearMonth, acc.getExpenseTotal());
            // 更新月度收支平衡
            updateMonthlyMap(balanceByMonth, yearMonth, acc.getBalance());
            
            // 更新最早和最晚的月份
            if (earliestMonth == null || yearMonth.compareTo(earliestMonth) < 0) {
                earliestMonth = yearMonth;
            }
            if (latestMonth == null || yearMonth.compareTo(latestMonth) > 0) {
                latestMonth = yearMonth;
            }
        }
        
        // 如果有數據，確保所有月份都有值（包括零值）
        if (earliestMonth != null && latestMonth != null) {
            fillMissingMonths(incomeByMonth, earliestMonth, latestMonth);
            fillMissingMonths(expenseByMonth, earliestMonth, latestMonth);
            fillMissingMonths(balanceByMonth, earliestMonth, latestMonth);
        }
        
        // 將各項數據加入到dataset
        addCategoryToDataset(dataset, incomeByMonth, "收入");
        addCategoryToDataset(dataset, expenseByMonth, "支出");
        addCategoryToDataset(dataset, balanceByMonth, "收支平衡");
        
        JFreeChart chart = ChartFactory.createLineChart(
                title,                  // 圖表標題
                "月份",                  // X軸標籤
                "金額 (元)",             // Y軸標籤
                dataset,                // 資料集
                PlotOrientation.VERTICAL, // 圖表方向
                true,                   // 是否顯示圖例
                true,                   // 是否顯示提示
                false                   // 是否產生URLs
        );
        
        // 設置中文字體
        setChartFont(chart);
        
        // 設置零線顏色
        CategoryPlot plot = chart.getCategoryPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRangeIncludesZero(true);
        
        // 設置線條樣式
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        // 收入：綠色
        renderer.setSeriesPaint(0, new Color(0, 150, 0));
        renderer.setSeriesShapesVisible(0, true);
        // 支出：紅色
        renderer.setSeriesPaint(1, new Color(200, 0, 0));
        renderer.setSeriesShapesVisible(1, true);
        // 平衡：藍色
        renderer.setSeriesPaint(2, new Color(0, 0, 220));
        renderer.setSeriesShapesVisible(2, true);
        
        // 加強圖例顯示
        chart.getLegend().setItemFont(new Font("Microsoft JhengHei", Font.BOLD, 12));
        
        return new ChartPanel(chart);
    }
    
    /**
     * 檢查收支平衡，若收入小於支出則彈出提醒
     * @param accountList 帳目列表
     * @param parent 父窗體用於顯示對話框
     */
    public static void checkBalance(AccountList accountList, Component parent) {
        // 計算總收入和總支出
        int totalIncome = 0;
        int totalExpense = 0;
        
        for (int i = 0; i < accountList.size(); i++) {
            Account acc = accountList.get(i);
            totalIncome += acc.getIncomeTotal();
            totalExpense += acc.getExpenseTotal();
        }
        
        // 如果總支出大於總收入，顯示警告
        if (totalExpense > totalIncome) {
            JOptionPane.showMessageDialog(
                parent,
                "⚠️ 警告：總支出 (" + totalExpense + " 元) 大於總收入 (" + totalIncome + " 元)！\n" +
                "您已入不敷出：" + (totalIncome - totalExpense) + " 元\n" +
                "請考慮增加收入或減少支出。",
                "收支警告",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }
    
    /**
     * 生成每月支出類別折線圖
     * @param accountList 帳目列表
     * @param title 圖表標題
     * @return 折線圖面板
     */
    public static ChartPanel createMonthlyExpenseByCategoryLineChart(AccountList accountList, String title) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // 按月份分組各類別的支出
        Map<String, Integer> breakfastByMonth = new TreeMap<>();
        Map<String, Integer> lunchByMonth = new TreeMap<>();
        Map<String, Integer> dinnerByMonth = new TreeMap<>();
        Map<String, Integer> othersByMonth = new TreeMap<>();
        
        for (int i = 0; i < accountList.size(); i++) {
            Account acc = accountList.get(i);
            String date = acc.getDate();
            // 從日期中提取年月 (yyyy/MM)
            String yearMonth = date.substring(0, 7);
            
            // 更新各類別的月支出
            updateMonthlyMap(breakfastByMonth, yearMonth, acc.getBreakfast());
            updateMonthlyMap(lunchByMonth, yearMonth, acc.getLunch());
            updateMonthlyMap(dinnerByMonth, yearMonth, acc.getDinner());
            updateMonthlyMap(othersByMonth, yearMonth, acc.getOthers());
        }
        
        // 將各類別資料加入到dataset
        addCategoryToDataset(dataset, breakfastByMonth, "早餐");
        addCategoryToDataset(dataset, lunchByMonth, "午餐");
        addCategoryToDataset(dataset, dinnerByMonth, "晚餐");
        addCategoryToDataset(dataset, othersByMonth, "其他");
        
        JFreeChart chart = ChartFactory.createLineChart(
                title,                  // 圖表標題
                "月份",                  // X軸標籤
                "支出 (元)",             // Y軸標籤
                dataset,                // 資料集
                PlotOrientation.VERTICAL, // 圖表方向
                true,                   // 是否顯示圖例
                true,                   // 是否顯示提示
                false                   // 是否產生URLs
        );
        
        return new ChartPanel(chart);
    }
    
    /**
     * 生成每月收入類別折線圖
     * @param accountList 帳目列表
     * @param title 圖表標題
     * @return 折線圖面板
     */
    public static ChartPanel createMonthlyIncomeByCategoryLineChart(AccountList accountList, String title) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // 按月份分組各類別的收入
        Map<String, Integer> salaryByMonth = new TreeMap<>();
        Map<String, Integer> investmentByMonth = new TreeMap<>();
        Map<String, Integer> bonusByMonth = new TreeMap<>();
        Map<String, Integer> otherIncomeByMonth = new TreeMap<>();
        
        for (int i = 0; i < accountList.size(); i++) {
            Account acc = accountList.get(i);
            String date = acc.getDate();
            // 從日期中提取年月 (yyyy/MM)
            String yearMonth = date.substring(0, 7);
            
            // 更新各類別的月收入
            updateMonthlyMap(salaryByMonth, yearMonth, acc.getSalary());
            updateMonthlyMap(investmentByMonth, yearMonth, acc.getInvestment());
            updateMonthlyMap(bonusByMonth, yearMonth, acc.getBonus());
            updateMonthlyMap(otherIncomeByMonth, yearMonth, acc.getOtherIncome());
        }
        
        // 將各類別資料加入到dataset
        addCategoryToDataset(dataset, salaryByMonth, "薪資");
        addCategoryToDataset(dataset, investmentByMonth, "投資");
        addCategoryToDataset(dataset, bonusByMonth, "獎金");
        addCategoryToDataset(dataset, otherIncomeByMonth, "其他");
        
        JFreeChart chart = ChartFactory.createLineChart(
                title,                  // 圖表標題
                "月份",                  // X軸標籤
                "收入 (元)",             // Y軸標籤
                dataset,                // 資料集
                PlotOrientation.VERTICAL, // 圖表方向
                true,                   // 是否顯示圖例
                true,                   // 是否顯示提示
                false                   // 是否產生URLs
        );
        
        return new ChartPanel(chart);
    }
    
    /**
     * 輔助方法：更新月度支出映射
     */
    private static void updateMonthlyMap(Map<String, Integer> map, String yearMonth, int value) {
        if (map.containsKey(yearMonth)) {
            map.put(yearMonth, map.get(yearMonth) + value);
        } else {
            map.put(yearMonth, value);
        }
    }
    
    /**
     * 輔助方法：將類別資料加入到資料集
     */
    private static void addCategoryToDataset(DefaultCategoryDataset dataset, 
                                           Map<String, Integer> dataMap, 
                                           String categoryName) {
        for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
            dataset.addValue(entry.getValue(), categoryName, entry.getKey());
        }
    }
    
    /**
     * 將圖表儲存為JPG或PNG檔案
     * @param chart 要保存的圖表
     * @param filePath 檔案路徑
     * @param width 圖表寬度
     * @param height 圖表高度
     * @throws IOException 保存圖表時可能發生的IO異常
     */
    public static void saveChartAsImage(JFreeChart chart, String filePath, int width, int height) throws IOException {
        if (filePath.toLowerCase().endsWith(".jpg") || filePath.toLowerCase().endsWith(".jpeg")) {
            ChartUtils.saveChartAsJPEG(new File(filePath), chart, width, height);
        } else if (filePath.toLowerCase().endsWith(".png")) {
            ChartUtils.saveChartAsPNG(new File(filePath), chart, width, height);
        } else {
            // 預設保存為PNG
            ChartUtils.saveChartAsPNG(new File(filePath + ".png"), chart, width, height);
        }
    }

    /**
     * 設置圖表字體
     * @param chart 要設置字體的圖表
     */
    private static void setChartFont(JFreeChart chart) {
        // 設置標題字體
        Font titleFont = new Font("Microsoft JhengHei", Font.BOLD, 16);
        chart.getTitle().setFont(titleFont);
        
        // 設置圖例字體
        Font legendFont = new Font("Microsoft JhengHei", Font.PLAIN, 12);
        chart.getLegend().setItemFont(legendFont);
        
        // 如果是類別圖表，設置軸標籤字體
        if (chart.getPlot() instanceof CategoryPlot) {
            CategoryPlot categoryPlot = (CategoryPlot) chart.getPlot();
            Font axisFont = new Font("Microsoft JhengHei", Font.PLAIN, 12);
            categoryPlot.getDomainAxis().setLabelFont(axisFont);
            categoryPlot.getDomainAxis().setTickLabelFont(axisFont);
            categoryPlot.getRangeAxis().setLabelFont(axisFont);
            categoryPlot.getRangeAxis().setTickLabelFont(axisFont);
        } 
        // 設置餅圖字體
        else if (chart.getPlot() instanceof org.jfree.chart.plot.PiePlot) {
            org.jfree.chart.plot.PiePlot piePlot = (org.jfree.chart.plot.PiePlot) chart.getPlot();
            piePlot.setLabelFont(new Font("Microsoft JhengHei", Font.PLAIN, 12));
        }
    }

    /**
     * 填充缺失的月份，確保時間序列連續
     * @param monthlyData 月度數據映射
     * @param earliestMonth 最早的月份 (yyyy/MM 格式)
     * @param latestMonth 最晚的月份 (yyyy/MM 格式)
     */
    private static void fillMissingMonths(Map<String, Integer> monthlyData, String earliestMonth, String latestMonth) {
        try {
            // 解析年月
            String[] earliestParts = earliestMonth.split("/");
            String[] latestParts = latestMonth.split("/");
            
            if (earliestParts.length < 2 || latestParts.length < 2) return;
            
            int startYear = Integer.parseInt(earliestParts[0]);
            int startMonth = Integer.parseInt(earliestParts[1]);
            int endYear = Integer.parseInt(latestParts[0]);
            int endMonth = Integer.parseInt(latestParts[1]);
            
            // 對於2000年1月前的日期，從2000年1月開始
            if (startYear < 2000) {
                startYear = 2000;
                startMonth = 1;
            }
            
            // 檢查開始和結束日期的有效性
            if (startYear > endYear || (startYear == endYear && startMonth > endMonth)) {
                return; // 無效的日期範圍
            }
            
            // 填充所有缺失的月份
            for (int year = startYear; year <= endYear; year++) {
                int monthStart = (year == startYear) ? startMonth : 1;
                int monthEnd = (year == endYear) ? endMonth : 12;
                
                for (int month = monthStart; month <= monthEnd; month++) {
                    String yearMonth = String.format("%d/%02d", year, month);
                    if (!monthlyData.containsKey(yearMonth)) {
                        monthlyData.put(yearMonth, 0);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("填充月份時發生錯誤: " + e.getMessage());
        }
    }
} 