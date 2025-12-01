module Projeto_Arduino {
	requires javafx.controls;
	requires javafx.fxml;
	requires com.fazecast.jSerialComm;
	
	opens application to javafx.graphics, javafx.fxml;
}
