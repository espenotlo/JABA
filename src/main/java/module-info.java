module espenotlo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens espenotlo to javafx.fxml;
    opens espenotlo.jaba to javafx.base;
    exports espenotlo;
}
