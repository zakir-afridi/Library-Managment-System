module com.lib.library {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    opens com.lib.library to javafx.fxml;
    exports com.lib.library;
    exports com.lib.Function;
    opens com.lib.Function to javafx.fxml;
    exports com.lib.Controller;
    opens com.lib.Controller to javafx.fxml;
    exports com.lib.Buttons;
    opens com.lib.Buttons to javafx.fxml;


    requires java.sql;
    requires mysql.connector.j;
    requires java.desktop;
    requires javafx.swing;

}