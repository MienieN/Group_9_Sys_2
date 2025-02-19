package main.java.zenit.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class TestUI extends Application {
	private MainController controller;
	
	@Override
	public void start(Stage stage) {
		controller = new MainController(stage);
	}
	
	@Override
	public void stop() {
		controller.quit();
		Platform.exit();
	}
	
	public static void main(String[] args) {
		launch(args);
		System.out.println("Hello World");
	}
}
