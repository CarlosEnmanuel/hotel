module com.example.hotel {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.hotel to javafx.fxml;
    exports com.example.hotel;
}