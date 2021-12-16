package espenotlo.jaba;

import java.time.LocalDate;

public class Transaction {
    private int tID;
    private int value;
    private String category;
    private LocalDate date;
    private String description;

    public Transaction(String category, String description, int value) {
        this.tID = 0;
        this.value = value;
        this.category = category;
        this.date = LocalDate.now();
        this.description = description;
    }

    public Transaction(LocalDate date, String category, String description, int value) {
        this.tID = 0;
        this.value = value;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    public Transaction(int tID, LocalDate date, String category, String description, int value) {
        this.tID = tID;
        this.value = value;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    public int getTid() {
        return tID;
    }

    public void setTid(int tID) {
        this.tID = tID;
    }

    public int getValue() {
        return value;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCategory() {
        return this.category;
    }

    public String getDescription() {
        return this.description;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getTransactionAsStringArray() {
        return new String[]{"" + this.date, this.category, this.description, "" + this.value};
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Transaction) {
            return ((Transaction) o).getTid() == this.tID;
        }
        return false;
    }
}
