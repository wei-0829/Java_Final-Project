import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// StreamHelper 類別負責處理檔案的儲存與讀取（序列化與反序列化）操作
// 這樣可以將帳目清單（AccountList）保存至檔案，也可以從檔案載入帳目清單
public class StreamHelper {

    // 建構子
    public StreamHelper() {
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
            
            // 找出資料中所有的支出和收入項目類別
            List<String> expenseCategories = new ArrayList<>();
            List<String> incomeCategories = new ArrayList<>();
            
            for (Account acc : list.getAll()) {
                for (String category : acc.getExpenseItems().keySet()) {
                    if (!expenseCategories.contains(category)) {
                        expenseCategories.add(category);
                    }
                }
                
                for (String category : acc.getIncomeItems().keySet()) {
                    if (!incomeCategories.contains(category)) {
                        incomeCategories.add(category);
                    }
                }
            }
            
            // 寫入標頭行
            bw.write("日期");
            
            // 寫入所有支出項目
            for (String category : expenseCategories) {
                bw.write("," + category);
            }
            
            // 寫入所有收入項目
            for (String category : incomeCategories) {
                bw.write("," + category);
            }
            
            // 寫入淨額和備註
            bw.write(",淨額,備註");
            bw.newLine();

            // 迭代帳目清單，將每筆帳目寫入 CSV 檔案
            for (Account acc : list.getAll()) {
                StringBuilder line = new StringBuilder(acc.getDate());
                
                // 寫入所有支出項目的金額
                for (String category : expenseCategories) {
                    line.append(",").append(acc.getExpenseItem(category));
                }
                
                // 寫入所有收入項目的金額
                for (String category : incomeCategories) {
                    line.append(",").append(acc.getIncomeItem(category));
                }
                
                // 寫入淨額和備註
                line.append(",").append(acc.getNet());
                line.append(",").append(acc.getNote());
                
                bw.write(line.toString());
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
                // 寫入日期
                bw.write("日期:" + acc.getDate());
                bw.newLine();
                
                // 寫入支出項目
                Map<String, Integer> expenseItems = acc.getExpenseItems();
                for (Map.Entry<String, Integer> entry : expenseItems.entrySet()) {
                    if (entry.getValue() > 0) {
                        bw.write(entry.getKey() + ":" + entry.getValue());
                        bw.newLine();
                    }
                }
                
                // 寫入收入項目
                Map<String, Integer> incomeItems = acc.getIncomeItems();
                for (Map.Entry<String, Integer> entry : incomeItems.entrySet()) {
                    if (entry.getValue() > 0) {
                        bw.write(entry.getKey() + ":" + entry.getValue());
                        bw.newLine();
                    }
                }
                
                // 寫入淨額和備註
                bw.write("淨額:" + acc.getNet());
                bw.newLine();
                bw.write("備註:" + acc.getNote());
                bw.newLine();
                
                // 寫入分隔線
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
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String headline = br.readLine();

            // 處理 UTF-8 BOM
            if (headline.startsWith("\uFEFF")) headline = headline.substring(1);

            String[] headers = headline.split(",");
            
            // 將 CSV 檔案的標題行轉換為 HashMap
            HashMap<String, Integer> columnIndexes = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                columnIndexes.put(headers[i].trim(), i);
            }

            String line;

            // 讀取每一行資料
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",");
                
                // 日期欄位必須存在
                String date = data[columnIndexes.get("日期")].trim();
                
                // 備註欄位可能存在也可能不存在
                String note = "";
                if (columnIndexes.containsKey("備註") && columnIndexes.get("備註") < data.length) {
                    note = data[columnIndexes.get("備註")].trim();
                }
                
                // 創建支出和收入項目的映射
                Map<String, Integer> expenseItems = new HashMap<>();
                Map<String, Integer> incomeItems = new HashMap<>();
                
