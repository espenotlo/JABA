package espenotlo.jaba;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private Connection connection;
    private boolean hasData = false;

    public DBManager() {
        try {
            getConnection();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void displayTransactions() throws SQLException {
        if (connection == null) {
            getConnection();
        }
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT date, name, description, value FROM transactions JOIN category USING(cID);");
            while (rs.next()) {
                System.out.println(rs.getString("date") + ": " + rs.getString("name") + ": " + rs.getString("description") + ": " + rs.getInt("value"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void getConnection() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:test.db");
        DatabaseMetaData meta = connection.getMetaData();
        System.out.println("The driver name is " + meta.getDriverName());

        initialize();
    }

    private void initialize() throws SQLException {
        if (!hasData) {
            hasData = true;

            try (Statement statement = connection.createStatement()) {
                ResultSet rs = statement.executeQuery("SELECT * FROM category;");
                System.out.println("Connected to existing database");

            } catch (SQLException e) {
                if (e.getMessage().contains("no such table")) {

                    createCategoryTable();
                    createTransactionsTable();

                    System.out.println("A new database has been created.");
                }
            }
        }
    }

    private void createCategoryTable() throws SQLException {
        System.out.println("Building the category table with prepopulated values.");
        try (Statement st = connection.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS category(cID integer primary key autoincrement, name varchar (60));");
        }
        insertCategory("debt");
        insertCategory("salary");
        insertCategory("entertainment");
        insertCategory("other");
    }

    private void createTransactionsTable() throws SQLException {
        System.out.println("Building transactions table.");
        try (Statement st = connection.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS transactions(" +
                    "tID integer primary key autoincrement, " +
                    "date varchar (8), cID integer, " +
                    "description varchar (60), " +
                    "value integer, " +
                    "foreign key (cID) references category (cID));");
        }
        //insertTransaction("01012021",2,"Eurospar",8500);
        //insertTransaction("15012021",3,"Netflix",-150);
    }

    public void insertCategory(String name) throws SQLException {
        if (this.connection == null) {
            getConnection();
        }
        if (hasData) {
            try (PreparedStatement prep = connection.prepareStatement("INSERT INTO category(name) values(?)")) {
                prep.setString(1, name);
                prep.execute();
            }
        } else {
            initialize();
        }
    }

    public void insertTransaction(String date, int category, String description, int value) throws SQLException {
        if (this.connection == null) {
            getConnection();
        }
        if (hasData) {
            try (PreparedStatement prep = connection.prepareStatement("INSERT INTO transactions(date,cID,description,value) values(?,?,?,?)")) {
                prep.setString(1, date);
                prep.setInt(2, category);
                prep.setString(3, description);
                prep.setInt(4, value);
                prep.execute();
            }
        } else {
            initialize();
        }
    }

    public void insertTransaction(Transaction transaction) {
        try {
            if (this.connection == null) {
                getConnection();
            }
            if (hasData) {
                try (PreparedStatement prep = connection.prepareStatement("INSERT INTO transactions(tID, date,cID,description,value) values(?,?,?,?,?)")) {
                    prep.setInt(1,transaction.getTid());
                    prep.setString(2, "" + transaction.getDate());
                    try (ResultSet rs = connection.createStatement().executeQuery("SELECT cID FROM category WHERE name = '" + transaction.getCategory() + "';")) {
                        prep.setInt(3, rs.getInt("cID"));
                    } catch (SQLException e) {
                        prep.setInt(4, 4);
                    }
                    prep.setString(4, transaction.getDescription());
                    prep.setInt(5, transaction.getValue());
                    prep.execute();
                }
            } else {
                initialize();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteTransaction(int tID) {
        try (Statement statement = connection.createStatement()){
            statement.execute("DELETE FROM transactions WHERE tID =" + tID + ";");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void editTransaction(Transaction transaction) {
        int cID = 4;
        try (ResultSet rs = connection.createStatement().executeQuery("SELECT cID FROM category WHERE name = '" + transaction.getCategory() + "';")) {
            cID = rs.getInt("cID");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("UPDATE transactions " +
                    "SET date = '" + transaction.getDate() +
                    "', cID = " + cID +
                    ", description = '" + transaction.getDescription() +
                    "', value = " + transaction.getValue() +
                    " WHERE tID = " + transaction.getTid());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Transaction> getAllTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        if (connection == null) {
            try {
                getConnection();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT tID, date, name, description, value FROM transactions JOIN category USING(cID);");
            while (rs.next()) {
                transactions.add(new Transaction(rs.getInt("tID"),LocalDate.parse(rs.getString("date")),rs.getString("name"),rs.getString("description"), rs.getInt("value")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return transactions;
    }
}
