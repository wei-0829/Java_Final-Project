import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Date;

// JFreeChart 相關的 import
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

// JDateChooser 相關的 import
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JYearChooser;
import com.toedter.calendar.JMonthChooser;

//資料匯出相關的 import
import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class AccountGUI {
    // GUI 元件與變數定義
    private AccountList accountList;   // 儲存帳目資料的容器
    private Account account;           // 暫存使用者輸入的帳目
    private JFrame frame;              // 主視窗
    private JPanel panel;              // 主面板
    private JTextArea area;            // 顯示訊息的文字區域
    private JMenu menu;                // 選單列
    private JTextField breakfastfield, lunchfield, dinnerfield, othersfield, incomfield, notefield;
    private JButton enterbutton, displaybutton, queryByDateButton, deleteByDateButton, deletebutton, searchByNoteButton, statsButton;
    private StreamHelper streamhelper; // 負責檔案讀寫的工具
    private JDateChooser dateChooser;  // JDateChooser 用於日期選擇

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
        JPanel leftPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        Font font = new Font("Microsoft JhengHei", Font.PLAIN, 14);
        leftPanel.add(createLabel("帳目日期（格式為YYYY/MM/DD）：", font));
        leftPanel.add(createLabel("早餐支出（若空白則視為0）：", font));
        leftPanel.add(createLabel("午餐支出（若空白則視為0）：", font));
        leftPanel.add(createLabel("晚餐支出（若空白則視為0）：", font));
        leftPanel.add(createLabel("其他支出（若空白則視為0）：", font));
        leftPanel.add(createLabel("額外收入（若空白則視為0）：", font));
        leftPanel.add(createLabel("帳目備註（若空白則視為無）：", font));
        leftPanel.add(createLabel("若要修改帳目，重新輸入後儲存即可", font));

        // 中間輸入欄位
        JPanel inputPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        dateChooser = new JDateChooser();
        breakfastfield = new JTextField();
        lunchfield = new JTextField();
        dinnerfield = new JTextField();
        othersfield = new JTextField();
        incomfield = new JTextField();
        notefield = new JTextField();
        enterbutton = new JButton("儲存帳目");

        inputPanel.add(dateChooser);
        inputPanel.add(breakfastfield);
        inputPanel.add(lunchfield);
        inputPanel.add(dinnerfield);
        inputPanel.add(othersfield);
        inputPanel.add(incomfield);
        inputPanel.add(notefield);
        inputPanel.add(enterbutton);

        // 設定日期選擇器的格式
        dateChooser.setDateFormatString("yyyy/MM/dd");
        dateChooser.setDate(new Date()); // 預設值為今天

        // 設定最小與最大可選日期
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date minDate = sdf.parse("1900/01/01");
            Date maxDate = new Date(); // 今天

            dateChooser.setMinSelectableDate(minDate);
            dateChooser.setMaxSelectableDate(maxDate);
        } catch (ParseException ev) {
            ev.printStackTrace();
        }

        // 右側功能按鈕
        JPanel rightPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        displaybutton = new JButton("列出所有帳目資料");
        queryByDateButton = new JButton("查詢指定日期帳目");
        deleteByDateButton = new JButton("刪除指定日期帳目");
        deletebutton = new JButton("清除所有帳目資料");
        searchByNoteButton = new JButton("查詢備註的關鍵字");
        statsButton = new JButton("查看統計顯示圖表");
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
        area.setEditable(false); // ← 加這一行讓文字區域唯讀
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

    // 建立選單列
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        Font menuFont = new Font("Microsoft JhengHei", Font.PLAIN, 16);

        menu = new JMenu("另存/讀取檔案");
        menu.setFont(menuFont);
        menu.setForeground(Color.BLACK); // 預設顏色

        // 讓檔案管理更顯目
        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menu.setForeground(Color.BLUE); // 滑鼠移上去變藍色
            }

            @Override
            public void mouseExited(MouseEvent e) {
                menu.setForeground(Color.BLACK); // 移開回復黑色
            }
        });

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

    // 儲存帳目到檔案
    public class EnterListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            StringBuilder errorMsg = new StringBuilder();
            
            Date selectedDate = dateChooser.getDate();
            String date = "";

            if (selectedDate == null) {
                errorMsg.append("❌ 日期為空或為無效日期，請輸入：YYYY/MM/DD\n");
            } else {
                date = new SimpleDateFormat("yyyy/MM/dd").format(selectedDate);

                if (DateUtils.isFutureDate(date)) {
                    errorMsg.append("❌ 不可以輸入未來的日期\n");
                }
            }

            String[] labels = { "早餐", "午餐", "晚餐", "其他", "收入" };
            JTextField[] fields = { breakfastfield, lunchfield, dinnerfield, othersfield, incomfield };
            int[] values = new int[5];
            boolean hasNonZero = false;

            // 處理金額欄位
            for (int i = 0; i < 5; i++) {
                String text = fields[i].getText().trim();

                if (text.isEmpty()) {
                    values[i] = 0;  // 空值視為 0 元
                } else {
                    try {
                        values[i] = Integer.parseInt(text);
                        if (values[i] < 0) {
                            errorMsg.append("❌ " + labels[i] + "金額不能為負數\n");
                        } else if (values[i] > 0) {
                            hasNonZero = true; // 有有效金額
                        }
                    } catch (NumberFormatException e) {
                        errorMsg.append("❌ " + labels[i] + "金額格式錯誤（請輸入有效整數）\n");
                    }
                }
            }

            if (!hasNonZero) {
                errorMsg.append("❌ 至少要輸入一個不是 0 的有效金額（早餐、午餐、晚餐、其他、收入）\n");
            }

            // 備註欄位
            String note = notefield.getText().trim();

            if (note.isEmpty()) {
                note = "無";
            }

            if (errorMsg.length() > 0) {
                area.setText(errorMsg.toString());
                return;
            }

            // 所有金額欄位轉換
            int breakfast = values[0];
            int lunch = values[1];
            int dinner = values[2];
            int others = values[3];
            int income = values[4];
            int net = income - (breakfast + lunch + dinner + others);

            // 更新或新增帳目
            boolean accountExists = false;

            for (int i = 0; i < accountList.size(); i++) {
                Account existingAccount = accountList.get(i);

                // 如果日期相同，則更新該筆帳目
                if (existingAccount.getDate().equals(date)) {
                    existingAccount.setBreakfast(breakfast);
                    existingAccount.setLunch(lunch);
                    existingAccount.setDinner(dinner);
                    existingAccount.setOthers(others);
                    existingAccount.setIncome(income);
                    existingAccount.setNet(net);
                    existingAccount.setNote(note);
                    area.setText("✅ 帳目已更新！ 日期：" + date);
                    accountExists = true;
                    break;
                }
            }

            // 如果沒有找到相同日期的帳目，則新增一筆
            if (!accountExists) {
                account = new Account(date, breakfast, lunch, dinner, others, income, net, note);
                accountList.add(account);
                account = null;
                area.setText("✅ 帳目建立成功！");
            }

            // 清空欄位
            breakfastfield.setText("");
            lunchfield.setText("");
            dinnerfield.setText("");
            othersfield.setText("");
            incomfield.setText("");
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
            // 建立日期選擇器
            JDateChooser dateChooser = new JDateChooser();
            dateChooser.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
            dateChooser.setDateFormatString("yyyy/MM/dd");

            // 限制可選日期範圍：1900/01/01 ~ 今天
            Calendar min = Calendar.getInstance();
            min.set(1900, Calendar.JANUARY, 1);

            dateChooser.setMinSelectableDate(min.getTime()); // 最小為 1900/01/01
            dateChooser.setMaxSelectableDate(new Date()); // 最大為今天
            dateChooser.setDate(new Date()); // 預設值為今天

            // 顯示在 JOptionPane 中
            int result = JOptionPane.showConfirmDialog(
                frame,
                dateChooser,
                "請選擇查詢日期",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) return;

            // 取得選擇的日期
            Date selectedDate = dateChooser.getDate();

            if (selectedDate == null) {
                JOptionPane.showMessageDialog(frame, "❌ 日期為空或為無效日期", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 轉換成 yyyy/MM/dd 格式字串
            String date = new SimpleDateFormat("yyyy/MM/dd").format(selectedDate);


            if (DateUtils.isFutureDate(date)) {
                JOptionPane.showMessageDialog(frame, "❌ 不可以輸入未來的日期\n", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 查詢帳目
            for (int i = 0; i < accountList.size(); i++) {
                Account acc = accountList.get(i);
                if (acc.getDate().equals(date)) {
                    area.setText("🔎 查詢結果：\n\n" + acc.printAccount());
                    return;
                }
            }

            area.setText("⚠️ 查無 " + date + " 的帳目資料");
        }
    }

    public class DeleteByDateListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            // 建立日期選擇器
            JDateChooser dateChooser = new JDateChooser();
            dateChooser.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
            dateChooser.setDateFormatString("yyyy/MM/dd");

            // 限制只能選 1900/01/01 ~ 今天
            Calendar min = Calendar.getInstance();
            min.set(1900, Calendar.JANUARY, 1);
            dateChooser.setMinSelectableDate(min.getTime());
            dateChooser.setMaxSelectableDate(new Date());
            dateChooser.setDate(new Date()); // 預設今天

            int result = JOptionPane.showConfirmDialog(
                frame,
                dateChooser,
                "請選擇要刪除的日期",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                Date selectedDate = dateChooser.getDate();

                if (selectedDate == null) {
                    JOptionPane.showMessageDialog(frame, "❌ 日期為空或為無效日期", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String date = new SimpleDateFormat("yyyy/MM/dd").format(selectedDate);

                if (DateUtils.isFutureDate(date)) {
                    JOptionPane.showMessageDialog(frame, "❌ 不可以輸入未來的日期\n", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 查找並刪除資料
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
                        return;
                    }
                }

                area.setText("⚠️ 無法刪除，查無 " + date + " 的帳目資料");
            }
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
                    // 建立年份選擇器
                    JYearChooser yearChooser = new JYearChooser();
                    yearChooser.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
                    yearChooser.setStartYear(1900);
                    yearChooser.setEndYear(Calendar.getInstance().get(Calendar.YEAR));

                    // 放進 JOptionPane
                    int result = JOptionPane.showConfirmDialog(
                        statsFrame,
                        yearChooser,
                        "請選擇年份",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                    );

                    // 如果使用者按下 OK
                    if (result == JOptionPane.OK_OPTION) {
                        int selectedYear = yearChooser.getYear();

                        // 轉成字串可用於查詢或顯示
                        String year = String.valueOf(selectedYear);
                        
                        // 進行統計
                        int breakfastTotal = 0, lunchTotal = 0, dinnerTotal = 0, othersTotal = 0, total = 0, incomeTotal = 0;

                        double[] monthlyTotals = new double[12]; // 用來存每月支出總和
                        double[] monthlyIncome = new double[12]; // 用來存每月收入總和

                        for (int i = 0; i < accountList.size(); i++) {
                            Account acc = accountList.get(i);
                            if (acc.getDate().startsWith(year)) {
                                breakfastTotal += acc.getBreakfast();
                                lunchTotal += acc.getLunch();
                                dinnerTotal += acc.getDinner();
                                othersTotal += acc.getOthers();
                                total += acc.getTotal();
                                incomeTotal += acc.getIncome();

                                // 取月份（假設格式是 yyyy/MM/dd），畫圖用到的
                                String[] parts = acc.getDate().split("/");
                                if (parts.length >= 2) {
                                    int monthIndex = Integer.parseInt(parts[1]) - 1;
                                    if (monthIndex >= 0 && monthIndex < 12) {
                                        monthlyTotals[monthIndex] += acc.getTotal();
                                        monthlyIncome[monthIndex] += acc.getIncome();
                                    }
                                }
                            }
                        }

                        // 顯示統計結果
                        String statsMessage = year + "年統計：\n"
                                + "早餐總支出：" + breakfastTotal + " 元\n"
                                + "午餐總支出：" + lunchTotal + " 元\n"
                                + "晚餐總支出：" + dinnerTotal + " 元\n"
                                + "其他總支出：" + othersTotal + " 元\n"
                                + "全部總支出：" + total + " 元\n"
                                + "額外總收入：" + incomeTotal + " 元\n"
                                + "全部淨支出：" + (incomeTotal - total) + " 元\n";
                                
                        JOptionPane.showMessageDialog(statsFrame, statsMessage, "年統計結果", JOptionPane.INFORMATION_MESSAGE);

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
                            true,                    // 是否顯示圖例
                            true,                    // 是否顯示 tooltip
                            false                    // 是否啟用 URL 功能
                        );

                        ChartPanel monthlyChartPanel = new ChartPanel(monthlyChart);

                        // === 建立「餐別總支出」的圓餅圖 ===
                        DefaultPieDataset pieDataset = new DefaultPieDataset();

                        pieDataset.setValue("早餐", breakfastTotal);
                        pieDataset.setValue("午餐", lunchTotal);
                        pieDataset.setValue("晚餐", dinnerTotal);
                        pieDataset.setValue("其他", othersTotal);

                        JFreeChart pieChart = ChartFactory.createPieChart(
                            year + "年各類別總支出", // 圖表標題
                            pieDataset,              // 資料集
                            true,                    // 是否顯示圖例
                            true,                    // 是否生成提示
                            false                    // 是否生成URL連結
                        );

                        ChartPanel pieChartPanel = new ChartPanel(pieChart);

                        // === 建立「每月收入」長條圖 ===
                        DefaultCategoryDataset incomeDataset = new DefaultCategoryDataset();
                        String incomeSeries = "每月收入";

                        for (int i = 0; i < 12; i++) {
                            incomeDataset.addValue(monthlyIncome[i], incomeSeries, monthNames[i]);
                        }

                        JFreeChart incomeChart = ChartFactory.createBarChart(
                            year + "年每月總收入",
                            "月份",
                            "金額（元）",
                            incomeDataset,
                            PlotOrientation.VERTICAL,
                            true,                    // 是否顯示圖例
                            true,                    // 是否顯示 tooltip
                            false                    // 是否啟用 URL 功能
                        );

                        // 設定長條顏色為藍色
                        CategoryPlot incomePlot = incomeChart.getCategoryPlot();
                        BarRenderer incomeRenderer = (BarRenderer) incomePlot.getRenderer();
                        incomeRenderer.setSeriesPaint(0, new Color(0, 0, 255)); // 收入改成藍色

                        ChartPanel incomeChartPanel = new ChartPanel(incomeChart);

                        // === 建立「收入與支出」折線圖 ===
                        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
                        String expenseLine = "支出";
                        String incomeLine = "收入";

                        for (int i = 0; i < 12; i++) {
                            lineDataset.addValue(monthlyTotals[i], expenseLine, monthNames[i]);
                            lineDataset.addValue(monthlyIncome[i], incomeLine, monthNames[i]);
                        }

                        JFreeChart lineChart = ChartFactory.createLineChart(
                            year + "年每月收入與支出比較",
                            "月份",
                            "金額（元）",
                            lineDataset,
                            PlotOrientation.VERTICAL,
                            true,                    // 是否顯示圖例
                            true,                    // 是否顯示 tooltip
                            false                    // 是否啟用 URL 功能
                        );

                        // 取得 plot，並設定 renderer 顯示圖形（資料點）
                        CategoryPlot plot = lineChart.getCategoryPlot();
                        LineAndShapeRenderer renderer = new LineAndShapeRenderer();

                        // 讓兩條線都顯示圓點、填滿圓點、顯示提示訊息
                        for (int i = 0; i < 2; i++) {
                            renderer.setSeriesShapesVisible(i, true);  // 顯示資料點
                            renderer.setSeriesShapesFilled(i, true);   // 填滿資料點
                            renderer.setSeriesToolTipGenerator(i, new StandardCategoryToolTipGenerator());
                        }

                        // 將 renderer 套用到圖表
                        plot.setRenderer(renderer);

                        ChartPanel lineChartPanel = new ChartPanel(lineChart);

                        // === 建立視窗，同時顯示四張圖表 ===
                        JPanel chartsPanel = new JPanel(new GridLayout(2, 2));
                        chartsPanel.add(monthlyChartPanel);  // 每月支出長條圖
                        chartsPanel.add(pieChartPanel);      // 餐別支出圓餅圖
                        chartsPanel.add(incomeChartPanel);   // 每月收入長條圖
                        chartsPanel.add(lineChartPanel);     // 收支折線圖

                        JFrame chartFrame = new JFrame(year + "年支出圖表");
                        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        chartFrame.add(chartsPanel);
                        chartFrame.setSize(1000, 800);
                        chartFrame.setLocationRelativeTo(statsFrame);
                        chartFrame.setVisible(true);
                    }
                }
            });

            // 月統計查詢邏輯
            monthButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    // 建立年份選擇器
                    JYearChooser yearChooser = new JYearChooser();
                    yearChooser.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
                    yearChooser.setStartYear(1900);
                    yearChooser.setEndYear(Calendar.getInstance().get(Calendar.YEAR));

                    // 建立月份選擇器
                    JMonthChooser monthChooser = new JMonthChooser();
                    monthChooser.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));

                    // 建立一個容器，將兩個選擇器並排顯示
                    JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
                    panel.add(new JLabel("選擇年份："));
                    panel.add(yearChooser);
                    panel.add(new JLabel("選擇月份："));
                    panel.add(monthChooser);

                    // 放進 JOptionPane
                    int result = JOptionPane.showConfirmDialog(
                        statsFrame,
                        panel,
                        "請選擇年份和月份",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                    );

                    // 如果使用者按下 OK 按鈕
                    if (result == JOptionPane.OK_OPTION) {
                        int selectedYear = yearChooser.getYear();
                        int selectedMonth = monthChooser.getMonth() + 1; // 月份從 0 開始，所以 +1

                        // 格式組合成 "YYYY/MM"
                        String month = String.format("%04d/%02d", selectedYear, selectedMonth);

                        int day = DateUtils.getDaysInYearMonth(month);

                        int breakfastTotal = 0, lunchTotal = 0, dinnerTotal = 0, othersTotal = 0, total = 0, incomeTotal = 0;

                        for (int i = 0; i < accountList.size(); i++) {
                            Account acc = accountList.get(i);
                            if (acc.getDate().startsWith(month)) {
                                breakfastTotal += acc.getBreakfast();
                                lunchTotal += acc.getLunch();
                                dinnerTotal += acc.getDinner();
                                othersTotal += acc.getOthers();
                                total += acc.getTotal();
                                incomeTotal += acc.getIncome();
                            }
                        }

                        String statsMessage = month + " 統計：\n"
                                + "早餐總支出：" + breakfastTotal + " 元\n"
                                + "午餐總支出：" + lunchTotal + " 元\n"
                                + "晚餐總支出：" + dinnerTotal + " 元\n"
                                + "其他總支出：" + othersTotal + " 元\n"
                                + "全部總支出：" + total + " 元\n"
                                + "額外總收入：" + incomeTotal + " 元\n"
                                + "全部淨支出：" + (incomeTotal - total) + " 元\n";

                        JOptionPane.showMessageDialog(statsFrame, statsMessage, "月統計結果", JOptionPane.INFORMATION_MESSAGE);

                        // === 準備每日收入與支出的折線圖資料集 ===
                        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
                        String expenseLine = "每日支出";
                        String incomeLine = "每日收入";

                        for (int d = 1; d <= day; d++) {
                            String dayStr = String.format("%02d", d); // 補零 01~31
                            String targetDatePrefix = month + "/" + dayStr;
                            int dailyTotal = 0;
                            int dailyIncome = 0;

                            for (int i = 0; i < accountList.size(); i++) {
                                Account acc = accountList.get(i);
                                if (acc.getDate().startsWith(targetDatePrefix)) {
                                    dailyTotal += acc.getTotal();
                                    dailyIncome += acc.getIncome();
                                }
                            }

                            String xLabel = String.valueOf(d); // x軸標籤：1~31
                            lineDataset.addValue(dailyTotal, expenseLine, xLabel);
                            lineDataset.addValue(dailyIncome, incomeLine, xLabel);
                        }

                        JFreeChart lineChart = ChartFactory.createLineChart(
                            month + " 每日收入與支出",
                            "日",
                            "金額（元）",
                            lineDataset,
                            PlotOrientation.VERTICAL,
                            true,
                            true,
                            false
                        );

                        CategoryPlot plot = lineChart.getCategoryPlot();
                        LineAndShapeRenderer renderer = new LineAndShapeRenderer();

                        for (int i = 0; i < 2; i++) {
                            renderer.setSeriesShapesVisible(i, true);
                            renderer.setSeriesShapesFilled(i, true);
                            renderer.setSeriesToolTipGenerator(i, new StandardCategoryToolTipGenerator());
                        }

                        plot.setRenderer(renderer);

                        ChartPanel lineChartPanel = new ChartPanel(lineChart);

                        // === 準備餐別支出的圓餅圖資料集 ===
                        DefaultPieDataset pieDataset = new DefaultPieDataset();
                        pieDataset.setValue("早餐", breakfastTotal);
                        pieDataset.setValue("午餐", lunchTotal);
                        pieDataset.setValue("晚餐", dinnerTotal);
                        pieDataset.setValue("其他", othersTotal);

                        JFreeChart pieChart = ChartFactory.createPieChart(
                            month + " 各類別總支出",
                            pieDataset,
                            true,
                            true,
                            false
                        );

                        ChartPanel pieChartPanel = new ChartPanel(pieChart);

                        // === 建立面板，顯示兩張圖 ===
                        JPanel chartsPanel = new JPanel(new GridLayout(1, 2));
                        chartsPanel.add(lineChartPanel);
                        chartsPanel.add(pieChartPanel);

                        JFrame chartFrame = new JFrame(month + " 支出圖表");
                        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        chartFrame.add(chartsPanel);
                        chartFrame.setSize(1200, 500);
                        chartFrame.setLocationRelativeTo(statsFrame);
                        chartFrame.setVisible(true);
                    }
                }
            });

            statsFrame.setVisible(true);
        }
    }

    // 檔案選單 - 儲存帳目到新檔案
    public class SaveMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            JFileChooser filechooser = new JFileChooser();

            //用filter建立可儲存的檔案類型選項，以及用此選項辨別如何處理資料
            FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV 檔案 (*.csv)", "csv");
            FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("TXT 檔案 (*.txt)", "txt");
            filechooser.addChoosableFileFilter(csvFilter);
            filechooser.addChoosableFileFilter(txtFilter);
            filechooser.setAcceptAllFileFilterUsed(true);
            
            // 設定預設檔名
            if (filechooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = filechooser.getSelectedFile();
                FileNameExtensionFilter filter = (FileNameExtensionFilter)filechooser.getFileFilter();
                String extension = filter.getExtensions()[0];

                // 如果使用者沒有手動加上 副檔名，幫他補上
                if (!selectedFile.getName().toLowerCase().endsWith("." + extension)) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + "." + extension);
                }

                // 儲存檔案
                // 根據選擇的檔案類型，呼叫不同的儲存方法
                switch(extension){
                    case "csv":
                        streamhelper.saveFileCsv(accountList, selectedFile);
                        break;
                    case "txt":
                        streamhelper.saveFileTxt(accountList, selectedFile);
                        break;
                    default:
                        streamhelper.saveFileAll(accountList, selectedFile);
                        break;
                }
                area.setText("✅ 帳目檔案已儲存");
            }
        }
    }

    // 檔案選單 - 從檔案讀取帳目
    public class LoadMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            JFileChooser filechooser = new JFileChooser();
            
            // 用 filter 建立可讀取的檔案類型選項，以及用此選項辨別如何處理資料
            if (filechooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File file = filechooser.getSelectedFile();
                String fileName = file.getName();
                String extension = fileName.contains(".")? fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase() : null;

                // 如果使用者沒有手動加上副檔名，幫他補上
                switch(extension){
                    case "csv":
                        accountList = streamhelper.loadFileCsv(filechooser.getSelectedFile());
                        break;
                    case "txt":
                        accountList = streamhelper.loadFileTxt(filechooser.getSelectedFile());
                        break;
                    default:
                        accountList = streamhelper.loadFileAll(filechooser.getSelectedFile());
                        break;
                }
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
