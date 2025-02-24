package main.java.zenit.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.java.zenit.filesystem.jreversions.JDKDirectories;

public class JREVersionsController extends AnchorPane {
	private Stage stage;
	private List<File> JVMs;
	private boolean darkMode;
	@FXML private ListView<String> JDKList;
	
	public JREVersionsController(boolean darkMode) {
		this.darkMode = darkMode;
	}
	
	public void start() {
		try {
			//setup scene
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/zenit/ui/JREVersions.fxml"));
			loader.setController(this);
			AnchorPane root = (AnchorPane) loader.load();
			Scene scene = new Scene(root);

			//set up stage
			stage = new Stage();
			stage.setResizable(false);
			stage.setScene(scene);
			
			initialize();
			
		} catch (IOException e) {
			System.out.println("Error JREVersionsController start() = " + e);
		}
	}
	
	private void initialize() {	
		ifDarkModeChanged(darkMode);
		updateList();
		stage.show();
	}

	private void updateList() {
		JVMs = JDKDirectories.readJDKInstallationDirectoriesFromFile();
		List<String> JVMsString = getJVMNames(JVMs);
		
		JDKList.getItems().clear();
		JDKList.getItems().addAll(JVMsString);

		updateDefaultJDK();
		sortJDKList();
	}

	private List<String> getJVMNames(List<File> JVMs) {
		List<String> JVMsString = new ArrayList<>();
		for (File JVM : JVMs) {
			JVMsString.add(JVM.getName());
		}
		return JVMsString;
	}

	private void updateDefaultJDK() {
		File defaultJDK = JDKDirectories.getDefaultJDKFile();
		if (defaultJDK != null) {
			String defaultName = defaultJDK.getName() + " [default]";
			JDKList.getItems().remove(defaultJDK.getName());
			JDKList.getItems().add(defaultName);
		}
	}

	private void sortJDKList() {
		JDKList.getItems().sort(String::compareTo);
	}

	@FXML
	private void addJRE() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(JDKDirectories.getJVMDirectory());
		directoryChooser.setTitle("Select JDK to add");
		
		File selected = directoryChooser.showDialog(stage);
		
		if (selected != null) {
			boolean success = JDKDirectories.appendToTrackedDirectoriesList(selected);
			if (success) {
				updateList();
			} else {
				DialogBoxes.errorDialog("JDK doesn't contain java or javac", "", "The selected JDK doesn't"
						+ "contain the needed java or javac executables");
			}
		}
	}

	@FXML
	private void removeJRE() {
		String selected = JDKList.getSelectionModel().getSelectedItem();

		if (selected == null) {
			DialogBoxes.errorDialog("No JDK selected", "", "Select a JDK to remove from Zenit");
			return;
		}

		if (selected.endsWith(" [default]")) {
			DialogBoxes.errorDialog("Can't remove default JDK", "", "Can't remove the default JDK, choose another default JDK to remove this one");
			return;
		}

		File selectedFile = findSelectedJDK(selected);

		if (selectedFile == null) {
			DialogBoxes.errorDialog("Couldn't find JDK", "", "The selected JDK couldn't be found");
			return;
		}

		boolean success = JDKDirectories.removeFromTrackedDirectoriesList(selectedFile);
		if (success) {
			DialogBoxes.informationDialog("JDK removed from Zenit", "The JDK " + selected + " has been removed from Zenit");
			updateList();
		} else {
			DialogBoxes.errorDialog("Couldn't remove JDK", "", "The JDK " + selected + " couldn't be removed from Zenit");
		}
	}

	private File findSelectedJDK(String selected) {
		return JVMs.stream()
				.filter(jvm -> jvm.getPath().endsWith(selected))
				.findFirst()
				.orElse(null);
	}

	@FXML
	private void selectDefaultJRE() {
		String selected = JDKList.getSelectionModel().getSelectedItem();
		if (selected != null && selected.endsWith(" [default]")) {
			return;
		}

		File selectedFile = findSelectedJDK(selected);
		if (selected != null) {
			JDKDirectories.setDefaultJDKFile(selectedFile);
			updateList();
		}
	}
	
	public void ifDarkModeChanged(boolean isDarkMode) {
		var stylesheets = stage.getScene().getStylesheets();
		var darkMode = getClass().getResource("/zenit/ui/projectinfo/mainStyle.css").toExternalForm();
		var lightMode = getClass().getResource("/zenit/ui/projectinfo/mainStyle-lm.css").toExternalForm();
		
		if (isDarkMode) {
			if (stylesheets.contains(lightMode)) {
				stylesheets.remove(lightMode);
			}
			
			stylesheets.add(darkMode);
		} else {
			if (stylesheets.contains(darkMode)) {
				stylesheets.remove(darkMode);
			}
			stylesheets.add(lightMode);
		}	
	}
}