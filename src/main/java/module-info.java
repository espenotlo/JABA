module espenotlo {
    requires javafx.controls;
    requires javafx.fxml;

    opens espenotlo to javafx.fxml;
    opens espenotlo.jaba to javafx.base;
    exports espenotlo;
}
