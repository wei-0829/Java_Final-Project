package AccountProgram;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static boolean isValidDate(String date) {
        if (date == null || !date.matches("\\d{4}/\\d{2}/\\d{2}")) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            sdf.setLenient(false);
            sdf.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFutureDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date inputDate = sdf.parse(date);
            Date currentDate = new Date();
            return inputDate.after(currentDate);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidYear(String year) {
        if (year == null || !year.matches("\\d{4}")) {
            return false;
        }
        try {
            int y = Integer.parseInt(year);
            return y >= 1900 && y <= 9999;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidYearMonth(String yearMonth) {
        if (yearMonth == null || !yearMonth.matches("\\d{4}/\\d{2}")) {
            return false;
        }
        try {
            String[] parts = yearMonth.split("/");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            return year >= 1900 && year <= 9999 && month >= 1 && month <= 12;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(new Date());
    }

    public static String parseNumericDate(String input) {
        if (input == null || !input.matches("\\d{8}")) {
            return null;
        }
        try {
            String year = input.substring(0, 4);
            String month = input.substring(4, 6);
            String day = input.substring(6, 8);
            String formatted = year + "/" + month + "/" + day;
            if (!isValidDate(formatted)) {
                return null;
            }
            return formatted;
        } catch (Exception e) {
            return null;
        }
    }
}