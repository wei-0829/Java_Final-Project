package AccountProgram;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AccountGUI {
    private JFrame frame;
    private JPanel panel;
    private AccountList accountList;
    private Account account;
    private JButton enterbutton, displaybutton, queryByDateButton, deleteByDateButton, deletebutton, searchByNoteButton, statsButton, chartButton;
    private JTextArea area;
    private JTextField datefield, breakfastfield, lunchfield, dinnerfield, othersfield, incomefield, notefield;
    private JPanel inputPanel, dynamicInputPanel;
    private JRadioButton incomeRadio, expenseRadio;
    private StreamHelper streamhelper;
    private JMenu menu;

    public void buildGUI() {
        frame = new JFrame("記帳小幫手");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        accountList = new AccountList();
        streamhelper = new StreamHelper();

        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Use BoxLayout for centerPanel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 左側標籤
        JPanel leftPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        leftPanel.setPreferredSize(new Dimension(250, 300));
        Font font = new Font("Microsoft JhengHei", Font.PLAIN, 14);
        leftPanel.add(createLabel("日期（輸入YYYYMMDD，例如20250518）：", font));
        leftPanel.add(createLabel("請選擇輸入類型：", font));
        leftPanel.add(createLabel("輸入欄位將根據選擇顯示", font));
        leftPanel.add(createLabel("", font)); // 占位
        leftPanel.add(createLabel("", font)); // 占位
        leftPanel.add(createLabel("", font)); // 占位
        leftPanel.add(createLabel("帳目備註（若空白則視為無）：", font));
        leftPanel.add(createLabel("若要修改帳目，重新輸入後儲存即可", font));

        // 中間輸入欄位
        inputPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        inputPanel.setPreferredSize(new Dimension(350, 300)); // Increased width
        inputPanel.setMinimumSize(new Dimension(350, 300)); // Ensure minimum size
        datefield = new JTextField();
        datefield.setToolTipText("輸入8位數字：YYYYMMDD，例如20250518");

        // 選擇收入或支出
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        incomeRadio = new JRadioButton("收入", true);
        expenseRadio = new JRadioButton("支出");
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(incomeRadio);
        typeGroup.add(expenseRadio);
        radioPanel.add(incomeRadio);
        radioPanel.add(expenseRadio);

        // 動態輸入面板
        dynamicInputPanel = new JPanel();
        dynamicInputPanel.setLayout(new BoxLayout(dynamicInputPanel, BoxLayout.Y_AXIS));
        dynamicInputPanel.setPreferredSize(new Dimension(330, 180)); // Increased height for expense fields
        incomefield = new JTextField(15);
        updateInputFields(true); // 初始化為收入模式

        notefield = new JTextField();
        enterbutton = new JButton("儲存帳目");

        inputPanel.add(datefield);
        inputPanel.add(radioPanel);
        inputPanel.add(dynamicInputPanel);
        inputPanel.add(new JLabel()); // 占位
        inputPanel.add(new JLabel()); // 占位
        inputPanel.add(notefield);
        inputPanel.add(enterbutton);

        // 右側功能按鈕
        JPanel rightPanel = new JPanel(new GridLayout(7, 1, 5, 5));
        rightPanel.setPreferredSize(new Dimension(200, 300));
        displaybutton = new JButton("列出所有帳目資料");
        queryByDateButton = new JButton("查詢指定日期帳目");
        deleteByDateButton = new JButton("刪除指定日期帳目");
        deletebutton = new JButton("清除所有帳目資料");
        searchByNoteButton = new JButton("查詢備註的關鍵字");
        statsButton = new JButton("查看所有帳目統計");
        chartButton = new JButton("顯示收入與支出圖表");
        rightPanel.add(displaybutton);
        rightPanel.add(queryByDateButton);
        rightPanel.add(deleteByDateButton);
        rightPanel.add(deletebutton);
        rightPanel.add(searchByNoteButton);
        rightPanel.add(statsButton);
        rightPanel.add(chartButton);

        // Add panels to centerPanel with spacing
        centerPanel.add(leftPanel);
        centerPanel.add(Box.createHorizontalStrut(10));
        centerPanel.add(inputPanel);
        centerPanel.add(Box.createHorizontalStrut(10));
        centerPanel.add(rightPanel);

        area = new JTextArea();
        area.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        area.setForeground(Color.BLACK);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(null);

        JScrollPane scroller = new JScrollPane(area,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setBorder(null);

        area.setText("👋 歡迎使用《記帳小幫手》！\n請輸入日期（YYYYMMDD）並選擇收入或支出，輸入資料後點擊『儲存帳目』開始記錄！");

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroller, centerPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(250);
        splitPane.setDividerSize(5);
        centerPanel.setMinimumSize(new Dimension(800, 300));

        panel.add(splitPane, BorderLayout.CENTER);

        frame.setJMenuBar(createMenuBar());

        // 監聽器
        enterbutton.addActionListener(new EnterListener());
        displaybutton.addActionListener(new DisplayListener());
        queryByDateButton.addActionListener(new QueryByDateListener());
        deleteByDateButton.addActionListener(new DeleteByDateListener());
        deletebutton.addActionListener(new DeleteListener());
        searchByNoteButton.addActionListener(new SearchByNoteListener());
        statsButton.addActionListener(new StatsButtonListener());
        chartButton.addActionListener(new ChartButtonListener());

        // 動態切換輸入欄位
        incomeRadio.addActionListener(e -> updateInputFields(true));
        expenseRadio.addActionListener(e -> updateInputFields(false));

        // Add ComponentListener for resizing
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                centerPanel.revalidate();
                centerPanel.repaint();
            }
        });

        frame.getContentPane().add(panel);
        frame.setSize(800, 600);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        showDailyQuote();
    }

    private void updateInputFields(boolean isIncome) {
        dynamicInputPanel.removeAll();
        dynamicInputPanel.setLayout(new BoxLayout(dynamicInputPanel, BoxLayout.Y_AXIS));
        Font font = new Font("Microsoft JhengHei", Font.PLAIN, 14);

        if (isIncome) {
            incomefield = new JTextField(15);
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            row.add(createLabel("收入金額：", font));
            row.add(incomefield);
            dynamicInputPanel.add(row);
            dynamicInputPanel.add(Box.createVerticalStrut(10));
        } else {
            breakfastfield = new JTextField(15);
            lunchfield = new JTextField(15);
            dinnerfield = new JTextField(15);
            othersfield = new JTextField(15);

            JPanel breakfastRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            breakfastRow.add(createLabel("早餐支出：", font));
            breakfastRow.add(breakfastfield);
            dynamicInputPanel.add(breakfastRow);
            dynamicInputPanel.add(Box.createVerticalStrut(10));

            JPanel lunchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            lunchRow.add(createLabel("午餐支出：", font));
            lunchRow.add(lunchfield);
            dynamicInputPanel.add(lunchRow);
            dynamicInputPanel.add(Box.createVerticalStrut(10));

            JPanel dinnerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            dinnerRow.add(createLabel("晚餐支出：", font));
            dinnerRow.add(dinnerfield);
            dynamicInputPanel.add(dinnerRow);
            dynamicInputPanel.add(Box.createVerticalStrut(10));

            JPanel othersRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            othersRow.add(createLabel("其他支出：", font));
            othersRow.add(othersfield);
            dynamicInputPanel.add(othersRow);
            dynamicInputPanel.add(Box.createVerticalStrut(10));
        }

        dynamicInputPanel.revalidate();
        dynamicInputPanel.repaint();
        inputPanel.revalidate();
        inputPanel.repaint();
        panel.revalidate(); // Ensure parent panel updates
        panel.repaint();
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

        JLabel dateLabel = new JLabel(DateUtils.getCurrentDate());
        dateLabel.setFont(menuFont);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        menuBar.add(dateLabel);

        return menuBar;
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

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

    public class EnterListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            StringBuilder errorMsg = new StringBuilder();
            String dateInput = datefield.getText().trim();
            String date = DateUtils.parseNumericDate(dateInput);

            if (date == null) {
                errorMsg.append("❌ 日期格式無效（請輸入8位數字：YYYYMMDD，例如20250518）\n");
            } else if (DateUtils.isFutureDate(date)) {
                errorMsg.append("❌ 不可以輸入未來的日期\n");
            }

            int breakfast = 0, lunch = 0, dinner = 0, others = 0, income = 0;
            if (incomeRadio.isSelected()) {
                String text = incomefield.getText().trim();
                if (text.isEmpty()) {
                    errorMsg.append("❌ 收入金額不能為空\n");
                } else {
                    try {
                        income = Integer.parseInt(text);
                        if (income < 0) {
                            errorMsg.append("❌ 收入金額不能為負數\n");
                        }
                    } catch (NumberFormatException e) {
                        errorMsg.append("❌ 收入金額格式錯誤（請輸入有效整數）\n");
                    }
                }
            } else {
                JTextField[] fields = { breakfastfield, lunchfield, dinnerfield, othersfield };
                String[] labels = { "早餐", "午餐", "晚餐", "其他" };
                int[] values = new int[4];
                boolean hasInput = false;
                for (int i = 0; i < 4; i++) {
                    String text = fields[i].getText().trim();
                    if (!text.isEmpty()) {
                        hasInput = true;
                        try {
                            values[i] = Integer.parseInt(text);
                            if (values[i] < 0) {
                                errorMsg.append("❌ " + labels[i] + "金額不能為負數\n");
                            }
                        } catch (NumberFormatException e) {
                            errorMsg.append("❌ " + labels[i] + "金額格式錯誤（請輸入有效整數）\n");
                        }
                    }
                }
                if (!hasInput) {
                    errorMsg.append("❌ 請至少輸入一項支出金額\n");
                }
                breakfast = values[0];
                lunch = values[1];
                dinner = values[2];
                others = values[3];
            }

            String note = notefield.getText().trim();
            if (note.isEmpty()) {
                note = "無";
            }

            if (errorMsg.length() > 0) {
                area.setText(errorMsg.toString());
                return;
            }

            boolean accountExists = false;
            for (int i = 0; i < accountList.size(); i++) {
                Account existingAccount = accountList.get(i);
                if (existingAccount.getDate().equals(date)) {
                    existingAccount.setBreakfast(existingAccount.getBreakfast() + breakfast);
                    existingAccount.setLunch(existingAccount.getLunch() + lunch);
                    existingAccount.setDinner(existingAccount.getDinner() + dinner);
                    existingAccount.setOthers(existingAccount.getOthers() + others);
                    existingAccount.setIncome(existingAccount.getIncome() + income);
                    existingAccount.setNote(note);
                    area.setText("✅ 帳目已更新！ 日期：" + date);
                    accountExists = true;
                    break;
                }
            }

            if (!accountExists) {
                account = new Account(breakfast, lunch, dinner, others, income, date, note);
                accountList.add(account);
                account = null;
                area.setText("✅ 帳目建立成功！");
            }

            datefield.setText("");
            if (incomeRadio.isSelected()) {
                incomefield.setText("");
            } else {
                breakfastfield.setText("");
                lunchfield.setText("");
                dinnerfield.setText("");
                othersfield.setText("");
            }
            notefield.setText("");
        }
    }

    public class DisplayListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            if (accountList.size() > 0) {
                area.setText("所有帳目資料：\n\n");
                for (int i = 0; i < accountList.size(); i++) {
                    area.append(accountList.get(i).printAccount() + "\n\n");
                }
            } else {
                area.setText("⚠️ 目前沒有任何帳目資料");
            }
        }
    }

    public class QueryByDateListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            String dateInput = JOptionPane.showInputDialog(frame, "請輸入查詢日期（格式：YYYYMMDD，例如20250518）：", "查詢視窗", JOptionPane.QUESTION_MESSAGE);
            if (dateInput == null) return;
            String date = DateUtils.parseNumericDate(dateInput);
            if (date == null) {
                JOptionPane.showMessageDialog(frame, "❌ 日期為空或為無效日期，請輸入：YYYYMMDD，例如20250518", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (DateUtils.isFutureDate(date)) {
                JOptionPane.showMessageDialog(frame, "❌ 不可以輸入未來的日期\n", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }
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
            String dateInput = JOptionPane.showInputDialog(frame, "請輸入要刪除的日期（格式：YYYYMMDD，例如20250518）：", "刪除視窗", JOptionPane.QUESTION_MESSAGE);
            if (dateInput == null) return;
            String date = DateUtils.parseNumericDate(dateInput);
            if (date == null) {
                JOptionPane.showMessageDialog(frame, "❌ 日期為空或為無效日期，請輸入：YYYYMMDD，例如20250518", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
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
                    return;
                }
            }
            area.setText("⚠️ 無法刪除，查無 " + date + " 的帳目資料");
        }
    }

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

    public class SearchByNoteListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            String keyword = JOptionPane.showInputDialog(frame, "請輸入備註關鍵字：", "查詢備註", JOptionPane.QUESTION_MESSAGE);
            if (keyword == null) return;
            if (keyword.trim().isEmpty()) {
                area.setText("⚠️ 請輸入有效的關鍵字！");
                return;
            }
            keyword = keyword.trim();
            StringBuilder result = new StringBuilder();
            boolean found = false;
            for (int i = 0; i < accountList.size(); i++) {
                Account acc = accountList.get(i);
                if (acc.getNote() != null && acc.getNote().contains(keyword)) {
                    result.append(acc.printAccount()).append("\n\n");
                    found = true;
                }
            }
            if (found) {
                area.setText("🔍 查詢結果如下（包含關鍵字：「" + keyword + "」）：\n\n" + result);
            } else {
                area.setText("❌ 沒有找到備註中包含關鍵字「" + keyword + "」的帳目。");
            }
        }
    }

    public class StatsButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            JFrame statsFrame = new JFrame("統計分析");
            statsFrame.setSize(300, 150);
            statsFrame.setLocationRelativeTo(frame);
            statsFrame.setLayout(new GridBagLayout());
            JButton yearButton = new JButton("查詢某年");
            JButton monthButton = new JButton("查詢某年某月");
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0;
            statsFrame.add(yearButton, gbc);
            gbc.gridx = 1;
            statsFrame.add(monthButton, gbc);

            yearButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    String year = JOptionPane.showInputDialog(statsFrame, "請輸入年份（例如：2025）");
                    if (year == null) return;
                    if (!DateUtils.isValidYear(year)) {
                        JOptionPane.showMessageDialog(statsFrame, "❌ 輸入為空或不是有效年份，請輸入 4 位數的有效年份，例如：2025", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    int breakfastTotal = 0, lunchTotal = 0, dinnerTotal = 0, othersTotal = 0, incomeTotal = 0, total = 0;
                    for (int i = 0; i < accountList.size(); i++) {
                        Account acc = accountList.get(i);
                        if (acc.getDate().startsWith(year)) {
                            breakfastTotal += acc.getBreakfast();
                            lunchTotal += acc.getLunch();
                            dinnerTotal += acc.getDinner();
                            othersTotal += acc.getOthers();
                            incomeTotal += acc.getIncome();
                            total += acc.getTotal();
                        }
                    }
                    String statsMessage = year + "年統計：\n"
                            + "早餐總支出：" + breakfastTotal + " 元\n"
                            + "午餐總支出：" + lunchTotal + " 元\n"
                            + "晚餐總支出：" + dinnerTotal + " 元\n"
                            + "其他總支出：" + othersTotal + " 元\n"
                            + "總收入：" + incomeTotal + " 元\n"
                            + "總支出：" + total + " 元";
                    JOptionPane.showMessageDialog(statsFrame, statsMessage);
                }
            });

            monthButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    String month = JOptionPane.showInputDialog(statsFrame, "請輸入年份和月份（格式：YYYY/MM）");
                    if (month == null) return;
                    if (!DateUtils.isValidYearMonth(month)) {
                        JOptionPane.showMessageDialog(statsFrame, "❌ 輸入為空或不是有效年月份，請輸入有效的年月份，例如：2025/05", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String[] parts = month.split("/");
                    int m = Integer.parseInt(parts[1]);
                    int breakfastTotal = 0, lunchTotal = 0, dinnerTotal = 0, othersTotal = 0, incomeTotal = 0, total = 0;
                    for (int i = 0; i < accountList.size(); i++) {
                        Account acc = accountList.get(i);
                        if (acc.getDate().startsWith(month)) {
                            breakfastTotal += acc.getBreakfast();
                            lunchTotal += acc.getLunch();
                            dinnerTotal += acc.getDinner();
                            othersTotal += acc.getOthers();
                            incomeTotal += acc.getIncome();
                            total += acc.getTotal();
                        }
                    }
                    String statsMessage = month + "月統計：\n"
                            + "早餐總支出：" + breakfastTotal + " 元\n"
                            + "午餐總支出：" + lunchTotal + " 元\n"
                            + "晚餐總支出：" + dinnerTotal + " 元\n"
                            + "其他總支出：" + othersTotal + " 元\n"
                            + "總收入：" + incomeTotal + " 元\n"
                            + "總支出：" + total + " 元";
                    JOptionPane.showMessageDialog(statsFrame, statsMessage);
                }
            });

            statsFrame.setVisible(true);
        }
    }

    public class SaveMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            JFileChooser filechooser = new JFileChooser();
            if (filechooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                streamhelper.saveFile(accountList, filechooser.getSelectedFile());
                area.setText("✅ 帳目檔案已儲存");
            }
        }
    }

    public class LoadMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            JFileChooser filechooser = new JFileChooser();
            if (filechooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                accountList = streamhelper.loadFile(filechooser.getSelectedFile());
                area.setText("✅ 帳目檔案載入完成");
            }
        }
    }

    public class ChartButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            if (accountList.size() == 0) {
                JOptionPane.showMessageDialog(frame, "⚠️ 目前沒有帳目資料可顯示圖表", "無資料", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFrame chartFrame = new JFrame("收入與支出圖表");
            chartFrame.setSize(1200, 800);
            chartFrame.setLocationRelativeTo(frame);
            chartFrame.setLayout(new GridLayout(2, 2, 20, 20));

            ChartUtils.createLineChart(accountList, "每日收入與支出趨勢", "日期", "金額 (元)", chartFrame);
            ChartUtils.createPieChartExpenses(accountList, "支出類別分佈", chartFrame);
            ChartUtils.createPieChartIncomeVsExpenses(accountList, "收入與支出比較", chartFrame);
            ChartUtils.createNetBalanceChart(accountList, "每日淨餘額趨勢 (收入 - 支出)", "日期", "淨餘額 (元)", chartFrame);

            chartFrame.setVisible(true);
        }
    }

    public static void main(String[] args) {
        AccountGUI gui = new AccountGUI();
        gui.buildGUI();
    }
}