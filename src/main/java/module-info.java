module com.example.runninglog {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.runninglog to javafx.fxml;
    exports com.example.runninglog;
}