import java.io.*;

// StreamHelper 類別負責處理檔案的儲存與讀取（序列化與反序列化）操作
// 這樣可以將帳目清單（AccountList）保存至檔案，也可以從檔案載入帳目清單
public class StreamHelper {
    
    // saveFile() 方法：將帳目清單儲存到檔案中（序列化）
    // @param list 要儲存的帳目清單（AccountList 物件）
    // @param file 儲存的目標檔案
    public void saveFileAll(AccountList list, File file) {
        // 使用 try-with-resources 來確保檔案流的正確關閉
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))) {
            os.writeObject(list); // 將 AccountList 物件序列化並寫入檔案
        } catch (IOException ex) {
            ex.printStackTrace(); // 捕捉 I/O 異常並印出錯誤訊息
        }
    }

    // saveFile() csv版本
    public void saveFileCsv(AccountList list, File file){

    }

    // loadFile() 方法：從檔案載入帳目清單（反序列化）
    // @param file 要讀取的檔案
    // @return 回傳從檔案讀取的 AccountList 物件
    public AccountList loadFile(File file) {
        AccountList list = null;
        // 使用 try-with-resources 來確保檔案流的正確關閉
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(file))) {
            list = (AccountList) is.readObject(); // 從檔案讀取並反序列化回 AccountList 物件
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace(); // 捕捉 I/O 或類別找不到異常並印出錯誤訊息
        }
        return list; // 回傳讀取到的帳目清單（如果成功）
    }
}
