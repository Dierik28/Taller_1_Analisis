module UI {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports UI;
    opens UI to javafx.graphics, javafx.fxml;

    opens Datos to javafx.fxml;
}
