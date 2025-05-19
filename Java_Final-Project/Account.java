package AccountProgram;

import java.io.Serializable;

/**
 * 帳目資料類別，每筆記帳資料就是一個 Account 物件
 */
public class Account implements Serializable {
    private int breakfast;  // 早餐支出金額
    private int lunch;      // 午餐支出金額
    private int dinner;     // 晚餐支出金額
    private int others;     // 其他支出金額
    private int income;     // 收入金額
    private String date;    // 記帳日期（格式範例："2025/05/12"）
    private String note;    // 備註內容

    /**
     * 建構子：建立一筆帳目資料
     */
    public Account(int breakfast, int lunch, int dinner, int others, int income, String date, String note) {
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.others = others;
        this.income = income;
        this.date = date;
        this.note = note;
    }

    /**
     * 回傳格式化後的帳目資訊（用於顯示）
     */
    public String printAccount() {
        StringBuilder sb = new StringBuilder();
        sb.append("日期：").append(date);
        if (breakfast > 0) sb.append(" | 早餐：").append(breakfast).append(" 元");
        if (lunch > 0) sb.append(" | 午餐：").append(lunch).append(" 元");
        if (dinner > 0) sb.append(" | 晚餐：").append(dinner).append(" 元");
        if (others > 0) sb.append(" | 其他：").append(others).append(" 元");
        if (income > 0) sb.append(" | 收入：").append(income).append(" 元");
        int totalExpenses = breakfast + lunch + dinner + others;
        if (totalExpenses > 0) sb.append(" | 總支出：").append(totalExpenses).append(" 元");
        sb.append(" | 備註：").append(note);
        return sb.toString();
    }

    // Getter 方法
    public int getBreakfast() {
        return breakfast;
    }

    public int getLunch() {
        return lunch;
    }

    public int getDinner() {
        return dinner;
    }

    public int getOthers() {
        return others;
    }

    public int getIncome() {
        return income;
    }

    public String getNote() {
        return note;
    }

    public int getTotal() {
        return breakfast + lunch + dinner + others;
    }

    // Setter 方法
    public void setBreakfast(int breakfast) {
        this.breakfast = breakfast;
    }

    public void setLunch(int lunch) {
        this.lunch = lunch;
    }

    public void setDinner(int dinner) {
        this.dinner = dinner;
    }

    public void setOthers(int others) {
        this.others = others;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return printAccount();
    }
}