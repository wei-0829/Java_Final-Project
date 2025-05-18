package AccountProgram;

import java.io.Serializable;

/**
 * 帳目資料類別，每筆記帳資料就是一個 Account 物件
 */
public class Account implements Serializable {
    // 各餐別的花費金額，以及該筆帳目的日期
    private int breakfast;  // 早餐支出金額
    private int lunch;      // 午餐支出金額
    private int dinner;     // 晚餐支出金額
    private int others;     // 其他支出金額
    // 新增收入相關屬性
    private int salary;     // 薪資收入
    private int investment; // 投資收入
    private int bonus;      // 獎金收入
    private int otherIncome; // 其他收入
    private String date;    // 記帳日期（格式範例："2025/05/12"）
    private String note;    // 備註內容

    /**
     * 建構子：建立一筆帳目資料
     * @param breakfast 早餐支出
     * @param lunch 午餐支出
     * @param dinner 晚餐支出
     * @param others 其他支出
     * @param salary 薪資收入
     * @param investment 投資收入
     * @param bonus 獎金收入
     * @param otherIncome 其他收入
     * @param date 記帳日期
     * @param note 備註內容
     */
    public Account(int breakfast, int lunch, int dinner, int others, 
                  int salary, int investment, int bonus, int otherIncome, 
                  String date, String note) {
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.others = others;
        this.salary = salary;
        this.investment = investment;
        this.bonus = bonus;
        this.otherIncome = otherIncome;
        this.date = date;
        this.note = note;
    }

    /**
     * 舊建構子：僅支出，方便舊程式碼兼容
     */
    public Account(int breakfast, int lunch, int dinner, int others, String date, String note) {
        this(breakfast, lunch, dinner, others, 0, 0, 0, 0, date, note);
    }

    /**
     * 回傳格式化後的帳目資訊（用於顯示）
     * @return 帳目詳細資訊的字串（包含所有支出和總計）
     */
    public String printAccount() {
        int totalExpense = getExpenseTotal();
        int totalIncome = getIncomeTotal();
        int balance = totalIncome - totalExpense;

        return String.format("日期：%s | 收入：%d 元 | 支出：%d 元 | 收支平衡：%d 元 | 備註：%s",
                             date, totalIncome, totalExpense, balance, note);
    }

    /**
     * 回傳格式化後的詳細帳目資訊（用於顯示）
     * @return 帳目詳細資訊的字串（包含所有收入支出細項）
     */
    public String printDetailedAccount() {
        int totalExpense = getExpenseTotal();
        int totalIncome = getIncomeTotal();
        int balance = totalIncome - totalExpense;

        return String.format("日期：%s\n" +
                             "支出明細: 早餐：%d 元 | 午餐：%d 元 | 晚餐：%d 元 | 其他：%d 元 | 支出總計：%d 元\n" +
                             "收入明細: 薪資：%d 元 | 投資：%d 元 | 獎金：%d 元 | 其他：%d 元 | 收入總計：%d 元\n" +
                             "收支平衡：%d 元 | 備註：%s",
                             date, 
                             breakfast, lunch, dinner, others, totalExpense,
                             salary, investment, bonus, otherIncome, totalIncome,
                             balance, note);
    }

    // 以下為 getter 方法，提供取得各項資料的方式

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

    public int getSalary() {
        return salary;
    }

    public int getInvestment() {
        return investment;
    }

    public int getBonus() {
        return bonus;
    }

    public int getOtherIncome() {
        return otherIncome;
    }

    public String getNote() {
        return note;
    }

    /**
     * 回傳總支出金額（將四項加總）
     * @return 總支出金額
     */
    public int getExpenseTotal() {
        return breakfast + lunch + dinner + others;
    }

    /**
     * 回傳總收入金額（將四項加總）
     * @return 總收入金額
     */
    public int getIncomeTotal() {
        return salary + investment + bonus + otherIncome;
    }

    /**
     * 回傳收支平衡（收入-支出）
     * @return 收支平衡金額
     */
    public int getBalance() {
        return getIncomeTotal() - getExpenseTotal();
    }

    /**
     * 舊方法名，為了兼容性保留，現在返回總支出
     */
    public int getTotal() {
        return getExpenseTotal();
    }

    // 以下為 setter 方法，允許更新各餐別的支出金額

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

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void setInvestment(int investment) {
        this.investment = investment;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public void setOtherIncome(int otherIncome) {
        this.otherIncome = otherIncome;
    }

    public void setNote(String note) {
        this.note = note;
    }
    
    /**
     * 回傳記帳日期
     * @return 日期字串
     */
    public String getDate() {
        return date;
    }

    // 可以選擇加入toString方法，以便輸出帳目資訊時使用
    @Override
    public String toString() {
        return printAccount();
    }
}
