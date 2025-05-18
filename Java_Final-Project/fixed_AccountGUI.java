package AccountProgram;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;

public class AccountGUI {
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
} 