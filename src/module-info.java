module Projeto_Arduino {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.fazecast.jSerialComm;
	requires java.sql;
    exports application;
    opens view to javafx.fxml;
    exports view;
}
