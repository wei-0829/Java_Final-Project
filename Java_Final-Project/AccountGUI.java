package AccountProgram;

<<<<<<< HEAD
=======
import javax.swing.*;

import AccountProgram.DateUtils;

>>>>>>> parent of 67077ae (Add files via upload)
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*; public class AccountGUI {
    // GUI 元件及資料結構
    private JFrame frame;
    private JPanel panel, inputPanel, datePanel;
    private AccountList accountList;   // 保存帳目資料容器
    private Account account;           // 保存使用者輸入的帳目
    private JButton enterbutton, displaybutton, queryByDateButton, deleteByDateButton, deletebutton, searchByNoteButton, statsButton, chartButton;
    private JTextArea area;            // 顯示訊息的文字區
    private JTextField amountField, notefield;
    private JComboBox<String> typeSelector, categorySelector;
    private JComboBox<Integer> yearSelector, monthSelector, daySelector;
    private StreamHelper streamhelper; // 負責檔案讀寫的工具
    private JMenu menu;

    public void buildGUI() { 
        frame = new JFrame("記帳小幫手");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        accountList = new AccountList();
        streamhelper = new StreamHelper();

        // 主面板使用BorderLayout
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 中央區域：左側標籤 + 中間輸入 + 右側按鈕
        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        // 左側標籤面板
        JPanel leftPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        Font font = new Font("Microsoft JhengHei", Font.PLAIN, 14);
        leftPanel.add(createLabel("日期", font));
        leftPanel.add(createLabel("收支類型", font));
        leftPanel.add(createLabel("項目類別", font));
        leftPanel.add(createLabel("金額", font));
        leftPanel.add(createLabel("備註", font));

        // 建立日期選擇器
        datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        yearSelector = createYearSelector();
        monthSelector = createMonthSelector();
        daySelector = createDaySelector();
        
        // 添加日期選擇組件
        datePanel.add(yearSelector);
        datePanel.add(new JLabel("年"));
        datePanel.add(monthSelector);
        datePanel.add(new JLabel("月"));
        datePanel.add(daySelector);
        datePanel.add(new JLabel("日"));
        
        JButton todayButton = new JButton("今天");
        todayButton.addActionListener(e -> setTodayDate());
        datePanel.add(todayButton);
        
        inputPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        inputPanel.add(datePanel);
        
        // 收支類型選擇
        String[] types = {"支出", "收入"};
        typeSelector = new JComboBox<>(types);
        typeSelector.addActionListener(e -> updateCategorySelector());
        inputPanel.add(typeSelector);
        
        // 項目類別選擇（根據收入或支出切換）
        categorySelector = new JComboBox<>();
        updateCategorySelector(); // 初始化選項
        inputPanel.add(categorySelector);
        
        // 金額輸入
        amountField = new JTextField();
        inputPanel.add(amountField);
        
        // 備註
        notefield = new JTextField();
        inputPanel.add(notefield);
        
        // 設置今天的日期
        setTodayDate();

        // 右側功能按鈕
        JPanel rightPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        enterbutton = new JButton("儲存帳目");
        displaybutton = new JButton("顯示所有帳目資料");
        queryByDateButton = new JButton("查詢指定日期帳目");
        deleteByDateButton = new JButton("刪除指定日期帳目");
        deletebutton = new JButton("清除所有帳目資料");
        searchByNoteButton = new JButton("查詢備註關鍵字");
        statsButton = new JButton("檢視所有帳目統計");
        chartButton = new JButton("圖表分析與匯出");
        System.out.println("Debug: Creating chart button");

        // 設置圖表按鈕外觀，讓它更突出
        chartButton.setBackground(new Color(200, 230, 200));
        chartButton.setForeground(Color.BLUE);
        chartButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));

        rightPanel.add(enterbutton);
        rightPanel.add(displaybutton);
        rightPanel.add(queryByDateButton);
        rightPanel.add(deleteByDateButton);
        rightPanel.add(deletebutton);
        rightPanel.add(searchByNoteButton);
        rightPanel.add(statsButton);
        rightPanel.add(chartButton);
        System.out.println("Debug: Chart button added to panel");

        // 加入到中間panel
        centerPanel.add(leftPanel);
        centerPanel.add(inputPanel);
        centerPanel.add(rightPanel);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 中間區域留白

        // 上方區域為文字區
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

        // 初始顯示歡迎訊息
        area.setText("💰 歡迎使用記帳小幫手！\n請選擇收入或支出類型，並輸入金額後，點擊『儲存帳目』開始記帳。");

        // 使用 JSplitPane 來分隔上面和下面的區域
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroller, centerPanel);
        splitPane.setResizeWeight(0.5); // 調整上下兩個區域大小比例為1:1
        splitPane.setDividerLocation(250); // 直接設置分隔線為一個值
        splitPane.setDividerSize(5); // 分隔線寬度
        panel.add(splitPane, BorderLayout.CENTER);

        // ===== 選單列=====
        frame.setJMenuBar(createMenuBar());

        // ===== 註冊事件監聽=====
        enterbutton.addActionListener(new EnterListener());
        displaybutton.addActionListener(new DisplayListener());
        queryByDateButton.addActionListener(new QueryByDateListener());
        deleteByDateButton.addActionListener(new DeleteByDateListener());
        deletebutton.addActionListener(new DeleteListener());
        searchByNoteButton.addActionListener(new SearchByNoteListener());
        statsButton.addActionListener(new StatsButtonListener());
        chartButton.addActionListener(new ChartButtonListener());

        // 加入主panel
        frame.getContentPane().add(panel);
        frame.setSize(900, 700);  // 調整窗口大小以適應更多內容
        frame.setMinimumSize(new Dimension(900, 700));  // 設置最小尺寸
        frame.setLocationRelativeTo(null); // 設定讓視窗顯示在螢幕中央
        frame.setVisible(true);

        // 顯示每日小語
        showDailyQuote();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        Font menuFont = new Font("Microsoft JhengHei", Font.PLAIN, 16);

        menu = new JMenu("檔案管理");
        menu.setFont(menuFont);

        JMenuItem saveMenuItem = new JMenuItem("儲存帳目資料");
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
        dateLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5)); // 讓日期離右邊5px
        menuBar.add(dateLabel);

        return menuBar;
    }

    // 建立標籤的方法
    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    // 顯示每日小語
    private void showDailyQuote() {
        String[] quotes = {
            "💡 每一筆花費都是給未來的自己一封信。",
            "💰 小錢不省，大錢難存。",
            "💼 理財不是有錢人的專利，而是每個人的責任。",
            "📊 記帳是與自己財務對話的開始。",
            "💪 積少成多，從每天記帳開始。",
            "⏱ 省錢是一時的，會錢一輩子。",
            "🏠 財務健康安全，記帳是關鍵。",
            "💼 財富不是賺來的，是管來的。"
        };

        int index = (int)(Math.random() * quotes.length);
        JOptionPane.showMessageDialog(frame, quotes[index], "💡 每日小語", JOptionPane.INFORMATION_MESSAGE);
    }

    // 獲取當前日期
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(new Date());
    }

    // 設置今天日期
    private void setTodayDate() {
        Calendar cal = Calendar.getInstance();
        yearSelector.setSelectedItem(cal.get(Calendar.YEAR));
        monthSelector.setSelectedItem(cal.get(Calendar.MONTH) + 1); // 月份從0開始
        daySelector.setSelectedItem(cal.get(Calendar.DAY_OF_MONTH));
    }
    
    // 根據收支類型更新類別選擇器
    private void updateCategorySelector() {
        categorySelector.removeAllItems();
        if (typeSelector.getSelectedIndex() == 0) {  // 支出
            categorySelector.addItem("早餐");
            categorySelector.addItem("午餐");
            categorySelector.addItem("晚餐");
            categorySelector.addItem("其他支出");
        } else {  // 收入
            categorySelector.addItem("薪資");
            categorySelector.addItem("投資");
            categorySelector.addItem("獎金");
            categorySelector.addItem("其他收入");
        }
    }
    
    // 建立年份選擇器
    private JComboBox<Integer> createYearSelector() {
        Integer[] years = new Integer[5];
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 5; i++) {
            years[i] = currentYear - 2 + i;
        }
        return new JComboBox<>(years);
    }
    
    // 建立月份選擇器
    private JComboBox<Integer> createMonthSelector() {
        Integer[] months = new Integer[12];
        for (int i = 0; i < 12; i++) {
            months[i] = i + 1;
        }
        return new JComboBox<>(months);
    }
    
    // 建立日期選擇器
    private JComboBox<Integer> createDaySelector() {
        Integer[] days = new Integer[31];
        for (int i = 0; i < 31; i++) {
            days[i] = i + 1;
        }
        return new JComboBox<>(days);
    }

    // 從選擇器取得日期字串
    private String getSelectedDate() {
        int year = (Integer) yearSelector.getSelectedItem();
        int month = (Integer) monthSelector.getSelectedItem();
        int day = (Integer) daySelector.getSelectedItem();
        
        // 格式為 YYYY/MM/DD
        return String.format("%d/%02d/%02d", year, month, day);
    }

    /**
     * 檢查日期是否有效
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @return 日期是否有效
     */
    private boolean isValidDate(int year, int month, int day) {
        if (year < 1900 || year > 2100) {
            return false;
        }
        
        if (month < 1 || month > 12) {
            return false;
        }
        
        // 依照月份檢查日期
        int maxDay;
        switch (month) {
            case 2: // 二月，考慮閏年
                if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                    maxDay = 29; // 閏年
                } else {
                    maxDay = 28; // 平年
                }
                break;
            case 4: case 6: case 9: case 11: // 小月
                maxDay = 30;
                break;
            default: // 大月
                maxDay = 31;
                break;
        }
        
        return day >= 1 && day <= maxDay;
    }

    // 輸入帳目事件：檢查數位並建立帳目物件
    public class EnterListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            StringBuilder errorMsg = new StringBuilder();
            String date = getSelectedDate();
            
            // 檢查是否是未來日期
            if (DateUtils.isFutureDate(date)) {
                errorMsg.append("❌ 不可以輸入未來的日期\n");
            }
            
            // 收支類型 (0=支出, 1=收入)
            int type = typeSelector.getSelectedIndex();
            String category = (String) categorySelector.getSelectedItem();
            
            // 檢查金額
            int amount = 0;
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                errorMsg.append("❌ 請輸入金額\n");
            } else {
                try {
                    amount = Integer.parseInt(amountText);
                    if (amount < 0) {
                        errorMsg.append("❌ 金額不能為負數\n");
                    }
                } catch (NumberFormatException e) {
                    errorMsg.append("❌ 金額格式錯誤（請輸入整數數字）\n");
                }
            }

            // 檢查備註
            String note = notefield.getText().trim();
            if (note.isEmpty()) {
                note = "無";
            }

            if (errorMsg.length() > 0) {
                area.setText(errorMsg.toString());
                return;
            }

            // 檢查是否已存在同一日期的帳目
            boolean accountExists = false;
            Account existingAccount = null;
            for (int i = 0; i < accountList.size(); i++) {
                Account acc = accountList.get(i);
                if (acc.getDate().equals(date)) {
                    existingAccount = acc;
                    accountExists = true;
                    break;
                }
            }

            // 依據支出或收入類別，更新或建帳目
            if (accountExists) {
                // 更新現有帳目
                if (type == 0) { // 支出
                    if (category.equals("早餐")) {
                        existingAccount.setBreakfast(amount);
                    } else if (category.equals("午餐")) {
                        existingAccount.setLunch(amount);
                    } else if (category.equals("晚餐")) {
                        existingAccount.setDinner(amount);
                    } else if (category.equals("其他支出")) {
                        existingAccount.setOthers(amount);
                    }
                } else { // 收入
                    if (category.equals("薪資")) {
                        existingAccount.setSalary(amount);
                    } else if (category.equals("投資")) {
                        existingAccount.setInvestment(amount);
                    } else if (category.equals("獎金")) {
                        existingAccount.setBonus(amount);
                    } else if (category.equals("其他收入")) {
                        existingAccount.setOtherIncome(amount);
                    }
                }
                existingAccount.setNote(note);
                area.setText("✅ 帳目已更新！日期：" + date + "，項目：" + category);
            } else {
                // 建立新帳目 - 依據類別設定不同欄位
                int breakfast = 0, lunch = 0, dinner = 0, others = 0;
                int salary = 0, investment = 0, bonus = 0, otherIncome = 0;
                
                if (type == 0) { // 支出
                    if (category.equals("早餐")) breakfast = amount;
                    else if (category.equals("午餐")) lunch = amount;
                    else if (category.equals("晚餐")) dinner = amount;
                    else if (category.equals("其他支出")) others = amount;
                } else { // 收入
                    if (category.equals("薪資")) salary = amount;
                    else if (category.equals("投資")) investment = amount;
                    else if (category.equals("獎金")) bonus = amount;
                    else if (category.equals("其他收入")) otherIncome = amount;
                }
                
                account = new Account(breakfast, lunch, dinner, others, 
                                     salary, investment, bonus, otherIncome, 
                                     date, note);
                accountList.add(account);
                account = null;
                area.setText("✅ 帳目建立成功！日期：" + date + "，項目：" + category);
            }

            // 檢查收支平衡警告
            int totalIncome = 0;
            int totalExpense = 0;
            
            for (int i = 0; i < accountList.size(); i++) {
                Account acc = accountList.get(i);
                totalIncome += acc.getIncomeTotal();
                totalExpense += acc.getExpenseTotal();
            }
            
            if (totalExpense > totalIncome) {
                JOptionPane.showMessageDialog(
                    frame,
                    "⚠️ 警告：總支出 (" + totalExpense + " 元) 大於總收入 (" + totalIncome + " 元)！\n" +
                    "您已入不敷出：" + (totalIncome - totalExpense) + " 元\n" +
                    "請考慮增加收入或減少支出。",
                    "收支警告",
                    JOptionPane.WARNING_MESSAGE
                );
            }

            // 清空輸入欄位
            amountField.setText("");
            notefield.setText("");
        }
    }

    // 顯示所有帳目資料
    public class DisplayListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            if (accountList.size() > 0) {
                area.setText("所有帳目資料：\n\n");
                for (int i = 0; i < accountList.size(); i++) {
                    area.append(accountList.get(i).printDetailedAccount() + "\n\n");
                }
            } else {
                area.setText("❌ 目前沒有任何帳目資料");
            }
        }
    }

    // 查詢指定日期的帳目資料
    public class QueryByDateListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            String date = JOptionPane.showInputDialog(frame, "請輸入查詢日期（格式：YYYY/MM/DD）：", "查詢視窗", JOptionPane.QUESTION_MESSAGE);

            if (date == null) return; // 使用者取消

            if (!DateUtils.isValidDate(date)) {
                JOptionPane.showMessageDialog(frame, "❌ 日期為空或格式不正確，請輸入：YYYY/MM/DD", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (DateUtils.isFutureDate(date)) {
                JOptionPane.showMessageDialog(frame, "❌ 不可以輸入未來的日期\n", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (int i = 0; i < accountList.size(); i++) {
                Account acc = accountList.get(i);

                if (acc.getDate().equals(date)) {
                    area.setText("✅ 查詢結果：\n\n" + acc.printDetailedAccount());
                    return; // 找到並顯示後，退出迴圈
                }
            }

            area.setText("❌ 找不到 " + date + " 的帳目資料");
        }
    }

    // 刪除指定日期的帳目資料
    public class DeleteByDateListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            String date = JOptionPane.showInputDialog(frame, "請輸入要刪除的日期（格式：YYYY/MM/DD）：", "刪除視窗", JOptionPane.QUESTION_MESSAGE);

            if (date == null) return; // 使用者取消
            
            if (!DateUtils.isValidDate(date)) {
                JOptionPane.showMessageDialog(frame, "❌ 日期為空或格式不正確，請輸入：YYYY/MM/DD", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
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
                    return; // 找到並刪除後，退出迴圈
                }
            }

            area.setText("❌ 刪除失敗，查無 " + date + " 的帳目資料");
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
                area.setText("❌ 取消清除所有帳目資料的操作");
            }
        }
    }

    // 尋找備註中是否有關鍵字
    public class SearchByNoteListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            String keyword = JOptionPane.showInputDialog(frame, "請輸入備註關鍵字", "查詢備註", JOptionPane.QUESTION_MESSAGE);

            if (keyword == null) return; // 使用者取消

            if (keyword.trim().isEmpty()) {
                area.setText("❌ 請輸入要搜尋的關鍵字");
                return;
            }

            keyword = keyword.trim();
            StringBuilder result = new StringBuilder();
            boolean found = false;

            for (int i = 0; i < accountList.size(); i++) {
                Account acc = accountList.get(i);

                if (acc.getNote() != null && acc.getNote().contains(keyword)) {
                    result.append(acc.printDetailedAccount()).append("\n\n");
                    found = true;
                }
            }

            if (found) {
                area.setText("✅ 查詢結果如下（備註中包含：" + keyword + "）：\n\n" + result);
            } else {
                area.setText("❌ 沒有找到備註中包含「" + keyword + "」的帳目");
            }
        }
    }

    // 統計功能 - 開啟視窗提供年/月統計
    public class StatsButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            JFrame statsFrame = new JFrame("統計功能");
            statsFrame.setSize(300, 150);
            statsFrame.setLocationRelativeTo(frame);
            statsFrame.setLayout(new GridBagLayout()); // 改為 GridBagLayout

            JButton yearButton = new JButton("查詢年度");
            JButton monthButton = new JButton("查詢年月");

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0; // 第一行
            gbc.insets = new Insets(10, 10, 10, 10); // 周圍留白

            // 放入第一個按鈕在第 0 欄位
            gbc.gridx = 0;
            statsFrame.add(yearButton, gbc);

            // 放入第二個按鈕在第 1 欄位
            gbc.gridx = 1;
            statsFrame.add(monthButton, gbc);

            // 年統計查詢功能
            yearButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    String year = JOptionPane.showInputDialog(statsFrame, "請輸入年份，例如：2025");

                    if (year == null) return; // 使用者取消操作

                    if (!DateUtils.isValidYear(year)) {
                        JOptionPane.showMessageDialog(statsFrame, "❌ 輸入為空或不是有效年份，請輸入 4 位數字的年份，例如：2025", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int breakfastTotal = 0, lunchTotal = 0, dinnerTotal = 0, othersTotal = 0, total = 0;

                    for (int i = 0; i < accountList.size(); i++) {
                        Account acc = accountList.get(i);
                        if (acc.getDate().startsWith(year)) {
                            breakfastTotal += acc.getBreakfast();
                            lunchTotal += acc.getLunch();
                            dinnerTotal += acc.getDinner();
                            othersTotal += acc.getOthers();
                            total += acc.getTotal();
                        }
                    }

                    String statsMessage = year + "年統計：\n"
                            + "早餐總支出：" + breakfastTotal + " 元\n"
                            + "午餐總支出：" + lunchTotal + " 元\n"
                            + "晚餐總支出：" + dinnerTotal + " 元\n"
                            + "其他總支出：" + othersTotal + " 元\n"
                            + "總支出：" + total + " 元";
                    JOptionPane.showMessageDialog(statsFrame, statsMessage);
                }
            });

            // 月統計查詢功能
            monthButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    String month = JOptionPane.showInputDialog(statsFrame, "請輸入年份與月份（格式：YYYY/MM）");

                    if (month == null) return;

                    if (!DateUtils.isValidYearMonth(month)) {
                        JOptionPane.showMessageDialog(statsFrame, "❌ 輸入為空或不是有效年月份，請輸入正確的年月份，例如：2025/05", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String[] parts = month.split("/");
                    int m = Integer.parseInt(parts[1]);

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

                    String statsMessage = month + "月統計：\n"
                            + "早餐總支出：" + breakfastTotal + " 元\n"
                            + "午餐總支出：" + lunchTotal + " 元\n"
                            + "晚餐總支出：" + dinnerTotal + " 元\n"
                            + "其他總支出：" + othersTotal + " 元\n"
                            + "總支出：" + total + " 元";
                    JOptionPane.showMessageDialog(statsFrame, statsMessage);
                }
            });

            statsFrame.setVisible(true);
        }
    }

    // 圖表功能 - 開啟視窗顯示多類型圖表
    public class ChartButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            System.out.println("Debug: Chart button clicked");
            
            // 檢查是否有數據
            if (accountList.size() == 0) {
                JOptionPane.showMessageDialog(frame, "提示：沒有帳目資料可以顯示", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 檢查收支平衡狀況
            checkBalance();
            
            try {
                // 直接將frame和accountList傳給SimpleChartPanel
                // 使用反射調用SimpleChartPanel
                Class<?> chartPanelClass = Class.forName("SimpleChartPanel");
                java.lang.reflect.Method showChartDialogMethod = chartPanelClass.getMethod("showChartDialog", JFrame.class, Object.class);
                showChartDialogMethod.invoke(null, frame, accountList);
            } catch (Exception e) {
                System.err.println("圖表功能錯誤: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, 
                    "圖表功能出現錯誤，請確認lib目錄中有JFreeChart的jar檔案。\n錯誤: " + e.getMessage(),
                    "圖表錯誤", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        // 本地檢查收支平衡的方法
        private void checkBalance() {
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
                    frame,
                    "⚠️ 警告：總支出 (" + totalExpense + " 元) 大於總收入 (" + totalIncome + " 元)！\n" +
                    "您已入不敷出：" + (totalIncome - totalExpense) + " 元\n" +
                    "請考慮增加收入或減少支出。",
                    "收支警告",
                    JOptionPane.WARNING_MESSAGE
                );
            }
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

    // 檔案選單 - 從檔案載入帳目
    public class LoadMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            JFileChooser filechooser = new JFileChooser();

            if (filechooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                accountList = streamhelper.loadFile(filechooser.getSelectedFile());
                area.setText("✅ 帳目檔案載入完成");
            }
        }
    }

    // 主函式進入點
    public static void main(String[] args) {
        // 檢查JFreeChart庫是否存在
        try {
            Class.forName("org.jfree.chart.JFreeChart");
            System.out.println("✓ JFreeChart庫已成功載入");
            Class.forName("org.jfree.data.general.DefaultPieDataset");
            System.out.println("✓ JFreeChart資料類別已成功載入");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ 找不到JFreeChart庫，請確保lib目錄中有相關的jar檔案");
            System.err.println("錯誤詳情: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "無法載入圖表功能所需的庫文件。\n請確保已下載JFreeChart和JCommon的JAR檔案，並放在lib目錄下。",
                "庫文件載入錯誤", JOptionPane.ERROR_MESSAGE);
            // 仍然繼續執行，但圖表功能可能無法使用
        }
        
        AccountGUI gui = new AccountGUI();
        gui.buildGUI();
    }
}
