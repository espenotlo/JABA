package espenotlo;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import espenotlo.jaba.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class PrimaryController {
    private Budget budget = new Budget();
    private final DBManager dbManager = new DBManager();
    private final ObservableList<Transaction> transactionListWrapper = FXCollections.observableArrayList(budget.getTransactions());
    private final ObservableList<String> categoriesListWrapper = FXCollections.observableArrayList(budget.getCategories());
    @FXML TableView<Transaction> table;
    @FXML TextField sumTextField;

    @FXML
    private void initialize() {
        loadTransactions();
        updateListWrapper();
        showTransactions();
    }


    @FXML
    private void showTransactions() {
        TableColumn<Transaction, LocalDate> dateTableColumn = new TableColumn<>("Dato");
        dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateTableColumn.setMinWidth(75);
        dateTableColumn.setMaxWidth(1000);

        TableColumn<Transaction, String> categoryTableColumn = new TableColumn<>("Kategori");
        categoryTableColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryTableColumn.setMinWidth(75);
        categoryTableColumn.setMaxWidth(1000);

        TableColumn<Transaction, String> descriptionTableColumn = new TableColumn<>("Beskrivelse");
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionTableColumn.setMinWidth(75);
        descriptionTableColumn.setMaxWidth(1000);

        TableColumn<Transaction, Integer> valueTableColumn = new TableColumn<>("Beløp");
        valueTableColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueTableColumn.setMinWidth(75);
        valueTableColumn.setMaxWidth(1000);

        table.getColumns().clear();
        table.getColumns().addAll(dateTableColumn, categoryTableColumn, descriptionTableColumn, valueTableColumn);

        table.setItems(transactionListWrapper);
    }

    @FXML
    private void updateListWrapper() {
        this.transactionListWrapper.setAll(this.budget.getTransactions());
        this.categoriesListWrapper.setAll(this.budget.getCategories());
        this.sumTextField.setText("" + this.budget.getTotalSum());
    }

    @FXML
    private void addTransaction() {
        TransactionDialog transactionDialog = new TransactionDialog(categoriesListWrapper);
        Optional<Transaction> result = transactionDialog.showAndWait();
        if (result.isPresent()) {
            Transaction t = result.get();
            t.setTid(budget.generateTid());
            budget.addTransaction(t);
            dbManager.insertTransaction(t);
            updateListWrapper();
        }
    }

    @FXML
    private void editTransaction() {
        Transaction transaction = table.getSelectionModel().getSelectedItem();
        if (null!= transaction) {
            TransactionDialog transactionDialog = new TransactionDialog(transaction, categoriesListWrapper);
            Optional<Transaction> result = transactionDialog.showAndWait();
            if (result.isPresent()) {
                dbManager.editTransaction(result.get());
                updateListWrapper();
            }
        }
    }

    @FXML
    private void deleteTransaction() {
        Transaction transaction = table.getSelectionModel().getSelectedItem();
        if (null != transaction) {
            dbManager.deleteTransaction(transaction.getTid());
            budget.removeTransaction(transaction);
            updateListWrapper();
        }
    }

    @FXML
    private void exportBudget() {
        CsvManager csvManager = new CsvManager();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Velg plassering for fillagring");
        File saveDirectory = directoryChooser.showDialog(this.table.getScene().getWindow());
        if (null != saveDirectory) {
            csvManager.setSaveFileDirectory(saveDirectory.getAbsolutePath());
            boolean success = false;
            if (csvManager.createCsvFile("Budget")) {
                success = true;
            }
            if (success) {
                csvManager.writeToCsv(budget.getBudgetAsStringArray());
            }
        }
    }

    @FXML
    private void importBudget() {
        CsvManager csvManager = new CsvManager();

        //Chooses the file to import
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser
                .ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setTitle("Åpne ressursfil");
        File file = fileChooser.showOpenDialog(table.getScene().getWindow());

        if (null != file) {
            //Adds the newly generated project to the project library,
            // updates the project list, and displays the new project to the user.
            try {
                this.budget = csvManager.importCsv(file);
                updateListWrapper();
            } catch (ArrayIndexOutOfBoundsException a) {
                System.out.println("uh oh");
            }
        }
    }

    @FXML
    private void storeTransactions() {
        this.budget.getTransactions().forEach(this.dbManager::insertTransaction);
    }

    @FXML
    private void loadTransactions() {
        this.dbManager.getAllTransactions().forEach(this.budget::addTransaction);
        updateListWrapper();
    }
}
