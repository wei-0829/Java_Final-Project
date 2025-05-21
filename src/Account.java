import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 帳目資料類別，每筆記帳資料就是一個 Account 物件
 */
public class Account implements Serializable {
    // 記帳日期
    private String date;                  // 記帳日期（格式範例："2025/05/12"）
    
    // 支出與收入項目的映射表
    private Map<String, Integer> expenseItems = new HashMap<>();  // 各項支出金額
    private Map<String, Integer> incomeItems = new HashMap<>();   // 各項收入金額
    
    private int net;                      // 淨額（收入 - 支出）
    private String note;                  // 備註內容

    /**
     * 建構子：建立一筆帳目資料（舊版相容）
     * @param date 記帳日期
     * @param breakfast 早餐支出
     * @param lunch 午餐支出
     * @param dinner 晚餐支出
     * @param others 其他支出
     * @param income 額外收入
     * @param net 淨額（收入 - 支出）
     * @param note 備註內容
     */
    public Account(String date, int breakfast, int lunch, int dinner, int others, int income, int net, String note) {
        this.date = date;
        
        // 初始化支出項目
        this.expenseItems.put("早餐", breakfast);
        this.expenseItems.put("午餐", lunch);
        this.expenseItems.put("晚餐", dinner);
        this.expenseItems.put("其他", others);
        
        // 初始化收入項目
        this.incomeItems.put("額外收入", income);
        
        this.net = net;
        this.note = note;
    }
    
    /**
     * 新建構子：從Map直接建立帳目資料
     * @param date 記帳日期
     * @param expenseItems 支出項目映射表
     * @param incomeItems 收入項目映射表
     * @param note 備註內容
     */
    public Account(String date, Map<String, Integer> expenseItems, Map<String, Integer> incomeItems, String note) {
        this.date = date;
        
        // 複製映射表，避免外部引用修改內部資料
        if (expenseItems != null) {
            this.expenseItems.putAll(expenseItems);
        }
        
        if (incomeItems != null) {
            this.incomeItems.putAll(incomeItems);
        }
        
        // 計算淨額
        int totalExpense = getTotalExpense();
        int totalIncome = getTotalIncome();
        this.net = totalIncome - totalExpense;
        
        this.note = note;
    }

    /**
     * 回傳格式化後的帳目資訊（用於顯示）
     * @return 帳目詳細資訊的字串（包含所有支出和總計）
     */
    public String printAccount() {
        StringBuilder result = new StringBuilder();
        result.append("日期：").append(date).append(" | ");
        
        // 新增所有支出項目
        for (Map.Entry<String, Integer> entry : expenseItems.entrySet()) {
            if (entry.getValue() > 0) {
                result.append(entry.getKey()).append("：")
                      .append(entry.getValue()).append(" 元 | ");
            }
        }
        
        // 新增所有收入項目
        for (Map.Entry<String, Integer> entry : incomeItems.entrySet()) {
            if (entry.getValue() > 0) {
                result.append(entry.getKey()).append("：")
                      .append(entry.getValue()).append(" 元 | ");
            }
        }
        
        // 新增淨額和備註
        result.append("淨額：").append(net).append(" 元 | ")
              .append("備註：").append(note);
        
        return result.toString();
    }

    // 以下為 getter 方法，提供取得各項資料的方式（支出金額與日期）

    /**
     * 取得指定支出項目金額，如果不存在則回傳0
     */
    public int getExpenseItem(String itemName) {
        return expenseItems.getOrDefault(itemName, 0);
    }
    
    /**
     * 取得指定收入項目金額，如果不存在則回傳0
     */
    public int getIncomeItem(String itemName) {
        return incomeItems.getOrDefault(itemName, 0);
    }
    
    /**
     * 取得所有支出項目的映射表
     */
    public Map<String, Integer> getExpenseItems() {
        return new HashMap<>(expenseItems); // 回傳複本以保護內部資料
    }
    
    /**
     * 取得所有收入項目的映射表
     */
    public Map<String, Integer> getIncomeItems() {
        return new HashMap<>(incomeItems); // 回傳複本以保護內部資料
    }
    
    // 為了向後相容保留原始的getter
    public int getBreakfast() {
        return getExpenseItem("早餐");
    }

    public int getLunch() {
        return getExpenseItem("午餐");
    }

    public int getDinner() {
        return getExpenseItem("晚餐");
    }

    public int getOthers() {
        return getExpenseItem("其他");
    }

    public int getIncome() {
        return getIncomeItem("額外收入");
    }

    public int getNet() {
        return net;
    }

    public String getNote() {
        return note;
    }

    /**
     * 回傳總支出金額
     * @return 總支出金額
     */
    public int getTotalExpense() {
        int total = 0;
        for (int amount : expenseItems.values()) {
            total += amount;
        }
        return total;
    }
    
    /**
     * 回傳總收入金額
     * @return 總收入金額
     */
    public int getTotalIncome() {
        int total = 0;
        for (int amount : incomeItems.values()) {
            total += amount;
        }
        return total;
    }
    
    /**
     * 為向後相容保留的方法
     */
    public int getTotal() {
        return getTotalExpense();
    }

    /**
     * 回傳淨額（收入 - 支出）
     * @return 淨額
     */
    public int getNetAmount() {
        return getTotalIncome() - getTotalExpense();
    }

    /**
     * 回傳記帳日期
     * @return 日期字串
     */
    public String getDate() {
        return date;
    }

    // 以下為 setter 方法，允許更新各項支出與收入金額

    /**
     * 設定指定支出項目的金額
     */
    public void setExpenseItem(String itemName, int amount) {
        if (amount >= 0) {
            expenseItems.put(itemName, amount);
            updateNet();
        }
    }
    
    /**
     * 設定指定收入項目的金額
     */
    public void setIncomeItem(String itemName, int amount) {
        if (amount >= 0) {
            incomeItems.put(itemName, amount);
            updateNet();
        }
    }
    
    /**
     * 重新計算並更新淨額
     */
    private void updateNet() {
        this.net = getTotalIncome() - getTotalExpense();
    }
    
    // 為了向後相容保留原始的setter
    public void setBreakfast(int breakfast) {
        setExpenseItem("早餐", breakfast);
    }

    public void setLunch(int lunch) {
        setExpenseItem("午餐", lunch);
    }

    public void setDinner(int dinner) {
        setExpenseItem("晚餐", dinner);
    }

    public void setOthers(int others) {
        setExpenseItem("其他", others);
    }

    public void setIncome(int income) {
        setIncomeItem("額外收入", income);
    }

    public void setNet(int net) {
        this.net = net;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // 可以選擇加入toString方法，以便輸出帳目資訊時使用
    @Override
    public String toString() {
        return printAccount();
    }
}
