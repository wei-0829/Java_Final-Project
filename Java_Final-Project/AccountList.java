package AccountProgram;

import java.io.Serializable;
import java.util.ArrayList;

// AccountList 類別是用來包裝 Java 內建的 ArrayList，
// 目的是保存多筆帳目資料（每筆帳目是 Account 物件）。
public class AccountList implements Serializable {
    // 用來儲存多筆帳目的 ArrayList
    private ArrayList<Account> list;

    // 建構子：當建立 AccountList 物件時，會初始化一個空的帳目清單。
    public AccountList() {
        list = new ArrayList<>(); // 建立一個新的 ArrayList
    }

    // add() 方法：用來將一筆帳目物件加入帳目清單。
    // @param a 帳目物件，將這筆帳目加入清單中。
    public void add(Account a) {
        list.add(a); // 將帳目加入 ArrayList
    }

    // get() 方法：用來取得指定位置的帳目物件。
    // @param i 目標帳目的索引位置（從 0 開始）
    // @return 回傳指定位置的帳目物件
    public Account get(int i) {
        return list.get(i); // 回傳 ArrayList 中索引為 i 的帳目
    }

    // remove() 方法：移除指定索引位置的帳目
    public void remove(int index) {
        list.remove(index);
    }

    // clear() 方法：清空帳目清單中的所有帳目。
    public void clear() {
        list.clear(); // 清空 ArrayList 中的所有帳目
    }

    // size() 方法：回傳帳目清單中的帳目數量。
    // @return 回傳帳目清單中的帳目數量
    public int size() {
        return list.size(); // 回傳 ArrayList 中的元素數量
    }

    // getAll() 方法：回傳原始的帳目清單（ArrayList）。
    // 若需要對帳目清單進行進一步操作，可以直接使用該清單。
    // @return 回傳帳目清單的原始 ArrayList
    public ArrayList<Account> getAll() {
        return list; // 回傳 ArrayList 物件本身
    }
}
