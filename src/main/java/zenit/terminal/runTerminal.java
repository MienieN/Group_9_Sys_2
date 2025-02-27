package main.java.zenit.terminal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The runTerminal class extends Application and is responsible for initializing and setting up the stage
 * with necessary components for the terminal application.
 */
public class runTerminal extends Application {
	private static final String FXML_PATH = "/zenit/terminal/Terminal.fxml";
	private static final String STAGE_TITLE = "Zenit";
	private static final String ERROR_MSG_PATTERN = "Error in %s: %s";

	/**
	 * Initializes and sets up the stage with necessary components for the terminal application.
	 *
	 * @param stage The primary stage of the application
	 */
	@Override
	public void start(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(FXML_PATH));
			loader.setController(new TerminalController());

			stage.setScene(new Scene(loader.load()));
			stage.setTitle(STAGE_TITLE);
			stage.show();
		} catch (Exception exception ) {
			String errorMsg = String.format(ERROR_MSG_PATTERN, this.getClass()
					.getSimpleName(), exception.getMessage());
			System.out.println(errorMsg);
		}
	}

	/**
	 * Launches the terminal application.
	 *
	 * @param args The command-line arguments passed to the program
	 */
	public static void main(String[] args) {
		launch(args);
	}
}