package espenotlo.jaba;

import java.time.LocalDate;
import java.util.*;

public class Budget {
    private ArrayList<String> categories;
    private ArrayList<Transaction> transactions;
    private int budgetedBalance;
    private int actualBalance;

    public Budget() {
        this.actualBalance = 0;
        this.budgetedBalance = 0;
        this.categories = new ArrayList<>();
        this.transactions = new ArrayList<>();
        createDefaultCategories();
    }

    public Budget(int balance) {
        this.actualBalance = balance;
        this.categories = new ArrayList<>();
        this.transactions = new ArrayList<>();
        createDefaultCategories();
    }

    private void createDefaultCategories() {
        String[] categoryNames = {"debt", "salary", "entertainment", "other"};
        Collections.addAll(this.categories, categoryNames);
    }

    public boolean addCategory(String name) {
        boolean found = false;
        Iterator<String> it = this.categories.iterator();
        while (!found && it.hasNext()) {
            String cat = it.next();
            if (cat.equalsIgnoreCase(name)) {
                found = true;
            }
        }
        if (!found) {
            this.categories.add(name);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeCategory(String name) {
        boolean found = false;
        Iterator<String> it = this.categories.iterator();
        while (!found && it.hasNext()) {
            String cat = it.next();
            if (cat.equalsIgnoreCase(name)) {
                this.categories.remove(cat);
                found = true;
            }
        }
        return found;
    }

    public void addTransaction(Transaction transaction) {
        if (transaction.getTid() == 0) {
            transaction.setTid(generateTid());
        }
        boolean duplicate = false;
        for (Transaction t : this.transactions) {
            if (t.equals(transaction)) {
                duplicate = true;
                break;
            }
        }
        if (!duplicate) {
            this.transactions.add(transaction);
        }
    }

    public void addTransaction(LocalDate date, String category, String description, int value) {
        this.transactions.add(new Transaction(generateTid(), date, category, description, value));
    }

    public int generateTid() {
        int i = 0;
        for (Transaction t : transactions) {
            if (t.getTid() > i) {
                i = t.getTid();
            }
        }
        return i+1;
    }

    public List<Transaction> getTransactions() {
        return this.transactions;
    }

    public void removeTransaction(Transaction transaction) {
        if (this.transactions.contains(transaction)) {
            this.transactions.remove(transaction);
        }
    }

    public List<String> getCategories() {
        return this.categories;
    }

    public int getTotalSum() {
        int sum = 0;
        for (Transaction transaction : transactions) {
            sum += transaction.getValue();
        }
        return sum;
    }

    public List<String[]> getBudgetAsStringArray() {
        List<String[]> budgetInfo = new ArrayList<>();
        this.transactions.forEach(transaction -> budgetInfo.add(transaction.getTransactionAsStringArray()));
        return budgetInfo;
    }
}
