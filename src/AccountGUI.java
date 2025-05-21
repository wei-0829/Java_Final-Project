import javax.swing.*;
import javax.swing.table.*;

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
import java.util.HashMap;
import java.util.Map;

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
    private JTextField notefield;
    private JComboBox<String> typeComboBox;
    private JButton enterbutton, displaybutton, queryByDateButton, deleteByDateButton, deletebutton, searchByNoteButton, statsButton;
    private StreamHelper streamhelper; // 負責檔案讀寫的工具
    private JDateChooser dateChooser;  // JDateChooser 用於日期選擇
    
    // 定義金額輸入欄位
    private Map<String, JTextField> amountFields = new HashMap<>();
    
    // 定義卡片面板
    private JPanel cardPanel;
    private CardLayout cardLayout;
    
    // 支出項目列表
    private final String[] expenseItems = {"早餐", "午餐", "晚餐", "交通", "住宿", "衣著", "水電費", "娛樂", "醫療", "教育", "通訊費", "其他"};
    // 收入項目列表
    private final String[] incomeItems = {"額外收入", "薪資", "獎金", "投資收益", "副業", "禮金"};

    public void buildGUI() { 
        frame = new JFrame("記帳小幫手");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        accountList = new AccountList();
        streamhelper = new StreamHelper();

        // 主面板使用 BorderLayout
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 中央區域（輸入區域 + 功能按鈕）
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
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
        
        // 創建左側輸入區域
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout(5, 5));
        
        // 字體設定
        Font font = new Font("Microsoft JhengHei", Font.PLAIN, 14);
        
        // 上半部 - 固定區域：日期選擇和類型選擇
        JPanel topPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        
        // 日期選擇
        dateChooser = new JDateChooser();
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

        JLabel dateLabel = new JLabel("日期:");
        dateLabel.setFont(font);
        topPanel.add(dateLabel);
        topPanel.add(dateChooser);
        
        // 類型選擇（收入/支出）
        String[] types = {"支出", "收入"};
        typeComboBox = new JComboBox<>(types);
        typeComboBox.setFont(font);
        
        JLabel typeLabel = new JLabel("類型:");
        typeLabel.setFont(font);
        topPanel.add(typeLabel);
        topPanel.add(typeComboBox);
        
        // 創建卡片面板存放不同類型的內容
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        
        // 支出面板 - 改為表格式佈局，項目和輸入欄位並排
        JPanel expensePanel = createItemPanel(expenseItems, "支出項目");
        
        // 收入面板 - 同樣使用表格式佈局
        JPanel incomePanel = createItemPanel(incomeItems, "收入項目");
        
        // 添加到卡片面板
        cardPanel.add(expensePanel, "支出");
        cardPanel.add(incomePanel, "收入");
        
        // 底部區域
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        
        // 備註面板
        JPanel notePanel = new JPanel(new BorderLayout(5, 5));
        notePanel.setBorder(BorderFactory.createTitledBorder("帳目備註(若空白則視為無)"));
        
        notefield = new JTextField(20);
        // 移除備註標籤，只保留輸入欄
        notePanel.add(notefield, BorderLayout.CENTER);
        
        // 儲存按鈕面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        enterbutton = new JButton("儲存帳目");
        enterbutton.setFont(font);
        buttonPanel.add(enterbutton);
        
        // 組合底部區域
        bottomPanel.add(notePanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        
        // 組合左側面板
        leftPanel.add(topPanel, BorderLayout.NORTH);
        leftPanel.add(cardPanel, BorderLayout.CENTER);
        leftPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // 類型選擇器變化時切換卡片
        typeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, (String) typeComboBox.getSelectedItem());
                clearInputFields();
            }
        });
        
        // 將左側面板和右側面板加入到中央面板
        centerPanel.add(leftPanel, BorderLayout.CENTER);
        centerPanel.add(rightPanel, BorderLayout.EAST);

        // 上方區域為文字區域
        area = new JTextArea();
        area.setEditable(false); // ← 加這一行讓文字區域唯讀
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setForeground(Color.BLACK);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(null);
        
        // 設定初始文字内容
        area.setText("歡迎使用《記帳小幫手》！\n請輸入今日的支出資料，並點擊『儲存帳目』開始記錄！");

        JScrollPane scroller = new JScrollPane(area,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setPreferredSize(new Dimension(600, 150));

        // 組裝主面板
        panel.add(scroller, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setJMenuBar(createMenuBar());
        frame.setSize(700, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // 註冊各個按鈕事件
        enterbutton.addActionListener(new EnterListener());
        displaybutton.addActionListener(new DisplayListener());
        queryByDateButton.addActionListener(new QueryByDateListener());
        deleteByDateButton.addActionListener(new DeleteByDateListener());
        deletebutton.addActionListener(new DeleteListener());
        searchByNoteButton.addActionListener(new SearchByNoteListener());
        statsButton.addActionListener(new StatsButtonListener());

        // 顯示開始訊息
        showDailyQuote();
    }

    /**
     * 創建表格式項目面板，將項目名稱和輸入欄位並排排列
     * @param items 項目名稱數組
     * @param title 面板標題
     * @return 格式化好的項目面板
     */
    private JPanel createItemPanel(String[] items, String title) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder(title));
        
        // 創建一個網格面板來呈現項目和輸入欄位
        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 10, 15));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 添加表頭
        JLabel headerName = new JLabel("項目名稱", JLabel.CENTER);
        JLabel headerAmount = new JLabel("金額", JLabel.CENTER);
        
        // 設定字體和樣式
        Font headerFont = new Font("Microsoft JhengHei", Font.BOLD, 14);
        headerName.setFont(headerFont);
        headerAmount.setFont(headerFont);
        
        // 添加表頭分隔線效果
        headerName.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        headerAmount.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        
        gridPanel.add(headerName);
        gridPanel.add(headerAmount);
        
        // 為每一個項目創建標籤和輸入欄位
        Font itemFont = new Font("Microsoft JhengHei", Font.PLAIN, 14);
        
        for (String item : items) {
            JLabel label = new JLabel(item + ":", JLabel.LEFT);
            label.setFont(itemFont);
            
            JTextField field = new JTextField(10);
            field.setFont(itemFont);
            
            // 將項目和對應的欄位存入 HashMap
            amountFields.put(item, field);
            
            gridPanel.add(label);
            gridPanel.add(field);
        }
        
        // 創建一個面板來包裹網格面板，以便可以控制間距和佈局
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        wrapperPanel.add(gridPanel, BorderLayout.NORTH);
        
        // 將項目網格放入一個滾動窗格，以便有很多項目時可以滾動
        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(300, 150));
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }
    
    // 清空所有輸入欄位
    private void clearInputFields() {
        for (JTextField field : amountFields.values()) {
            field.setText("");
        }
        notefield.setText("");
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

            // 取得使用者選擇的類型
            int typeIndex = typeComboBox.getSelectedIndex(); // 0: 支出, 1: 收入
            
            // 創建用於保存收入和支出項目的映射表
            Map<String, Integer> expenseItemsMap = new HashMap<>();
            Map<String, Integer> incomeItemsMap = new HashMap<>();
            
            boolean hasNonZero = false;

            // 根據所選類型處理相應欄位
            if (typeIndex == 0) { // 支出
                // 從 amountFields 中取得各項支出金額
                for (String item : expenseItems) {
                    JTextField field = amountFields.get(item);
                    if (field == null) continue;
                    
                    String value = field.getText().trim();
                    if (!value.isEmpty()) {
                        try {
                            int amount = Integer.parseInt(value);
                            if (amount < 0) {
                                errorMsg.append("❌ " + item + "金額不能為負數\n");
                            } else if (amount > 0) {
                                hasNonZero = true;
                                expenseItemsMap.put(item, amount);
                } else {
                                expenseItemsMap.put(item, 0); // 將零金額也添加到映射中
                            }
                        } catch (NumberFormatException e) {
                            errorMsg.append("❌ " + item + "金額格式錯誤（請輸入有效整數）\n");
                        }
                    } else {
                        expenseItemsMap.put(item, 0); // 空白欄位設為 0
                    }
                }
            } else { // 收入
                // 從 amountFields 中取得各項收入金額
                for (String item : incomeItems) {
                    JTextField field = amountFields.get(item);
                    if (field == null) continue;
                    
                    String value = field.getText().trim();
                    if (!value.isEmpty()) {
                        try {
                            int amount = Integer.parseInt(value);
                            if (amount < 0) {
                                errorMsg.append("❌ " + item + "金額不能為負數\n");
                            } else if (amount > 0) {
                                hasNonZero = true;
                                incomeItemsMap.put(item, amount);
                            } else {
                                incomeItemsMap.put(item, 0); // 將零金額也添加到映射中
                        }
                    } catch (NumberFormatException e) {
                            errorMsg.append("❌ " + item + "金額格式錯誤（請輸入有效整數）\n");
                        }
                    } else {
                        incomeItemsMap.put(item, 0); // 空白欄位設為 0
                    }
                }
            }

            if (!hasNonZero) {
                errorMsg.append("❌ 至少要輸入一個大於0的金額\n");
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

            // 計算支出和收入總額
            int totalExpense = 0;
            for (Integer value : expenseItemsMap.values()) {
                totalExpense += value;
            }
            
            int totalIncome = 0;
            for (Integer value : incomeItemsMap.values()) {
                totalIncome += value;
            }
            
            int net = totalIncome - totalExpense;

            // 更新或新增帳目
            boolean accountExists = false;

            for (int i = 0; i < accountList.size(); i++) {
                Account existingAccount = accountList.get(i);

                // 如果日期相同，則更新該筆帳目
                if (existingAccount.getDate().equals(date)) {
                    // 更新所有項目的金額
                    for (Map.Entry<String, Integer> entry : expenseItemsMap.entrySet()) {
                        existingAccount.setExpenseItem(entry.getKey(), entry.getValue());
                    }
                    
                    for (Map.Entry<String, Integer> entry : incomeItemsMap.entrySet()) {
                        existingAccount.setIncomeItem(entry.getKey(), entry.getValue());
                    }
                    
                    existingAccount.setNet(net);
                    existingAccount.setNote(note);
                    
                    StringBuilder updatedItems = new StringBuilder();
                    
                    // 添加支出項目到訊息
                    for (Map.Entry<String, Integer> entry : expenseItemsMap.entrySet()) {
                        if (entry.getValue() > 0) {
                            updatedItems.append(entry.getKey()).append(": ")
                                        .append(entry.getValue()).append("元 ");
                        }
                    }
                    
                    // 添加收入項目到訊息
                    for (Map.Entry<String, Integer> entry : incomeItemsMap.entrySet()) {
                        if (entry.getValue() > 0) {
                            updatedItems.append(entry.getKey()).append(": ")
                                        .append(entry.getValue()).append("元 ");
                        }
                    }
                    
                    area.setText("✅ 帳目已更新！ 日期：" + date + "\n" + updatedItems);
                    accountExists = true;
                    break;
                }
            }

            // 如果沒有找到相同日期的帳目，則新增一筆
            if (!accountExists) {
                account = new Account(date, expenseItemsMap, incomeItemsMap, note);
                accountList.add(account);
                account = null;
                
                StringBuilder updatedItems = new StringBuilder();
                
                // 添加支出項目到訊息
                for (Map.Entry<String, Integer> entry : expenseItemsMap.entrySet()) {
                    if (entry.getValue() > 0) {
                        updatedItems.append(entry.getKey()).append(": ")
                                    .append(entry.getValue()).append("元 ");
                    }
                }
                
                // 添加收入項目到訊息
                for (Map.Entry<String, Integer> entry : incomeItemsMap.entrySet()) {
                    if (entry.getValue() > 0) {
                        updatedItems.append(entry.getKey()).append(": ")
                                    .append(entry.getValue()).append("元 ");
                    }
                }
                
                area.setText("✅ 帳目建立成功！ 日期：" + date + "\n" + updatedItems);
            }

            // 清空欄位
            clearInputFields();
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
                        
                        // 初始化統計資料
                        Map<String, Integer> expenseTotals = new HashMap<>();
                        Map<String, Integer> incomeTotals = new HashMap<>();
                        
                        // 初始化所有可能的項目類別，設定為0
                        for (String item : expenseItems) {
                            expenseTotals.put(item, 0);
                        }
                        
                        for (String item : incomeItems) {
                            incomeTotals.put(item, 0);
                        }
                        
                        // 初始化月度資料
                        double[][] monthlyExpenseByCategory = new double[expenseItems.length][12]; // [類別][月份]
                        double[][] monthlyIncomeByCategory = new double[incomeItems.length][12];   // [類別][月份]
                        double[] monthlyTotalExpense = new double[12];
                        double[] monthlyTotalIncome = new double[12];
                        
                        int totalExpense = 0;
                        int totalIncome = 0;

                        // 遍歷所有帳目，統計年度數據
                        for (int i = 0; i < accountList.size(); i++) {
                            Account acc = accountList.get(i);
                            if (acc.getDate().startsWith(year)) {
                                // 取得月份（假設格式是 yyyy/MM/dd）
                                String[] dateParts = acc.getDate().split("/");
                                int monthIndex = -1;
                                if (dateParts.length >= 2) {
                                    monthIndex = Integer.parseInt(dateParts[1]) - 1; // 0-based index
                                }
                                
                                // 累加各支出項目金額
                                Map<String, Integer> accountExpenses = acc.getExpenseItems();
                                for (int categoryIndex = 0; categoryIndex < expenseItems.length; categoryIndex++) {
                                    String category = expenseItems[categoryIndex];
                                    int amount = accountExpenses.getOrDefault(category, 0);
                                    
                                    // 累加到總額
                                    expenseTotals.put(category, expenseTotals.get(category) + amount);
                                    totalExpense += amount;
                                    
                                    // 如果找到月份，也累加到月度資料
                                    if (monthIndex >= 0 && monthIndex < 12) {
                                        monthlyExpenseByCategory[categoryIndex][monthIndex] += amount;
                                        monthlyTotalExpense[monthIndex] += amount;
                                    }
                                }
                                
                                // 累加各收入項目金額
                                Map<String, Integer> accountIncomes = acc.getIncomeItems();
                                for (int categoryIndex = 0; categoryIndex < incomeItems.length; categoryIndex++) {
                                    String category = incomeItems[categoryIndex];
                                    int amount = accountIncomes.getOrDefault(category, 0);
                                    
                                    // 累加到總額
                                    incomeTotals.put(category, incomeTotals.get(category) + amount);
                                    totalIncome += amount;
                                    
                                    // 如果找到月份，也累加到月度資料
                                    if (monthIndex >= 0 && monthIndex < 12) {
                                        monthlyIncomeByCategory[categoryIndex][monthIndex] += amount;
                                        monthlyTotalIncome[monthIndex] += amount;
                                    }
                                }
                            }
                        }

                        // 建立詳細的統計結果文字
                        StringBuilder statsMessage = new StringBuilder(year + "年統計：\n");
                        
                        // 添加各支出項目統計
                        statsMessage.append("\n【支出項目統計】\n");
                        for (String category : expenseItems) {
                            int amount = expenseTotals.getOrDefault(category, 0);
                            if (amount > 0) {
                                statsMessage.append(category).append("總支出：").append(amount).append(" 元\n");
                            }
                        }
                        statsMessage.append("全部總支出：").append(totalExpense).append(" 元\n");
                        
                        // 添加各收入項目統計
                        statsMessage.append("\n【收入項目統計】\n");
                        for (String category : incomeItems) {
                            int amount = incomeTotals.getOrDefault(category, 0);
                            if (amount > 0) {
                                statsMessage.append(category).append("總收入：").append(amount).append(" 元\n");
                            }
                        }
                        statsMessage.append("全部總收入：").append(totalIncome).append(" 元\n");
                        
                        // 添加淨收支統計
                        statsMessage.append("\n【淨收支統計】\n");
                        statsMessage.append("全年淨收入：").append(totalIncome - totalExpense).append(" 元\n");
                        
                        JOptionPane.showMessageDialog(statsFrame, statsMessage.toString(), "年統計結果", JOptionPane.INFORMATION_MESSAGE);

                        // === 建立「每月總支出」的圖表 ===
                        DefaultCategoryDataset monthlyDataset = new DefaultCategoryDataset();
                        String[] monthNames = { "1月", "2月", "3月", "4月", "5月", "6月", 
                                                "7月", "8月", "9月", "10月", "11月", "12月" };

                        // 將每個月的總支出數據添加到資料集
                        for (int i = 0; i < 12; i++) {
                            monthlyDataset.addValue(monthlyTotalExpense[i], "總支出", monthNames[i]);
                        }

                        JFreeChart monthlyChart = ChartFactory.createBarChart(
                            year + "年每月總支出",
                            "月份",
                            "金額（元）",
                            monthlyDataset,
                            PlotOrientation.VERTICAL,
                            true,
                            true,
                            false
                        );

                        ChartPanel monthlyChartPanel = new ChartPanel(monthlyChart);

                        // === 建立「支出項目」的圓餅圖 ===
                        DefaultPieDataset pieDataset = new DefaultPieDataset();

                        // 將各項支出添加到圓餅圖資料集
                        for (String category : expenseItems) {
                            int amount = expenseTotals.getOrDefault(category, 0);
                            if (amount > 0) {
                                pieDataset.setValue(category, amount);
                            }
                        }

                        JFreeChart pieChart = ChartFactory.createPieChart(
                            year + "年各類別總支出",
                            pieDataset,
                            true,
                            true,
                            false
                        );

                        ChartPanel pieChartPanel = new ChartPanel(pieChart);

                        // === 建立「每月收入」長條圖 ===
                        DefaultCategoryDataset incomeDataset = new DefaultCategoryDataset();

                        // 將每個月的總收入數據添加到資料集
                        for (int i = 0; i < 12; i++) {
                            incomeDataset.addValue(monthlyTotalIncome[i], "總收入", monthNames[i]);
                        }

                        JFreeChart incomeChart = ChartFactory.createBarChart(
                            year + "年每月總收入",
                            "月份",
                            "金額（元）",
                            incomeDataset,
                            PlotOrientation.VERTICAL,
                            true,
                            true,
                            false
                        );

                        // 設定長條顏色為藍色
                        CategoryPlot incomePlot = incomeChart.getCategoryPlot();
                        BarRenderer incomeRenderer = (BarRenderer) incomePlot.getRenderer();
                        incomeRenderer.setSeriesPaint(0, new Color(0, 0, 255)); // 收入改成藍色

                        ChartPanel incomeChartPanel = new ChartPanel(incomeChart);

                        // === 建立「收入項目」的圓餅圖 ===
                        DefaultPieDataset incomePieDataset = new DefaultPieDataset();

                        // 將各項收入添加到圓餅圖資料集
                        for (String category : incomeItems) {
                            int amount = incomeTotals.getOrDefault(category, 0);
                            if (amount > 0) {
                                incomePieDataset.setValue(category, amount);
                            }
                        }

                        JFreeChart incomePieChart = ChartFactory.createPieChart(
                            year + "年各類別總收入",
                            incomePieDataset,
                            true,
                            true,
                            false
                        );

                        ChartPanel incomePieChartPanel = new ChartPanel(incomePieChart);

                        // === 建立視窗，同時顯示四張圖表 ===
                        JPanel chartsPanel = new JPanel(new GridLayout(2, 2));
                        chartsPanel.add(monthlyChartPanel);     // 每月支出長條圖
                        chartsPanel.add(pieChartPanel);         // 支出項目圓餅圖
                        chartsPanel.add(incomeChartPanel);      // 每月收入長條圖
                        chartsPanel.add(incomePieChartPanel);   // 收入項目圓餅圖

                        JFrame chartFrame = new JFrame(year + "年收支圖表");
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

                        // 初始化統計資料
                        Map<String, Integer> expenseTotals = new HashMap<>();
                        Map<String, Integer> incomeTotals = new HashMap<>();
                        
                        // 初始化所有可能的項目類別，設定為0
                        for (String item : expenseItems) {
                            expenseTotals.put(item, 0);
                        }
                        
                        for (String item : incomeItems) {
                            incomeTotals.put(item, 0);
                        }
                        
                        // 初始化日度支出和收入資料
                        double[][] dailyExpenseByCategory = new double[expenseItems.length][31]; // [類別][日]
                        double[][] dailyIncomeByCategory = new double[incomeItems.length][31];   // [類別][日]
                        double[] dailyTotalExpense = new double[31];
                        double[] dailyTotalIncome = new double[31];
                        
                        int totalExpense = 0;
                        int totalIncome = 0;
                        
                        // 遍歷所有帳目，查找符合年月的資料
                        for (int i = 0; i < accountList.size(); i++) {
                            Account acc = accountList.get(i);
                            if (acc.getDate().startsWith(month)) {
                                // 解析日期，取得日
                                String[] dateParts = acc.getDate().split("/");
                                int dayIndex = -1;
                                if (dateParts.length >= 3) {
                                    dayIndex = Integer.parseInt(dateParts[2]) - 1; // 0-based index
                                }
                                
                                // 累加各支出項目金額
                                Map<String, Integer> accountExpenses = acc.getExpenseItems();
                                for (int categoryIndex = 0; categoryIndex < expenseItems.length; categoryIndex++) {
                                    String category = expenseItems[categoryIndex];
                                    int amount = accountExpenses.getOrDefault(category, 0);
                                    
                                    // 累加到總額
                                    expenseTotals.put(category, expenseTotals.get(category) + amount);
                                    totalExpense += amount;
                                    
                                    // 如果找到日，也累加到日度資料
                                    if (dayIndex >= 0 && dayIndex < 31) {
                                        dailyExpenseByCategory[categoryIndex][dayIndex] += amount;
                                        dailyTotalExpense[dayIndex] += amount;
                                    }
                                }
                                
                                // 累加各收入項目金額
                                Map<String, Integer> accountIncomes = acc.getIncomeItems();
                                for (int categoryIndex = 0; categoryIndex < incomeItems.length; categoryIndex++) {
                                    String category = incomeItems[categoryIndex];
                                    int amount = accountIncomes.getOrDefault(category, 0);
                                    
                                    // 累加到總額
                                    incomeTotals.put(category, incomeTotals.get(category) + amount);
                                    totalIncome += amount;
                                    
                                    // 如果找到日，也累加到日度資料
                                    if (dayIndex >= 0 && dayIndex < 31) {
                                        dailyIncomeByCategory[categoryIndex][dayIndex] += amount;
                                        dailyTotalIncome[dayIndex] += amount;
                                    }
                                }
                            }
                        }

                        // 建立詳細的統計結果文字
                        StringBuilder statsMessage = new StringBuilder(month + " 統計：\n");
                        
                        // 添加各支出項目統計
                        statsMessage.append("\n【支出項目統計】\n");
                        for (String category : expenseItems) {
                            int amount = expenseTotals.getOrDefault(category, 0);
                            if (amount > 0) {
                                statsMessage.append(category).append("總支出：").append(amount).append(" 元\n");
                            }
                        }
                        statsMessage.append("全部總支出：").append(totalExpense).append(" 元\n");
                        
                        // 添加各收入項目統計
                        statsMessage.append("\n【收入項目統計】\n");
                        for (String category : incomeItems) {
                            int amount = incomeTotals.getOrDefault(category, 0);
                            if (amount > 0) {
                                statsMessage.append(category).append("總收入：").append(amount).append(" 元\n");
                            }
                        }
                        statsMessage.append("全部總收入：").append(totalIncome).append(" 元\n");
                        
                        // 添加淨收支統計
                        statsMessage.append("\n【淨收支統計】\n");
                        statsMessage.append("當月淨收入：").append(totalIncome - totalExpense).append(" 元\n");

                        JOptionPane.showMessageDialog(statsFrame, statsMessage.toString(), "月統計結果", JOptionPane.INFORMATION_MESSAGE);

                        // === 準備每日收入與支出的折線圖資料集 ===
                        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
                        String expenseLine = "每日支出";
                        String incomeLine = "每日收入";

                        for (int d = 1; d <= day; d++) {
                            String dayStr = String.valueOf(d); // 天數標籤 1-31
                            int dayIndex = d - 1; // 轉換為陣列索引
                            
                            // 添加每日總支出和總收入
                            lineDataset.addValue(dailyTotalExpense[dayIndex], expenseLine, dayStr);
                            lineDataset.addValue(dailyTotalIncome[dayIndex], incomeLine, dayStr);
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

                        // === 準備支出項目的圓餅圖資料集 ===
                        DefaultPieDataset pieDataset = new DefaultPieDataset();
                        
                        // 將各項支出添加到圓餅圖資料集
                        for (String category : expenseItems) {
                            int amount = expenseTotals.getOrDefault(category, 0);
                            if (amount > 0) {
                                pieDataset.setValue(category, amount);
                            }
                        }

                        JFreeChart pieChart = ChartFactory.createPieChart(
                            month + " 各類別總支出",
                            pieDataset,
                            true,
                            true,
                            false
                        );

                        ChartPanel pieChartPanel = new ChartPanel(pieChart);

                        // === 準備收入項目的圓餅圖資料集 ===
                        DefaultPieDataset incomePieDataset = new DefaultPieDataset();
                        
                        // 將各項收入添加到圓餅圖資料集
                        for (String category : incomeItems) {
                            int amount = incomeTotals.getOrDefault(category, 0);
                            if (amount > 0) {
                                incomePieDataset.setValue(category, amount);
                            }
                        }

                        JFreeChart incomePieChart = ChartFactory.createPieChart(
                            month + " 各類別總收入",
                            incomePieDataset,
                            true,
                            true,
                            false
                        );

                        ChartPanel incomePieChartPanel = new ChartPanel(incomePieChart);

                        // === 建立面板，顯示多張圖 ===
                        JPanel chartsPanel = new JPanel(new GridLayout(2, 2));
                        chartsPanel.add(lineChartPanel);        // 每日收支折線圖
                        chartsPanel.add(pieChartPanel);         // 支出項目圓餅圖
                        chartsPanel.add(incomePieChartPanel);   // 收入項目圓餅圖
                        
                        // 其餘空間保留或添加其他圖表
                        JPanel infoPanel = new JPanel();
                        infoPanel.add(new JLabel(month + " 月收支統計圖表"));
                        chartsPanel.add(infoPanel);

                        JFrame chartFrame = new JFrame(month + " 收支圖表");
                        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        chartFrame.add(chartsPanel);
                        chartFrame.setSize(1200, 800);
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
