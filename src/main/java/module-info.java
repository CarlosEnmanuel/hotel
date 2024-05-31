module com.example.hotel {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires kernel;
    requires java.desktop;
    requires layout;


    opens com.example.hotel to javafx.fxml;
    exports com.example.hotel;
}