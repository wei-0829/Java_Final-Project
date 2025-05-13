package AccountProgram;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AccountGUI {
    // GUI 元件與變數定義
    private JFrame frame;
    private JPanel panel;
    private AccountList accountList;  // 儲存帳目資料的容器
    private Account account;          // 暫存使用者輸入的帳目
    private JButton enterbutton, displaybutton, queryByDateButton, deleteByDateButton, deletebutton, statsButton;
    private JTextArea area;           // 顯示訊息的文字區域
    private JTextField datefield, breakfastfield, lunchfield, dinnerfield, othersfield;
    private StreamHelper streamhelper; // 負責檔案讀寫的工具
    private JMenu menu;

    // 建立整體 GUI
    public void buildGUI() {
        frame = new JFrame("記帳小幫手");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        accountList = new AccountList();
        streamhelper = new StreamHelper();

        panel = new JPanel(new BorderLayout());

        // 建立畫面左右中三個區塊
        Box leftbox = new Box(BoxLayout.Y_AXIS);
        Box centerbox = new Box(BoxLayout.Y_AXIS);
        Box rightbox = new Box(BoxLayout.Y_AXIS);
        panel.add(BorderLayout.WEST, leftbox);
        panel.add(BorderLayout.CENTER, centerbox);
        panel.add(BorderLayout.EAST, rightbox);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 建立訊息顯示區域
        area = new JTextArea(10, 10);
        area.setLineWrap(true);
        area.setSelectionColor(new Color(173, 216, 230));
        area.setSelectedTextColor(Color.BLACK);
        JScrollPane scroller = new JScrollPane(area);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(BorderLayout.NORTH, scroller);

        // 建立輸入欄位
        datefield = new JTextField(10);
        breakfastfield = new JTextField(5);
        lunchfield = new JTextField(5);
        dinnerfield = new JTextField(5);
        othersfield = new JTextField(5);
        centerbox.add(datefield);
        centerbox.add(breakfastfield);
        centerbox.add(lunchfield);
        centerbox.add(dinnerfield);
        centerbox.add(othersfield);

        // 建立標籤欄位
        Font font = new Font("LALA", Font.BOLD, 14);
        JLabel datelabel = new JLabel("日期（格式為YYYY/MM/DD）");
        datelabel.setFont(font);
        JLabel breakfastlabel = new JLabel("早餐支出");
        breakfastlabel.setFont(font);
        JLabel lunchlabel = new JLabel("午餐支出");
        lunchlabel.setFont(font);
        JLabel dinnerlabel = new JLabel("晚餐支出");
        dinnerlabel.setFont(font);
        JLabel otherslabel = new JLabel("其他支出");
        otherslabel.setFont(font);
        leftbox.add(datelabel);
        leftbox.add(breakfastlabel);
        leftbox.add(lunchlabel);
        leftbox.add(dinnerlabel);
        leftbox.add(otherslabel);

        // 建立功能按鈕
        enterbutton = new JButton("儲存帳目");
        displaybutton = new JButton("列出所有帳目資料");
        queryByDateButton = new JButton("查詢指定日期帳目");
        deleteByDateButton = new JButton("刪除指定日期帳目");
        deletebutton = new JButton("清除所有帳目資料");
        statsButton = new JButton("查看所有帳目統計");

        // 將按鈕加入畫面
        centerbox.add(enterbutton);
        rightbox.add(displaybutton);
        rightbox.add(queryByDateButton);
        rightbox.add(deleteByDateButton);
        rightbox.add(deletebutton);
        rightbox.add(statsButton);

        // 設定選單列
        menu = new JMenu("檔案管理");
        JMenuBar menuBar = new JMenuBar();
        JMenuItem saveMenuItem = new JMenuItem("另存帳目新檔");
        JMenuItem loadMenuItem = new JMenuItem("讀取帳目檔案");
        saveMenuItem.addActionListener(new SaveMenuListener());
        loadMenuItem.addActionListener(new LoadMenuListener());
        menu.add(saveMenuItem);
        menu.add(loadMenuItem);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        // 註冊按鈕監聽器
        displaybutton.addActionListener(new DisplayListener());
        enterbutton.addActionListener(new EnterListener());
        queryByDateButton.addActionListener(new QueryByDateListener());
        deleteByDateButton.addActionListener(new DeleteByDateListener());
        deletebutton.addActionListener(new DeleteListener());
        statsButton.addActionListener(new StatsButtonListener());

        frame.setSize(400, 400);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    // 輸入帳目按鈕：檢查欄位並建立帳目物件
    public class EnterListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            StringBuilder errorMsg = new StringBuilder();
            String date = datefield.getText().trim();

            if (!isValidDate(date)) {
                errorMsg.append("❌ 日期格式錯誤（請輸入：YYYY/MM/DD）\n");
            }

            int breakfast = 0, lunch = 0, dinner = 0, others = 0;

            try {
                breakfast = Integer.parseInt(breakfastfield.getText().trim());
                lunch = Integer.parseInt(lunchfield.getText().trim());
                dinner = Integer.parseInt(dinnerfield.getText().trim());
                others = Integer.parseInt(othersfield.getText().trim());

                if (breakfast < 0 || lunch < 0 || dinner < 0 || others < 0) {
                    errorMsg.append("❌ 金額不能為負數\n");
                }
            } catch (NumberFormatException e) {
                errorMsg.append("❌ 金額欄位格式錯誤（請輸入有效整數）\n");
            }

            if (errorMsg.length() > 0) {
                area.setText(errorMsg.toString());
                return;
            }

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
                    area.setText("✅ 帳目已更新！ 日期：" + date);
                    accountExists = true;
                    break;
                }
            }

            // 如果沒有相同日期的帳目，則新增一筆帳目
            if (!accountExists) {
                account = new Account(breakfast, lunch, dinner, others, date);
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
        }

        // 日期格式與範圍驗證
        private boolean isValidDate(String dateStr) {
            if (!dateStr.matches("\\d{4}/\\d{2}/\\d{2}")) return false;

            try {
                String[] parts = dateStr.split("/");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);

                if (month < 1 || month > 12) return false;

                int[] daysInMonth = { 31, isLeapYear(year) ? 29 : 28, 31, 30, 31, 30,
                                    31, 31, 30, 31, 30, 31 };

                return day >= 1 && day <= daysInMonth[month - 1];
            } catch (Exception e) {
                return false;
            }
        }

        private boolean isLeapYear(int year) {
            return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
        }
    }

    // 顯示所有帳目資料
    public class DisplayListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            if (accountList.size() > 0) {
                area.setText("");
                for (int i = 0; i < accountList.size(); i++) {
                    area.append(accountList.get(i).printAccount() + "\n");
                }
            } else {
                area.setText("⚠️ 目前沒有任何帳目資料");
            }
        }
    }

    public class QueryByDateListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            String date = JOptionPane.showInputDialog(frame, "請輸入查詢日期（格式：YYYY/MM/DD）：");

            if (date == null) return; // 使用者取消
            if (!date.matches("\\d{4}/\\d{2}/\\d{2}")) {
                JOptionPane.showMessageDialog(frame, "❌ 日期格式錯誤，請輸入：YYYY/MM/DD", "格式錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (int i = 0; i < accountList.size(); i++) {
                Account acc = accountList.get(i);
                if (acc.getDate().equals(date)) {
                    area.setText("🔎 查詢結果：\n" + acc.printAccount());
                    return;
                }
            }

            area.setText("⚠️ 查無 " + date + " 的帳目資料");
        }
    }

    // 刪除特定帳目監聽器
    public class DeleteByDateListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            String date = JOptionPane.showInputDialog(frame, "請輸入要刪除的日期（格式：YYYY/MM/DD）：");

            if (date == null) return; // 使用者取消輸入

            if (!date.matches("\\d{4}/\\d{2}/\\d{2}")) {
                JOptionPane.showMessageDialog(frame, "❌ 日期格式錯誤，請輸入：YYYY/MM/DD", "格式錯誤", JOptionPane.ERROR_MESSAGE);
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

            JOptionPane.showMessageDialog(frame, "⚠️ 查無 " + date + " 的帳目資料", "資料未找到", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // 清除所有帳目
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
            return;
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
                public void actionPerformed(ActionEvent e) {
                    String year = JOptionPane.showInputDialog(statsFrame, "請輸入年份（例如：2025）");

                    if (year == null) return; // 按下取消或關閉
                    if (!year.matches("\\d{4}")) {
                        JOptionPane.showMessageDialog(statsFrame, "❌ 輸入格式錯誤，請輸入 4 位數的年份，例如：2025", "格式錯誤", JOptionPane.ERROR_MESSAGE);
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

            // 月統計查詢邏輯
            monthButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String month = JOptionPane.showInputDialog(statsFrame, "請輸入年份和月份（格式：YYYY/MM）");

                    if (month == null) return;
                    if (!month.matches("\\d{4}/\\d{2}")) {
                        JOptionPane.showMessageDialog(statsFrame, "❌ 輸入格式錯誤，請輸入正確的年月格式，例如：2025/05", "格式錯誤", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String[] parts = month.split("/");
                    int m = Integer.parseInt(parts[1]);
                    if (m < 1 || m > 12) {
                        JOptionPane.showMessageDialog(statsFrame, "❌ 月份錯誤，請輸入介於 01 到 12 的月份", "格式錯誤", JOptionPane.ERROR_MESSAGE);
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
