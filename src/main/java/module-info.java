module com.example.debuglogic {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.compiler;

    opens com.example.debuglogic to javafx.fxml;
    exports com.example.debuglogic;
}