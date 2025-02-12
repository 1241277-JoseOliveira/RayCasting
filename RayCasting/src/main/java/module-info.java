module com.example.raycasting {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.raycasting to javafx.fxml;
    exports com.example.raycasting;
}