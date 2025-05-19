import java.util.Calendar;

public class DateUtils {

    // 檢查日期格式與範圍
    public static boolean isValidDate(String dateStr) {
        if (!dateStr.matches("\\d{4}/\\d{2}/\\d{2}")) return false;

        try {
            String[] parts = dateStr.split("/");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            if (year < 1900 || month < 1 || month > 12) return false;

            int[] daysInMonth = { 31, isLeapYear(year) ? 29 : 28, 31, 30, 31, 30,
                                  31, 31, 30, 31, 30, 31 };

            return day >= 1 && day <= daysInMonth[month - 1];
        } catch (Exception e) {
            return false;
        }
    }

    // 檢查閏年
    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

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

    // 檢查年份的格式與範圍
    public static boolean isValidYear(String yearStr) {
        if (!yearStr.matches("\\d{4}")) return false;

        try {
            int year = Integer.parseInt(yearStr);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            return year >= 1900 && year <= currentYear; // 合理範圍
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int getDaysInYearMonth(String yearMonthStr) {
        if (!yearMonthStr.matches("\\d{4}/\\d{2}")) return 0;

        try {
            String[] parts = yearMonthStr.split("/");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);

            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

            // 檢查年、月合理性
            if (year < 1900 || month < 1 || month > 12) return 0;
            if (year > currentYear || (year == currentYear && month > currentMonth)) return 0;

            // 使用 Calendar 取得該月的最大天數
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month - 1); // 月份從 0 開始
            cal.set(Calendar.DAY_OF_MONTH, 1);
            return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            return 0;
        }
    }

}
