import java.awt.*;
import java.io.File;
import java.lang.reflect.*;
import javax.swing.*;

/**
 * 增強版圖表面板類，使用反射動態加載JFreeChart庫
 * 支持多種圖表類型和文字說明
 */
public class SimpleChartPanel {
    
    /**
     * 創建並顯示一個圖表分析窗口
     * @param parent 父窗口
     * @param accountListObj 帳目列表對象
     */
    public static void showChartDialog(JFrame parent, Object accountListObj) {
        try {
            // 檢查JFreeChart庫是否可用
            Class.forName("org.jfree.chart.JFreeChart");
            
            // 使用直接傳入的accountList，不再嘗試反射獲取
            Object accountList = accountListObj;
            if (accountList == null) {
                JOptionPane.showMessageDialog(parent, 
                    "無法獲取帳目數據。請確保已添加一些記帳數據。",
                    "數據錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 創建圖表選擇窗口
            JFrame chartFrame = new JFrame("圖表分析");
            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chartFrame.setSize(900, 600);
            chartFrame.setLocationRelativeTo(parent);
            
            // 使用卡片佈局來切換不同的圖表
            final CardLayout cardLayout = new CardLayout();
            final JPanel cardPanel = new JPanel(cardLayout);
            
            // 創建圖表面板
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            
            // 支出圓餅圖按鈕
            JButton expensePieButton = new JButton("支出分布圓餅圖");
            expensePieButton.addActionListener(e -> {
                try {
                    // 使用反射調用ChartHelper.createExpensePieChart
                    showChartPanel(cardPanel, cardLayout, "expensePie", 
                        invokeStaticMethod(
                            "AccountProgram.ChartHelper", 
                            "createExpensePieChart", 
                            new Class[]{getClass("AccountProgram.AccountList"), String.class},
                            new Object[]{accountList, "支出分布圓餅圖"}
                        )
                    );
                } catch (Exception ex) {
                    showError(chartFrame, ex);
                }
            });
            
            // 收入圓餅圖按鈕
            JButton incomePieButton = new JButton("收入分布圓餅圖");
            incomePieButton.addActionListener(e -> {
                try {
                    // 使用反射調用ChartHelper.createIncomePieChart
                    showChartPanel(cardPanel, cardLayout, "incomePie", 
                        invokeStaticMethod(
                            "AccountProgram.ChartHelper", 
                            "createIncomePieChart", 
                            new Class[]{getClass("AccountProgram.AccountList"), String.class},
                            new Object[]{accountList, "收入分布圓餅圖"}
                        )
                    );
                } catch (Exception ex) {
                    showError(chartFrame, ex);
                }
            });
            
            // 支出趨勢折線圖按鈕
            JButton expenseLineButton = new JButton("月支出趨勢圖");
            expenseLineButton.addActionListener(e -> {
                try {
                    // 使用反射調用ChartHelper.createMonthlyExpenseLineChart
                    showChartPanel(cardPanel, cardLayout, "expenseLine", 
                        invokeStaticMethod(
                            "AccountProgram.ChartHelper", 
                            "createMonthlyExpenseLineChart", 
                            new Class[]{getClass("AccountProgram.AccountList"), String.class},
                            new Object[]{accountList, "月支出趨勢折線圖"}
                        )
                    );
                } catch (Exception ex) {
                    showError(chartFrame, ex);
                }
            });
            
            // 收入趨勢折線圖按鈕
            JButton incomeLineButton = new JButton("月收入趨勢圖");
            incomeLineButton.addActionListener(e -> {
                try {
                    // 使用反射調用ChartHelper.createMonthlyIncomeLineChart
                    showChartPanel(cardPanel, cardLayout, "incomeLine", 
                        invokeStaticMethod(
                            "AccountProgram.ChartHelper", 
                            "createMonthlyIncomeLineChart", 
                            new Class[]{getClass("AccountProgram.AccountList"), String.class},
                            new Object[]{accountList, "月收入趨勢折線圖"}
                        )
                    );
                } catch (Exception ex) {
                    showError(chartFrame, ex);
                }
            });
            
            // 收支平衡趨勢圖按鈕
            JButton balanceButton = new JButton("收支平衡趨勢圖");
            balanceButton.addActionListener(e -> {
                try {
                    // 使用反射調用ChartHelper.createIncomeExpenseBalanceChart
                    showChartPanel(cardPanel, cardLayout, "balance", 
                        invokeStaticMethod(
                            "AccountProgram.ChartHelper", 
                            "createIncomeExpenseBalanceChart", 
                            new Class[]{getClass("AccountProgram.AccountList"), String.class},
                            new Object[]{accountList, "收支平衡趨勢圖"}
                        )
                    );
                } catch (Exception ex) {
                    showError(chartFrame, ex);
                }
            });
            
            // 添加說明標籤
            JPanel infoPanel = new JPanel(new BorderLayout());
            JLabel infoLabel = new JLabel("<html><b>圖表使用指南</b><br><br>" +
                "• 支出分布圓餅圖：顯示各支出項目的比例<br>" +
                "• 收入分布圓餅圖：顯示各收入來源的比例<br>" +
                "• 月支出趨勢圖：顯示每月總支出的變化趨勢<br>" +
                "• 月收入趨勢圖：顯示每月總收入的變化趨勢<br>" +
                "• 收支平衡趨勢圖：顯示每月收入、支出和收支平衡的對比<br><br>" +
                "提示：點擊上方按鈕切換不同圖表類型</html>");
            infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            infoPanel.add(infoLabel, BorderLayout.CENTER);
            
            // 添加保存按鈕
            JButton saveButton = new JButton("保存圖表為圖片");
            saveButton.addActionListener(e -> saveCurrentChart(chartFrame, accountList));
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.add(saveButton);
            infoPanel.add(bottomPanel, BorderLayout.SOUTH);
            
            // 添加所有按鈕到面板
            buttonPanel.add(expensePieButton);
            buttonPanel.add(incomePieButton);
            buttonPanel.add(expenseLineButton);
            buttonPanel.add(incomeLineButton);
            buttonPanel.add(balanceButton);
            
            // 創建默認的歡迎面板
            JPanel welcomePanel = new JPanel(new BorderLayout());
            welcomePanel.add(new JLabel("<html><div style='text-align: center;'>" +
                "<h1>歡迎使用圖表分析功能</h1>" +
                "<p>請點擊上方按鈕選擇要查看的圖表類型</p>" +
                "</div></html>", JLabel.CENTER), BorderLayout.CENTER);
            cardPanel.add(welcomePanel, "welcome");
            
            // 設置佈局
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(buttonPanel, BorderLayout.NORTH);
            mainPanel.add(cardPanel, BorderLayout.CENTER);
            mainPanel.add(infoPanel, BorderLayout.SOUTH);
            
            chartFrame.add(mainPanel);
            chartFrame.setVisible(true);
            
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(parent, 
                "無法載入JFreeChart圖表庫。\n\n" +
                "請確保lib目錄中有jfreechart-1.5.3.jar和jcommon-1.0.24.jar文件。\n\n" +
                "您可以從以下網址下載: https://www.jfree.org/jfreechart/download/",
                "圖表庫缺失", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, 
                "圖表功能發生錯誤: " + e.getMessage(),
                "錯誤", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * 顯示圖表面板
     */
    private static void showChartPanel(JPanel cardPanel, CardLayout cardLayout, String name, Object chartPanel) {
        // 檢查是否已經添加了這個面板
        boolean found = false;
        Component[] components = cardPanel.getComponents();
        for (Component c : components) {
            if (c.getName() != null && c.getName().equals(name)) {
                found = true;
                break;
            }
        }
        
        if (!found) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setName(name);
            
            // 添加圖表說明文字
            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            textArea.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 12));
            textArea.setBackground(new Color(240, 240, 240));
            textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            // 根據不同圖表類型設置不同的說明文字
            if (name.equals("expensePie")) {
                textArea.setText("【支出分布圓餅圖】\n此圖表顯示各支出類別的占比，讓您清楚了解支出結構。\n" + 
                                "早餐、午餐、晚餐及其他支出各占多少比例，一目了然。");
            } else if (name.equals("incomePie")) {
                textArea.setText("【收入分布圓餅圖】\n此圖表顯示各收入來源的占比，讓您了解收入結構。\n" + 
                                "薪資、投資、獎金及其他收入各占多少比例，清楚明瞭。");
            } else if (name.equals("expenseLine")) {
                textArea.setText("【月支出趨勢圖】\n此圖表顯示每月總支出的變化趨勢，幫助您了解支出模式。\n" + 
                                "通過折線圖可以看出支出是否穩定、有無異常波動，以便及時調整預算。");
            } else if (name.equals("incomeLine")) {
                textArea.setText("【月收入趨勢圖】\n此圖表顯示每月總收入的變化趨勢，幫助您了解收入穩定性。\n" + 
                                "通過折線圖可以看出收入是否穩定增長或有季節性變化，有助於財務規劃。");
            } else if (name.equals("balance")) {
                textArea.setText("【收支平衡趨勢圖】\n此圖表同時顯示每月收入、支出和收支平衡情況。\n" + 
                                "綠線代表收入、紅線代表支出、藍線代表收支平衡。收支平衡為正值表示有結餘，負值表示入不敷出。");
            }
            
            // 創建一個面板包含圖表和說明文字
            panel.add((Component)chartPanel, BorderLayout.CENTER);
            panel.add(textArea, BorderLayout.SOUTH);
            
            cardPanel.add(panel, name);
        }
        
        cardLayout.show(cardPanel, name);
    }
    
    /**
     * 顯示錯誤訊息
     */
    private static void showError(JFrame parent, Exception ex) {
        JOptionPane.showMessageDialog(parent, 
            "生成圖表時發生錯誤: " + ex.getMessage(),
            "圖表錯誤", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
    
    /**
     * 保存當前圖表為圖片
     */
    private static void saveCurrentChart(JFrame parent, Object accountList) {
        try {
            // 創建檔案選擇器
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("保存圖表為PNG圖片");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".png");
                }
                public String getDescription() {
                    return "PNG圖片文件 (*.png)";
                }
            });
            
            if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String path = file.getPath();
                if (!path.toLowerCase().endsWith(".png")) {
                    path += ".png";
                    file = new File(path);
                }
                
                // 顯示選擇圖表類型的對話框
                String[] options = {"支出分布圓餅圖", "收入分布圓餅圖", "月支出趨勢圖", "月收入趨勢圖", "收支平衡趨勢圖"};
                int choice = JOptionPane.showOptionDialog(parent, 
                    "請選擇要保存的圖表類型:", 
                    "選擇圖表", 
                    JOptionPane.DEFAULT_OPTION, 
                    JOptionPane.QUESTION_MESSAGE, 
                    null, options, options[0]);
                
                if (choice >= 0) {
                    // 根據選擇創建相應的圖表
                    Object chart = null;
                    String methodName = "";
                    String title = "";
                    
                    switch (choice) {
                        case 0:
                            methodName = "createExpensePieChart";
                            title = "支出分布圓餅圖";
                            break;
                        case 1:
                            methodName = "createIncomePieChart";
                            title = "收入分布圓餅圖";
                            break;
                        case 2:
                            methodName = "createMonthlyExpenseLineChart";
                            title = "月支出趨勢圖";
                            break;
                        case 3:
                            methodName = "createMonthlyIncomeLineChart";
                            title = "月收入趨勢圖";
                            break;
                        case 4:
                            methodName = "createIncomeExpenseBalanceChart";
                            title = "收支平衡趨勢圖";
                            break;
                    }
                    
                    // 獲取ChartPanel對象
                    Object chartPanel = invokeStaticMethod(
                        "AccountProgram.ChartHelper", 
                        methodName, 
                        new Class[]{getClass("AccountProgram.AccountList"), String.class},
                        new Object[]{accountList, title}
                    );
                    
                    // 獲取JFreeChart對象
                    Method getChartMethod = chartPanel.getClass().getMethod("getChart");
                    Object chart1 = getChartMethod.invoke(chartPanel);
                    
                    // 調用ChartHelper.saveChartAsImage方法
                    invokeStaticMethod(
                        "AccountProgram.ChartHelper",
                        "saveChartAsImage",
                        new Class[]{getClass("org.jfree.chart.JFreeChart"), String.class, int.class, int.class},
                        new Object[]{chart1, path, 800, 600}
                    );
                    
                    JOptionPane.showMessageDialog(parent, 
                        "圖表已成功保存為: " + path,
                        "保存成功", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, 
                "保存圖表時發生錯誤: " + e.getMessage(),
                "錯誤", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * 獲取指定類
     */
    private static Class<?> getClass(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }
    
    /**
     * 通過反射調用靜態方法
     */
    private static Object invokeStaticMethod(String className, String methodName, Class<?>[] paramTypes, Object[] args) throws Exception {
        Class<?> cls = Class.forName(className);
        Method method = cls.getMethod(methodName, paramTypes);
        return method.invoke(null, args);
    }
    
    /**
     * 顯示一個簡單的測試圖表
     */
    private static void showSampleChart() throws Exception {
        // 使用反射避免直接引用JFreeChart類
        Class<?> datasetClass = Class.forName("org.jfree.data.general.DefaultPieDataset");
        Object dataset = datasetClass.getDeclaredConstructor().newInstance();
        
        // 添加數據
        Class<?> pieDatasetClass = Class.forName("org.jfree.data.general.PieDataset");
        Method setValue = datasetClass.getMethod("setValue", Comparable.class, Number.class);
        setValue.invoke(dataset, "早餐", 1000);
        setValue.invoke(dataset, "午餐", 1500);
        setValue.invoke(dataset, "晚餐", 1800);
        setValue.invoke(dataset, "其他", 2000);
        
        // 創建圖表
        Class<?> chartFactoryClass = Class.forName("org.jfree.chart.ChartFactory");
        Method createPieChart = chartFactoryClass.getMethod("createPieChart", 
                                                         String.class, 
                                                         pieDatasetClass, 
                                                         boolean.class, 
                                                         boolean.class, 
                                                         boolean.class);
        Object chart = createPieChart.invoke(null, "支出分布", dataset, true, true, false);
        
        // 創建圖表面板
        Class<?> chartPanelClass = Class.forName("org.jfree.chart.ChartPanel");
        Object chartPanel = chartPanelClass.getDeclaredConstructor(chart.getClass())
                                           .newInstance(chart);
        
        // 顯示圖表
        JFrame chartFrame = new JFrame("支出分布圖");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.setSize(600, 400);
        
        // 將chartPanel添加到畫面
        chartFrame.getContentPane().add((Component)chartPanel);
        chartFrame.setVisible(true);
    }
} 