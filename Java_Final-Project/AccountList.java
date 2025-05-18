package AccountProgram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AccountList implements Serializable {
    private ArrayList<Account> accounts;

    public AccountList() {
        accounts = new ArrayList<>();
    }

    public void add(Account account) {
        accounts.add(account);
    }

    public Account get(int index) {
        return accounts.get(index);
    }

    public void remove(int index) {
        accounts.remove(index);
    }

    public int size() {
        return accounts.size();
    }

    public void clear() {
        accounts.clear();
    }

    public List<Account> getAll() {
        return new ArrayList<>(accounts);
    }
}