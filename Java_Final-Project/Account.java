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
    private String date;    // 記帳日期（格式範例："2025/05/12"）

    /**
     * 建構子：建立一筆帳目資料
     * @param breakfast 早餐支出
     * @param lunch 午餐支出
     * @param dinner 晚餐支出
     * @param others 其他支出
     * @param date 記帳日期
     */
    public Account(int breakfast, int lunch, int dinner, int others, String date) {
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.others = others;
        this.date = date;
    }

    /**
     * 回傳格式化後的帳目資訊（用於顯示）
     * @return 帳目詳細資訊的字串（包含所有支出和總計）
     */
    public String printAccount() {
        int total = breakfast + lunch + dinner + others;
        return String.format("日期：%s | 早餐：%d 元 | 午餐：%d 元 | 晚餐：%d 元 | 其他：%d 元 | 總計：%d 元",
                             date, breakfast, lunch, dinner, others, total);
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

    /**
     * 回傳總支出金額（將四項加總）
     * @return 總支出金額
     */
    public int getTotal() {
        return breakfast + lunch + dinner + others;
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
