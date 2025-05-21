import java.io.Serializable;

/**
 * 帳目資料類別，每筆記帳資料就是一個 Account 物件
 */
public class Account implements Serializable {
    // 各餐別的花費金額，以及該筆帳目的日期
    private String date;    // 記帳日期（格式範例："2025/05/12"）
    private int breakfast;  // 早餐支出金額
    private int lunch;      // 午餐支出金額
    private int dinner;     // 晚餐支出金額
    private int others;     // 其他支出金額
    private int income;     // 額外收入金額
    private int net;        // 淨額（收入 - 支出）
    private String note;    // 備註內容

    /**
     * 建構子：建立一筆帳目資料
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
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.others = others;
        this.income = income;
        this.net = net;
        this.note = note;
    }

    /**
     * 回傳格式化後的帳目資訊（用於顯示）
     * @return 帳目詳細資訊的字串（包含所有支出和總計）
     */
    public String printAccount() {

        return String.format("日期：%s | 早餐：%d 元 | 午餐：%d 元 | 晚餐：%d 元 | 其他：%d 元 | 收入：%d 元 | 淨額：%d 元 | 備註：%s",
                             date, breakfast, lunch, dinner, others, income, net, note);
    }

    // 以下為 getter 方法，提供取得各項資料的方式（支出金額與日期）

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

    public int getNet() {
        return net;
    }

    public String getNote() {
        return note;
    }

    /**
     * 回傳總支出金額（將四項加總）
     * @return 總支出金額
     */
    public int getTotal() {
        return breakfast + lunch + dinner + others;
    }

    /**
     * 回傳淨額（收入 - 支出）
     * @return 淨額
     */
    public int getNetAmount() {
        return income - getTotal();
    }

    /**
     * 回傳記帳日期
     * @return 日期字串
     */
    public String getDate() {
        return date;
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

    public void setIncome(int income) {
        this.income = income;
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
