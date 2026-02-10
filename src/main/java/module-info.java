module UI {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // Abre los paquetes necesarios
    opens UI to javafx.graphics, javafx.fxml;
    opens Datos to javafx.fxml, javafx.base;
    opens AgendaDao to javafx.fxml;

    // Exporta los paquetes que necesitan ser accesibles
    exports UI;
    exports Datos;
}