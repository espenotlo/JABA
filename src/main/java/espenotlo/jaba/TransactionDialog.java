package espenotlo.jaba;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;

public class TransactionDialog extends Dialog<Transaction> {

    private final TransactionDialog.Mode mode;
    private Transaction transaction = null;
    private final ObservableList<String> categories;

    public enum Mode {
        NEW, EDIT
    }

    public TransactionDialog(ObservableList<String> categories) {
        super();
        this.mode = Mode.NEW;
        this.categories = categories;
        showContent();
    }

    public TransactionDialog(Transaction transaction, ObservableList<String> categories) {
        super();
        this.mode = Mode.EDIT;
        this.transaction = transaction;
        this.categories = categories;
        showContent();
    }

    private void showContent() {
        getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

        //Creates GridPane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 50, 10, 10));

        //Creates the fields that holds Object information.
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        datePicker.setBorder(new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        ComboBox<String> categoryPicker = new ComboBox<>(categories);
        categoryPicker.getSelectionModel().selectFirst();
        categoryPicker.setBorder(new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        TextField descriptionTextField = new TextField();
        descriptionTextField.setBorder(new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        descriptionTextField.setPromptText("Beskrivelse");

        TextField valueTextField = new TextField();
        valueTextField.setBorder(new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        valueTextField.setPromptText("Beløp");

        if (mode == Mode.EDIT) {
            datePicker.setValue(transaction.getDate());
            categoryPicker.getSelectionModel().select(transaction.getCategory());
            descriptionTextField.setText(transaction.getDescription());
            valueTextField.setText("" + transaction.getValue());
        }

        grid.add(new Label("Dato:"), 1, 0);
        grid.add(datePicker, 2, 0 );

        grid.add(new Label("Kategori:"), 1, 1);
        grid.add(categoryPicker, 2, 1);

        grid.add(new Label("Beskrivelse:"), 1, 2);
        grid.add(descriptionTextField, 2, 2);

        grid.add(new Label("Beløp:"), 1, 3);
        grid.add(valueTextField, 2, 3);

        getDialogPane().setContent(grid);

        setResultConverter((ButtonType button) -> {
            Transaction result = null;
            if (button == ButtonType.APPLY) {
                if (mode == Mode.NEW) {
                    result = new Transaction(datePicker.getValue(), categoryPicker.getValue(), descriptionTextField.getText(), Integer.parseInt(valueTextField.getText()));
                } else {
                    transaction.setDate(datePicker.getValue());
                    transaction.setCategory(categoryPicker.getValue());
                    transaction.setDescription(descriptionTextField.getText());
                    transaction.setValue(Integer.parseInt(valueTextField.getText()));
                    result = transaction;
                }
            }
            return result;
        });
    }
}
