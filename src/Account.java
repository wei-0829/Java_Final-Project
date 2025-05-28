import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Account implements Serializable {
    // 記帳日期
    private String date;                  // 記帳日期（格式範例："2025/05/12"）
    
    // 支出與收入項目的映射表
    private Map<String, Integer> expenseItems = new HashMap<>();  // 各項支出金額
    private Map<String, Integer> incomeItems = new HashMap<>();   // 各項收入金額
    
    private int net;                      // 淨額（收入 - 支出）
    private String note;                  // 備註內容

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
        this.net = getTotalIncome() - getTotalExpense();
        this.note = note;
    }

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

    public int getExpenseItem(String itemName) {
        return expenseItems.getOrDefault(itemName, 0);
    }
    
    public int getIncomeItem(String itemName) {
        return incomeItems.getOrDefault(itemName, 0);
    }
    
    public Map<String, Integer> getExpenseItems() {
        return new HashMap<>(expenseItems); // 回傳複本以保護內部資料
    }
    
    public Map<String, Integer> getIncomeItems() {
        return new HashMap<>(incomeItems); // 回傳複本以保護內部資料
    }
    
    public int getTotalExpense() {
        int total = 0;
        for (int amount : expenseItems.values()) {
            total += amount;
        }
        return total;
    }
    
    public int getTotalIncome() {
        int total = 0;
        for (int amount : incomeItems.values()) {
            total += amount;
        }
        return total;
    }

    public int getNetAmount() {
        return getTotalIncome() - getTotalExpense();
    }

    public String getDate() {
        return date;
    }

    public void setExpenseItem(String itemName, int amount) {
        expenseItems.put(itemName, amount);
        updateNet();
    }
    
    public void setIncomeItem(String itemName, int amount) {
        incomeItems.put(itemName, amount);
        updateNet();
    }
    
    private void updateNet() {
        this.net = getTotalIncome() - getTotalExpense();
    }

    public int getNet() {
        return net;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return printAccount();
    }
}