                // 標準支出項目
                String[] standardExpenses = {"早餐", "午餐", "晚餐", "其他", "交通", "住宿", "衣著", "水電費", "娛樂", "醫療", "教育", "通訊費"};
                for (String item : standardExpenses) {
                    if (columnIndexes.containsKey(item) && columnIndexes.get(item) < data.length) {
                        try {
                            int amount = Integer.parseInt(data[columnIndexes.get(item)].trim());
                            expenseItems.put(item, amount);
                        } catch (NumberFormatException e) {
                            // 如果無法解析為數字，則設為0
                            expenseItems.put(item, 0);
                        }
                    }
                }
                
                // 標準收入項目
                String[] standardIncomes = {"收入", "額外收入", "薪資", "獎金", "投資收益", "副業", "禮金"};
                for (String item : standardIncomes) {
                    if (columnIndexes.containsKey(item) && columnIndexes.get(item) < data.length) {
                        try {
                            int amount = Integer.parseInt(data[columnIndexes.get(item)].trim());
                            incomeItems.put(item, amount);
                        } catch (NumberFormatException e) {
                            // 如果無法解析為數字，則設為0
                            incomeItems.put(item, 0);
                        }
                    }
                }
                
                // 創建新帳目
                Account acc = new Account(date, expenseItems, incomeItems, note);
                list.add(acc);
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
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line = br.readLine();

            // 處理 UTF-8 BOM
            if (line != null && line.startsWith("\uFEFF")) {
                line = line.substring(1); // 處理 BOM
            }
            
            Map<String, Integer> expenseItems = new HashMap<>();
            Map<String, Integer> incomeItems = new HashMap<>();
            String date = "";
            String note = "";
            
            // 讀取每一行資料
            while (line != null) {
                // 如果是分隔線，則建立新帳目並加入清單
                if (line.matches("^[\\-]+$")) {
                    if (!date.isEmpty()) {
                        Account acc = new Account(date, expenseItems, incomeItems, note);
                        list.add(acc);
                        
                        // 清空暫存資料
                        expenseItems = new HashMap<>();
                        incomeItems = new HashMap<>();
                        date = "";
                        note = "";
                    }
                } else if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    String key = parts[0].trim();
                    String value = parts.length > 1 ? parts[1].trim() : "";
                    
                    // 處理各種欄位
                    if (key.equals("日期")) {
                        date = value;
                    } else if (key.equals("備註")) {
                        note = value;
                    } else if (key.equals("淨額")) {
                        // 淨額會自動計算，不需要處理
                    } else {
                        // 嘗試將值解析為數字
                        try {
                            int amount = extractNumber(value);
                            
                            // 判斷是支出還是收入項目
                            if (isExpenseCategory(key)) {
                                expenseItems.put(key, amount);
                            } else {
                                incomeItems.put(key, amount);
                            }
                        } catch (NumberFormatException ignored) {
                            // 如果無法解析為數字，則忽略
                        }
                    }
                }
                
                line = br.readLine();
            }
            
            // 處理最後一筆資料
            if (!date.isEmpty()) {
                Account acc = new Account(date, expenseItems, incomeItems, note);
                list.add(acc);
            }
            
        } catch (IOException ev) {
            ev.printStackTrace();
        }

        return list;
    }
    
    // 輔助方法：從字串中提取數字
    private int extractNumber(String text) {
        Pattern numPattern = Pattern.compile("(\\d+)");
        Matcher matcher = numPattern.matcher(text);
        
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        
        throw new NumberFormatException("No number found in: " + text);
    }
    
    // 輔助方法：判斷是否為支出類別
    private boolean isExpenseCategory(String category) {
        // 標準支出類別列表
        String[] standardExpenses = {"早餐", "午餐", "晚餐", "其他", "交通", "住宿", "衣著", "水電費", "娛樂", "醫療", "教育", "通訊費"};
        
        for (String item : standardExpenses) {
            if (category.equals(item)) {
                return true;
            }
        }
        
        // 如果不在標準支出類別中，則判斷是否為收入類別
        String[] standardIncomes = {"收入", "額外收入", "薪資", "獎金", "投資收益", "副業", "禮金"};
        
        for (String item : standardIncomes) {
            if (category.equals(item)) {
                return false;
            }
        }
        
        // 預設為支出類別
        return true;
    }
}
