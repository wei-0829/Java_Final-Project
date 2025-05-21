import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Arrays;

// StreamHelper 類別負責處理檔案的儲存與讀取（序列化與反序列化）操作
// 這樣可以將帳目清單（AccountList）保存至檔案，也可以從檔案載入帳目清單
public class StreamHelper {

    // 這一部分是突發應用的，目前純粹為 saveFileTxt() 使用
    private final HashMap<String, Function<Account, ?>> getFuncMap = new HashMap<>();
    private final String[] accItemStr = {"日期", "早餐", "午餐", "晚餐", "其他", "收入", "淨額", "備註"};
    private final List<Function<Account, ?>> getFuncList = Arrays.asList(
        Account::getDate, Account::getBreakfast, Account::getLunch,
        Account::getDinner, Account::getOthers, Account::getIncome,
        Account::getNet, Account::getNote
    );

    // 建構子：當建立 StreamHelper 物件時，會初始化 getFuncMap
    public StreamHelper() {
        for (int i = 0; i < accItemStr.length; i++) {
            getFuncMap.put(accItemStr[i], getFuncList.get(i));
        }
    }

    // 將帳目清單儲存到檔案中（序列化）
    public void saveFileAll(AccountList list, File file) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))) {
            os.writeObject(list);
        } catch (IOException ev) {
            ev.printStackTrace();
        }
    }

    // 將帳目清單儲存為 CSV 格式
    public void saveFileCsv(AccountList list, File file) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            bw.write('\uFEFF'); // UTF-8 BOM
            bw.write("日期,早餐,午餐,晚餐,其他,收入,淨額,備註");
            bw.newLine();

            // 迭代帳目清單，將每筆帳目寫入 CSV 檔案
            for (Account acc : list.getAll()) {
                String line = String.format(
                    "%s,%d,%d,%d,%d,%d,%d,%s",
                    acc.getDate(), acc.getBreakfast(), acc.getLunch(), acc.getDinner(),
                    acc.getOthers(), acc.getIncome(), acc.getNet(), acc.getNote()
                );
                
                bw.write(line);
                bw.newLine();
            }

            bw.flush();
        } catch (IOException ev) {
            ev.printStackTrace();
        }
    }

    // 將帳目清單儲存為純文字格式
    public void saveFileTxt(AccountList list, File file) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            bw.write('\uFEFF');

            // 迭代帳目清單，將每筆帳目寫入純文字檔案
            for (Account acc : list.getAll()) {
                for (int i = 0; i < accItemStr.length; i++) {
                    Function<Account, ?> func = getFuncMap.get(accItemStr[i]);
                    String line = accItemStr[i] + ":" + func.apply(acc).toString();

                    bw.write(line);
                    bw.newLine();
                }

                bw.write("-------------------------------");
                bw.newLine();
            }

            bw.flush();
        } catch (IOException ev) {
            ev.printStackTrace();
        }
    }

    // 從檔案載入帳目清單（反序列化）
    public AccountList loadFileAll(File file) {
        AccountList list = null;

        // 檢查檔案是否存在
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(file))) {
            list = (AccountList) is.readObject();
        } catch (IOException | ClassNotFoundException ev) {
            ev.printStackTrace();
        }

        return list;
    }

    // 從 CSV 檔案載入帳目清單
    public AccountList loadFileCsv(File file) {
        AccountList list = new AccountList();

        // 檢查檔案是否存在
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String headline = br.readLine();

            // 處理 UTF-8 BOM
            if (headline.startsWith("\uFEFF")) headline = headline.substring(1);

            String[] headers = headline.split(",");
            HashMap<String, Integer> column = new HashMap<>();

            // 將 CSV 檔案的標題行轉換為 HashMap
            for (int i = 0; i < headers.length; i++) {
                column.put(headers[i].trim(), i);
            }

            String line;

            // 讀取每一行資料
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",");
                String date = data[column.get("日期")].trim();
                int breakfast = Integer.parseInt(data[column.get("早餐")].trim());
                int lunch = Integer.parseInt(data[column.get("午餐")].trim());
                int dinner = Integer.parseInt(data[column.get("晚餐")].trim());
                int others = Integer.parseInt(data[column.get("其他")].trim());
                int income = Integer.parseInt(data[column.get("收入")].trim());
                int net = Integer.parseInt(data[column.get("淨額")].trim());
                String note = data[column.get("備註")].trim();

                list.add(new Account(date, breakfast, lunch, dinner, others, income, net, note));
            }
        } catch (IOException ev) {
            ev.printStackTrace();
        }

        return list;
    }

    // 從純文字檔案載入帳目清單
    public AccountList loadFileTxt(File file) {
        AccountList list = new AccountList();

        // 檢查檔案是否存在
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            Pattern pattern;
            Matcher matcher;
            String line = br.readLine();

            // 處理 UTF-8 BOM
            while (line != null) {
                if (line.startsWith("\uFEFF")) line = line.substring(1); // 處理 BOM

                StringBuilder sb = new StringBuilder();

                // 讀取每一行資料，直到遇到分隔線或檔案結束
                while (line != null && !line.matches("^(.)\\1*$")) {
                    sb.append(line);
                    line = br.readLine();
                }

                String section = sb.toString();

                // 擷取日期
                String date;
                pattern = Pattern.compile("\\d{4}/\\d{2}/\\d{2}");
                matcher = pattern.matcher(section);

                // 如果找到日期，則將其從 section 中移除
                if (matcher.find()) {
                    date = matcher.group();
                    section = section.substring(0, matcher.start()) + section.substring(matcher.end());
                } else {
                    date = "";
                }

                // 擷取備註
                String note;
                int noteIndex = section.indexOf("備註");

                // 如果找到備註，則將其從 section 中移除
                if (noteIndex != -1) {
                    note = section.substring(noteIndex + 2).trim();
                    note = note.replaceFirst("^[\\p{Punct}\\s　、，。：；]+", "");
                    section = section.substring(0, noteIndex);
                } else {
                    note = "";
                }

                Account acc = new Account(date, 0, 0, 0, 0, 0, 0, note);

                acc.setBreakfast(getSectionData(section, "早餐"));
                acc.setLunch(getSectionData(section, "午餐"));
                acc.setDinner(getSectionData(section, "晚餐"));
                acc.setOthers(getSectionData(section, "其他"));
                acc.setIncome(getSectionData(section, "收入"));
                acc.setNet(getSectionData(section, "淨額"));

                list.add(acc);
                line = br.readLine();
            }
        } catch (IOException ev) {
            ev.printStackTrace();
        }

        return list;
    }

    // 擷取欄位資料用
    private int getSectionData(String section, String keyword) {
        int dataIndex = section.indexOf(keyword);

        // 如果找到關鍵字，則擷取該欄位的資料
        if (dataIndex != -1) {
            Pattern numPattern = Pattern.compile("(\\d+)");
            Matcher matcher = numPattern.matcher(section.substring(dataIndex));

            // 擷取數字
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        }

        return 0;
    }
}
