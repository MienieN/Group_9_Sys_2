package main.java.zenit.launchers;

import javafx.stage.Stage;
import main.java.zenit.ui.MainController;

/**
 * Launcher class for macOS that initializes the main application window.
 */
public class MacOSLauncher {

	/**
	 * Constructs a MacOSLauncher and sets the application name for macOS menu.
	 *
	 * @param stage The primary stage for the application.
	 */
	public MacOSLauncher(Stage stage) {
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "WikiTeX");
		new MainController(stage);
	}
}
