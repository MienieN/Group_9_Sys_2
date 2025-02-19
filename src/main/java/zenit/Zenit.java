package main.java.zenit;

import java.io.File;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import main.java.zenit.launchers.MacOSLauncher;
import main.java.zenit.setup.SetupController;
import main.java.zenit.ui.MainController;

/**
 * The Zenit class represents the main entry point for the application that
 * extends the JavaFX Application class. It initializes the application
 * based on the operating system, setting up the required environment
 * and launching the appropriate controllers.
 */
public class Zenit extends Application {
	
	public static final String OS = System.getProperty("os.name");
	
	@Override
	public void start(Stage stage) throws Exception {
		
		File workspace = new File("res/workspace/workspace.dat");
		File JDK = new File("res/JDK/JDK.dat");
		File defaultJDK = new File ("res/JDK/DefaultJDK.dat");
		
		SetupController sc;
		if (!workspace.exists() || !JDK.exists() || !defaultJDK.exists()) {
			sc = new SetupController();
			sc.start();
		}

		if (OS.equals("Mac OS X")) {
			new MacOSLauncher(stage);
		} else if (OS.equals("Linux")){
			new MainController(stage);
		} else if (OS.startsWith("Windows")) {
			new MainController(stage);
		}
	}
	
	@Override
	public void stop() {
		Platform.exit();
		System.exit(0);
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}

}
