import java.util.Calendar;

public class DateUtils {
    // 檢查日期為未來的日期
    public static boolean isFutureDate(String dateStr) {
        String[] parts = dateStr.split("/");
        int inputYear = Integer.parseInt(parts[0]);
        int inputMonth = Integer.parseInt(parts[1]);
        int inputDay = Integer.parseInt(parts[2]);

        // 取得今天的年月日
        Calendar today = Calendar.getInstance();
        int currentYear = today.get(Calendar.YEAR);
        int currentMonth = today.get(Calendar.MONTH) + 1; // 月份從0開始
        int currentDay = today.get(Calendar.DAY_OF_MONTH);

        if (inputYear > currentYear) {
            return true;
        } else if (inputYear == currentYear && inputMonth > currentMonth){
            return true;
        } else if (inputYear == currentYear && inputMonth == currentMonth && inputDay > currentDay) {
            return true;
        } else {
            return false;
        }
    }

    // 回傳該年該月最大天數
    public static int getDaysInYearMonth(String yearMonthStr) {
        String[] parts = yearMonthStr.split("/");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]); // 1 ~ 12

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1); // Calendar 月份從 0 開始
        cal.set(Calendar.DAY_OF_MONTH, 1);

        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

}
