import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// 為了獲取當日日期
import java.util.Date;

// JFreeChart 相關的 import
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class AccountGUI {
    // GUI 元件與變數定義
    private JFrame frame;
    private JPanel panel;
    private AccountList accountList;   // 儲存帳目資料的容器
    private Account account;           // 暫存使用者輸入的帳目
    private JButton enterbutton, displaybutton, queryByDateButton, deleteByDateButton, deletebutton, searchByNoteButton, statsButton;
    private JTextArea area;            // 顯示訊息的文字區域
    private JTextField datefield, breakfastfield, lunchfield, dinnerfield, othersfield, notefield;
    private StreamHelper streamhelper; // 負責檔案讀寫的工具
    private JMenu menu;

    public void buildGUI() { 
        frame = new JFrame("記帳小幫手");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        accountList = new AccountList();
        streamhelper = new StreamHelper();

        // 主面板使用 BorderLayout
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 中央區域（左側輸入 + 中間欄位 + 右側按鈕）
        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        // 左側 label
        JPanel leftPanel = new JPanel(new GridLayout(7, 1, 5, 5));
        Font font = new Font("Microsoft JhengHei", Font.PLAIN, 14);
        leftPanel.add(createLabel("日期（格式為YYYY/MM/DD）：", font));
        leftPanel.add(createLabel("早餐支出：", font));
        leftPanel.add(createLabel("午餐支出：", font));
        leftPanel.add(createLabel("晚餐支出：", font));
        leftPanel.add(createLabel("其他支出：", font));
        leftPanel.add(createLabel("帳目備註（若空白則視為無）：", font));
        leftPanel.add(createLabel("若要修改帳目，重新輸入後儲存即可", font));

        // 中間輸入欄位
        JPanel inputPanel = new JPanel(new GridLayout(7, 1, 5, 5));
        datefield = new JTextField();
        breakfastfield = new JTextField();
        lunchfield = new JTextField();
        dinnerfield = new JTextField();
        othersfield = new JTextField();
        notefield = new JTextField();
        inputPanel.add(datefield);
        inputPanel.add(breakfastfield);
        inputPanel.add(lunchfield);
        inputPanel.add(dinnerfield);
        inputPanel.add(othersfield);
        inputPanel.add(notefield);
        enterbutton = new JButton("儲存帳目");
        inputPanel.add(enterbutton);

        // 右側功能按鈕
        JPanel rightPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        displaybutton = new JButton("列出所有帳目資料");
        queryByDateButton = new JButton("查詢指定日期帳目");
        deleteByDateButton = new JButton("刪除指定日期帳目");
        deletebutton = new JButton("清除所有帳目資料");
        searchByNoteButton = new JButton("查詢備註的關鍵字");
        statsButton = new JButton("查看所有帳目統計");
        rightPanel.add(displaybutton);
        rightPanel.add(queryByDateButton);
        rightPanel.add(deleteByDateButton);
        rightPanel.add(deletebutton);
        rightPanel.add(searchByNoteButton);
        rightPanel.add(statsButton);

        // 加入到中央 panel
        centerPanel.add(leftPanel);
        centerPanel.add(inputPanel);
        centerPanel.add(rightPanel);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 中間區域四周留白

        // 上方區域為文字區域
        area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setForeground(Color.BLACK);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(null);

        JScrollPane scroller = new JScrollPane(area,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setBorder(null);

        // 這行顯示歡迎訊息
        area.setText("👋 歡迎使用《記帳小幫手》！\n請輸入今日的支出資料，並點擊『儲存帳目』開始記錄！");

        // 使用 JSplitPane 來分割上面和下面的區域
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroller, centerPanel);
        splitPane.setResizeWeight(0.5); // 調整時讓兩個區域的大小比例為 1:1
        splitPane.setDividerLocation(250); // 直接設定分隔線為一半高度
        splitPane.setDividerSize(5); // 分隔線寬度
        panel.add(splitPane, BorderLayout.CENTER);

        // ===== 選單列 =====
        frame.setJMenuBar(createMenuBar());

        // ===== 註冊按鈕監聽器 =====
        enterbutton.addActionListener(new EnterListener());
        displaybutton.addActionListener(new DisplayListener());
        queryByDateButton.addActionListener(new QueryByDateListener());
        deleteByDateButton.addActionListener(new DeleteByDateListener());
        deletebutton.addActionListener(new DeleteListener());
        searchByNoteButton.addActionListener(new SearchByNoteListener());
        statsButton.addActionListener(new StatsButtonListener());

        // 加入主 panel
        frame.getContentPane().add(panel);
        frame.setSize(800, 600);  // 設定精確的初始大小
        frame.setMinimumSize(new Dimension(800, 600));  // 設定最小尺寸
        frame.setLocationRelativeTo(null); // 這行讓視窗顯示在螢幕中央
        frame.setVisible(true);

        // 顯示每日小語
        showDailyQuote();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        Font menuFont = new Font("Microsoft JhengHei", Font.PLAIN, 16);

        menu = new JMenu("檔案管理");
        menu.setFont(menuFont);

        JMenuItem saveMenuItem = new JMenuItem("另存帳目新檔");
        JMenuItem loadMenuItem = new JMenuItem("讀取帳目檔案");
        saveMenuItem.setFont(menuFont);
        loadMenuItem.setFont(menuFont);

        saveMenuItem.addActionListener(new SaveMenuListener());
        loadMenuItem.addActionListener(new LoadMenuListener());

        menu.add(saveMenuItem);
        menu.add(loadMenuItem);
        menuBar.add(menu);
        menuBar.add(Box.createHorizontalGlue());

        JLabel dateLabel = new JLabel(getCurrentDate());
        dateLabel.setFont(menuFont);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5)); // 讓日期離右邊界5px
        menuBar.add(dateLabel);

        return menuBar;
    }

    // 建立標籤的輔助方法
    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    // 顯示每日小語
    private void showDailyQuote() {
        String[] quotes = {
            "💡 每一筆花費，都是給未來的自己的一封信。",
            "💪 小錢不省，大錢難存。",
            "📘 理財不是有錢人的專利，而是每個人的責任。",
            "💰 記帳是與自己財務對話的開始。",
            "🌱 積少成多，從每天的記帳開始。",
            "🧠 花錢前多想五秒，省錢一整天。",
            "📊 錢要花得安心，記帳是關鍵。",
            "💬 財富不是賺來的，是管來的。"
        };

        int index = (int)(Math.random() * quotes.length);
        JOptionPane.showMessageDialog(frame, quotes[index], "📢 每日小語", JOptionPane.INFORMATION_MESSAGE);
    }

    // 取得當前日期
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(new Date());
    }

    // 輸入帳目按鈕：檢查欄位並建立帳目物件
    public class EnterListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            StringBuilder errorMsg = new StringBuilder();
            String date = datefield.getText().trim();

            if (!DateUtils.isValidDate(date)) {
                errorMsg.append("❌ 日期格式為空或為無效日期（請輸入：YYYY/MM/DD）\n");
            } else if (DateUtils.isFutureDate(date)) {
                errorMsg.append("❌ 不可以輸入未來的日期\n");
            }

            String[] labels = { "早餐", "午餐", "晚餐", "其他" };
            JTextField[] fields = { breakfastfield, lunchfield, dinnerfield, othersfield };
            int[] values = new int[4];

            for (int i = 0; i < 4; i++) {
                String text = fields[i].getText().trim();
                if (text.isEmpty()) {
                    errorMsg.append("❌ " + labels[i] + "金額不能為空\n");
                    continue;
                }
                try {
                    values[i] = Integer.parseInt(text);
                    if (values[i] < 0) {
                        errorMsg.append("❌ " + labels[i] + "金額不能為負數\n");
                    }
                } catch (NumberFormatException e) {
                    errorMsg.append("❌ " + labels[i] + "金額格式錯誤（請輸入有效整數）\n");
                }
            }

            // 取得備註
            String note = notefield.getText().trim();
            if (note.isEmpty()) {
                note = "無";
            }

            if (errorMsg.length() > 0) {
                area.setText(errorMsg.toString());
                return;
            }

            // 如果通過驗證，設定變數
            int breakfast = values[0];
            int lunch = values[1];
            int dinner = values[2];
            int others = values[3];

            // 檢查是否已有相同日期的帳目
            boolean accountExists = false;
            for (int i = 0; i < accountList.size(); i++) {
                Account existingAccount = accountList.get(i);

                if (existingAccount.getDate().equals(date)) {
                    // 如果已經有相同日期的帳目，更新該帳目
                    existingAccount.setBreakfast(breakfast);
                    existingAccount.setLunch(lunch);
                    existingAccount.setDinner(dinner);
                    existingAccount.setOthers(others);
                    existingAccount.setNote(note);
                    area.setText("✅ 帳目已更新！ 日期：" + date);
                    accountExists = true;
                    break;
                }
            }

            // 如果沒有相同日期的帳目，則新增一筆帳目
            if (!accountExists) {
                account = new Account(breakfast, lunch, dinner, others, date, note);
                accountList.add(account);
                account = null;
                area.setText("✅ 帳目建立成功！");
            }

            // 清空輸入欄位
            datefield.setText("");
            breakfastfield.setText("");
            lunchfield.setText("");
            dinnerfield.setText("");
            othersfield.setText("");
            notefield.setText("");
        }
    }

    // 顯示所有帳目資料
    public class DisplayListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            if (accountList.size() > 0) {
                area.setText("所有帳目資料（依日期排序）：\n\n");

                // 複製一份並排序，不改變原本 list
                List<Account> sortedList = new ArrayList<>(accountList.getAll());
                Collections.sort(sortedList, Comparator.comparing(Account::getDate));

                for (Account acc : sortedList) {
                    area.append(acc.printAccount() + "\n\n");
                }
            } else {
                area.setText("⚠️ 目前沒有任何帳目資料");
            }
        }
    }

    // 查詢特定日期的帳目資料
    public class QueryByDateListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            String date = JOptionPane.showInputDialog(frame, "請輸入查詢日期（格式：YYYY/MM/DD）：", "查詢視窗", JOptionPane.QUESTION_MESSAGE);

            if (date == null) return; // 使用者取消

            if (!DateUtils.isValidDate(date)) {
                JOptionPane.showMessageDialog(frame, "❌ 日期為空或為無效日期，請輸入：YYYY/MM/DD", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (DateUtils.isFutureDate(date)) {
                JOptionPane.showMessageDialog(frame, "❌ 不可以輸入未來的日期\n", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (int i = 0; i < accountList.size(); i++) {
                Account acc = accountList.get(i);

                if (acc.getDate().equals(date)) {
                    area.setText("🔎 查詢結果：\n\n" + acc.printAccount());
                    return; // 茶道並顯示後，結束迴圈
                }
            }

            area.setText("⚠️ 查無 " + date + " 的帳目資料");
        }
    }

    // 刪除特定日期的帳目資料
    public class DeleteByDateListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            String date = JOptionPane.showInputDialog(frame, "請輸入要刪除的日期（格式：YYYY/MM/DD）：", "刪除視窗", JOptionPane.QUESTION_MESSAGE);

            if (date == null) return; // 使用者取消
            
            if (!DateUtils.isValidDate(date)) {
                JOptionPane.showMessageDialog(frame, "❌ 日期為空或為無效日期，請輸入：YYYY/MM/DD", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (DateUtils.isFutureDate(date)) {
                JOptionPane.showMessageDialog(frame, "❌ 不可以輸入未來的日期\n", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (int i = 0; i < accountList.size(); i++) {
                Account acc = accountList.get(i);

                if (acc.getDate().equals(date)) {
                    int confirm = JOptionPane.showConfirmDialog(
                            frame,
                            "確定要刪除 " + date + " 的帳目資料嗎？",
                            "確認刪除",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        accountList.remove(i);
                        area.setText("✅ 已刪除 " + date + " 的帳目資料");
                    } else {
                        area.setText("❌ 取消刪除操作");
                    }
                    return; // 找到並刪除後，結束迴圈
                }
            }

            area.setText("⚠️ 無法刪除，查無 " + date + " 的帳目資料");
        }
    }

    // 清除所有帳目資料
    public class DeleteListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            int confirm = JOptionPane.showConfirmDialog(
                frame,
                "確定要刪除所有帳目資料嗎？",
                "確認清除",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                accountList.clear();
                area.setText("✅ 所有帳目資料已清除");
            } else {
                area.setText("❌ 取消所有帳目資料刪除操作");
            }
        }
    }

    // 尋找備註中是否有關鍵字
    public class SearchByNoteListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            String keyword = JOptionPane.showInputDialog(frame, "請輸入備註關鍵字：", "查詢備註", JOptionPane.QUESTION_MESSAGE);

            if (keyword == null) return; // 使用者取消

            if (keyword.trim().isEmpty()) {
                area.setText("⚠️ 請輸入有效的關鍵字！");
                return;
            }

            keyword = keyword.trim();
            List<Account> matchedAccounts = new ArrayList<>();

            // 先找出符合條件的帳目
            for (int i = 0; i < accountList.size(); i++) {
                Account acc = accountList.get(i);

                if (acc.getNote() != null && acc.getNote().contains(keyword)) {
                    matchedAccounts.add(acc);
                }
            }

            if (matchedAccounts.isEmpty()) {
                area.setText("❌ 沒有找到備註中包含關鍵字「" + keyword + "」的帳目。");
            } else {
                // 依照日期排序
                Collections.sort(matchedAccounts, Comparator.comparing(Account::getDate));

                StringBuilder result = new StringBuilder();
                for (Account acc : matchedAccounts) {
                    result.append(acc.printAccount()).append("\n\n");
                }

                area.setText("🔍 查詢結果如下（包含關鍵字：「" + keyword + "」）：\n\n" + result);
            }
        }
    }

    // 統計按鈕 - 開啟新視窗提供年/月統計
    public class StatsButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            JFrame statsFrame = new JFrame("統計分析");
            statsFrame.setSize(300, 150);
            statsFrame.setLocationRelativeTo(frame);
            statsFrame.setLayout(new GridBagLayout()); // 改為 GridBagLayout

            JButton yearButton = new JButton("查詢某年");
            JButton monthButton = new JButton("查詢某年某月");

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0; // 同一列
            gbc.insets = new Insets(10, 10, 10, 10); // 四周留白

            // 加入第一個按鈕（在第 0 欄）
            gbc.gridx = 0;
            statsFrame.add(yearButton, gbc);

            // 加入第二個按鈕（在第 1 欄）
            gbc.gridx = 1;
            statsFrame.add(monthButton, gbc);

            // 年統計查詢邏輯
            yearButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    String year = JOptionPane.showInputDialog(statsFrame, "請輸入年份（例如：2025）");

                    if (year == null) return; // 按下取消或關閉

                    if (!DateUtils.isValidYear(year)) {
                        JOptionPane.showMessageDialog(statsFrame, "❌ 輸入為空或不是有效年份，請輸入 4 位數的有效年份，例如：2025", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int breakfastTotal = 0, lunchTotal = 0, dinnerTotal = 0, othersTotal = 0, total = 0;

                    double[] monthlyTotals = new double[12]; // 用來存每月支出總和

                    for (int i = 0; i < accountList.size(); i++) {
                        Account acc = accountList.get(i);
                        if (acc.getDate().startsWith(year)) {
                            breakfastTotal += acc.getBreakfast();
                            lunchTotal += acc.getLunch();
                            dinnerTotal += acc.getDinner();
                            othersTotal += acc.getOthers();
                            total += acc.getTotal();

                            // 取月份（假設格式是 yyyy/MM/dd），畫圖用到的
                            String[] parts = acc.getDate().split("/");
                            if (parts.length >= 2) {
                                int monthIndex = Integer.parseInt(parts[1]) - 1;
                                if (monthIndex >= 0 && monthIndex < 12) {
                                    monthlyTotals[monthIndex] += acc.getTotal();
                                }
                            }
                        }
                    }

                    String statsMessage = year + "年統計：\n"
                            + "早餐總支出：" + breakfastTotal + " 元\n"
                            + "午餐總支出：" + lunchTotal + " 元\n"
                            + "晚餐總支出：" + dinnerTotal + " 元\n"
                            + "其他總支出：" + othersTotal + " 元\n"
                            + "總支出：" + total + " 元";
                    JOptionPane.showMessageDialog(statsFrame, statsMessage);

                    // === 建立「每月總支出」的圖表 ===
                    DefaultCategoryDataset monthlyDataset = new DefaultCategoryDataset();
                    String monthlySeriesName = "每月支出";
                    String[] monthNames = { "1月", "2月", "3月", "4月", "5月", "6月", 
                                            "7月", "8月", "9月", "10月", "11月", "12月" };

                    for (int i = 0; i < 12; i++) {
                        monthlyDataset.addValue(monthlyTotals[i], monthlySeriesName, monthNames[i]);
                    }

                    JFreeChart monthlyChart = ChartFactory.createBarChart(
                        year + "年每月總支出",
                        "月份",
                        "金額（元）",
                        monthlyDataset,
                        PlotOrientation.VERTICAL,
                        false, true, false
                    );

                    ChartPanel monthlyChartPanel = new ChartPanel(monthlyChart);

                    // === 建立「餐別總支出」的圖表 ===
                    DefaultCategoryDataset mealDataset = new DefaultCategoryDataset();
                    String mealSeriesName = "各類別支出";

                    mealDataset.addValue(breakfastTotal, mealSeriesName, "早餐");
                    mealDataset.addValue(lunchTotal, mealSeriesName, "午餐");
                    mealDataset.addValue(dinnerTotal, mealSeriesName, "晚餐");
                    mealDataset.addValue(othersTotal, mealSeriesName, "其他");

                    JFreeChart mealChart = ChartFactory.createBarChart(
                        year + "年各類別總支出",
                        "類別",
                        "金額（元）",
                        mealDataset,
                        PlotOrientation.VERTICAL,
                        false, true, false
                    );

                    ChartPanel mealChartPanel = new ChartPanel(mealChart);

                    // === 建立視窗，同時顯示兩張圖表 ===
                    JPanel chartsPanel = new JPanel(new GridLayout(1, 2)); // 1 列 2 欄的格狀版面
                    chartsPanel.add(monthlyChartPanel);
                    chartsPanel.add(mealChartPanel);

                    JFrame chartFrame = new JFrame(year + "年支出圖表");
                    chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    chartFrame.add(chartsPanel);
                    chartFrame.setSize(1000, 500);
                    chartFrame.setLocationRelativeTo(statsFrame);
                    chartFrame.setVisible(true);
                }
            });

            // 月統計查詢邏輯
            monthButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    int day;
                    String month = JOptionPane.showInputDialog(statsFrame, "請輸入年份和月份（格式：YYYY/MM）");

                    if (month == null) return;

                    day = DateUtils.getDaysInYearMonth(month);

                    if (day == 0) {
                        JOptionPane.showMessageDialog(statsFrame, "❌ 輸入為空或不是有效年月份，請輸入有效的年月份，例如：2025/05", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int breakfastTotal = 0, lunchTotal = 0, dinnerTotal = 0, othersTotal = 0, total = 0;

                    for (int i = 0; i < accountList.size(); i++) {
                        Account acc = accountList.get(i);
                        if (acc.getDate().startsWith(month)) {
                            breakfastTotal += acc.getBreakfast();
                            lunchTotal += acc.getLunch();
                            dinnerTotal += acc.getDinner();
                            othersTotal += acc.getOthers();
                            total += acc.getTotal();
                        }
                    }

                    String statsMessage = month + " 統計：\n"
                            + "早餐總支出：" + breakfastTotal + " 元\n"
                            + "午餐總支出：" + lunchTotal + " 元\n"
                            + "晚餐總支出：" + dinnerTotal + " 元\n"
                            + "其他總支出：" + othersTotal + " 元\n"
                            + "總支出：" + total + " 元";
                    JOptionPane.showMessageDialog(statsFrame, statsMessage);

                    // === 準備每日支出的折線圖資料集 ===

                    DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
                    String lineSeries = " 每日總支出";

                    for (int d = 1; d <= day; d++) {
                        String dayStr = String.format("%02d", d); // 補零，例如 01、02
                        String targetDatePrefix = month + "/" + dayStr;
                        int dailyTotal = 0;

                        for (int i = 0; i < accountList.size(); i++) {
                            Account acc = accountList.get(i);
                            if (acc.getDate().startsWith(targetDatePrefix)) {
                                dailyTotal += acc.getTotal();
                            }
                        }

                        lineDataset.addValue(dailyTotal, lineSeries, String.valueOf(d)); // x 軸是 "1", "2", ...
                    }

                    JFreeChart lineChart = ChartFactory.createLineChart(
                        month + " 每日總支出",
                        "日",
                        "金額（元）",
                        lineDataset,
                        PlotOrientation.VERTICAL,
                        false, true, false
                    );

                    // 取得 plot，並設定 renderer 顯示圖形（資料點）
                    CategoryPlot plot = lineChart.getCategoryPlot();
                    LineAndShapeRenderer renderer = new LineAndShapeRenderer();

                    // 設定第一條線：顯示形狀、填滿形狀
                    renderer.setSeriesShapesVisible(0, true);
                    renderer.setSeriesShapesFilled(0, true);
                    renderer.setSeriesToolTipGenerator(0, new StandardCategoryToolTipGenerator());

                    // 將 renderer 套用到圖表
                    plot.setRenderer(renderer);

                    ChartPanel lineChartPanel = new ChartPanel(lineChart);

                    // === 準備餐別支出的長條圖資料集 ===
                    DefaultCategoryDataset mealDataset = new DefaultCategoryDataset();
                    String mealSeries = " 各類別總支出";

                    mealDataset.addValue(breakfastTotal, mealSeries, "早餐");
                    mealDataset.addValue(lunchTotal, mealSeries, "午餐");
                    mealDataset.addValue(dinnerTotal, mealSeries, "晚餐");
                    mealDataset.addValue(othersTotal, mealSeries, "其他");

                    JFreeChart mealChart = ChartFactory.createBarChart(
                        month + " 各類別總支出",
                        "類別",
                        "金額（元）",
                        mealDataset,
                        PlotOrientation.VERTICAL,
                        false, true, false
                    );
                    ChartPanel mealChartPanel = new ChartPanel(mealChart);

                    // === 建立面板，顯示兩張圖 ===
                    JPanel chartsPanel = new JPanel(new GridLayout(1, 2));
                    chartsPanel.add(lineChartPanel);
                    chartsPanel.add(mealChartPanel);

                    JFrame chartFrame = new JFrame(month + " 支出圖表");
                    chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    chartFrame.add(chartsPanel);
                    chartFrame.setSize(1200, 500);
                    chartFrame.setLocationRelativeTo(statsFrame);
                    chartFrame.setVisible(true);
                }
            });

            statsFrame.setVisible(true);
        }
    }

    // 檔案選單 - 儲存帳目到新檔案
    public class SaveMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            JFileChooser filechooser = new JFileChooser();

            if (filechooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                streamhelper.saveFile(accountList, filechooser.getSelectedFile());
                area.setText("✅ 帳目檔案已儲存");
            }
        }
    }

    // 檔案選單 - 從檔案讀取帳目
    public class LoadMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            JFileChooser filechooser = new JFileChooser();

            if (filechooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                accountList = streamhelper.loadFile(filechooser.getSelectedFile());
                area.setText("✅ 帳目檔案載入完成");
            }
        }
    }

    // 主程式進入點
    public static void main(String[] args) {
        AccountGUI gui = new AccountGUI();
        gui.buildGUI();
    }
}
